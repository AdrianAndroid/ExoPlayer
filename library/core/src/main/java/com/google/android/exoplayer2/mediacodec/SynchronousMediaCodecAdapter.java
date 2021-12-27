/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.exoplayer2.mediacodec;

import static com.google.android.exoplayer2.util.Assertions.checkNotNull;
import static com.google.android.exoplayer2.util.Util.castNonNull;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.Surface;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.decoder.CryptoInfo;
import com.google.android.exoplayer2.util.TraceUtil;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A {@link MediaCodecAdapter} that operates the underlying {@link MediaCodec} in synchronous mode.
 */
public class SynchronousMediaCodecAdapter implements MediaCodecAdapter {

    /**
     * A factory for {@link SynchronousMediaCodecAdapter} instances.
     */
    public static class Factory implements MediaCodecAdapter.Factory {

        @Override
        public MediaCodecAdapter createAdapter(Configuration configuration) throws IOException {
            @Nullable MediaCodec codec = null;
            try {
                codec = createCodec(configuration);
                TraceUtil.beginSection("configureCodec");
                codec.configure(configuration.mediaFormat, configuration.surface, configuration.crypto, configuration.flags);
                TraceUtil.endSection();
                TraceUtil.beginSection("startCodec");
                codec.start();
                TraceUtil.endSection();
                return new SynchronousMediaCodecAdapter(codec);
            } catch (IOException | RuntimeException e) {
                if (codec != null) {
                    codec.release();
                }
                throw e;
            }
        }

        /**
         * Creates a new {@link MediaCodec} instance.
         */
        protected MediaCodec createCodec(Configuration configuration) throws IOException {
            checkNotNull(configuration.codecInfo);
            String codecName = configuration.codecInfo.name;
            TraceUtil.beginSection("createCodec:" + codecName);
            MediaCodec mediaCodec = MediaCodec.createByCodecName(codecName);
            TraceUtil.endSection();
            return mediaCodec;
        }
    }

    private final MediaCodec codec;
    @Nullable
    private ByteBuffer[] inputByteBuffers;
    @Nullable
    private ByteBuffer[] outputByteBuffers;

    private SynchronousMediaCodecAdapter(MediaCodec mediaCodec) {
        this.codec = mediaCodec;
        if (Util.SDK_INT < 21) {
            inputByteBuffers = codec.getInputBuffers();
            outputByteBuffers = codec.getOutputBuffers();
        }
    }

    @Override
    public boolean needsReconfiguration() {
        return false;
    }

    @Override
    public int dequeueInputBufferIndex() {
        return codec.dequeueInputBuffer(0);
    }

    @Override
    public int dequeueOutputBufferIndex(MediaCodec.BufferInfo bufferInfo) {
        int index;
        do {
            index = codec.dequeueOutputBuffer(bufferInfo, 0);
            if (index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED && Util.SDK_INT < 21) {
                outputByteBuffers = codec.getOutputBuffers();
            }
        } while (index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED);

        return index;
    }

    @Override
    public MediaFormat getOutputFormat() {
        return codec.getOutputFormat();
    }

    @Override
    @Nullable
    public ByteBuffer getInputBuffer(int index) {
        if (Util.SDK_INT >= 21) {
            return codec.getInputBuffer(index);
        } else {
            return castNonNull(inputByteBuffers)[index];
        }
    }

    @Override
    @Nullable
    public ByteBuffer getOutputBuffer(int index) {
        if (Util.SDK_INT >= 21) {
            return codec.getOutputBuffer(index);
        } else {
            return castNonNull(outputByteBuffers)[index];
        }
    }

    @Override
    public void queueInputBuffer(int index, int offset, int size, long presentationTimeUs, int flags) {
        codec.queueInputBuffer(index, offset, size, presentationTimeUs, flags);
    }

    @Override
    public void queueSecureInputBuffer(int index, int offset, CryptoInfo info, long presentationTimeUs, int flags) {
        codec.queueSecureInputBuffer(index, offset, info.getFrameworkCryptoInfo(), presentationTimeUs, flags);
    }

    @Override
    public void releaseOutputBuffer(int index, boolean render) {
        codec.releaseOutputBuffer(index, render);
        // 如果您使用完缓冲区，请使用此调用将缓冲区返回给编解码器或在输出Surface上呈现它。
        // 如果您使用输出Surface配置了编解码器，则将 render 设置为 true 将首先将缓冲区发送到该输出Surface。
        // 一旦不再使用/显示，Surface会将缓冲区释放回编解码器。
        // 一旦输出缓冲区被释放到编解码器，它必须不被使用，直到它稍后被 getOutputBuffer 检索以响应 dequeueOutputBuffer 返回值或
        // MediaCodec.Callback.onOutputBufferAvailable 回调。
    }

    @Override
    @RequiresApi(21)
    public void releaseOutputBuffer(int index, long renderTimeStampNs) {
        codec.releaseOutputBuffer(index, renderTimeStampNs);
        // If you are done with a buffer, use this call to update its surface timestamp and return it to the codec to render it on the output surface. If you have not specified an output surface when configuring this video codec, this call will simply return the buffer to the codec.
        // The timestamp may have special meaning depending on the destination surface.
        // SurfaceView specifics
        // If you render your buffer on a android.view.SurfaceView, you can use the timestamp to render the buffer at a specific time (at the VSYNC at or after the buffer timestamp). For this to work, the timestamp needs to be reasonably close to the current System.nanoTime. Currently, this is set as within one (1) second. A few notes:
        // the buffer will not be returned to the codec until the timestamp has passed and the buffer is no longer used by the Surface.
        // buffers are processed sequentially, so you may block subsequent buffers to be displayed on the Surface. This is important if you want to react to user action, e.g. stop the video or seek.
        // if multiple buffers are sent to the Surface to be rendered at the same VSYNC, the last one will be shown, and the other ones will be dropped.
        // if the timestamp is not "reasonably close" to the current system time, the Surface will ignore the timestamp, and display the buffer at the earliest feasible time. In this mode it will not drop frames.
        // for best performance and quality, call this method when you are about two VSYNCs' time before the desired render time. For 60Hz displays, this is about 33 msec.
        // Once an output buffer is released to the codec, it MUST NOT be used until it is later retrieved by getOutputBuffer in response to a dequeueOutputBuffer return value or a MediaCodec.Callback.onOutputBufferAvailable callback
    }

    @Override
    public void flush() {
        codec.flush();
    }

    @Override
    public void release() {
        inputByteBuffers = null;
        outputByteBuffers = null;
        codec.release();
    }

    @Override
    @RequiresApi(23)
    public void setOnFrameRenderedListener(OnFrameRenderedListener listener, Handler handler) {
        codec.setOnFrameRenderedListener(
                (codec, presentationTimeUs, nanoTime) -> listener.onFrameRendered(SynchronousMediaCodecAdapter.this, presentationTimeUs, nanoTime),
                handler);
    }

    @Override
    @RequiresApi(23)
    public void setOutputSurface(Surface surface) {
        codec.setOutputSurface(surface);
    }

    @Override
    @RequiresApi(19)
    public void setParameters(Bundle params) {
        codec.setParameters(params);
    }

    @Override
    public void setVideoScalingMode(@C.VideoScalingMode int scalingMode) {
        codec.setVideoScalingMode(scalingMode);
    }
}

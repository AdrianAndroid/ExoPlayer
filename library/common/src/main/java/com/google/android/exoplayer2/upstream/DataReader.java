/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.google.android.exoplayer2.upstream;

import com.google.android.exoplayer2.C;
import java.io.IOException;

/**
 * Reads bytes from a data stream. 从数据流中读取字节。
 */
public interface DataReader {
    /**
     * Reads up to {@code length} bytes of data from the input. 从输入中读取最多长度字节的数据。
     *
     * <p>If {@code readLength} is zero then 0 is returned. Otherwise, if no data is available because
     * the end of the opened range has been reached, then {@link C#RESULT_END_OF_INPUT} is returned.
     * Otherwise, the call will block until at least one byte of data has been read and the number of
     * bytes read is returned. 如果readLength为零，则返回0。否则，如果由于已达到打开范围的末尾而没有可用数据，则
     * 返回RESULT_END_OF_INPUT。否则，调用将阻塞，直到至少读取了一个字节的数据并返回读取的字节数。
     *
     * @param buffer A target array into which data should be written. 应写入数据的目标数组。
     * @param offset The offset into the target array at which to write. 写入目标数组的偏移量。
     * @param length The maximum number of bytes to read from the input.  从输入读取的最大字节数。
     * @return The number of bytes read, or {@link C#RESULT_END_OF_INPUT} if the input has ended. This
     * may be less than {@code length} because the end of the input (or available data) was
     * reached, the method was interrupted, or the operation was aborted early for another reason.
     * 读取的字节数，如果输入已经结束，则为RESULT_END_OF_INPUT。这可能小于Length，因为已经达到输入（或可用数据）的末尾，
     * 方法被终端或操作原因提前中止。
     * @throws IOException If an error occurs reading from the input.
     */
    int read(byte[] buffer, int offset, int length) throws IOException;
}

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
package com.google.android.exoplayer2.trackselection;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.RendererConfiguration;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;

/**
 * The component of an {@link ExoPlayer} responsible for selecting tracks to be consumed by each of
 * the player's {@link Renderer}s. The {@link DefaultTrackSelector} implementation should be
 * suitable for most use cases.
 * {@link ExoPlayer}的组件，负责选择每个播放器的{@link Renderer}要使用的曲目。{@link DefaultTrackSelector}实现应该适合大多数用例.
 *
 * <h2>Interactions with the player</h2>
 * 跟player的互动
 * <p>
 * The following interactions occur between the player and its track selector during playback.
 * 播放期间播放器与其曲目选择器之间发生一下交互
 * <ul>
 *   <li>When the player is created it will initialize the track selector by calling
 *   {@link #init(InvalidationListener, BandwidthMeter)}.
 *    创建播放器时，它将通过调用{@link #init(InvalidationListener, BandwidthMeter}
 *
 *   <li>When the player needs to make a track selection it will call {@link
 *       #selectTracks(RendererCapabilities[], TrackGroupArray, MediaPeriodId, Timeline)}. This
 *       typically occurs at the start of playback, when the player starts to buffer a new period of
 *       the media being played, and when the track selector invalidates its previous selections.
 *       当播放器需要进行曲目选择时，它将调用#selectTracks(RendererCapabilities[], TrackGroupArray, MediaPeriodId, Timeline)},
 *       这通常发生在播放开始时， 播放器开始缓冲正在播放的媒体的新时间段时，以及当曲目选择器使其先前的选择无效时。
 *
 *   <li>The player may perform a track selection well in advance of the selected tracks becoming
 *       active, where active is defined to mean that the renderers are actually consuming media
 *       corresponding to the selection that was made.
 *       播放器可以在所选曲目变为活动之前很好地执行曲目选择，其中活动被定义为意味着渲染器实际上正在消费与所选择相对应的媒体。
 *       For example when playing media containing multiple periods, the track selection for a period is made when
 *       the player starts to buffer that period.
 *       例如，当播放包含多个时间段的媒体时，当播放器开始缓冲该时间段时，会选择该时间段的曲目。
 *       Hence if the player's buffering policy is to maintain a 30 second buffer, the selection will occur
 *       approximately 30 seconds in advance of it becoming active.
 *       因此，如果播放器的缓冲策是保持30秒的缓冲，则选择将在其变为活动状态前大约30秒发生。
 *       In fact the selection may never become active, for example if the user seeks to some other period of
 *       the media during the 30 second gap.
 *       事实上，该选择可能永远不会变为活动状态，例如，如果用户在30秒间隔期间需求媒体的某个其他时间段。
 *       The player indicates to the track selector when a selection it has previously made becomes active by calling
 *       {@link #onSelectionActivated(Object)}.
 *       播放器通过调用{@link #onSelectionActivated(Object)} 向曲目选择器指示其先前所做的选择合适变为活动状态
 *
 *   <li>If the track selector wishes to indicate to the player that selections it has previously
 *       made are invalid, it can do so by calling {@link InvalidationListener#onTrackSelectionsInvalidated()}
 *       on the {@link InvalidationListener} that was passed to {@link #init(InvalidationListener, BandwidthMeter)}.
 *       如果轨道选择器希望向播放器表明它之前所做的选择是无效的， 它可以通过在传递给{@link InvalidationListener}上调用
 *       {@link InvalidationListener#onTrackSelectionsInvalidated()}来实现{@link #init(InvalidationListener, BandwidthMeter)}
 *       A track selector may wish to do this if its configuration has changed, for example if it now wishes to
 *       prefer audio tracks in a particular language.
 *       如果轨道选择器的配置发生了变化，则它可能希望这样做，例如，如果它现在希望更喜欢特定语言的音轨。
 *       This will trigger the player to make new track selections.
 *       这将触发玩家进行心的曲目选择
 *       Note that the player will have to re-buffer in the case that the new track selection for the currently
 *       playing period differs from the one that was invalidated.
 *       请注意，如果当前播放时段的新曲目选择与无效的曲目选择不同，则播放器将不得不重新缓冲。
 *       Implementing subclasses can trigger invalidation by calling {@link #invalidate()}, which
 *       will call {@link InvalidationListener#onTrackSelectionsInvalidated()}.
 * </ul>
 *
 * <h2>Renderer configuration</h2>
 * 渲染配置
 * The {@link TrackSelectorResult} returned by {@link #selectTracks(RendererCapabilities[],
 * TrackGroupArray, MediaPeriodId, Timeline)} contains not only {@link TrackSelection}s for each
 * renderer, but also {@link RendererConfiguration}s defining configuration parameters that the
 * renderers should apply when consuming the corresponding media.
 * {@link #selectTracks(RendererCapabilities[], TrackGroupArray, MediaPeriodId, Timeline)}
 * 不仅包含每个的 {@link TrackSelection}渲染器，还有 {@link RendererConfiguration} 定义配置参数，使用相应的媒体时应应用渲染器。
 * Whilst it may seem counter-intuitive for a track selector to also specify renderer configuration information, in
 * practice the two are tightly bound together.
 * 虽然轨道选择器同时指定渲染器配置信息似乎有悖常理，但实际上两者是紧密结合在一起的。
 * It may only be possible to play a certain combination tracks if the renderers are configured in a particular way.
 * 如果渲染器以特定方式配置，则可能只有在渲染器配置为轨道的情况下才能播放特定组合轨道。
 * Equally, it may only be possible to configure renderers in a particular way if certain tracks are selected.
 * 同样，如果选择了某些轨道，则可能只能以特定方式配置渲染器。
 * Hence it makes sense to determine the track selection and corresponding renderer configurations in a single step.
 * 因此，在一个步骤中确定轨道选择和相应的渲染器配置是有意义的。
 *
 *
 * <h2>Threading model</h2>
 * <p>
 * All calls made by the player into the track selector are on the player's internal playback
 * thread. The track selector may call {@link InvalidationListener#onTrackSelectionsInvalidated()}
 * from any thread.
 * 播放器对曲目选择器的所有调用都在播放器的内部播放线程上。轨道选择器可以从任何线程调用
 * {@link InvalidationListener#onTrackSelectionsInvalidated()}
 */
public abstract class TrackSelector {

    /**
     * Notified when selections previously made by a {@link TrackSelector} are no longer valid.
     */
    public interface InvalidationListener {

        /**
         * Called by a {@link TrackSelector} to indicate that selections it has previously made are no
         * longer valid. May be called from any thread.
         */
        void onTrackSelectionsInvalidated();
    }

    @Nullable
    private InvalidationListener listener;
    @Nullable
    private BandwidthMeter bandwidthMeter;

    /**
     * Called by the player to initialize the selector.
     *
     * @param listener       An invalidation listener that the selector can call to indicate that selections
     *                       it has previously made are no longer valid.
     * @param bandwidthMeter A bandwidth meter which can be used by track selections to select tracks.
     */
    public final void init(InvalidationListener listener, BandwidthMeter bandwidthMeter) {
        this.listener = listener;
        this.bandwidthMeter = bandwidthMeter;
    }

    /**
     * Called by the player to perform a track selection.
     *
     * @param rendererCapabilities The {@link RendererCapabilities} of the renderers for which tracks
     *                             are to be selected.
     * @param trackGroups          The available track groups.
     * @param periodId             The {@link MediaPeriodId} of the period for which tracks are to be selected.
     * @param timeline             The {@link Timeline} holding the period for which tracks are to be selected.
     * @return A {@link TrackSelectorResult} describing the track selections.
     * @throws ExoPlaybackException If an error occurs selecting tracks.
     */
    public abstract TrackSelectorResult selectTracks(
        RendererCapabilities[] rendererCapabilities,
        TrackGroupArray trackGroups,
        MediaPeriodId periodId,
        Timeline timeline)
        throws ExoPlaybackException;

    /**
     * Called by the player when a {@link TrackSelectorResult} previously generated by {@link
     * #selectTracks(RendererCapabilities[], TrackGroupArray, MediaPeriodId, Timeline)} is activated.
     *
     * @param info The value of {@link TrackSelectorResult#info} in the activated selection.
     */
    public abstract void onSelectionActivated(@Nullable Object info);

    /**
     * Calls {@link InvalidationListener#onTrackSelectionsInvalidated()} to invalidate all previously
     * generated track selections.
     */
    protected final void invalidate() {
        if (listener != null) {
            listener.onTrackSelectionsInvalidated();
        }
    }

    /**
     * Returns a bandwidth meter which can be used by track selections to select tracks. Must only be
     * called after {@link #init(InvalidationListener, BandwidthMeter)} has been called.
     */
    protected final BandwidthMeter getBandwidthMeter() {
        return Assertions.checkNotNull(bandwidthMeter);
    }
}

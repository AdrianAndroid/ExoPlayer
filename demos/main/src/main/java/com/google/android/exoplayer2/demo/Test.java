package com.google.android.exoplayer2.demo;

import android.content.Context;
import android.net.Uri;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.List;

public class Test {


  public static void playMultipleAudiosWithVolume(Context context, List<String> audioList,
      List<Float> volumeList) {
    SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();

    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
        Util.getUserAgent(context, "YourApplicationName"));
    List<MediaSource> mediaSourceList = new ArrayList<>();

    for (int i = 0; i < audioList.size(); i++) {
      Uri audioUri = Uri.parse(audioList.get(i));
      MediaItem mediaItem = MediaItem.fromUri(audioUri);
      MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
      mediaSourceList.add(mediaSource);
    }

    MediaSource[] mediaSources = new MediaSource[mediaSourceList.size()];
    mediaSourceList.toArray(mediaSources);

    ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource(mediaSources);
    player.setMediaSource(concatenatedSource);

    for (int i = 0; i < audioList.size(); i++) {
      player.setVolume(volumeList.get(i));
    }

    player.prepare();
    player.setPlayWhenReady(true);
  }

  public static void main(String[] args) {
    List<String> audioList = new ArrayList<>();
    audioList.add("https://example.com/audio1.mp3");
    audioList.add("https://example.com/audio2.mp3");
    audioList.add("https://example.com/audio3.mp3");

    List<Float> volumeList = new ArrayList<>();
    volumeList.add(1.0f);
    volumeList.add(0.8f);
    volumeList.add(0.5f);

    Context context = null;
    playMultipleAudiosWithVolume(context, audioList, volumeList);
  }

}

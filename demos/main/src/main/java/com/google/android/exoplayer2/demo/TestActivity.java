package com.google.android.exoplayer2.demo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

  private String url1 = "https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/c731c3b2012a44d3ba94a6cbe522e980.mp3";
  private String url2 = "https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/1483b8dd6f67a99dcb842b021deee388.mp3";
  private String url3 = "https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/47f6623d833a8f16ad7f1aa9bd3cd6c3.mp3";
  private String url4 = "https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/13991852da4ddd20a7bf48cecbf3d543.mp3";
  private String url5 = "https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/71850e07c3a7a118187d9b6b5ecbf16c.mp3";
  private String url6 = "https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/9d5ebc0b2cbce013924a4a3e557db309.mp3";
  private String url7 = "https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/0ed1c2340212b28950d525fc03003d4f.mp3";

  private AppCompatSeekBar seek1;
  private AppCompatSeekBar seek2;
  private AppCompatSeekBar seek3;
  private AppCompatSeekBar seek4;
  private AppCompatSeekBar seek5;
  private AppCompatSeekBar seek6;
  private AppCompatSeekBar seek7;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);

    seek1 = findViewById(R.id.seek1);
    seek2 = findViewById(R.id.seek2);
    seek3 = findViewById(R.id.seek3);
    seek4 = findViewById(R.id.seek4);
    seek5 = findViewById(R.id.seek5);
    seek6 = findViewById(R.id.seek6);
    seek7 = findViewById(R.id.seek7);

    findViewById(R.id.btnPlay).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        test();
      }
    });
  }

  public void playMultipleAudiosWithVolume(Context context, List<String> audioList, List<Float> volumeList) {
    SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();

    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "YourApplicationName"));
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
    player.addAnalyticsListener(new AnalyticsListener() {
      @Override
      public void onPlayWhenReadyChanged(EventTime eventTime, boolean playWhenReady, int reason) {
        Log.i(TestActivity.class.getSimpleName(), "onPlayWhenReadyChanged playWhenReady=" + playWhenReady + " , reason=" + reason);
      }

      @Override
      public void onPositionDiscontinuity(EventTime eventTime, Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
        Log.i(TestActivity.class.getSimpleName(), "onPositionDiscontinuity, reason=" + reason);
      }

      @Override
      public void onPlaybackStateChanged(EventTime eventTime, int state) {
        Log.i(TestActivity.class.getSimpleName(), "onPlaybackStateChanged state=" + state);
      }
    });

    for (int i = 0; i < audioList.size(); i++) {
      player.setVolume(volumeList.get(i));
    }

    player.prepare();
    player.setPlayWhenReady(true);
  }

  public void test() {
    List<String> audioList = new ArrayList<>();
    audioList.add(url1);
    audioList.add(url2);
    audioList.add(url3);
    audioList.add(url4);
    audioList.add(url5);
    audioList.add(url6);
    audioList.add(url7);

    List<Float> volumeList = new ArrayList<>();
    volumeList.add(1.0f);
    volumeList.add(1.0f);
    volumeList.add(1.0f);
    volumeList.add(1.0f);
    volumeList.add(1.0f);
    volumeList.add(1.0f);
    volumeList.add(1.0f);

    playMultipleAudiosWithVolume(this, audioList, volumeList);
  }
}
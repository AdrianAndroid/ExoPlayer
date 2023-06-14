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
  private String url2 = "https://gc-static.chataimaster.com/iso-aigpt/common/mp3/7/f48406c70daedd36690ab2e7eb8d9dad.mp3";
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
    List<Mp3Bean> list = new ArrayList<>();
    list.add(new Mp3Bean(url2, 1.0f));
    list.add(new Mp3Bean(url1, 1.0f));
//    list.add(new Mp3Bean(url2, 1.0f));
//    list.add(new Mp3Bean(url3, 1.0f));
//    list.add(new Mp3Bean(url4, 1.0f));
//    list.add(new Mp3Bean(url5, 1.0f));
//    list.add(new Mp3Bean(url6, 1.0f));
//    list.add(new Mp3Bean(url7, 1.0f));

    List<String> audioList = new ArrayList<>();
    for (Mp3Bean mp3Bean : list) {
      audioList.add(mp3Bean.url);
    }

    List<Float> volumeList = new ArrayList<>();
    for (Mp3Bean mp3Bean : list) {
      volumeList.add(mp3Bean.volume);
    }

    playMultipleAudiosWithVolume(this, audioList, volumeList);
  }

  private static final class Mp3Bean {

    private final String url;
    private final float volume;

    private Mp3Bean(String url, float volume) {
      this.url = url;
      this.volume = volume;
    }
  }
}

//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/c731c3b2012a44d3ba94a6cbe522e980.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/1483b8dd6f67a99dcb842b021deee388.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/47f6623d833a8f16ad7f1aa9bd3cd6c3.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/13991852da4ddd20a7bf48cecbf3d543.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/71850e07c3a7a118187d9b6b5ecbf16c.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/9d5ebc0b2cbce013924a4a3e557db309.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/0ed1c2340212b28950d525fc03003d4f.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/1be7bb01b967891c2254ff0aee83b33c.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/7ae25b9d1e36353659b205cced7707c3.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/4feab7294f1ecb2d84cf512e3c3d4941.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/671f0075c0606a0566764273dc936792.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/e021e9552bbc735347df783f4d58257b.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/9aca549f95e0b6b2e9d47ce6fb3f51f9.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/0317757e06b29f5e6cab741b1120485c.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/59cc36fb5e0d07a73077cf654d3ed980.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/7bc2e3c501819dde39e5490489fd8095.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/99e19a667619712875dda90a7a122ff7.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/aa8834f39953a75fc1093e4c6887be45.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/a5a60dafc516de4266bb71d610900a1d.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/a5e1f9c656b08e72c833758ccd032057.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/6aa37076bca05bc715b11abe538c1867.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/abb2251ba7eb476b6830f57c0f33103a.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/29852eff36d4aacd0ea8535cccddb388.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/ec21a83b03c1a5d4db7c4031641c29b8.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/a976fda8fb267af5557b323a7abbdfa7.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/f82ffcaca8088edd6628c0d223ea893d.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/44cb0ddec0fc2733ee904f928bdabdf0.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/4b9ef9934917d9590a4f3d50601403ac.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/81d93eb7d70242cfbe825c715b28a4de.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/5f4ad0bbca1d12f60ae849bada25c674.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/bb54a5004dc3ee826630ec16628c3891.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/c3dd1865496f2cb3bea273797c46dbdb.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/abf59cae78f6bbc7ffd1927bdcb9edd7.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/efbb27e7a1386fee07a923cff096c580.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/748d67136c9f3b4426c46cc4b2237f86.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/0bc754f8b05cf5f2e1f8d13357293532.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/6bb4761cfd5c9275c955218d38873357.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/ec536f3e59109613c639312f68c2645d.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/e44358e9935dd0928ef09faa6d015c8e.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/7/f48406c70daedd36690ab2e7eb8d9dad.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/c731c3b2012a44d3ba94a6cbe522e980.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/1483b8dd6f67a99dcb842b021deee388.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/47f6623d833a8f16ad7f1aa9bd3cd6c3.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/13991852da4ddd20a7bf48cecbf3d543.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/71850e07c3a7a118187d9b6b5ecbf16c.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/9d5ebc0b2cbce013924a4a3e557db309.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/0ed1c2340212b28950d525fc03003d4f.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/1be7bb01b967891c2254ff0aee83b33c.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/7ae25b9d1e36353659b205cced7707c3.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/4feab7294f1ecb2d84cf512e3c3d4941.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/671f0075c0606a0566764273dc936792.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/e021e9552bbc735347df783f4d58257b.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/9aca549f95e0b6b2e9d47ce6fb3f51f9.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/0317757e06b29f5e6cab741b1120485c.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/59cc36fb5e0d07a73077cf654d3ed980.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/7bc2e3c501819dde39e5490489fd8095.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/99e19a667619712875dda90a7a122ff7.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/aa8834f39953a75fc1093e4c6887be45.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/a5a60dafc516de4266bb71d610900a1d.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/a5e1f9c656b08e72c833758ccd032057.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/6aa37076bca05bc715b11abe538c1867.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/abb2251ba7eb476b6830f57c0f33103a.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/29852eff36d4aacd0ea8535cccddb388.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/ec21a83b03c1a5d4db7c4031641c29b8.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/a976fda8fb267af5557b323a7abbdfa7.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/f82ffcaca8088edd6628c0d223ea893d.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/44cb0ddec0fc2733ee904f928bdabdf0.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/4b9ef9934917d9590a4f3d50601403ac.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/81d93eb7d70242cfbe825c715b28a4de.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/5f4ad0bbca1d12f60ae849bada25c674.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/bb54a5004dc3ee826630ec16628c3891.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/c3dd1865496f2cb3bea273797c46dbdb.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/abf59cae78f6bbc7ffd1927bdcb9edd7.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/efbb27e7a1386fee07a923cff096c580.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/748d67136c9f3b4426c46cc4b2237f86.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/0bc754f8b05cf5f2e1f8d13357293532.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/6bb4761cfd5c9275c955218d38873357.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/ec536f3e59109613c639312f68c2645d.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/6/e44358e9935dd0928ef09faa6d015c8e.mp3
//noise uri=https://gc-static.chataimaster.com/iso-aigpt/common/mp3/7/f48406c70daedd36690ab2e7eb8d9dad.mp3
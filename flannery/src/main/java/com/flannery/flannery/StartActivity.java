package com.flannery.flannery;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    void playMultipleAudioWithVolume(List<String> audioList, List<Float> volumeList) {
    }
}
package com.debarunlahiri.burnab.messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.VideoView;

import com.debarunlahiri.burnab.messenger.R;

public class StartActivity extends AppCompatActivity {

    private VideoView videoView;
    private Button startloginbutton, startregisterbutton;

    private MediaPlayer mMediaPlayer;
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
        setContentView(R.layout.activity_start);

        videoView = findViewById(R.id.videoView3);
        startloginbutton = findViewById(R.id.startloginbutton);
        startregisterbutton = findViewById(R.id.startregisterbutton);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.start_background);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer = mp;
                mp.setLooping(true);
                if (currentPosition != 0) {
                    mp.seekTo(0);
                    mp.start();
                }
            }
        });

        startloginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(loginIntent);

            }
        });

        startregisterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        videoView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

}

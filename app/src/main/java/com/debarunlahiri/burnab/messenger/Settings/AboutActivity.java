package com.debarunlahiri.burnab.messenger.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.debarunlahiri.burnab.messenger.BuildConfig;
import com.debarunlahiri.burnab.messenger.R;

public class AboutActivity extends AppCompatActivity {

    private Toolbar abouttoolbar;
    private TextView textView7, tvBurnabAboutVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        abouttoolbar = findViewById(R.id.abouttoolbar);
        abouttoolbar.setTitle("About");
        //abouttoolbar.setTitleTextColor(Color.WHITE);
        //abouttoolbar.setBackgroundColor(Color.BLACK);

        setSupportActionBar(abouttoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        abouttoolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.black_back));
        abouttoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.super.onBackPressed();
            }
        });

        textView7 = findViewById(R.id.tvLoginForgortPassword);
        tvBurnabAboutVersion = findViewById(R.id.tvBurnabAboutVersion);
        //textView7.setShadowLayer(11, 3, 6, Color.GRAY);

        tvBurnabAboutVersion.setText("v" + BuildConfig.VERSION_NAME);
    }

    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }

}

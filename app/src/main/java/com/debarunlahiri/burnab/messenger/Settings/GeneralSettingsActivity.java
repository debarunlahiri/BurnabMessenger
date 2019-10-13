package com.debarunlahiri.burnab.messenger.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.debarunlahiri.burnab.messenger.R;

public class GeneralSettingsActivity extends AppCompatActivity {

    private Toolbar generalsettingstoolbar;

    private CardView generalSettingsChangeProfilePIcCV;
    private CardView generalSettingsChangeUsernameCV;
    private CardView generalSettingsChangeUserDetailsCV;
    private CardView generalSettingsChangeEmailIdCV;
    private CardView generalSettingsChangeProfileBackgroundCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_settings);

        generalsettingstoolbar = findViewById(R.id.generalsettingstoolbar);
        generalsettingstoolbar.setTitle("General Settings");
        setSupportActionBar(generalsettingstoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        generalsettingstoolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.black_back));
        generalsettingstoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        generalSettingsChangeProfilePIcCV = findViewById(R.id.generalSettingsChangeProfilePIcCV);
        generalSettingsChangeUsernameCV = findViewById(R.id.generalSettingsChangeUsernameCV);
        generalSettingsChangeUserDetailsCV = findViewById(R.id.generalSettingsChangeUserDetailsCV);
        generalSettingsChangeEmailIdCV = findViewById(R.id.generalSettingsChangeEmailIdCV);
        generalSettingsChangeProfileBackgroundCV = findViewById(R.id.generalSettingsChangeProfileBackgroundCV);

        generalSettingsChangeProfilePIcCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profilePicIntent = new Intent(GeneralSettingsActivity.this, SettingsChangeProfilePictureActivity.class);
                startActivity(profilePicIntent);
            }
        });

        generalSettingsChangeUsernameCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changeUsernameIntent = new Intent(GeneralSettingsActivity.this, SettingsChangeUsernameActivity.class);
                startActivity(changeUsernameIntent);
            }
        });

        generalSettingsChangeUserDetailsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editUserDetailsIntent = new Intent(GeneralSettingsActivity.this, SettingsEditUserDetailsActivity.class);
                startActivity(editUserDetailsIntent);
            }
        });

        generalSettingsChangeEmailIdCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changeEmailIntent = new Intent(GeneralSettingsActivity.this, SettingsChnageEmailAddressActivity.class);
                startActivity(changeEmailIntent);
            }
        });

        generalSettingsChangeProfileBackgroundCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changeBackgroundIntent = new Intent(GeneralSettingsActivity.this, ChnageBackgroundActivity.class);
                startActivity(changeBackgroundIntent);
            }
        });
    }
}

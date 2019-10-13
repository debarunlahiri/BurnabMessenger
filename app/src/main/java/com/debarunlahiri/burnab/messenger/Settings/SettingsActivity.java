package com.debarunlahiri.burnab.messenger.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.debarunlahiri.burnab.messenger.ProfileActivity;
import com.debarunlahiri.burnab.messenger.R;
import com.debarunlahiri.burnab.messenger.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar settingstoolbar;

    private CardView generalSettingsCV, privacySettingsCV, aboutSettingsCV, logoutSettingsCV;
    private TextView textView4;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingstoolbar = findViewById(R.id.settingstoolbar);
        settingstoolbar.setTitle("Settings");
        setSupportActionBar(settingstoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        settingstoolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.black_back));
        settingstoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        generalSettingsCV = findViewById(R.id.generalSettingsCV);
        privacySettingsCV = findViewById(R.id.privacySettingsCV);
        logoutSettingsCV = findViewById(R.id.logoutSettingsCV);
        aboutSettingsCV = findViewById(R.id.aboutSettingsCV);
        textView4 = findViewById(R.id.textView4);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        aboutSettingsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aboutIntent = new Intent(SettingsActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
            }
        });

        generalSettingsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent generalIntent = new Intent(SettingsActivity.this, GeneralSettingsActivity.class);
                startActivity(generalIntent);
            }
        });

        privacySettingsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent privacyIntent = new Intent(SettingsActivity.this, PrivacySettingsActivity.class);
                startActivity(privacyIntent);
            }
        });

        logoutSettingsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("users").child(user_id).child("token_id").removeValue();
                mAuth.signOut();
                Intent loginIntent = new Intent(SettingsActivity.this, StartActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            }
        });


    }
}

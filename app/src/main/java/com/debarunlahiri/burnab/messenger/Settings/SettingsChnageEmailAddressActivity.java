package com.debarunlahiri.burnab.messenger.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.debarunlahiri.burnab.messenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

public class SettingsChnageEmailAddressActivity extends AppCompatActivity {

    private Toolbar settingschangeemailtoolbar;

    private EditText etChangeEmail;
    private Button changeemailbutton;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_chnage_email_address);

        settingschangeemailtoolbar = findViewById(R.id.settingschangeemailtoolbar);
        settingschangeemailtoolbar.setTitle("Change email");
        setSupportActionBar(settingschangeemailtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        settingschangeemailtoolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.black_back));
        settingschangeemailtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etChangeEmail = findViewById(R.id.etChangeEmail);
        changeemailbutton = findViewById(R.id.changeemailbutton);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        //Get email id
        String getEmail = currentUser.getEmail();
        etChangeEmail.setText(getEmail);

        changeemailbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etChangeEmail.getText().toString();
                changeemailbutton.setText("Please wait...");
                if (getEmail.equals(email)) {
                    changeemailbutton.setText("Change");
                    Toast.makeText(getApplicationContext(), "Email address is already same", Toast.LENGTH_LONG).show();
                } else {
                    currentUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Email changed successfully", Toast.LENGTH_LONG).show();
                                mDatabase.child("users").child(user_id).child("user_data").child("email_change_time_period").setValue(new Date(System.currentTimeMillis()+30L * 24 * 60 * 60 * 1000));
                            } else {
                                changeemailbutton.setText("Change");
                                Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            changeemailbutton.setText("Change");
                            Toast.makeText(getApplicationContext(), "Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });
    }
}

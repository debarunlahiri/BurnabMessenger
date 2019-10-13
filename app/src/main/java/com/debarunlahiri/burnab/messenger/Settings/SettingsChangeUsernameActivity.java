package com.debarunlahiri.burnab.messenger.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.debarunlahiri.burnab.messenger.MainActivity;
import com.debarunlahiri.burnab.messenger.R;
import com.debarunlahiri.burnab.messenger.SetupUser.SetupUser3Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

public class SettingsChangeUsernameActivity extends AppCompatActivity {

    private Toolbar changeusernametoolbar;

    private EditText etChangeUsername;
    private Button savechangedusername;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;
    private String ds_username, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_change_username);

        changeusernametoolbar = findViewById(R.id.changeusernametoolbar);
        setSupportActionBar(changeusernametoolbar);
        changeusernametoolbar.setTitle("Change username");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        changeusernametoolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.black_back));
        changeusernametoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        etChangeUsername = findViewById(R.id.etChangeUsername);
        savechangedusername = findViewById(R.id.savechangedusername);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        savechangedusername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = etChangeUsername.getText().toString();

                if (username.isEmpty()) {
                    etChangeUsername.setError("Please enter your username");
                } else {
                    mDatabase.child("usernames").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if (ds.child("username").getValue().equals(username)) {
                                        ds_username = ds.child("username").getValue().toString();
                                    }

                                }
                            }

                            if (ds_username != null &&  ds_username.equals(username)) {
                                Toast.makeText(getApplicationContext(), "Username already exists", Toast.LENGTH_LONG).show();
                            } else {
                                //Toast.makeText(getApplicationContext(), "insert username", Toast.LENGTH_LONG).show();
                                //mDatabase.child("usernames").child("username").setValue(username);
                                mDatabase.child("users").child(user_id).child("user_data").child("username").setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Username changed successfully", Toast.LENGTH_LONG).show();
                                            onBackPressed();
                                            mDatabase.child("usernames").push().child("username").setValue(username);
                                            mDatabase.child("users").child(user_id).child("username").setValue(username);
                                            mDatabase.child("users").child(currentUser.getUid()).child("user_data").child("age_change_time_period").setValue(new Date(System.currentTimeMillis()+14L * 24 * 60 * 60 * 1000));
                                        }
                                    }
                                });


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });


    }
}

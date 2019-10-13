package com.debarunlahiri.burnab.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.debarunlahiri.burnab.messenger.SetupUser.SetupFinalActivity;
import com.debarunlahiri.burnab.messenger.SetupUser.SetupUser1Activity;
import com.debarunlahiri.burnab.messenger.SetupUser.SetupUser2Activity;
import com.debarunlahiri.burnab.messenger.SetupUser.SetupUser3Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class VerificationCheckActivity extends AppCompatActivity {

    private ProgressBar verificationcheckPB;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_check);

        verificationcheckPB = findViewById(R.id.verificationcheckPB);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        if (currentUser == null) {
            Toast.makeText(getApplicationContext(), "Please login again", Toast.LENGTH_LONG).show();
            Intent startIntent = new Intent(VerificationCheckActivity.this, StartActivity.class);
            startActivity(startIntent);
            finish();
        } else {
            user_id = currentUser.getUid();
            mDatabase.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        mDatabase.child("users").child(user_id).child("user_data").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.child("profile_image").exists() && !dataSnapshot.child("username").exists() && !dataSnapshot.child("name").exists() &&
                                        !dataSnapshot.child("gender").exists()) {
                                    Intent setupUserIntent = new Intent(VerificationCheckActivity.this, SetupUser1Activity.class);
                                    setupUserIntent.putExtra("age", dataSnapshot.child("age").getValue().toString());
                                    startActivity(setupUserIntent);
                                    finish();
                                } else if (!dataSnapshot.child("profile_image").exists()) {
                                    Intent setupUserIntent = new Intent(VerificationCheckActivity.this, SetupUser2Activity.class);
                                    setupUserIntent.putExtra("age", dataSnapshot.child("age").getValue().toString());
                                    setupUserIntent.putExtra("name", dataSnapshot.child("name").getValue().toString());
                                    setupUserIntent.putExtra("bio", dataSnapshot.child("bio").getValue().toString());
                                    setupUserIntent.putExtra("gender", dataSnapshot.child("gender").getValue().toString());
                                    startActivity(setupUserIntent);
                                    finish();
                                } else if (!dataSnapshot.child("username").exists()) {
                                    Intent setupUserIntent = new Intent(VerificationCheckActivity.this, SetupUser3Activity.class);
                                    setupUserIntent.putExtra("age", dataSnapshot.child("age").getValue().toString());
                                    setupUserIntent.putExtra("name", dataSnapshot.child("name").getValue().toString());
                                    setupUserIntent.putExtra("bio", dataSnapshot.child("bio").getValue().toString());
                                    setupUserIntent.putExtra("gender", dataSnapshot.child("gender").getValue().toString());
                                    setupUserIntent.putExtra("profileImageURI", dataSnapshot.child("profile_image").getValue().toString());
                                    startActivity(setupUserIntent);
                                    finish();
                                } else {
                                    Intent mainIntent = new Intent(VerificationCheckActivity.this, MainActivity.class);
                                    startActivity(mainIntent);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Please register your account", Toast.LENGTH_LONG).show();
                        Intent startIntent = new Intent(VerificationCheckActivity.this, StartActivity.class);
                        startActivity(startIntent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}

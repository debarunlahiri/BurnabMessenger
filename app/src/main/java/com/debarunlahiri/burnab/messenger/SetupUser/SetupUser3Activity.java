package com.debarunlahiri.burnab.messenger.SetupUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.MainActivity;
import com.debarunlahiri.burnab.messenger.R;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupUser3Activity extends AppCompatActivity {

    private EditText etSetupUser3Username;
    private Button setupuser3savebutton;
    private ImageButton setupuser3backIB;
    private CircleImageView setupuser3profileCIV;
    private TextView tvSetupUser3Name;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;
    private String name, gender, bio, profile_image, username, age;
    private Uri profileImageUri = null;
    private String ds_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_user3);

        setupuser3backIB = findViewById(R.id.setupuser3backIB);
        etSetupUser3Username = findViewById(R.id.etSetupUser3Username);
        setupuser3savebutton = findViewById(R.id.setupuser3savebutton);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        setupuser3backIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        setupuser3savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = etSetupUser3Username.getText().toString();

                if (username.isEmpty()) {
                    etSetupUser3Username.setError("Please enter your username");
                } else if (username.contains(" ")) {
                    etSetupUser3Username.setError("No spaces allowed");
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


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    if (ds_username != null &&  ds_username.equals(username)) {
                        Toast.makeText(getApplicationContext(), "Username already exists", Toast.LENGTH_LONG).show();
                    } else {
                        //Toast.makeText(getApplicationContext(), "insert username", Toast.LENGTH_LONG).show();
                        //mDatabase.child("usernames").child("username").setValue(username);
                        mDatabase.child("users").child(user_id).child("user_data").child("username").setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Profile created successfully", Toast.LENGTH_LONG).show();
                                    Intent finalSetupUserIntent = new Intent(SetupUser3Activity.this, MainActivity.class);
                                    startActivity(finalSetupUserIntent);
                                    mDatabase.child("usernames").push().child("username").setValue(username);
                                    mDatabase.child("users").child(user_id).child("username").setValue(username);
                                    mDatabase.child("users").child(currentUser.getUid()).child("user_data").child("age_change_time_period").setValue(new Date(System.currentTimeMillis()+14L * 24 * 60 * 60 * 1000));
                                    finish();
                                }
                            }
                        });


                    }

                }
            }
        });
    }
}

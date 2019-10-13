package com.debarunlahiri.burnab.messenger.SetupUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.debarunlahiri.burnab.messenger.AddStoryActivity;
import com.debarunlahiri.burnab.messenger.MainActivity;
import com.debarunlahiri.burnab.messenger.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import id.zelory.compressor.Compressor;

public class SetupFinalActivity extends AppCompatActivity {

    private ProgressBar setupfinalPB;
    private TextView tvSetupFinalMessage;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;
    private String name, gender, bio, stringProfileImageURI, username, age;
    private Uri profileImageUri = null;
    private Bitmap mCompressedProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_final);

        Bundle bundle = getIntent().getExtras();
        name = bundle.get("name").toString();
        gender = bundle.get("gender").toString();
        username = bundle.get("username").toString();
        age = bundle.get("age").toString();
        bio = bundle.get("bio").toString();
        stringProfileImageURI = bundle.get("profileImageURI").toString();
        profileImageUri = Uri.parse(stringProfileImageURI);
        Toast.makeText(getApplicationContext(), stringProfileImageURI, Toast.LENGTH_LONG).show();
        setupfinalPB = findViewById(R.id.setupfinalPB);
        tvSetupFinalMessage = findViewById(R.id.tvSetupFinalMessage);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        uploadData();
    }

    private void uploadData() {

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Toast.makeText(getApplicationContext(), "Saving user details. Cannot go back", Toast.LENGTH_LONG).show();
    }
}

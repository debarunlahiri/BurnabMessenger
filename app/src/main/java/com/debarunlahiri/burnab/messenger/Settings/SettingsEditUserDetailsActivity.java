package com.debarunlahiri.burnab.messenger.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class SettingsEditUserDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar editusertoolbar;

    private EditText etEditUserName, etEditUserAge, etEditUserBio;
    private Button editsaveuserdetailsbutton;
    private Spinner genderspinner;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;
    private String name, age, bio;
    private String gender;
    private String edit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_edit_user_details);

        editusertoolbar = findViewById(R.id.editusertoolbar);
        setSupportActionBar(editusertoolbar);
        editusertoolbar.setTitle("Edit details");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editusertoolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.black_back));
        editusertoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        etEditUserName = findViewById(R.id.etEditUserName);
        etEditUserAge = findViewById(R.id.etEditUserAge);
        etEditUserBio = findViewById(R.id.etEditUserBio);
        editsaveuserdetailsbutton = findViewById(R.id.editsaveuserdetailsbutton);
        genderspinner = findViewById(R.id.genderspinner);

        final ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        genderspinner.setAdapter(genderAdapter);
        genderspinner.setOnItemSelectedListener(SettingsEditUserDetailsActivity.this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        mDatabase.child("users").child(user_id).child("user_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                age = dataSnapshot.child("age").getValue().toString();
                bio = dataSnapshot.child("bio").getValue().toString();
                gender = dataSnapshot.child("gender").getValue().toString();

                int spinnerPosition = genderAdapter.getPosition(gender);
                genderspinner.setSelection(spinnerPosition);
                etEditUserAge.setText(age);
                etEditUserBio.setText(bio);
                etEditUserName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        editsaveuserdetailsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserDetailsToFirebase();
            }
        });
    }

    private void saveUserDetailsToFirebase() {
        name = etEditUserName.getText().toString();
        age = etEditUserAge.getText().toString();
        bio = etEditUserBio.getText().toString();
        //gender = genderspinner.getOnItemSelectedListener().toString();

        if (name.isEmpty()) {
            etEditUserName.setError("Please enter your name");
        } else if (age.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter your date", Toast.LENGTH_LONG).show();
        } else if (bio.isEmpty()) {
            etEditUserBio.setError("Please enter your bio");
        } else if (gender.equals("Select Gender")) {
            Toast.makeText(getApplicationContext(), "Please select your gender", Toast.LENGTH_LONG).show();
        } else if (Integer.parseInt(age) <= 13) {
            Toast.makeText(getApplicationContext(), "Please enter our correct age", Toast.LENGTH_LONG).show();
        } else if (name.length() <= 3) {
            Toast.makeText(getApplicationContext(), "Please enter name your correctly", Toast.LENGTH_LONG).show();
        } else {
            saveDetails();
        }
    }

    private void saveDetails() {
        final HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", name);
        dataMap.put("age", age);
        dataMap.put("bio", bio);
        dataMap.put("gender", gender);
        mDatabase.child("users").child(currentUser.getUid()).child("user_data").updateChildren(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mDatabase.child("users").child(currentUser.getUid()).child("user_data").child("age_change_time_period").setValue(new Date(System.currentTimeMillis()+90L * 24 * 60 * 60 * 1000));
                    Toast.makeText(getApplicationContext(), "Details saved successfully", Toast.LENGTH_LONG).show();
                } else {
                    String errMsg = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), "Error: " + errMsg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        gender = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

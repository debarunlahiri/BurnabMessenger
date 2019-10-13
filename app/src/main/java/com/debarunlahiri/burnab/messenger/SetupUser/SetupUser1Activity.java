package com.debarunlahiri.burnab.messenger.SetupUser;

import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class SetupUser1Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button setupuser1nextbutton;
    private EditText etSetupUserName, etSetupUserBio;
    private Spinner setupusergender;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;
    private String gender;
    private String age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_user1);

        Bundle bundle = getIntent().getExtras();
        age = bundle.get("age").toString();

        setupuser1nextbutton = findViewById(R.id.setupuser1nextbutton);
        etSetupUserName = findViewById(R.id.etSetupUserName);
        etSetupUserBio = findViewById(R.id.etSetupUserBio);
        setupusergender = findViewById(R.id.setupusergender);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        final ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        setupusergender.setAdapter(genderAdapter);
        setupusergender.setOnItemSelectedListener(SetupUser1Activity.this);

        setupuser1nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etSetupUserName.getText().toString();
                String bio = etSetupUserBio.getText().toString();

                if (name.isEmpty()) {
                    etSetupUserName.setError("Please enter your name");
                } else if (bio.isEmpty()) {
                    etSetupUserBio.setError("Please enter your bio");
                } else if (gender.equals("Select Gender")) {
                    Toast.makeText(getApplicationContext(), "Please select your gender", Toast.LENGTH_LONG).show();
                } else {
                    HashMap<String, Object> mSetupUser1DataMap = new HashMap<>();
                    mSetupUser1DataMap.put("name", name);
                    mSetupUser1DataMap.put("bio", bio);
                    mSetupUser1DataMap.put("gender", gender);
                    mDatabase.child("users").child(user_id).child("user_data").updateChildren(mSetupUser1DataMap);

                    Intent setupUser2Intent = new Intent(SetupUser1Activity.this, SetupUser2Activity.class);
                    startActivity(setupUser2Intent);
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

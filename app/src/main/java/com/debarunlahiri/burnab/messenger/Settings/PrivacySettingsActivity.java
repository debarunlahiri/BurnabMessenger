package com.debarunlahiri.burnab.messenger.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.debarunlahiri.burnab.messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PrivacySettingsActivity extends AppCompatActivity {

    private Toolbar privacysettingstoolbar;

    private Switch privacyHideFollowingSwitch, privacyHideJoinedGroupsSwitch, privacyHideCreatedGroupsSwitch, privacyPrivateProfileSwitch;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private ValueEventListener valueEventListener, valueEventListener1;

    private String user_id;
    private boolean hide_following, hide_joined_groups, hide_created_groups, private_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_settings);

        privacysettingstoolbar = findViewById(R.id.privacysettingstoolbar);
        privacysettingstoolbar.setTitle("Privacy Settings");
        setSupportActionBar(privacysettingstoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        privacysettingstoolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.black_back));
        privacysettingstoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        privacyHideFollowingSwitch = findViewById(R.id.privacyHideFollowingSwitch);
        privacyHideJoinedGroupsSwitch = findViewById(R.id.privacyHideJoinedGroupsSwitch);
        privacyHideCreatedGroupsSwitch = findViewById(R.id.privacyHideCreatedGroupsSwitch);
        privacyPrivateProfileSwitch = findViewById(R.id.privacyPrivateProfileSwitch);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        fetchUserPrivacy();

        privacyHideFollowingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mDatabase.child("users").child(user_id).child("privacy").child("hide_following").setValue(b);
            }
        });

        privacyHideJoinedGroupsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mDatabase.child("users").child(user_id).child("privacy").child("hide_joined_groups").setValue(b);
            }
        });

        privacyHideCreatedGroupsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mDatabase.child("users").child(user_id).child("privacy").child("hide_created_groups").setValue(b);
            }
        });

        privacyPrivateProfileSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mDatabase.child("users").child(user_id).child("privacy").child("private_profile").setValue(b);
            }
        });


    }

    private void fetchUserPrivacy() {
        mDatabase.child("users").child(user_id).child("privacy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    hide_following = (boolean) dataSnapshot.child("hide_following").getValue();
                    hide_joined_groups = (boolean) dataSnapshot.child("hide_joined_groups").getValue();
                    hide_created_groups = (boolean) dataSnapshot.child("hide_created_groups").getValue();
                    private_profile = (boolean) dataSnapshot.child("private_profile").getValue();

                    privacyHideFollowingSwitch.setChecked(hide_following);
                    privacyHideJoinedGroupsSwitch.setChecked(hide_joined_groups);
                    privacyHideCreatedGroupsSwitch.setChecked(hide_created_groups);
                    privacyPrivateProfileSwitch.setChecked(private_profile);
                } else {
                    Toast.makeText(getApplicationContext(), "Privacy data is not set", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Privacy settings saved successfully", Toast.LENGTH_LONG).show();
        super.onBackPressed();
    }
}

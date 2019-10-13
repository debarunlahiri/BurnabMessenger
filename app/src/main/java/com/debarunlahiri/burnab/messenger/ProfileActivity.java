package com.debarunlahiri.burnab.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.debarunlahiri.burnab.messenger.Settings.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileName, tvProfileUsername, tvProfileFollowers, tvProfileFollowing, tvProfileBio, tvChangeBackground;
    private ImageView backgroundcoverphotoIV;
    private CircleImageView profileCIV;
    private ImageButton profilebackIB;
    private CardView profileSettingsCV;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;
    private String searched_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle bundle = getIntent().getExtras();
        searched_user_id = bundle.get("searched_user_id").toString();

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileUsername = findViewById(R.id.tvProfileUsername);
        tvProfileFollowers = findViewById(R.id.tvProfileFollowers);
        tvProfileFollowing = findViewById(R.id.tvProfileFollowing);
        tvProfileBio = findViewById(R.id.tvProfileBio);
        backgroundcoverphotoIV = findViewById(R.id.backgroundcoverphotoIV);
        profileCIV = findViewById(R.id.profileCIV);
        profilebackIB = findViewById(R.id.profilebackIB);
        tvChangeBackground = findViewById(R.id.tvChangeBackground);
        profileSettingsCV = findViewById(R.id.profileSettingsCV);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        getFollowersCount();
        getFollowingCount();

        profileCIV.setElevation(12);

        profilebackIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (!user_id.equals(searched_user_id)) {
            profileSettingsCV.setVisibility(View.GONE);
        } else {
            profileSettingsCV.setVisibility(View.VISIBLE);
        }

        profileSettingsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });



    }

    private void getFollowingCount() {
        mDatabase.child("following").child(searched_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int following_count = (int) dataSnapshot.getChildrenCount();
                tvProfileFollowing.setText(following_count + " Following");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowersCount() {
        mDatabase.child("followers").child(searched_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int followers_count = (int) dataSnapshot.getChildrenCount();
                if (followers_count == 0) {
                    tvProfileFollowers.setText(followers_count + " Follower");
                } else {
                    tvProfileFollowers.setText(followers_count + " Followers");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserDetails() {
        mDatabase.child("users").child(searched_user_id).child("user_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String username = dataSnapshot.child("username").getValue().toString();
                String profile_image = dataSnapshot.child("profile_image").getValue().toString();
                String bio = dataSnapshot.child("bio").getValue().toString();

                Glide.with(getApplicationContext()).load(profile_image).thumbnail(0.1f).into(profileCIV);
                tvProfileBio.setText(bio);
                tvProfileName.setText(name);
                tvProfileUsername.setText("@" + username);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        mDatabase.child("coverphoto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("image").getValue().toString();
                Glide.with(getApplicationContext()).load(R.drawable.profile_background_gradient).apply(RequestOptions.bitmapTransform(new BlurTransformation(18, 5))).into(backgroundcoverphotoIV);
                setStatusBarGradiant(ProfileActivity.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        getUserDetails();
        super.onStart();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.profile_background_gradient);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }
}

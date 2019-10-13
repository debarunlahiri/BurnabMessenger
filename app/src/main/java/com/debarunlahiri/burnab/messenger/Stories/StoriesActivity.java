package com.debarunlahiri.burnab.messenger.Stories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.MotionEventCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.MainActivity;
import com.debarunlahiri.burnab.messenger.R;
import com.debarunlahiri.burnab.messenger.Utils.OnSwipeTouchListener;
import com.denzcoskun.imageslider.adapters.ViewPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoriesActivity extends AppCompatActivity implements View.OnTouchListener, GestureDetector.OnGestureListener {

    private CardView storiestopCV;
    private TextView tvStoryName;
    private CircleImageView storyuserprofileCIV;

    private List<Stories> storiesList = new ArrayList<>();
    private Context mContext;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;

    private String story_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stories);

        mContext = StoriesActivity.this;

        Bundle bundle = getIntent().getExtras();
        story_user_id = bundle.get("story_user_id").toString();

        storiestopCV = findViewById(R.id.storiestopCV);
        tvStoryName = findViewById(R.id.tvStoryName);
        storyuserprofileCIV = findViewById(R.id.storyuserprofileCIV);

        ViewPager storiesVP = findViewById(R.id.storiesVP);
        StoriesViewPageAdapter storiesViewPageAdapter = new StoriesViewPageAdapter(storiesList, mContext);
        storiesVP.setAdapter(storiesViewPageAdapter);

        storiesVP.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeDown() {
               onBackPressed();
            }
        });



        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        storiestopCV.setBackgroundResource(R.drawable.black_to_transparent);

        mDatabase.child("users").child(story_user_id).child("user_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String profile_image = dataSnapshot.child("profile_image").getValue().toString();
                tvStoryName.setText(name);
                Glide.with(mContext).load(profile_image).thumbnail(0.1f).into(storyuserprofileCIV);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("stories").child(story_user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Stories stories = dataSnapshot.getValue(Stories.class);
                storiesList.add(stories);
                storiesViewPageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        Toast.makeText(mContext, String.valueOf(motionEvent.getY()), Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        return false;
    }
}

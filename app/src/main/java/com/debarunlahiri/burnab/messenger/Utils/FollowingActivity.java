package com.debarunlahiri.burnab.messenger.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.debarunlahiri.burnab.messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FollowingActivity extends AppCompatActivity {

    private Toolbar followingtoolbar;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;
    private boolean forFollowing, forFollower;
    private String searched_user_id;

    private Context mContext;
    private List<Like> likeList = new ArrayList<>();
    private RecyclerView followingRV;
    private LikeAdapter likeAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        mContext = FollowingActivity.this;

        Bundle bundle = getIntent().getExtras();
        forFollower = (boolean) bundle.get("forFollower");
        forFollowing = (boolean) bundle.get("forFollowing");
        searched_user_id = bundle.get("searched_user_id").toString();

        followingtoolbar = findViewById(R.id.followingtoolbar);
        followingtoolbar.setTitle("Following");
        setSupportActionBar(followingtoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        followingtoolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.black_back));
        followingtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        followingRV = findViewById(R.id.followingRV);
        likeAdapter = new LikeAdapter(likeList, mContext, "forStories");
        linearLayoutManager = new LinearLayoutManager(mContext);
        followingRV.setLayoutManager(linearLayoutManager);
        followingRV.setAdapter(likeAdapter);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        if (forFollowing == true) {
            mDatabase.child("following").child(searched_user_id).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Like like = dataSnapshot.getValue(Like.class);
                    likeList.add(like);
                    likeAdapter.notifyDataSetChanged();
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
        } else if (forFollower == true) {
            mDatabase.child("follower").child(searched_user_id).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Like like = dataSnapshot.getValue(Like.class);
                    likeList.add(like);
                    likeAdapter.notifyDataSetChanged();
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

    }
}

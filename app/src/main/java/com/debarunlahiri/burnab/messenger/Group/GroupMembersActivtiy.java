package com.debarunlahiri.burnab.messenger.Group;

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
import com.debarunlahiri.burnab.messenger.Utils.Like;
import com.debarunlahiri.burnab.messenger.Utils.LikeAdapter;
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

public class GroupMembersActivtiy extends AppCompatActivity {

    private Toolbar groupmemberstoolbar;

    private Context mContext;
    private RecyclerView groupmembersRV;
    private LinearLayoutManager linearLayoutManager;
    private LikeAdapter likeAdapter;
    private List<Like> likeList = new ArrayList<>();

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;
    private String group_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members_activtiy);

        mContext = GroupMembersActivtiy.this;

        Bundle bundle = getIntent().getExtras();
        group_id = bundle.get("group_id").toString();

        groupmemberstoolbar = findViewById(R.id.groupmemberstoolbar);
        groupmemberstoolbar.setTitle("Members");
        setSupportActionBar(groupmemberstoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        groupmemberstoolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.black_back));
        groupmemberstoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        groupmembersRV = findViewById(R.id.groupmembersRV);
        likeAdapter = new LikeAdapter(likeList, mContext, "forStories");
        linearLayoutManager = new LinearLayoutManager(mContext);
        groupmembersRV.setAdapter(likeAdapter);
        groupmembersRV.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        mDatabase.child("groups").child(group_id).child("members").addChildEventListener(new ChildEventListener() {
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

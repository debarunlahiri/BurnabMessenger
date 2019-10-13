package com.debarunlahiri.burnab.messenger.Group;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.R;
import com.google.android.exoplayer2.C;
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

public class GroupUserProfileActivity extends AppCompatActivity {

    private ImageButton profilebackIB;
    private TextView tvCreatedGroups, tvGroupJoined;
    private CircleImageView profileCIV;

    private RecyclerView createdgroupsRV, joinedgroupsRV;
    private List<Group> createdgroupList = new ArrayList<>();
    private List<Group> joinedgroupList = new ArrayList<>();
    private Context mContext;
    private GroupAdapter groupAdapter;
    private LinearLayoutManager createdgroupslinearLayoutManager, joinedgroupslinearLayoutManager;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_user_profile);
        setStatusBarGradiant(GroupUserProfileActivity.this);

        mContext = GroupUserProfileActivity.this;

        profilebackIB = findViewById(R.id.profilebackIB);
        tvGroupJoined = findViewById(R.id.tvGroupJoined);
        tvCreatedGroups = findViewById(R.id.tvCreatedGroups);
        profileCIV = findViewById(R.id.profileCIV);

        createdgroupsRV = findViewById(R.id.createdgroupsRV);
        joinedgroupsRV = findViewById(R.id.joinedgroupsRV);
        createdgroupslinearLayoutManager = new LinearLayoutManager(mContext);
        joinedgroupslinearLayoutManager = new LinearLayoutManager(mContext);
        createdgroupsRV.setLayoutManager(createdgroupslinearLayoutManager);
        joinedgroupsRV.setLayoutManager(joinedgroupslinearLayoutManager);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        profilebackIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mDatabase.child("users").child(user_id).child("user_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profile_image = dataSnapshot.child("profile_image").getValue().toString();
                Glide.with(mContext).load(profile_image).thumbnail(0.1f).into(profileCIV);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("groups").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Group group = dataSnapshot.getValue(Group.class);
                if (group.getGroup_admin_user_id().equals(user_id)) {
                    tvCreatedGroups.setVisibility(View.GONE);
                    groupAdapter = new GroupAdapter(createdgroupList, mContext, "groupscreated");
                    createdgroupsRV.setAdapter(groupAdapter);
                    createdgroupList.add(group);
                    groupAdapter.notifyDataSetChanged();
                }
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

        mDatabase.child("groups").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Group group = dataSnapshot.getValue(Group.class);
                mDatabase.child("groups").child(group.getGroup_id()).child("members").child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            tvGroupJoined.setVisibility(View.GONE);
                            groupAdapter = new GroupAdapter(joinedgroupList, mContext, "joinedgroups");
                            joinedgroupsRV.setAdapter(groupAdapter);
                            joinedgroupList.add(group);
                            groupAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.group_user_profile_bg);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }
}

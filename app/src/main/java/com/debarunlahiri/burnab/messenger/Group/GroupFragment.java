package com.debarunlahiri.burnab.messenger.Group;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.Inbox.Inbox;
import com.debarunlahiri.burnab.messenger.Inbox.InboxAdapter;
import com.debarunlahiri.burnab.messenger.OverallProfileActivity;
import com.debarunlahiri.burnab.messenger.ProfileActivity;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {

    private CircleImageView groupfragmentprofileCIV;
    private ImageButton creategroupIB, inboxsearchIB;
    private TextView tvNoGroupConvo;
    private ProgressBar groupConvoPB;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private RecyclerView fragmentgroupRV;
    private InboxGroupAdapter inboxGroupAdapter;
    private List<Group> groupList = new ArrayList<>();
    private Context mContext;
    private LinearLayoutManager linearLayoutManager;

    private String user_id;


    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();

        groupfragmentprofileCIV = view.findViewById(R.id.groupfragmentprofileCIV);
        creategroupIB = view.findViewById(R.id.creategroupIB);
        inboxsearchIB = view.findViewById(R.id.inboxsearchIB);
        tvNoGroupConvo = view.findViewById(R.id.tvNoGroupConvo);
        groupConvoPB = view.findViewById(R.id.groupConvoPB);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        fragmentgroupRV = view.findViewById(R.id.fragmentgroupRV);
        inboxGroupAdapter = new InboxGroupAdapter(groupList, mContext);
        linearLayoutManager = new LinearLayoutManager(mContext);
        fragmentgroupRV.setAdapter(inboxGroupAdapter);
        fragmentgroupRV.setLayoutManager(linearLayoutManager);

        user_id = currentUser.getUid();
        groupfragmentprofileCIV.setVisibility(View.GONE);
        tvNoGroupConvo.setVisibility(View.VISIBLE);
        groupConvoPB.setVisibility(View.GONE);

        fetchGroup();

        creategroupIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createGroupIntent = new Intent(getActivity(), CreateGroupActivity.class);
                startActivity(createGroupIntent);
            }
        });

        groupfragmentprofileCIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(getActivity(), OverallProfileActivity.class);
                profileIntent.putExtra("searched_user_id", user_id);
                startActivity(profileIntent);
            }
        });

        inboxsearchIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(getActivity(), GroupSearchActivity.class);
                profileIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(profileIntent);
            }
        });


    }

    private void fetchGroup() {
        mDatabase.child("groups").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                groupList.clear();
                Group group = dataSnapshot.getValue(Group.class);
                mDatabase.child("groups").child(group.getGroup_id()).child("members").child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            tvNoGroupConvo.setVisibility(View.GONE);
                            //groupConvoPB.setVisibility(View.GONE);
                            groupList.add(group);
                            inboxGroupAdapter.notifyDataSetChanged();
                        } else {
                            //groupConvoPB.setVisibility(View.GONE);
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
        mDatabase.child("users").child(user_id).child("user_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    if (dataSnapshot.child("profile_image").exists()) {
                        String profile_image = dataSnapshot.child("profile_image").getValue().toString();
                        Glide.with(getActivity()).load(profile_image).thumbnail(0.1f).into(groupfragmentprofileCIV);
                        groupfragmentprofileCIV.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fadein));
                        groupfragmentprofileCIV.setVisibility(View.VISIBLE);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}

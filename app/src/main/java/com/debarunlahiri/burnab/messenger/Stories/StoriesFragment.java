package com.debarunlahiri.burnab.messenger.Stories;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.AddStoryActivity;
import com.debarunlahiri.burnab.messenger.OverallProfileActivity;
import com.debarunlahiri.burnab.messenger.R;
import com.debarunlahiri.burnab.messenger.Utils.Following;
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
public class StoriesFragment extends Fragment {

    private CircleImageView groupfragmentprofileCIV;
    private ImageButton creategroupIB, inboxsearchIB;
    private TextView tvNoStories;
    private ProgressBar storiesPB;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private Context mContext;
    private RecyclerView storiesRV;
    private StoriesAdapter storiesAdapter;
    private List<Stories> storiesList = new ArrayList<>();
    private List<Following> followingList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;

    private String user_id;


    public StoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();



        groupfragmentprofileCIV = view.findViewById(R.id.groupfragmentprofileCIV);
        creategroupIB = view.findViewById(R.id.creategroupIB);
        inboxsearchIB = view.findViewById(R.id.inboxsearchIB);
        tvNoStories = view.findViewById(R.id.tvNoStories);
        storiesPB = view.findViewById(R.id.storiesFragPB);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        storiesRV = view.findViewById(R.id.storiesRV);
        storiesAdapter = new StoriesAdapter(storiesList, mContext);
        gridLayoutManager = new GridLayoutManager(mContext, 2);
        storiesRV.setAdapter(storiesAdapter);
        storiesRV.setLayoutManager(gridLayoutManager);

        user_id = currentUser.getUid();
        groupfragmentprofileCIV.setVisibility(View.GONE);
        tvNoStories.setVisibility(View.GONE);
        storiesPB.setVisibility(View.GONE);

        fetchStories();

        creategroupIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent storyIntent = new Intent(getActivity(), AddStoryActivity.class);
                startActivity(storyIntent);
            }
        });

        groupfragmentprofileCIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userStoryProfileIntent = new Intent(getActivity(), OverallProfileActivity.class);
                userStoryProfileIntent.putExtra("searched_user_id", user_id);
                startActivity(userStoryProfileIntent);
            }
        });
    }

    private void fetchStories() {
        mDatabase.child("stories").child(user_id).limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                storiesList.clear();
                Stories stories = dataSnapshot.getValue(Stories.class);
                storiesList.add(stories);
                storiesAdapter.notifyDataSetChanged();

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
        mDatabase.child("following").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storiesList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String following_key = ds.getKey();
                    mDatabase.child("stories").child(following_key).limitToLast(1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if (dataSnapshot.exists()) {
                                tvNoStories.setVisibility(View.GONE);
                                Stories stories = dataSnapshot.getValue(Stories.class);
                                storiesList.add(stories);
                                storiesAdapter.notifyDataSetChanged();
                            } else {
                                tvNoStories.setVisibility(View.VISIBLE);
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
                }
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
        user_id = currentUser.getUid();
        mDatabase.child("stories").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tvNoStories.setVisibility(View.GONE);
                } else {
                    tvNoStories.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        user_id = currentUser.getUid();
        mDatabase.child("stories").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tvNoStories.setVisibility(View.GONE);
                } else {
                    tvNoStories.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

package com.debarunlahiri.burnab.messenger.Inbox;


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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.Notifications.Inbox.NotificationsActivity;
import com.debarunlahiri.burnab.messenger.OverallProfileActivity;
import com.debarunlahiri.burnab.messenger.ProfileActivity;
import com.debarunlahiri.burnab.messenger.R;
import com.debarunlahiri.burnab.messenger.Search.SearchActivity;
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
public class InboxFragment extends Fragment {

    private CircleImageView circleImageView;
    private TextView tvNoMessages, tvInboxChat;
    private ImageButton inboxsearchIB, inboxnotificationIB;
    private ProgressBar inboxPB;
    private ImageView inboxnotificationsnotifyIV;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private RecyclerView inboxRV;
    private InboxAdapter inboxAdapter;
    private List<Inbox> inboxList = new ArrayList<>();
    private Context mContext;
    private LinearLayoutManager linearLayoutManager;

    private String user_id;

    public InboxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();

        circleImageView = view.findViewById(R.id.groupfragmentprofileCIV);
        tvInboxChat = view.findViewById(R.id.tvInboxChat);
        inboxsearchIB = view.findViewById(R.id.inboxsearchIB);
        tvNoMessages = view.findViewById(R.id.tvNoMessages);
        inboxPB = view.findViewById(R.id.inboxPB);
        inboxnotificationIB = view.findViewById(R.id.inboxnotificationIB);
        inboxnotificationsnotifyIV = view.findViewById(R.id.inboxnotificationsnotifyIV);

        inboxRV = view.findViewById(R.id.inboxRV);
        inboxAdapter = new InboxAdapter(inboxList, mContext);
        linearLayoutManager = new LinearLayoutManager(mContext);
        inboxRV.setAdapter(inboxAdapter);
        inboxRV.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();
        circleImageView.setVisibility(View.GONE);
        inboxPB.setVisibility(View.VISIBLE);
        tvNoMessages.setVisibility(View.GONE);

        fetchInbox();

        mDatabase.child("requests").child("following").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    inboxnotificationsnotifyIV.setVisibility(View.VISIBLE);
                } else {
                    inboxnotificationsnotifyIV.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tvInboxChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(getActivity(), OverallProfileActivity.class);
                profileIntent.putExtra("searched_user_id", user_id);
                startActivity(profileIntent);
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
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
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                searchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(searchIntent);
            }
        });

        inboxnotificationIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notificationIntent = new Intent(getActivity(), NotificationsActivity.class);
                startActivity(notificationIntent);
            }
        });

        inboxPB.setVisibility(View.GONE);
    }

    private void fetchInbox() {
        mDatabase.child("chats").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inboxList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String user_key = ds.getKey();

                    mDatabase.child("chats").child(user_id).child(user_key).limitToLast(1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if (dataSnapshot.exists()) {
                                tvNoMessages.setVisibility(View.GONE);
                                inboxPB.setVisibility(View.GONE);
                                Inbox inbox = dataSnapshot.getValue(Inbox.class);
                                inboxList.add(inbox);
                                inbox.setUser_key(user_key);
                                inboxAdapter.notifyDataSetChanged();
                            } else {
                                tvNoMessages.setVisibility(View.VISIBLE);
                                inboxPB.setVisibility(View.GONE);
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
        getUserDetails();
    }

    private void getUserDetails() {
        mDatabase.child("users").child(user_id).child("user_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    if (dataSnapshot.child("profile_image").exists()) {
                        String profile_image = dataSnapshot.child("profile_image").getValue().toString();
                        Glide.with(getActivity()).load(profile_image).thumbnail(0.1f).into(circleImageView);
                        circleImageView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fadein));
                        circleImageView.setVisibility(View.VISIBLE);
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
        mDatabase.child("chats").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tvNoMessages.setVisibility(View.GONE);
                } else {
                    tvNoMessages.setVisibility(View.VISIBLE);

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
        mDatabase.child("chats").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tvNoMessages.setVisibility(View.GONE);
                } else {
                    tvNoMessages.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

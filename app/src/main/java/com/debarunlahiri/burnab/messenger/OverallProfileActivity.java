package com.debarunlahiri.burnab.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.debarunlahiri.burnab.messenger.Chat.ChatActivity;
import com.debarunlahiri.burnab.messenger.Group.Group;
import com.debarunlahiri.burnab.messenger.Group.GroupAdapter;
import com.debarunlahiri.burnab.messenger.Group.GroupChat;
import com.debarunlahiri.burnab.messenger.Group.GroupChatAdapter;
import com.debarunlahiri.burnab.messenger.Settings.SettingsActivity;
import com.debarunlahiri.burnab.messenger.Stories.Stories;
import com.debarunlahiri.burnab.messenger.Stories.StoriesActivity;
import com.debarunlahiri.burnab.messenger.Stories.StoriesUserProfileActivity;
import com.debarunlahiri.burnab.messenger.Stories.StoryUserProfileViewerActivity;
import com.debarunlahiri.burnab.messenger.Utils.Following;
import com.debarunlahiri.burnab.messenger.Utils.FollowingActivity;
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
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class OverallProfileActivity extends AppCompatActivity {

    private ImageButton groupchatbackIB;
    private CircleImageView profileCIV, groupchatprofileCIV;
    private TextView tvProfileFollowers, tvProfileFollowing, tvProfileBio, tvProfileName, tvProfileUsername, tvOverallProfileChangeBackground;
    private TextView tvCreatedGroups, tvGroupJoined, tvIndicateProfilePrivate, tvStories;
    private Button overallprofilemessagebutton, overallprofilefollbutton;
    private ImageView overallprofilebackgroundIV;
    private CardView overallstoriesCV, profileBottomCV, createdGroupsCV, joinedGroupsCV;
    private NestedScrollView overallprofileNSV;

    private RecyclerView createdgroupsRV, joinedgroupsRV;
    private List<Group> createdgroupList = new ArrayList<>();
    private List<Group> joinedgroupList = new ArrayList<>();
    private Context mContext;
    private GroupAdapter groupAdapter;
    private LinearLayoutManager createdgroupslinearLayoutManager, joinedgroupslinearLayoutManager;

    private RecyclerView storiesuserprofileRV;
    private List<Stories> storiesList = new ArrayList<>();
    private ProfileStoriesAdapter profileStoriesAdapter;
    private LinearLayoutManager linearLayoutManagerHorizontal;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;
    private String searched_user_id;
    private boolean userHasFollowed, followingIsOnRequest = false;
    private String name, profile_image, username, bio;
    private boolean hide_following = false, hide_joined_groups = false, hide_created_groups = false, private_profile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
        setContentView(R.layout.activity_overall_profile);


        mContext = OverallProfileActivity.this;
        Bundle bundle = getIntent().getExtras();
        searched_user_id = bundle.get("searched_user_id").toString();

        //TextView
        tvProfileFollowers = findViewById(R.id.tvProfileFollowers);
        tvProfileFollowing = findViewById(R.id.tvProfileFollowing);
        tvProfileBio = findViewById(R.id.tvProfileBio);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileUsername = findViewById(R.id.tvProfileUsername);
        tvGroupJoined = findViewById(R.id.tvGroupJoined);
        tvCreatedGroups = findViewById(R.id.tvCreatedGroups);
        tvIndicateProfilePrivate = findViewById(R.id.tvIndicateProfilePrivate);
        tvOverallProfileChangeBackground = findViewById(R.id.tvOverallProfileChangeBackground);

        //ImageButton
        groupchatbackIB = findViewById(R.id.groupchatbackIB);

        //ImageView
        profileCIV = findViewById(R.id.profileCIV);
        groupchatprofileCIV = findViewById(R.id.groupchatprofileCIV);
        overallprofilebackgroundIV = findViewById(R.id.overallprofilebackgroundIV);

        //Button
        overallprofilemessagebutton = findViewById(R.id.overallprofilemessagebutton);
        overallprofilefollbutton = findViewById(R.id.overallprofilefollbutton);

        //CardView
        overallstoriesCV = findViewById(R.id.overallstoriesCV);
        profileBottomCV = findViewById(R.id.profileBottomCV);
        createdGroupsCV = findViewById(R.id.createdGroupsCV);
        joinedGroupsCV = findViewById(R.id.joinedGroupsCV);

        //Others
        overallprofileNSV = findViewById(R.id.overallprofileNSV);

        storiesuserprofileRV = findViewById(R.id.storiesuserprofileRV);
        profileStoriesAdapter = new ProfileStoriesAdapter(storiesList, mContext);
        linearLayoutManagerHorizontal = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, true);
        storiesuserprofileRV.setAdapter(profileStoriesAdapter);
        storiesuserprofileRV.setLayoutManager(linearLayoutManagerHorizontal);
        storiesuserprofileRV.setHasFixedSize(true);
        linearLayoutManagerHorizontal.setStackFromEnd(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        overallprofilemessagebutton.setVisibility(View.GONE);
        if (searched_user_id.equals(user_id)) {
            overallprofilefollbutton.setVisibility(View.GONE);
            overallprofilemessagebutton.setVisibility(View.GONE);
            tvOverallProfileChangeBackground.setVisibility(View.VISIBLE);
        } else {
            tvOverallProfileChangeBackground.setVisibility(View.GONE);
        }

        groupchatprofileCIV.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            overallprofileNSV.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    int x = i1-i3;
                    if (overallprofileNSV != null) {
                        if (x > 0) {
                            groupchatprofileCIV.setVisibility(View.VISIBLE);
                        } else if (overallprofileNSV.getScrollY() < 0) {
                            groupchatprofileCIV.setVisibility(View.GONE);
                        }
                    }

                }
            });
        }

        createdgroupsRV = findViewById(R.id.createdgroupsRV);
        joinedgroupsRV = findViewById(R.id.joinedgroupsRV);
        createdgroupslinearLayoutManager = new LinearLayoutManager(mContext);
        joinedgroupslinearLayoutManager = new LinearLayoutManager(mContext);

        getFollowersCount();
        getFollowingCount();
        getSearchedUserDetails();
        checkUserFollowingOrNot();
        checkUserCreatedGroupsOrNot();
        checkUserJoinedGroupsOrNot();
        fetchUserStories();
        fetchUserPrivacy();
        fetchCreatedGroups();

        mDatabase.child("requests").child("following").child(searched_user_id).child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    followingIsOnRequest = true;
                    overallprofilefollbutton.setText("Requested");
                } else {
                    followingIsOnRequest = false;
                    overallprofilefollbutton.setText("Follow");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("users").child(searched_user_id).child("privacy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    hide_following = (boolean) dataSnapshot.child("hide_following").getValue();
                    hide_joined_groups = (boolean) dataSnapshot.child("hide_joined_groups").getValue();
                    hide_created_groups = (boolean) dataSnapshot.child("hide_created_groups").getValue();
                    private_profile = (boolean) dataSnapshot.child("private_profile").getValue();

                    if (hide_following == true && !searched_user_id.equals(user_id)) {
                        tvProfileFollowing.setVisibility(View.GONE);
                    } else {
                        tvProfileFollowing.setVisibility(View.VISIBLE);
                    }

                    if (hide_created_groups == true && !searched_user_id.equals(user_id)) {
                        createdGroupsCV.setVisibility(View.GONE);
                    } else {
                        createdGroupsCV.setVisibility(View.VISIBLE);
                    }

                    if (hide_joined_groups == true && !searched_user_id.equals(user_id)) {
                        joinedGroupsCV.setVisibility(View.GONE);
                    } else {
                        joinedGroupsCV.setVisibility(View.VISIBLE);
                    }

                    if (searched_user_id.equals(user_id)) {
                        if (private_profile == true) {
                            tvIndicateProfilePrivate.setText("Your profile is private");
                        } else {
                            tvIndicateProfilePrivate.setVisibility(View.GONE);
                        }
                    } else {
                        if (private_profile == true) {
                            tvIndicateProfilePrivate.setText("This account is private");
                            profileBottomCV.setVisibility(View.GONE);
                            overallstoriesCV.setVisibility(View.GONE);
                            overallprofilemessagebutton.setVisibility(View.GONE);
                        } else {
                            tvIndicateProfilePrivate.setVisibility(View.GONE);
                        }
                    }

                    overallprofilefollbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (followingIsOnRequest == true) {
                                mDatabase.child("requests").child("following").child(searched_user_id).child(user_id).removeValue();
                                overallprofilefollbutton.setText("Follow");
                            } else {
                                if (userHasFollowed == true) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setTitle("Unfollow " + username + "?");
                                    builder.setMessage("Are you sure you want to unfollow " + username + "?");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mDatabase.child("following").child(user_id).child(searched_user_id).removeValue();
                                            mDatabase.child("follower").child(searched_user_id).child(user_id).removeValue();
                                            overallprofilefollbutton.setText("Follow");
                                            overallprofilemessagebutton.setVisibility(View.GONE);
                                            Toast.makeText(mContext, "You have unfollowed " + username, Toast.LENGTH_LONG).show();
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });
                                    builder.show();
                                } else {
                                    if (private_profile == true) {
                                        overallprofilemessagebutton.setVisibility(View.GONE);
                                        HashMap<String, Object> mFollowDataMap = new HashMap<>();
                                        mFollowDataMap.put("user_id", user_id);
                                        //mDatabase.child("following").child(user_id).child(searched_user_id).setValue(mFollowDataMap);
                                        //mDatabase.child("follower").child(searched_user_id).child(user_id).setValue(mFollowDataMap);
                                        mDatabase.child("requests").child("following").child(searched_user_id).child(user_id).setValue(mFollowDataMap);
                                        //Toast.makeText(mContext, "You are now following " + username, Toast.LENGTH_LONG).show();
                                        overallprofilefollbutton.setText("Requested");
                                        overallprofilefollbutton.setBackgroundResource(R.drawable.primary_buton_bg);
                                    } else {
                                        overallprofilemessagebutton.setVisibility(View.VISIBLE);
                                        HashMap<String, Object> mFollowDataMap = new HashMap<>();
                                        mFollowDataMap.put("user_id", user_id);
                                        mDatabase.child("following").child(user_id).child(searched_user_id).setValue(mFollowDataMap);
                                        mDatabase.child("follower").child(searched_user_id).child(user_id).setValue(mFollowDataMap);
                                        //mDatabase.child("requests").child("following").child(user_id).child(searched_user_id).setValue(mFollowDataMap);
                                        Toast.makeText(mContext, "You are now following " + username, Toast.LENGTH_LONG).show();
                                        overallprofilefollbutton.setText("Following");
                                        overallprofilemessagebutton.setVisibility(View.VISIBLE);
                                    }


                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Privacy data is not set", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        groupchatbackIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        overallprofilemessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(mContext, ChatActivity.class);
                chatIntent.putExtra("searched_user_id", searched_user_id);
                startActivity(chatIntent);
            }
        });

        tvOverallProfileChangeBackground.setText("Settings");
        tvOverallProfileChangeBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(OverallProfileActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

        tvProfileFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent followerIntent = new Intent(OverallProfileActivity.this, FollowingActivity.class);
                followerIntent.putExtra("forFollower", true);
                followerIntent.putExtra("forFollowing", false);
                followerIntent.putExtra("searched_user_id", searched_user_id);
                startActivity(followerIntent);
            }
        });

        tvProfileFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent followerIntent = new Intent(OverallProfileActivity.this, FollowingActivity.class);
                followerIntent.putExtra("forFollower", false);
                followerIntent.putExtra("forFollowing", true);
                followerIntent.putExtra("searched_user_id", searched_user_id);
                startActivity(followerIntent);
            }
        });


    }

    private void fetchCreatedGroups() {
    }

    private void fetchUserPrivacy() {

    }

    private void fetchUserStories() {
        mDatabase.child("stories").child(searched_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    overallstoriesCV.setVisibility(View.VISIBLE);
                } else {
                    overallstoriesCV.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDatabase.child("stories").child(searched_user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Stories stories = dataSnapshot.getValue(Stories.class);
                storiesList.add(stories);
                profileStoriesAdapter.notifyDataSetChanged();
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

    private void getSearchedUserDetails() {
        mDatabase.child("users").child(searched_user_id).child("user_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                profile_image = dataSnapshot.child("profile_image").getValue().toString();
                username = dataSnapshot.child("username").getValue().toString();
                bio = dataSnapshot.child("bio").getValue().toString();

                Glide.with(mContext).load(profile_image).thumbnail(0.1f).into(profileCIV);
                Glide.with(mContext).load(profile_image).thumbnail(0.1f).into(groupchatprofileCIV);
                tvProfileBio.setText(bio);
                tvProfileUsername.setText("@" + username);
                tvProfileName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserJoinedGroupsOrNot() {
        mDatabase.child("groups").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Group group = dataSnapshot.getValue(Group.class);
                mDatabase.child("groups").child(group.getGroup_id()).child("members").child(searched_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            tvGroupJoined.setVisibility(View.GONE);
                            groupAdapter = new GroupAdapter(joinedgroupList, mContext, "joinedgroups");
                            joinedgroupsRV.setAdapter(groupAdapter);
                            joinedgroupsRV.setLayoutManager(joinedgroupslinearLayoutManager);
                            joinedgroupList.add(group);
                            groupAdapter.notifyDataSetChanged();
                            joinedgroupslinearLayoutManager.setReverseLayout(true);
                            joinedgroupslinearLayoutManager.setStackFromEnd(true);
                        } else {
                            tvGroupJoined.setVisibility(View.VISIBLE);
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

    private void checkUserCreatedGroupsOrNot() {
        mDatabase.child("groups").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Group group = dataSnapshot.getValue(Group.class);
                if (group.getGroup_admin_user_id().equals(searched_user_id)) {
                    tvCreatedGroups.setVisibility(View.GONE);
                    groupAdapter = new GroupAdapter(createdgroupList, mContext, "groupscreated");
                    createdgroupsRV.setAdapter(groupAdapter);
                    createdgroupsRV.setLayoutManager(createdgroupslinearLayoutManager);
                    createdgroupList.add(group);
                    groupAdapter.notifyDataSetChanged();
                    createdgroupslinearLayoutManager.setReverseLayout(true);
                    createdgroupslinearLayoutManager.setStackFromEnd(true);
                } else {
                    tvCreatedGroups.setVisibility(View.VISIBLE);
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

    private void checkUserFollowingOrNot() {
        mDatabase.child("following").child(user_id).child(searched_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userHasFollowed = true;
                    overallprofilefollbutton.setText("Following");
                } else {
                    userHasFollowed = false;
                    overallprofilefollbutton.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    public class ProfileStoriesAdapter extends RecyclerView.Adapter<ProfileStoriesAdapter.ViewHolder> {

        private List<Stories> storiesList;
        private Context mContext;

        public ProfileStoriesAdapter(List<Stories> storiesList, Context mContext) {
            this.storiesList = storiesList;
            this.mContext = mContext;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stories_profile_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Stories stories = storiesList.get(position);

            Glide.with(mContext).load(stories.getStory_image()).thumbnail(0.1f).into(holder.storiesprofilelistIV);

            holder.storiesprofilelistCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent storyIntent = new Intent(mContext, StoryUserProfileViewerActivity.class);
                    storyIntent.putExtra("story_id", stories.getStory_id());
                    storyIntent.putExtra("story_user_id", searched_user_id);
                    mContext.startActivity(storyIntent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return storiesList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private CardView storiesprofilelistCV;
            private ImageView storiesprofilelistIV;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                storiesprofilelistCV = itemView.findViewById(R.id.storiesprofilelistCV);
                storiesprofilelistIV = itemView.findViewById(R.id.storiesprofilelistIV);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user_id = currentUser.getUid();
        mDatabase.child("users").child(user_id).child("user_data").child("profile_background_image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String profile_background_image = dataSnapshot.getValue().toString();
                    Glide.with(mContext).load(profile_background_image).apply(RequestOptions.bitmapTransform(new BlurTransformation(38, 12))).into(overallprofilebackgroundIV);
                } else {
                    Glide.with(mContext).load(R.drawable.overallprofilebg).apply(RequestOptions.bitmapTransform(new BlurTransformation(38, 12))).into(overallprofilebackgroundIV);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        user_id = currentUser.getUid();
        mDatabase.child("users").child(user_id).child("user_data").child("profile_background_image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String profile_background_image = dataSnapshot.getValue().toString();
                    Glide.with(mContext).load(profile_background_image).apply(RequestOptions.bitmapTransform(new BlurTransformation(38, 12))).into(overallprofilebackgroundIV);
                } else {
                    Glide.with(mContext).load(R.drawable.overallprofilebg).apply(RequestOptions.bitmapTransform(new BlurTransformation(38, 12))).into(overallprofilebackgroundIV);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

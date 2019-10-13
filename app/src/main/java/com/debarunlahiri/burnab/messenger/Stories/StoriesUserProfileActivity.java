package com.debarunlahiri.burnab.messenger.Stories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.Group.Group;
import com.debarunlahiri.burnab.messenger.Group.GroupAdapter;
import com.debarunlahiri.burnab.messenger.Group.GroupUserProfileActivity;
import com.debarunlahiri.burnab.messenger.R;
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

public class StoriesUserProfileActivity extends AppCompatActivity {

    private ImageButton profilebackIB;
    private TextView tvCreatedGroups, tvStoryProfileName, tvStoryProfileUsername;
    private CircleImageView profileCIV;

    private RecyclerView storiesuserprofileRV;
    private List<Stories> storiesList = new ArrayList<>();
    private Context mContext;
    private StoryUserProfileAdapter storyUserProfileAdapter;
    private GridLayoutManager gridLayoutManager;

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
        setContentView(R.layout.activity_stories_user_profile);
        setStatusBarGradiant(StoriesUserProfileActivity.this);

        mContext = StoriesUserProfileActivity.this;

        Bundle bundle = getIntent().getExtras();
        story_user_id = bundle.get("story_user_id").toString();

        profilebackIB = findViewById(R.id.profilebackIB);
        tvCreatedGroups = findViewById(R.id.tvCreatedGroups);
        profileCIV = findViewById(R.id.profileCIV);
        tvStoryProfileName = findViewById(R.id.tvStoryProfileName);
        tvStoryProfileUsername = findViewById(R.id.tvStoryProfileUsername);

        storiesuserprofileRV = findViewById(R.id.storiesuserprofileRV);
        storyUserProfileAdapter = new StoryUserProfileAdapter(storiesList, mContext);
        gridLayoutManager = new GridLayoutManager(mContext, 3);
        storiesuserprofileRV.setAdapter(storyUserProfileAdapter);;
        storiesuserprofileRV.setLayoutManager(gridLayoutManager);
        storiesuserprofileRV.setHasFixedSize(true);


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

        mDatabase.child("users").child(story_user_id).child("user_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profile_image = dataSnapshot.child("profile_image").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                String username = dataSnapshot.child("username").getValue().toString();
                Glide.with(mContext).load(profile_image).thumbnail(0.1f).into(profileCIV);
                tvStoryProfileName.setText(name);
                tvStoryProfileUsername.setText(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("stories").child(story_user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    tvCreatedGroups.setVisibility(View.GONE);
                    Stories stories = dataSnapshot.getValue(Stories.class);
                    storiesList.add(stories);
                    storyUserProfileAdapter.notifyDataSetChanged();
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

    public class StoryUserProfileAdapter extends RecyclerView.Adapter<StoryUserProfileAdapter.ViewHolder> {

        private List<Stories> storiesList;
        private Context mContext;

        public StoryUserProfileAdapter(List<Stories> storiesList, Context mContext) {
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
                    storyIntent.putExtra("story_user_id", story_user_id);
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.story_user_profile_bg);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }
}

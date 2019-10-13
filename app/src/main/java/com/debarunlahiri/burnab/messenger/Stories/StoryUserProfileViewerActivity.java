package com.debarunlahiri.burnab.messenger.Stories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.debarunlahiri.burnab.messenger.R;
import com.debarunlahiri.burnab.messenger.Utils.OnSwipeTouchListener;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class StoryUserProfileViewerActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;
    private String story_id, story_user_id;

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_story_user_profile_viewer);



        mContext = StoryUserProfileViewerActivity.this;

        Bundle bundle = getIntent().getExtras();
        story_id = bundle.get("story_id").toString();
        story_user_id = bundle.get("story_user_id").toString();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        ImageView storybgIV = findViewById(R.id.storybgIV);
        ImageView storyIV = findViewById(R.id.storyIV);
        CardView storybottomCV = findViewById(R.id.storybottomCV);
        RelativeTimeTextView tvStoriesTime = findViewById(R.id.tvStoriesTime);
        TextView tvStoriesLikeCount = findViewById(R.id.tvStoriesLikeCount);
        TextView tvStoriesCommentCount = findViewById(R.id.tvStoriesCommentCount);

        storyIV.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeDown() {
                onBackPressed();
            }
        });

        mDatabase.child("stories").child(story_user_id).child(story_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Stories stories = dataSnapshot.getValue(Stories.class);

                Glide.with(mContext).load(stories.getStory_image()).apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).into(storybgIV);
                Glide.with(mContext).load(stories.getStory_image()).into(storyIV);
                tvStoriesTime.setReferenceTime(stories.getTimestamp());
                storybottomCV.setBackgroundResource(R.drawable.blacktotransparent2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("stories").child(story_user_id).child(story_id).child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int likes_count = (int) dataSnapshot.getChildrenCount();
                    if (likes_count == 1) {
                        tvStoriesLikeCount.setText(likes_count + " Like");
                    } else {
                        tvStoriesLikeCount.setText(likes_count + " Likes");
                    }
                } else {
                    tvStoriesLikeCount.setText("0 Likes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("stories").child(story_user_id).child(story_id).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int comment_count = (int) dataSnapshot.getChildrenCount();
                    if (comment_count == 1) {
                        tvStoriesCommentCount.setText(comment_count + " Comment");
                    } else {
                        tvStoriesCommentCount.setText(comment_count + " Comments");
                    }
                } else {
                    tvStoriesCommentCount.setText("0 Comments");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

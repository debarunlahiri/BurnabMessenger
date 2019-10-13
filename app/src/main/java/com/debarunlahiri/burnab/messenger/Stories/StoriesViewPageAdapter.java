package com.debarunlahiri.burnab.messenger.Stories;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.debarunlahiri.burnab.messenger.MainActivity;
import com.debarunlahiri.burnab.messenger.R;
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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class StoriesViewPageAdapter extends PagerAdapter implements View.OnTouchListener, GestureDetector.OnGestureListener {

    private List<Stories> storiesList;
    private Context mContext;
    private LayoutInflater layoutInflater;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;
    private boolean userHasLiked;

    public StoriesViewPageAdapter(List<Stories> storiesList, Context mContext) {
        this.storiesList = storiesList;
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return storiesList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.stories_viewpager_list_item, container, false);
        Stories stories = storiesList.get(position);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        ImageView storybgIV = view.findViewById(R.id.storybgIV);
        ImageView storyIV = view.findViewById(R.id.storyIV);
        CardView storybottomCV = view.findViewById(R.id.storybottomCV);
        RelativeTimeTextView tvStoriesTime = view.findViewById(R.id.tvStoriesTime);
        TextView tvStoriesLikeCount = view.findViewById(R.id.tvStoriesLikeCount);
        TextView tvStoriesCommentCount = view.findViewById(R.id.tvStoriesCommentCount);
        ImageButton storylikeIB = view.findViewById(R.id.storylikeIB);
        Button commentpostbutton = view.findViewById(R.id.commentpostbutton);
        EditText etSroryComment = view.findViewById(R.id.etSroryComment);

        //storyIV.setOnTouchListener(this);

        Glide.with(mContext).load(stories.getStory_image()).apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).into(storybgIV);
        Glide.with(mContext).load(stories.getStory_image()).into(storyIV);
        tvStoriesTime.setReferenceTime(stories.getTimestamp());
        storybottomCV.setBackgroundResource(R.drawable.blacktotransparent2);

        mDatabase.child("stories").child(stories.getUser_id()).child(stories.getStory_id()).child("likes").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userHasLiked = true;
                    storylikeIB.setBackgroundResource(R.mipmap.white_hearted);
                } else {
                    userHasLiked = false;
                    storylikeIB.setBackgroundResource(R.mipmap.white_heart);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        storylikeIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userHasLiked == false) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    String formattedDate = sdf.format(new Date());
                    HashMap<String, Object> mStoryLikeDataMap = new HashMap<>();
                    mStoryLikeDataMap.put("user_id", user_id);
                    mStoryLikeDataMap.put("timestamp", System.currentTimeMillis());
                    mStoryLikeDataMap.put("formatted_date", formattedDate);
                    mStoryLikeDataMap.put("stoy_id", stories.getStory_id());
                    mStoryLikeDataMap.put("story_user_id", stories.getUser_id());
                    mDatabase.child("stories").child(stories.getUser_id()).child(stories.getStory_id()).child("likes").child(user_id).setValue(mStoryLikeDataMap);
                    storylikeIB.setBackgroundResource(R.mipmap.white_hearted);
                } else if (userHasLiked == true) {
                    mDatabase.child("stories").child(stories.getUser_id()).child(stories.getStory_id()).child("likes").child(user_id).removeValue();
                    storylikeIB.setBackgroundResource(R.mipmap.white_heart);
                }
            }
        });

        mDatabase.child("stories").child(stories.getUser_id()).child(stories.getStory_id()).child("likes").addValueEventListener(new ValueEventListener() {
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

        commentpostbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = etSroryComment.getText().toString();
                if (comment.isEmpty()) {
                    Toast.makeText(mContext, "Cannot post empty comment", Toast.LENGTH_LONG).show();
                } else {
                    etSroryComment.setText("");
                    String comment_id = mDatabase.child("stories").child(stories.getUser_id()).child(stories.getStory_id()).child("comments").push().getKey();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    String formattedDate = sdf.format(new Date());
                    HashMap<String, Object> mCommentDataMap = new HashMap<>();
                    mCommentDataMap.put("comment", comment.trim());
                    mCommentDataMap.put("timestamp", System.currentTimeMillis());
                    mCommentDataMap.put("formatted_date", formattedDate);
                    mCommentDataMap.put("user_id", user_id);
                    mCommentDataMap.put("story_id", stories.getStory_id());
                    mCommentDataMap.put("story_user_id", stories.getUser_id());
                    mCommentDataMap.put("comment_id", comment_id);
                    mDatabase.child("stories").child(stories.getUser_id()).child(stories.getStory_id()).child("comments").child(comment_id).setValue(mCommentDataMap);
                }
            }
        });

        mDatabase.child("stories").child(stories.getUser_id()).child(stories.getStory_id()).child("comments").addValueEventListener(new ValueEventListener() {
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

        tvStoriesLikeCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent showLikesIntent = new Intent(mContext, StoryLikeCommentActivity.class);
                showLikesIntent.putExtra("show_likes", true);
                showLikesIntent.putExtra("show_comment", false);
                showLikesIntent.putExtra("story_id", stories.getStory_id());
                showLikesIntent.putExtra("story_user_id", stories.getUser_id());
                mContext.startActivity(showLikesIntent);
            }
        });

        tvStoriesCommentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent showCommentIntent = new Intent(mContext, StoryLikeCommentActivity.class);
                showCommentIntent.putExtra("show_comments", true);
                showCommentIntent.putExtra("show_likes", false);
                showCommentIntent.putExtra("story_id", stories.getStory_id());
                showCommentIntent.putExtra("story_user_id", stories.getUser_id());
                mContext.startActivity(showCommentIntent);
            }
        });

        container.addView(view);
        return view;
        //return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {

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
        //Toast.makeText(mContext, String.valueOf(motionEvent.getY()), Toast.LENGTH_SHORT).show();

        return false;
    }


}

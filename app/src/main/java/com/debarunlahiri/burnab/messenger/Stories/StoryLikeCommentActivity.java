package com.debarunlahiri.burnab.messenger.Stories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.debarunlahiri.burnab.messenger.R;
import com.debarunlahiri.burnab.messenger.Utils.Comment;
import com.debarunlahiri.burnab.messenger.Utils.CommentAdapter;
import com.debarunlahiri.burnab.messenger.Utils.Like;
import com.debarunlahiri.burnab.messenger.Utils.LikeAdapter;
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

public class StoryLikeCommentActivity extends AppCompatActivity {

    private Toolbar storylikecommenttoolbar;

    private RecyclerView storylikecommentRV;
    private CommentAdapter commentAdapter;
    private LikeAdapter likeAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private List<Like> likeList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private Context mContext;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;
    private String story_id;
    private String story_user_id;

    private boolean show_likes = false;
    private boolean show_comments = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_like_comment);

        mContext = StoryLikeCommentActivity.this;

        Bundle bundle = getIntent().getExtras();
        show_likes = bundle.getBoolean("show_likes");
        show_comments = bundle.getBoolean("show_comments");
        story_id = bundle.get("story_id").toString();
        story_user_id = bundle.get("story_user_id").toString();

        storylikecommenttoolbar = findViewById(R.id.storylikecommenttoolbar);
        if (show_likes == true) {
            storylikecommenttoolbar.setTitle("Likes");
        } else {
            storylikecommenttoolbar.setTitle("Comments");
        }
        setSupportActionBar(storylikecommenttoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        storylikecommenttoolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.black_back));
        storylikecommenttoolbar.setNavigationOnClickListener(new View.OnClickListener() {
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

        storylikecommentRV = findViewById(R.id.storylikecommentRV);

        if (show_likes == true) {
            mDatabase.child("stories").child(story_user_id).child(story_id).child("likes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int like_count = (int) dataSnapshot.getChildrenCount();
                    if (like_count == 1) {
                        storylikecommenttoolbar.setSubtitle(like_count + " Like");
                    } else {
                        storylikecommenttoolbar.setSubtitle(like_count + " Likes");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            likeAdapter = new LikeAdapter(likeList, mContext, "forStories");
            linearLayoutManager = new LinearLayoutManager(mContext);
            storylikecommentRV.setLayoutManager(linearLayoutManager);
            storylikecommentRV.setAdapter(likeAdapter);
            storylikecommentRV.setHasFixedSize(true);

            mDatabase.child("stories").child(story_user_id).child(story_id).child("likes").addChildEventListener(new ChildEventListener() {
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

        } else {
            mDatabase.child("stories").child(story_user_id).child(story_id).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int like_count = (int) dataSnapshot.getChildrenCount();
                    if (like_count == 1) {
                        storylikecommenttoolbar.setSubtitle(like_count + " Comment");
                    } else {
                        storylikecommenttoolbar.setSubtitle(like_count + " Comments");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            commentAdapter = new CommentAdapter(commentList, mContext, "forStories");
            linearLayoutManager = new LinearLayoutManager(mContext);
            storylikecommentRV.setLayoutManager(linearLayoutManager);
            storylikecommentRV.setAdapter(commentAdapter);
            storylikecommentRV.setHasFixedSize(true);
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);

            mDatabase.child("stories").child(story_user_id).child(story_id).child("comments").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                    commentAdapter.notifyDataSetChanged();
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

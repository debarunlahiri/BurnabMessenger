package com.debarunlahiri.burnab.messenger.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.OverallProfileActivity;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> commentList;
    private String type;
    private Context mContext;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;
    private boolean userHasLikedComment;

    public CommentAdapter(List<Comment> commentList, Context mContext, String type) {
        this.commentList = commentList;
        this.type = type;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        TextPaint paint = holder.tvCommentLike.getPaint();
        float width = paint.measureText("Tianjin, China");

        Shader textShader = new LinearGradient(0, 0, width, holder.tvCommentLike.getTextSize(),
                new int[]{
                        Color.parseColor("#5851DB"),
                        Color.parseColor("#C13584"),
                        Color.parseColor("#E1306C"),
                }, null, Shader.TileMode.CLAMP);


        if (type.equals("forStories")) {
            setCommentDetails(holder, comment);

            mDatabase.child("stories").child(comment.getStory_user_id()).child(comment.getStory_id())
                    .child("comments").child(comment.getComment_id()).child("likes").child(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userHasLikedComment = true;
                        holder.tvCommentLike.setText("Liked");
                        //holder.tvCommentLike.getPaint().setShader(textShader);
                        
                    } else {
                        userHasLikedComment = false;
                        holder.tvCommentLike.setText("Like");
                        //holder.tvCommentLike.setTextColor(mContext.getResources().getColor(R.color.colorGray));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mDatabase.child("stories").child(comment.getStory_user_id()).child(comment.getStory_id())
                    .child("comments").child(comment.getComment_id()).child("likes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int like_count = (int) dataSnapshot.getChildrenCount();
                    if (like_count == 1) {
                        holder.tvCommentLikeCounter.setText(like_count + " Like");
                    } else {
                        holder.tvCommentLikeCounter.setText(like_count + " Likes");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            holder.tvCommentLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userHasLikedComment == true) {
                        mDatabase.child("stories").child(comment.getStory_user_id()).child(comment.getStory_id())
                                .child("comments").child(comment.getComment_id()).child("likes").child(user_id).removeValue();
                        holder.tvCommentLike.setText("Like");
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                        String formattedDate = sdf.format(new Date());
                        HashMap<String, Object> mCommentLikeDataMap = new HashMap<>();
                        mCommentLikeDataMap.put("story_id", comment.getStory_id());
                        mCommentLikeDataMap.put("story_user_id", comment.getStory_user_id());
                        mCommentLikeDataMap.put("comment_id", comment.getComment_id());
                        mCommentLikeDataMap.put("user_id", user_id);
                        mCommentLikeDataMap.put("timestamp", System.currentTimeMillis());
                        mCommentLikeDataMap.put("formatted_date", formattedDate);
                        mDatabase.child("stories").child(comment.getStory_user_id()).child(comment.getStory_id())
                                .child("comments").child(comment.getComment_id()).child("likes").child(user_id).setValue(mCommentLikeDataMap);
                        holder.tvCommentLike.setText("Liked");

                    }
                }
            });

        }

        holder.commentprofileCIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(mContext, OverallProfileActivity.class);
                profileIntent.putExtra("searched_user_id", comment.getUser_id());
                mContext.startActivity(profileIntent);
            }
        });

    }

    private void setCommentDetails(ViewHolder holder, Comment comment) {
        holder.tvCommentBody.setText(comment.getComment());
        holder.tvCommentPosted.setReferenceTime(comment.getTimestamp());
        mDatabase.child("users").child(comment.getUser_id()).child("user_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String profile_image = dataSnapshot.child("profile_image").getValue().toString();
                Glide.with(mContext).load(profile_image).thumbnail(0.1f).into(holder.commentprofileCIV);
                holder.tvCommentName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView commentCV;
        private TextView tvCommentName, tvCommentBody, tvCommentLike, tvCommentLikeCounter;
        private RelativeTimeTextView tvCommentPosted;
        private CircleImageView commentprofileCIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            commentCV = itemView.findViewById(R.id.commentCV);
            tvCommentName = itemView.findViewById(R.id.tvCommentName);
            tvCommentBody = itemView.findViewById(R.id.tvCommentBody);
            tvCommentLike = itemView.findViewById(R.id.tvCommentLike);
            tvCommentLikeCounter = itemView.findViewById(R.id.tvCommentLikeCounter);
            tvCommentPosted = itemView.findViewById(R.id.tvCommentPosted);
            commentprofileCIV = itemView.findViewById(R.id.commentprofileCIV);
        }
    }
}

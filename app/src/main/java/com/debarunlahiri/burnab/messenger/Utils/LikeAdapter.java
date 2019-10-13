package com.debarunlahiri.burnab.messenger.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.OverallProfileActivity;
import com.debarunlahiri.burnab.messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.icu.lang.UProperty.INT_START;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder> {

    private List<Like> likeList;
    private Context mContext;
    private String type;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;
    private boolean userHasFollowed;

    private String name, profile_image, username;

    public LikeAdapter(List<Like> likeList, Context mContext, String type) {
        this.likeList = likeList;
        this.mContext = mContext;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.like_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Like like = likeList.get(position);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        if (type.equals("forStories")) {
            setLiKeDetails(holder, like);
            checkUserFollowedOrNot(holder, like);

            if (like.getUser_id().equals(user_id)) {
                holder.likefollowbutton.setVisibility(View.INVISIBLE);
            } else {
                holder.likefollowbutton.setVisibility(View.VISIBLE);
            }

            holder.likefollowbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userHasFollowed == true) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Unfollow " + username + "?");
                        builder.setMessage("Are you sure you want to unfollow " + username + "?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDatabase.child("following").child(user_id).child(like.getUser_id()).removeValue();
                                mDatabase.child("follower").child(like.getUser_id()).child(user_id).removeValue();
                                holder.likefollowbutton.setText("Follow");
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
                        HashMap<String, Object> mFollowDataMap = new HashMap<>();
                        mFollowDataMap.put("user_id", user_id);
                        mDatabase.child("following").child(user_id).child(like.getUser_id()).setValue(mFollowDataMap);
                        mDatabase.child("follower").child(like.getUser_id()).child(user_id).setValue(mFollowDataMap);
                        Toast.makeText(mContext, "You are now following " + username, Toast.LENGTH_LONG).show();
                        holder.likefollowbutton.setText("Following");
                    }
                }
            });
        }

        holder.likeCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(mContext, OverallProfileActivity.class);
                profileIntent.putExtra("searched_user_id", like.getUser_id());
                mContext.startActivity(profileIntent);
            }
        });

    }

    private void checkUserFollowedOrNot(ViewHolder holder, Like like) {
        mDatabase.child("following").child(user_id).child(like.getUser_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userHasFollowed = true;
                    holder.likefollowbutton.setText("Following");
                } else {
                    userHasFollowed = false;
                    holder.likefollowbutton.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setLiKeDetails(ViewHolder holder, Like like) {
        mDatabase.child("users").child(like.getUser_id()).child("user_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                username = dataSnapshot.child("username").getValue().toString();
                profile_image = dataSnapshot.child("profile_image").getValue().toString();

                Glide.with(mContext).load(profile_image).thumbnail(0.1f).into(holder.likeprofileCIV);
                holder.tvLikeName.setText(name);
                holder.tvLikeUsername.setText("@" + username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return likeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView likeCV;
        private TextView tvLikeName, tvLikeUsername;
        private Button likefollowbutton;
        private CircleImageView likeprofileCIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            likeCV = itemView.findViewById(R.id.likeCV);
            tvLikeName = itemView.findViewById(R.id.tvLikeName);
            tvLikeUsername = itemView.findViewById(R.id.tvLikeUsername);
            likefollowbutton = itemView.findViewById(R.id.likefollowbutton);
            likeprofileCIV = itemView.findViewById(R.id.likeprofileCIV);
        }
    }
}

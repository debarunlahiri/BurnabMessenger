package com.debarunlahiri.burnab.messenger.Group;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.ViewHolder> {

    private List<GroupChat> groupChatList;
    private Context mContext;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;

    public GroupChatAdapter(List<GroupChat> groupChatList, Context mContext) {
        this.groupChatList = groupChatList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupChat groupChat = groupChatList.get(position);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        setMessage(holder, position);
        setUserDetails(holder, position);

        holder.tvGroupChatTime.setVisibility(View.GONE);

        if (position > 0) {
            GroupChat previousGroupChat = groupChatList.get(position-1);
            if (previousGroupChat != null) {
                if (previousGroupChat.getUser_id().equals(groupChat.getUser_id())) {
                    if (Math.abs(previousGroupChat.getTimestamp()-groupChat.getTimestamp()) > 180000) {
                        holder.tvGroupChatName.setVisibility(View.VISIBLE);
                        holder.groupchatprofileCIV.setVisibility(View.VISIBLE);
                    } else {
                        holder.tvGroupChatName.setVisibility(View.GONE);
                        holder.groupchatprofileCIV.setVisibility(View.GONE);
                    }

                }
            }
        }

        holder.tvGroupChatMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.tvGroupChatTime.getVisibility() == View.GONE) {
                    holder.tvGroupChatTime.setVisibility(View.VISIBLE);
                } else if (holder.tvGroupChatTime.getVisibility() == View.VISIBLE) {
                    holder.tvGroupChatTime.setVisibility(View.GONE);
                }
            }
        });
    }



    private void setUserDetails(ViewHolder holder, int position) {
        GroupChat groupChat = groupChatList.get(position);
        mDatabase.child("users").child(groupChat.getUser_id()).child("user_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String profile_image = dataSnapshot.child("profile_image").getValue().toString();
                    if (groupChat.getUser_id().equals(user_id)) {
                        holder.tvGroupChatName.setText("You");
                    } else {
                        holder.tvGroupChatName.setText(name);
                    }
                    Glide.with(mContext).load(profile_image).thumbnail(0.1f).into(holder.groupchatprofileCIV);
                } else {
                    holder.tvGroupChatName.setText("Unknown User");
                    holder.tvGroupChatName.setTypeface(null, Typeface.ITALIC);
                    Glide.with(mContext).load(R.drawable.default_profile_pic).thumbnail(0.1f).into(holder.groupchatprofileCIV);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setMessage(ViewHolder holder, int position) {
        GroupChat groupChat = groupChatList.get(position);
        holder.tvGroupChatMessage.setText(groupChat.getMessage());
        holder.tvGroupChatTime.setReferenceTime(groupChat.getTimestamp());
        mDatabase.child("groups").child(groupChat.getGroup_id()).child("members").child(user_id).child("chat_color_code").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String color = dataSnapshot.getValue().toString();
                    holder.tvGroupChatName.setTextColor(Integer.parseInt(color));
                } else {
                    holder.tvGroupChatName.setTextColor(Color.BLACK);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    @Override
    public int getItemCount() {
        return groupChatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvGroupChatName, tvGroupChatMessage;
        private RelativeTimeTextView tvGroupChatTime;
        private CircleImageView groupchatprofileCIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvGroupChatName = itemView.findViewById(R.id.tvGroupChatName);
            tvGroupChatMessage = itemView.findViewById(R.id.tvGroupChatMessage);
            tvGroupChatTime = itemView.findViewById(R.id.tvGroupChatTime);
            groupchatprofileCIV = itemView.findViewById(R.id.groupchatprofileCIV);

        }
    }
}

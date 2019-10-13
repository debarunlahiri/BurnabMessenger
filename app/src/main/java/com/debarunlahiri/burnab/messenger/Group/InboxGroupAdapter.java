package com.debarunlahiri.burnab.messenger.Group;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.R;
import com.github.curioustechizen.ago.RelativeTimeTextView;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class InboxGroupAdapter extends RecyclerView.Adapter<InboxGroupAdapter.ViewHolder> {

    private List<Group> groupList;
    private Context mContext;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;

    public InboxGroupAdapter(List<Group> groupList, Context mContext) {
        this.groupList = groupList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_group_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = groupList.get(position);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");
        user_id = currentUser.getUid();

        setGroupDetails(holder, group);

        holder.inboxgroupCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("groups").child(group.getGroup_id()).child("members").child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Intent groupChatIntent = new Intent(mContext, GroupChatActivity.class);
                            groupChatIntent.putExtra("group_id", group.getGroup_id());
                            mContext.startActivity(groupChatIntent);
                        } else {
                            Toast.makeText(mContext, "You are no longer member of this group", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    private void setGroupDetails(ViewHolder holder, Group group) {
        Glide.with(mContext).load(group.getGroup_profile_image()).thumbnail(0.1f).into(holder.tvGroupInboxProfileCIV);
        holder.tvGroupInboxName.setText(group.getGroup_name());
        mDatabase.child("groups").child(group.getGroup_id()).child("chats").limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                GroupChat groupChat = dataSnapshot.getValue(GroupChat.class);
                if (groupChat.getUser_id().equals(user_id)) {
                    holder.tvGroupInboxMessagae.setText("You: " + groupChat.getMessage());
                } else {
                    holder.tvGroupInboxMessagae.setText(groupChat.getMessage());
                }

                holder.tvGroupInboxTime.setReferenceTime(groupChat.getTimestamp());
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

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvGroupInboxName, tvGroupInboxMessagae;
        private CircleImageView tvGroupInboxProfileCIV;
        private RelativeTimeTextView tvGroupInboxTime;
        private CardView inboxgroupCV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupInboxName = itemView.findViewById(R.id.tvGroupInboxName);
            tvGroupInboxMessagae = itemView.findViewById(R.id.tvGroupInboxMessagae);
            tvGroupInboxProfileCIV = itemView.findViewById(R.id.tvGroupInboxProfileCIV);
            tvGroupInboxTime = itemView.findViewById(R.id.tvGroupInboxTime);
            inboxgroupCV = itemView.findViewById(R.id.inboxgroupCV);
        }
    }
}

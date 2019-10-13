package com.debarunlahiri.burnab.messenger.Group;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<Group> groupList;
    private Context mContext;
    private String typegroup = null;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;




    public GroupAdapter(List<Group> groupList, Context mContext, String typegroup) {
        this.groupList = groupList;
        this.mContext = mContext;
        this.typegroup = typegroup;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_item, parent, false);
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





        if (typegroup == null) {
            setGroupDetails(holder, position);
        } else {
            if (typegroup.equals("groupscreated")) {
                holder.groupsearchCV.setBackgroundColor(Color.TRANSPARENT);
                holder.tvGroupMembers.setTextColor(Color.WHITE);
                holder.tvGroupDesc.setTextColor(Color.WHITE);
                holder.tvGroupName.setTextColor(Color.WHITE);
                setCreatedGroupDetails(holder, group);
            }
            if (typegroup.equals("joinedgroups")) {
                holder.groupsearchCV.setBackgroundColor(Color.TRANSPARENT);
                holder.tvGroupMembers.setTextColor(Color.WHITE);
                holder.tvGroupDesc.setTextColor(Color.WHITE);
                holder.tvGroupName.setTextColor(Color.WHITE);
                setJoinedGroupsDetails(holder, group);
            }
        }


        holder.groupsearchCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent groupIntent = new Intent(mContext, GroupActivity.class);
                groupIntent.putExtra("group_id", group.getGroup_id());
                mContext.startActivity(groupIntent);
            }
        });
    }

    private void setJoinedGroupsDetails(ViewHolder holder, Group group) {
        holder.tvGroupName.setText(group.getGroup_name());
        holder.tvGroupDesc.setText(group.getGroup_desc());
        Glide.with(mContext).load(group.getGroup_profile_image()).thumbnail(0.1f).into(holder.groupprofileimageCIV);
        mDatabase.child("groups").child(group.getGroup_id()).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int group_members_count = (int) dataSnapshot.getChildrenCount();
                    if (group_members_count == 1) {
                        holder.tvGroupMembers.setText("There is only " + String.valueOf(group_members_count) + " member in this group");
                    } else {
                        holder.tvGroupMembers.setText("There are " + String.valueOf(group_members_count) + " members in this group");
                    }

                } else {
                    holder.tvGroupMembers.setText("There are no members in this group");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setCreatedGroupDetails(ViewHolder holder, Group group) {
        holder.tvGroupName.setText(group.getGroup_name());
        holder.tvGroupDesc.setText(group.getGroup_desc());
        Glide.with(mContext).load(group.getGroup_profile_image()).thumbnail(0.1f).into(holder.groupprofileimageCIV);
        mDatabase.child("groups").child(group.getGroup_id()).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int group_members_count = (int) dataSnapshot.getChildrenCount();
                    if (group_members_count == 1) {
                        holder.tvGroupMembers.setText("There is only " + String.valueOf(group_members_count) + " member in this group");
                    } else {
                        holder.tvGroupMembers.setText("There are " + String.valueOf(group_members_count) + " members in this group");
                    }

                } else {
                    holder.tvGroupMembers.setText("There are no members in this group");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setGroupDetails(ViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.tvGroupName.setText(group.getGroup_name());
        holder.tvGroupDesc.setText(group.getGroup_desc());
        Glide.with(mContext).load(group.getGroup_profile_image()).thumbnail(0.1f).into(holder.groupprofileimageCIV);
        mDatabase.child("groups").child(group.getGroup_id()).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int group_members_count = (int) dataSnapshot.getChildrenCount();
                    if (group_members_count == 1) {
                        holder.tvGroupMembers.setText("There is only " + String.valueOf(group_members_count) + " member in this group");
                    } else {
                        holder.tvGroupMembers.setText("There are " + String.valueOf(group_members_count) + " in this group");
                    }

                } else {
                    holder.tvGroupMembers.setText("There are no members in this group");
                }

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

        private CircleImageView groupprofileimageCIV;
        private TextView tvGroupName, tvGroupDesc, tvGroupMembers;
        private CardView groupsearchCV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupprofileimageCIV = itemView.findViewById(R.id.groupprofileimageCIV);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvGroupDesc = itemView.findViewById(R.id.tvGroupDesc);
            tvGroupMembers = itemView.findViewById(R.id.tvGroupMembers);
            groupsearchCV = itemView.findViewById(R.id.groupsearchCV);
        }
    }
}

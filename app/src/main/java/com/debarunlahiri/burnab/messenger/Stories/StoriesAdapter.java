package com.debarunlahiri.burnab.messenger.Stories;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ViewHolder> {

    private List<Stories> storiesList;
    private Context mContext;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;

    public StoriesAdapter(List<Stories> storiesList, Context mContext) {
        this.storiesList = storiesList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stories_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stories stories = storiesList.get(position);
        holder.storieslistitemprofileCV.setBackgroundResource(R.drawable.story_profile_bg);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        setStory(holder, stories);

        holder.storylistitemCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent storyIntent = new Intent(mContext, StoriesActivity.class);
                storyIntent.putExtra("story_user_id", stories.getUser_id());
                mContext.startActivity(storyIntent);
            }
        });
    }

    private void setStory(ViewHolder holder, Stories stories) {
        Glide.with(mContext).load(stories.getStory_image()).thumbnail(0.1f).into(holder.storieslistitemStoryIV);
        mDatabase.child("users").child(stories.getUser_id()).child("user_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String profile_image = dataSnapshot.child("profile_image").getValue().toString();
               Glide.with(mContext).load(profile_image).thumbnail(0.1f).into(holder.storieslistitemuserprofileCIV);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return storiesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView storylistitemCV, storieslistitemprofileCV;
        private ImageView storieslistitemStoryIV;
        private CircleImageView storieslistitemuserprofileCIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storieslistitemprofileCV = itemView.findViewById(R.id.storieslistitemprofileCV);
            storylistitemCV = itemView.findViewById(R.id.storylistitemCV);
            storieslistitemStoryIV = itemView.findViewById(R.id.storieslistitemStoryIV);
            storieslistitemuserprofileCIV = itemView.findViewById(R.id.storieslistitemuserprofileCIV);
        }
    }
}

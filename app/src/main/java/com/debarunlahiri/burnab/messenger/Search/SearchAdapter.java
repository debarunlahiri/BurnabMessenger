package com.debarunlahiri.burnab.messenger.Search;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.Chat.ChatActivity;
import com.debarunlahiri.burnab.messenger.OverallProfileActivity;
import com.debarunlahiri.burnab.messenger.ProfileActivity;
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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<Search> searchList;
    private Context mContext;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;


    public SearchAdapter(List<Search> searchList, Context mContext) {
        this.searchList = searchList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Search search = searchList.get(position);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();


        getUserDetails(holder, position);

        holder.searchCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //searchList.clear();
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(holder.searchprofileCIV, "searchProfileImage");
                if (search.getUser_id().equals(user_id)) {
                    Intent chatIntent = new Intent(mContext, OverallProfileActivity.class);
                    chatIntent.putExtra("searched_user_id", search.getUser_id());
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, pairs);
                    mContext.startActivity(chatIntent, options.toBundle());
                } else {
                    Intent chatIntent = new Intent(mContext, ChatActivity.class);
                    chatIntent.putExtra("searched_user_id", search.getUser_id());
                    mContext.startActivity(chatIntent);
                }
                String push_recent_search_id = mDatabase.child("search").child("recent_searches").child(user_id).push().getKey();
                HashMap<String, Object> mRecentSearchDataMap = new HashMap<>();
                mRecentSearchDataMap.put("user_id", search.getUser_id());
                mRecentSearchDataMap.put("timestamp", System.currentTimeMillis());
                mRecentSearchDataMap.put("push_recent_search_id", push_recent_search_id);
                mDatabase.child("search").child("recent_searches").child(user_id).child(search.getUser_id()).setValue(mRecentSearchDataMap);
            }
        });
    }

    private void getUserDetails(ViewHolder holder, int position) {
        Search search = searchList.get(position);

        mDatabase.child("users").child(search.getUser_id()).child("user_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profile_image = dataSnapshot.child("profile_image").getValue().toString();
                String username = dataSnapshot.child("username").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();

                Glide.with(mContext).load(profile_image).thumbnail(0.1f).into(holder.searchprofileCIV);
                holder.tvSearchName.setText(name);
                holder.tvSearchUsername.setText("@" + username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView searchprofileCIV;
        private TextView tvSearchName, tvSearchUsername;
        private CardView searchCV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            searchprofileCIV = itemView.findViewById(R.id.searchprofileCIV);
            tvSearchName = itemView.findViewById(R.id.tvSearchName);
            tvSearchUsername = itemView.findViewById(R.id.tvSearchUsername);
            searchCV = itemView.findViewById(R.id.searchCV);
        }
    }


}

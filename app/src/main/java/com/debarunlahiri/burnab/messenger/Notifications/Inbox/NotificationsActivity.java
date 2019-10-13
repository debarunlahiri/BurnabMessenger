package com.debarunlahiri.burnab.messenger.Notifications.Inbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.R;
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
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsActivity extends AppCompatActivity {

    private Toolbar inboxnotificationstoolbar;

    private TextView textView40;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;

    private RecyclerView inboxnotificationsRV;
    private Context mContext;
    private List<Notifications> notificationsList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private NotificationsAdapter notificationsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        mContext = NotificationsActivity.this;

        inboxnotificationstoolbar = findViewById(R.id.inboxnotificationstoolbar);
        inboxnotificationstoolbar.setTitle("Follow Requests");
        setSupportActionBar(inboxnotificationstoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        inboxnotificationstoolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.black_back));
        inboxnotificationstoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        textView40 = findViewById(R.id.textView40);

        inboxnotificationsRV = findViewById(R.id.inboxnotificationsRV);
        notificationsAdapter = new NotificationsAdapter(notificationsList, mContext);
        linearLayoutManager = new LinearLayoutManager(mContext);
        inboxnotificationsRV.setAdapter(notificationsAdapter);
        inboxnotificationsRV.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();


        mDatabase.child("requests").child("following").child(user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    textView40.setVisibility(View.GONE);
                    Notifications notifications = dataSnapshot.getValue(Notifications.class);
                    notificationsList.add(notifications);
                    notificationsAdapter.notifyDataSetChanged();
                } else {
                    textView40.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                for (Notifications notifications : notificationsList) {
                    if (key.equals(notifications.getUser_id())) {
                        notificationsList.remove(notifications);
                        notificationsAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

        private List<Notifications> notificationsList;
        private Context mContext;

        public NotificationsAdapter(List<Notifications> notificationsList, Context mContext) {
            this.notificationsList = notificationsList;
            this.mContext = mContext;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_request_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Notifications notifications = notificationsList.get(position);

            mDatabase = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            mStorage = FirebaseStorage.getInstance();
            storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

            user_id = currentUser.getUid();

            mDatabase.child("users").child(notifications.getUser_id()).child("user_data").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String profile_image = dataSnapshot.child("profile_image").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();

                    Glide.with(mContext).load(profile_image).thumbnail(0.1f).into(holder.followrequestprofileCIV);
                    holder.tvFollowRequestUsername.setText(username);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            holder.followrequestdeletebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDatabase.child("requests").child("following").child(user_id).child(notifications.getUser_id()).removeValue();
                }
            });

            holder.followrequestconfirmbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, Object> mFollowDataMap = new HashMap<>();
                    mFollowDataMap.put("user_id", user_id);
                    mDatabase.child("following").child(user_id).child(notifications.getUser_id()).setValue(mFollowDataMap);
                    mDatabase.child("follower").child(notifications.getUser_id()).child(user_id).setValue(mFollowDataMap);
                    mDatabase.child("requests").child("following").child(user_id).child(notifications.getUser_id()).removeValue();
                }
            });
        }

        @Override
        public int getItemCount() {
            return notificationsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private CircleImageView followrequestprofileCIV;
            private TextView tvFollowRequestUsername;
            private Button followrequestconfirmbutton, followrequestdeletebutton;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                followrequestprofileCIV = itemView.findViewById(R.id.followrequestprofileCIV);
                tvFollowRequestUsername = itemView.findViewById(R.id.tvFollowRequestUsername);
                followrequestconfirmbutton = itemView.findViewById(R.id.followrequestconfirmbutton);
                followrequestdeletebutton = itemView.findViewById(R.id.followrequestdeletebutton);
            }
        }
    }
}

package com.debarunlahiri.burnab.messenger.Inbox;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.Chat.ChatActivity;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private List<Inbox> inboxList;
    private Context mContext;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;

    public InboxAdapter(List<Inbox> inboxList, Context mContext) {
        this.inboxList = inboxList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inbox inbox = inboxList.get(position);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        setInbox(holder, position);
        setUserDetails(holder, position);
        setWhoMessagedIdentification(holder, position);
        checkUserOnlineOrNot(holder, position);

        holder.inboxCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(mContext, ChatActivity.class);
                chatIntent.putExtra("searched_user_id", inbox.getUser_key());
                mContext.startActivity(chatIntent);
            }
        });



    }

    private void checkUserOnlineOrNot(ViewHolder holder, int position) {
        Inbox inbox = inboxList.get(position);
        mDatabase.child("active").child(inbox.getUser_key()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean isOnline = (boolean) dataSnapshot.child("isOnline").getValue();
                    long timestamp = (long) dataSnapshot.child("timestamp").getValue();
                    if (Math.abs(timestamp - System.currentTimeMillis()) > 86400000) {
                        holder.tvUserOnlineIndicator1.setVisibility(View.INVISIBLE);
                        holder.tvUserOnlineIndicator2.setVisibility(View.INVISIBLE);
                    }
                    String formatted_date = dataSnapshot.child("formatted_date").getValue().toString();
                    if (isOnline == false) {
                        holder.inboxuseronlineCV.setVisibility(View.GONE);
                        holder.tvUserOnlineIndicator1.setText("Active");
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        Date date = null;
                        try {
                            date = inputFormat.parse(formatted_date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String niceDateStr = (String) DateUtils.getRelativeTimeSpanString(date.getTime() , Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS);
                        holder.tvUserOnlineIndicator2.setText(niceDateStr);

                    } else {
                        holder.inboxuseronlineCV.setVisibility(View.VISIBLE);
                        holder.tvUserOnlineIndicator1.setVisibility(View.GONE);
                        holder.tvUserOnlineIndicator2.setVisibility(View.GONE);
                    }
                } else {
                    holder.inboxuseronlineCV.setVisibility(View.GONE);
                    holder.tvUserOnlineIndicator1.setVisibility(View.GONE);
                    holder.tvUserOnlineIndicator2.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setWhoMessagedIdentification(ViewHolder holder, int position) {
        Inbox inbox = inboxList.get(position);
        if (inbox.getSender_user_id().equals(user_id)) {
            holder.tvInboxMessage.setText("You: " + inbox.getMessage());
        } else {
            holder.tvInboxMessage.setText(inbox.getMessage());
        }
    }

    private void setUserDetails(ViewHolder holder, int position) {
        Inbox inbox = inboxList.get(position);
        mDatabase.child("users").child(inbox.getUser_key()).child("user_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String profile_image = dataSnapshot.child("profile_image").getValue().toString();
                    holder.tvInboxName.setText(name);
                    Glide.with(mContext).load(profile_image).thumbnail(0.1f).into(holder.inboxprofileCIV);
                } else {
                    holder.tvInboxName.setText("Unknown user");
                    Glide.with(mContext).load(R.drawable.default_profile_pic).into(holder.inboxprofileCIV);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setInbox(ViewHolder holder, int position) {
        Inbox inbox = inboxList.get(position);
        holder.tvInboxMessage.setText(inbox.getMessage());
        holder.tvInboxTime.setReferenceTime(inbox.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return inboxList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView inboxprofileCIV;
        private TextView tvInboxName, tvInboxMessage, tvUserOnlineIndicator1;
        private RelativeTimeTextView tvInboxTime;
        private CardView inboxCV, inboxuseronlineCV;
        private RelativeTimeTextView tvUserOnlineIndicator2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            inboxprofileCIV = itemView.findViewById(R.id.inboxprofileCIV);
            tvInboxName = itemView.findViewById(R.id.tvInboxName);
            tvInboxMessage = itemView.findViewById(R.id.tvInboxMessage);
            tvInboxTime = itemView.findViewById(R.id.tvInboxTime);
            inboxCV = itemView.findViewById(R.id.inboxCV);
            inboxuseronlineCV = itemView.findViewById(R.id.inboxuseronlineCV);
            tvUserOnlineIndicator1 = itemView.findViewById(R.id.tvUserOnlineIndicator1);
            tvUserOnlineIndicator2 = itemView.findViewById(R.id.tvUserOnlineIndicator2);

        }
    }
}

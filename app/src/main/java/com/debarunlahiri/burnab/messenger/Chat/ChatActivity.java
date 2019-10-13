package com.debarunlahiri.burnab.messenger.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.MainActivity;
import com.debarunlahiri.burnab.messenger.OverallProfileActivity;
import com.debarunlahiri.burnab.messenger.ProfileActivity;
import com.debarunlahiri.burnab.messenger.R;
import com.debarunlahiri.burnab.messenger.Utils.TimeAgo;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private EditText etChat;
    private Button chatsendbutton;
    private CircleImageView chatuserprofileCIV;
    private TextView tvChatUserName, tvReplyUsername, tvReplyMessage, tvChatUserIndicator1;
    private RelativeTimeTextView tvChatUserIndicator;
    private CardView chatheadprofileCV;
    private ImageButton chatbackIB, replymessagecloseIB;
    private CardView messaggereplyCV;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private RecyclerView chatRV;
    private ChatAdapter chatAdapter;
    private Context mContext;
    private LinearLayoutManager linearLayoutManager;
    private List<Chat> chatList = new ArrayList<>();

    private String searched_user_id;
    private String user_id;
    public boolean isOnline = false;

    private String searched_user_name;
    private String searched_profile_image;
    private String reply_chat_id, reply_message, reply_sender_user_id;
    private int reply_chat_position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mContext = ChatActivity.this;

        Bundle bundle = getIntent().getExtras();
        searched_user_id = bundle.get("searched_user_id").toString();

        etChat = findViewById(R.id.etChat);
        chatsendbutton = findViewById(R.id.chatsendbutton);
        chatuserprofileCIV = findViewById(R.id.chatuserprofileCIV);
        tvChatUserName = findViewById(R.id.tvChatUserName);
        tvChatUserIndicator = findViewById(R.id.tvChatUserIndicator);
        chatheadprofileCV = findViewById(R.id.chatheadprofileCV);
        chatbackIB = findViewById(R.id.chatbackIB);
        messaggereplyCV = findViewById(R.id.messaggereplyCV);
        tvReplyUsername = findViewById(R.id.tvReplyUsername);
        tvReplyMessage = findViewById(R.id.tvReplyMessage);
        replymessagecloseIB = findViewById(R.id.replymessagecloseIB);
        tvChatUserIndicator1 = findViewById(R.id.tvChatUserIndicator1);

        chatRV = findViewById(R.id.chatRV);
        chatAdapter = new ChatAdapter(chatList, mContext);
        linearLayoutManager = new LinearLayoutManager(mContext);
        chatRV.setLayoutManager(linearLayoutManager);
        chatRV.setAdapter(chatAdapter);
        //linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();
        messaggereplyCV.setBackgroundResource(R.drawable.chat_et_bg);
        messaggereplyCV.setVisibility(View.GONE);

        getSearchedUserDetails();
        checkSearchedUserIdOnlineOrNot();
        fetchMessages();

        chatbackIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        chatsendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etChat.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(mContext, "Cannot send empty message", Toast.LENGTH_LONG).show();
                } else {
                    etChat.setText("");
                    if (reply_chat_id == null) {
                        sendMessage(message);
                    } else {
                        replyMessage(message);
                    }

                }

            }
        });

        tvChatUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(mContext, OverallProfileActivity.class);
                profileIntent.putExtra("searched_user_id", searched_user_id);
                startActivity(profileIntent);
            }
        });

        chatuserprofileCIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(mContext, OverallProfileActivity.class);
                profileIntent.putExtra("searched_user_id", searched_user_id);
                startActivity(profileIntent);
            }
        });

        tvChatUserIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(mContext, OverallProfileActivity.class);
                profileIntent.putExtra("searched_user_id", searched_user_id);
                startActivity(profileIntent);
            }
        });

        etChat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.hasFocus()) {
                    etChat.setBackgroundResource(R.drawable.chat_et_focus_bg);
                } else {
                    etChat.setBackgroundResource(R.drawable.chat_et_bg);
                }
            }
        });

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver, new IntentFilter("reply_message"));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver, new IntentFilter("reply_message_position"));

        replymessagecloseIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reply_chat_id = null;
                messaggereplyCV.setVisibility(View.GONE);
            }
        });


    }

    private void replyMessage(String message) {
        if (reply_chat_id!= null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            String formattedDate = sdf.format(new Date());
            String chat_id = mDatabase.child("chats").push().getKey();
            HashMap<String, Object> mReplyMessageDataMap = new HashMap<>();
            mReplyMessageDataMap.put("message", message.trim());
            mReplyMessageDataMap.put("sender_user_id", user_id);
            mReplyMessageDataMap.put("receiver_user_id", searched_user_id);
            mReplyMessageDataMap.put("timestamp", System.currentTimeMillis());
            mReplyMessageDataMap.put("has_seen", isOnline);
            mReplyMessageDataMap.put("formatted_date", formattedDate);
            mReplyMessageDataMap.put("chat_id", chat_id);
            mReplyMessageDataMap.put("reply_chat_id", reply_chat_id);
            mDatabase.child("chats").child(user_id).child(searched_user_id).child(chat_id).setValue(mReplyMessageDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mDatabase.child("chats").child(searched_user_id).child(user_id).child(chat_id).setValue(mReplyMessageDataMap);
                        mDatabase.child("last_message").child(user_id).child(searched_user_id).child(chat_id).setValue(mReplyMessageDataMap);
                        mDatabase.child("last_message").child(searched_user_id).child(user_id).child(chat_id).setValue(mReplyMessageDataMap);
                        messaggereplyCV.setVisibility(View.GONE);
                        reply_chat_id = null;
                    } else {
                        Toast.makeText(mContext, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mContext, "Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(mContext, "Error sending message", Toast.LENGTH_LONG).show();
        }
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            reply_chat_id = intent.getStringExtra("reply_chat_id");
            if (intent.getStringExtra("reply_chat_id_position") != null) {
                reply_chat_position = Integer.parseInt(intent.getStringExtra("reply_chat_id_position"));
                Toast.makeText(mContext, String.valueOf(reply_chat_position), Toast.LENGTH_LONG).show();
                chatRV.smoothScrollToPosition(reply_chat_position);
            }

            if (reply_chat_id != null) {
                mDatabase.child("chats").child(user_id).child(searched_user_id).child(reply_chat_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reply_message = dataSnapshot.child("message").getValue().toString();
                        reply_sender_user_id  = dataSnapshot.child("sender_user_id").getValue().toString();

                        if (reply_sender_user_id.equals(user_id)) {
                            tvReplyUsername.setText("You");
                        } else {
                            mDatabase.child("users").child(reply_sender_user_id).child("user_data").child("name").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String reply_name = dataSnapshot.getValue().toString();
                                    tvReplyUsername.setText(reply_name);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        tvReplyMessage.setText(reply_message);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                messaggereplyCV.setVisibility(View.VISIBLE);

            }
        }
    };

    private void fetchMessages() {
        mDatabase.child("chats").child(user_id).child(searched_user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                chatList.add(chat);
                chatAdapter.notifyDataSetChanged();
                chatRV.smoothScrollToPosition(chatAdapter.getItemCount()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String chat_key = dataSnapshot.getKey();


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getSearchedUserDetails() {
        mDatabase.child("users").child(searched_user_id).child("user_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    searched_profile_image = dataSnapshot.child("profile_image").getValue().toString();
                    searched_user_name = dataSnapshot.child("name").getValue().toString();
                    Glide.with(mContext).load(searched_profile_image).thumbnail(0.1f).into(chatuserprofileCIV);
                    tvChatUserName.setText(searched_user_name);
                } else {
                    Glide.with(mContext).load(R.drawable.default_profile_pic).into(chatuserprofileCIV);
                    tvChatUserName.setText("Unknown User");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkSearchedUserIdOnlineOrNot() {
        mDatabase.child("active").child(searched_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    isOnline = (boolean) dataSnapshot.child("isOnline").getValue();
                    long timestamp = (long) dataSnapshot.child("timestamp").getValue();
                    String formatted_date = dataSnapshot.child("formatted_date").getValue().toString();

                    if (isOnline == false) {
                        isOnline = false;
                        tvChatUserIndicator1.setText("Last active:");
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        Date date = null;
                        try {
                            date = inputFormat.parse(formatted_date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String niceDateStr = (String) DateUtils.getRelativeTimeSpanString(date.getTime() , Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS);
                        tvChatUserIndicator.setText(niceDateStr);
                    } else {
                        isOnline = true;
                        tvChatUserIndicator1.setText("Active now");
                        tvChatUserIndicator.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        String formattedDate = sdf.format(new Date());
        String chat_id = mDatabase.child("chats").push().getKey();
        HashMap<String, Object> mChatDataMap = new HashMap<>();
        mChatDataMap.put("message", message.trim());
        mChatDataMap.put("sender_user_id", user_id);
        mChatDataMap.put("receiver_user_id", searched_user_id);
        mChatDataMap.put("timestamp", System.currentTimeMillis());
        mChatDataMap.put("has_seen", isOnline);
        mChatDataMap.put("formatted_date", formattedDate);
        mChatDataMap.put("chat_id", chat_id);
        mDatabase.child("chats").child(user_id).child(searched_user_id).child(chat_id).setValue(mChatDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mDatabase.child("chats").child(searched_user_id).child(user_id).child(chat_id).setValue(mChatDataMap);
                    mDatabase.child("last_message").child(user_id).child(searched_user_id).child(chat_id).setValue(mChatDataMap);
                    mDatabase.child("last_message").child(searched_user_id).child(user_id).child(chat_id).setValue(mChatDataMap);
                } else {
                    Toast.makeText(mContext, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStop() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        String formattedDate = sdf.format(new Date());
        HashMap<String, Object> mActiveDataMap = new HashMap<>();
        mActiveDataMap.put("user_id", user_id);
        mActiveDataMap.put("timestamp", System.currentTimeMillis());
        mActiveDataMap.put("isOnline", false);
        mActiveDataMap.put("formatted_date", formattedDate);
        mDatabase.child("active").child(user_id).setValue(mActiveDataMap);
        super.onStop();
    }

    @Override
    protected void onResume() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        String formattedDate = sdf.format(new Date());
        HashMap<String, Object> mActiveDataMap = new HashMap<>();
        mActiveDataMap.put("user_id", user_id);
        mActiveDataMap.put("timestamp", System.currentTimeMillis());
        mActiveDataMap.put("isOnline", true);
        mActiveDataMap.put("formatted_date", formattedDate);
        mDatabase.child("active").child(user_id).setValue(mActiveDataMap);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        String formattedDate = sdf.format(new Date());
        HashMap<String, Object> mActiveDataMap = new HashMap<>();
        mActiveDataMap.put("user_id", user_id);
        mActiveDataMap.put("timestamp", System.currentTimeMillis());
        mActiveDataMap.put("isOnline", false);
        mActiveDataMap.put("formatted_date", formattedDate);
        mDatabase.child("active").child(user_id).setValue(mActiveDataMap);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        String formattedDate = sdf.format(new Date());
        HashMap<String, Object> mActiveDataMap = new HashMap<>();
        mActiveDataMap.put("user_id", user_id);
        mActiveDataMap.put("timestamp", System.currentTimeMillis());
        mActiveDataMap.put("isOnline", true);
        mActiveDataMap.put("formatted_date", formattedDate);
        mDatabase.child("active").child(user_id).setValue(mActiveDataMap);
        super.onStart();
    }
}

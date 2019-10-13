package com.debarunlahiri.burnab.messenger.Group;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class GroupActivity extends AppCompatActivity {

    private Toolbar grouptoolbar;

    private CircleImageView groupProfileCIV, groupUserProfileCIV;
    private ImageView groupCoverIV, groupdialogaddpostIV;
    private TextView tvGroupName, tvGroupDesc, tvGroupDialogAddImage, tvGroupShowMessage, tvGroupPostPosting, tvCountGroupMembers, tvCountGroupMemberPosts;
    private Button groupjoinbutton, groupchatbutton;
    private EditText etGroupAddPost;
    private CardView groupdialogpostingCV, groupaddpostCV, groupaddpostmainCV, groupshowpostsCV;
    private NestedScrollView groupNSW;
    private ProgressBar groupPB;

    private RecyclerView groupRV;
    private GroupPostsAdapter groupPostsAdapter;
    private List<GroupPosts> groupPostsList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String group_id;
    private String group_cover_image, group_profile_image, group_name, group_desc;
    private String user_id;
    private String profile_image;

    private Context mContext;
    private Uri postImageUri = null;
    private Bitmap mCompressedGroupProfileImage, mCompressedGroupCoverImage;

    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_group);

        mContext = GroupActivity.this;

        Bundle bundle = getIntent().getExtras();
        group_id = bundle.get("group_id").toString();

        grouptoolbar = findViewById(R.id.grouptoolbar);
        grouptoolbar.setSubtitle("");
        setSupportActionBar(grouptoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        grouptoolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.black_back));
        grouptoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        groupProfileCIV = findViewById(R.id.groupProfileCIV);
        groupCoverIV = findViewById(R.id.groupCoverIV);
        tvGroupName = findViewById(R.id.tvGroupName);
        tvGroupDesc = findViewById(R.id.tvGroupDesc);
        groupjoinbutton = findViewById(R.id.groupjoinbutton);
        groupchatbutton = findViewById(R.id.groupchatbutton);
        groupUserProfileCIV = findViewById(R.id.groupUserProfileCIV);
        etGroupAddPost = findViewById(R.id.etGroupAddPost);
        tvGroupShowMessage = findViewById(R.id.tvGroupShowMessage);
        groupaddpostmainCV = findViewById(R.id.groupaddpostmainCV);
        groupshowpostsCV = findViewById(R.id.groupshowpostsCV);
        groupPB = findViewById(R.id.groupPB);
        groupNSW = findViewById(R.id.groupNSW);
        tvCountGroupMembers = findViewById(R.id.tvCountGroupMembers);
        tvCountGroupMemberPosts = findViewById(R.id.tvCountGroupMemberPosts);

        groupRV = findViewById(R.id.groupRV);
        groupPostsAdapter = new GroupPostsAdapter(groupPostsList, mContext);
        linearLayoutManager = new LinearLayoutManager(mContext);
        groupRV.setLayoutManager(linearLayoutManager);
        groupRV.setAdapter(groupPostsAdapter);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        //groupchatbutton.setVisibility(View.GONE);
        etGroupAddPost.setFocusable(false);
        groupProfileCIV.setElevation(5);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();
        groupPB.setVisibility(View.VISIBLE);
        groupNSW.setVisibility(View.GONE);
        checkUserJoinedGroupOrNot();
        fetchGroupDetails();
        fetchUserDetails();
        fetchPosts();
        countGroupMembers();
        countGroupMembersPosts();

        etGroupAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(GroupActivity.this);
                dialog.setContentView(R.layout.group_add_post_dialog_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ImageButton dialogproupcloseIB = dialog.findViewById(R.id.dialogproupcloseIB);
                EditText etGroupDialogAddPostBody = dialog.findViewById(R.id.etGroupDialogAddPostBody);
                Button groupdialogaddpostbutton = dialog.findViewById(R.id.groupdialogaddpostbutton);
                tvGroupDialogAddImage = dialog.findViewById(R.id.tvGroupDialogAddImage);
                groupdialogaddpostIV = dialog.findViewById(R.id.groupdialogaddpostIV);
                groupaddpostCV = dialog.findViewById(R.id.groupaddpostmainCV);
                groupdialogpostingCV = dialog.findViewById(R.id.groupdialogpostingCV);
                tvGroupPostPosting = dialog.findViewById(R.id.tvGroupPostPosting);

                groupdialogaddpostIV.setVisibility(View.GONE);
                groupdialogpostingCV.setVisibility(View.GONE);

                tvGroupDialogAddImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dexter.withActivity(GroupActivity.this)
                                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {
                                        CropImage.activity()
                                                .setGuidelines(CropImageView.Guidelines.ON)
                                                .setMinCropResultSize(512, 512)
                                                .start(GroupActivity.this);
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {
                                        Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();

                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                        token.continuePermissionRequest();
                                    }
                                })
                                .check();
                    }
                });
                
                groupdialogaddpostbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String body = etGroupDialogAddPostBody.getText().toString();
                        if (postImageUri == null) {
                            if (body.isEmpty()) {
                                etGroupDialogAddPostBody.setError("Cannot post empty");
                            } else {
                                uploadPost(body);
                            }
                        } else {
                            uploadImagePost(body);
                        }
                    }
                });
                
                dialogproupcloseIB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
            }
        });

        groupchatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(GroupActivity.this, GroupChatActivity.class);
                chatIntent.putExtra("group_id", group_id);
                startActivity(chatIntent);
            }
        });

        tvCountGroupMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent membersIntent = new Intent(GroupActivity.this, GroupMembersActivtiy.class);
                membersIntent.putExtra("group_id", group_id);
                startActivity(membersIntent);
            }
        });

        groupjoinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupjoinbutton.getText().equals("Joined")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Leave Group?");
                    builder.setMessage("Are you sure you want to leave " + group_name + "?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mDatabase.child("groups").child(group_id).child("members").child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //Toast.makeText(mContext, "You have left the group", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(mContext, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else if (groupjoinbutton.getText().equals("+Join")){
                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    String formattedDate = sdf.format(new Date());
                    HashMap<String, Object> mGroupJoinDataMap = new HashMap<>();
                    mGroupJoinDataMap.put("user_id", user_id);
                    mGroupJoinDataMap.put("timestamp", System.currentTimeMillis());
                    mGroupJoinDataMap.put("formatted_date", formattedDate);
                    mGroupJoinDataMap.put("chat_color_code", String.valueOf(color));
                    mDatabase.child("groups").child(group_id).child("members").child(user_id).setValue(mGroupJoinDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(mContext, "You have successfully joined the group", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(mContext, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void countGroupMembersPosts() {
        mDatabase.child("groups").child(group_id).child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int group_member_post_count = (int) dataSnapshot.getChildrenCount();

                    if (group_member_post_count == 1) {
                        tvCountGroupMemberPosts.setText(group_member_post_count + " member post");
                    } else {
                        tvCountGroupMemberPosts.setText(group_member_post_count + " member posts");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void countGroupMembers() {
        mDatabase.child("groups").child(group_id).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int group_members_count = (int) dataSnapshot.getChildrenCount();

                    if (group_members_count == 1) {
                        tvCountGroupMembers.setText(group_members_count + " group member");
                    } else {
                        tvCountGroupMembers.setText(group_members_count + " group members");
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserJoinedGroupOrNot() {
        mDatabase.child("groups").child(group_id).child("members").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    groupjoinbutton.setText("Joined");
                    groupchatbutton.setVisibility(View.VISIBLE);
                    groupaddpostmainCV.setVisibility(View.VISIBLE);
                    groupshowpostsCV.setVisibility(View.VISIBLE);
                } else {
                    groupjoinbutton.setText("+Join");
                    groupchatbutton.setVisibility(View.GONE);
                    groupaddpostmainCV.setVisibility(View.GONE);
                    groupshowpostsCV.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchPosts() {
        mDatabase.child("groups").child(group_id).child("posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    tvGroupShowMessage.setVisibility(View.GONE);
                    GroupPosts groupPosts = dataSnapshot.getValue(GroupPosts.class);
                    groupPostsList.add(groupPosts);
                    groupPostsAdapter.notifyDataSetChanged();
                } else {
                    tvGroupShowMessage.setVisibility(View.VISIBLE);
                }
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

    private void uploadImagePost(String body) {
        groupaddpostCV.setVisibility(View.GONE);
        groupdialogpostingCV.setVisibility(View.VISIBLE);
        File mFileGroupProfileImage = new File(postImageUri.getPath());

        final Long ts_long = System.currentTimeMillis()/1000;
        final String ts = ts_long.toString();
        //final StorageReference childRef = storageReference.child("users/profiles/profile_images/" + currentUser.getUid() + ".jpg");
        //final StorageReference thumb_childRef = storageReference.child("users/profile_images/profile_images/" + currentUser.getUid() + ".jpg");

        try {
            mCompressedGroupProfileImage = new Compressor(GroupActivity.this).setQuality(8).compressToBitmap(mFileGroupProfileImage);
        } catch (IOException e) {
            e.printStackTrace();
        }


        ByteArrayOutputStream mProfileBAOS = new ByteArrayOutputStream();
        mCompressedGroupProfileImage.compress(Bitmap.CompressFormat.JPEG, 25, mProfileBAOS);
        byte[] mProfileThumbData = mProfileBAOS.toByteArray();

        final StorageReference mThumbChildRefProfile = storageReference.child("group/profiles/profile_images/" + currentUser.getUid() + "/thumb/" + ts + ".jpg");

        final UploadTask profile_thumb_uploadTask = mThumbChildRefProfile.putBytes(mProfileThumbData);

        profile_thumb_uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Task<Uri> thumb_uriTask = profile_thumb_uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mThumbChildRefProfile.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri profile_thumb_downloadUri = task.getResult();
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    String post_id = mDatabase.child("groups").child(group_id).push().getKey();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                                    String formattedDate = sdf.format(new Date());
                                    HashMap<String, Object> mPostDataMap = new HashMap<>();
                                    mPostDataMap.put("body", body.trim());
                                    mPostDataMap.put("group_id", group_id);
                                    mPostDataMap.put("post_id", post_id);
                                    mPostDataMap.put("formatted_date", formattedDate);
                                    mPostDataMap.put("timestamp", System.currentTimeMillis());
                                    mPostDataMap.put("user_id", user_id);
                                    mPostDataMap.put("post_image", downloadUri.toString());
                                    mDatabase.child("groups").child(group_id).child("posts").child(post_id).setValue(mPostDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                dialog.cancel();
                                                Toast.makeText(getApplicationContext(), "Post added successfully", Toast.LENGTH_LONG).show();
                                            } else {
                                                groupaddpostCV.setVisibility(View.VISIBLE);
                                                groupdialogpostingCV.setVisibility(View.GONE);
                                                Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    // Handle failures
                                    // ...
                                    groupaddpostCV.setVisibility(View.VISIBLE);
                                    groupdialogpostingCV.setVisibility(View.GONE);
                                    String errMsg = task.getException().getMessage();
                                    Toast.makeText(getApplicationContext(), "Download Uri Error: " + errMsg, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                }

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                tvGroupPostPosting.setText("Posting... " + String.valueOf(progress) + "%");
            }
        });
    }


    private void uploadPost(String body) {
        String post_id = mDatabase.child("groups").child(group_id).push().getKey();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        String formattedDate = sdf.format(new Date());
        HashMap<String, Object> mPostDataMap = new HashMap<>();
        mPostDataMap.put("body", body.trim());
        mPostDataMap.put("group_id", group_id);
        mPostDataMap.put("post_id", post_id);
        mPostDataMap.put("formatted_date", formattedDate);
        mPostDataMap.put("timestamp", System.currentTimeMillis());
        mPostDataMap.put("user_id", user_id);
        mDatabase.child("groups").child(group_id).child("posts").child(post_id).setValue(mPostDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(), "Post added successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void fetchUserDetails() {
        mDatabase.child("users").child(user_id).child("user_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profile_image = dataSnapshot.child("profile_image").getValue().toString();
                Glide.with(getApplicationContext()).load(profile_image).into(groupUserProfileCIV);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    String getMyData() {
        return group_id;
    }

    private void fetchGroupDetails() {
        mDatabase.child("groups").child(group_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                group_cover_image = dataSnapshot.child("group_cover_image").getValue().toString();
                group_name = dataSnapshot.child("group_name").getValue().toString();
                group_profile_image = dataSnapshot.child("group_profile_image").getValue().toString();
                group_desc = dataSnapshot.child("group_desc").getValue().toString();
                Glide.with(getApplicationContext()).load(group_cover_image).into(groupCoverIV);
                Glide.with(getApplicationContext()).load(group_profile_image).into(groupProfileCIV);
                tvGroupName.setText(group_name);
                tvGroupDesc.setText(group_desc);
                grouptoolbar.setTitle(group_name);

                groupPB.setVisibility(View.GONE);
                groupNSW.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            //filePath = data.getData();

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                postImageUri = result.getUri();
                if (postImageUri != null) {
                    groupdialogaddpostIV.setVisibility(View.VISIBLE);
                    tvGroupDialogAddImage.setText("Change photo");
                    Glide.with(getApplicationContext()).load(postImageUri).into(groupdialogaddpostIV);
                } else {
                    groupdialogaddpostIV.setVisibility(View.GONE);
                }
                //Toast.makeText(getApplicationContext(), (CharSequence) postImageUri, Toast.LENGTH_LONG).show();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(getApplicationContext(), (CharSequence) error, Toast.LENGTH_LONG).show();
            }



            //Setting image to ImageView



        }
    }


}

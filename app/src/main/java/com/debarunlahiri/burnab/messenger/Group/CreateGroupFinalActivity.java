package com.debarunlahiri.burnab.messenger.Group;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.debarunlahiri.burnab.messenger.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

public class CreateGroupFinalActivity extends AppCompatActivity {

    private ProgressBar creategroupfinalPB;
    private TextView tvCreateGroupFinal;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private Uri group_profile_image_URI = null;
    private Uri group_cover_photo_URI = null;

    private String group_name, group_desc;
    private String group_profile_image, group_cover_photo;

    private Bitmap mCompressedGroupProfileImage, mCompressedGroupCoverImage;

    private String user_id;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_final);

        Bundle bundle = getIntent().getExtras();
        group_profile_image_URI = Uri.parse(bundle.get("group_profile_image").toString());
        group_cover_photo_URI = Uri.parse(bundle.get("group_cover_image").toString());
        group_name = bundle.get("group_name").toString();
        group_desc = bundle.get("group_desc").toString();

        tvCreateGroupFinal = findViewById(R.id.tvCreateGroupFinal);
        creategroupfinalPB = findViewById(R.id.creategroupfinalPB);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        File mFileGroupProfileImage = new File(group_profile_image_URI.getPath());
        File mFileGroupCoverImage = new File(group_cover_photo_URI.getPath());

        final Long ts_long = System.currentTimeMillis()/1000;
        final String ts = ts_long.toString();
        //final StorageReference childRef = storageReference.child("users/profiles/profile_images/" + currentUser.getUid() + ".jpg");
        //final StorageReference thumb_childRef = storageReference.child("users/profile_images/profile_images/" + currentUser.getUid() + ".jpg");

        try {
            mCompressedGroupProfileImage = new Compressor(CreateGroupFinalActivity.this).setQuality(8).compressToBitmap(mFileGroupProfileImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mCompressedGroupCoverImage = new Compressor(CreateGroupFinalActivity.this).setQuality(8).compressToBitmap(mFileGroupCoverImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream mProfileBAOS = new ByteArrayOutputStream();
        mCompressedGroupProfileImage.compress(Bitmap.CompressFormat.JPEG, 25, mProfileBAOS);
        byte[] mProfileThumbData = mProfileBAOS.toByteArray();

        ByteArrayOutputStream mCoverBAOS = new ByteArrayOutputStream();
        mCompressedGroupCoverImage.compress(Bitmap.CompressFormat.JPEG, 50, mCoverBAOS);
        byte[] mCoverThumbData = mCoverBAOS.toByteArray();

        final StorageReference mThumbChildRefProfile = storageReference.child("group/profiles/profile_images/" + currentUser.getUid() + "/thumb/" + ts + ".jpg");
        final StorageReference mThumbChildRefCover = storageReference.child("group/profiles/cover_images/" + currentUser.getUid() + "/thumb/" + ts + ".jpg");

        final UploadTask profile_thumb_uploadTask = mThumbChildRefProfile.putBytes(mProfileThumbData);
        final UploadTask cover_thumb_uploadTask = mThumbChildRefCover.putBytes(mCoverThumbData);

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

                                cover_thumb_uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Task<Uri> urlTask = cover_thumb_uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                @Override
                                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                    if (!task.isSuccessful()) {
                                                        throw task.getException();
                                                    }

                                                    // Continue with the task to get the download URL
                                                    return mThumbChildRefCover.getDownloadUrl();
                                                }
                                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if (task.isSuccessful()) {
                                                        Uri downloadUri = task.getResult();
                                                        final String mUri = downloadUri.toString();
                                                        final String group_id = mDatabase.child("groups").push().getKey();
                                                        final HashMap<String, Object> dataMap = new HashMap<>();
                                                        dataMap.put("group_name", group_name);
                                                        dataMap.put("group_desc", group_desc);
                                                        dataMap.put("group_cover_image", mUri);
                                                        dataMap.put("group_profile_image", profile_thumb_downloadUri.toString());
                                                        dataMap.put("group_admin_user_id", user_id);
                                                        dataMap.put("group_id", group_id);
                                                        mDatabase.child("groups").child(group_id).setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getApplicationContext(), "Group created suucessfully", Toast.LENGTH_LONG).show();
                                                                    tvCreateGroupFinal.setText("Redirecting...");
                                                                    new Handler().postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Intent groupIntent = new Intent(CreateGroupFinalActivity.this, GroupActivity.class);
                                                                            groupIntent.putExtra("group_admin_user_id", user_id);
                                                                            groupIntent.putExtra("group_id", group_id);
                                                                            groupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                            startActivity(groupIntent);
                                                                            finish();
                                                                        }
                                                                    }, 1500);

                                                                } else {
                                                                    String errMsg = task.getException().getMessage();
                                                                    Toast.makeText(getApplicationContext(), "Error: " + errMsg, Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        // Handle failures
                                                        // ...
                                                        String errMsg = task.getException().getMessage();
                                                        Toast.makeText(getApplicationContext(), "Download Uri Error: " + errMsg, Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });

                                        } else {
                                            String errMsg = task.getException().getMessage();
                                            Toast.makeText(getApplicationContext(), "Error: " + errMsg, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Upload Failed: " + e, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Cannot go back. Group creating in process.", Toast.LENGTH_LONG).show();
    }
}

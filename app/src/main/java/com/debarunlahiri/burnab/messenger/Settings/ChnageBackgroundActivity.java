package com.debarunlahiri.burnab.messenger.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.AddStoryActivity;
import com.debarunlahiri.burnab.messenger.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import id.zelory.compressor.Compressor;

public class ChnageBackgroundActivity extends AppCompatActivity {

    private Toolbar changebackgroundtoolbar;

    private ImageView changebackgroundIV;
    private Button changebackgroundsavebutton;
    private TextView tvChangeBackgroundAddImage;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;

    private Context mContext;
    private Uri postImageUri = null;
    private Bitmap mCompressedStoryImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chnage_background);

        mContext = ChnageBackgroundActivity.this;

        changebackgroundtoolbar = findViewById(R.id.changebackgroundtoolbar);
        changebackgroundtoolbar.setTitle("Change background image");
        setSupportActionBar(changebackgroundtoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        changebackgroundtoolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.black_back));
        changebackgroundtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        changebackgroundsavebutton = findViewById(R.id.changebackgroundsavebutton);
        changebackgroundIV = findViewById(R.id.changebackgroundIV);
        tvChangeBackgroundAddImage = findViewById(R.id.tvChangeBackgroundAddImage);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        fetchUserBackgroundImage();



        changebackgroundIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(ChnageBackgroundActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setMinCropResultSize(512, 512)
                                        .start(ChnageBackgroundActivity.this);
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

        changebackgroundsavebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postImageUri == null) {
                    Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_LONG).show();
                } else {
                    changeProfileBackground();
                }
            }
        });
    }

    private void fetchUserBackgroundImage() {
        mDatabase.child("users").child(user_id).child("user_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("profile_background_image").exists()) {
                    tvChangeBackgroundAddImage.setVisibility(View.GONE);
                    String profile_background_image = dataSnapshot.child("profile_background_image").getValue().toString();
                    Glide.with(mContext).load(profile_background_image).into(changebackgroundIV);
                } else {
                    tvChangeBackgroundAddImage.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void changeProfileBackground() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Changing background...");
        progressDialog.show();
        File mFileGroupProfileImage = new File(postImageUri.getPath());

        final Long ts_long = System.currentTimeMillis() / 1000;
        final String ts = ts_long.toString();
        //final StorageReference childRef = storageReference.child("users/profiles/profile_images/" + currentUser.getUid() + ".jpg");
        //final StorageReference thumb_childRef = storageReference.child("users/profile_images/profile_images/" + currentUser.getUid() + ".jpg");

        try {
            mCompressedStoryImage = new Compressor(ChnageBackgroundActivity.this).setQuality(8).compressToBitmap(mFileGroupProfileImage);
        } catch (IOException e) {
            e.printStackTrace();
        }


        ByteArrayOutputStream mProfileBAOS = new ByteArrayOutputStream();
        mCompressedStoryImage.compress(Bitmap.CompressFormat.JPEG, 25, mProfileBAOS);
        byte[] mProfileThumbData = mProfileBAOS.toByteArray();

        final StorageReference mThumbChildRefProfile = storageReference.child("background_profile_image/" + currentUser.getUid() + "/thumb/" + ts + ".jpg");

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
                                    String story_id = mDatabase.child("stories").child(user_id).push().getKey();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                                    String formattedDate = sdf.format(new Date());
                                    HashMap<String, Object> mPostDataMap = new HashMap<>();
                                    mPostDataMap.put("profile_background_image", downloadUri.toString());
                                    mDatabase.child("users").child(user_id).child("user_data").child("profile_background_image").setValue(downloadUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Profile background changed successfully", Toast.LENGTH_LONG).show();
                                                onBackPressed();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    // Handle failures
                                    // ...
                                    progressDialog.dismiss();
                                    String errMsg = task.getException().getMessage();
                                    Toast.makeText(getApplicationContext(), "Download Uri Error: " + errMsg, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                }

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

                Glide.with(getApplicationContext()).load(postImageUri).into(changebackgroundIV);

                //Toast.makeText(getApplicationContext(), (CharSequence) postImageUri, Toast.LENGTH_LONG).show();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(getApplicationContext(), (CharSequence) error, Toast.LENGTH_LONG).show();
            }



            //Setting image to ImageView



        }
    }
}

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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.R;
import com.debarunlahiri.burnab.messenger.SetupUser.SetupUser2Activity;
import com.debarunlahiri.burnab.messenger.SetupUser.SetupUser3Activity;
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
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsChangeProfilePictureActivity extends AppCompatActivity {

    private Toolbar settingschangeprofilepictoolbar;

    private CircleImageView profileCIV;
    private TextView tvChangeProfilePic;
    private Button changeprofilepicbutton;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;
    private Context mContext;
    private Uri profilePictureURI = null;
    private Uri profileImageUri = null;
    private Bitmap mCompressedProfileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_change_profile_picture);

        mContext = SettingsChangeProfilePictureActivity.this;

        settingschangeprofilepictoolbar = findViewById(R.id.settingschangeprofilepictoolbar);
        setSupportActionBar(settingschangeprofilepictoolbar);
        settingschangeprofilepictoolbar.setTitle("Change profile pic");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        settingschangeprofilepictoolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.black_back));
        settingschangeprofilepictoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        profileCIV = findViewById(R.id.profileCIV);
        tvChangeProfilePic = findViewById(R.id.tvChangeProfilePic);
        changeprofilepicbutton = findViewById(R.id.changeprofilepicbutton);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        mDatabase.child("users").child(user_id).child("user_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profle_image = dataSnapshot.child("profile_image").getValue().toString();
                Glide.with(mContext).load(profle_image).thumbnail(0.1f).into(profileCIV);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tvChangeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(SettingsChangeProfilePictureActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setMinCropResultSize(512, 512)
                                        .setAspectRatio(1, 1)
                                        .start(SettingsChangeProfilePictureActivity.this);
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

        profileCIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(SettingsChangeProfilePictureActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setMinCropResultSize(512, 512)
                                        .setAspectRatio(1, 1)
                                        .start(SettingsChangeProfilePictureActivity.this);
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

        changeprofilepicbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadProfilePic();
            }
        });
    }

    private void uploadProfilePic() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final Long ts_long = System.currentTimeMillis() / 1000;
        final String ts = ts_long.toString();
        File mFileProfileImage = new File(profilePictureURI.getPath());
        //final StorageReference childRef = storageReference.child("users/profiles/profile_images/" + currentUser.getUid() + ".jpg");
        //final StorageReference thumb_childRef = storageReference.child("users/profile_images/profile_images/" + currentUser.getUid() + ".jpg");

        try {
            mCompressedProfileImage = new Compressor(SettingsChangeProfilePictureActivity.this).setQuality(15).compressToBitmap(mFileProfileImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream mProfileBAOS = new ByteArrayOutputStream();
        mCompressedProfileImage.compress(Bitmap.CompressFormat.JPEG, 15, mProfileBAOS);
        byte[] mProfileData = mProfileBAOS.toByteArray();

        final StorageReference mChildRefProfile = storageReference.child("users/profiles/profile_images/" + currentUser.getUid() + ".jpg");
        //final StorageReference mThumbChildRefProfile = storageReference.child("users/profiles/profile_images/thumbs/" + currentUser.getUid() + ".jpg");

        final UploadTask profile_uploadTask = mChildRefProfile.putBytes(mProfileData);

        profile_uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Task<Uri> uriTask = profile_uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mChildRefProfile.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                //final String product_key = mDatabase.child("products").child(item).push().getKey();
                                final HashMap<String, Object> dataMap = new HashMap<>();
                                dataMap.put("profile_image", downloadUri.toString());
                                dataMap.put("thumb_profile_image", downloadUri.toString());
                                mDatabase.child("users").child(currentUser.getUid()).child("user_data").updateChildren(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Toast.makeText(mContext, "Picture changed successfully", Toast.LENGTH_LONG).show();
                                            onBackPressed();
                                        } else {
                                            progressDialog.dismiss();
                                            String errMsg = task.getException().getMessage();
                                            Toast.makeText(getApplicationContext(), "Error: " + errMsg, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Image Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
                profilePictureURI = result.getUri();

                Glide.with(getApplicationContext()).load(profilePictureURI).into(profileCIV);

                //Toast.makeText(getApplicationContext(), (CharSequence) postImageUri, Toast.LENGTH_LONG).show();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(getApplicationContext(), (CharSequence) error, Toast.LENGTH_LONG).show();
            }



            //Setting image to ImageView



        }
    }
}

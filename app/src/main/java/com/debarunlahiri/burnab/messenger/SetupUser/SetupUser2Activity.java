package com.debarunlahiri.burnab.messenger.SetupUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.AddStoryActivity;
import com.debarunlahiri.burnab.messenger.MainActivity;
import com.debarunlahiri.burnab.messenger.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SetupUser2Activity extends AppCompatActivity {

    private CircleImageView setupuser2circleImageView;
    private Button setupuser2nextbutton;
    private ImageButton setuser2backIB;
    private TextView tvSetupUser2AddImage;

    private Uri profilePictureURI = null;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;
    private Uri profileImageUri = null;
    private Bitmap mCompressedProfileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_user2);

        setupuser2circleImageView = findViewById(R.id.setupuser2circleImageView);
        setupuser2nextbutton = findViewById(R.id.setupuser2nextbutton);
        setuser2backIB = findViewById(R.id.setuser2backIB);
        tvSetupUser2AddImage = findViewById(R.id.tvSetupUser2AddImage);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        setuser2backIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        setupuser2circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(SetupUser2Activity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setMinCropResultSize(512, 512)
                                        .setAspectRatio(1, 1)
                                        .start(SetupUser2Activity.this);
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

        tvSetupUser2AddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(SetupUser2Activity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setMinCropResultSize(512, 512)
                                        .setAspectRatio(1, 1)
                                        .start(SetupUser2Activity.this);
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

        setupuser2nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadProfilePic();
            }
        });

    }

    private void uploadProfilePic() {
        if (profilePictureURI == null) {
            Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_LONG).show();
        } else {
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
                mCompressedProfileImage = new Compressor(SetupUser2Activity.this).setQuality(15).compressToBitmap(mFileProfileImage);
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
                                                Intent usernameIntent = new Intent(SetupUser2Activity.this, SetupUser3Activity.class);
                                                startActivity(usernameIntent);
                                                finish();
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            //filePath = data.getData();

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                profilePictureURI = result.getUri();

                Glide.with(getApplicationContext()).load(profilePictureURI).into(setupuser2circleImageView);

                //Toast.makeText(getApplicationContext(), (CharSequence) postImageUri, Toast.LENGTH_LONG).show();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(getApplicationContext(), (CharSequence) error, Toast.LENGTH_LONG).show();
            }



            //Setting image to ImageView



        }
    }
}

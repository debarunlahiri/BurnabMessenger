package com.debarunlahiri.burnab.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.Group.GroupActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import id.zelory.compressor.Compressor;

public class AddStoryActivity extends AppCompatActivity {

    private CardView addstorytopCV, addstorybottomCV, addstorymiddleCV;
    private ImageView addstoryaddIV, addstoryIV;
    private TextView tvAddStory, tvAddStoryChangeImage, tvAddStoryCropImage, tvAddStoryBody;
    private Button addstorybutton;
    private EditText etAddStoryBody;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id;

    private Context mContext;
    private Uri postImageUri = null;
    private Bitmap mCompressedStoryImage;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        addstorybottomCV = findViewById(R.id.addstorybottomCV);
        addstorytopCV = findViewById(R.id.addstorytopCV);
        tvAddStory = findViewById(R.id.tvAddStory);
        addstoryaddIV = findViewById(R.id.addstoryaddIV);
        tvAddStoryChangeImage = findViewById(R.id.tvAddStoryChangeImage);
        tvAddStoryCropImage = findViewById(R.id.tvAddStoryCropImage);
        addstoryIV = findViewById(R.id.addstoryIV);
        addstorybutton = findViewById(R.id.addstorybutton);
        etAddStoryBody = findViewById(R.id.etAddStoryBody);
        addstorymiddleCV = findViewById(R.id.addstorymiddleCV);
        tvAddStoryBody = findViewById(R.id.tvAddStoryBody);

        addstorytopCV.setBackgroundResource(R.drawable.black_to_transparent);
        addstorybottomCV.setBackgroundResource(R.drawable.blacktotransparent2);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        user_id = currentUser.getUid();

        addstorybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postImageUri == null) {
                    Toast.makeText(getApplicationContext(), "Select an image", Toast.LENGTH_LONG).show();
                } else {

                }
            }
        });

        addstoryaddIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(AddStoryActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setMinCropResultSize(512, 512)
                                        .start(AddStoryActivity.this);
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

        tvAddStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(AddStoryActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setMinCropResultSize(512, 512)
                                        .start(AddStoryActivity.this);
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

        tvAddStoryChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(AddStoryActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setMinCropResultSize(512, 512)
                                        .start(AddStoryActivity.this);
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

        tvAddStoryCropImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(AddStoryActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                CropImage.activity(postImageUri)
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setMinCropResultSize(512, 512)
                                        .start(AddStoryActivity.this);
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

        addstorybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String story_caption = etAddStoryBody.getText().toString();
                uploadStory(story_caption);
            }
        });

        etAddStoryBody.setVisibility(View.GONE);
        addstorytopCV.setVisibility(View.GONE);
        addstorymiddleCV.setVisibility(View.GONE);
        addstoryIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postImageUri != null) {
                    if (etAddStoryBody.getVisibility() == View.GONE) {
                        etAddStoryBody.setVisibility(View.VISIBLE);
                        addstorymiddleCV.setVisibility(View.GONE);
                    } else if (etAddStoryBody.getVisibility() == View.VISIBLE) {
                        etAddStoryBody.setVisibility(View.GONE);
                        addstorymiddleCV.setVisibility(View.VISIBLE);
                        String story_body = etAddStoryBody.getText().toString();
                        if (story_body.isEmpty()) {
                            addstorymiddleCV.setVisibility(View.GONE);
                        } else {
                            addstorymiddleCV.setVisibility(View.VISIBLE);
                            tvAddStoryBody.setText(story_body);
                        }
                    }

                }
            }
        });

        if (postImageUri == null) {
            addstorybottomCV.setVisibility(View.INVISIBLE);
        } else {
            addstorybottomCV.setVisibility(View.VISIBLE);
        }

    }

    private void uploadStory(String story_caption) {
        if (postImageUri == null) {
            Toast.makeText(getApplicationContext(), "Select an image", Toast.LENGTH_LONG).show();
        } else {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Adding story");
            progressDialog.show();
            File mFileGroupProfileImage = new File(postImageUri.getPath());

            final Long ts_long = System.currentTimeMillis() / 1000;
            final String ts = ts_long.toString();
            //final StorageReference childRef = storageReference.child("users/profiles/profile_images/" + currentUser.getUid() + ".jpg");
            //final StorageReference thumb_childRef = storageReference.child("users/profile_images/profile_images/" + currentUser.getUid() + ".jpg");

            try {
                mCompressedStoryImage = new Compressor(AddStoryActivity.this).setQuality(8).compressToBitmap(mFileGroupProfileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }


            ByteArrayOutputStream mProfileBAOS = new ByteArrayOutputStream();
            mCompressedStoryImage.compress(Bitmap.CompressFormat.JPEG, 25, mProfileBAOS);
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
                                        String story_id = mDatabase.child("stories").child(user_id).push().getKey();
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                                        String formattedDate = sdf.format(new Date());
                                        HashMap<String, Object> mPostDataMap = new HashMap<>();
                                        mPostDataMap.put("body", story_caption.trim());
                                        mPostDataMap.put("story_id", story_id);
                                        mPostDataMap.put("formatted_date", formattedDate);
                                        mPostDataMap.put("timestamp", System.currentTimeMillis());
                                        mPostDataMap.put("user_id", user_id);
                                        mPostDataMap.put("story_image", downloadUri.toString());
                                        mDatabase.child("stories").child(user_id).child(story_id).setValue(mPostDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Story added successfully", Toast.LENGTH_LONG).show();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            //filePath = data.getData();

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                postImageUri = result.getUri();
                addstoryaddIV.setVisibility(View.GONE);
                tvAddStory.setVisibility(View.GONE);

                Glide.with(getApplicationContext()).load(postImageUri).into(addstoryIV);

                if (postImageUri == null) {
                    addstorybottomCV.setVisibility(View.INVISIBLE);
                } else {
                    addstorybottomCV.setVisibility(View.VISIBLE);
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

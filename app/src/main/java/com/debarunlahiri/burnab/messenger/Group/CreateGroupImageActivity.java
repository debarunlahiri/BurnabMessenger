package com.debarunlahiri.burnab.messenger.Group;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.debarunlahiri.burnab.messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class CreateGroupImageActivity extends AppCompatActivity {

    private Toolbar creategroupimagetoolbar;

    private Button createfinalgroupbutton;
    private ImageView creategroupcoverIV;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private Uri group_profile_image_URI = null;
    private Uri group_cover_photo_URI = null;

    private String group_name, group_desc;
    private String group_profile_image, group_cover_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_image);

        Bundle bundle = getIntent().getExtras();
        group_profile_image_URI = Uri.parse(bundle.get("group_profile_image").toString());
        group_name = bundle.get("group_name").toString();
        group_desc = bundle.get("group_desc").toString();

        creategroupimagetoolbar = findViewById(R.id.creategroupimagetoolbar);
        creategroupimagetoolbar.setTitle("Add cover photo");
        setSupportActionBar(creategroupimagetoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        creategroupimagetoolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.black_back));
        creategroupimagetoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        createfinalgroupbutton = findViewById(R.id.createfinalgroupbutton);
        creategroupcoverIV = findViewById(R.id.creategroupcoverIV);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        creategroupcoverIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(CreateGroupImageActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setMinCropResultSize(512, 512)
                                        .start(CreateGroupImageActivity.this);
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

        createfinalgroupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (group_cover_photo_URI == null) {
                    Toast.makeText(getApplicationContext(), "Add cover photo", Toast.LENGTH_LONG).show();
                } else {
                    Intent addCoverGroupIntent = new Intent(CreateGroupImageActivity.this, CreateGroupFinalActivity.class);
                    addCoverGroupIntent.putExtra("group_name", group_name);
                    addCoverGroupIntent.putExtra("group_desc", group_desc);
                    addCoverGroupIntent.putExtra("group_profile_image", group_profile_image_URI);
                    addCoverGroupIntent.putExtra("group_cover_image", group_cover_photo_URI);
                    startActivity(addCoverGroupIntent);
                }
            }
        });

    }

    private void createGroup() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            //filePath = data.getData();

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                group_cover_photo_URI = result.getUri();

                Glide.with(getApplicationContext()).load(group_cover_photo_URI).into(creategroupcoverIV);

                //Toast.makeText(getApplicationContext(), (CharSequence) postImageUri, Toast.LENGTH_LONG).show();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(getApplicationContext(), (CharSequence) error, Toast.LENGTH_LONG).show();
            }



            //Setting image to ImageView



        }
    }
}

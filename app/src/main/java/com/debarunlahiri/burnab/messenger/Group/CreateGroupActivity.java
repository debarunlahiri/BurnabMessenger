package com.debarunlahiri.burnab.messenger.Group;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupActivity extends AppCompatActivity {

    private Toolbar creategrouptoolbar;

    private CircleImageView creategroupprofileCIV;
    private EditText etCreateGroupName, etCreateGroupDesc;
    private Button creategroupbutton;
    private TextView tvCreateGroupAddImage;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private Context mContext;
    private Uri postImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mContext = CreateGroupActivity.this;

        creategrouptoolbar = findViewById(R.id.creategrouptoolbar);
        creategrouptoolbar.setTitle("Create Group");
        setSupportActionBar(creategrouptoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        creategrouptoolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.black_back));
        creategrouptoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        creategroupprofileCIV = findViewById(R.id.creategroupprofileCIV);
        etCreateGroupDesc = findViewById(R.id.etCreateGroupDesc);
        etCreateGroupName = findViewById(R.id.etCreateGroupName);
        creategroupbutton = findViewById(R.id.creategroupbutton);
        tvCreateGroupAddImage = findViewById(R.id.tvCreateGroupAddImage);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        tvCreateGroupAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(CreateGroupActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setMinCropResultSize(512, 512)
                                        .setAspectRatio(1, 1)
                                        .start(CreateGroupActivity.this);
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

        creategroupprofileCIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(CreateGroupActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setMinCropResultSize(512, 512)
                                        .setAspectRatio(1, 1)
                                        .start(CreateGroupActivity.this);
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

        creategroupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creategroupbutton.setText("Please wait...");
                String group_name = etCreateGroupName.getText().toString();
                String group_desc = etCreateGroupDesc.getText().toString();

                if (group_name.isEmpty()) {
                    creategroupbutton.setText("Next");
                    etCreateGroupName.setError("Please enter group name");
                } else if (group_desc.isEmpty()) {
                    creategroupbutton.setText("Next");
                    etCreateGroupDesc.setError("Please enter group description");
                } else if (postImageUri == null) {
                    creategroupbutton.setText("Next");
                    Toast.makeText(getApplicationContext(), "Please add profile image", Toast.LENGTH_LONG).show();
                } else {
                    Intent addCoverGroupIntent = new Intent(CreateGroupActivity.this, CreateGroupImageActivity.class);
                    addCoverGroupIntent.putExtra("group_name", group_name);
                    addCoverGroupIntent.putExtra("group_desc", group_desc);
                    addCoverGroupIntent.putExtra("group_profile_image", postImageUri);
                    startActivity(addCoverGroupIntent);
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

                Glide.with(getApplicationContext()).load(postImageUri).into(creategroupprofileCIV);

                //Toast.makeText(getApplicationContext(), (CharSequence) postImageUri, Toast.LENGTH_LONG).show();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(getApplicationContext(), (CharSequence) error, Toast.LENGTH_LONG).show();
            }



            //Setting image to ImageView



        }
    }
}

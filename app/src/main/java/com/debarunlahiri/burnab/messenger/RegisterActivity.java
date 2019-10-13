package com.debarunlahiri.burnab.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.debarunlahiri.burnab.messenger.SetupUser.SetupFinalActivity;
import com.debarunlahiri.burnab.messenger.SetupUser.SetupUser1Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegisterEmail, etRegisterPassword, etRegisterAge;
    private Button registerbutton;
    private ProgressBar registerPB;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterAge = findViewById(R.id.etRegisterAge);
        registerbutton = findViewById(R.id.registerbutton);
        registerPB = findViewById(R.id.registerPB);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        registerPB.setVisibility(View.INVISIBLE);

        if (currentUser != null) {
            sendToMain();
        } else {
            registerbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyboard();
                    registerbutton.setText("Registering in...");
                    registerbutton.setEnabled(false);
                    registerPB.setVisibility(View.VISIBLE);
                    String email = etRegisterEmail.getText().toString();
                    String password = etRegisterPassword.getText().toString();
                    String age = etRegisterAge.getText().toString();

                    if (email.isEmpty()) {

                        etRegisterEmail.requestFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        etRegisterEmail.setError("Please enter email id");
                        registerPB.setVisibility(View.INVISIBLE);
                        registerbutton.setText("Register");
                        registerbutton.setEnabled(true);

                    } else if (password.isEmpty()) {

                        etRegisterPassword.requestFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        etRegisterPassword.setError("Please enter your password");
                        registerPB.setVisibility(View.INVISIBLE);
                        registerbutton.setText("Register");
                        registerbutton.setEnabled(true);

                    } else if (password.equals("password") || password.equals("password1234") || password.equals("12345678") || password.equals("welcome")
                            || password.equals("princess") || password.equals("iloveyou") || password.equals("monkey") || password.equals("!@#$%^&*") || password.equals("666666")) {

                        Toast.makeText(getApplicationContext(), "Password not strong enough. Please use different password.", Toast.LENGTH_LONG).show();
                        registerPB.setVisibility(View.INVISIBLE);
                        registerbutton.setText("Register");
                        registerbutton.setEnabled(true);
                        etRegisterPassword.requestFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

                    } else if (age.isEmpty()) {

                        etRegisterAge.requestFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        etRegisterAge.setError("Please enter your age");
                        registerPB.setVisibility(View.INVISIBLE);
                        registerbutton.setText("Register");
                        registerbutton.setEnabled(true);

                    } else if (Integer.parseInt(age) >= 13) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //String token_id = FirebaseInstanceId.getInstance().getToken();
                                    //mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("token_id").setValue(token_id);
                                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("user_data").child("age").setValue(age);
                                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("user_id").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    Intent addUserDetailsIntent = new Intent(RegisterActivity.this, SetupUser1Activity.class);
                                    addUserDetailsIntent.putExtra("age", age);
                                    startActivity(addUserDetailsIntent);
                                    finish();
                                } else {
                                    registerbutton.setText("Register");
                                    registerbutton.setEnabled(true);
                                    String errMsg = task.getException().getMessage();
                                    Toast.makeText(getApplicationContext(), "Error: " + errMsg, Toast.LENGTH_LONG).show();
                                    registerPB.setVisibility(View.INVISIBLE);
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                                }
                            }
                        });
                    } else {
                        etRegisterAge.requestFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        Toast.makeText(getApplicationContext(), "We have the Age Verification set up with a minimum member age of 13", Toast.LENGTH_LONG).show();
                        registerPB.setVisibility(View.INVISIBLE);
                        registerbutton.setText("Register");
                        registerbutton.setEnabled(true);
                    }

                }
            });
        }
    }

    void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) RegisterActivity.this.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        View focusedView = RegisterActivity.this.getCurrentFocus();
        /*
         * If no view is focused, an NPE will be thrown
         *
         * Maxim Dmitriev
         */
        if (focusedView != null) {
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this, SetupUser1Activity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}

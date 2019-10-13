package com.debarunlahiri.burnab.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginEmail, etLoginPassword;
    private Button loginbutton;
    private ProgressBar loginPB;
    private TextView tvLoginForgortPassword, tvLoginRegister;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLoginEmail = findViewById(R.id.etRegisterEmail);
        etLoginPassword = findViewById(R.id.etRegisterPassword);
        loginbutton = findViewById(R.id.registerbutton);
        loginPB = findViewById(R.id.loginPB);
        tvLoginForgortPassword = findViewById(R.id.tvLoginForgortPassword);
        tvLoginRegister = findViewById(R.id.tvLoginRegister);

        loginPB.setVisibility(View.GONE);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        if (currentUser != null) {
            sendToMain();
        } else {
            loginbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = etLoginEmail.getText().toString();
                    String password = etLoginPassword.getText().toString();
                    loginPB.setVisibility(View.VISIBLE);
                    loginbutton.setText("Logging in");
                    loginbutton.setEnabled(false);
                    if (email.isEmpty()) {
                        loginPB.setVisibility(View.GONE);
                        etLoginEmail.requestFocus();
                        loginbutton.setText("Login");
                        loginbutton.setEnabled(true);
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        etLoginEmail.setError("Please enter your email");
                    } else if (password.isEmpty()) {
                        loginPB.setVisibility(View.GONE);
                        etLoginPassword.requestFocus();
                        loginbutton.setText("Login");
                        loginbutton.setEnabled(true);
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        etLoginPassword.setError("Please enter your password");
                    } else {
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String token_id = FirebaseInstanceId.getInstance().getToken();
                                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("token_id").setValue(token_id);
                                    sendToMain();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loginPB.setVisibility(View.GONE);
                                loginbutton.setText("Login");
                                loginbutton.setEnabled(true);
                                Toast.makeText(getApplicationContext(), "Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }


                }
            });
        }

        tvLoginForgortPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Feature will come soon", Toast.LENGTH_LONG).show();
            }
        });

        tvLoginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

    }

    private void sendToMain() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}

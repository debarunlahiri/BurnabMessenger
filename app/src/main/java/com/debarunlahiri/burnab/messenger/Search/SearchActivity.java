package com.debarunlahiri.burnab.messenger.Search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.debarunlahiri.burnab.messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageButton searchbackIB, searchclearIB;
    private TextView tvSearchRecent;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private List<Search> searchList = new ArrayList<>();
    private SearchAdapter searchAdapter;
    private Context mContext;
    private RecyclerView searchRV;
    private LinearLayoutManager linearLayoutManager;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mContext = SearchActivity.this;

        etSearch = findViewById(R.id.etSearch);
        searchbackIB = findViewById(R.id.searchbackIB);
        searchclearIB = findViewById(R.id.searchclearIB);
        tvSearchRecent = findViewById(R.id.tvSearchRecent);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://burnab-812eb.appspot.com");

        searchRV = findViewById(R.id.searchRV);
        searchAdapter = new SearchAdapter(searchList, mContext);
        linearLayoutManager = new LinearLayoutManager(mContext);
        searchRV.setAdapter(searchAdapter);
        searchRV.setLayoutManager(linearLayoutManager);
        searchRV.setItemAnimator(new DefaultItemAnimator());

        user_id = currentUser.getUid();
        searchclearIB.setVisibility(View.INVISIBLE);

        searchbackIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.super.onBackPressed();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                overridePendingTransition(0,0);
            }
        });

        searchclearIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.setText("");
            }
        });

        if (searchAdapter.getItemCount() == 0) {
            loadRecentSearches();
        } else {

        }

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    tvSearchRecent.setVisibility(View.GONE);
                    String user = etSearch.getText().toString();
                    if (user.isEmpty()) {
                        Toast.makeText(mContext, "Search bar cannot be empty", Toast.LENGTH_LONG).show();
                    } else {
                        searchUsers(user);
                    }

                }
                return false;
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 1) {
                    searchclearIB.setVisibility(View.VISIBLE);
                } else if (charSequence.length() < 1) {
                    searchclearIB.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    searchList.clear();
                    searchAdapter.notifyDataSetChanged();
                } else {
                    tvSearchRecent.setVisibility(View.GONE);
                    searchUsers(editable.toString());
                }

            }
        });
    }

    private void loadRecentSearches() {
        mDatabase.child("search").child("recent_searches").child(user_id).orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //searchList.clear();
                if (dataSnapshot.exists()) {
                    Search search = dataSnapshot.getValue(Search.class);
                    searchList.add(search);
                    searchAdapter.notifyDataSetChanged();
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

    private void searchUsers(String user) {
        mDatabase.child("users").orderByChild("username").startAt(user.toLowerCase()).endAt(user.toLowerCase() + "\uf8ff").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                searchList.clear();
                Search search = dataSnapshot.getValue(Search.class);
                searchList.add(search);
                searchAdapter.notifyDataSetChanged();


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

    @Override
    protected void onStart() {
        etSearch.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        super.onStart();
    }

    @Override
    protected void onStop() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        overridePendingTransition(0,0);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

package edu.fsu.cs.mobile.spinner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsSearchActivity extends SpinnerBaseActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);
        final EditText usernameSearch = findViewById(R.id.addFriends_search);

        // Initialize ListView
        databaseReference = database.getReference();
        Query query = databaseReference.orderByChild("username");
        query.addValueEventListener(new ValueEventListener() {
            private ArrayList<String> usernameList = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    String username;

                    if(data.child("username").getValue() != null) {
                        username = data.child("username").getValue().toString();
                        usernameList.add(username);
                    }
                }

                ArrayAdapter<String> usernameAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, usernameList);
                ((ListView) findViewById(R.id.addFriends_list)).setAdapter(usernameAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        // Add EditText Query Functionality
        usernameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String text = s.toString();
                databaseReference = database.getReference();
                Query query = databaseReference.orderByChild("username");
                query.addValueEventListener(new ValueEventListener() {
                    private ArrayList<String> usernameList = new ArrayList<>();

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()) {
                            String username;

                            if(data.child("username").getValue() != null && text.equals("")) {
                                username = data.child("username").getValue().toString();
                                usernameList.add(username);
                            }
                            else if(data.child("username").getValue() != null && data.child("username").getValue().toString().toLowerCase().startsWith(text.toLowerCase())) {
                                username = data.child("username").getValue().toString();
                                usernameList.add(username);
                            }
                        }

                        ArrayAdapter<String> usernameAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, usernameList);
                        ((ListView) findViewById(R.id.addFriends_list)).setAdapter(usernameAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}

package edu.fsu.cs.mobile.spinner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.Annotation;
import java.util.ArrayList;

public class FriendListActivity extends SpinnerBaseActivity {
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final FirebaseUser myUser = firebaseAuth.getCurrentUser();
    private final DatabaseReference databaseReference = database.getReference().child(myUser.getUid()).child("friendList");
    private final String TAG = "FriendListActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_friends);

        // Initialize ListView
        Query query = databaseReference.orderByChild("username");
        query.addValueEventListener(new ValueEventListener() {
            private ArrayList<String> friendList = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    String username, date;

                    if(data.child("username").getValue() != null && data.child("dateAdded").getValue() != null) {
                        username = data.child("username").getValue().toString();
                        date = data.child("dateAdded").getValue().toString();
                        friendList.add(username + "    |    Added on " + date);
                        Log.i(TAG, username + " added on " + date);
                    }
                }

                ArrayAdapter<String> friendListAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, friendList);
                
                // If it's the same adapter, don't update
                if(((ListView) findViewById(R.id.friendList_list)).getAdapter() != friendListAdapter)
                    ((ListView) findViewById(R.id.friendList_list)).setAdapter(friendListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        ((ListView) findViewById(R.id.friendList_list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView friendView = (TextView) view;
                Query query = database.getReference().orderByChild("username");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String friendUsername;
                        friendUsername = friendView.getText().toString().substring(0, friendView.getText().toString().indexOf("    |    "));

                        // Locate the Friend by Searching all Users
                        // Assumes no duplicate usernames...
                        for(DataSnapshot data : dataSnapshot.getChildren()) {
                            String userUID, username;

                            if(data.child("username").getValue() != null) {
                                username = data.child("username").getValue().toString();
                                Log.i(TAG, "Checking " + username + " against " + friendUsername + ".");

                                if(username != null && username.equals(friendUsername)) {
                                    userUID = data.getKey();

                                    Bundle bundle = new Bundle();
                                    bundle.putString("userUID", userUID);
                                    Intent intent = new Intent(FriendListActivity.this, ProfileActivity.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}

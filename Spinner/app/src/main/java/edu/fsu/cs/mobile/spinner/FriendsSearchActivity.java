package edu.fsu.cs.mobile.spinner;

import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

        final ArrayList<String> usernameList = new ArrayList<>();
        final ArrayAdapter<String> usernameAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, usernameList);

        // Initialize ListView
        databaseReference = database.getReference();
        final Query query = databaseReference.orderByChild("username");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usernameList.clear();

                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    String username;

                    if(data.child("username").getValue() != null) {
                        username = data.child("username").getValue().toString();
                        usernameList.add(username);
                    }
                }

                ((ListView) findViewById(R.id.addFriends_list)).setAdapter(usernameAdapter);
                query.removeEventListener(this);
                Log.e("FriendSearchActivity", "UPDATING LISTVIEW");
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
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        usernameList.clear();
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
                        usernameAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ((ListView) findViewById(R.id.addFriends_list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String friendName = ((TextView) view).getText().toString();
                Log.v("In FriendSearchActivity", "friendName=" + friendName);

                firebaseAuth = FirebaseAuth.getInstance();
                final FirebaseUser myUser = firebaseAuth.getCurrentUser();
                databaseReference = database.getReference().child(myUser.getUid());

                databaseReference.child("friendList").child(friendName).child("username").setValue(friendName);
                databaseReference.child("friendList").child(friendName).child("dateAdded").setValue(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
            }
        });
    }
}
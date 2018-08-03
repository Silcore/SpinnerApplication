package edu.fsu.cs.mobile.spinner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

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
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        final ArrayList<String> friendList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser myUser = firebaseAuth.getCurrentUser();
        databaseReference = database.getReference().child(myUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                String flist;
                String TAG = "MyActivity";
                if(data.child("friendList").getValue() != null){
                    flist = data.child("friendList").getValue().toString();
                    Log.i(TAG, flist);
                }



                }
                ArrayAdapter<String> usernameAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, friendList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

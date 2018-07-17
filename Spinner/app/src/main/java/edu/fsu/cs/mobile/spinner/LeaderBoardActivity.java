package edu.fsu.cs.mobile.spinner;


import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class LeaderBoardActivity extends AppCompatActivity {
    private ArrayList<String> usernameList = new ArrayList<>();
    private ArrayList<String> scoreList = new ArrayList<>();

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        databaseReference = database.getReference("spinner-494f4");
        Query q = databaseReference.orderByChild("highscore").limitToFirst(10);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    String username = data.child("username").getValue().toString();
                    String highscore = data.child("highscore").getValue().toString();
                    String TAG = "MyActivity";

                    Log.i(TAG, username);
                    Log.i(TAG, highscore);
                    usernameList.add(username);
                    scoreList.add(highscore);
                }

                ArrayAdapter<String> usernameAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, usernameList);
                ArrayAdapter<String> scoreAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, scoreList);
                ((ListView) findViewById(R.id.leaderboard_userView)).setAdapter(usernameAdapter);
                ((ListView) findViewById(R.id.leaderboard_scoreView)).setAdapter(scoreAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class Stats{

        public String username;
        int wins;

        public Stats(){

        }

        public Stats(String username, int wins){
            this.username = username;
            this.wins = wins;
        }

        public String getUser(){
            return username;
        }

        public void setUser(String uername){
            this.username = username;
        }

        public int getWin() {
            return wins;
        }

        public void setWin(int score) {
            this.wins = wins;
        }


    }
}



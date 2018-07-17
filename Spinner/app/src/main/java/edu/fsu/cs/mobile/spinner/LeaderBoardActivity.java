package edu.fsu.cs.mobile.spinner;


import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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




public class LeaderBoardActivity extends AppCompatActivity {

    private TextView[] orderWins = new TextView[10];
    private TextView[] orderUser = new TextView[10];





    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        /*orderWins[0] = findViewById(R.id.win1);
        orderWins[1] = findViewById(R.id.win2);
        orderWins[2] = findViewById(R.id.win3);
        orderWins[3] = findViewById(R.id.win4);
        orderWins[4] = findViewById(R.id.win5);
        orderWins[5] = findViewById(R.id.win6);
        orderWins[6] = findViewById(R.id.win7);
        orderWins[7] = findViewById(R.id.win8);
        orderWins[8] = findViewById(R.id.win9);
        orderWins[9] = findViewById(R.id.win10);*/



        orderUser[0] = findViewById(R.id.user1);
        orderUser[1] = findViewById(R.id.user2);
        orderUser[2] = findViewById(R.id.user3);
        orderUser[3] = findViewById(R.id.user4);
        orderUser[4] = findViewById(R.id.user5);
        orderUser[5] = findViewById(R.id.user6);
        orderUser[6] = findViewById(R.id.user7);
        orderUser[7] = findViewById(R.id.user8);
        orderUser[8] = findViewById(R.id.user9);
        orderUser[9] = findViewById(R.id.user10);



        databaseReference = database.getReference("spinner-494f4");
        Query q = databaseReference.orderByChild("wins").limitToFirst(10);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    String username = data.child("username").getValue().toString();
                    String wins = data.child("wins").getValue().toString();
                    String TAG = "MyActivity";

                    Log.i(TAG, wins);
                    orderUser[i].setText(username);
                    orderWins[i].setText(wins);
                }
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



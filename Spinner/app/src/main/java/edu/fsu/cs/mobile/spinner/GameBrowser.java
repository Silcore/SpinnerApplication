package edu.fsu.cs.mobile.spinner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class GameBrowser extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String uname;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_browser);

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser myUser = firebaseAuth.getCurrentUser();
        databaseReference = database.getReference().child(myUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String TAG = "In gameBrowser";
                //uses the User class made in ProfileActivity to get data
                ProfileActivity.User user = dataSnapshot.getValue(ProfileActivity.User.class);
                Log.v(TAG, user.email);
                Log.v(TAG, user.username);
                Log.v(TAG, "Wins = " + Integer.toString(user.wins));
                Log.v(TAG, "Losses = " + Integer.toString(user.losses));
                Log.v(TAG, "Ties = " + Integer.toString(user.ties));
                Log.v(TAG, "Highscore = " + Integer.toString(user.highscore));
                Log.v(TAG, "gameFlag = " + user.gameFlag);

                uname = user.username;

                if (user.gameFlag.equals("false")) {
                    Log.v(TAG, "gameFlag off, turning gameFlag on");
                    database.getReference().child(myUser.getUid()).child("gameFlag").setValue("true");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(GameBrowser.this,
                        "Read data failed", Toast.LENGTH_LONG).show();
            }
        });

        Log.v("in GameBrowser.....", "searching for users");
        database.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    //go through all users, and if their gameflag is true
                    String tempFlag = snapshot.child("gameFlag").getValue().toString();
                    String tempUname = snapshot.child("username").getValue().toString();

                    Log.v("in gameBrowser", "snapshot is=" + snapshot);
                    Log.v("in gameBroswer,", "temp =" + tempFlag);

                    if((tempFlag.equals("true")) && (!tempUname.equals(uname))){
                        //if the flag is up and its not you
                        Log.v("In gameBrowser", "user1 =" +uname + "user2 ="+tempUname +" want to play");

                        database.getReference().child(myUser.getUid()).child("opponent").setValue(tempUname);

                        if(snapshot.child("username").getValue().toString().equals(tempUname)){
                            //gets opponent username and sets his opponent to you
                            database.getReference().child(snapshot.getKey()).child("opponent").setValue(uname);

                            Bundle bundle = new Bundle();
                            String str_title = "Head to head";
                            Integer time_val = 30;

                            bundle.putString("string title", str_title);
                            bundle.putInt("Time_in_seconds", time_val);

                            Intent intent = new Intent(GameBrowser.this, Game.class);
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

}

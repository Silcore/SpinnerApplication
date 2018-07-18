package edu.fsu.cs.mobile.spinner;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView winsTextView;
    private TextView lossesTextView;
    private TextView tiesTextView;
    private TextView highscoreTextView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameTextView = findViewById(R.id.profileInsertUsername);
        winsTextView = findViewById(R.id.profileInsertWins);
        lossesTextView = findViewById(R.id.profileInsertLosses);
        tiesTextView = findViewById(R.id.profileInsertTies);
        highscoreTextView = findViewById(R.id.profileInsertHighscore);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser myUser = firebaseAuth.getCurrentUser();

        //uses UID as a key to search for the user that is currently logged in
        databaseReference = database.getReference().child(myUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String TAG = "in Profile Activity";
                User user = dataSnapshot.getValue(User.class);
                Log.v(TAG, user.email);
                Log.v(TAG, user.username);
                Log.v(TAG, Integer.toString(user.wins));
                Log.v(TAG, Integer.toString(user.losses));
                Log.v(TAG, Integer.toString(user.ties));
                Log.v(TAG, Integer.toString(user.highscore));

                usernameTextView.setText(user.username);
                winsTextView.setText(Integer.toString(user.wins));
                lossesTextView.setText(Integer.toString(user.losses));
                tiesTextView.setText(Integer.toString(user.ties));
                highscoreTextView.setText(Integer.toString(user.highscore));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this,
                        "Read data failed", Toast.LENGTH_LONG).show();;
            }
        });
    }

    public static class User{
        public String username;
        public String email;
        int wins;
        int losses;
        int ties;
        int highscore;
        String gameFlag;
        String opponent;
        int currentGameScore;

        User() {

        }

        public User(String uname, String em, int win, int loss, int tie, int hs, String gf, String op, int cgs) {
            username = uname;
            email = em;
            wins = win;
            losses = loss;
            ties = tie;
            highscore = hs;
            gameFlag = gf;
            opponent = op;
            currentGameScore = cgs;
        }
    }

    public void buttonMainMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

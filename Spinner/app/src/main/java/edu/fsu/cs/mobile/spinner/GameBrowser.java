package edu.fsu.cs.mobile.spinner;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
        databaseReference.child("gameFlag").setValue("true");
        databaseReference.child("gameOver").setValue("false");

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProfileActivity.User user = dataSnapshot.child(myUser.getUid()).getValue(ProfileActivity.User.class);

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // If we successfully find an opponent, set his opponent to us, and our opponent to him
                    if(snapshot.child("gameFlag").getValue() != null &&
                            snapshot.child("gameFlag").getValue().toString().equals("true") &&
                            !snapshot.child("username").getValue().toString().equals(user.username)) {
                        databaseReference.child("opponent").setValue(snapshot.child("username").getValue().toString());
                        database.getReference().child(snapshot.getKey()).child("opponent").setValue(user.username);
                        databaseReference.child("gameFlag").setValue("false");
                        databaseReference.child("currentGameScore").setValue(0);

                        Log.i("GAME BROWSER ACTIVITY: ", "" + !snapshot.child("username").getValue().toString().equals(user.username));
                        Log.i("GAME BROWSER ACTIVITY: ", "" + snapshot.child("username").getValue().toString());
                        Log.i("GAME BROWSER ACTIVITY: ", "" + user.username);

                        // Bundle Game Title and Game Length
                        Bundle bundle = new Bundle();
                        String str_title = "Head to Head";
                        String opponentKey = snapshot.getKey();
                        Integer time_val = 30;

                        bundle.putString("string title", str_title);
                        bundle.putString("opponentKey", opponentKey);
                        bundle.putInt("Time_in_seconds", time_val);

                        database.getReference().removeEventListener(this);

                        // Pass Bundle into Intent
                        Intent intent = new Intent(GameBrowser.this, Game.class);
                        intent.putExtras(bundle);
                        startActivity(intent);

                        break;
                    }

                    Log.i("GAME BROWSER ACTIVITY: ", "You've successfully escaped the if statement.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        database.getReference().addValueEventListener(listener);
    }

}

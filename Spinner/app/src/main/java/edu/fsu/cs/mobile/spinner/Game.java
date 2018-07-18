package edu.fsu.cs.mobile.spinner;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Random;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Game extends AppCompatActivity implements SensorEventListener {

    Button mStart;
    TextView mTimer;
    TextView mScore;
    TextView mDirection;
    String uname;
    int myWins;
    int myLoss;
    int myTie;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private static final int NORTH = 1;
    private static final int EAST = 2;
    private static final int SOUTH = 3;
    private static final int WEST = 4;
    private static int TIMER_TIME;
    private static final int MILLISECS_TO_SEC = 1000;

    private SensorManager mSensorManager;

    private SpinnerGame currGame;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent i = getIntent();

        // set Timer to time from intent
        TIMER_TIME = ( i.getExtras().getInt("Time_in_seconds") * MILLISECS_TO_SEC );

        mStart = (Button) findViewById(R.id.start_button);
        mTimer = (TextView) findViewById(R.id.timer_textView);
        mScore = (TextView) findViewById(R.id.score_textView);
        mDirection = (TextView) findViewById(R.id.direction_textView);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        currGame = new SpinnerGame();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onPause(){
        super.onPause();
        // if user leaves screen end current game
        if(timer != null)
            timer.onFinish();
        
        currGame.endGame();
        mSensorManager.unregisterListener(this);
    }

    public void ButtonClick(View view) {
        currGame.startGame(TIMER_TIME);
        mDirection.setText( directionToString(currGame.getCallDirection()));
        mStart.setVisibility(View.INVISIBLE);
        timer = new CountDownTimer(TIMER_TIME, MILLISECS_TO_SEC){

            // set TextView time and internal currGame.GameTime
            @Override
            public void onTick(long millisUntilFinished) {
                mTimer.setText(String.valueOf(millisUntilFinished / MILLISECS_TO_SEC));
                currGame.setGameTime( (int) (millisUntilFinished / MILLISECS_TO_SEC) );
            }

            // when timer is done display game over
            @Override
            public void onFinish() {
                mTimer.setText(getString(R.string.game_over));
                currGame.endGame();
            }

        }.start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // if user changes direction check score and game direction
        int i = currGame.setUserDirection(Math.round(event.values[0]));
        mDirection.setText( directionToString( currGame.getCallDirection() ) );
        mScore.setText( String.valueOf( currGame.getNumberMatches() ) );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // DO NOTHING
    }

    // directions managed as ints, converts int to string
    public String directionToString(int i){
        if ( i == NORTH ){
            return getString(R.string.north);
        }
        else if ( i == EAST ){
            return getString(R.string.east);
        }
        else if ( i == WEST ){
            return getString(R.string.west);
        }
        else if ( i == SOUTH ){
            return getString(R.string.south);
        }
        return "";
    }

    private class SpinnerGame {
        private int UserDirection = 0;
        private int CallDirection = 0;
        private int NumberMatches = 0;
        private int GameTime = 0;
        private boolean GameOver = false;
        private Random rand;

        private void startGame(int time){
            GameOver = false;
            setGameTime(time);
            rand = new Random();
            setCallDirection();
        }
        // converts from degrees to direction number
        // returns user's current direction (not used but helpful for testing)
        private int setUserDirection( float degree ){

            if ( ( degree >= 295 && degree <= 360 ) || ( degree > 360 && degree < 45 ) ){
                UserDirection = NORTH;
            }
            else if ( degree >= 45 && degree < 135 ){
                UserDirection = EAST;
            }
            else if ( degree >= 135 && degree < 225 ){
                UserDirection = SOUTH;
            }
            else if ( degree >= 225 && degree < 295 ){
                UserDirection = WEST;
            }

            // if user and call direction match and gametime is not zero and game is not over
            // increment score by one and pick new random call direction
            if ( UserDirection == CallDirection && CallDirection != 0 && GameTime != 0 && !GameOver){
                NumberMatches++;
                setCallDirection();
            }
            return UserDirection;
        }
        // calls random direction
        private void setCallDirection(){
            CallDirection = rand.nextInt( 4 ) + 1;
        }

        private int getCallDirection(){
            return CallDirection;
        }

        private int getNumberMatches(){
            return NumberMatches;
        }

        private void setGameTime(int time){
            GameTime = time;
        }

        private void endGame(){
            GameOver = true;
            firebaseAuth = FirebaseAuth.getInstance();
            final FirebaseUser myUser = firebaseAuth.getCurrentUser();
            databaseReference = database.getReference().child(myUser.getUid());

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String TAG = "In Game Activity";
                    //uses the User class made in ProfileActivity to get data
                    ProfileActivity.User user = dataSnapshot.getValue(ProfileActivity.User.class);
                    Log.v(TAG, user.email);
                    Log.v(TAG, user.username);
                    Log.v(TAG, "Wins = " + Integer.toString(user.wins));
                    Log.v(TAG, "Losses = " + Integer.toString(user.losses));
                    Log.v(TAG, "Ties = " + Integer.toString(user.ties));
                    Log.v(TAG, "Highscore = " + Integer.toString(user.highscore));
                    Log.v(TAG, "gameFlag = " + user.gameFlag);

                    myWins = user.wins;
                    myLoss = user.losses;
                    myTie = user.ties;

                    uname = user.username;

                    if(getNumberMatches() > user.highscore){
                        Log.v(TAG, "getnumberMatches > user.highscore");
                        database.getReference().child(myUser.getUid()).child("highscore").setValue(getNumberMatches());
                    }

                    if(user.gameFlag.equals("true")){
                        Log.v(TAG, "gameFlag on, turning gameFlag off");
                        database.getReference().child(myUser.getUid()).child("currentGameScore").setValue(getNumberMatches());
                        database.getReference().child(myUser.getUid()).child("gameFlag").setValue("false");

                        database.getReference().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                    //go through all users, and if their gameflag is true
                                    String tempUname = snapshot.child("username").getValue().toString();

                                    Log.v("in gameBrowser", "snapshot is=" + snapshot);

                                    if(!tempUname.equals(uname)){
                                        //if the flag is up and its not you
                                        Log.v("in game, waiting", "waiting");

                                        if(snapshot.child("opponent").getValue().toString().equals(uname)){
                                            //gets opponent you were playing
                                           // while(snapshot.child("gameFlag").equals("true")){
                                            //    //wait untill opponent is finished
                                            //}

                                            if(getNumberMatches() > Integer.parseInt(snapshot.child("currentGameScore").getValue().toString())){
                                                myWins += 1;
                                                database.getReference().child(myUser.getUid()).child("wins").setValue(myWins);
                                                database.getReference().child(myUser.getUid()).child("currentGameScore").setValue(0);
                                                database.getReference().child(myUser.getUid()).child("opponent").setValue("");
                                                Toast.makeText(Game.this, "YOU WON!", Toast.LENGTH_LONG).show();
                                            }else if(getNumberMatches() == Integer.parseInt(snapshot.child("currentGameScore").getValue().toString())){
                                                myTie += 1;
                                                database.getReference().child(myUser.getUid()).child("ties").setValue(myTie);
                                                database.getReference().child(myUser.getUid()).child("currentGameScore").setValue(0);
                                                database.getReference().child(myUser.getUid()).child("opponent").setValue("");
                                                Toast.makeText(Game.this, "YOU TIED!", Toast.LENGTH_LONG).show();
                                            }else if(getNumberMatches() < Integer.parseInt(snapshot.child("currentGameScore").getValue().toString())){
                                                myLoss += 1;
                                                database.getReference().child(myUser.getUid()).child("losses").setValue(myLoss);
                                                database.getReference().child(myUser.getUid()).child("currentGameScore").setValue(0);
                                                Toast.makeText(Game.this, "YOU LOST!", Toast.LENGTH_LONG).show();
                                            }
                                            Intent intent = new Intent(Game.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        //end if gameFlag == true
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Game.this,
                            "Read data failed", Toast.LENGTH_LONG).show();
                }
            });

            Intent intent = new Intent(Game.this, MainActivity.class);
            startActivity(intent);
        }
    }
}

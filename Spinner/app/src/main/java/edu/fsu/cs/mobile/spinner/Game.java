package edu.fsu.cs.mobile.spinner;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
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

public class Game extends SpinnerBaseActivity implements SensorEventListener {

    Button mStart;
    TextView mTimer;
    TextView mScore;
    TextView mDirection;
    String uname;
    String opponentKey;

    int myWins;
    int myLoss;
    int myTie;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    boolean isPressed = false;

    private static final int NORTH = 1;
    private static final int EAST = 2;
    private static final int SOUTH = 3;
    private static final int WEST = 4;
    private static final int PRESS = 5;
    private static final int SHAKE = 6;
    private static int TIMER_TIME;
    private static final int MILLISECS_TO_SEC = 1000;

    private long lastUpdate;

    private SensorManager mSensorManager;

    private SpinnerGame currGame;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent i = getIntent();

        // set Timer to time from intent
        if(i.getExtras() != null) {
            TIMER_TIME = (i.getExtras().getInt("Time_in_seconds") * MILLISECS_TO_SEC);

            if(i.getExtras().getString("opponentKey") != null)
                opponentKey = i.getExtras().getString("opponentKey");
        }

        mStart = (Button) findViewById(R.id.start_button);
        mTimer = (TextView) findViewById(R.id.timer_textView);
        mScore = (TextView) findViewById(R.id.score_textView);
        mDirection = (TextView) findViewById(R.id.direction_textView);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        lastUpdate =  System.currentTimeMillis();

        currGame = new SpinnerGame();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

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

    public void onPress(View view) {
        if ( currGame.getCallDirection() == PRESS ) {
            isPressed = true;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // if user changes direction check score and game direction
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            int i = currGame.setUserDirection(Math.round(event.values[0]));
            mDirection.setText(directionToString(currGame.getCallDirection()));
            mScore.setText(String.valueOf(currGame.getNumberMatches()));
        }
        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            if (isShake(event)){
                currGame.handleJump();
            }
        }

    }

    public boolean isShake(SensorEvent event){
        float[] values = event.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        // acceleration = (x^2 + y^2 + z^2) / g^2
        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);

        long actualTime = event.timestamp;

        if (accelationSquareRoot >= 2){
            if (actualTime - lastUpdate < 200) {
                return false;
            }
            lastUpdate = actualTime;
            return true;
        }
        return false;
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
        else if ( i == PRESS ){
            return getString(R.string.press);
        }
        else if ( i == SHAKE ){
            return getString(R.string.shake);
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
        private int setUserDirection( float degree ) {

            if ((degree >= 295 && degree <= 360) || (degree > 360 && degree < 45)) {
                UserDirection = NORTH;
            } else if (degree >= 45 && degree < 135) {
                UserDirection = EAST;
            } else if (degree >= 135 && degree < 225) {
                UserDirection = SOUTH;
            } else if (degree >= 225 && degree < 295) {
                UserDirection = WEST;
            }

            // if user and call direction match and gametime is not zero and game is not over
            // increment score by one and pick new random call direction
            if (UserDirection == CallDirection && CallDirection != 0 && GameTime != 0 && !GameOver) {
                NumberMatches++;
                setCallDirection();
            }
            else if ( CallDirection == PRESS && isPressed && GameTime != 0 && !GameOver ){
                NumberMatches++;
                setCallDirection();
                isPressed = false;
            }
            return UserDirection;
        }
        // calls random direction
        private void setCallDirection(){
            CallDirection = rand.nextInt( 6 ) + 1;
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

        private void handleJump(){
            if (CallDirection == SHAKE ){
                NumberMatches++;
                setCallDirection();
            }
        }


        private void endGame(){
            GameOver = true;
            firebaseAuth = FirebaseAuth.getInstance();
            final FirebaseUser myUser = firebaseAuth.getCurrentUser();
            databaseReference = database.getReference().child(myUser.getUid());
            final ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final ProfileActivity.User user = dataSnapshot.child(myUser.getUid()).getValue(ProfileActivity.User.class);
                    final DatabaseReference opponentReference;
                    if (opponentKey != null) {
                        opponentReference = database.getReference().child(opponentKey);
                    }
                    else {
                        opponentReference = null;
                    }
                    // Check if current score is higher than saved high score, if so update
                    if(user.highscore < getNumberMatches())
                        databaseReference.child("highscore").setValue(getNumberMatches());

                    // Update current score
                    databaseReference.child("currentGameScore").setValue(getNumberMatches());

                    final ValueEventListener opponentListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final ProfileActivity.User opponent = dataSnapshot.getValue(ProfileActivity.User.class);

                            if(opponent.gameOver.equals("true") && user.gameOver.equals("true")) {
                                // Win Status Checking
                                if (user.currentGameScore > opponent.currentGameScore) {
                                    Log.i("GAME.JAVA: ", "Your Score: " + user.currentGameScore + " | " + opponent.username + "'s Score: " + opponent.currentGameScore);
                                    Toast.makeText(Game.this, "You beat " + user.opponent + " by " +
                                            (user.currentGameScore - opponent.currentGameScore) + " points.", Toast.LENGTH_LONG).show();
                                    databaseReference.child("wins").setValue(user.wins + 1);
                                } else if (user.currentGameScore == opponent.currentGameScore) {
                                    Log.i("GAME.JAVA: ", "Your Score: " + user.currentGameScore + " | " + opponent.username + "'s Score: " + opponent.currentGameScore);
                                    Toast.makeText(Game.this, "You tied with " + user.opponent + " with " +
                                            user.currentGameScore + " points.", Toast.LENGTH_LONG).show();
                                    databaseReference.child("ties").setValue(user.ties + 1);
                                } else if (user.currentGameScore < opponent.currentGameScore) {
                                    Log.i("GAME.JAVA: ", "Your Score: " + user.currentGameScore + " | " + opponent.username + "'s Score: " + opponent.currentGameScore);
                                    Toast.makeText(Game.this, "You lost to " + user.opponent + " by " +
                                            (opponent.currentGameScore - user.currentGameScore) + " points.", Toast.LENGTH_LONG).show();
                                    databaseReference.child("losses").setValue(user.losses + 1);
                                }

                                databaseReference.child("opponent").setValue("");
                                opponentReference.removeEventListener(this);
                                database.getReference().removeEventListener(getReference());
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };

                    if (opponentReference != null) {
                        opponentReference.addValueEventListener(opponentListener);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

                protected ValueEventListener getReference() {
                    return this;
                }
            };

            database.getReference().addValueEventListener(listener);
            databaseReference.child("gameOver").setValue("true");

            Intent intent = new Intent(Game.this, MainActivity.class);
            startActivity(intent);
        }
    }
}

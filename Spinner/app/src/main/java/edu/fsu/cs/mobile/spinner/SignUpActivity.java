package edu.fsu.cs.mobile.spinner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUpActivity extends AppCompatActivity {

    private Button registerButton;
    private TextView emailTextView;
    private TextView passwordTextView;
    private TextView userNameTextView;
    private DatabaseReference databaseReference;

    private FirebaseAuth firebaseAuth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("server/allUsers");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        registerButton = findViewById(R.id.signUpRegister);
        emailTextView = findViewById(R.id.signupEmail);
        passwordTextView = findViewById(R.id.signUpPassword);
        userNameTextView = findViewById(R.id.signUpUserName);

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean submitFlag = true;
                final String email = emailTextView.getText().toString();
                String password = passwordTextView.getText().toString();
                final String username = userNameTextView.getText().toString();

                if(email.matches("")){
                    emailTextView.setError("Field cannot be blank");
                    submitFlag = false;
                }
                else if(!validEmail(email)) {
                    emailTextView.setError("Please enter a valid email.");
                    submitFlag = false;
                }

                if(password.matches("")){
                    passwordTextView.setError("Field cannot be blank");
                    submitFlag = false;
                }

                if(password.length() < 6){
                    passwordTextView.setError("Field must be longer than 6 characters");
                    submitFlag = false;
                }

                if (username.matches("")) {
                    userNameTextView.setError("Field cannot be blank");
                    submitFlag = false;
                }

                if(submitFlag){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener
                            (new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "Success",
                                        Toast.LENGTH_LONG).show();

                                //adding username and email to database
                                databaseReference = FirebaseDatabase.getInstance().getReference();
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                User newAcct = new User(username,email);
                                databaseReference.child(user.getUid()).setValue(newAcct);

                                Intent myIntent = new Intent(SignUpActivity.this, ProfileActivity.class);
                                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                SignUpActivity.this.startActivity(myIntent);
                            }else{
                                Toast.makeText(SignUpActivity.this, "Registration Failed",
                                        Toast.LENGTH_LONG).show();
                            }
                        }//end onComplete
                    });
                }
            }//end onClick
        });
    }

    public static class User{
        public String username;
        public String email;
        int wins;
        int losses;
        int ties;
        int highscore;
        int currentGameScore;
        String gameFlag;
        String gameOver;
        String opponent;

        public User(String uname, String em) {
            username = uname;
            email = em;
            wins = 0;
            losses = 0;
            ties = 0;
            highscore = 0;
            gameFlag = "false";
            currentGameScore = 0;
            opponent = "";
            gameOver = "false";
        }
    }

    private static boolean validEmail(String email) {
        // Pattern Referenced from http://emailregex.com/
        Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
        Matcher matcher = pattern.matcher(email);

        return matcher.find();
    }
}

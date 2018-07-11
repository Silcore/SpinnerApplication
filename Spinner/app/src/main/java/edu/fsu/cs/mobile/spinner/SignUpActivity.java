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

                if(submitFlag == true){
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

        public User(String uname, String em) {
            username = uname;
            email = em;
            wins = 0;
            losses = 0;
            ties = 0;
        }
    }
}

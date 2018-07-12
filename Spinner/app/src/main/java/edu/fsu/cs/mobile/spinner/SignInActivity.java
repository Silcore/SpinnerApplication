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

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextView emailTextView;
    private TextView passwordTextView;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();
        emailTextView = findViewById(R.id.signInEmail);
        passwordTextView = findViewById(R.id.signInPassword);
        signInButton = findViewById(R.id.signInButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTextView.getText().toString();
                final String password = passwordTextView.getText().toString();
                Boolean submitFlag = true;

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

                if(submitFlag == true){
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener
                            (new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(SignInActivity.this,
                                        "Signed in successfully", Toast.LENGTH_LONG).show();

                                // Open Main Activity
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(SignInActivity.this,
                                        "Sign in failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    }); //end signInWithEmail...
                }
            }//end onClick
        }); //end onClickListener
    }
}

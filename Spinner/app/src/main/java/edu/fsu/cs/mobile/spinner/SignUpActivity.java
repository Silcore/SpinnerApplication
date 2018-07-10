package edu.fsu.cs.mobile.spinner;

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


public class SignUpActivity extends AppCompatActivity {

    private Button registerButton;
    private TextView emailTextView;
    private TextView passwordTextView;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        registerButton = findViewById(R.id.signUpRegister);
        emailTextView = findViewById(R.id.signupEmail);
        passwordTextView = findViewById(R.id.signUpPassword);

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean submitFlag = true;
                String email = emailTextView.getText().toString();
                String password = passwordTextView.getText().toString();

                if(email.matches("")){
                    emailTextView.setError("Field cannot be blank");
                    submitFlag = false;
                }
                if(password.matches("")){
                    passwordTextView.setError("Field cannot be blank");
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


}

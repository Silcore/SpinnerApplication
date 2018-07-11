package edu.fsu.cs.mobile.spinner;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

public class ProfileActivity extends Activity {

    private TextView usernameTextView;
    private TextView winsTextView;
    private TextView lossesTextView;
    private TextView tiesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameTextView = findViewById(R.id.profileInsertUsername);
        winsTextView = findViewById(R.id.profileInsertWins);
        lossesTextView = findViewById(R.id.profileInsertLosses);
        tiesTextView = findViewById(R.id.profileInsertTies);


    }
}

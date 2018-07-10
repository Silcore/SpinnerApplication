package edu.fsu.cs.mobile.spinner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //added this here just to test and see if Firebase was hooked up properly
        Intent myIntent = new Intent(MainActivity.this, SignUpActivity.class);
        MainActivity.this.startActivity(myIntent);
    }
}

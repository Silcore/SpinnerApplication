package edu.fsu.cs.mobile.spinner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.signin.SignIn;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buttonNewGame(View view) {
        Intent intent = new Intent(this, NewGame.class);
        startActivity(intent);
    }

    public void buttonBrowseGames(View view) {
        Intent intent = new Intent(this, GameBrowser.class);
        startActivity(intent);
    }

    public void buttonProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void buttonScores(View view) {
        Intent intent = new Intent(this, LeaderBoardActivity.class);
        startActivity(intent);
    }

    public void buttonChatrooms(View view) {
        Intent intent = new Intent(this, chatRoomList.class);
        startActivity(intent);
    }
}

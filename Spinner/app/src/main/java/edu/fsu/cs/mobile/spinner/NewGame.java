package edu.fsu.cs.mobile.spinner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class NewGame extends SpinnerBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_game);

        final Spinner mySpinner = findViewById(R.id.newGame_sTime);

        Button button_submit = findViewById(R.id.newGame_bStart);
        button_submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String spin = mySpinner.getSelectedItem().toString();
                Integer time_val = Integer.parseInt(spin);     //getting the time in integer from the spinner
                boolean errorFlag = false;

                EditText the_title = findViewById(R.id.newGame_eGameTitle);
                String str_title = the_title.getText().toString();

                if(str_title.length() == 0) {
                    the_title.setError("Your game lobby must have a title.");
                    errorFlag = true;
                }

                if(!errorFlag) {
                    Bundle bundle = new Bundle();
                    bundle.putString("string title", str_title);
                    bundle.putInt("Time_in_seconds", time_val);

                    //Intent intent = new Intent(NewGame.this, GameBrowser.class);
                    //intent.putExtras(bundle);
                    //startActivity(intent);   //now start GameBrowser

                    // LaBelle - unsure how gamebrowser works
                    // Temporary solution: created separate Game Activity with nested SpinnerGame Class
                    Intent intent = new Intent(NewGame.this, Game.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

    }
}

package edu.fsu.cs.mobile.spinner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class NewGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_game);

    }

    public void GoToGameBrowser(View view){
        Spinner myspinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(NewGame.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Time));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myspinner.setAdapter(myAdapter);

        String spin = myspinner.getSelectedItem().toString();
        Integer time_val = Integer.parseInt(spin);     //getting the time in integer from the spinner

        EditText the_title = (EditText) findViewById(R.id.title);
        String str_title = the_title.getText().toString();

        Bundle bundle = new Bundle();
        bundle.putString("string title", str_title);
        bundle.putInt("Time_in_seconds", time_val);

        /*
        Intent intent = new Intent(this, GameBrowser.class);
        intent.putExtras(bundle);
        startActivity(intent);   //now start GameBrowser
        */
    }
}

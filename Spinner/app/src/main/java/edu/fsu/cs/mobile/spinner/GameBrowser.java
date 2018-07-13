package edu.fsu.cs.mobile.spinner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


public class GameBrowser extends Activity {

    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_browser);

        // Find the ListView resource.
        mainListView = (ListView) findViewById(R.id.mobile_list);

        Intent i = getIntent();
        String title_data = i.getExtras().getString("string title","");
        Integer _time_ = i.getExtras().getInt("Time_in_seconds");
        String str_time = _time_.toString();
        String title_and_time = title_data + "                               " + str_time + " seconds";

        // Create and populate the list of created games.
        String[] list = new String[] {title_and_time};
        ArrayList<String> List = new ArrayList<String>();
        List.addAll( Arrays.asList(list) );

        // Create ArrayAdapter using the created list.
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, List);

        mainListView.setAdapter( listAdapter );
    }



}

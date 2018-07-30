package edu.fsu.cs.mobile.spinner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class chatRoomList extends AppCompatActivity {

    private Button chatroomSubmit;
    private EditText enterRoom;
    private ListView chatrooms;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> chatroomList = new ArrayList<>();
    private DatabaseReference myChatrooms = FirebaseDatabase.getInstance().getReference().child("Chatrooms");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_list);

        chatroomSubmit = findViewById(R.id.addChatroomButton);
        enterRoom = findViewById(R.id.newChatroom);
        chatrooms = findViewById(R.id.chatroomListView);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, chatroomList);
        chatrooms.setAdapter(arrayAdapter);

        chatroomSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*hashes the name of the room with empty quotes because we just need the name
                 * of the room as a key, with no value */
                if(enterRoom.getText().toString().matches("")) {
                    enterRoom.setError("Field cannot be left blank");
                }else {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(removeWhitespace(enterRoom.getText().toString()), "");
                    myChatrooms.updateChildren(map);

                    // Reset room name
                    enterRoom.setText("");
                }
            }
        });

        myChatrooms.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();
                while(i.hasNext()) {
                    set.add(((DataSnapshot) i.next()).getKey());
                }
                chatroomList.clear();
                chatroomList.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); //end addValueEventListener


        chatrooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*launches intent that is for an individual chat room, uses arrayAdapter item that was clicked,
                  * casts its view to a textView to get the name of the chatroom that was clicked. */

                Intent intent = new Intent(chatRoomList.this, chatroom.class);
                intent.putExtra("roomName", ((TextView)view).getText().toString());
                startActivity(intent);
            }
        });
    }   //end onCreate

    private String removeWhitespace(String s) {
        return s.replace("\n", "");
    }
}

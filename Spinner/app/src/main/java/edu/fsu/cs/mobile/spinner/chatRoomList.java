package edu.fsu.cs.mobile.spinner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private Button chatroomDelete;
    private EditText enterRoom;
    private ListView chatrooms;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> chatroomList = new ArrayList<>();
    private DatabaseReference myChatrooms = FirebaseDatabase.getInstance().getReference().child("Chatrooms");

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    String username;
    Boolean chatroomFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_list);

        chatroomSubmit = findViewById(R.id.addChatroomButton);
        chatroomDelete = findViewById(R.id.deleteChatroom);
        enterRoom = findViewById(R.id.newChatroom);
        chatrooms = findViewById(R.id.chatroomListView);

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser myUser = firebaseAuth.getCurrentUser();
        databaseReference = database.getReference().child(myUser.getUid());

        //to avoid null ptr exceptions if user has never created a chatroom, just set it to empty quotes

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

                    /*
                    if the user has no chatroom then set it to one. If the user has a chatroom or the
                    chatroom name already exists tell them they cant make one
                     */
                    databaseReference.child("chatroom").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("chatroom")) {
                                //hasChild() prevents toString() nullptr exception
                                if (!dataSnapshot.getValue().toString().matches("")) {
                                    chatroomFlag = false;
                                    enterRoom.setError("You already have a chatroom, sheesh!");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });         //end ValueEventListenerForSingleEvent

                    if(chatroomFlag) {
                        databaseReference.child("chatroom").setValue(enterRoom.getText().toString());
                        map.put(removeWhitespace(enterRoom.getText().toString()), "");
                        myChatrooms.updateChildren(map);
                        enterRoom.setText("");
                    }
                }
            }
        });

        chatroomDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enterRoom.getText().toString().matches("")) {
                    enterRoom.setError("Field cannot be left blank");
                }else {
                    /* make sure user trying to delete chatroom is the person who created the
                    chatroom and then go ahead and delete the chatroom by removing the child's value
                    and setting the users chatroom the empty quotes
                     */

                    databaseReference.child("chatroom").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()) {
                                /*
                                to circumvent the null ptr exception potentially lurking from toString()
                                 */
                                if (dataSnapshot.getValue().toString().matches(enterRoom.getText().toString())) {
                                    myChatrooms.child(enterRoom.getText().toString()).removeValue();
                                    databaseReference.child("chatroom").setValue("");
                                }else{
                                    //they're using an old account without a chatroom attribute
                                    enterRoom.setError("Not your chatroom! Meanie!");
                                }
                            }else{
                                enterRoom.setError("Not your chatroom! Meanie!");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });         //end child.('chatroom') onDataChange
                }
            }
        });     //end deleteButton

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
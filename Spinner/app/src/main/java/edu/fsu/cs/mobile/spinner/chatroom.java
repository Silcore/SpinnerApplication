package edu.fsu.cs.mobile.spinner;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class chatroom extends AppCompatActivity {

    private Button sendButton;
    private EditText inputMessage;
    private TextView chatMessages;
    private DatabaseReference chatroomReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private String chatroomName;
    private String tempKey;
    private String username;
    private String chatUname, chatMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        Log.v("In chatroom", "inside of chatroom");

        sendButton = findViewById(R.id.chatroomSendMsg);
        inputMessage = findViewById(R.id.userMessage);
        chatMessages = findViewById(R.id.chatroomContent);
        chatroomName = getIntent().getExtras().get("roomName").toString();
        setTitle("Room: " + chatroomName);
        Log.v("In chatroom", "chatroomName ="+ chatroomName);


        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser myUser = firebaseAuth.getCurrentUser();
        databaseReference = database.getReference().child(myUser.getUid());

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ProfileActivity.User user = dataSnapshot.child(myUser.getUid()).getValue(ProfileActivity.User.class);
                username = user.username;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        database.getReference().addValueEventListener(listener);


        Log.v("In chatroom", "username is " + username);

        chatroomReference = FirebaseDatabase.getInstance().getReference().child("Chatrooms").child(chatroomName);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*get reference to chatroom name in database to add messages. Creates a unique key for each message
                 * being sent. Each unique message key is then given two children 1) the username of who is sending
                 * the message and 2) the actual message being sent. */

                if (inputMessage.getText().toString().matches("")) {
                    inputMessage.setError("Field cannot be left blank");
                } else if (inputMessage.getText().toString().length() > 25) {
                    inputMessage.setError("Too many characters");
                } else {

                    Map<String, Object> map = new HashMap<String, Object>();
                    tempKey = chatroomReference.push().getKey();
                    chatroomReference.updateChildren(map);

                    DatabaseReference ourMessage = chatroomReference.child(tempKey);
                    Map<String, Object> messageMap = new HashMap<String, Object>();

                    messageMap.put("name", username);
                    messageMap.put("message", inputMessage.getText().toString());
                    ourMessage.updateChildren(messageMap);
                }
            }
        });     //end onClickListener

        chatroomReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                addToConversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                addToConversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); //end ChildEventListener
    }

    private void addToConversation(DataSnapshot dataSnapshot){
        /*iterates through the messages in the database and displays them in our textView we created
         *which is also inside a scroll view so you can scroll around*/
        Iterator i = dataSnapshot.getChildren().iterator();

        while(i.hasNext()){
            chatMsg = (String)((DataSnapshot)i.next()).getValue();
            chatUname = (String)((DataSnapshot)i.next()).getValue();
            chatMessages.append(chatUname + " : " + chatMsg + " \n");
        }
    }
}

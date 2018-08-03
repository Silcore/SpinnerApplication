package edu.fsu.cs.mobile.spinner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SpinnerBaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.options_main_menu:
                Intent mainIntent = new Intent(this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                return true;
            case R.id.options_profile:
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                startActivity(profileIntent);
                return true;
            case R.id.options_chatrooms:
                Intent chatroomIntent = new Intent(this, chatRoomList.class);
                startActivity(chatroomIntent);
                return true;
            case R.id.options_friends_list:
                Intent friendsIntent = new Intent(this, FriendListActivity.class);
                startActivity(friendsIntent);
                return true;
            case R.id.options_add_friends:
                Intent addIntent = new Intent(this, FriendsSearchActivity.class);
                startActivity(addIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

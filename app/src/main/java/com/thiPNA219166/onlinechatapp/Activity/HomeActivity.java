package com.thiPNA219166.onlinechatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
//import androidx.recyclerview.widget.RecyclerView;


//import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
//import android.view.View;
import android.view.inputmethod.EditorInfo;
//import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;


//import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.navigation.NavigationBarView;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
import com.thiPNA219166.onlinechatapp.Fragment.GroupsFragment;
import com.thiPNA219166.onlinechatapp.Fragment.ConversationsFragment;
import com.thiPNA219166.onlinechatapp.Fragment.SearchFragment;
import com.thiPNA219166.onlinechatapp.Fragment.UserFragment;
import com.thiPNA219166.onlinechatapp.Fragment.FriendsFragment;
import com.thiPNA219166.onlinechatapp.R;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener{

    private ConversationsFragment conversationsFragment;
    private FriendsFragment friendsFragment;
    private GroupsFragment groupsFragment;
    private UserFragment userFragment;
    private SearchFragment searchFragment;

    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_messages);



        conversationsFragment = new ConversationsFragment();
        friendsFragment = new FriendsFragment();
        groupsFragment = new GroupsFragment();
        userFragment = new UserFragment();


        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnItemSelectedListener(this);
        loadFragment(conversationsFragment);

        editText = findViewById(R.id.et_search);

        editText.setOnEditorActionListener( new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    searchFragment = SearchFragment.newInstance(editText.getText().toString().trim());
                    //Fragment fragment = new SearchFragment();
                    loadFragment(searchFragment);
                    navigationView.setSelectedItemId(R.id.invisible);
                }
                return false;
            }
        });
    }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
            int itemID = item.getItemId();
            if ( itemID == R.id.iconmenu_messages) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_messages);
                selectedFragment = conversationsFragment;
            }
            else if ( itemID == R.id.iconmenu_users) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_users);
                selectedFragment = friendsFragment;
            }
            else if (itemID == R.id.iconmenu_groups) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_groups);
                selectedFragment = groupsFragment;
            } else if ( itemID == R.id.iconmenu_user) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_user);
                selectedFragment = userFragment;
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                editText.setText("");
            }
                return true;

        }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_framelayout, fragment);
        transaction.commit();
    }


    /*
    private void hideKeyboard(EditText editText){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
     */





}
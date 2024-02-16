package com.thiPNA219166.onlinechatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thiPNA219166.onlinechatapp.Model.Chat;
import com.thiPNA219166.onlinechatapp.Model.Message;
import com.thiPNA219166.onlinechatapp.Prevalent.DateTime;
import com.thiPNA219166.onlinechatapp.Prevalent.Prevalent;
import com.thiPNA219166.onlinechatapp.R;
import com.thiPNA219166.onlinechatapp.ViewHolder.LeftMessageViewHolder;
import com.thiPNA219166.onlinechatapp.ViewHolder.RightMessageViewHolder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import io.paperdb.Paper;

public class GroupChatActivity extends AppCompatActivity {
    private String chatID = null;
    private String myName;

    private DatabaseReference db;
    private RecyclerView mRecyclerView;
    private TextInputEditText contentMessEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        myName = Paper.book().read(Prevalent.currentOnlineUser);
        //firebase
        db = FirebaseDatabase.getInstance().getReference();
        // RecyclerView
        mRecyclerView = findViewById(R.id.recyclerview_chat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // linearLayoutManager.setReverseLayout(false);
        // linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);



        Intent intent = getIntent();

        // hien thi Toolbar
        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        chatID = intent.getStringExtra("chatID");
        String receiverName = intent.getStringExtra("groupName");
        Objects.requireNonNull(getSupportActionBar()).setTitle(receiverName);

        displayMessages();


        // Message Input EditTexxt
        contentMessEditText = findViewById(R.id.et_chat);
        //contentMessEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        TextInputLayout contentMessLayout = findViewById(R.id.text_layout);

        contentMessEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() !=0) {
                    contentMessLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        contentMessEditText.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String content = textView.getText().toString().trim();
                    if (!content.equals("")) {
                        String time = DateTime.getDatetimeNow();
                        Message message = new Message(chatID,myName,content,time);
                        chat(message);
                    }
                }
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  // back to login in activity
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        } else {
            if (item.getItemId() == R.id.itemmenu_chat_menu) {
                Intent intent = new Intent(GroupChatActivity.this, DetailGroupActivity.class);
                intent.putExtra("groupID",chatID);
                startActivity(intent);
            }
            return super.onOptionsItemSelected(item);
        }
    }

    private void chat(Message message){
        String lastMess;
        lastMess = myName + ": " + message.getContent();

        HashMap<String,Object> messRef = new HashMap<>();
        String messID = db.child("Message/"+chatID).push().getKey();
        messRef.put("Message/"+ chatID + "/"+ messID,message);
        messRef.put("Chat/" + chatID+ "/lastMessageContent", lastMess);
        messRef.put("Chat/" + chatID+ "/lastSendTime" , message.getSendTime());

        db.updateChildren(messRef).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    contentMessEditText.setText(null);
                }
            }
        });
    }

    private void displayMessages(){
        Query messRef= db.child("Message").child(chatID);

        FirebaseRecyclerOptions<Message> options=new FirebaseRecyclerOptions.Builder<Message>().setQuery(messRef, Message.class).build();
        //FirebaseRecyclerOptions<Message> options1 = Collections.reverse(options);
        FirebaseRecyclerAdapter<Message, RecyclerView.ViewHolder> adapter=new FirebaseRecyclerAdapter<Message, RecyclerView.ViewHolder>(options) {
            private static final int ITEM_TYPE_ANOTHER = 0;
            private static final int ITEM_TYPE_ME =1;
            @Override
            public int getItemViewType(int position) {
                if (getItem(position).getSender().equals(myName)) {
                    return ITEM_TYPE_ME;
                } else {
                    return ITEM_TYPE_ANOTHER;
                }
            }
            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull final Message model) {
                if (getItemViewType(position)==ITEM_TYPE_ME){
                    RightMessageViewHolder rightMessageViewHolder = (RightMessageViewHolder) holder;
                    rightMessageViewHolder.contentTextView.setText(model.getContent());
                    rightMessageViewHolder.timeTextView.setText(model.getSendTime());
                }
                else if (getItemViewType(position)==ITEM_TYPE_ANOTHER){
                    LeftMessageViewHolder leftMessageViewHolder = (LeftMessageViewHolder) holder;
                    String senderName = model.getSender();
                    leftMessageViewHolder.nameTextView.setText(senderName);
                    leftMessageViewHolder.contentTextView.setText(model.getContent());
                    leftMessageViewHolder.timeTextView.setText(model.getSendTime());
                    db.child("User").child(senderName).child("imageURL").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String imageUrl = snapshot.getValue(String.class);
                                assert imageUrl != null;
                                if (!imageUrl.equals("")) {
                                    Picasso.get().load(imageUrl).into(leftMessageViewHolder.imageView);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                if (viewType == ITEM_TYPE_ME) {
                    return new RightMessageViewHolder(layoutInflater.inflate(R.layout.message_right_item, parent, false));
                } else {
                    return new LeftMessageViewHolder(layoutInflater.inflate(R.layout.message_left_item, parent, false));
                }
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                mRecyclerView.scrollToPosition(getItemCount()-1);
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
        mRecyclerView.scrollToPosition(adapter.getItemCount()-1);
    }
}
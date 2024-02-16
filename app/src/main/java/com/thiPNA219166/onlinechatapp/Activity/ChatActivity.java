package com.thiPNA219166.onlinechatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
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

import java.util.HashMap;
import java.util.Objects;

import io.paperdb.Paper;

public class ChatActivity extends AppCompatActivity {

    private String chatID = null;
    private String myName, receiverName;
  //  private boolean isNew;
    private DatabaseReference db;
    private RecyclerView mRecyclerView;
    private TextInputEditText contentMessEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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

      //  isNew = false;

        // hien thi Toolbar
        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        chatID = intent.getStringExtra("chatID");
        receiverName = intent.getStringExtra("receiverName"); // chat ca nhan
       // user1_user2 = intent.getStringExtra("user1_user2");

        if (chatID == null) {
            db.child("User Chat").child(myName).orderByValue().equalTo(receiverName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            chatID = ds.getKey();
                            break;
                        }
                        displayMessages();
                        //Log.e("Notification", "find chatID in User Chat/myName/" + chatID );
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            displayMessages();
        }

        Objects.requireNonNull(getSupportActionBar()).setTitle(receiverName);





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
    public boolean onOptionsItemSelected(MenuItem item) {  // back to login in activity
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void chat(Message message){
        HashMap<String,Object> messMap = new HashMap<>();
        String chat_ID = message.getChatID();
        if (chat_ID == null){ // neu chua co chatID thi tao moi
            chat_ID = db.child("Chat").push().getKey();
            Chat chat = new Chat(chat_ID, myName, receiverName, message.getContent(), message.getSendTime());
            messMap.put("User Chat/"+myName+"/"+chat_ID,receiverName);
            messMap.put("User Chat/"+receiverName+"/"+chat_ID,myName);
            messMap.put("Chat/"+chat_ID, chat);
        } else {
            messMap.put("Chat/" + chat_ID+ "/lastMessageContent", message.getContent());
            messMap.put("Chat/" + chat_ID+ "/lastSendTime" , message.getSendTime());
        }
            String messID = db.child("Message/"+chat_ID).push().getKey();
            messMap.put("Message/"+ chat_ID + "/"+ messID,message);


        String finalChat_ID = chat_ID; // do o dong 186 yeu cau bien tuong duong final
        db.updateChildren(messMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        contentMessEditText.setText(null);
                        if (chatID == null){
                            chatID = finalChat_ID;
                            displayMessages();
                        }
                    }
                }
            });
    }

    private void displayMessages(){
        // lay image ng nhan
        db.child("User").child(receiverName).child("imageURL").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String imageUrl = snapshot.getValue(String.class);

                    // recyclerview
                    DatabaseReference messMap= db.child("Message").child(chatID);

                    FirebaseRecyclerOptions<Message> options=new FirebaseRecyclerOptions.Builder<Message>().setQuery(messMap, Message.class).build();
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
                                assert imageUrl != null;
                                if (!imageUrl.equals("")) {
                                    Picasso.get().load(imageUrl).into(leftMessageViewHolder.imageView);
                                }
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
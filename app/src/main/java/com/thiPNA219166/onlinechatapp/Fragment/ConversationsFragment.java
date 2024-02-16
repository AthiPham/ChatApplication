package com.thiPNA219166.onlinechatapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thiPNA219166.onlinechatapp.Activity.ChatActivity;
import com.thiPNA219166.onlinechatapp.Activity.GroupChatActivity;
import com.thiPNA219166.onlinechatapp.Model.Chat;
import com.thiPNA219166.onlinechatapp.Prevalent.Prevalent;
import com.thiPNA219166.onlinechatapp.R;
import com.thiPNA219166.onlinechatapp.ViewHolder.ChatViewHolder;

import io.paperdb.Paper;


public class ConversationsFragment extends Fragment {
    public ConversationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String username = Paper.book().read(Prevalent.currentOnlineUser);
        //firebase
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference chatRef= db.child("Chat");
        assert username != null;
        Query groupQuery = db.child("User Chat").child(username);
        //recyclerview
        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerview_list_group_chat);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<Chat> options=new FirebaseRecyclerOptions.Builder<Chat>().setIndexedQuery(groupQuery,chatRef,Chat.class).build();
        FirebaseRecyclerAdapter<Chat, ChatViewHolder> adapter=new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull final Chat model) {
                if (model.isGroupChat()) {
                    holder.nameTextView.setText(model.getGroupName());
                } else {
                    String anotherUserName = model.getAnotherUserName(username);
                    holder.nameTextView.setText(anotherUserName);
                    db.child("User").child(anotherUserName).child("imageURL").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String imageUrl = snapshot.getValue(String.class);
                                assert imageUrl != null;
                                if (!imageUrl.equals("")) {
                                    Picasso.get().load(imageUrl).into(holder.imageView);
                                } else {
                                    holder.imageView.setImageResource(R.drawable.user);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                holder.contentTextView.setText(model.getLastMessageContent());
                holder.timeTextView.setText(model.getLastSendTime());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = model.getChatID();
                        Intent intent;
                        if (model.isGroupChat()) {
                            intent = new Intent(getActivity(), GroupChatActivity.class);
                            intent.putExtra("groupName", model.getGroupName());
                        } else {
                            intent = new Intent(getActivity(), ChatActivity.class);
                            String anotherUserName = model.getAnotherUserName(username);
                            intent.putExtra("receiverName",anotherUserName);
                        }
                        intent.putExtra("chatID", id);
                        //intent.putExtra("user1_user2", Chat.findUser1_User2(username,anotherUserName));
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ChatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,parent,false));
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }
}
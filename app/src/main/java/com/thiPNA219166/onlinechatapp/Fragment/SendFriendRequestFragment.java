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
import com.thiPNA219166.onlinechatapp.Activity.GroupChatActivity;
import com.thiPNA219166.onlinechatapp.Model.Chat;
import com.thiPNA219166.onlinechatapp.Model.FriendRequest;
import com.thiPNA219166.onlinechatapp.Prevalent.Prevalent;
import com.thiPNA219166.onlinechatapp.R;
import com.thiPNA219166.onlinechatapp.ViewHolder.ChatViewHolder;
import com.thiPNA219166.onlinechatapp.ViewHolder.FriendRequestViewHolder;

import io.paperdb.Paper;

public class SendFriendRequestFragment extends Fragment {


    public SendFriendRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_friend_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String username = Paper.book().read(Prevalent.currentOnlineUser);
        //firebase
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Query requestQuery= db.child("Friend Request").orderByChild("senderName").equalTo(username);
        assert username != null;
        //Query sendRequestQuery = db.child("Relationship").child(username).orderByValue().equalTo(Prevalent.Relationship.SEND_REQUEST);
        //recyclerview
        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerview_list_friend_request);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<FriendRequest> options=new FirebaseRecyclerOptions.Builder<FriendRequest>().setQuery(requestQuery,FriendRequest.class).build();
        FirebaseRecyclerAdapter<FriendRequest, FriendRequestViewHolder> adapter=new FirebaseRecyclerAdapter<FriendRequest, FriendRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position, @NonNull final FriendRequest model) {
                holder.nameTextView.setText(model.getReceiverName());
                holder.contentTextView.setText(String.format("%s đã xin kết bạn",username));
                holder.timeTextView.setText(model.getSendTime());
                holder.denyButton.setVisibility(View.GONE);
                holder.acceptButton.setVisibility(View.GONE);
                int status = model.getStatus();
                if (status ==FriendRequest.REQUEST_ACCEPT){
                    holder.statusTextView.setVisibility(View.VISIBLE);
                } else if (status == FriendRequest.REQUEST_DENY) {
                    holder.itemView.setVisibility(View.GONE);
                }
                db.child("User").child(model.getReceiverName()).child("imageURL").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String url = snapshot.getValue(String.class);
                            assert url != null;
                            if (!url.equals("")){
                                Picasso.get().load(url).into(holder.imageView);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                return new FriendRequestViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_item,parent,false));
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
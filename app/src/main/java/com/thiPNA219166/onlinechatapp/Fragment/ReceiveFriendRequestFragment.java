package com.thiPNA219166.onlinechatapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thiPNA219166.onlinechatapp.Model.Chat;
import com.thiPNA219166.onlinechatapp.Model.FriendRequest;
import com.thiPNA219166.onlinechatapp.Prevalent.DateTime;
import com.thiPNA219166.onlinechatapp.Prevalent.Prevalent;
import com.thiPNA219166.onlinechatapp.R;
import com.thiPNA219166.onlinechatapp.ViewHolder.FriendRequestViewHolder;

import java.util.HashMap;
import java.util.Objects;

import io.paperdb.Paper;

public class ReceiveFriendRequestFragment extends Fragment {

    private DatabaseReference db;
    private FirebaseRecyclerAdapter<FriendRequest, FriendRequestViewHolder> adapter;

    public ReceiveFriendRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_receive_friend_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String username = Paper.book().read(Prevalent.currentOnlineUser);
        //firebase
        db = FirebaseDatabase.getInstance().getReference();
        Query requestQuery= db.child("Friend Request").orderByChild("receiverName").equalTo(username);
        assert username != null;
        //Query sendRequestQuery = db.child("Relationship").child(username).orderByValue().equalTo(Prevalent.Relationship.RECEIVE_REQUEST);
        //recyclerview
        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerview_list_friend_request);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<FriendRequest> options=new FirebaseRecyclerOptions.Builder<FriendRequest>().setQuery(requestQuery,FriendRequest.class).build();
         adapter=new FirebaseRecyclerAdapter<FriendRequest, FriendRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position, @NonNull final FriendRequest model) {
                holder.nameTextView.setText(model.getSenderName());
                holder.contentTextView.setText(String.format("%s đã xin kết bạn",model.getSenderName()));
                holder.timeTextView.setText(model.getSendTime());
                int status = model.getStatus();
                if (status ==FriendRequest.REQUEST_ACCEPT){
                    holder.statusTextView.setVisibility(View.VISIBLE);
                    holder.acceptButton.setVisibility(View.GONE);
                    holder.denyButton.setVisibility(View.GONE);
                } else if (status == FriendRequest.REQUEST_DENY) {
                    holder.itemView.setVisibility(View.GONE);
                } else {
                    holder.statusTextView.setVisibility(View.GONE);
                }
                holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        acceptRequest(model,holder.getBindingAdapterPosition());
                    }
                });
                holder.denyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        denyRequest(model,holder.getBindingAdapterPosition());
                    }
                });
                db.child("User").child(model.getSenderName()).child("imageURL").addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void acceptRequest(FriendRequest request, int adapterPosition){
        String requestID = request.getRequestID();
        String sender = request.getSenderName();
        String receiver = request.getReceiverName();

        db.child("User Chat").child(receiver).orderByValue().equalTo(sender).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String chatID = null;
                HashMap<String,Object> hashMap = new HashMap<>();
                if (!snapshot.exists()) {
                    chatID = db.child("Chat").push().getKey();
                    Chat chat = new Chat(chatID, sender, receiver, "Hãy bắt đầu cuộc trò chuyện", DateTime.getDatetimeNow());
                    hashMap.put("User Chat/"+sender+"/"+chatID,receiver);
                    hashMap.put("User Chat/"+receiver+"/"+chatID,sender);
                    hashMap.put("Chat/"+chatID, chat);
                }
                hashMap.put("Friend Request/"+requestID+"/status", FriendRequest.REQUEST_ACCEPT);
                hashMap.put("Friend/"+receiver+"/"+sender,true);
                hashMap.put("Friend/"+sender+"/"+receiver,true);
                hashMap.put("Relationship/"+receiver+"/"+sender,Prevalent.Relationship.FRIEND);
                hashMap.put("Relationship/"+sender+"/"+receiver,Prevalent.Relationship.FRIEND);
                db.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            adapter.notifyItemChanged(adapterPosition);
                            Log.e("Notification","acceptRequest:" + requestID+" with "+ adapterPosition);
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void denyRequest(FriendRequest request, int adapterPosition){
        String requestID = request.getRequestID();
        String sender = request.getSenderName();
        String receiver = request.getReceiverName();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Friend Request/"+requestID+"/status", FriendRequest.REQUEST_DENY);
        hashMap.put("Relationship/"+receiver+"/"+sender,null); // xoa moi quan he, coi nhu van la ng la
        hashMap.put("Relationship/"+sender+"/"+receiver,null);
        db.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    adapter.notifyItemChanged(adapterPosition);
                    Log.e("Notification","denyRequest:" + requestID+" with "+ adapterPosition);
                }
            }
        });
    }
}
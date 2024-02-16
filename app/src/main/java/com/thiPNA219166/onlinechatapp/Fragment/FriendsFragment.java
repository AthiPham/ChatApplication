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
import android.widget.TextView;
import android.widget.Toast;

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
import com.thiPNA219166.onlinechatapp.Activity.ListFriendRequestActivity;
import com.thiPNA219166.onlinechatapp.Model.Chat;
import com.thiPNA219166.onlinechatapp.Model.User;
import com.thiPNA219166.onlinechatapp.Prevalent.Prevalent;
import com.thiPNA219166.onlinechatapp.R;
import com.thiPNA219166.onlinechatapp.ViewHolder.UserViewHolder;

import io.paperdb.Paper;

public class FriendsFragment extends Fragment {

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView friendRequest_TV = view.findViewById(R.id.tv_friend_request);
        friendRequest_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListFriendRequestActivity.class);
                startActivity(intent);
            }
        });

        String username = Paper.book().read(Prevalent.currentOnlineUser);
        //firebase
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef= db.child("User");
        assert username != null;
        Query friendQuery = db.child("Friend").child(username);
        //recyclerview
        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerview_list_users);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<User> options=new FirebaseRecyclerOptions.Builder<User>().setIndexedQuery(friendQuery,userRef,User.class).build();
        FirebaseRecyclerAdapter<User, UserViewHolder> adapter=new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull final User model) {
                holder.textView.setText(model.getName());
                String imageUrl = model.getImageURL();
                if (!imageUrl.equals("")){Picasso.get().load(imageUrl).into(holder.imageView);}
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String receiverName = model.getName();

                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("receiverName",receiverName);
                        startActivity(intent);

                    }
                });

            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false));
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
package com.thiPNA219166.onlinechatapp.Fragment;


import android.content.Intent;
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
import android.widget.Toast;

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
import com.thiPNA219166.onlinechatapp.Activity.ChatActivity;
import com.thiPNA219166.onlinechatapp.Model.Chat;
import com.thiPNA219166.onlinechatapp.Model.FriendRequest;
import com.thiPNA219166.onlinechatapp.Model.User;
import com.thiPNA219166.onlinechatapp.Prevalent.DateTime;
import com.thiPNA219166.onlinechatapp.Prevalent.Prevalent;
import com.thiPNA219166.onlinechatapp.R;
import com.thiPNA219166.onlinechatapp.ViewHolder.UserViewHolder;

import java.util.HashMap;

import io.paperdb.Paper;

public class SearchFragment extends Fragment {

    private static final String ARG_USER_NAME = "searchedName";

    private String searchedName;

    public SearchFragment() {
        // Required empty public constructor
    }


    public static SearchFragment newInstance(String name) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchedName = getArguments().getString(ARG_USER_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e("Test ","searched Name: "+ searchedName);

        String myName = Paper.book().read(Prevalent.currentOnlineUser);
        //firebase
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Query userRef = db.child("User").orderByChild("name").startAt(searchedName).endAt(searchedName+"\uf8ff");
        //recyclerview
        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerview_list_users);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<User> options=new FirebaseRecyclerOptions.Builder<User>().setQuery(userRef,User.class).build();
        FirebaseRecyclerAdapter<User, UserViewHolder> adapter=new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull final User model) {
                String anotherUserName = model.getName();
                if (anotherUserName.equals(myName))
                {
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));

                } else
                {
                    holder.textView.setText(anotherUserName);
                    String imageUrl = model.getImageURL();
                    if (!imageUrl.equals("")) {
                        Picasso.get().load(imageUrl).into(holder.imageView);
                    }

                    assert myName != null;
                    db.child("Relationship").child(myName).child(anotherUserName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                int relation = snapshot.getValue(Integer.class);
                                if (relation == Prevalent.Relationship.RECEIVE_REQUEST) {
                                    holder.button.setText(R.string.received);
                                    holder.button.setEnabled(false);
                                    holder.button.setVisibility(View.VISIBLE);
                                } else if (relation == Prevalent.Relationship.SEND_REQUEST) {
                                    holder.button.setText(R.string.sent);
                                    holder.button.setEnabled(false);
                                    holder.button.setVisibility(View.VISIBLE);
                                } else if (relation == Prevalent.Relationship.FRIEND) {
                                    holder.button.setVisibility(View.INVISIBLE);
                                }

                            } else {

                                holder.button.setVisibility(View.VISIBLE);
                            }

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                                    intent.putExtra("receiverName", anotherUserName);
                                    startActivity(intent);
                                }
                            });


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    holder.button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // gui loi moi ket ban
                            String receiverName = model.getName();
                            String id = db.child("Friend Request").push().getKey();
                            FriendRequest friendRequest = new FriendRequest(id, myName, receiverName, DateTime.getDatetimeNow(), FriendRequest.REQUEST_WAITING);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("Friend Request/" + id, friendRequest);
                            hashMap.put("Relationship/" + myName + "/" + receiverName, Prevalent.Relationship.SEND_REQUEST);
                            hashMap.put("Relationship/" + receiverName + "/" + myName, Prevalent.Relationship.RECEIVE_REQUEST);


                            db.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        holder.button.setText(R.string.sent);
                                        holder.button.setEnabled(false);
                                    } else {
                                        Toast.makeText(getActivity(), "Gửi lời mời thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });
                }

            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false));
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();

        /*
        if (adapter.getItemCount()<=0){
            TextView notificationTextView = view.findViewById(R.id.tv_notification);
            notificationTextView.setText("Không tìm thấy người dùng trên");
            notificationTextView.setVisibility(View.VISIBLE);
        }

         */
    }

}
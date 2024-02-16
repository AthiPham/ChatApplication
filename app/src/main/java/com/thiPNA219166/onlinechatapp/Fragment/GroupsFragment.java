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
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.thiPNA219166.onlinechatapp.Activity.AddGroupActivity;
import com.thiPNA219166.onlinechatapp.Activity.GroupChatActivity;
import com.thiPNA219166.onlinechatapp.Model.Chat;
import com.thiPNA219166.onlinechatapp.Prevalent.Prevalent;
import com.thiPNA219166.onlinechatapp.R;
import com.thiPNA219166.onlinechatapp.ViewHolder.ChatViewHolder;

import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GroupsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupsFragment newInstance(String param1, String param2) {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnAddGroup = view.findViewById(R.id.btn_add_group);
        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddGroupActivity.class));
            }
        });


        String username = Paper.book().read(Prevalent.currentOnlineUser);
        //firebase
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference chatRef= db.child("Chat");
        assert username != null;
        Query groupQuery = db.child("User Chat").child(username).orderByValue().equalTo(true);
        //recyclerview
        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerview_list_group_chat);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<Chat> options=new FirebaseRecyclerOptions.Builder<Chat>().setIndexedQuery(groupQuery,chatRef,Chat.class).build();
        FirebaseRecyclerAdapter<Chat, ChatViewHolder> adapter=new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull final Chat model) {
                holder.nameTextView.setText(model.getGroupName());
                holder.contentTextView.setText(model.getLastMessageContent());
                holder.timeTextView.setText(model.getLastSendTime());
                //Picasso.get().load(model.getImageURL()).into(holder.imageView);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), GroupChatActivity.class);
                        intent.putExtra("chatID", model.getChatID());
                        intent.putExtra("groupName",model.getGroupName());
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
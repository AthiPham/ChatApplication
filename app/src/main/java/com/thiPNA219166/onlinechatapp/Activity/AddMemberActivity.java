package com.thiPNA219166.onlinechatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thiPNA219166.onlinechatapp.Model.User;
import com.thiPNA219166.onlinechatapp.Prevalent.Prevalent;
import com.thiPNA219166.onlinechatapp.R;
import com.thiPNA219166.onlinechatapp.ViewHolder.MemberViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.paperdb.Paper;

public class AddMemberActivity extends AppCompatActivity {
    private String groupID;
    private String myName;
    private int sumChosenMember;
    private final HashMap<String, Boolean> listChosenMemberName = new HashMap<String, Boolean>();
    private DatabaseReference db, friendRef;
    private TextView sumTextView;
    //private FirebaseRecyclerAdapter<User, MemberViewHolder> adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        Intent intent = getIntent();
        groupID = intent.getStringExtra("groupID");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_member);

        myName = Paper.book().read(Prevalent.currentOnlineUser);


        Button button = findViewById(R.id.btn_add_member);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!listChosenMemberName.isEmpty()){
                    addMember();
                }
            }
        });

        
        sumChosenMember =0;
        sumTextView = findViewById(R.id.sum_member);

        recyclerView = findViewById(R.id.recyclerview_member);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseDatabase.getInstance().getReference();
        friendRef = db.child("Friend").child(myName);
        search(friendRef);

    }

    private void search(Query friendQuery){
        DatabaseReference userRef= db.child("User");
        
        FirebaseRecyclerOptions<User> options=new FirebaseRecyclerOptions.Builder<User>().setIndexedQuery(friendQuery,userRef,User.class).build();
        FirebaseRecyclerAdapter<User, MemberViewHolder> adapter = new FirebaseRecyclerAdapter<User, MemberViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull MemberViewHolder holder, int position, @NonNull final User model) {
                String name = model.getName();
                holder.textView.setText(name);
                String url = model.getImageURL();
                if (!url.equals("")) {
                    Picasso.get().load(model.getImageURL()).into(holder.imageView);
                }

                db.child("Group Member").child(groupID).child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){  // da la thanh vien
                            holder.checkBox.setChecked(true);
                            holder.checkBox.setEnabled(false);
                        } else {      // chua la thanh vien
                            if (listChosenMemberName.containsKey(name)){  // da dc chon trc do
                                holder.checkBox.setChecked(true);
                            }
                            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                        if (holder.checkBox.isChecked()){
                                            sumChosenMember ++;
                                            listChosenMemberName.put(name,true);
                                        } else {
                                            sumChosenMember --;
                                            listChosenMemberName.remove(name);
                                        }
                                        sumTextView.setText(String.valueOf(sumChosenMember));
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MemberViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item,parent,false));
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem searchViewItem = menu.findItem(R.id.search_bar);

        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Query searchQuery = db.child("Friend").child(myName).orderByKey().startAt(query).endAt(query+"\uf8ff");
                search(searchQuery);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query searchQuery;
                if (newText.equals("")){
                    searchQuery = friendRef;
                } else {
                    searchQuery = db.child("Friend").child(myName).orderByKey().startAt(newText).endAt(newText+"\uf8ff");
                }
                search(searchQuery);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void addMember(){
        List<String> listOfKeys = new ArrayList<String>(listChosenMemberName.keySet());

        HashMap<String, Object> groupDataMap = new HashMap<>();
        for (String item: listOfKeys) {
            groupDataMap.put("Group Member/"+groupID+"/"+item,true);
          //  groupDataMap.put("User Group/"+ item+"/"+groupID,true);
            groupDataMap.put("User Chat/"+item+"/"+groupID,true);
        }
        groupDataMap.put("Group/"+groupID+"/numberOfMember", ServerValue.increment(listOfKeys.size()));
        db.updateChildren(groupDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //loadingbar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    Toast.makeText(AddMemberActivity.this,"Thêm thành viên thành công", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(AddMemberActivity.this,"Thêm thành viên thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


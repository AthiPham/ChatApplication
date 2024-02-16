package com.thiPNA219166.onlinechatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;
import com.thiPNA219166.onlinechatapp.Model.User;
import com.thiPNA219166.onlinechatapp.Prevalent.Prevalent;
import com.thiPNA219166.onlinechatapp.R;
import com.thiPNA219166.onlinechatapp.ViewHolder.MemberViewHolder;

import java.util.HashMap;
import java.util.Objects;

import io.paperdb.Paper;

public class LeaderLeaveGroupActivity extends AppCompatActivity {
    
    private DatabaseReference db, memberRef;
    private String myName, groupID;
    private String chosenNewLeader;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_leave_group);

        Intent intent = getIntent();
        groupID = intent.getStringExtra("groupID");
        myName = Paper.book().read(Prevalent.currentOnlineUser);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.chose_leader);


        db = FirebaseDatabase.getInstance().getReference();
        memberRef = db.child("Group Member").child(groupID);

        recyclerView = findViewById(R.id.recyclerview_member);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button leaveGroupButton = findViewById(R.id.btn_leave_group);
        leaveGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chosenNewLeader!=null){
                    leaveGroup();
                }
                else {
                    Toast.makeText(LeaderLeaveGroupActivity.this,"Hãy chọn 1 leader mới",Toast.LENGTH_SHORT).show();
                }
            }
        });

        search(memberRef);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);

        MenuItem searchViewItem = menu.findItem(R.id.search_bar);

        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Query friendQuery = db.child("Group Member").child(groupID).orderByKey().startAt(query).endAt(query+"\uf8ff");
                search(friendQuery);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")){
                    search(memberRef);
                } else {
                    Query friendQuery = db.child("Group Member").child(groupID).orderByKey().startAt(newText).endAt(newText+"\uf8ff");
                    search(friendQuery);
                }
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
    
    private void search(Query searchQuery){

        DatabaseReference userRef= db.child("User");

        FirebaseRecyclerOptions<User> options=new FirebaseRecyclerOptions.Builder<User>().setIndexedQuery(searchQuery,userRef,User.class).build();
        FirebaseRecyclerAdapter<User, MemberViewHolder> adapter=new FirebaseRecyclerAdapter<User, MemberViewHolder>(options) {
            private int chosePosition = -1;

            @Override
            protected void onBindViewHolder(@NonNull MemberViewHolder holder, int position, @NonNull final User model) {
                String name = model.getName();
                if (name.equals(myName)){ // la leader cu (me) thi ko hien thi
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                } else { // cac thanh vien khac
                    holder.textView.setText(name);
                    String url = model.getImageURL();
                    if (!url.equals("")) {
                        Picasso.get().load(url).into(holder.imageView);
                    }

                    if (name.equals(chosenNewLeader)){holder.checkBox.setChecked(true);}
                    holder.checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (holder.checkBox.isChecked()) { //b == holder.checkBox.isChecked()
                                if (chosenNewLeader!=null) {  // da chon member
                                    MemberViewHolder chosenViewHolder = (MemberViewHolder) recyclerView.findViewHolderForAdapterPosition(chosePosition);
                                    if (chosenViewHolder != null) { // neu member dc chon van hien thi tren dt
                                        chosenViewHolder.checkBox.setChecked(false);
                                    }
                                }
                                chosenNewLeader = name;
                                chosePosition = holder.getBindingAdapterPosition();
                            } else {
                                chosenNewLeader = null;
                                chosePosition = -1;
                                Log.e("Notification","onCheckedChange boolean b = false");
                            }
                        }
                    });
                }

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

    private void leaveGroup(){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Group/"+groupID+"/numberOfMember", ServerValue.increment(-1));
        hashMap.put("Group/"+groupID+"/leaderName",chosenNewLeader);
        hashMap.put("Group Member/"+groupID+"/"+myName,null);
        hashMap.put("User Group/"+ myName +"/"+groupID,null);
        hashMap.put("User Chat/"+myName+"/"+groupID,null);

        db.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LeaderLeaveGroupActivity.this,"Rời nhóm thành công",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LeaderLeaveGroupActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(LeaderLeaveGroupActivity.this,"Rời nhóm thất bại",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
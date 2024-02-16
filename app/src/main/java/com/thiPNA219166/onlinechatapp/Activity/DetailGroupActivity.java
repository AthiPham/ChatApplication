package com.thiPNA219166.onlinechatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thiPNA219166.onlinechatapp.Model.Group;
import com.thiPNA219166.onlinechatapp.Model.User;
import com.thiPNA219166.onlinechatapp.Prevalent.Prevalent;
import com.thiPNA219166.onlinechatapp.R;
import com.thiPNA219166.onlinechatapp.ViewHolder.MemberViewHolder;
import com.thiPNA219166.onlinechatapp.ViewHolder.UserViewHolder;

import java.util.HashMap;
import java.util.Objects;

import io.paperdb.Paper;

public class DetailGroupActivity extends AppCompatActivity {
    private Group group;
    private String myName;
    private boolean isLeader;

    private DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_group);

        group = new Group();
        Intent intent = getIntent();
        group.setGroupID(intent.getStringExtra("groupID"));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle( R.string.title_group_detail);

        TextView nameTextView = findViewById(R.id.tv_group_name);
        TextView leaderTextView = findViewById(R.id.tv_leader_name);
        TextView sum_member = findViewById(R.id.tv_sum_member);

        myName = Paper.book().read(Prevalent.currentOnlineUser);

        Button addMemberButton = findViewById(R.id.btn_add_member);
        Button deleteGroupButton = findViewById(R.id.btn_delete_group);
        Button leaveGroupButton = findViewById(R.id.btn_leave_group);

        db = FirebaseDatabase.getInstance().getReference();
        db.child("Group").child(group.getGroupID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    //group = snapshot.getValue(Group.class);
                    //assert group != null;
                    group.setName(snapshot.child("name").getValue(String.class));
                    group.setLeaderName(snapshot.child("leaderName").getValue(String.class));
                    group.setNumberOfMember(snapshot.child("numberOfMember").getValue(Integer.class));
                    nameTextView.setText(group.getName());
                    sum_member.setText(String.valueOf(group.getNumberOfMember()));
                    String leaderName = group.getLeaderName();
                    leaderTextView.setText(leaderName);
                    isLeader = myName.equals(leaderName);
                    leaveGroupButton.setVisibility(View.VISIBLE);
                    if (isLeader){
                        addMemberButton.setVisibility(View.VISIBLE);
                        deleteGroupButton.setVisibility(View.VISIBLE);
                    }

                    // RecyclerView
                    RecyclerView recyclerView = findViewById(R.id.recyclerview_member);
                    recyclerView.setLayoutManager(new LinearLayoutManager(DetailGroupActivity.this));

                    DatabaseReference userRef = db.child("User");
                    DatabaseReference memberRef = db.child("Group Member").child(group.getGroupID());
                    FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setIndexedQuery(memberRef,userRef,User.class).build();
                    FirebaseRecyclerAdapter<User, UserViewHolder> adapter=new FirebaseRecyclerAdapter<User, UserViewHolder>(options){
                        @Override
                        protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
                            String member = model.getName();
                            holder.textView.setText(member);
                            String imageUrl = model.getImageURL();
                            if (!imageUrl.equals("")){ Picasso.get().load(imageUrl).into(holder.imageView);}
                            //Log.e("Warn","member: "+ member);
                            if (isLeader) {
                                if (!leaderName.equals(member)) {  //ko click vao ten leader de xoa thanh vien dc
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            askForRemoveMember(member);
                                        }
                                    });
                                }
                            }
                        }

                        @NonNull
                        @Override
                        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false));
                        }
                    };

                    recyclerView.setAdapter(adapter);
                    adapter.startListening();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        deleteGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ko can if (isLeader) do la leader moi hien button delete
                    askForDeleteGroup();
            }
        });

        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailGroupActivity.this,AddMemberActivity.class);
                intent.putExtra("groupID",group.getGroupID());
                startActivity(intent);
            }
        });

        leaveGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    askForLeaveGroup();
            }
        });


    }


    private void askForRemoveMember(String memberName){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("Xác định xóa thành viên " + memberName+"?");
        b.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {  // OK
            public void onClick(DialogInterface dialog, int id) {
                removeMember(memberName);
            }
        });
        b.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {     // Canel
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        //AlertDialog al = b.create();
        //al.show();
        b.show();
    }

    private void removeMember(String memberName){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Group/"+group.getGroupID()+"/numberOfMember", ServerValue.increment(-1));
        hashMap.put("Group Member/"+ group.getGroupID() +"/"+memberName,null);
        //hashMap.put("User Group/"+ memberName +"/"+groupID,null);
        hashMap.put("User Chat/"+memberName+"/"+group.getGroupID(),null);
        db.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(DetailGroupActivity.this,"Xóa thành viên thành công",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailGroupActivity.this,"Xóa thành viên thất bại",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void askForDeleteGroup(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("Xác định xóa nhóm ?");
        b.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteGroup();
            }
        });
        b.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {     // Canel
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        b.show();
    }
    private void deleteGroup(){
        db.child("Group Member").child(group.getGroupID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("Group/"+ group.getGroupID(), null);
                    hashMap.put("Group Member/"+ group.getGroupID(),null);
                    hashMap.put("Message/"+ group.getGroupID(),null );
                    hashMap.put("Chat/"+ group.getGroupID(),null);
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String memberName = ds.getKey();
                       // hashMap.put("User Group/"+ memberName +"/"+groupID,null);
                        hashMap.put("User Chat/"+memberName+"/"+group.getGroupID(),null);
                    }
                    db.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(DetailGroupActivity.this,"Xóa nhóm thành công",Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(DetailGroupActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            } else {
                                Toast.makeText(DetailGroupActivity.this,"Xóa nhóm thất bại",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void askForLeaveGroup() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("Xác định rời nhóm ?");
        b.setPositiveButton("Rời nhóm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isLeader) {
                    Intent intent = new Intent(DetailGroupActivity.this, LeaderLeaveGroupActivity.class);
                    intent.putExtra("groupID",group.getGroupID());
                    startActivity(intent);
                } else {
                    leaveGroup();
                }
            }
        });
        b.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {     // Canel
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        b.show();
    }
    private void leaveGroup(){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Group/"+ group.getGroupID()+"/numberOfMember", ServerValue.increment(-1));
        hashMap.put("Group Member/"+group.getGroupID()+"/"+myName,null);
     //   hashMap.put("User Group/"+ myName +"/"+groupID,null);
        hashMap.put("User Chat/"+myName+"/"+group.getGroupID(),null);

        db.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(DetailGroupActivity.this,"Rời nhóm thành công",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DetailGroupActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(DetailGroupActivity.this,"Rời nhóm thất bại",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
package com.thiPNA219166.onlinechatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.thiPNA219166.onlinechatapp.Model.Group;
import com.thiPNA219166.onlinechatapp.Model.User;
import com.thiPNA219166.onlinechatapp.Prevalent.DateTime;
import com.thiPNA219166.onlinechatapp.Prevalent.Prevalent;
import com.thiPNA219166.onlinechatapp.R;
import com.thiPNA219166.onlinechatapp.ViewHolder.MemberViewHolder;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.paperdb.Paper;

public class AddGroupActivity extends AppCompatActivity {
    //private Group group;
    private TextInputLayout nameTextLayout;
    private TextInputEditText nameEditText;
    private TextView sumMemberTextView;
    private int sumMember;
    private ProgressBar loadingbar;
    private DatabaseReference db;
    private final HashMap<String, Boolean> listMemberName= new HashMap<String, Boolean>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        db = FirebaseDatabase.getInstance().getReference();
        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_add_group);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.add_group);

        // nameEditText
        nameTextLayout = findViewById(R.id.name_container);
        nameEditText = findViewById(R.id.name_input);

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() ==0) {
                    nameTextLayout.setError("Không để trống tên nhóm");
                } else {
                    nameTextLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Button button = findViewById(R.id.btn_add_group);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addGroup();
            }
        });

        sumMemberTextView = findViewById(R.id.sum_member);
         sumMember = 1;
        loadingbar = findViewById(R.id.loadingbar);


        // recyclerview
        String username = Paper.book().read(Prevalent.currentOnlineUser);
        //firebase

        DatabaseReference userRef= db.child("User");
        assert username != null;
        Query friendQuery = db.child("Friend").child(username);
        //recyclerview
        RecyclerView mRecyclerView = findViewById(R.id.recyclerview_member);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<User> options=new FirebaseRecyclerOptions.Builder<User>().setIndexedQuery(friendQuery,userRef,User.class).build();
        FirebaseRecyclerAdapter<User, MemberViewHolder> adapter=new FirebaseRecyclerAdapter<User, MemberViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull MemberViewHolder holder, int position, @NonNull final User model) {
                String name = model.getName();
                holder.textView.setText(name);
                String url = model.getImageURL();
                if (!url.equals("")) {
                    Picasso.get().load(url).into(holder.imageView);
                }
                if (listMemberName.containsKey(name)) { holder.checkBox.setChecked(true);}
                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b){ //b == holder.checkBox.isChecked()
                            sumMember ++;
                            listMemberName.put(name,true);

                        }
                        else {
                            sumMember --;
                            listMemberName.remove(name);// ko dùng listMemberName.replace(model.getName(), false) do can lay key la ten thanh vien
                        }
                        sumMemberTextView.setText(String.valueOf(sumMember));
                    }
                });

            }

            @NonNull
            @Override
            public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                return new MemberViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item,parent,false));
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    private void addGroup(){
        String name = Objects.requireNonNull(nameEditText.getText()).toString().trim();

        if (TextUtils.isEmpty(name)){
            nameTextLayout.setError("Không để trống tên nhóm");
        } else if (sumMember <2 ) {
            Toast.makeText(this,"Nhóm phải có từ 2 thành viên",Toast.LENGTH_SHORT).show();
        } else {
            loadingbar.setVisibility(View.VISIBLE);

            Group group = new Group();
            String leaderName = Paper.book().read(Prevalent.currentOnlineUser); // ten leader la ten ng dung
            listMemberName.put(leaderName,true);
            group.setName(name);
            group.setLeaderName(leaderName);
            group.setNumberOfMember(sumMember);
            checkGroup(group);
        }
    }

    private void checkGroup(Group group){
        Query query = db.child("Group").orderByChild("name").equalTo(group.getName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                   loadingbar.setVisibility(View.GONE);
                   nameTextLayout.setError("Trùng tên nhóm");
                } else {
                    saveGroup(group);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private  void saveGroup(Group group){
        // lay DS key (ten thanh vien trong nhom) tu Hashmap
        List<String> listOfKeys = new ArrayList<String>(listMemberName.keySet());
        

        String groupID = db.child("Group").push().getKey();
        group.setGroupID(groupID);

        // tao 1 hoi thoai chat co chatID = groupID
        Chat groupChat = new Chat(groupID,group.getName(),"Hãy bắt đầu trò chuyện", DateTime.getDatetimeNow());
        HashMap<String, Object> groupDataMap = new HashMap<>();
        groupDataMap.put("Group/"+ groupID,group);
        groupDataMap.put("Chat/"+groupID,groupChat);
        for (String item: listOfKeys) {   // moi item la ten 1 thanh vien trg nhom
            groupDataMap.put("Group Member/"+groupID+"/"+item,true);
            //groupDataMap.put("User Group/"+ item+"/"+groupID,true);
            groupDataMap.put("User Chat/"+item+"/"+groupID,true);
        }
        db.updateChildren(groupDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingbar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    Toast.makeText(AddGroupActivity.this,"Tạo nhóm thành công", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(AddGroupActivity.this,"Tạo nhóm thất bại", Toast.LENGTH_SHORT).show();
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
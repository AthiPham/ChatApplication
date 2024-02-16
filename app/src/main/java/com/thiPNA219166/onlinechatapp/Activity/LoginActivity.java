package com.thiPNA219166.onlinechatapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import io.paperdb.Paper;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thiPNA219166.onlinechatapp.Model.User;
import com.thiPNA219166.onlinechatapp.Prevalent.Prevalent;
import com.thiPNA219166.onlinechatapp.R;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    //private User user;
    private TextInputEditText nameEditText, passwordEditText;

    private TextInputLayout nameTextLayout, passwordTextLayout;

    private ProgressBar loadingbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        loadingbar = findViewById(R.id.loadingbar);
        Button loginButton=findViewById(R.id.btn_login);
        nameEditText=findViewById(R.id.name_input);
        passwordEditText=findViewById(R.id.password_input);
        nameTextLayout = findViewById(R.id.name_container);
        passwordTextLayout = findViewById(R.id.password_container);
        TextView registerTextView = findViewById(R.id.tv_register);

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() ==0) {
                    nameTextLayout.setError("Không để trống tên người dùng");
                } else {
                    nameTextLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
       // nameEditText.onKeyDown()

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() ==0){
                    passwordTextLayout.setError("Không để trống mật khẩu");
                } else {
                    passwordTextLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn();
            }
        });
        registerTextView.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this,RegisterActivity.class)));

        //paper io for storage
        Paper.init(this);

    }

    private void logIn() {
        String name = Objects.requireNonNull(nameEditText.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();
        if (TextUtils.isEmpty(name)) {
            nameTextLayout.setError("Không để trống tên người dùng");
        } else if (TextUtils.isEmpty(password)) {
            passwordTextLayout.setError("Không để trống mật khẩu");
        }else {
            loadingbar.setVisibility(View.VISIBLE);
            User user = new User(name,password,"");
            checkUser(user);
        }

    }

    private void checkUser(User user) {

        final DatabaseReference userRef;
        userRef = FirebaseDatabase.getInstance().getReference().child("User").child(user.getName());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingbar.setVisibility(View.GONE);
                if (snapshot.exists()){
                    User dbUser = snapshot.getValue(User.class);
                    assert dbUser != null;
                    if (dbUser.getPassword().equals(user.getPassword())){
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            Paper.book().write(Prevalent.currentOnlineUser,user.getName());

                            Intent intent=new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();

                    } else {
                            Toast.makeText(LoginActivity.this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Không tồn tại người dùng trên", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Error", "loadPost:onCancelled", error.toException());
                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
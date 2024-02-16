package com.thiPNA219166.onlinechatapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
//import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thiPNA219166.onlinechatapp.Model.User;
import com.thiPNA219166.onlinechatapp.R;

//import java.util.HashMap;
import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {

   // private User user;

    private TextInputEditText  nameEditText,passwordEditText1,passwordEditText2;
    private TextInputLayout nameTextLayout, passwordTextLayout1, passwordTextLayout2;

    private ImageView imageView;
    private Uri imageUri;

    private ProgressBar loadingbar;
    private DatabaseReference userRef;
    public static final int GALLERY_PICK_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        loadingbar = findViewById(R.id.loadingbar);
        imageView = findViewById(R.id.user_image);
        Button registerButton = findViewById(R.id.btn_register);
        nameEditText = findViewById(R.id.name_input);
        passwordEditText1 = findViewById(R.id.password_input1);
        passwordEditText2 = findViewById(R.id.password_input2);
        nameTextLayout = findViewById(R.id.name_container);
        passwordTextLayout1 = findViewById(R.id.password_container1);
        passwordTextLayout2 = findViewById(R.id.password_container2);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open gallery trong android
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_PICK_REQUEST_CODE);
            }
        });

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

        passwordEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() ==0){
                    passwordTextLayout1.setError("Không để trống mật khẩu");
                } else {
                    passwordTextLayout1.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passwordEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() ==0){
                    passwordTextLayout2.setError("Không để trống mật khẩu");
                } else {
                    passwordTextLayout2.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register(){

        String name = Objects.requireNonNull(nameEditText.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordEditText1.getText()).toString().trim();
        String re_password = Objects.requireNonNull(passwordEditText2.getText()).toString().trim();


        if (TextUtils.isEmpty(name)) {
            nameTextLayout.setError("Không để trống tên người dùng");
        } else if (TextUtils.isEmpty(password)) {
            passwordTextLayout1.setError("Không để trống mật khẩu");
        }else if (TextUtils.isEmpty(re_password)) {
            passwordTextLayout2.setError("Không để trống xác nhận mật khẩu");
        }else {
            if (re_password.equals(password)) {
                loadingbar.setVisibility(View.VISIBLE);
                User user = new User(name,password,"");
                checkUser(user);
            }
            else {
                passwordTextLayout2.setError("Mật khẩu xác nhận khác nhau");
            }
        }

    }

    private void checkUser(User user) {
        userRef.child(user.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists())) {
                    loadingbar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Lỗi: Tên đã tồn tại", Toast.LENGTH_SHORT).show();

                } else {
                    saveUser(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterActivity.this, "Lỗi: "+ error.getDetails(), Toast.LENGTH_LONG).show();

            }
        });
    }


    private void saveUser(User user) {

        if (imageUri!= null) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("ImageOfUser");

            final StorageReference filePath = imageRef.child( user.getName() +".jpg");

            filePath.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        user.setImageURL(task.getResult().toString());

                        /*
                        // them new user vao Firebase Database
                        HashMap<String, Object> userDataMap = new HashMap<>();
                        userDataMap.put("name", name);
                        userDataMap.put("password", password);
                        userDataMap.put("imageURL", downloadImageUrl);
                       .child(name).updateChildren(userDataMap)
                         */

                        userRef.child(user.getName()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingbar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    } else {
                        loadingbar.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, "Lỗi: Lưu ảnh thất bại",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            /*
            HashMap<String, Object> userDataMap = new HashMap<>();
            userDataMap.put("password", password);
            userDataMap.put("name", name);
            userDataMap.put("imageURL", "");

             */
            userRef.child(user.getName()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    loadingbar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  // back to login in activity
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
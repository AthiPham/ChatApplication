package com.thiPNA219166.onlinechatapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thiPNA219166.onlinechatapp.Activity.LoginActivity;
import com.thiPNA219166.onlinechatapp.Prevalent.Prevalent;
import com.thiPNA219166.onlinechatapp.R;

import io.paperdb.Paper;

public class UserFragment extends Fragment {

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = view.findViewById(R.id.user_image);
        TextView textView = view.findViewById(R.id.tv_user_name);
        Button button = view.findViewById(R.id.btn_logout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Paper.book().delete(Prevalent.currentOnlineUser);
                startActivity(intent);
                getActivity().finish();
            }
        });

        String currentUser= Paper.book().read(Prevalent.currentOnlineUser);
        assert currentUser != null;
        DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("User").child(currentUser);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imageUrl = snapshot.child("imageURL").getValue(String.class);
                assert imageUrl != null;
                if (!imageUrl.equals("")){Picasso.get().load(imageUrl).into(imageView);}
                String name = snapshot.child("name").getValue(String.class);
                textView.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
package com.thiPNA219166.onlinechatapp.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.thiPNA219166.onlinechatapp.R;

public class FriendRequestViewHolder extends RecyclerView.ViewHolder {

    public TextView nameTextView, timeTextView, contentTextView, statusTextView;
    public ImageView imageView;
    public Button acceptButton, denyButton;
    public FriendRequestViewHolder(View itemView){
        super(itemView);
        nameTextView = itemView.findViewById(R.id.tv_user_name);
        contentTextView = itemView.findViewById(R.id.tv_content);
        timeTextView = itemView.findViewById(R.id.tv_datetime);
        statusTextView = itemView.findViewById(R.id.tv_status);
        imageView = itemView.findViewById(R.id.image_user);
        acceptButton = itemView.findViewById(R.id.btn_accept_request);
        denyButton = itemView.findViewById(R.id.btn_deny_request);
    }
}

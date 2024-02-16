package com.thiPNA219166.onlinechatapp.ViewHolder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thiPNA219166.onlinechatapp.R;

public class MemberViewHolder extends RecyclerView.ViewHolder {
    public CheckBox checkBox;
    public TextView textView;
    public ImageView imageView;
    public MemberViewHolder(@NonNull View itemView) {
        super(itemView);
         checkBox = itemView.findViewById(R.id.checkbox_member);
         textView = itemView.findViewById(R.id.name_user);
         imageView = itemView.findViewById(R.id.image_user);
    }
}

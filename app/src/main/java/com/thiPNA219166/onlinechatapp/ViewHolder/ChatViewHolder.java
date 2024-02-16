package com.thiPNA219166.onlinechatapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.thiPNA219166.onlinechatapp.Interface.ItemClickListener;
import com.thiPNA219166.onlinechatapp.R;

public class ChatViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/{
    public TextView nameTextView, contentTextView,timeTextView;
    public ImageView imageView;
    //public ItemClickListener itemClickListner;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.tv_name);
        contentTextView = itemView.findViewById(R.id.tv_message_content);
        timeTextView = itemView.findViewById(R.id.tv_message_datetime);
        imageView = itemView.findViewById(R.id.image);
    }
/*
    public void setItemClickListner(ItemClickListener itemClickListner) {
        this.itemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onItemClick(v, getAbsoluteAdapterPosition(), false);
    }

 */
}

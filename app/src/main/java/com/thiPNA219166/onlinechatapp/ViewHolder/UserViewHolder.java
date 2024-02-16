package com.thiPNA219166.onlinechatapp.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
//import com.thiPNA219166.onlinechatapp.Interface.ItemClickListener;
import com.thiPNA219166.onlinechatapp.R;


public class UserViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

    public TextView textView;
    public ImageView imageView;
    public Button button;
    //public ItemClickListener itemClickListner;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.name_user);
        imageView = itemView.findViewById(R.id.image_user);
        button = itemView.findViewById(R.id.btn_add_friend);
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

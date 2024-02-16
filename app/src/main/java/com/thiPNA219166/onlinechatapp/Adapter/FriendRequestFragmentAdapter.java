package com.thiPNA219166.onlinechatapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.thiPNA219166.onlinechatapp.Fragment.ReceiveFriendRequestFragment;
import com.thiPNA219166.onlinechatapp.Fragment.SendFriendRequestFragment;

public class FriendRequestFragmentAdapter extends FragmentStateAdapter {
    public FriendRequestFragmentAdapter(@NonNull FragmentManager fragmentManager,
                                        @NonNull Lifecycle lifecycle ) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if (position == 0){
            return new SendFriendRequestFragment();
        }
        return new ReceiveFriendRequestFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}

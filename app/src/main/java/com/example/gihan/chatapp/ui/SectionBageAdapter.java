package com.example.gihan.chatapp.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Gihan on 7/16/2017.
 */

class SectionBageAdapter extends FragmentPagerAdapter{


    public SectionBageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                RequestsFragment requestsFragmentt=new RequestsFragment();
                return requestsFragmentt;
            case 1:
                ChatFragment chatsFragment=new ChatFragment();
                return chatsFragment;
            case 2:
                FriendsFragment friendsFragment=new FriendsFragment();
                return friendsFragment;
            default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return 3;
    }
    public CharSequence getPageTitle(int postion){

        switch (postion){
            case 0 :
                return "Request";
            case 1:
                return "Chat";
            case 2:
                return "Friends";
            default:
                return null;
        }
    }
}

package com.example.mnemosyne.chat;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by mnemosyne on 2016/3/9.
 */
public class ListsFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 4;
    private Context context;
    private ArrayList<String> usrArlst, frdArlst, myReqArlst, recvReqArlst, onlineList;
    private static Set<String> newLogo;
    private ListFragment userFragment, friendFragment, myRequestFragment, receivedRequestFragment;

    public ListsFragmentPagerAdapter(FragmentManager fm, Context context,
                                     ArrayList<String> usrArlst,
                                     ArrayList<String> frdArlst,
                                     ArrayList<String> myReqArlst,
                                     ArrayList<String> recvReqArlst,
                                     ArrayList<String> onlineList,
                                     Set<String> newLogo){
        super(fm);
        this.usrArlst = usrArlst;
        this.frdArlst = frdArlst;
        this.myReqArlst = myReqArlst;
        this.recvReqArlst = recvReqArlst;
        this.onlineList = onlineList;
        this.context = context;
        this.newLogo = newLogo;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    public void notifyDataChange(){
        notifyChange(userFragment);
        notifyChange(friendFragment);
        notifyChange(myRequestFragment);
        notifyChange(receivedRequestFragment);
    }

    private void notifyChange(ListFragment listFragment){
        if(listFragment != null)
            listFragment.notifyDataChange();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                userFragment = ListFragment.newInstance(position, usrArlst, frdArlst, context);
                return userFragment;
            case 1:
                friendFragment = ListFragment.newInstance(position, frdArlst, onlineList, newLogo, context);
                return friendFragment;
            case 2:
                myRequestFragment = ListFragment.newInstance(position, myReqArlst, context);
                return myRequestFragment;
            case 3:
                receivedRequestFragment = ListFragment.newInstance(position, recvReqArlst, context);
                return receivedRequestFragment;
            default:
                return userFragment;
        }
    }

    @Override
    public String getPageTitle(int position) {
        switch (position){
            case 0:
                return "User List";
            case 1:
                return "Friend List";
            case 2:
                return "My Request List";
            case 3:
                return "Received Request";
            default:
                return "User List";
        }
    }
}

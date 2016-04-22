package com.example.mnemosyne.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by mnemosyne on 2016/3/9.
 */
public class FriendListRowAdapter extends BaseAdapter {
    ArrayList<String> frdArlst, onlineList;
    private static Set<String> newLogo;
    private LayoutInflater layoutInflater;
    FriendListRowAdapter(Context context, ArrayList<String> frdArlst, ArrayList<String> onlineList, Set<String> newLogo){
        layoutInflater = LayoutInflater.from(context);
        this.onlineList = onlineList;
        this.frdArlst = frdArlst;
        this.newLogo = newLogo;
    }
    @Override
    public int getCount() {
        return frdArlst.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.friend_list_row_with_head, null);
        TextView friendAccount = (TextView)convertView.findViewById(R.id.friendAccount);
        TextView onOffline = (TextView)convertView.findViewById(R.id.isOnline);
        TextView lastSentence = (TextView)convertView.findViewById(R.id.lastSentence);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.light);
        ImageView newLogoImage = (ImageView)convertView.findViewById(R.id.newLogo);
        friendAccount.setText(frdArlst.get(position));
        lastSentence.setText(Dialogs.getInstance().getLastSentences(frdArlst.get(position)));
        if(onlineList.contains(frdArlst.get(position))){
            onOffline.setText("Online");
            imageView.setBackgroundResource(R.drawable.online);
        }
        else{
            onOffline.setText("Offline");
            imageView.setBackgroundResource(R.drawable.offline);
        }
        if(newLogo.contains(frdArlst.get(position)))
            newLogoImage.setBackgroundResource(R.drawable.newlogo);
        else
            newLogoImage.setBackgroundColor(0X0FFFFFFF);
        if(position % 2 == 0)
            convertView.setBackgroundColor(0X0FFF5151);
        else
            convertView.setBackgroundColor(0X0FFFFFFF);
        return convertView;
    }
}

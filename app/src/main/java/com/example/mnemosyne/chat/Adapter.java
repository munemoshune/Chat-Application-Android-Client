package com.example.mnemosyne.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mnemosyne on 2016/3/7.
 */
public class Adapter extends BaseAdapter {
    ArrayList<String> usrArlst, frdArlst;
    private LayoutInflater layoutInflater;
    Adapter(Context context, ArrayList<String> usrArlst, ArrayList<String> frdArlst){
        layoutInflater = LayoutInflater.from(context);
        this.usrArlst = usrArlst;
        this.frdArlst = frdArlst;
    }

    @Override
    public int getCount() {
        return usrArlst.size();
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
        View thisRow = convertView;
        if(thisRow == null){
            thisRow = layoutInflater.inflate(R.layout.user_list_row, null);
        }
        TextView friendAccount = (TextView)thisRow.findViewById(R.id.userAccount);
        TextView isFriend = (TextView)thisRow.findViewById(R.id.isFriend);
        friendAccount.setText(usrArlst.get(position));
        if(frdArlst.contains(usrArlst.get(position)))
            isFriend.setText("Friend");
        else
            isFriend.setText("");
        if(position % 2 == 0)
            thisRow.setBackgroundColor(0X0FFF5151);
        else
            thisRow.setBackgroundColor(0X0FFFFFFF);
        return thisRow;
    }
}

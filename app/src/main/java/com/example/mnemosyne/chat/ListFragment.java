package com.example.mnemosyne.chat;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by mnemosyne on 2016/3/9.
 */
public class ListFragment extends Fragment {

    private int position;
    private Context context;
    private ArrayList<String> accountList = new ArrayList<>();
    private ArrayList<String> infoList = null;
    private static Set<String> newLogo;
    private BaseAdapter adapter = null;
    private View view;

    public static ListFragment newInstance(int position, ArrayList<String> accountList, ArrayList<String> infoList, Set<String> newLogo, Context context){
        ListFragment listFragment = new ListFragment();
        listFragment.position = position;
        listFragment.accountList = accountList;
        listFragment.infoList = infoList;
        listFragment.context = context;
        listFragment.newLogo = newLogo;
        return listFragment;
    }

    public static ListFragment newInstance(int position, ArrayList<String> accountList, ArrayList<String> infoList, Context context){
        ListFragment listFragment = new ListFragment();
        listFragment.position = position;
        listFragment.accountList = accountList;
        listFragment.infoList = infoList;
        listFragment.context = context;
        return listFragment;
    }

    public static ListFragment newInstance(int position, ArrayList<String> accountList, Context context){
        ListFragment listFragment = new ListFragment();
        listFragment.position = position;
        listFragment.accountList = accountList;
        listFragment.context = context;
        return listFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void notifyDataChange(){
        adapter.notifyDataSetChanged();
    }

    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.list_fragment, container, false);
        ListView listView = (ListView) view.findViewById(R.id.fragmentListView);
        if(adapter == null) {
            switch (position){
                case 0:
                    adapter = new Adapter(context, accountList, infoList);
                    break;
                case 1:
                    adapter = new FriendListRowAdapter(context, accountList, infoList, newLogo);
                    break;
                case 2:
                    adapter = new ArrayAdapter<String>(context, android.R.layout.simple_expandable_list_item_1, accountList);
                    break;
                case 3:
                    adapter = new ArrayAdapter<String>(context, android.R.layout.simple_expandable_list_item_1, accountList);
                    break;
            }
        }
        listView.setAdapter(adapter);
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                menu.setHeaderTitle("What to do with " + accountList.get((int) info.id));
                switch (position) {
                    case 0:
                        if(!infoList.contains(accountList.get((int) info.id)))
                            menu.add("Add");
                        break;
                    case 1:
                        menu.add("Delete friendship");
                        menu.add("Chat");
                        break;
                    case 2:
                        menu.add("Delete my request");
                        break;
                    case 3:
                        menu.add("Delete request");
                        menu.add("Approve request");
                        break;
                    default:
                        break;
                }
            }
        });
        return view;
    }

}

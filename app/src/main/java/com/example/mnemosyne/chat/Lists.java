package com.example.mnemosyne.chat;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Lists extends AppCompatActivity {
    private String account, password, lists;
    private Integer selectedItem;
    private ArrayList<String> usrArlst, frdArlst, myReqArlst, recvReqArlst, onlineList;
    private ListView userListView;
    private String[] optMnu = {"Log out", "User list", "Friend list", "My request", "Received request"};
    private ArrayAdapter ad;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);
        Bundle bundle = getIntent().getExtras();
        account = bundle.getString("account");
        password = bundle.getString("password");
        lists = bundle.getString("Lists");
        userListView = (ListView)findViewById(R.id.listView);
        getAllInfo(lists);
        setTitle("Friend list");
        //refreshListView(frdArlst);
        //userListView.setAdapter(new FriendListRowAdapter(this, onlineList, frdArlst));
        if(!Dialogs.getInstance().isInitial())
            requestDialogs();
        userListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                menu.setHeaderTitle("What to do with " + usrArlst.get((int) info.id));
                selectedItem = (int) info.id;
                menu.add("Delete friendship");
                menu.add("Chat");
            }
        });
    }
    private void requestDialogs(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", account);
            jsonObject.put("action", "requestDialogs");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SocketSpeaker(jsonObject).start();
    }
    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            try {
                JSONObject jsonObject = new JSONObject((String)message.obj);
                if(!jsonObject.get("data").equals("empty"));
                    Dialogs.getInstance().putDialog(jsonObject.getString("target"), jsonObject.getJSONArray("data"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    public boolean onKeyDown(int kCode,KeyEvent kEvent){
        if(kCode == KeyEvent.KEYCODE_BACK);
        return true;
    }
    void getAllInfo(String data){
        try {
            JSONObject jsonObject = new JSONObject(data);
            usrArlst = jsonArrayToArrayList(jsonObject.getJSONArray("userList"));
            frdArlst = jsonArrayToArrayList(jsonObject.getJSONArray("frdList"));
            myReqArlst = jsonArrayToArrayList(jsonObject.getJSONArray("myReqList"));
            recvReqArlst = jsonArrayToArrayList(jsonObject.getJSONArray("rcvReqList"));
            onlineList = jsonArrayToArrayList(jsonObject.getJSONArray("onlineList"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    ArrayList<String> jsonArrayToArrayList(JSONArray jsonArray) throws Exception{
        ArrayList<String> arrayList = new ArrayList<String>();
        if(jsonArray.length() != 0){
            for(int i = 0; i < jsonArray.length(); i ++){
                arrayList.add(jsonArray.get(i).toString());
            }
        }
        return arrayList;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        for(int i = 0; i < optMnu.length; i++) {
            menu.add(Menu.NONE, i, Menu.NONE, optMnu[i]);
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getTitle().toString()) {
            case "Log out":
                logOut();
                break;
            case "User list":
                setTitle("User list");
                //refreshListView(usrArlst);
                Adapter adapter = new Adapter(this, usrArlst, frdArlst);
                userListView.setAdapter(adapter);
               /* adapter.notifyDataSetChanged();*/
                userListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                        menu.setHeaderTitle("What to do with " + usrArlst.get((int) info.id));
                        selectedItem = (int) info.id;
                        menu.add("Add");
                    }
                });
                break;
            case "My request":
                setTitle("My request");
                refreshListView(myReqArlst);
                userListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                        menu.setHeaderTitle("What to do with " + usrArlst.get((int) info.id));
                        selectedItem = (int) info.id;
                        menu.add("Delete my request");
                    }
                });
                break;
            case "Friend list":
                setTitle("Friend list");
                //refreshListView(frdArlst);
                //userListView.setAdapter(new FriendListRowAdapter(this, onlineList, frdArlst));
                userListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                        menu.setHeaderTitle("What to do with " + usrArlst.get((int) info.id));
                        selectedItem = (int) info.id;
                        menu.add("Delete friendship");
                        menu.add("Chat");
                    }
                });
                break;
            case "Received request":
                setTitle("Received request");
                refreshListView(recvReqArlst);
                userListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                        menu.setHeaderTitle("What to do with " + usrArlst.get((int) info.id));
                        selectedItem = (int) info.id;
                        menu.add("Delete request");
                        menu.add("Approve request");
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    void setUsrArlst(){

    }
    void logOut(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "logOut");
            jsonObject.put("account", account);
            new SocketSpeaker(jsonObject).start();
            Intent intent = new Intent();
            intent.setClass(Lists.this, MainActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void refreshListView(ArrayList<String> list){
        if(list == null)
            ad = new ArrayAdapter<String>(Lists.this, android.R.layout.simple_expandable_list_item_1, new ArrayList<String>());
        else
            ad = new ArrayAdapter<String>(Lists.this, android.R.layout.simple_expandable_list_item_1, list);
        userListView.setAdapter(ad);
        ad.notifyDataSetChanged();
    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", account);
            switch (item.getTitle().toString()) {
                case "Add":
                    jsonObject.put("action", "sendReq");
                    jsonObject.put("target", usrArlst.get(selectedItem));
                    if(!myReqArlst.contains(usrArlst.get(selectedItem)) &&
                            !recvReqArlst.contains(usrArlst.get(selectedItem)) &&
                            !frdArlst.contains(usrArlst.get(selectedItem))){
                        myReqArlst.add(usrArlst.get(selectedItem));
                    }
                    new SocketSpeaker(jsonObject).start();
                    break;
                case "Delete my request":
                    jsonObject.put("action", "removeMyReq");
                    jsonObject.put("target", myReqArlst.get(selectedItem));
                    myReqArlst.remove(myReqArlst.get(selectedItem));
                    refreshListView(myReqArlst);
                    new SocketSpeaker(jsonObject).start();
                    break;
                case "Delete friendship":
                    jsonObject.put("action", "removeFrd");
                    jsonObject.put("target", frdArlst.get(selectedItem));
                    frdArlst.remove(frdArlst.get(selectedItem));
                    refreshListView(frdArlst);
                    new SocketSpeaker(jsonObject).start();
                    break;
                case "Approve request":
                    jsonObject.put("action", "approveReq");
                    jsonObject.put("target", recvReqArlst.get(selectedItem));
                    frdArlst.add(recvReqArlst.get(selectedItem));
                    recvReqArlst.remove(recvReqArlst.get(selectedItem));
                    refreshListView(recvReqArlst);
                    Log.e("AR", jsonObject.toString());
                    new SocketSpeaker(jsonObject).start();
                    break;
                case "Delete request":
                    jsonObject.put("action", "removeRcvReq");
                    jsonObject.put("target", recvReqArlst.get(selectedItem));
                    recvReqArlst.remove(recvReqArlst.get(selectedItem));
                    refreshListView(recvReqArlst);
                    new SocketSpeaker(jsonObject).start();
                    break;
                case "Chat":
                    goToChatRoom();
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
    void goToChatRoom(){
        Bundle bundle = new Bundle();
        bundle.putString("account", account);
        bundle.putString("password", password);
        bundle.putString("target", frdArlst.get(selectedItem));
        bundle.putString("Lists", lists);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(Lists.this, ChatRoom.class);
        startActivity(intent);
    }
}

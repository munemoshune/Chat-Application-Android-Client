package com.example.mnemosyne.chat;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class SlidingLists extends AppCompatActivity {
    private static ArrayList<String> usrArlst, frdArlst, myReqArlst, recvReqArlst, onlineList;
    private static Set<String> newLogo;
    private String account, password, lists;
    private static ListsFragmentPagerAdapter listsFragmentPagerAdapter;
    private int selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_lists);

        Bundle bundle = getIntent().getExtras();
        account = bundle.getString("account");
        password = bundle.getString("password");
        lists = bundle.getString("Lists");
        setTitle("Welcome!!     " + account);
        if(bundle.getString("from").equals("Main"))
            getAllInfo(lists);
        else
            newLogo.remove(bundle.getString("from"));

        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        listsFragmentPagerAdapter = new ListsFragmentPagerAdapter(getSupportFragmentManager(), this, usrArlst, frdArlst, myReqArlst, recvReqArlst, onlineList, newLogo);
        viewPager.setAdapter(listsFragmentPagerAdapter);
        viewPager.setCurrentItem(1);
        PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip)findViewById(R.id.tabs);
        pagerSlidingTabStrip.setViewPager(viewPager);
        if(!Dialogs.getInstance().isInitial())
            requestDialogs();

        handler = new Handler(){
            @Override
            public void handleMessage(Message message){
                try {
                    Log.e("Slide Handler", (String)message.obj);
                    JSONObject jsonObject = new JSONObject((String)message.obj);
                    switch (jsonObject.getString("dataType")){
                        case "friendOffline":
                            Log.e(jsonObject.getString("account"), "is Offline");
                            onlineList.remove(jsonObject.getString("account"));
                            listsFragmentPagerAdapter.notifyDataChange();
                            Toast.makeText(SlidingLists.this, jsonObject.getString("account") +  " is offline.", Toast.LENGTH_LONG).show();
                            break;
                        case "friendOnline":
                            Log.e(jsonObject.getString("account"), "is Online");
                            if(!onlineList.contains(jsonObject.getString("account")))
                                onlineList.add(jsonObject.getString("account"));
                            listsFragmentPagerAdapter.notifyDataChange();
                            Toast.makeText(SlidingLists.this, jsonObject.getString("account") +  " is online.", Toast.LENGTH_LONG).show();
                            break;
                        case "dialog":
                            if(!jsonObject.get("data").equals("empty"));
                            Dialogs.getInstance().putDialog(jsonObject.getString("target"), jsonObject.getJSONArray("data"));
                            listsFragmentPagerAdapter.notifyDataChange();
                            break;
                        case "sendReqNotice":
                            recvReqArlst.add(jsonObject.getString("account"));
                            if(!onlineList.contains(jsonObject.getString("account")))
                                onlineList.add(jsonObject.getString("account"));
                            listsFragmentPagerAdapter.notifyDataChange();
                            break;
                        case "reqApprovedNotice":
                            myReqArlst.remove(jsonObject.getString("account"));
                            frdArlst.add(jsonObject.getString("account"));
                            if(!onlineList.contains(jsonObject.getString("account")))
                                onlineList.add(jsonObject.getString("account"));
                            listsFragmentPagerAdapter.notifyDataChange();
                            break;
                        case "removeMyReqNotice":
                            recvReqArlst.remove(jsonObject.getString("account"));
                            listsFragmentPagerAdapter.notifyDataChange();
                            break;
                        case "removeRcvReqNotice":
                            myReqArlst.remove(jsonObject.getString("account"));
                            listsFragmentPagerAdapter.notifyDataChange();
                            break;
                        case "removeFriendNotice":
                            frdArlst.remove(jsonObject.getString("account"));
                            onlineList.remove(jsonObject.getString("account"));
                            listsFragmentPagerAdapter.notifyDataChange();
                            break;
                        case "newMsg":
                            Log.e("Slide", "newMsg");
                            newLogo.add(jsonObject.getString("account"));
                            listsFragmentPagerAdapter.notifyDataChange();
                            //Toast.makeText(SlidingLists.this, jsonObject.getString("account") +  " : " + jsonObject.getString("Msg"), Toast.LENGTH_LONG).show();
                            break;
                        case "newUser":
                            usrArlst.add(jsonObject.getString("account"));
                            listsFragmentPagerAdapter.notifyDataChange();
                            Toast.makeText(SlidingLists.this, jsonObject.getString("account") +  " signed up!!!", Toast.LENGTH_LONG).show();
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
    public static Handler notifyDataChangeHandler = new Handler();
    public static Runnable notifyDataChange = new Runnable() {
        @Override
        public void run() {
            listsFragmentPagerAdapter.notifyDataChange();
        }
    };
    public static Handler handler;
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
            newLogo = new HashSet<String>();
            listsFragmentPagerAdapter.notifyDataChange();
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
        menu.add(Menu.NONE, 0, Menu.NONE, "Log out");
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getTitle().toString()) {
            case "Log out":
                logOut();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    void logOut(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "logOut");
            jsonObject.put("account", account);
            new SocketSpeaker(jsonObject).start();
            Intent intent = new Intent();
            intent.setClass(SlidingLists.this, MainActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        selectedItem = (int) info.id;
        JSONObject jsonObject = new JSONObject();
        try {
            int which = -1;
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
                    new SocketSpeaker(jsonObject).start();
                    which = 0;
                    break;
                case "Delete friendship":
                    jsonObject.put("action", "removeFrd");
                    jsonObject.put("target", frdArlst.get(selectedItem));
                    onlineList.remove(frdArlst.get(selectedItem));
                    frdArlst.remove(frdArlst.get(selectedItem));
                    new SocketSpeaker(jsonObject).start();
                    which = 0;
                    break;
                case "Approve request":
                    jsonObject.put("action", "approveReq");
                    jsonObject.put("target", recvReqArlst.get(selectedItem));
                    frdArlst.add(recvReqArlst.get(selectedItem));
                    recvReqArlst.remove(recvReqArlst.get(selectedItem));
                    Log.e("AR", jsonObject.toString());
                    new SocketSpeaker(jsonObject).start();
                    which = 0;
                    break;
                case "Delete request":
                    jsonObject.put("action", "removeRcvReq");
                    jsonObject.put("target", recvReqArlst.get(selectedItem));
                    recvReqArlst.remove(recvReqArlst.get(selectedItem));
                    new SocketSpeaker(jsonObject).start();
                    which = 0;
                    break;
                case "Chat":
                    selectedItem = (int) info.id;
                    newLogo.remove(frdArlst.get(selectedItem));
                    goToChatRoom();
                    break;
            }
            if(which != -1)
                listsFragmentPagerAdapter.notifyDataChange();
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
        intent.setClass(SlidingLists.this, ChatRoom.class);
        startActivity(intent);
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


}

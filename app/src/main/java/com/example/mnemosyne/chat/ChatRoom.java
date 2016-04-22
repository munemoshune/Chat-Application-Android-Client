package com.example.mnemosyne.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashSet;

public class ChatRoom extends AppCompatActivity {
    private String account, password, target, lists;
    private String[] optMnu = {"Log out", "Return to Lists"};
    private ArrayList<View> bubbleList;
    RelativeLayout relativeLayout;
    Integer offset = 1;
    static Handler msgHandler;
    EditText msgEditText;
    static private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    static ScrollView scrollView;
    ProgressDialog pd;
    LayoutInflater layoutInflater;
    Button sendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_chat_room);
        initialProcesses();
        msgEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgHandler.post(fullScroll);
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("action", "sendMsg");
                    jsonObject.put("account", account);
                    jsonObject.put("target", target);
                    jsonObject.put("Msg", msgEditText.getText());
                    new SocketSpeaker(jsonObject).start();
                    Dialogs.getInstance().putMsgInDialogWith(jsonObject);
                    singleChatBubbleHandler(jsonObject);
                    msgEditText.setText("");
                    msgHandler.post(fullScroll);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        msgHandler = new Handler(){
            @Override
            public void handleMessage(Message message){
                Log.e("Chat Handler", (String)message.obj);
                try {
                    Log.e("handler Got", (String)message.obj);
                    JSONObject jsonObject = new JSONObject((String)message.obj);
                    if(jsonObject.getString("dataType").equals("timeStamp")){
                        View bubble = bubbleList.get(jsonObject.getInt("Msg#") - 1);
                        TextView time = (TextView)bubble.findViewById(R.id.time);
                        TextView msg = (TextView)bubble.findViewById(R.id.msg);
                        Log.e("Handler sets", "Msg " + msg.getText() + " time = " + jsonObject.getString("time"));
                        time.setText(jsonObject.getString("time"));
                        Dialogs.getInstance().updateTimeStamp(target, jsonObject.getString("time"), jsonObject.getInt("Msg#") - 1);
                    }
                    else if(jsonObject.getString("dataType").equals("newMsg")){
                        singleChatBubbleHandler(jsonObject);
                        msgHandler.post(fullScroll);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        msgHandler.post(fullScroll);
    }
    @Override
    public boolean onKeyDown(int kCode,KeyEvent kEvent){
        if(kCode == KeyEvent.KEYCODE_BACK)
            returnToLists();
        return true;
    }
    public static Runnable fullScroll = new Runnable() {
        @Override
        public void run() {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    };
    void initialProcesses(){
        layoutInflater = LayoutInflater.from(this);
        pd = new ProgressDialog(ChatRoom.this);
        pd.setMessage("Loading dialog...");
        pd.show();
        Bundle bundle = getIntent().getExtras();
        account = bundle.getString("account");
        password = bundle.getString("password");
        target = bundle.getString("target");
        lists = bundle.getString("Lists");
        sendButton = (Button)findViewById(R.id.sendButton);
        msgEditText = (EditText)findViewById(R.id.msgEditText);
        relativeLayout = (RelativeLayout)findViewById(R.id.rl);
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(msgEditText.getWindowToken(),0);
                return false;
            }
        });
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        setTitle("Chat with " + target);
        readDialogToBubbles();
    }
    void readDialogToBubbles(){
        bubbleList = new ArrayList<>();
        if(Dialogs.getInstance().containDialogWith(target))
            chatBubblesHandler(Dialogs.getInstance().getDialogWith(target));
        else {
            Log.e("Got", "No dialog yet");
            pd.dismiss();
        }
    }
    void singleChatBubbleHandler(JSONObject jsonObject) throws Exception{
        Log.e("singl Got", jsonObject.toString());
        View newBubble;
        if(jsonObject.get("account").equals(account))
            newBubble = layoutInflater.inflate(R.layout.bubble_right, null);
        else
            newBubble = layoutInflater.inflate(R.layout.bubble_left, null);
        TextView msg = (TextView)newBubble.findViewById(R.id.msg);
        TextView time = (TextView)newBubble.findViewById(R.id.time);
        msg.setText(jsonObject.getString("Msg"));
        if(jsonObject.has("time"))
            time.setText(jsonObject.getString("time"));
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(WC, WC);
        if (!bubbleList.isEmpty())
            layoutParams.addRule(RelativeLayout.BELOW, bubbleList.get(bubbleList.size() - 1).getId());
        newBubble.setId(offset ++);
        bubbleList.add(newBubble);
        relativeLayout.addView(newBubble, layoutParams);
    }
    void chatBubblesHandler(JSONArray jsonArray){
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                View newBubble;
                JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
                if (jsonObject.getString("account").equals(account)) {
                    newBubble = layoutInflater.inflate(R.layout.bubble_right, null);
                }
                else{
                    newBubble = layoutInflater.inflate(R.layout.bubble_left, null);
                }
                TextView msg = (TextView)newBubble.findViewById(R.id.msg);
                TextView time = (TextView)newBubble.findViewById(R.id.time);
                msg.setText(jsonObject.getString("Msg"));
                time.setText(jsonObject.getString("time"));
                RelativeLayout.LayoutParams layoutParams =
                        new RelativeLayout.LayoutParams(WC, WC);
                if (!bubbleList.isEmpty())
                    layoutParams.addRule(RelativeLayout.BELOW, bubbleList.get(bubbleList.size() - 1).getId());
                newBubble.setId(offset ++);
                bubbleList.add(newBubble);
                relativeLayout.addView(newBubble, layoutParams);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        pd.dismiss();
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
            case "Return to Lists":
                returnToLists();
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
            intent.setClass(ChatRoom.this, MainActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void returnToLists(){
        Bundle bundle = new Bundle();
        bundle.putString("account", account);
        bundle.putString("password", password);
        bundle.putString("from", target);
        bundle.putString("Lists", lists);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(ChatRoom.this, SlidingLists.class);
        startActivity(intent);
    }
}
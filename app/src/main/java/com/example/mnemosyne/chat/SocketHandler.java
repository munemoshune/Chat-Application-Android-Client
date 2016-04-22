package com.example.mnemosyne.chat;

import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mnemosyne on 2016/3/8.
 */
public class SocketHandler extends Thread {
    private String socket;
    SocketHandler(String socket){
        this.socket = socket;
    }
    private NewMsgListener newMsgListener;
    public void setNewMsgListener(NewMsgListener newMsgListener){
        this.newMsgListener = newMsgListener;
    }
    @Override
    public void run(){
        try {
            Message message1, message2;
            JSONObject jsonObject = new JSONObject(socket);
            switch (jsonObject.getString("dataType")){
                case "timeStamp":
                    message1 = new Message();
                    message1.obj = jsonObject.toString();
                    ChatRoom.msgHandler.sendMessage(message1);
                    break;
                case "sendReqNotice":
                case "reqApprovedNotice":
                case "removeMyReqNotice":
                case "removeRcvReqNotice":
                case "removeFriendNotice":
                case "friendOffline":
                case "friendOnline":
                case "dialog":
                    message1 = new Message();
                    message1.obj = jsonObject.toString();
                    SlidingLists.handler.sendMessage(message1);
                    break;
                case "newMsg":
                    Log.e("Got", " a new Msg : " + jsonObject.toString());
                    jsonObject.remove("dataType");
                    Dialogs.getInstance().putMsg(jsonObject);
                    jsonObject.put("dataType", "newMsg");
                    Log.e("new", " Message : ");
                    message1 = new Message();
                    message1.obj = jsonObject.toString();
                    message2 = new Message();
                    message2.obj = jsonObject.toString();
                    Log.e("Send", " Message : " + message1.obj);
                    SlidingLists.handler.sendMessage(message1);
                    ChatRoom.msgHandler.sendMessage(message2);
                    break;
                case "newUser":
                    message1 = new Message();
                    message1.obj = jsonObject.toString();
                    SlidingLists.handler.sendMessage(message1);
                    break;
            }
        } catch (Exception e) {
            Log.e("Got", "Exception");
            e.printStackTrace();
        }
    }
}

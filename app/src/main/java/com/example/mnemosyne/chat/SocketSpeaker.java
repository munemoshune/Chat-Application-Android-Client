package com.example.mnemosyne.chat;

import android.util.Log;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by mnemosyne on 2016/2/28.
 */
public class SocketSpeaker extends Thread{
    private Socket socket;
    private JSONObject msg;
    SocketSpeaker(JSONObject msg){
        this.socket = MainActivity.socket;
        this.msg = msg;
    }
    @Override
    public void run(){
        try {
            Log.e("Speaker Got", msg.toString());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(msg.toString());
            if(msg.getString("action") == "logOut"){
                Dialogs.getInstance().flushDialogs();
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

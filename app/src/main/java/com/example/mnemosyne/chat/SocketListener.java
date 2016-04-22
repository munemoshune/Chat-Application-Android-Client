package com.example.mnemosyne.chat;

/**
 * Created by mnemosyne on 2016/3/2.
 */
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by mnemosyne on 2016/2/28.
 */
public class SocketListener extends Thread{
    private Socket socket;
    SocketListener(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run(){
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine;
            while(true){
                while ((inputLine = in.readLine()) == null);
                Log.e("Listener Got", inputLine);
                new SocketHandler(inputLine).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

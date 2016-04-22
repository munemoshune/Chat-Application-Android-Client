package com.example.mnemosyne.chat;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by mnemosyne on 2016/3/8.
 */
public class Dialogs {
    private static Dialogs ourInstance = new Dialogs();
    private boolean isInitial = false;
    private static Hashtable<String, JSONArray> dialogs = new  Hashtable<String, JSONArray>();
    public static Dialogs getInstance() {
        return ourInstance;
    }

    public void putDialog(String target, JSONArray dialog) throws Exception{
        for(int i = 0; i < dialog.length(); i ++){
            dialog.getJSONObject(i).remove("sentenceCount");
        }
        dialogs.put(target, dialog);
        isInitial = true;
    }

    public void putMsg(JSONObject jsonObject){
        try {
            if(!dialogs.containsKey(jsonObject.getString("account")))
                dialogs.put(jsonObject.getString("account"), new JSONArray());
            dialogs.get(jsonObject.getString("account")).put(jsonObject);
            Log.e("put Msg", jsonObject.getString("Msg") + "!!!");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void putMsgInDialogWith(JSONObject temp) throws Exception{
        JSONObject jsonObject = new JSONObject(temp.toString());
        String target = jsonObject.getString("target");
        jsonObject.remove("action");
        jsonObject.remove("target");
        jsonObject.put("time", "");
        if(dialogs.containsKey(target)){
            JSONArray targetDialog = dialogs.get(target);
            targetDialog.put(jsonObject);
        }
        else{
            dialogs.put(target, new JSONArray());
            dialogs.get(target).put(jsonObject);
        }
    }

    public void updateTimeStamp(String target, String time, int msgNum) throws Exception{
        JSONArray targetDialog = dialogs.get(target);
        targetDialog.getJSONObject(msgNum).put("time", time);
    }

    public String getLastSentences(String account){
        try {
            if(dialogs.containsKey(account)) {

                JSONObject jsonObject = (JSONObject)dialogs.get(account).get(dialogs.get(account).length() - 1);
                Log.e("contain", jsonObject.getString("Msg"));
                return jsonObject.getString("Msg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean containDialogWith(String target){
        return dialogs.containsKey(target);
    }

    public JSONArray getDialogWith(String target){
        return dialogs.get(target);
    }

    public boolean isInitial(){
        return isInitial;
    }

    public void flushDialogs(){
        ourInstance = new Dialogs();
        dialogs = new  Hashtable<String, JSONArray>();
    }

    private Dialogs() {
    }
}

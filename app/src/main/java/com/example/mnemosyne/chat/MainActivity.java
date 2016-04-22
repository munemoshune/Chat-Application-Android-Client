package com.example.mnemosyne.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    static Socket socket;
    private String account, password, IP = "114.46.171.49";
    EditText accountEditText, passwordEditText, ipEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button logInButton = (Button)findViewById(R.id.logInButton);
        Button signUpBotton = (Button)findViewById(R.id.signUpBotton);
        accountEditText = (EditText)findViewById(R.id.accountEditText);
        passwordEditText = (EditText)findViewById(R.id.passwordEditText);
        ipEditText = (EditText)findViewById(R.id.ipEditText);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals(""))
                    Toast.makeText(MainActivity.this, "Empty blank is not allowed", Toast.LENGTH_LONG).show();
                else {
                    account = accountEditText.getText().toString();
                    password = passwordEditText.getText().toString();
                    IP = ipEditText.getText().toString();
                    new logIn().execute(account, password);
                }
            }
        });
        signUpBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = accountEditText.getText().toString();
                password = passwordEditText.getText().toString();
                new signUp().execute(account, password);
            }
        });
    }
    @Override
    public boolean onKeyDown(int kCode,KeyEvent kEvent){
        if(kCode == KeyEvent.KEYCODE_BACK);
        return true;
    }
    class logIn extends AsyncTask<String, Void, String>{
        ProgressDialog pd;
        @Override
        protected void onPreExecute(){
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Log in...");
            pd.show();
        }
        @Override
        protected String doInBackground(String...params){
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("account", params[0]);
                jsonObject.put("password", params[1]);
                jsonObject.put("action", "logIn");
                socket = new Socket();
                InetSocketAddress isa = new InetSocketAddress(IP, 9999);
                socket.connect(isa, 10000);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(jsonObject.toString());
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) == null);
                return inputLine;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String params){
            pd.dismiss();
            if(params.equals("error"))
                Toast.makeText(MainActivity.this, "No such account or wrong password", Toast.LENGTH_LONG).show();
            else {
                SocketListener socketListener = new SocketListener(socket);
                socketListener.setDaemon(true);
                socketListener.start();
                Log.e("Got", params);
                Bundle bundle = new Bundle();
                bundle.putString("account", account);
                bundle.putString("password", password);
                bundle.putString("from", "Main");
                bundle.putString("Lists", params);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                //intent.setClass(MainActivity.this, Lists.class);
                intent.setClass(MainActivity.this, SlidingLists.class);
                startActivity(intent);
            }
        }
    }
    class signUp extends AsyncTask<String, Void, String>{
        ProgressDialog pd;
        @Override
        protected void onPreExecute(){
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Sign up...");
            pd.show();
        }
        @Override
        protected String doInBackground(String...params){
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("account", params[0]);
                jsonObject.put("password", params[1]);
                jsonObject.put("action", "signUp");
                socket = new Socket();
                InetSocketAddress isa = new InetSocketAddress(IP, 9999);
                socket.connect(isa, 10000);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(jsonObject.toString());
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) == null);
                return inputLine;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String params){
            pd.dismiss();
            if(params.equals("duplicated"))
                Toast.makeText(MainActivity.this, "Account duplicated", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(MainActivity.this, "Sign up successes", Toast.LENGTH_LONG).show();
        }
    }
    class reset extends AsyncTask<Void, Void, String>{
        ProgressDialog pd;
        @Override
        protected void onPreExecute(){
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Reset server...");
            pd.show();
        }
        @Override
        protected String doInBackground(Void...params){
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("action", "RESET");
                socket = new Socket();
                InetSocketAddress isa = new InetSocketAddress(IP, 9999);
                socket.connect(isa, 10000);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(jsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String nothing){
            pd.dismiss();
        }
    }
}
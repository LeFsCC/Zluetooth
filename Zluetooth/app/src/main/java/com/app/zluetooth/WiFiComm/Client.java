package com.app.zluetooth.WiFiComm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


public class Client{
    Socket socket = null;
    Activity activity;
    Context context;

    public Client(String host, int port, Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        try {
            socket = new Socket(host, port);
            socket.setKeepAlive(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String inputString) {
        new sendMessThread(inputString).start();
    }

    class sendMessThread extends Thread {
        private String sendMsg = "";
        sendMessThread(String str) {
            this.sendMsg = str;
        }

        @Override
        public void run() {
            super.run();
            OutputStream os = null;
            try {
                os= socket.getOutputStream();
                os.write((""+this.sendMsg).getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeClient() {
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

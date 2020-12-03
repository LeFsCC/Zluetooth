package com.app.zluetooth.WiFiComm;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.zluetooth.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Scanner;


public class Server extends Thread {
    ServerSocket server = null;
    Socket socket = null;
    Activity activity;
    Context context;
    String timeStamp = "";

    public Server(int port, Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        try {
            System.out.println("  等待客户端连接...");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "等待客户端连接", Toast.LENGTH_SHORT).show();
                }
            });

            socket = server.accept();
            socket.setKeepAlive(true);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "  客户端 （" + socket.getInetAddress().getHostAddress() + "） 连接成功", Toast.LENGTH_SHORT).show();
                }
            });

            InputStream in = socket.getInputStream();
            int len = 0;
            final byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1) {

                final int finalLen = len;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timeStamp = new String(buf, 0, finalLen, StandardCharsets.UTF_8);
                        try {
                            double t=System.currentTimeMillis();
                            double t2 = Double.parseDouble(timeStamp);
                            Log.e("时间差: ", String.valueOf(t-t2));
                        } catch (Exception ignored) {
                            ;
                        }
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void closeServer() {

        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!server.isClosed()) {
            try {
                server.close();
                server = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.app.zluetooth.DistanceEstimation;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.app.zluetooth.Exception.ZlueToothException;
import com.app.zluetooth.FSK.Decoder;
import com.app.zluetooth.FSK.Encoder;
import com.app.zluetooth.MainActivity;
import com.app.zluetooth.R;
import com.app.zluetooth.Utils.RigidData;
import com.app.zluetooth.WiFiComm.Client;
import com.app.zluetooth.WiFiComm.Server;

public class DistanceEstimation {

    private Server server;
    private Context context;
    private Client client;
    private Activity activity;
    private Decoder receiver;
    private Encoder encoder;
    double A_0 = 0;
    double A_3 = 0;
    double A_1 = 0;
    double B_2 = 0;
    double B_3 = 0;
    double B_1 = 0;

    public DistanceEstimation(Server server, Client client, Context context, Activity activity) {
        this.activity = activity;
        this.server = server;
        this.client = client;
        this.context = context;
    }

    int rec_time1 = 0;
    int rec_time2 = 0;

    public void server_start(){
        receiver = new Decoder("recorded.wav", RigidData.sample_rate, RigidData.dis_symbol_size, context);
        new Thread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                receiver.record_start();
                double start_time = System.currentTimeMillis();
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                encoder = new Encoder("", RigidData.dis_symbol_size, context);
                encoder.writeAudio();
                A_0 = System.currentTimeMillis();
                ((MainActivity)activity).initTransmit();

                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                receiver.record_stop();
                rec_time1=receiver.locate_start(0);
                A_1 = start_time + rec_time1 * 1000.0 / RigidData.sample_rate;

                Log.e("A_1", String.valueOf((rec_time1 * 1000.0 / RigidData.sample_rate)));

                rec_time2=receiver.locate_start(rec_time1 + 1000);
                A_3 = start_time + rec_time2 * 1000.0 / RigidData.sample_rate;

                Log.e("A_3", String.valueOf((rec_time2 * 1000.0 / RigidData.sample_rate)));

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String timeStamp = server.getTimeStamp();
                Log.e("stamp", timeStamp);

                String[] str = timeStamp.split(";");
                B_2 = Double.parseDouble(str[0]);
                B_1 = Double.parseDouble(str[1]);
                B_3 = Double.parseDouble(str[2]);

                Log.e("A3 - A1", String.valueOf(A_3 - A_1));
                Log.e("B3 - B1", String.valueOf(B_3 - B_1));

                final double d = 0.17 * ((A_3 - A_1) - (B_3 - B_1)) ;

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView distance_txt = activity.findViewById(R.id.distance_txt);
                        distance_txt.setText((int)(Math.abs(d * 100)) + "cm");
                    }
                });
                Log.e("距离", String.valueOf(Math.abs(d * 100)));
            }
        }).start();
    }

    public void client_start(){
        rec_time1=0;
        rec_time2=0;
        receiver = new Decoder("recorded.wav", RigidData.sample_rate, RigidData.dis_symbol_size, context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                receiver.record_start();
                double start_time = System.currentTimeMillis();
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //发声波
                encoder = new Encoder("", RigidData.dis_symbol_size, context);
                encoder.writeAudio();
                B_2 = System.currentTimeMillis();
                ((MainActivity)activity).initTransmit();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                receiver.record_stop();
                rec_time1=receiver.locate_start(0);
                B_1 = start_time  + rec_time1 * 1000.0 / RigidData.sample_rate;
                rec_time2 = receiver.locate_start(rec_time1 + 1000);
                B_3 = start_time + rec_time2 * 1000.0 / RigidData.sample_rate;

                Log.e("B_1", String.valueOf(rec_time1 * 1000.0 / RigidData.sample_rate));
                Log.e("B_3", String.valueOf(rec_time2 * 1000.0 / RigidData.sample_rate));
                try{
                    client.sendMessage(B_2 + ";" + B_1 + ";" + B_3);
                } catch (Exception e) {
                    Toast.makeText(context, "服务器异常, 请重启", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }
}

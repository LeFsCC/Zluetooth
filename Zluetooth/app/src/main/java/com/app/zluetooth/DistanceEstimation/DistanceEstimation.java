package com.app.zluetooth.DistanceEstimation;


import android.content.Context;
import android.widget.Toast;

import com.app.zluetooth.Exception.ZlueToothException;
import com.app.zluetooth.WiFiComm.Server;

public class DistanceEstimation {

    private Server server;
    private Context context;

    public DistanceEstimation(Server server, Context context) {
        this.server = server;
        this.context = context;
    }

    private double getWiFiPacketTime() {
        try {
            String timeStamp = server.getTimeStamp();
        } catch (Exception e) {
            Toast.makeText(context, "服务器异常", Toast.LENGTH_SHORT).show();
        }
        return 0;
    }

    private double getPacketArrivalTime() {
        return 0;
    }

    private double calcDistance() {
        double timeVariance = getPacketArrivalTime() - getWiFiPacketTime();
        return timeVariance * 340.0;
    }
}

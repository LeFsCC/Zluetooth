package com.app.zluetooth;

public class DataPacket {
    int raw_length;
    String data;
    double RSSI;
    DataPacket(int raw_length, String data, double rssi) {
        this.raw_length = raw_length;
        this.data = data;
        this.RSSI = rssi;
    }
}

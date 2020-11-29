package com.app.zluetooth.Utils;

public class DataPacket {
    int raw_length;
    String data;
    DataPacket(int raw_length, String data) {
        this.raw_length = raw_length;
        this.data = data;
    }
}

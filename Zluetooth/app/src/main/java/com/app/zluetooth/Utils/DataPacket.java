package com.app.zluetooth.Utils;

public class DataPacket {
    int []  bi_data;
    String data;

    public DataPacket() {

    }

    public void setBi_data(int[] bi_data) {
        this.bi_data = bi_data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int[] getBi_data() {
        return bi_data;
    }

    public String getData() {
        return data;
    }
}

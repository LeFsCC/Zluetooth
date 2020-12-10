package com.app.zluetooth.Utils;

public class DataPacket {
    int []  bi_data;
    String data;

    public DataPacket() {

    }

    public void setBi_data(int[] bi_data) {
        this.bi_data = bi_data;
    }

    public void add_data(int[] n_data) {
        int[] n_bi_data = new int[this.bi_data.length + n_data.length];
        System.arraycopy(this.bi_data, 0, n_bi_data, 0, this.bi_data.length);
        System.arraycopy(n_data, 0, n_bi_data, this.bi_data.length, n_data.length);
        this.bi_data = n_bi_data;
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

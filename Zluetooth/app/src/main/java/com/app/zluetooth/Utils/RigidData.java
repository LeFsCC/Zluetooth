package com.app.zluetooth.Utils;

public class RigidData {
    public final static int fs = 5000;      // 5000
    public final static int sync_fs = 15000;   // 15000

    public final static int number_of_carriers = 8; // 2 or 8
    public final static int module_order = 3;       // 3 for 8, 2 for 4, 1 for 2
    public final static int time_interval = 625;   // 625

    public final static int sample_rate = 44100;
    public final static double symbol_size = 0.2;
    public final static int number_of_letter_each_packet = 12;

    public final static int dis_fs = 4000;
    public final static int sync_dis_fs = 14000;
    public final static double dis_symbol_size = 0.02;
}

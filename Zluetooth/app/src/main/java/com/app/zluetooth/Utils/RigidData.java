package com.app.zluetooth.Utils;

public class RigidData {
    public final static int fs = 4000;      // start frequency 5000
    public final static int sync_fs = 14000;   // end frequency 15000

    public final static int number_of_carriers = 2; // 2 or 8
    public final static int module_order = 1;       // 3 for 8, 2 for 4, 1 for 2
    public final static int frequency_interval = 2000;   // 625

    public final static int sample_rate = 48000;  // 44100
    public final static double symbol_size = 0.025; // s
    public final static int number_of_letter_each_packet = 12;

    public final static int dis_fs = 4000;
    public final static int sync_dis_fs = 14000;
    public final static double dis_symbol_size = 0.02;
}

package com.app.zluetooth.Utils;

public class RigidData {
    public static int fs = 5000;      // start frequency 5000
    public static int sync_fs = 15000;   // end frequency 15000

    public static int number_of_carriers = 8; // 2 or 8
    public static int module_order = 3;       // 3 for 8, 2 for 4, 1 for 2
    public static int frequency_interval = 625;   // 625

    public static int sample_rate = 44100;  // 44100
    public static double symbol_size = 0.2; // s
    public static int number_of_letter_each_packet = 12;

    public static double dis_symbol_size = 0.5;
    public static void reset() {
        RigidData.fs = 5000;
        RigidData.sync_fs = 15000;
        RigidData.number_of_carriers = 8;
        RigidData.module_order = 3;
        RigidData.frequency_interval = 625;
        RigidData.sample_rate = 44100;
        RigidData.symbol_size = 0.2;
        RigidData.number_of_letter_each_packet = 12;
        RigidData.dis_symbol_size = 0.5;
    }
}

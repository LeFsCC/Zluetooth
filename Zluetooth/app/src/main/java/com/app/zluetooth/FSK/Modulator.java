package com.app.zluetooth.FSK;

import android.renderscript.ScriptIntrinsicYuvToRGB;

import com.app.zluetooth.Utils.RigidData;

import java.util.ArrayList;

public class Modulator {

    private static int[] data;
    private static double symbol_size;
    private static int number_of_carriers;
    private static int[] frequencies;
    private static int fs;
    private static ArrayList<Double> modulated;
    private static ArrayList<SignalGenerator> carriers;


    public Modulator(double symbol_size) {
        Modulator.symbol_size = symbol_size;
        Modulator.number_of_carriers = RigidData.number_of_carriers;
        fs = RigidData.fs;
        initFrequencies();
        initCarriers();
    }

    public void setData(int[] data) {
        Modulator.data = data;
    }

    public void initFrequencies() {
        frequencies = new int[number_of_carriers];
        frequencies[0] = fs;
        for (int i = 1; i < number_of_carriers; i++) {
            frequencies[i] = frequencies[i - 1] + RigidData.time_interval;
        }
    }

    public void initCarriers() {
        carriers = new ArrayList<>();
        for (int i = 0; i < number_of_carriers; i++) {
            SignalGenerator s = new SignalGenerator(symbol_size, frequencies[i], 1.0 / RigidData.sample_rate);
            carriers.add(s);
        }
    }

    public void modulate() {

        double level_t = Math.log(number_of_carriers) / Math.log(2);
        int level = (int) level_t;

        int[] temp = new int[level];
        modulated = new ArrayList<>();

        // 前面加白
        for(int i = 0; i < 10000; i++) {
            modulated.add(0.0);
        }

        modulated.addAll(carriers.get(0).generate_chirp_sync());
        for (int i = 0; i < data.length - (level - 1); i += level) {
            temp[0] = data[i];
            temp[1] = data[i + 1];
            temp[2] = data[i + 2];
            if(level == 4) {
                temp[3] = data[i + 3];
            }
            if(level == 3) {
                map8(temp);
            }
        }

        // 后面加白, 为了和下一个数据包分开
        for(int i = 0; i < 10000; i++) {
            modulated.add(0.0);
        }
    }

    public void map8(int[] temp) {
        modulated.addAll(carriers.get(toNumber(temp)).generate());
    }

    private int toNumber(int[] temp) {
        StringBuilder ee = new StringBuilder();
        for(int t: temp) {
            ee.append(t);
        }
        return Integer.parseInt(ee.toString(), 2);
    }

    public ArrayList<Double> getModulated() {
        return modulated;
    }
}

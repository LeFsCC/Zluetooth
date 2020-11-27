package com.app.zluetooth.FSK;

import com.app.zluetooth.Utils.RigidData;

import java.util.ArrayList;

public class SignalGenerator {
    private double symbol_size;
    private double f;
    private double step_size;
    private double sample_rate;
    private ArrayList<Double> data;
    private ArrayList<Double> sync;

    public SignalGenerator(double symbol_size, double f, double step_size) {
        this.symbol_size = symbol_size;
        this.f = f;
        this.step_size = step_size;
        this.sample_rate = 1.0 / step_size;
    }

    public ArrayList<Double> getData() {
        return data;
    }

    public ArrayList<Double> getSync() {
        return sync;
    }

    public ArrayList<Double> generate() {

        try {
            data = new ArrayList<Double>();
            double rad = 0;
            for (int i = 0; i < symbol_size / step_size; i++) {
                rad = (2 * Math.PI * f * i * step_size);
                data.add(Math.cos(rad));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return data;
    }

    public ArrayList<Double> generate_sync() {

        sync = new ArrayList<Double>();
        double rad = 0;
        double k = ((RigidData.sync_fs - RigidData.fs) / symbol_size);
        for (int i = 0; i < symbol_size * sample_rate; i++) {

            rad = (2 * Math.PI * ((k / 2) * i * step_size + RigidData.fs) * i * step_size);
            sync.add(Math.cos(rad));
        }
        return sync;
    }
}

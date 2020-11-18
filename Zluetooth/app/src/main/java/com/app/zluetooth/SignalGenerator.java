package com.app.zluetooth;

import java.util.ArrayList;

/**
 * Created by misha on 2016/09/13.
 */
// 信号生成类
public class SignalGenerator {
    private double symbol_size;
    private double f;
    private double step_size;
    private double sample_rate;
    private ArrayList<Double> data;
    private ArrayList<Double> sync;

//    参数包括信号时间，信号频率，采样周期（采样频率的倒数）
    public SignalGenerator(double symbol_size, double f, double step_size) {
        this.symbol_size = symbol_size;
        this.f = f;
        this.step_size = step_size;
        this.sample_rate = 1.0 / step_size;
    }

    public double getSymbol_size() {
        return symbol_size;
    }

    public void setSymbol_size(double symbol_size) {
        this.symbol_size = symbol_size;
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public double getStep_size() {
        return step_size;
    }

    public void setStep_size(double step_size) {
        this.step_size = step_size;
    }

    public ArrayList<Double> getData() {
        return data;
    }

    public ArrayList<Double> getSync() {
        return sync;
    }

//    获得信号序列
    public ArrayList<Double> generate() {

        try {
            data = new ArrayList<Double>();
            double rad = 0;
            for (int i = 0; i < symbol_size / step_size; i++) {
//                2*pi*f为角速度，i*step_size为采样时间
                rad = (2 * Math.PI * f * i * step_size);
                data.add(Math.cos(rad));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return data;
    }

// todo 暂时不知道用途
    public ArrayList<Double> generate_sync() {

        sync = new ArrayList<Double>();
        double rad = 0;
        double k = ((16000 - 6000) / symbol_size);
        for (int i = 0; i < symbol_size * sample_rate; i++) {

            rad = (2 * Math.PI * ((k / 2) * i * step_size + 6000) * i * step_size);
            sync.add(Math.cos(rad));
        }
        return sync;
    }
}

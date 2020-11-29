package com.app.zluetooth.FSK;

import android.annotation.SuppressLint;
import android.content.Context;

import com.app.zluetooth.Utils.MyAudio;
import com.app.zluetooth.Utils.StringAndBinary;

import java.util.ArrayList;

// 信号发送器
public class Transmitter {

    private static String src;
    private static double                      symbol_size;
    private static int []                      data;
    private static ArrayList<Double> modulated;

    private static Modulator fsk_modulator;
    private static MyAudio audio_handler;
    private static StringAndBinary stringToBinary;//获得二进制序列

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public Transmitter(String src, double symbol_size, Context context) {
        Transmitter.src = src;
        Transmitter.symbol_size = symbol_size;
        Transmitter.context = context;
        getBinarySeq();
        initModulator();
    }

    public void getBinarySeq(){
        stringToBinary = new StringAndBinary(src);
        data = new int [stringToBinary.getB().length];
        data = stringToBinary.getB();
    }

    public void initModulator (){
        fsk_modulator = new Modulator(data,  symbol_size);
        fsk_modulator.modulate();
        modulated = fsk_modulator.getModulated();
    }

    public void writeAudio (){
        audio_handler = new MyAudio(castToDouble(modulated),context,"FSK.wav");
        audio_handler.writeFile();
        audio_handler.close();
    }

    public Double[] castToDouble(ArrayList<Double> in ){
       Double[] r = new Double[in.size()];
        for (int i = 0; i <in.size() ; i++) {
            r[i] = (Double) in.get(i);
        }
        return r;
    }
}

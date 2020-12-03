package com.app.zluetooth.FSK;

import android.annotation.SuppressLint;
import android.content.Context;

import com.app.zluetooth.Utils.MyAudio;
import com.app.zluetooth.Utils.StringAndBinary;

import java.util.ArrayList;

public class Encoder {

    private static double  symbol_size;
    private static int []  data;
    private static ArrayList<Double> modulated;

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public Encoder(String src, double symbol_size, Context context) {
        Encoder.symbol_size = symbol_size;
        Encoder.context = context;
        try {
            StringAndBinary stringToBinary = new StringAndBinary(src);
            data = new int [stringToBinary.getB().length];
            data = stringToBinary.getB();
        } catch (Exception ignored){

        }
        initModulator();
    }

    public void initModulator (){
        Modulator fsk_modulator = new Modulator(data, symbol_size);
        fsk_modulator.modulate();
        modulated = fsk_modulator.getModulated();
    }

    public void writeAudio (){
        Double[] r = new Double[modulated.size()];
        for (int i = 0; i <modulated.size() ; i++) {
            r[i] = (Double) modulated.get(i);
        }
        MyAudio audio_handler = new MyAudio(r, context, "FSK.wav");
        audio_handler.writeFile();
        audio_handler.close();
    }
}

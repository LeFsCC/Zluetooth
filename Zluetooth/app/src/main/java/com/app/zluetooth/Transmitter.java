package com.app.zluetooth;

import android.content.Context;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by misha on 2016/09/12.
 */
public class Transmitter {

    private static String modulation;
    private static String src;
    private static double                      sample_rate;
    private static double                      symbol_size;
    private static int []                      data;
    private static double                      sample_period;
    private static int                         number_of_carriers;
    private static ArrayList<Double> modulated;

    private static FSK_Modulator               fsk_modulator;
    private static AudioHandler                audio_handler;
    private static StringHanlder               string_handler;

    private static Context context;

    public Transmitter() {

    }

    public Transmitter(String modulation, String src, double sample_rate, double symbol_size, double sample_period, int number_of_carriers, Context context) {//Constructore for String
        Transmitter.modulation = modulation;
        Transmitter.src = src;
        Transmitter.sample_rate = sample_rate;
        Transmitter.symbol_size = symbol_size;
        Transmitter.sample_period = sample_period;
        Transmitter.number_of_carriers = number_of_carriers;

        Transmitter.context = context;

        long startTime = System.nanoTime();
        initString();
        initModulator();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000;

        System.out.println("Time taken for modulation: " + duration + "ms") ;

    }


    public void initString(){
        string_handler = new StringHanlder(src);
        System.out.println("This is the length of Data : " + string_handler.getB().length);
        data =              new int [string_handler.getB().length];
        data =              string_handler.getB();

    }

    public void initModulator (){
        if (modulation.equals("FSK")) {
            fsk_modulator = new FSK_Modulator(data, sample_rate, symbol_size, number_of_carriers);
            fsk_modulator.modulate();
            modulated = fsk_modulator.getModulated();
        }
    }

    public void writeAudio (){
        audio_handler = new AudioHandler(castToDouble(modulated),context,"FSK.wav");
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

package com.app.zluetooth.FSK;

import android.content.Context;
import android.util.Log;

import com.app.zluetooth.Utils.AudioHandler;
import com.app.zluetooth.Utils.Recorder;
import com.app.zluetooth.Utils.StringHanlder;

import java.util.ArrayList;
import java.util.List;

public class Receiver {

    private String file_name;

    private double sample_rate;
    private double symbol_size;
    private double sample_period;
    private double duration;
    private int number_of_carriers;
    private ArrayList<Double> modulated;
    private ArrayList<Integer> demodulated;


    private Demodulator fsk_demodulator;
    private AudioHandler audio_handler;
    private StringHanlder string_handler;
    private MatchedFilter matched_filter;
    private ArrayList<Double> recoverd_signal;

    private String recoverd_string;

    private Context context;

    private Recorder r;

    public Receiver(String file_name, double sample_rate, double symbol_size, double duration, int number_of_carriers, Context context) {
        this.file_name = file_name;
        this.sample_rate = sample_rate;
        this.sample_period = 1.0 / sample_rate;
        this.symbol_size = symbol_size;
        this.duration = duration;
        this.number_of_carriers = number_of_carriers;
        this.context = context;
    }

    public void record_start() {
        try {
            r = new Recorder("TEST");
            r.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void record_stop() {
        try {
            r.stop();
            double avg_vol = r.getVolume();
            Log.e("该段录音平均音量大小：", String.valueOf(avg_vol));
            audio_handler = new AudioHandler(context, file_name);
            modulated = audio_handler.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     // 使用阈值判断和梯度检测的方式判断同步码位置
    public int locate_start() {
        List<Double> sub_signal = new ArrayList<>();
        sub_signal = modulated.subList(0, (int)(symbol_size * sample_rate));
        ArrayList<Double> temp_signal = new ArrayList<>(sub_signal);

        matched_filter = new MatchedFilter(duration, symbol_size, sample_rate,  temp_signal);

        ArrayList<Double> res = matched_filter.get_start_index();
        int start_index = -1;
        double last_max = 1;
        if(res.size() != 0) {
            last_max = res.get(0);
            double temp = res.get(1);
            start_index = (int) temp;
        }

        int offset = 0;

        while(offset + (int)(symbol_size * sample_rate) < modulated.size()) {
            offset += symbol_size * sample_rate;
            sub_signal = modulated.subList(offset, (int)(offset + symbol_size * sample_rate));
            temp_signal = new ArrayList<>(sub_signal);
            matched_filter = new MatchedFilter(duration, symbol_size, sample_rate, temp_signal);
            res = matched_filter.get_start_index();
            if(res.size() != 0) {
                 double cur_max = res.get(0);
                 double temp = res.get(1);
                 if((last_max > 0.001 && (cur_max / last_max) >= 40)) {
                     start_index = (int) temp;
                     break;
                 }
                 last_max = cur_max;
            }
            matched_filter = null; // System.gc(); 内存垃圾回收
        }
        return start_index + offset;
    }

    public void recover_signal() {

        int start_index = locate_start();
        if(start_index == -1) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("S I G N A L   R E C O V E R E D   F A I L E D, P L Z   T R Y   A G A I N.");
        }

        recoverd_signal = new ArrayList<Double>();
        System.out.println("Start index is at " + start_index);
        System.out.println("Size of signal is" + modulated.size());
        System.out.println("Recovered String");
        System.out.println("----------------------------------------------------------------");

        recoverd_signal.addAll(modulated.subList(start_index, modulated.size()));
        fsk_demodulator = new Demodulator(sample_rate, symbol_size, recoverd_signal);
        String res = demodulate();
        System.out.println(res);
        fsk_demodulator = null;
        modulated = null;
        System.gc();
    }

    public String demodulate() {
        if(recoverd_signal.get(0) == -1.0)
        {
            recoverd_string = "-1";
            return "no signal";
        }

        fsk_demodulator.demodulate();
        demodulated = fsk_demodulator.getDemodulated();
        printBitStream();
        string_handler = new StringHanlder(demodulated);
        recoverd_string = string_handler.getString();
        return recoverd_string;
    }

    public void printBitStream() {
        for (int i = 0; i < demodulated.size(); i++) {
            System.out.print(demodulated.get(i));
        }
        System.out.println("");
    }

    public String getRecoverd_string() {
        return recoverd_string;
    }
}

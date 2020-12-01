package com.app.zluetooth.FSK;

import android.content.Context;

import com.app.zluetooth.Utils.MyAudio;
import com.app.zluetooth.Utils.Recorder;
import com.app.zluetooth.Utils.StringAndBinary;

import java.util.ArrayList;
import java.util.List;

public class Receiver {

    private String file_name;

    private double sample_rate;
    private double symbol_size;
    private ArrayList<Double> modulated;
    private ArrayList<Integer> demodulated;
    private Demodulator fsk_demodulator;
    private ArrayList<Double> recovered_signal;
    private String recovered_string;
    private Context context;
    private Recorder r;

    public Receiver(String file_name, double sample_rate, double symbol_size, Context context) {
        this.file_name = file_name;
        this.sample_rate = sample_rate;
        this.symbol_size = symbol_size;
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
            MyAudio audio_handler = new MyAudio(context, file_name);
            modulated = audio_handler.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     // 使用阈值判断和梯度检测的方式判断同步码位置
    public int locate_start(int st) {
        if(st >= modulated.size()) {
            return -1;
        }

        try{
            List<Double> sub_signal = new ArrayList<>();
            sub_signal = modulated.subList(st, st + (int)(symbol_size * sample_rate));
            ArrayList<Double> temp_signal = new ArrayList<>(sub_signal);

            MatchedFilter matched_filter = new MatchedFilter(symbol_size, sample_rate, temp_signal);

            ArrayList<Double> res = matched_filter.get_start_index();
            int start_index = -1;
            double last_max = 1;
            if(res.size() != 0) {
                last_max = res.get(0);
                double temp = res.get(1);
                start_index = (int) temp;
            }

            int offset = st;

            while(offset + (int)(symbol_size * sample_rate) < modulated.size()) {
                offset += symbol_size * sample_rate;
                sub_signal = modulated.subList(offset, (int)(offset + symbol_size * sample_rate));
                temp_signal = new ArrayList<>(sub_signal);
                matched_filter = new MatchedFilter(symbol_size, sample_rate, temp_signal);
                res = matched_filter.get_start_index();
                if(res.size() != 0) {
                    double cur_max = res.get(0);
                    double temp = res.get(1);
                    if((last_max > 0.001 && (cur_max / last_max) >= 40) || cur_max > 10.0) {
                        start_index = (int) temp;
                        break;
                    }
                    last_max = cur_max;
                }
                matched_filter = null; // System.gc(); 内存垃圾回收
            }
            return start_index + offset;
        } catch (Exception e) {
            return -1;
        }
    }

    public void recover_signal() {

        int start_index = locate_start(0);
        if(start_index == -1) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("S I G N A L   R E C O V E R E D   F A I L E D, P L Z   T R Y   A G A I N.");
            return;
        }

        recovered_signal = new ArrayList<Double>();
        System.out.println("Start index is at " + start_index);
        System.out.println("Size of signal is" + modulated.size());
        System.out.println("Recovered String");
        System.out.println("----------------------------------------------------------------");

        recovered_signal.addAll(modulated.subList(start_index, modulated.size()));
        fsk_demodulator = new Demodulator(sample_rate, symbol_size, recovered_signal);
        String res = demodulate();
        System.out.println(res);
        fsk_demodulator = null;
        modulated = null;
        System.gc();
    }

    public String demodulate() {
        if(recovered_signal.get(0) == -1.0) {
            recovered_string = "-1";
            return "no signal";
        }

        fsk_demodulator.demodulate();
        demodulated = fsk_demodulator.getDemodulated();
        printBitStream();
        StringAndBinary string_handler = new StringAndBinary(demodulated);
        recovered_string = string_handler.getString();
        return recovered_string;
    }

    public void printBitStream() {
        for (int i = 0; i < demodulated.size(); i++) {
            System.out.print(demodulated.get(i));
        }
    }

    public String getRecoverd_string() {
        return recovered_string;
    }
}

package com.app.zluetooth.FSK;

import android.content.Context;

import com.app.zluetooth.Utils.MyAudio;
import com.app.zluetooth.Utils.Recorder;
import com.app.zluetooth.Utils.RigidData;
import com.app.zluetooth.Utils.StringAndBinary;

import java.util.ArrayList;
import java.util.List;

public class Receiver {

    private String file_name;

    private double sample_rate;
    private double symbol_size;
    private ArrayList<Double> modulated = new ArrayList<>();
    private ArrayList<Integer> demodulated;
    private Demodulator fsk_demodulator;
    private ArrayList<Double> recovered_signal = new ArrayList<>();
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

        try {
            List<Double> sub_signal = new ArrayList<>();
            sub_signal = modulated.subList(st, st + (int)(symbol_size * sample_rate));
            ArrayList<Double> temp_signal = new ArrayList<>(sub_signal);

            MatchedFilter matched_filter = new MatchedFilter(symbol_size, sample_rate, temp_signal);

            ArrayList<Double> res = matched_filter.get_start_index();
            int start_index = -1;
            double last_max = 1;
            if(res.size() != 0) {
                last_max = res.get(0);
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
                    if((last_max > 0.001 && (cur_max / last_max) >= 40)) {
                        start_index = (int) temp;
                        // 滑窗再次滑动, 找到真正的最大值
                        int offset2 = offset + (int)(symbol_size * sample_rate);
                        sub_signal = modulated.subList(offset2, (int)(offset2 + symbol_size * sample_rate));
                        temp_signal = new ArrayList<>(sub_signal);
                        matched_filter = new MatchedFilter(symbol_size, sample_rate, temp_signal);
                        res = matched_filter.get_start_index();
                        double cur_max2 = res.get(0);
                        double temp_2 = res.get(1);
                        double tmp1 = cur_max/cur_max2;
                        if(tmp1<1.15 && tmp1>0.87){
                            return (int)(temp_2 + offset2+start_index + offset)/2;
                        }
                        if(cur_max2 > cur_max) {
                            return (int) (temp_2 + offset2);
                        } else {
                            return start_index + offset;
                        }
                    }
                    last_max = cur_max;
                }
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    public void recover_data_packet() {
        recovered_string = "";
        int start_index = locate_start(0);

        while(start_index < modulated.size()) {
            recover_signal(start_index, Math.min(start_index + 8 * RigidData.number_of_letter_each_packet * (int) (symbol_size * sample_rate) / 3, modulated.size()));
            start_index += 8 * RigidData.number_of_letter_each_packet * (int)(symbol_size * sample_rate) / 3;
            start_index = locate_start(start_index + 100);
            if(start_index == -1) return;
        }
    }

    private void recover_signal(int start_index, int end_index) {
        if(start_index == -1) return;
        recovered_signal.clear();
        System.out.println("Recovered String");
        System.out.println("----------------------------------------------------------------");
        recovered_signal.addAll(modulated.subList(start_index, end_index));
        fsk_demodulator = new Demodulator(sample_rate, symbol_size, recovered_signal);
        String res = demodulate();
        System.out.println(res);
        System.gc();
    }

    private String demodulate() {
        if(recovered_signal.get(0) == -1.0) {
            recovered_string = "-1";
            return "no signal";
        }

        fsk_demodulator.demodulate();
        demodulated = fsk_demodulator.getDemodulated();
        StringAndBinary string_handler = new StringAndBinary(demodulated);
        recovered_string = recovered_string.concat(string_handler.getString());
        return recovered_string;
    }

    private void printBitStream() {
        for (int i = 0; i < demodulated.size(); i++) {
            System.out.print(demodulated.get(i));
        }
    }

    public String getRecoverd_string() {
        return recovered_string;
    }
}

package com.app.zluetooth.FSK;


import com.app.zluetooth.Utils.RigidData;

import org.jtransforms.fft.DoubleFFT_1D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MatchedFilter {

    private double symbol_size;
    private double sample_rate;
    private double sample_period;

    private double[] chirp_fft;
    private double[] signal_fft;
    private double[] filter_out;

    private int start_index;

    private ArrayList<Double> modulated;
    private ArrayList<Double> chirp_signal;
    private double[] chirp_signal_a;

    private ArrayList<Double> recovered_signal;

    private DoubleFFT_1D fft;

    private SignalGenerator signal_generator;

    public MatchedFilter(double symbol_size, double sample_rate, ArrayList<Double> modulated) {
        this.symbol_size = symbol_size;
        this.sample_rate = sample_rate;
        this.sample_period = 1.0 / sample_rate;
        this.modulated = modulated;
        this.recovered_signal = new ArrayList<>();
        getSync();
        matchSignal();
    }

    public void getSync() {
        signal_generator = new SignalGenerator(symbol_size, RigidData.sync_fs, sample_period);
        signal_generator.generate_chirp_sync();
        chirp_signal = signal_generator.getSync();
        chirp_signal_a = toArray((chirp_signal));
        Collections.reverse(chirp_signal);
    }

    public void matchSignal() {
        try {
            fft = new DoubleFFT_1D(modulated.size());
            signal_fft = new double[modulated.size() * 2];
            System.arraycopy(toArray(modulated), 0, signal_fft, 0, modulated.size());
            fft.realForwardFull(signal_fft);
            chirp_fft = new double[modulated.size() * 2];
            System.arraycopy(chirp_signal_a, 0, chirp_fft, 0, chirp_signal_a.length);
            fft.realForwardFull(chirp_fft);
            filter_out = new double[signal_fft.length];
            for (int i = 0; i < signal_fft.length; i++) {
                filter_out[i] = chirp_fft[i] * signal_fft[i];
            }
            fft.realInverseFull(filter_out, true);
            filter_out = abs(filter_out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Double> get_start_index() {
        ArrayList<Double> res = new ArrayList<>();
        try {
            double[] sorted = new double[filter_out.length];
            System.arraycopy(filter_out, 0, sorted, 0, filter_out.length);
            Arrays.sort(sorted);
            double max = sorted[sorted.length - 1];
            start_index = maxIndex(filter_out, max) +(int) (symbol_size*sample_rate);
            res.add(max);
            res.add((double)start_index);
            System.out.println("max value" + max);
            return res;
        } catch (Exception e) {
            return res;
        }
    }

    public int maxIndex(double[] input, double max) {
        for (int i = 0; i < input.length; i++) {
            if (input[i] == max) {
                return i;
            }
        }
        return 0;
    }

    public double[] toArray(ArrayList<Double> in) {
        double[] ret = new double[in.size() * 2];
        for (int i = 0; i < in.size(); i++) {
            ret[i] = in.get(i);
        }
        return ret;
    }

    public double[] abs(double[] input) {
        double[] ret = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            ret[i] = Math.abs(input[i]);
        }
        return ret;
    }
}

package com.app.zluetooth.FSK;

import com.app.zluetooth.Utils.RigidData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class Demodulator {


    private double sample_rate;
    private double symbol_size;
    private int number_of_carriers;
    private int[] frequencies;
    private int fs;
    private ArrayList<Integer> demodulated;
    private ArrayList<SignalGenerator> carriers;
    private ArrayList<Double> modulated;


    public Demodulator(double sample_rate, double symbol_size, ArrayList<Double> modulated) {
        this.sample_rate = sample_rate;
        this.symbol_size = symbol_size;
        this.number_of_carriers = RigidData.number_of_carriers;
        this.fs = RigidData.fs;
        this.modulated = modulated;
        demodulated = new ArrayList<>();
        initFrequencies();
        initCarriers();
    }

    public void initFrequencies() {
        frequencies = new int[number_of_carriers];
        frequencies[0] = fs;
        for (int i = 1; i < number_of_carriers; i++) {
            frequencies[i] = frequencies[i - 1] + RigidData.frequency_interval;
        }
    }

    public void initCarriers() {
        carriers = new ArrayList<>();
        for (int i = 0; i < number_of_carriers; i++) {
            SignalGenerator s = new SignalGenerator(symbol_size, frequencies[i], 1.0 / (double)RigidData.sample_rate);
            s.generate();
            carriers.add(s);
        }
    }

    public void demodulate() {
        try {
            double[] temp = toArray();
            ArrayList<Double> holder = new ArrayList<>();
            System.out.println("symbol_size * sample_rate " + symbol_size * sample_rate);
            for (int i = 0; i < modulated.size(); i += (int) (symbol_size * sample_rate)) {

                double[] Symbol = Arrays.copyOfRange(temp, i, (int) (i + symbol_size * sample_rate));

                for (int j = 0; j < number_of_carriers; j++) {
                    holder.add(Math.abs(trapz(carriers.get(j).getData(), Symbol)));
                }

                double max = Collections.max(holder);
                for (int n = 0; n < holder.size(); n++) {
                    if (max == holder.get(n)) {
                        demodulated.addAll(getBits8(n));
                    }
                }
                holder.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Integer> getBits2(int n) {
        String s = Integer.toBinaryString(n);
        char[] t = s.toCharArray();
        ArrayList<Integer> bits = new ArrayList<>();
        for(int i = 0; i < s.length(); i++) {
            bits.add(Integer.parseInt(String.valueOf(t[i])));
        }
        return bits;
    }

    private ArrayList<Integer> getBits8(int n) {
        String s = Integer.toBinaryString(n);
        char[] t = s.toCharArray();
        ArrayList<Integer> bits = new ArrayList<>();
        int j = 3 - t.length;

        for(int k = 0; k < j;k++) {
            bits.add(0);
        }

        for(int i = 0; i < s.length(); i++) {
            bits.add(Integer.parseInt(String.valueOf(t[i])));
        }
        return bits;
    }

    public double trapz(ArrayList<Double> Carrier, double[] Symbol) {
        double r = 0;
        try {
            if (Carrier.size() != Symbol.length) {
                return 0;
            }
            double sum = 0;
            for (int i = 0; i < Carrier.size() - 1; i++) {
                sum += Carrier.get(i) * Symbol[i] + Carrier.get(i + 1) * Symbol[i + 1];
            }
            r = sum * 0.5;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    public double[] toArray() {
        double[] r = new double[modulated.size()];
        for (int i = 0; i < modulated.size(); i++) {
            r[i] = modulated.get(i);
        }
        return r;
    }

    public ArrayList<Integer> getDemodulated() {
        return demodulated;
    }
}

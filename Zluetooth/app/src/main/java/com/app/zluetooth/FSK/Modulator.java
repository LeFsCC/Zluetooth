package com.app.zluetooth.FSK;

import com.app.zluetooth.Utils.RigidData;

import java.util.ArrayList;
import java.util.Arrays;

public class Modulator {

    private static int[] data;
    private static double sample_rate;
    private static double symbol_size;
    private static double sample_period;
    private static int number_of_carriers;
    private static int[] frequencies;
    private static int fs;                    //开始频率
    private static ArrayList<Double> modulated;
    private static ArrayList<SignalGenerator> carriers;

    int s_0[] = {0, 0, 0, 0};
    int s_1[] = {0, 0, 0, 1};
    int s_2[] = {0, 0, 1, 0};
    int s_3[] = {0, 0, 1, 1};
    int s_4[] = {0, 1, 0, 0};
    int s_5[] = {0, 1, 0, 1};
    int s_6[] = {0, 1, 1, 0};
    int s_7[] = {0, 1, 1, 1};
    int s_8[] = {1, 0, 0, 0};
    int s_9[] = {1, 0, 0, 1};
    int s_10[] = {1, 0, 1, 0};
    int s_11[] = {1, 0, 1, 1};
    int s_12[] = {1, 1, 0, 0};
    int s_13[] = {1, 1, 0, 1};
    int s_14[] = {1, 1, 1, 0};
    int s_15[] = {1, 1, 1, 1};

    int t_0[] = {0, 0, 0};
    int t_1[] = {0, 0, 1};
    int t_2[] = {0, 1, 0};
    int t_3[] = {0, 1, 1};
    int t_4[] = {1, 0, 0};
    int t_5[] = {1, 0, 1};
    int t_6[] = {1, 1, 0};
    int t_7[] = {1, 1, 1};


    public Modulator(int[] data, double sample_rate, double symbol_size, int number_of_carriers) {
        this.data = data;
        this.sample_rate = sample_rate;
        this.symbol_size = symbol_size;
        this.sample_period = 1 / sample_rate;
        this.number_of_carriers = RigidData.number_of_carriers;
        this.fs = RigidData.fs;
        initFrequencies();
        initCarriers();
    }

    public void initFrequencies() {
        frequencies = new int[number_of_carriers];
        frequencies[0] = fs;
        for (int i = 1; i < number_of_carriers; i++) {
            frequencies[i] = frequencies[i -1] + 625;
        }
    }

    public void initCarriers() {
        carriers = new ArrayList<SignalGenerator>();
        for (int i = 0; i < number_of_carriers; i++) {
            SignalGenerator s = new SignalGenerator(symbol_size, frequencies[i], 1.0 / 44100.0);
            carriers.add(s);
        }
    }

    public void modulate() {

        double level_t = Math.log(number_of_carriers) / Math.log(2);
        int level = (int) level_t;

        int temp[] = new int[level];
        modulated = new ArrayList<Double>();
        for(int i = 0; i < 10000; i++) {
            modulated.add(0.002);
        }

        modulated.addAll(carriers.get(0).generate_sync());
        System.out.println("data length " + data.length);
        for (int i = 0; i < data.length - (level - 1); i += level) {
            temp[0] = data[i];
            temp[1] = data[i + 1];
            temp[2] = data[i + 2];
            if(level == 4) {
                temp[3] = data[i + 3];
            }
            if(level == 3) {
                map8(temp);
            } else if(level == 4) {
                map16(temp);
            }
        }
    }

    public void map8(int[] temp) {
        if (Arrays.equals(temp, t_0)) {
            modulated.addAll(carriers.get(0).generate());

        } else if (Arrays.equals(temp, t_1)) {
            modulated.addAll(carriers.get(1).generate());

        } else if (Arrays.equals(temp, t_2)) {
            modulated.addAll(carriers.get(2).generate());

        } else if (Arrays.equals(temp, t_3)) {
            modulated.addAll(carriers.get(3).generate());

        } else if (Arrays.equals(temp, t_4)) {
            modulated.addAll(carriers.get(4).generate());

        } else if (Arrays.equals(temp, t_5)) {
            modulated.addAll(carriers.get(5).generate());

        } else if (Arrays.equals(temp, t_6)) {
            modulated.addAll(carriers.get(6).generate());

        } else if (Arrays.equals(temp, t_7)) {
            modulated.addAll(carriers.get(7).generate());
        }
    }

    public void map16  (int[] temp   )   {
        if (Arrays.equals(temp, s_0)) {
            modulated.addAll(carriers.get(0).generate());

        } else if (Arrays.equals(temp, s_1)) {
            modulated.addAll(carriers.get(1).generate());

        } else if (Arrays.equals(temp, s_2)) {
            modulated.addAll(carriers.get(2).generate());

        } else if (Arrays.equals(temp, s_3)) {
            modulated.addAll(carriers.get(3).generate());

        } else if (Arrays.equals(temp, s_4)) {
            modulated.addAll(carriers.get(4).generate());

        } else if (Arrays.equals(temp, s_5)) {
            modulated.addAll(carriers.get(5).generate());

        } else if (Arrays.equals(temp, s_6)) {
            modulated.addAll(carriers.get(6).generate());

        } else if (Arrays.equals(temp, s_7)) {
            modulated.addAll(carriers.get(7).generate());

        } else if (Arrays.equals(temp, s_8)) {
            modulated.addAll(carriers.get(8).generate());

        } else if (Arrays.equals(temp, s_9)) {
            modulated.addAll(carriers.get(9).generate());

        } else if (Arrays.equals(temp, s_10)) {
            modulated.addAll(carriers.get(10).generate());

        } else if (Arrays.equals(temp, s_11)) {
            modulated.addAll(carriers.get(11).generate());

        } else if (Arrays.equals(temp, s_12)) {
            modulated.addAll(carriers.get(12).generate());

        } else if (Arrays.equals(temp, s_13)) {
            modulated.addAll(carriers.get(13).generate());

        } else if (Arrays.equals(temp, s_14)) {
            modulated.addAll(carriers.get(14).generate());

        } else if (Arrays.equals(temp, s_15)) {
            modulated.addAll(carriers.get(15).generate());
        }
    }

    public ArrayList<Double> getModulated() {
        return modulated;
    }
}

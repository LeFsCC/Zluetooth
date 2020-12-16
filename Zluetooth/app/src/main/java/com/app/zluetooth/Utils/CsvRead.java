package com.app.zluetooth.Utils;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;

public class CsvRead {

    String root = Environment.getExternalStorageDirectory().toString();
    String filePath = "/0ZlueTooth/";
    String filename = "res.csv";

    int sample_rate;
    BigDecimal symbol_duration;
    int frequency_low;
    int frequency_high;
    ArrayList<Integer> onsetList = new ArrayList<>();

    public void read() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(root + filePath + filename), "utf-8"));
            String line = null;
            line=reader.readLine();
            String item[] = line.split(",");
            sample_rate = Integer.parseInt(item[item.length-1]);
            System.out.println(sample_rate);
            line=reader.readLine();
            item = line.split(",");

            symbol_duration = new BigDecimal(item[item.length-1]);
            System.out.println(symbol_duration.toPlainString());
            line=reader.readLine();
            item = line.split(",");
            frequency_low = Integer.parseInt(item[item.length-1]);
            line=reader.readLine();
            item = line.split(",");
            frequency_high = Integer.parseInt(item[item.length-1]);


            line  = reader.readLine();
            while((line=reader.readLine())!=null){
                item = line.split(",");
                System.out.println(String.valueOf(item[3]));
                int onset = Integer.parseInt(item[3]);
                onset += 24000;
                onsetList.add(onset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getFrequency_high() {
        return frequency_high;
    }

    public int getFrequency_low() {
        return frequency_low;
    }

    public BigDecimal getSymbol_duration() {
        return symbol_duration;
    }

    public int getSample_rate() {
        return sample_rate;
    }

    public ArrayList<Integer> getOnsetList() {
        return onsetList;
    }
}

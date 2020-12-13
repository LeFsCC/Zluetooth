package com.app.zluetooth.Utils;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.security.PublicKey;
import java.util.ArrayList;

public class CSVFile {
    private ArrayList<ArrayList<Integer>> data = new ArrayList<>();

    public void setData(ArrayList<ArrayList<Integer>> data) {
        this.data = data;
    }

    public void writeCsvFile() {
        String root = Environment.getExternalStorageDirectory().toString();
        String filePath = "E://0//";     // 0ZlueTooth/
        String fileName = "CSV_1.csv";
        File csvFile = new File(filePath, fileName);
        BufferedWriter csvWriter;
        try{
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();
            csvWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GB2312"), 1024);
            for (ArrayList<Integer> bits : data) {
                for(Integer e : bits) {
                    String rowStr = "\"" + e + "\",";
                    csvWriter.write(rowStr);
                }
                csvWriter.newLine();
            }
            csvWriter.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearData() {
        this.data.clear();
    }
}

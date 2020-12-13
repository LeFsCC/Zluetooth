package com.app.zluetooth;

import com.app.zluetooth.Utils.CSVFile;
import com.app.zluetooth.Utils.DataPacket;
import com.app.zluetooth.Utils.RigidData;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        CSVFile csvFile = new CSVFile();
        ArrayList<Integer> t = new ArrayList<>();
        ArrayList<ArrayList<Integer>> row = new ArrayList<>();
        t.add(1);
        t.add(2);
        t.add(3);
        row.add(t);
        row.add(t);
        row.add(t);
        csvFile.setData(row);
        csvFile.writeCsvFile();
    }
}

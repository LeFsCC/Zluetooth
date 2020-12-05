package com.app.zluetooth;

import com.app.zluetooth.Utils.DataPacket;
import com.app.zluetooth.Utils.RigidData;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String s = Integer.toBinaryString(2);
        char[] t = s.toCharArray();
        ArrayList<Integer> bits = new ArrayList<>();
        int j = 3 - t.length;
        
        for(int k = 0; k < j;k++) {
            bits.add(0);
        }

        for(int i = 0; i < s.length(); i++) {
            bits.add(Integer.parseInt(String.valueOf(t[i])));
        }

        for(int i = 0; i < bits.size(); i++) {
            System.out.println(bits.get(i));
        }
    }
}

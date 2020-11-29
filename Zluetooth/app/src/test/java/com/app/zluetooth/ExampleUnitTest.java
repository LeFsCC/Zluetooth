package com.app.zluetooth;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        double[] in = new double[4];
        for(int i = 0;i < 4; i++) {
            in[i] = i;
        }
        double[] out = new double[8];
        System.arraycopy(in, 0, out, 0, 4);
        System.out.println(Arrays.toString(out));
    }
}

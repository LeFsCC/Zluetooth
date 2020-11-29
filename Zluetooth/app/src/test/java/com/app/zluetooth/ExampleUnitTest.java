package com.app.zluetooth;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        int[] temp = {0, 1, 1};
        String ee = "";
        for(int t: temp) {
            ee += String.valueOf(t);
        }
        System.out.println(ee);
        int ii = Integer.parseInt(ee, 2);
        System.out.println(ii);
    }
}

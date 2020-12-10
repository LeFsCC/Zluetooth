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
        int[] n_bi_data = new int[5];
        int[] bi_data = {0, 1, 2};
        int[] n_data = {3, 4};
        System.arraycopy(bi_data, 0, n_bi_data, 0, bi_data.length);
        System.arraycopy(n_data, 0, n_bi_data, bi_data.length, n_data.length);
        for(int bit : n_bi_data){
            System.out.println(bit);
        }
    }
}

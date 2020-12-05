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
        String src = "sfdsdf safds1f56 sdf11sdfawef1vcf vc";
        if(src.length() == 0){
            return ;
        }

        ArrayList<DataPacket> dataPackets = new ArrayList<>();
        int start_index = 0;
        while(start_index < src.length()){
            DataPacket dataPacket = new DataPacket();
            if(src.length() - start_index < RigidData.number_of_letter_each_packet) {
                dataPacket.setData(src.substring(start_index));
            } else {
                dataPacket.setData(src.substring(start_index, start_index + RigidData.number_of_letter_each_packet));
            }
            start_index += RigidData.number_of_letter_each_packet;
            dataPackets.add(dataPacket);
        }
        System.out.println(dataPackets.size());
        for(int i = 0; i < dataPackets.size(); i++) {
            System.out.println(dataPackets.get(i).getData());
        }
    }
}

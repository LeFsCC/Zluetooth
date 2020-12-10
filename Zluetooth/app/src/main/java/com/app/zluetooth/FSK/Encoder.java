package com.app.zluetooth.FSK;

import android.annotation.SuppressLint;
import android.content.Context;

import com.app.zluetooth.Utils.DataPacket;
import com.app.zluetooth.Utils.AudioHandler;
import com.app.zluetooth.Utils.RigidData;
import com.app.zluetooth.Utils.StringAndBinary;

import java.util.ArrayList;
import java.util.Arrays;

public class Encoder {

    private static double  symbol_size;
    private static int []  data;
    private static ArrayList<Double> modulated = new ArrayList<>();
    private static ArrayList<DataPacket> dataPackets;

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private ArrayList<DataPacket> splitPacket(String src) {
        if(src.length() == 0){
            return null;
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
        for(int i = 0; i < dataPackets.size(); i++) {
            System.out.println(dataPackets.get(i).getData());
        }
        return dataPackets;
    }

    // 数据包分包
    public Encoder(String src, double symbol_size, Context context) {
        Encoder.symbol_size = symbol_size;
        Encoder.context = context;
        try {
            dataPackets = splitPacket(src);
            if(dataPackets != null) {
                for(int i = 0; i < dataPackets.size(); i++) {
                    DataPacket dataPacket = dataPackets.get(i);
                    StringAndBinary stringToBinary = new StringAndBinary(dataPacket.getData());
                    // 获取数据
                    int[] data = stringToBinary.getB();
                    // 在包头加上包长，用8位表示
                    System.out.println("packet length " + data.length);
                    dataPacket.setBi_data(getPacketLengthBi(data.length));
                    // 加上数据
                    dataPacket.add_data(data);
                }
            }
        } catch (Exception ignored){

        }
        initModulator();
    }

    // 将包长转化为8位二进制
    private int[] getPacketLengthBi(int len) {
        String s = Integer.toBinaryString(len);
        char[] t = s.toCharArray();
        int[] bits = new int[9];
        for(int i = 0; i < 9 - s.length(); i++) {
            bits[i] = 0;
        }
        for(int i = 9 - s.length(); i < s.length(); i++) {
            bits[i] = Integer.parseInt(String.valueOf(t[i - (9 - s.length())]));
        }
        System.out.println(Arrays.toString(bits));
        return bits;
    }

    public void initModulator () {
        Modulator fsk_modulator = new Modulator(symbol_size);
        if(dataPackets!=null){
            for(int i = 0; i < dataPackets.size(); i++) {
                fsk_modulator.setData(dataPackets.get(i).getBi_data());
                fsk_modulator.modulate();
                modulated.addAll(fsk_modulator.getModulated());
            }

        }else{
            fsk_modulator.modulate();
            modulated.addAll(fsk_modulator.getModulated());
        }
    }

    public void writeAudio (){
        Double[] r = new Double[modulated.size()];
        for (int i = 0; i <modulated.size() ; i++) {
            r[i] = modulated.get(i);
        }
        AudioHandler audio_handler = new AudioHandler(r, context, "FSK.wav");
        audio_handler.writeFile();
        modulated.clear();
        audio_handler.close();
    }
}

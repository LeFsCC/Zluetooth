package com.app.zluetooth.FSK;

import android.annotation.SuppressLint;
import android.content.Context;

import com.app.zluetooth.Utils.DataPacket;
import com.app.zluetooth.Utils.MyAudio;
import com.app.zluetooth.Utils.RigidData;
import com.app.zluetooth.Utils.StringAndBinary;

import java.util.ArrayList;

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

    public Encoder(String src, double symbol_size, Context context) {
        Encoder.symbol_size = symbol_size;
        Encoder.context = context;
        try {
            dataPackets = splitPacket(src);
            if(dataPackets != null) {
                for(int i = 0; i < dataPackets.size(); i++) {
                    DataPacket dataPacket = dataPackets.get(i);
                    StringAndBinary stringToBinary = new StringAndBinary(dataPacket.getData());
                    dataPacket.setBi_data(stringToBinary.getB());
                }
            }
        } catch (Exception ignored){

        }
        initModulator();
    }

    public void initModulator () {
        Modulator fsk_modulator = new Modulator(symbol_size);
        for(int i = 0; i < dataPackets.size(); i++) {
            fsk_modulator.setData(dataPackets.get(i).getBi_data());
            fsk_modulator.modulate();
            modulated.addAll(fsk_modulator.getModulated());
        }
    }

    public void writeAudio (){
        Double[] r = new Double[modulated.size()];
        for (int i = 0; i <modulated.size() ; i++) {
            r[i] = modulated.get(i);
        }
        MyAudio audio_handler = new MyAudio(r, context, "FSK.wav");
        audio_handler.writeFile();
        audio_handler.close();
    }
}

package com.app.zluetooth.Utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class MyAudio {

    private double sample_rate;
    private double duration;
    private long n_frames;
    private WavFile wavfile;
    private double[] data;
    private String filename;
    private ArrayList<Double> modulated;


    public MyAudio(Context context, String filename) {
        this.filename = filename;
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            this.wavfile = WavFile.openWavFile(new File(root, "0ZlueTooth/" + filename));
            wavfile.display();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MyAudio(Double[] src, Context context, String filename) { //Overloaded Constructor For Writing
        this.sample_rate = 44100;
        this.duration = src.length / sample_rate;
        this.n_frames = (long) (duration * sample_rate);
        this.filename = filename;

        data = new double[src.length];
        for (int i = 0; i < src.length; i++) {
            data[i] = (double) src[i];
        }
        initWrite();
    }


    public void initWrite() {
        try {
            if (canWriteOnExternalStorage()) {

                String root = Environment.getExternalStorageDirectory().toString();
                File folder = new File(root, "0ZlueTooth");
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                n_frames = data.length;
                this.wavfile = WavFile.newWavFile(new File(root, "0ZlueTooth/" + filename), 1, n_frames, 16, (long) sample_rate);
                wavfile.display();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void writeFile() {

        long frameCounter = 0;
        double[] buffer = new double[100];
        int index = 0;

        while (frameCounter < n_frames) {
            long remaining = wavfile.getFramesRemaining();
            int toWrite = (remaining > 100) ? 100 : (int) remaining;

            for (int s = 0; s < toWrite; s++, frameCounter++, index++) {
                buffer[s] = data[index];
            }
            try {
                wavfile.writeFrames(buffer, toWrite);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Double> read() {
        modulated = new ArrayList<>();
        double[] buffer = new double[100];
        int framesRead;
        try {
            do {
                framesRead = wavfile.readFrames(buffer, 100);
                for (int s = 0; s < framesRead; s++) {
                    Double temp = (Double) buffer[s];
                    modulated.add(temp);
                }
            }
            while (framesRead != 0);
            close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return modulated;
        }
    }

    public void close() {
        try {
            wavfile.close();
        } catch (Exception e) {
            Log.d("0ZlueTooth", e.toString());
        }
    }

    public static boolean canWriteOnExternalStorage() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

}

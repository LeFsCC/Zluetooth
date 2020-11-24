package com.app.zluetooth;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * File sdcard = Environment.getExternalStorageDirectory();
 * <p/>
 * Created by misha on 2016/09/13.
 */
//音频处理类
public class AudioHandler {

    private double sample_rate;
    private double duration;
    private long n_frames;
    private WavFile wavfile;
    private Double[] src;
    private double[] data;
    private String filename;
    private ArrayList<Double> modulated;
    private ArrayList<Double> recordedData;
    private long n;
    private static Context context;
// 初始化获得当前场景，文件名
    public AudioHandler(Context context, String filename) {
        AudioHandler.context = context;
        this.filename = filename;

        try {

            String root = Environment.getExternalStorageDirectory().toString();
            this.wavfile = WavFile.openWavFile(new File(root, "RedTooth/" + filename));
            wavfile.display();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public AudioHandler(Double[] src, Context context, String filename) { //Overloaded Constructor For Writing
        this.src = src;
        System.out.println("This is the size of the modulated file: " + src.length);
        this.sample_rate = 44100;
        this.duration = src.length / sample_rate;
        this.n_frames = (long) (duration * sample_rate);
        this.filename = filename;

        AudioHandler.context = context;

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
                Log.e("文件路径",folder.getAbsolutePath());
                Log.e("文件是否存在", String.valueOf(folder.exists()));
                n_frames = data.length;

                this.wavfile = WavFile.newWavFile(new File(root, "0ZlueTooth/" + filename), 1, n_frames, 16, (long) sample_rate);
                System.out.println("Wav File Written!");
                wavfile.display();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void addBuffer(short[] audioBuffer) {
        for (int i = 0; i < audioBuffer.length; i++) {
            recordedData.add((double) audioBuffer[i]);
        }
    }

    public void writeFile() {


        long frameCounter = 0;
        double[] buffer = new double[100];
        int index = 0;


        while (frameCounter < n_frames) {
            // Determine how many frames to write, up to a maximum of the buffer size
            long remaining = wavfile.getFramesRemaining();
            int toWrite = (remaining > 100) ? 100 : (int) remaining;

            for (int s = 0; s < toWrite; s++, frameCounter++, index++) {
                buffer[s] = data[index];
            }
            try {

                // Write the buffer
                wavfile.writeFrames(buffer, toWrite);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }


    public ArrayList<Double> read() {
        modulated = new ArrayList<Double>();
        n = 0;
        double[] buffer = new double[100];
        int framesRead;
        try {
            do {
                // Read frames into buffer
                framesRead = wavfile.readFrames(buffer, 100);

                // Loop through frames and write values to demodulated data
                for (int s = 0; s < framesRead; s++) {
                    Double temp = (Double) buffer[s];
                    modulated.add(temp);
                }
            }
            while (framesRead != 0);

            // Close the wavFile
            close();
            // Output the minimum and maximum value

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
            Log.d("RedTooth", e.toString());
        }
    }

    public static boolean canWriteOnExternalStorage() {
        // get the state of your external storage
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // if storage is mounted return true
            Log.d("Zluetooth", "Yes, can write to external storage.");
            return true;
        }
        return false;
    }

    public void getInfo() {
        wavfile.display();

    }
}

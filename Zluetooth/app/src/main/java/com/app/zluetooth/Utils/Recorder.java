package com.app.zluetooth.Utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AutomaticGainControl;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class Recorder {

    //Begin private fields for this class
    private AudioRecord recorder;

    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = "recorded.wav";
    private static final String AUDIO_RECORDER_FOLDER = "0ZlueTooth";
    private static final String AUDIO_RECORDER_TEMP_FILE = "recorded.raw";
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_CHANNELS_INT = 1;

    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private int bufferSize = 200000;
    short[] buffer;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    public ArrayList<Double> volume = new ArrayList<>();

    //Constructor
    public Recorder(String uniquename) {
        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

        System.out.println("BUFFER SIZE VALUE IS " + bufferSize);

        int buffercount = 4088 / bufferSize;
        if (buffercount < 1)
            buffercount = 1;
        recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, 44100);

        if (AutomaticGainControl.isAvailable()) {
            System.out.println("AGC IS AVAILIABLE");
            AutomaticGainControl agc = AutomaticGainControl.create(
                    recorder.getAudioSessionId()
            );
            agc.setEnabled(false);
        }
        else{
            System.out.println("AGC NOT AVAILIABLE");
        }
    }

    public void start() throws IllegalStateException, IOException {

        buffer = new short[4088];

        recorder.startRecording();

        isRecording = true;

        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");

        recordingThread.start();
    }

    public void stop() {
        System.out.println("Told to stop");
        stopRecording();
    }

    public boolean isRecording() {
        return recorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING;
    }


    private void stopRecording() {
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
        copyWaveFile(getTempFilename(), getFilename());
    }

    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    private void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = RECORDER_CHANNELS_INT;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

           WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            byte[] bytes2 = new byte[buffer.length * 2];
            ByteBuffer.wrap(bytes2).order(ByteOrder.LITTLE_ENDIAN)
                    .asShortBuffer().put(buffer);
            while (in.read(bytes2) != -1) {
                out.write(bytes2);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTempFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        File tempFile = new File(filepath, AUDIO_RECORDER_TEMP_FILE);

        if (tempFile.exists())
            tempFile.delete();
        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    private void writeAudioDataToFile() {
        byte data[] = new byte[bufferSize];

        String filename = getTempFilename();
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int read = 0;

        while (isRecording) {
            int r = recorder.read(buffer, 0, buffer.length);
            long v = 0;
            for(short value : buffer) {
                v += value * value;
            }
            double mean = v / (double) r;
            this.volume.add(10 * Math.log10(mean));

            if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                try {
                    byte[] bytes2 = new byte[buffer.length * 2];
                    ByteBuffer.wrap(bytes2).order(ByteOrder.LITTLE_ENDIAN)
                            .asShortBuffer().put(buffer);

                    os.write(bytes2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getVolume() {
        double sum = 0;
        for(double t : volume) {
            sum += t;
        }
        int size = volume.size();
        volume.clear();
        return sum / size;
    }

    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        System.out.println("---9---");
        byte[] header = new byte[4088];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) RECORDER_CHANNELS_INT;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (RECORDER_CHANNELS_INT * RECORDER_BPP / 8); // block align
        header[33] = 0;
        header[34] = RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 4088);
    }
}
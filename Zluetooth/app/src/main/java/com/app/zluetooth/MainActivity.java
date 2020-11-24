package com.app.zluetooth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.zluetooth.Permissions;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Transmitter transmitter;
    private Receiver receiver;
    private String modulation;
    private String src;
    private double duration;
    private double sample_rate;
    private double symbol_size;
    private double sample_period;
    private int number_of_carriers;
    private MediaPlayer mediaplayer;

    private String recovered_string;
    private EditText mEdit;
    private Button decode_btn;
    private Button transmit_btn;
    private Button receive_btn;
    private Button encode_btn;
    private TextView recovered_textView;
    private static String TAG = "Permission";
    private static final int REQUEST_WRITE_STORAGE = 112;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        modulation = "FSK";
        sample_rate = 44100.0;
        symbol_size = 0.5;
        sample_period = 1.0 / sample_rate;
        duration = 12;     //duration = src.length * 16 * symbol_size / 7
        number_of_carriers = 16;

        Permissions.requestWritePermissions(this, MainActivity.this);
        Permissions.requestRecordPermissions(this, MainActivity.this);
        decode_btn = findViewById(R.id.decode_btn);
        transmit_btn = findViewById(R.id.transmit_btn);
        receive_btn = findViewById(R.id.receive_btn);
        encode_btn = findViewById(R.id.encode_btn);
        decode_btn.setOnClickListener(this);
        transmit_btn.setOnClickListener(this);
        receive_btn.setOnClickListener(this);
        encode_btn.setOnClickListener(this);

        mEdit = findViewById(R.id.raw_data_txt);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length == 0
                    || grantResults[0] !=
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "permission denied ", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "permission denied ");
            } else {
                Toast.makeText(this, "permission granted ", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "permission granted ");
            }
        }
    }

    public void initReceive() {
        try {
            record();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initTransmit() {
        mediaplayer = new MediaPlayer();
        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root, "0ZlueTooth");
        if (!dir.exists()) {
            dir.mkdir();
        }

        try {
            mediaplayer.setDataSource(dir + File.separator + "FSK.wav");
            mediaplayer.prepare();
            mediaplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generate() {
        transmitter = new Transmitter(modulation, src, sample_rate, symbol_size, sample_period, number_of_carriers, getApplicationContext());
        System.out.println("Writing WavFile");
        transmitter.writeAudio();
        System.out.println("WaveFile Written. Thread waiting");
    }

    public void record() {
        long startTime = System.nanoTime();

        receiver = new Receiver("recorded.wav", sample_rate, symbol_size, duration, number_of_carriers, getApplicationContext());
        receiver.record();
        receiver.demodulate();
        recovered_string = "aaaa";
        recovered_string = receiver.getRecoverd_string();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println("Time taken for Reception: " + duration + "ms");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.receive_btn: {
                initReceive();
                break;
            }
            case R.id.decode_btn:{
                break;
            }
            case R.id.transmit_btn:{
                initTransmit();
                break;
            }
            case R.id.encode_btn:{
                Toast.makeText(this, "别点我", Toast.LENGTH_SHORT).show();
                final Context context = getApplicationContext();
                src = mEdit.getText().toString();
                while (src.length() < 5) {
                    src += " ";
                }
                generate();
                break;
            }


        }
    }
}

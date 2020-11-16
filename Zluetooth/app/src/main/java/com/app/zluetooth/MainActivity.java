package com.app.zluetooth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.zluetooth.Permissions;

public class MainActivity extends AppCompatActivity {

    private Transmitter transmitter;
    private Receiver receiver;
    private String modulation;
    private String srcData;
    private double duration;
    private double sample_rate;
    private double symbol_size;
    private double sample_period;
    private int number_of_carriers;
    private MediaPlayer mediaplayer;

    private String recovered_string;
    private EditText mEdit;
    private Button generate;
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
}

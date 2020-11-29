package com.app.zluetooth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

import com.app.zluetooth.FSK.Receiver;
import com.app.zluetooth.FSK.Transmitter;
import com.app.zluetooth.Utils.Permissions;
import com.app.zluetooth.Utils.RigidData;
import com.app.zluetooth.WiFiComm.ApManager;
import com.app.zluetooth.WiFiComm.Client;
import com.app.zluetooth.WiFiComm.Server;

import java.io.File;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Transmitter transmitter;
    private Receiver receiver;
    private String src;
    private double sample_rate;
    private double symbol_size;
    private double sample_period;
    private double duration;

    private int number_of_carriers;
    private MediaPlayer mediaplayer;

    private String recovered_string;

    private EditText mEdit;
    private Button decode_btn;
    private Button transmit_btn;
    private Button receive_btn;
    private Button encode_btn;
    private String modulation = "FSK";
    private TextView recovered_textView;


    private static String TAG = "Permission";
    private static final int REQUEST_WRITE_STORAGE = 112;

    private Server server = null;
    private Client client = null;
    private TextView server_ip_txt;
    private TextView server_port_txt;
    private Button open_server_btn;
    private Button open_client_btn;
    private EditText input_ip;
    private EditText input_port;
    private Button send_msg;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        modulation = "FSK";

        duration = 32;
        sample_rate = RigidData.sample_rate;
        symbol_size = RigidData.symbol_size;
        sample_period = 1.0 / sample_rate;

        number_of_carriers = RigidData.number_of_carriers;
        askPermission();
        initComView();
    }

    private void askPermission() {
        Permissions.requestWritePermissions(this, MainActivity.this);
        Permissions.requestRecordPermissions(this, MainActivity.this);
        Permissions.askForPermission(this);
    }

    private void initComView() {
        decode_btn = findViewById(R.id.decode_btn);
        transmit_btn = findViewById(R.id.transmit_btn);
        receive_btn = findViewById(R.id.receive_btn);
        encode_btn = findViewById(R.id.encode_btn);
        recovered_textView = findViewById(R.id.decode_data_txt);

        decode_btn.setOnClickListener(this);
        transmit_btn.setOnClickListener(this);
        receive_btn.setOnClickListener(this);
        encode_btn.setOnClickListener(this);

        mEdit = findViewById(R.id.raw_data_txt);

        Button dis_btn = findViewById(R.id.dis_btn);
        dis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_dis);
                initDisView();
            }
        });
    }

    private void initDisView() {
        server_ip_txt = findViewById(R.id.server_ip);
        server_port_txt = findViewById(R.id.server_port);
        open_server_btn = findViewById(R.id.open_server);
        open_client_btn = findViewById(R.id.open_client);
        input_ip = findViewById(R.id.server_ip_in);
        input_port = findViewById(R.id.server_port_in);

        Button com_btn = findViewById(R.id.com_btn2);

        com_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(server != null) {
                    server.closeServer();
                }
                if(client != null) {
                    client.closeClient();
                }

                setContentView(R.layout.activity_main);
                initComView();
            }
        });

        open_server_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                server = null;
                server = new Server(8185,MainActivity.this, getApplicationContext());
                server.start();
            }
        });

        open_client_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ip = input_ip.getText().toString();
                final String port = input_port.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client = null;
                        client = new Client(ip, Integer.parseInt(port), MainActivity.this, getApplicationContext());
                    }
                }).start();
            }
        });

        send_msg = findViewById(R.id.send_msg);

        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    client.sendMessage("this is a message");
                } catch (Exception e) {
                    final String ip = input_ip.getText().toString();
                    final String port = input_port.getText().toString();
                    client = null;
                    client = new Client(ip, Integer.parseInt(port), MainActivity.this, getApplicationContext());
                    Toast.makeText(getApplicationContext(), "连接被重置, 请重新发送", Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            String Server_IP = ApManager.getLocalIpAddress(getApplicationContext());
            server_ip_txt.setText(Server_IP);
            server_port_txt.setText("8185");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
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
                boolean y = Permissions.defaultPermissionCheck(getApplicationContext());
                System.out.println("wifi permission on :" + y);
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
        System.gc();
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
        System.gc();
        transmitter = new Transmitter(src, sample_rate, symbol_size, number_of_carriers, getApplicationContext());
        System.out.println("Writing WavFile");
        transmitter.writeAudio();
        System.out.println("WaveFile Written. Thread waiting");
    }

    public void record() {
        recovered_textView.setText("");
        Toast.makeText(this, "开始录音", Toast.LENGTH_SHORT).show();
        receiver = new Receiver("recorded.wav", sample_rate, symbol_size, number_of_carriers, getApplicationContext());
        receiver.record_start();
    }

    public void decode() {
        Toast.makeText(this, "录音结束", Toast.LENGTH_SHORT).show();
        receiver.record_stop();
        receiver.recover_signal();
        recovered_string = "";
        recovered_string = receiver.getRecoverd_string();
        recovered_textView.setText(recovered_string);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.receive_btn: {
                initReceive();
                break;
            }
            case R.id.decode_btn:{
                decode();
                break;
            }
            case R.id.transmit_btn:{
                initTransmit();
                break;
            }
            case R.id.encode_btn:{
                src = mEdit.getText().toString();

                while (src.length() < 5) {
                    src += " ";
                }
                generate();
                Toast.makeText(this, "编码完成", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
}

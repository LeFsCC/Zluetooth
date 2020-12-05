package com.app.zluetooth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

import com.app.zluetooth.FSK.Decoder;
import com.app.zluetooth.FSK.Encoder;
import com.app.zluetooth.Utils.Permissions;
import com.app.zluetooth.Utils.RigidData;
import com.app.zluetooth.WiFiComm.ApManager;
import com.app.zluetooth.WiFiComm.Client;
import com.app.zluetooth.WiFiComm.Server;

import java.io.File;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Encoder encoder;
    private Decoder receiver;
    private String src;
    private double sample_rate;
    private double symbol_size;

    private MediaPlayer mediaplayer;

    private String recovered_string;

    private EditText mEdit;
    private Button decode_btn;
    private Button transmit_btn;
    private Button receive_btn;
    private Button encode_btn;
    private TextView recovered_textView;
    private TextView distance_txt;


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
        setContentView(R.layout.activity_dis);
        sample_rate = RigidData.sample_rate;
        symbol_size = RigidData.symbol_size;
        askPermission();
        initDisView();
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


    @SuppressLint("SetTextI18n")
    private void initDisView() {
        server_ip_txt = findViewById(R.id.server_ip);
        server_port_txt = findViewById(R.id.server_port);
        open_server_btn = findViewById(R.id.open_server);
        open_client_btn = findViewById(R.id.open_client);
        input_ip = findViewById(R.id.server_ip_in);
        input_port = findViewById(R.id.server_port_in);
        distance_txt = findViewById(R.id.distance_txt);

        findViewById(R.id.transmit_dis_btn).setOnClickListener(this);
        findViewById(R.id.rev_start).setOnClickListener(this);

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

        // 启动服务器
        open_server_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                server = null;
                server = new Server(8185,MainActivity.this, getApplicationContext());
                server.start();
            }
        });

        // 启动客户端
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

        // 客户端向服务端发信息
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

        // 服务端获取ip和port
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
        encoder = new Encoder(src, symbol_size, getApplicationContext());
        encoder.writeAudio();
    }

    public void record() {
        recovered_textView.setText("");
        Toast.makeText(this, "开始录音", Toast.LENGTH_SHORT).show();
        receiver = new Decoder("recorded.wav", sample_rate, symbol_size, getApplicationContext());
        receiver.record_start();
    }

    public void decode() {
        Toast.makeText(this, "录音结束", Toast.LENGTH_SHORT).show();
        receiver.record_stop();
        receiver.recover_data_packet();
        recovered_string = "";
        recovered_string = receiver.getRecoverd_string();
        recovered_textView.setText(recovered_string);
    }

    double A_0 = 0;
    double A_3 = 0;
    double A_1 = 0;
    double B_2 = 0;
    double B_3 = 0;
    double B_1 = 0;

    public void server_start(){
        rec_time1 = 0;
        rec_time2 = 0;
        receiver = new Decoder("recorded.wav", RigidData.sample_rate, RigidData.dis_symbol_size, getApplicationContext());
        new Thread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                receiver.record_start();
                double start_time = System.currentTimeMillis();
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                encoder = new Encoder("", RigidData.dis_symbol_size, getApplicationContext());
                encoder.writeAudio();
                A_0 = System.currentTimeMillis();
                initTransmit();
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                receiver.record_stop();
                rec_time1=receiver.locate_start(0);
                A_1 = start_time + rec_time1 * 1000.0 / RigidData.sample_rate;

                Log.e("A_1", String.valueOf((rec_time1 * 1000.0 / RigidData.sample_rate)));

                rec_time2=receiver.locate_start(rec_time1 + 1000);
                A_3 = start_time + rec_time2 * 1000.0 / RigidData.sample_rate;

                Log.e("A_3", String.valueOf((rec_time2 * 1000.0 / RigidData.sample_rate)));

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String timeStamp = server.getTimeStamp();
                Log.e("stamp", timeStamp);

                String[] str = timeStamp.split(";");
                B_2 = Double.parseDouble(str[0]);
                B_1 = Double.parseDouble(str[1]);
                B_3 = Double.parseDouble(str[2]);

                Log.e("A3 - A1", String.valueOf(A_3 - A_1));
                Log.e("B3 - B1", String.valueOf(B_3 - B_1));

                final double d = 0.17 * ((A_3 - A_1) - (B_3 - B_1)) ;

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView distance_txt = findViewById(R.id.distance_txt);
                        distance_txt.setText((int)(Math.abs(d * 100)) + "cm");
                    }
                });
                Log.e("距离", String.valueOf(Math.abs(d * 100)));
            }
        }).start();
    }

    int rec_time1 = 0;
    int rec_time2 = 0;

    public void client_start(){
        rec_time1=0;
        rec_time2=0;
        receiver = new Decoder("recorded.wav", RigidData.sample_rate, RigidData.dis_symbol_size, getApplicationContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
                receiver.record_start();
                double start_time = System.currentTimeMillis();
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //发声波
                encoder = new Encoder("", RigidData.dis_symbol_size, getApplicationContext());
                encoder.writeAudio();
                B_2 = System.currentTimeMillis();
                initTransmit();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                receiver.record_stop();
                rec_time1=receiver.locate_start(0);
                B_1 = start_time  + rec_time1 * 1000.0 / RigidData.sample_rate;
                rec_time2 = receiver.locate_start(rec_time1 + 1000);
                B_3 = start_time + rec_time2 * 1000.0 / RigidData.sample_rate;

                Log.e("B_1", String.valueOf(rec_time1 * 1000.0 / RigidData.sample_rate));
                Log.e("B_3", String.valueOf(rec_time2 * 1000.0 / RigidData.sample_rate));

                client.sendMessage(B_2 + ";" + B_1 + ";" + B_3);
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.transmit_dis_btn:
                Toast.makeText(getApplicationContext(), "开始发射声波", Toast.LENGTH_SHORT).show();
                server_start();
                break;
            case R.id.rev_start:
                client_start();
                break;
            case R.id.receive_btn:
                initReceive();
                break;
            case R.id.decode_btn:
                decode();
                break;
            case R.id.transmit_btn:
                initTransmit();
                break;
            case R.id.encode_btn:
                src = mEdit.getText().toString();
                generate();
                Toast.makeText(this, "编码完成", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}

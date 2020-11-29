package com.app.zluetooth.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//  获得各种权限
public class Permissions {
    private static String TAG = "Permission";
    private static final int REQUEST_WRITE_STORAGE = 112;

//  记录权限/录音
    public static void requestRecordPermissions(final Context context, final Activity activity) {
        int permission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to Record Audio denied");

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.RECORD_AUDIO)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Permission to Record Audio")
                        .setTitle("Permission required");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "Clicked");
                        makeRecordRequest(context, activity);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                makeRecordRequest(context, activity);
            }
        }
    }

    protected static void makeRecordRequest(Context context, Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_WRITE_STORAGE);
    }
//  存储权限
    public static void requestWritePermissions(final Context context, final Activity activity) {
        int permission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied");

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Permission to access the SD-CARD is required for this app to Download PDF.")
                        .setTitle("Permission required");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "Clicked");
                        makeWriteRequest(context, activity);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                makeWriteRequest(context, activity);
            }
        }
    }

    protected static void makeWriteRequest(Context context, Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_STORAGE);
    }

    public static void askForPermission(Activity activity) {

        ActivityCompat.requestPermissions(activity, new String[] {
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE
        }, REQUEST_WRITE_STORAGE);
    }

    public static boolean defaultPermissionCheck(Context context) {
        int external_storage_write = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        int a = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE);
        int b = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE);
        return external_storage_write == PackageManager.PERMISSION_GRANTED && a==PackageManager.PERMISSION_GRANTED && b==PackageManager.PERMISSION_GRANTED;
    }
}

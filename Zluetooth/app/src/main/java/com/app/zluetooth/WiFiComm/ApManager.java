package com.app.zluetooth.WiFiComm;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Enumeration;

public class ApManager {

    public static boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {}
        return false;
    }

    public static String getLocalIpAddress(Context context) throws UnknownHostException {

        if (ApManager.isApOn(context)) {

            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                     en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    if (intf.getName().contains("wlan")) {

                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && (inetAddress.getAddress().length == 4)) {
                                Log.e("error", inetAddress.getHostAddress());
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                }
            } catch (SocketException ex) {
                Log.e("error", ex.toString());
            }

        } else {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            if (wifiManager != null) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipInt = wifiInfo.getIpAddress();
                return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
            }
        }
        return null;
    }
}

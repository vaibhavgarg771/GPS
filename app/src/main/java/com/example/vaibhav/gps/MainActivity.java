package com.example.vaibhav.gps;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.Object;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService();

    }
    public void startService() {
        startService(new Intent(getBaseContext(), MyService.class));
    }

    // Method to stop the service
    public void stopService() {
        stopService(new Intent(getBaseContext(), MyService.class));
    }

    public void onPressed(View v) {
        setUpWifiHotspot("atShop", "12345678");
    }

    public void setUpWifiHotspot(String hpname, String passWord){

            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);;

            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
            Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();
            boolean methodFound = false;
            for (Method method: wmMethods) {
                if (method.getName().equals("setWifiApEnabled")) {
                    methodFound = true;
                    WifiConfiguration netConfig = new WifiConfiguration();
                    netConfig.SSID = hpname;
                    netConfig.preSharedKey= passWord;
                    netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                    netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    try {
                        boolean apstatus = (Boolean) method.invoke(wifiManager, netConfig, true);

                        for (Method isWifiApEnabled: wmMethods) {
                            if (isWifiApEnabled.getName().equals("isWifiApEnabled")) {
                                while (!(Boolean) isWifiApEnabled.invoke(wifiManager)) {};
                                for (Method method1: wmMethods) {
                                    if (method1.getName().equals("getWifiApState")) {
                                        int apstate;
                                        apstate = (Integer) method1.invoke(wifiManager);
                                        Log.i(this.getClass().toString(), "Apstate ::: "+apstate);
                                    }
                                }
                            }
                        }
                        if (apstatus) {
                            Log.d("Splash Activity", "Access Point created");
                        } else {
                            Log.d("Splash Activity", "Access Point creation failed");
                        }

                    } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!methodFound) {
                Log.d("Splash Activity",
                        "cannot configure an access point");
            }
        }
}



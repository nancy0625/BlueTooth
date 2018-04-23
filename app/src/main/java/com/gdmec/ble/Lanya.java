package com.gdmec.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.widget.Button;

import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by asus on 2018/4/23.
 */

public class Lanya extends Activity {
    private static final String TAG = "THINBTCLIENT";
    private static final boolean D = true;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;

    private OutputStream outStream = null;
    Button mButtonF;
    Button mButtonB;
    Button mButtonL;
    Button mButtonR;
    Button mButtonS;

    private static final UUID MY_UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");//蓝牙设备上的标准串行
    private static String address = "";//要连接的蓝牙设备MAC地址

}

package com.gdmec.ble;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wangkai.blecommunication.ITelephony;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static java.lang.Character.getNumericValue;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private ListView listView;
    private Button button_discover;
    private TextView textView_status;
    private TextView textView_receive;
    private CheckBox checkBox_receivehex;
    private CheckBox checkBox_sendhex;
    private EditText editText_send;
    private Button button_send;
    private List<Map<String, Object>> listItem;
    private SimpleAdapter simpleAdapter;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothDevice bluetoothDevice;
    public BluetoothGatt bluetoothGatt;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic bluetoothGattCharacteristic;
    private List<BluetoothDevice> devices = new ArrayList<>();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            bluetoothLeScanner.stopScan(scanCallback);
            textView_status.setText("搜索完成");
            textView_status.setTextColor(Color.rgb(30, 90, 80));
        }
    };

    private String str_receive;
    /**
     * 电话的下标的变化量
     */
    private int countTell = 0;
    private int flag = 0;
    private int flag1 = 0;
    private int flag2 = 0;
    private char preNum = '0';
    /**
     * 文本转语音
     */
    private TextToSpeech textToSpeech = null;
    private TextToSpeech textToSpeech1 = null;
    private TextToSpeech textToSpeech2 = null;

    private boolean isForeground = true;//界面处于前台？

    /////

    private Handler handler = new Handler();
    private static final String TAG = "mydebug";
    View view_main;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        view_main = layoutInflater.inflate(R.layout.activity_main, null);
        setContentView(view_main);
        init();




        ///

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "你点击了第" + position + "项");
                bluetoothDevice = devices.get(position);
                handler.removeCallbacks(runnable);
                bluetoothLeScanner.stopScan(scanCallback);
                if (bluetoothGatt != null)
                    bluetoothGatt.disconnect();
                bluetoothGatt = bluetoothDevice.connectGatt(MainActivity.this, false, new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        super.onConnectionStateChange(gatt, status, newState);
                        if (newState == BluetoothProfile.STATE_CONNECTED) {//状态变已连接
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    textView_status.setText("成功建立连接");
                                    textView_status.setTextColor(Color.BLUE);
                                }
                            });
                            gatt.discoverServices();//连接成功，开始搜索服务，一定要调用此方法，否则获取不到服务
                        }
                        if (newState == BluetoothProfile.STATE_DISCONNECTED) { //状态变为未连接
                            Log.e(TAG, "连接断开");
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    textView_status.setText("连接断开");
                                    textView_status.setTextColor(Color.RED);
                                    setContentView(view_main);
                                }
                            });
                            return;
                        }
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, final int status) {
                        super.onServicesDiscovered(gatt, status);
                        if (status == gatt.GATT_SUCCESS) { // 发现服务成功
                            String service_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb";
                            String characteristic_UUID = "0000fff2-0000-1000-8000-00805f9b34fb";
                            bluetoothGattService = gatt.getService(UUID.fromString(service_UUID));
                            bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(characteristic_UUID));
                            if (bluetoothGattCharacteristic != null) {
                                Log.e(TAG, "成功获取特征");
                                gatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setContentView(R.layout.layout_operate);
                                        textView_receive = (TextView) findViewById(R.id.textView_receive);
                                        textView_receive.setMovementMethod(ScrollingMovementMethod.getInstance());//开启滚动条
                                        editText_send = (EditText) findViewById(R.id.edit_send);
                                        checkBox_receivehex = (CheckBox) findViewById(R.id.checkBox_receivehex);
                                        checkBox_sendhex = (CheckBox) findViewById(R.id.checkBox_sendhex);
                                        button_send = (Button) findViewById(R.id.button_send);
                                        checkBox_sendhex.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                editText_send.selectAll();
                                            }
                                        });
                                        button_send.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(MainActivity.this,Main1Activity.class);
                                                startActivity(intent);
                                                /*if (editText_send.getText().length() > 0) {
                                                    byte[] senddatas = new byte[0];
                                                    String str = new String();
                                                    if (checkBox_sendhex.isChecked() == false) {
                                                        try {
                                                            senddatas = editText_send.getText().toString().getBytes("gb2312");
                                                            str = new String(senddatas);
                                                        } catch (UnsupportedEncodingException e) {
                                                            e.printStackTrace();
                                                            textView_receive.append(Html.fromHtml("<html><body><br><font size=\"4\" color=\"#B50300\"><br>GB-2312出错</font></br></body></html>"));//追加字符串
                                                        }
                                                    } else {
                                                        String s = editText_send.getText().toString();
                                                        s = s.toUpperCase().replace(" ", "").replace("\r", "").replace("\n", "");//将小写转换成大写，将" "替换"",将即去掉空格
                                                        senddatas = new byte[s.length() / 2];
                                                        char[] hexChars = s.toCharArray();
                                                        for (int i = 0; i < s.length() / 2; i++) {
                                                            int pos = i * 2;                                        //indexOf(首次出现位置)
                                                            byte a = (byte) "0123456789ABCDEF".indexOf(hexChars[pos]);
                                                            byte b = (byte) "0123456789ABCDEF".indexOf(hexChars[pos + 1]);
                                                            if (a == -1 || b == -1) {
                                                                textView_receive.append(Html.fromHtml("<html><body><br><font size=\"4\" color=\"#B50300\">输入格式错误</font></br></body></html>"));//追加字符串
                                                                return;
                                                            }
                                                            senddatas[i] = (byte) (a << 4 | b);
                                                        }
                                                        for (int i = 0; i < senddatas.length; i++) {
                                                            String str_hex = (Integer.toHexString((int) senddatas[i] & 0x000000ff) + "").toUpperCase();
                                                            if (str_hex.length() == 1)
                                                                str_hex = "0" + str_hex;
                                                            str += str_hex + " ";
                                                        }
                                                    }
                                                    Log.e(TAG, str);
                                                    bluetoothGattCharacteristic.setValue(senddatas);
                                                    bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
                                                    textView_receive.append(Html.fromHtml("<html><body><br><font size=\"3\" color=\"#146E00\">你发送了：</font></br></body></html>"));//追加字符串
                                                    textView_receive.append(str);
                                                    int offset = textView_receive.getLineCount() * textView_receive.getLineHeight();//自动移动光标
                                                    if (offset > textView_receive.getHeight()) {
                                                        textView_receive.scrollTo(0, offset - textView_receive.getHeight());
                                                    }
                                                } else {
                                                    textView_receive.append(Html.fromHtml("<html><body><br><font size=\"4\" color=\"#B50300\">请输入数据</font></br></body></html>"));//追加字符串
                                                }*/
                                            }
                                        });
                                    }
                                });
                            } else {
                                Log.e(TAG, "获取特征失败");
                                if (gatt != null) gatt.disconnect();
                                textView_status.setText("获取特征失败");
                                textView_status.setTextColor(Color.RED);
                            }
                        } else {
                            Log.e(TAG, "发明服务失败");
                            if (gatt != null) gatt.disconnect();
                            textView_status.setText("发现服务失败");
                            textView_status.setTextColor(Color.RED);
                        }
                    }


                    //////////判断电话数组下标
                 /*   ITelephony iTelephony;



      try {

                        Method getITelephonyMethod = TelephonyManager.class.getDeclaredMethod("getITelephony", (Class[]) null);

                        getITelephonyMethod.setAccessible(true);

                        iTelephony = (ITelephony) getITelephonyMethod.invoke(tm, (Object[]) null);

                        iTelephony.endCall();

                    } catch (Exception e) {

                        e.printStackTrace();

                        System.out.println(e.getMessage());

                    }*/

                    /*  private boolean isIndex(int tell, int len){
                        if (tell == -1){

                            return true;
                        }
                        if (tell == len+1){

                            countTell = 0;
                            return true;
                        }
                        return false;
                    }*/
                    /////////////
                    @Override
                    public void onCharacteristicChanged(BluetoothGatt
                                                                gatt, BluetoothGattCharacteristic characteristic) {
                        //用此函数#################################接收数据####################################
                        super.onCharacteristicChanged(gatt, characteristic);
                        byte[] bytesreceive = characteristic.getValue();
                        Log.e(TAG, "收到数据");
                        /////////////创建电话簿数组
                       /* final String tell[] = {};*/
                        final String tell[] = {"15382664921", "13428206324", "18566769375", "13068560902", "13725477419"};
                        int len = tell.length;//数组长度
                        //////////////////
                        if (bytesreceive.length != 0) {

                            if (checkBox_receivehex.isChecked() == true) {
                                str_receive = new String();
                                for (int i = 0; i < bytesreceive.length; i++) {
                                    String str_hex = (Integer.toHexString((int) bytesreceive[i] & 0x000000ff) + "").toUpperCase();
                                    if (str_hex.length() == 1) str_hex = "0" + str_hex;
                                    str_receive += str_hex + " ";
                                }
                                Log.e("EEE", str_receive.charAt(1) + "");

                                //定时器 拨打电话
                                Timer timer = new Timer();
                                TimerTask timerTask = new TimerTask() {
                                    @Override
                                    public void run() {
                                        startTell(tell[countTell]);

                                    }
                                };
                                ////////////判断是否拨打电话
                                switch (str_receive.charAt(1)) {
                                    case '1':
                                        endTell();

                                        break;
                                    case '2':
                                        Log.e("FF", str_receive.charAt(1) + "");

                                        if (preNum != str_receive.charAt(0)) {
                                            flag = 0;
                                            if (flag != 1) {
                                                broadcast(tell[countTell]);
                                                timer.schedule(timerTask, 5000);
                                            }

                                        }

                                            preNum = str_receive.charAt(0);


                                        break;
                                    case '3':
                                        //左摇头
                                        if (preNum != str_receive.charAt(0)) {
                                            flag1 = 0;
                                            if (flag1 != 3) {
                                               if (countTell == -1) {
                                                    countTell = len ;
                                                }
                                                countTell--;
                                                broadcast1(tell[countTell]);
                                            }

                                        }

                                            preNum = str_receive.charAt(0);


                                        break;
                                    case '4':
                                        //右摇头
                                        if (preNum != str_receive.charAt(0)) {
                                            flag2 = 0;
                                            if (flag2 != 4) {
                                                if (countTell == len-1) {
                                                    countTell = -1;
                                                }
                                                countTell++;
                                                broadcast2(tell[countTell]);
                                            }

                                        }

                                            preNum = str_receive.charAt(0);


                                        break;
                                    case '5':

                                        //左点头
                                        // endTell();
                                       /* if (flag !=1){
                                            broadcast(tell[countTell]);
                                            timer.schedule(timerTask,5000);
                                        }*/
                                        break;
                                    case '6':

                                        // endTell();
                                        //右点头
                                        /*if (flag !=1){
                                            broadcast(tell[countTell]);
                                            timer.schedule(timerTask,5000);
                                        }*/
                                        break;
                                    default:
                                        break;
                                }

                            } else {
                                str_receive = new String(bytesreceive);
                                Log.e(TAG, str_receive);
                            }

                            handler.post(new Runnable() {

                                @Override
                                public void run() {


                                    textView_receive.append(Html.fromHtml("<html><body><br><font size=\"3\" color=\"#2600AF\">你收到了：</font></br></body></html>"));//追加字符串
                                    textView_receive.append(str_receive);//追加字符串
                                    int offset = textView_receive.getLineCount() * textView_receive.getLineHeight();//自动移动光标
                                    if (offset > textView_receive.getHeight()) {
                                        textView_receive.scrollTo(0, offset - textView_receive.getHeight());
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCharacteristicWrite(BluetoothGatt
                                                              gatt, BluetoothGattCharacteristic characteristic, int status) {
                        Log.e(TAG, gatt.getDevice().getName() + " write successfully");
                    }
                });
            }
        });
        bluetoothManager = (BluetoothManager)

                getSystemService(Context.BLUETOOTH_SERVICE);

        bluetoothAdapter = bluetoothManager.getAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent(bluetoothAdapter.ACTION_REQUEST_ENABLE), 0);  // 弹对话框的形式提示用户开启蓝牙
        }

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();  //android5.0把扫描方法单独弄成一个对象了
        button_discover.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                bluetoothLeScanner.stopScan(scanCallback);
                bluetoothLeScanner.startScan(scanCallback);
                textView_status.setText("正在搜索...");
                textView_status.setTextColor(Color.BLUE);
                handler.postDelayed(runnable, 10000);
            }
        });
        bluetoothLeScanner.startScan(scanCallback);
        textView_status.setText("正在搜索...");
        textView_status.setTextColor(Color.BLUE);
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 10000);
        Log.e(TAG, "onCreat");
    }


    private void init() {
        listView = (ListView) findViewById(R.id.listView1);
        button_discover = (Button) findViewById(R.id.button1);
        textView_status = (TextView) findViewById(R.id.textView1);
        listItem = new ArrayList<>();
        simpleAdapter = new SimpleAdapter(this, listItem, R.layout.item_main, new String[]{"name", "mac"}, new int[]{R.id.item_1, R.id.item_2});
        listView.setAdapter(simpleAdapter);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                //初始化成功的话，设置语音，这里将它设置为中文
                if (status == TextToSpeech.SUCCESS) {
                    int supported = textToSpeech.setLanguage(Locale.US);
                    if ((supported != TextToSpeech.LANG_AVAILABLE) && (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
                        Toast.makeText(MainActivity.this, "不支持当前语言", Toast.LENGTH_SHORT).show();
                    }
                }

            }


        });

        textToSpeech1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                //初始化成功的话，设置语音，这里将它设置为中文
                if (status == TextToSpeech.SUCCESS) {
                    int supported = textToSpeech1.setLanguage(Locale.US);
                    if ((supported != TextToSpeech.LANG_AVAILABLE) && (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
                        Toast.makeText(MainActivity.this, "不支持当前语言", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        textToSpeech2 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                //初始化成功的话，设置语音，这里将它设置为中文
                if (status == TextToSpeech.SUCCESS) {
                    int supported = textToSpeech2.setLanguage(Locale.US);
                    if ((supported != TextToSpeech.LANG_AVAILABLE) && (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
                        Toast.makeText(MainActivity.this, "不支持当前语言", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

    /**
     * 文本转语音的播放方法
     * @param
     */
    private void broadcast(String phoneNum) {
        textToSpeech.setLanguage(Locale.CHINA);
        textToSpeech.speak("您即将拨打" + phoneNum, TextToSpeech.QUEUE_ADD, null);//可以用QUEUE_FLUSH
        flag = 1;
    }

    private void broadcast1(String phoneNum) {
        textToSpeech1.setLanguage(Locale.CHINA);
        textToSpeech1.speak("上一個" + phoneNum, TextToSpeech.QUEUE_ADD, null);//可以用QUEUE_FLUSH
        flag1 = 3;

    }

    private void broadcast2(String phoneNum) {
        textToSpeech2.setLanguage(Locale.CHINA);
        textToSpeech2.speak("下一個" + phoneNum, TextToSpeech.QUEUE_ADD, null);//可以用QUEUE_FLUSH
        flag2 = 4;

    }


    /////////////////拨打电话
    private void startTell(String phoneNum) {
        Intent
                intent = new

                Intent(Intent.ACTION_CALL);

        Uri data = Uri.parse("tel:"

                + phoneNum);

        intent.setData(data);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    //////////////////////////////

    //挂断电话
    private void endTell() {

        // 延迟5秒后自动挂断电话
        // 首先拿到TelephonyManager
        TelephonyManager telMag = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;

        // 再去反射TelephonyManager里面的私有方法 getITelephony 得到 ITelephony对象
        Method mthEndCall = null;
        try {
            mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
            //允许访问私有方法
            mthEndCall.setAccessible(true);
            final Object obj = mthEndCall.invoke(telMag, (Object[]) null);

            // 再通过ITelephony对象去反射里面的endCall方法，挂断电话
            Method mt = obj.getClass().getMethod("endCall");
            //允许访问私有方法
            mt.setAccessible(true);
            mt.invoke(obj);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Toast.makeText(MainActivity.this, "挂断电话！", Toast.LENGTH_SHORT).show();
    }
    private void AnswerTell() {

        // 延迟5秒后自动挂断电话
        // 首先拿到TelephonyManager
        TelephonyManager telMag = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;

        // 再去反射TelephonyManager里面的私有方法 getITelephony 得到 ITelephony对象
        Method mthEndCall = null;
        try {
            mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
            //允许访问私有方法
            mthEndCall.setAccessible(true);
            final Object obj = mthEndCall.invoke(telMag, (Object[]) null);

            // 再通过ITelephony对象去反射里面的endCall方法，挂断电话
            Method mt = obj.getClass().getMethod("answerRingingCall");
            //允许访问私有方法
            mt.setAccessible(true);
            mt.invoke(obj);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Toast.makeText(MainActivity.this, "电话！", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        isForeground = true;
        Log.e("TTT", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        Log.e("TTT", "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isForeground = true;
        Log.e("TTT", "onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
        Log.e("TTT", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        isForeground = false;
        Log.e("TTT", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e(TAG, "onDestroy");
        bluetoothLeScanner.stopScan(scanCallback);
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
        }
        isForeground = false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Item_disconnect://点击“断开连接”
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bluetoothGatt != null)
                            bluetoothGatt.disconnect();
                    }
                });
                break;
            case R.id.Item_about://点击“关于”

                new AlertDialog.Builder(MainActivity.this).setTitle("关于")//设置对话框标题
                        .setMessage("版本：V1.0")//设置显示的内容
                        .setPositiveButton("返回", new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                // TODO Auto-generated method stub

                            }
                        }).show();//在按键响应事件中显示此对话框
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult results) {
            super.onScanResult(callbackType, results);// callbackType：触发类型 ，result：包括4.3版本的蓝牙信息，信号强度rssi，和广播数据scanRecord
            BluetoothDevice device = results.getDevice();
            if (!devices.contains(device)) {  //判断是否已经添加
                devices.add(device);
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", device.getName());
                map.put("mac", device.getAddress());
                listItem.add(map);
                simpleAdapter.notifyDataSetChanged();
            }
        }

        public void onScanFailed(int errorCode) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    textView_status.setText("扫描失败");
                    textView_status.setTextColor(Color.RED);
                }
            });
        }
    };


    /**
     * Called to signal the completion of the TextToSpeech engine initialization.
     *
     * @param status {@link TextToSpeech#SUCCESS} or {@link TextToSpeech#ERROR}.
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.CHINA);
            textToSpeech1.setLanguage(Locale.CHINA);
            textToSpeech2.setLanguage(Locale.CHINA);
        }
    }


}

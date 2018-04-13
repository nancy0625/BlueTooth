package com.gdmec.ble;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.TelephonyManager;

import com.wangkai.blecommunication.ITelephony;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by asus on 2018/3/27.
 */

public class Myreciver extends BroadcastReceiver {




    @Override
    public void onReceive(Context context, Intent intent) {

        if (!intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {

           // String mIncomingNumber ="";
            //如果是来电
            TelephonyManager tManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            switch (tManager.getCallState()){
                case TelephonyManager.CALL_STATE_RINGING:
                   // mIncomingNumber = intent.getStringExtra("incoming_number");
                    //int blackContactMode = dao.getBlackContactMode(mIncomingNumber);
                   // if (blackContactMode==1||blackContactMode==3){
                        //观察(另外一个应用程序数据库的变化)呼叫记录的变化,
                        //如果呼叫记录生成了。就把呼叫记录给删除了
                       // Uri uri = Uri.parse("content://call_log/calls");
                        //context.getContentResolver().registerContentObserver(uri,true,new CallLogObserver(new Handler(),mIncomingNumber,context));
                        answerRingingCall(context);


                    break;


            }
        }

    }
    public void endCall(Context context){
        try{
            Class clazz = context.getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("getService",String.class);
            IBinder iBinder = (IBinder)method.invoke(null,Context.TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void answerRingingCall(Context context){
        try {
            Class cla = context.getClassLoader().loadClass("android.os.ServiceManager");
            Method method = cla.getDeclaredMethod("getService",String.class);
            IBinder iBinder = (IBinder)method.invoke(null,Context.TELECOM_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.answerRingingCall();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

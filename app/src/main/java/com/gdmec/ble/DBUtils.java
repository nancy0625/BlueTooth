package com.gdmec.ble;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by apple on 18/4/10.
 */

public class DBUtils {
    private static DBUtils instance = null;
    private static SQLiteOpenHelper helper;
    private static SQLiteDatabase db;
    public DBUtils(Context context){
        helper = new SQLiteHelper(context);
        db = helper.getWritableDatabase();
    }
    public static DBUtils getInstance(Context context){
        if (instance == null){
            instance = new DBUtils(context);
        }
        return instance;
    }

    /**
     * 保存用户输入资料
     * @param bean
     */
    public void saveUserInfo(UserBean bean){
        ContentValues cv = new ContentValues();
        //cv.put("userName",bean.userName);
        cv.put("address1",bean.address1);
        cv.put("phoneNum1",bean.phoneNum1);
        cv.put("address2",bean.address2);
        cv.put("phoneNum2",bean.phoneNum2);
        cv.put("address3",bean.address3);
        cv.put("phoneNum3",bean.phoneNum3);
        db.insert(SQLiteHelper.U_USERINFO,null,cv);
    }
    public UserBean getUserInfo(/**String userName*/){
        String sql = "SELECT * FROM "+SQLiteHelper.U_USERINFO;//+" WHERE userName=? ";
        Cursor cursor = db.rawQuery(sql,null);//new String[]{userName}
        UserBean bean = null;
        if (cursor.moveToFirst()){
            bean = new UserBean();
           // bean.userName = cursor.getString(cursor.getColumnIndex("userName"));
            bean.address1 = cursor.getString(cursor.getColumnIndex("address1"));
            bean.phoneNum1 = cursor.getString(cursor.getColumnIndex("phoneNum1"));
            bean.address2 = cursor.getString(cursor.getColumnIndex("address2"));
            bean.phoneNum2 = cursor.getString(cursor.getColumnIndex("phoneNum2"));
            bean.address3 = cursor.getString(cursor.getColumnIndex("address3"));
            bean.phoneNum3 = cursor.getString(cursor.getColumnIndex("phoneNum3"));
        }
        cursor.close();
        return bean;
    }
    /**
     * 修改用户输入信息
     */
    public void updateUserInfo(String key,String value){
        ContentValues cv = new ContentValues();
        cv.put(key,value);
        db.update(SQLiteHelper.U_USERINFO,cv,null,null);
    }
}

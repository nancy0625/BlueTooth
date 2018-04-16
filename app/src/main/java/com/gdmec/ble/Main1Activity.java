package com.gdmec.ble;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Main1Activity extends AppCompatActivity implements View.OnClickListener{

    private TextView tv_address1;
    private RelativeLayout rl_address1;
    private TextView tv_phoneNum1;
    private RelativeLayout rl_phoneNum1;
    private TextView tv_address2;
    private RelativeLayout rl_address2;
    private TextView tv_phoneNum2;
    private RelativeLayout rl_phoneNum2;
    private TextView tv_address3;
    private RelativeLayout rl_address3;
    private TextView tv_phoneNum3;
    private RelativeLayout rl_phoneNum3;
    private TextView tv_back;
    private static final int CHANGE_ADDRESS1 = 11;//地址1
    private static final int CHANGE_ADDRESS2 = 12;//地址2
    private static final int CHANGE_ADDRESS3 = 13;//地址3
    private static final int CHANGE_PHONE1 = 21;//手机号1
    private static final int CHANGE_PHONE2 = 22;//手机号2
    private static final int CHANGE_PHONE3 = 23;//手机号3


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        initView();
        initData();

    }

    private void initView() {
        tv_address1 = (TextView) findViewById(R.id.tv_address1);
        rl_address1 = (RelativeLayout) findViewById(R.id.rl_address1);
        tv_phoneNum1 = (TextView) findViewById(R.id.tv_phoneNum1);
        rl_phoneNum1 = (RelativeLayout) findViewById(R.id.rl_phoneNum1);
        tv_address2 = (TextView) findViewById(R.id.tv_address2);
        rl_address2 = (RelativeLayout) findViewById(R.id.rl_address2);
        tv_phoneNum2 = (TextView) findViewById(R.id.tv_phoneNum2);
        rl_phoneNum2 = (RelativeLayout) findViewById(R.id.rl_phoneNum2);
        tv_address3 = (TextView) findViewById(R.id.tv_address3);
        rl_address3 = (RelativeLayout) findViewById(R.id.rl_address3);
        tv_phoneNum3 = (TextView) findViewById(R.id.tv_phoneNum3);
        rl_phoneNum3 = (RelativeLayout) findViewById(R.id.rl_phoneNum3);
        tv_back = (TextView)findViewById(R.id.tv_back);
        rl_address1.setOnClickListener(this);
        rl_address2.setOnClickListener(this);
        rl_address3.setOnClickListener(this);
        rl_phoneNum1.setOnClickListener(this);
        rl_phoneNum2.setOnClickListener(this);
        rl_phoneNum3.setOnClickListener(this);
    }
    private void initData() {
        UserBean bean = null;
        bean = DBUtils.getInstance(this).getUserInfo();
        //首先判斷一下數據庫是否有數據
        if (bean == null) {
            bean = new UserBean();
            //bean.userName = "qq";
            bean.address1 = " 广东机电北校区G3";
            bean.phoneNum1 = "18566769375";
            bean.address2 = " 广东机电北校区G2";
            bean.phoneNum2 = "15382664921";
            bean.address3 = " 广东机电北校区G1";
            bean.phoneNum3 = "13068560902";

            //保存用戶信息到數據庫
            DBUtils.getInstance(this).saveUserInfo(bean);
        }
        setValue(bean);

    }
    public void setValue(UserBean bean) {
        tv_address1.setText(bean.address1);
        tv_phoneNum1.setText(bean.phoneNum1);
        tv_address2.setText(bean.address2);
         tv_phoneNum2.setText(bean.phoneNum2);
        tv_address3.setText(bean.address3);
        tv_phoneNum3.setText(bean.phoneNum3);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back:
                this.finish();
                break;
            case R.id.rl_address1:

                String address1 = tv_address1.getText().toString();
                Bundle bdad1 = new Bundle();
                bdad1.putString("content",address1);
                bdad1.putString("title","输入地址(近的)");
                bdad1.putInt("flag",11);
                enterActivityForResult(ChangeUserInfoActivity.class,CHANGE_ADDRESS1,bdad1);
                break;
            case R.id.rl_address2:
                String address2 = tv_address2.getText().toString();
                Bundle bdad2 = new Bundle();
                bdad2.putString("content",address2);
                bdad2.putString("title","输入地址(较近的)");
                bdad2.putInt("flag",12);
                enterActivityForResult(ChangeUserInfoActivity.class,CHANGE_ADDRESS2,bdad2);
                break;
            case R.id.rl_address3:
                String address3 = tv_address3.getText().toString();
                Bundle bdad3 = new Bundle();
                bdad3.putString("content",address3);
                bdad3.putString("title","输入地址(远的)");
                bdad3.putInt("flag",13);
                enterActivityForResult(ChangeUserInfoActivity.class,CHANGE_ADDRESS3,bdad3);
                break;
            case R.id.rl_phoneNum1:
                String phoneNum1 = tv_phoneNum1.getText().toString();
                Bundle bdpnd1 = new Bundle();
                bdpnd1.putString("content",phoneNum1);
                bdpnd1.putString("title","手机号码1");
                bdpnd1.putInt("flag",21);
                enterActivityForResult(ChangeUserInfoActivity.class,CHANGE_PHONE1,bdpnd1);
                break;
            case R.id.rl_phoneNum2:
                String phoneNum2 = tv_phoneNum2.getText().toString();
                Bundle bdpnd2 = new Bundle();
                bdpnd2.putString("content",phoneNum2);
                bdpnd2.putString("title","手机号码2");
                bdpnd2.putInt("flag",22);
                enterActivityForResult(ChangeUserInfoActivity.class,CHANGE_PHONE2,bdpnd2);
                break;
            case R.id.rl_phoneNum3:
                String phoneNum3 = tv_phoneNum3.getText().toString();
                Bundle bdpnd3 = new Bundle();
                bdpnd3.putString("content",phoneNum3);
                bdpnd3.putString("title","手机号码3");
                bdpnd3.putInt("flag",23);
                enterActivityForResult(ChangeUserInfoActivity.class,CHANGE_PHONE3,bdpnd3);
                break;
                default:
                    break;

        }
    }
    /**
     * 回传数据
     */
    private String new_info;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CHANGE_ADDRESS1://近程地址
                 if(data != null){
                new_info = data.getStringExtra("address1");
                if (TextUtils.isEmpty(new_info)){
                    return;
                }
                tv_address1.setText(new_info);
                //更新数据库中的字段
                     DBUtils.getInstance(Main1Activity.this).updateUserInfo("address1",new_info);
                 }
                 break;
            case CHANGE_ADDRESS2://中程地址
                if(data != null){
                    new_info = data.getStringExtra("address2");
                    if (TextUtils.isEmpty(new_info)){
                        return;
                    }
                    tv_address2.setText(new_info);
                    //更新数据库中地址2的字段
                    DBUtils.getInstance(Main1Activity.this).updateUserInfo("address2",new_info);
                }
                break;
            case CHANGE_ADDRESS3://远程地址
                if(data != null){
                    new_info = data.getStringExtra("address3");
                    if (TextUtils.isEmpty(new_info)){
                        return;
                    }
                    tv_address3.setText(new_info);
                    //更新数据库中的字段
                    DBUtils.getInstance(Main1Activity.this).updateUserInfo("address3",new_info);
                }
                break;
            case CHANGE_PHONE1:
                if(data != null){
                    new_info = data.getStringExtra("phoneNum1");
                    if (TextUtils.isEmpty(new_info)){
                        return;
                    }
                    tv_phoneNum1.setText(new_info);
                    //更新数据库中的字段
                    DBUtils.getInstance(Main1Activity.this).updateUserInfo("phoneNum1",new_info);
                }
                break;
            case CHANGE_PHONE2:
                if(data != null){
                    new_info = data.getStringExtra("phoneNum2");
                    if (TextUtils.isEmpty(new_info)){
                        return;
                    }
                    tv_phoneNum2.setText(new_info);
                    //更新数据库中的字段
                    DBUtils.getInstance(Main1Activity.this).updateUserInfo("phoneNum2",new_info);
                }
                break;
            case CHANGE_PHONE3:
                if(data != null){
                    new_info = data.getStringExtra("phoneNum3");
                    if (TextUtils.isEmpty(new_info)){
                        return;
                    }
                    tv_phoneNum3.setText(new_info);
                    //更新数据库中的字段
                    DBUtils.getInstance(Main1Activity.this).updateUserInfo("phoneNum3",new_info);
                }
                break;


        }
    }

    /**
     * 获取传回数据时需要使用的跳转方法，第一个参数to表示需要跳转到的界面
     * 第2个参数requestCode表示一个请求码，第3个参数表示跳转传递的数据
     */
    public void enterActivityForResult(Class<?> to, int requestCode, Bundle b) {
        Intent i = new Intent(this, to);
        i.putExtras(b);
        startActivityForResult(i, requestCode);

    }
}

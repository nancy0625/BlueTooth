package com.gdmec.ble;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeUserInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tv_back;
    private TextView tv_main_title;
    private RelativeLayout rl_title_bar;
    private TextView tv_save;
    private RelativeLayout title_bar;
    private EditText et_content;
    private String title,content;
    private ImageView iv_delete;
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_info);
        initView();
    }

    private void initView() {
        //从个人资料页面传递过来的标题和内容
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        flag = getIntent().getIntExtra("flag",0);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        rl_title_bar = (RelativeLayout)findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(Color.parseColor("#30B4FF"));
        tv_main_title.setText(title);
        tv_save = (TextView) findViewById(R.id.tv_save);
        tv_save.setVisibility(View.VISIBLE);
        title_bar = (RelativeLayout) findViewById(R.id.title_bar);
        et_content = (EditText) findViewById(R.id.et_content);
        if (flag==23 || flag == 21 || flag == 22){
            et_content.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        iv_delete = (ImageView) findViewById(R.id.iv_delete);
        if (!TextUtils.isEmpty(content)){
            et_content.setText(content);
            et_content.setSelection(content.length());
        }
        tv_back.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        iv_delete.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back:
                ChangeUserInfoActivity.this.finish();
                break;
            case R.id.iv_delete:
                et_content.setText("");
                break;
            case R.id.tv_save:
                Intent data = new Intent();
                String etContent = et_content.getText().toString().trim();
                switch (flag){
                    case 11:
                        if (!TextUtils.isEmpty(etContent)){
                            data.putExtra("address1",etContent);
                            setResult(RESULT_OK,data);
                            Toast.makeText(ChangeUserInfoActivity.this,"保存成功", Toast.LENGTH_SHORT).show();
                            ChangeUserInfoActivity.this.finish();
                        }else {
                            Toast.makeText(ChangeUserInfoActivity.this,"近程地址不能为空", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 21:
                        if (!TextUtils.isEmpty(etContent)){
                            data.putExtra("phoneNum1",etContent);
                            setResult(RESULT_OK,data);
                            Toast.makeText(ChangeUserInfoActivity.this,"保存成功", Toast.LENGTH_SHORT).show();
                            ChangeUserInfoActivity.this.finish();
                        }else {
                            Toast.makeText(ChangeUserInfoActivity.this,"近程手机号码不能为空", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 12:
                        if (!TextUtils.isEmpty(etContent)){
                            data.putExtra("address2",etContent);
                            setResult(RESULT_OK,data);
                            Toast.makeText(ChangeUserInfoActivity.this,"保存成功", Toast.LENGTH_SHORT).show();
                            ChangeUserInfoActivity.this.finish();
                        }else {
                            Toast.makeText(ChangeUserInfoActivity.this,"中程地址不能为空", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 22:
                        if (!TextUtils.isEmpty(etContent)){
                            data.putExtra("phoneNum2",etContent);
                            setResult(RESULT_OK,data);
                            Toast.makeText(ChangeUserInfoActivity.this,"保存成功", Toast.LENGTH_SHORT).show();
                            ChangeUserInfoActivity.this.finish();
                        }else {
                            Toast.makeText(ChangeUserInfoActivity.this,"中程手机号码不能为空", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 13:
                        if (!TextUtils.isEmpty(etContent)){
                            data.putExtra("address3",etContent);
                            setResult(RESULT_OK,data);
                            Toast.makeText(ChangeUserInfoActivity.this,"保存成功", Toast.LENGTH_SHORT).show();
                            ChangeUserInfoActivity.this.finish();
                        }else {
                            Toast.makeText(ChangeUserInfoActivity.this,"远程地址不能为空", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 23:
                        if (!TextUtils.isEmpty(etContent)){
                            data.putExtra("phoneNum3",etContent);
                            setResult(RESULT_OK,data);
                            Toast.makeText(ChangeUserInfoActivity.this,"保存成功", Toast.LENGTH_SHORT).show();
                            ChangeUserInfoActivity.this.finish();
                        }else {
                            Toast.makeText(ChangeUserInfoActivity.this,"远程手机号码不能为空", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
        }
    }
    private void contentListener() {
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Editable editable = et_content.getText();
                int len = editable.length();
                if (len > 0){
                    iv_delete.setVisibility(View.VISIBLE);

                }else {
                    iv_delete.setVisibility(View.GONE);
                }
                switch (flag){
                    case 11: //近程
                        //昵称限制最多8个文字，超过8个需要截取多余的文字
                        if (len > 25){
                            int selEndIndex = Selection.getSelectionEnd(editable);
                            String str = editable.toString();
                            //截取新字符串
                            String newStr = str.substring(0,8);
                            et_content.setText(newStr);
                            editable = et_content.getText();
                            //新字符串的长度
                            int newLen = editable.length();
                            //旧光标位置超过新字符串的长度
                            if (selEndIndex > newLen){
                                selEndIndex = editable.length();
                            }
                            //设置光标所在的位置
                            Selection.setSelection(editable,selEndIndex);
                        }
                        break;
                    case 21:
                        //签名最多是16个文字，超过16个需要截取掉多余的文字
                        if (len > 16){
                            int selEndIndex = Selection.getSelectionEnd(editable);
                            String str = editable.toString();
                            //截取新字符串
                            String newStr = editable.toString();
                            et_content.setText(newStr);
                            editable = et_content.getText();
                            //新字符串的长度
                            int newLen = editable.length();
                            //旧光标位置超过新字符串的长度
                            if (selEndIndex > newLen){
                                selEndIndex = editable.length();
                            }
                            //设置新光标所在的位置
                            Selection.setSelection(editable,selEndIndex);
                        }
                        break;
                    case 12:

                        //签名最多是16个文字，超过16个需要截取掉多余的文字
                        if (len > 25){
                            int selEndIndex = Selection.getSelectionEnd(editable);
                            String str = editable.toString();
                            //截取新字符串
                            String newStr = editable.toString();
                            et_content.setText(newStr);
                            editable = et_content.getText();
                            //新字符串的长度
                            int newLen = editable.length();
                            //旧光标位置超过新字符串的长度
                            if (selEndIndex > newLen){
                                selEndIndex = editable.length();
                            }
                            //设置新光标所在的位置
                            Selection.setSelection(editable,selEndIndex);
                        }
                        break;
                    case 22:
                        //签名最多是16个文字，超过16个需要截取掉多余的文字
                        if (len > 16){
                            int selEndIndex = Selection.getSelectionEnd(editable);
                            String str = editable.toString();
                            //截取新字符串
                            String newStr = editable.toString();
                            et_content.setText(newStr);
                            editable = et_content.getText();
                            //新字符串的长度
                            int newLen = editable.length();
                            //旧光标位置超过新字符串的长度
                            if (selEndIndex > newLen){
                                selEndIndex = editable.length();
                            }
                            //设置新光标所在的位置
                            Selection.setSelection(editable,selEndIndex);
                        }
                        break;
                    case 13:

                        //签名最多是16个文字，超过16个需要截取掉多余的文字
                        if (len > 25){
                            int selEndIndex = Selection.getSelectionEnd(editable);
                            String str = editable.toString();
                            //截取新字符串
                            String newStr = editable.toString();
                            et_content.setText(newStr);
                            editable = et_content.getText();
                            //新字符串的长度
                            int newLen = editable.length();
                            //旧光标位置超过新字符串的长度
                            if (selEndIndex > newLen){
                                selEndIndex = editable.length();
                            }
                            //设置新光标所在的位置
                            Selection.setSelection(editable,selEndIndex);
                        }
                        break;
                    case 23:
                        //签名最多是16个文字，超过16个需要截取掉多余的文字
                        if (len > 16){
                            int selEndIndex = Selection.getSelectionEnd(editable);
                            String str = editable.toString();
                            //截取新字符串
                            String newStr = editable.toString();
                            et_content.setText(newStr);
                            editable = et_content.getText();
                            //新字符串的长度
                            int newLen = editable.length();
                            //旧光标位置超过新字符串的长度
                            if (selEndIndex > newLen){
                                selEndIndex = editable.length();
                            }
                            //设置新光标所在的位置
                            Selection.setSelection(editable,selEndIndex);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}

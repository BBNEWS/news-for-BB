package com.example.a13016.a7_20;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.a13016.a7_20.Util.CommonRequest;
import com.example.a13016.a7_20.Util.CommonResponse;
import com.example.a13016.a7_20.Util.Consts;
import com.example.a13016.a7_20.Util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.Util;

public class Register extends AppCompatActivity {

    private Button back;
    private Button registerBtn;
    private EditText accountText;
    private EditText pwdText;
    private EditText confirmPwdText;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        back = findViewById(R.id.buttonregister1);
        registerBtn = findViewById(R.id.buttonregister);
        accountText = findViewById(R.id.editregister1);
        pwdText = findViewById(R.id.editregister2);
        confirmPwdText = findViewById(R.id.editregister3);
        progressDialog = new ProgressDialog(Register.this);
        setListeners();
    }


    void setListeners(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });// 设置返回键监听
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    /**
     *  POST方式Register
     *
     */
    public static String StringHandle(String input){
        String output;
        // 将包含有 单引号(')，分号(;) 和 注释符号(--)的语句替换掉
        output = input.trim().replaceAll(".*([';]+|(--)+).*", " ");
        return output;
    }
    private void register() {
        // 创建请求体对象
        CommonRequest request = new CommonRequest();

        // 前端参数校验，防SQL注入
        String account = StringHandle(accountText.getText().toString());
        String pwd = StringHandle(pwdText.getText().toString());
        String pwd_confirm = StringHandle(confirmPwdText.getText().toString());

        // 检查数据格式是否正确
        String resMsg = checkDataValid(account,pwd,pwd_confirm);
        if(!resMsg.equals("")){
            showResponse(resMsg);
            return;
        }

        // 填充参数
        request.addRequestParam("account",account);
        request.addRequestParam("pwd",pwd);

        // POST请求
        HttpUtil.sendPost(Consts.URL_Register, request.getJsonStr(), new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                CommonResponse res = new CommonResponse(response.body().string());
                String resCode = res.getResCode();
                String resMsg = res.getResMsg();
                // 显示注册结果
                showResponse(resMsg);
                // 注册成功
                if (resCode.equals( Consts.SUCCESSCODE_REGISTER)) {
                    finish();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showResponse("Network ERROR");
            }
        });
    }

    private void showResponse(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Register.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String checkDataValid(String account,String pwd,String pwd_confirm){
        if(TextUtils.isEmpty(account) | TextUtils.isEmpty(pwd) | TextUtils.isEmpty(pwd_confirm))
            return getResources().getString(R.string.null_hint);
        if(!pwd.equals(pwd_confirm))
            return getResources().getString(R.string.not_equal_hint);
        if(account.length() != 11 && !account.contains("@"))
            return getResources().getString(R.string.account_invalid_hint);
        return "";
    }
}

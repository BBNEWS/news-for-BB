package com.example.a13016.a7_20;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a13016.a7_20.Data.User;
import com.example.a13016.a7_20.Util.CommonRequest;
import com.example.a13016.a7_20.Util.CommonResponse;
import com.example.a13016.a7_20.Util.SharedPreferencesUtil;
import com.example.a13016.a7_20.Util.UserManager;
import com.example.a13016.a7_20.Util.HttpUtil;
import com.example.a13016.a7_20.Util.Consts;

import java.io.IOException;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.Util;

public class Login extends AppCompatActivity {
    private Button loginBtn;
    private Button registerBtn;
    private EditText accountText;
    private EditText passwordText;
    private CheckBox isRememberPwd;
    private CheckBox isAutoLogin;

    private String account;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        loginBtn = findViewById(R.id.buttonlogin1);
        registerBtn = findViewById(R.id.buttonlogin);
        accountText = findViewById(R.id.editlogin1);
        passwordText = findViewById(R.id.editlogin2);
        isRememberPwd = findViewById(R.id.check1);
        isAutoLogin = findViewById(R.id.check2);
        LitePal.getDatabase();// 建立数据库
        UserManager.clear();
        setListeners();

        // 自动填充
        SharedPreferencesUtil spu = new SharedPreferencesUtil(this);
        Boolean isRemember = (Boolean) spu.getParam("isRememberPwd",false);
        Boolean isAutoLogin = (Boolean) spu.getParam("isAutoLogin",false);
        // SharedPreference获取用户账号密码，存在则填充
        String account = (String) spu.getParam("account","");
        String pwd = (String)spu.getParam("pwd","");
        if(!account.equals("") && !pwd.equals("")){
            if(isRemember){
                accountText.setText(account);
                passwordText.setText(pwd);
                isRememberPwd.setChecked(true);
            }
            if(isAutoLogin)
                Login();
        }
    }


    void setListeners(){
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

//        visitorText.setOnClickListener(new OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                // 若已有游客账号则以游客身份登录，不存在则新建游客账号
////                User visitor = DataSupport.where("isVisitor = ?","1")
////                                          .findFirst(User.class);
////                if(visitor == null){
////                    visitor = new User();
////                    visitor.setAccount("Visitor");
////                    visitor.setPassword("Visitor");
////                    visitor.setVisitor(true);
////                    visitor.save();
////                }
////                UserManager.setCurrentUser(visitor);
////                autoStartActivity(MainActivity.class);
////            }
////        });
    }

    /**
     *  POST方式Login
     */
    public static String StringHandle(String input){
        String output;
        // 将包含有 单引号(')，分号(;) 和 注释符号(--)的语句替换掉
        output = input.trim().replaceAll(".*([';]+|(--)+).*", " ");
        return output;
    }
    private void Login() {
        // 创建请求体对象
        CommonRequest request = new CommonRequest();

        // 前端参数校验，防SQL注入

        account = StringHandle(accountText.getText().toString());
        password = StringHandle(passwordText.getText().toString());

        // 检查数据格式是否正确
        String resMsg = checkDataValid(account,password);
        if(!resMsg.equals("")){
            showResponse(resMsg);
            return;
        }

        OptionHandle(account,password);// 处理自动登录及记住密码

        // 填充参数
        request.addRequestParam("account",account);
        request.addRequestParam("pwd",password);

        // POST请求
        HttpUtil.sendPost(Consts.URL_Login, request.getJsonStr(), new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                CommonResponse res = new CommonResponse(response.body().string());
                String resCode = res.getResCode();
                String resMsg = res.getResMsg();
                // 登录成功
                if (resCode.equals(Consts.SUCCESSCODE_LOGIN)) {
                    // 查找本地数据库中是否已存在当前用户,不存在则新建用户并写入
                    User user = DataSupport.where("account=?",account).findFirst(User.class);
                    if(user == null){
                        user = new User();
                        user.setAccount(account);
                        user.setPassword(password);
                        user.setVisitor(false);
                        user.save();
                    }
                    UserManager.setCurrentUser(user);// 设置当前用户
                    Intent intent = new Intent( Login.this,jiemian.class );
                    startActivity( intent );
                }
                showResponse(resMsg);
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
                Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String checkDataValid(String account,String pwd){
        if(TextUtils.isEmpty(account) | TextUtils.isEmpty(pwd))
            return getResources().getString(R.string.null_hint);
        if(account.length() != 11 && !account.contains("@"))
            return getResources().getString(R.string.account_invalid_hint);
        return "";
    }

    void OptionHandle(String account,String pwd){
        SharedPreferences.Editor editor = getSharedPreferences("UserData",MODE_PRIVATE).edit();
        SharedPreferencesUtil spu = new SharedPreferencesUtil(this);
        if(isRememberPwd.isChecked()){
            editor.putBoolean("isRememberPwd",true);
            // 保存账号密码
            spu.setParam("account",account);
            spu.setParam("pwd",pwd);
        }else{
            editor.putBoolean("isRememberPwd",false);
        }
        if(isAutoLogin.isChecked()){
            editor.putBoolean("isAutoLogin",true);
        }else{
            editor.putBoolean("isAutoLogin",false);
        }
        editor.apply();
    }

}

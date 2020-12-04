package com.example.studywell.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.studywell.activity.R;
import com.example.studywell.pojo.Response;
import com.example.studywell.utils.CallBackUtil;
import com.example.studywell.utils.OkhttpUtil;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loginBn;
    private EditText username;
    private EditText password;
    private TextView registerText;
    // 存储
    private SharedPreferences mSpf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBn = findViewById(R.id.loginBn);
        registerText = findViewById(R.id.registerText);

        // 注册点击监听器
        loginBn.setOnClickListener(this);
        registerText.setOnClickListener(this);

        // 获取mSpf
        mSpf = getSharedPreferences("user", MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 发送登录请求
            case R.id.loginBn:
                login();
                break;
            // 跳转到注册界面
            case R.id.registerText:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void login() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        String name = String.valueOf(username.getText());
        String pass = String.valueOf(password.getText());
        String url = "http://www.baidu.com";
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("username", name);
        paramsMap.put("password", pass);
        // 执行在UI线程（主线程）
        OkhttpUtil.okHttpPost(url, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Toast.makeText(LoginActivity.this, "不能正常连接到服务器", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                // Response res = JSON.parseObject(response, Response.class);

                // 测试数据的保存
                saveInfo();

                Intent intent = new Intent("ACTION_HOME");
                startActivity(intent);
                // 判断是否登录成功
                /*
                switch (res.getStatus()) {
                    case 1:
                        // step.1 保存用户标识

                        // step.2 跳转到主界面
                        Intent intent = new Intent("ACTION_HOME");
                        startActivity(intent);
                        break;
                    default:
                        Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        break;

                }
                */
            }
        });
    }

    private void saveInfo()
    {
        SharedPreferences.Editor editor = mSpf.edit();
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        String name = String.valueOf(username.getText());
        String pass = String.valueOf(password.getText());
        editor.putString("username", name);
        editor.putString("password", pass);
        editor.commit();
    }

}

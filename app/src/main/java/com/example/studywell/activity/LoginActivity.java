package com.example.studywell.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.studywell.pojo.Res;
import com.example.studywell.utils.CallBackUtil;
import com.example.studywell.utils.OkhttpUtil;

import java.util.HashMap;

import okhttp3.Call;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    /* 控件对象 */
    private Button btn_login;       // 登录按钮
    private Button btn_reg;         // 注册按钮
    private EditText et_username;   // 用户名输入框
    private EditText et_password;   // 密码输入框

    /* 本地存储键值对 */
    private SharedPreferences mSpf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // 初始化控件
        btn_login = findViewById(R.id.btn_login);
        btn_reg = findViewById(R.id.btn_reg);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);

        // 绑定点击事件
        btn_login.setOnClickListener(this);
        btn_reg.setOnClickListener(this);

        // 获取mSpf
        mSpf = getSharedPreferences("user", MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 登录
            case R.id.btn_login:
                login();
                break;
            // 跳转到注册界面
            case R.id.btn_reg:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void login() {

        String name = String.valueOf(et_username.getText());
        String pass = String.valueOf(et_password.getText());

        // 请求url
        String url = getString(R.string.baseUrl) + "/login";
        // 参数列表
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("username", name);
        paramsMap.put("password", pass);
        // onFailure、onResponse执行在UI线程（主线程）
        OkhttpUtil.okHttpPost(url, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Toast.makeText(LoginActivity.this, "不能正常连接到服务器", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                Res res = JSON.parseObject(response, Res.class);
                // 判断是否登录成功
                switch (res.getCode()) {
                    // 登录成功
                    case 1:
                        // step.1 保存用户标识

                        // step.2 跳转到主界面
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        break;
                    // 出现错误
                    default:
                        Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        break;

                }

            }
        });
    }

/*
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
*/
}

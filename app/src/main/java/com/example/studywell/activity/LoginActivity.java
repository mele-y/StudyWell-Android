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

    private Button btn_login;
    private Button btn_reg;
    private EditText et_username;
    private EditText et_password;
    // 存储
    private SharedPreferences mSpf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.btn_login);
        btn_reg = findViewById(R.id.btn_reg);

        // 注册点击监听器
        btn_login.setOnClickListener(this);
        btn_reg.setOnClickListener(this);

        // 获取mSpf
        mSpf = getSharedPreferences("user", MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 发送登录请求
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
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        String name = String.valueOf(et_username.getText());
        String pass = String.valueOf(et_password.getText());

        Toast.makeText(LoginActivity.this, ""+name+pass, Toast.LENGTH_SHORT).show();

        String url = "http://121.196.150.190/login";
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
                Res res = JSON.parseObject(response, Res.class);
                // 测试数据的保存
                //saveInfo();

                //Intent intent = new Intent("ACTION_HOME");
                //startActivity(intent);
                // 判断是否登录成功

                switch (res.getStatus()) {
                    case 1:
                        // step.1 保存用户标识
                        // step.2 跳转到主界面
                        //Intent intent = new Intent("ACTION_HOME");
                        //startActivity(intent);
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        break;
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

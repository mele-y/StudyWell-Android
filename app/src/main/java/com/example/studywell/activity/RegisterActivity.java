package com.example.studywell.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.studywell.activity.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button registerBn;
    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 注册点击事件监听器
        registerBn = findViewById(R.id.registerBn);
        registerBn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            // 注册按钮点击事件
            case R.id.registerBn:
                register();
                break;
        }
    }

    /* 注册 */
    private void register()
    {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        String name = String.valueOf(username.getText());
        String pass = String.valueOf(password.getText());
        String url = "https://www.baidu.com/";//替换成自己的服务器地址
        SendMessage(url, name, pass);
    }

    private void SendMessage(String url, final String userName, String passWord) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username", userName);
        formBuilder.add("password", passWord);
//        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "连接到服务器失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("login", res);
                        Toast.makeText(RegisterActivity.this, res, Toast.LENGTH_SHORT).show();
                        if (res.equals("0")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, "该用户名已被注册", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, "成功", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                    }
                });
            }
        });

    }
}

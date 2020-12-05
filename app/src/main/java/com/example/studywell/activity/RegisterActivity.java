package com.example.studywell.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.studywell.pojo.Res;
import com.example.studywell.utils.CallBackUtil;
import com.example.studywell.utils.OkhttpUtil;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_takephoto;
    private Button btn_choosephoto;
    private Button btn_reg;
    private EditText et_username;
    private EditText et_password;

    public static final int REG_TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    private ImageView iv_picture;
    private Uri reg_imageUri;
    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 沉浸式状态栏
        QMUIStatusBarHelper.translucent(this);

        View root = LayoutInflater.from(this).inflate(R.layout.activity_register, null);
        ButterKnife.bind(this, root);
        //初始化状态栏
        initTopBar();
        setContentView(root);

        iv_picture = findViewById(R.id.iv_picture);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);

        btn_takephoto = findViewById(R.id.btn_takephoto);
        btn_takephoto.setOnClickListener(this);
        btn_choosephoto = findViewById(R.id.btn_choosephoto);
        btn_choosephoto.setOnClickListener(this);
        btn_reg = findViewById(R.id.btn_reg);
        btn_reg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            // 注册按钮点击事件
            case R.id.btn_reg:
                register();
                break;
            case R.id.btn_takephoto:
                takephoto();
                break;
            case R.id.btn_choosephoto:
                chosephoto();
                break;
        }
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTopBar.setTitle("注册账号");
    }

    /* 注册 */
    private void register()
    {
        String name = String.valueOf(et_username.getText());
        String pass = String.valueOf(et_password.getText());
        Bitmap image = ((BitmapDrawable)iv_picture.getDrawable()).getBitmap();
        File imagefile = saveBitmapFile(image);
        String url = "http://121.196.150.196/register";//替换成自己的服务器地址

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("username", name);
        paramsMap.put("password", pass);
/*
        OkhttpUtil.okHttpUploadFile(url, imagefile,"user_image", "image", paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public Object onParseResponse(Call call, Res res) {
                return null;
            }

            @Override
            public void onFailure(Call call, Exception e) {
                Toast.makeText(RegisterActivity.this, "不能正常连接到服务器", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Object response) {
                Res res = JSON.parseObject((String) response, Res.class);
                switch (res.getStatus()){
                    case 0:
                        Toast.makeText(RegisterActivity.this, "用户已存在", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(RegisterActivity.this,LoginActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        break;
                }

            }
        });
        */

    }

    private void takephoto(){
        Toast.makeText(RegisterActivity.this, "拍照", Toast.LENGTH_SHORT).show();
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try//判断图片是否存在，存在则删除在创建，不存在则直接创建
        {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //判断运行设备的系统版本是否低于Android7.0
        if (Build.VERSION.SDK_INT >= 24)
        {
            reg_imageUri = FileProvider.getUriForFile(RegisterActivity.this,
                    "com.example.cameraalbumtest.fileprovider", outputImage);

        } else {
            reg_imageUri = Uri.fromFile(outputImage);
        }
        //使用隐示的Intent，调用摄像头，并把它存储
        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, reg_imageUri);
        startActivityForResult(intent, REG_TAKE_PHOTO);
        //调用会返回结果的开启方式，返回成功的话，则把它显示出来
    }

    private void chosephoto(){
        Toast.makeText(RegisterActivity.this, "选择照片", Toast.LENGTH_SHORT).show();
        if(ContextCompat.checkSelfPermission(RegisterActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },1);
        }
        // 在新的Intent里面打开，并且传递CHOOSE_PHOTO选项
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //Intent intent = new Intent();
        //intent.setClass(RegisterActivity.this, AlbumActivity.class);
        //也可以这样写intent.setClass(MainActivity.this, OtherActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id", CHOOSE_PHOTO);
        intent.putExtras(bundle);
        RegisterActivity.this.startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REG_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(reg_imageUri));
                        iv_picture.setImageBitmap(bitmap);
                        //将图片解析成Bitmap对象，并把它显现出来
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }


    public File saveBitmapFile(Bitmap bitmap) {
        File file = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}

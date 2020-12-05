package com.example.studywell.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
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
import com.example.studywell.utils.RealFilePathUtil;
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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    private File outputImage;
    //private Uri imageUri;

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

        if (reg_imageUri == null)
        {
            Toast.makeText(this, "照片不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //String url = "http://121.196.150.190/register";//替换成自己的服务器地址
        File user_image;
        // 照片放在缓存里
        if (outputImage != null)
        {
            user_image = outputImage;
        }
        else
        {
            user_image = new File(RealFilePathUtil.getPath(this, reg_imageUri));
        }

        String url = "http://121.196.150.190/register";//替换成自己的服务器地址
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("username", name);
        paramsMap.put("password", pass);

        OkhttpUtil.okHttpUploadFile(url, user_image, "user_photo", OkhttpUtil.FILE_TYPE_IMAGE, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Toast.makeText(RegisterActivity.this, "不能正常连接到服务器", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(float progress, long total) {

            }

            @Override
            public void onResponse(String response) {
                Log.i("==================================",response);
                Res res = JSON.parseObject(response, Res.class);
                switch (res.getCode()) {
                    case 0:
                        Toast.makeText(RegisterActivity.this, "用户已存在", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

    }

    private void takephoto(){
        Toast.makeText(RegisterActivity.this, "拍照", Toast.LENGTH_SHORT).show();
        outputImage = new File(getExternalCacheDir(), "output_image.jpg");
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
        //ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, reg_imageUri);
        startActivityForResult(intent, REG_TAKE_PHOTO);
        //调用会返回结果的开启方式，返回成功的话，则把它显示出来
    }

    private void chosephoto(){
        outputImage = null;
        Toast.makeText(RegisterActivity.this, "选择照片", Toast.LENGTH_SHORT).show();
        if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.
                PERMISSION_GRANTED) {
            Log.d("permission", "permission");
            ActivityCompat.requestPermissions(RegisterActivity.this, new
                    String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Log.d("album", "openAlbum");
            openAlbum();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
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

            case CHOOSE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    reg_imageUri = data.getData();
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.
                    getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();

        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.
                        Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            iv_picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.
                        PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

/*
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
*/
}

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

    public static final int REG_TAKE_PHOTO = 1; //
    public static final int CHOOSE_PHOTO = 2;
    private Uri reg_imageUri;                   // 选择的图片Uri
    private File outputImage;                   // 相机照片

    /* 控件对象 */
    private Button btn_takephoto;       // 拍照按钮
    private Button btn_choosephoto;     // 相册按钮
    private Button btn_reg;             // 注册按钮
    private EditText et_username;       // 用户名输入框
    private EditText et_password;       // 密码输入框
    private ImageView iv_picture;       // 图片展示控件

    @BindView(R.id.topbar)
    QMUITopBar mTopBar;

    /* 调试 */
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

        /* 初始化控件对象 */
        iv_picture = findViewById(R.id.iv_picture);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_takephoto = findViewById(R.id.btn_takephoto);
        btn_choosephoto = findViewById(R.id.btn_choosephoto);
        btn_reg = findViewById(R.id.btn_reg);

        /* 绑定点击事件 */
        btn_takephoto.setOnClickListener(this);
        btn_choosephoto.setOnClickListener(this);
        btn_reg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 注册按钮点击事件
            case R.id.btn_reg:
                register();
                break;
            // 调用相机
            case R.id.btn_takephoto:
                takephoto();
                break;
            // 调用相册
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

    /* 注册新用户 */
    private void register() {
        String name = String.valueOf(et_username.getText());
        String pass = String.valueOf(et_password.getText());

        if (reg_imageUri == null) {
            Toast.makeText(this, "照片不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        File user_image;    // 要上传的文件
        // 判断是上传相机照片还是相册照片
        if (outputImage != null) {
            // 获取存储在缓存的相机照片
            user_image = outputImage;
        } else {
            // 获取相册文件的真实路径
            user_image = new File(RealFilePathUtil.getPath(this, reg_imageUri));
        }

        String url = getString(R.string.baseUrl) + "/register"; // url
        /* 参数列表 */
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("username", name);
        paramsMap.put("password", pass);


        /* 上传单个文件+参数 */
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


    /* 调用相机 */
    private void takephoto() {
        outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try//判断图片是否存在，存在则删除在创建，不存在则直接创建
        {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            Log.e(TAG,Log.getStackTraceString(e));
        }
        // 判断运行设备的系统版本是否低于Android7.0，为outputImage设置Uri
        if (Build.VERSION.SDK_INT >= 24) {
            reg_imageUri = FileProvider.getUriForFile(RegisterActivity.this,
                    "com.example.cameraalbumtest.fileprovider", outputImage);

        } else {
            reg_imageUri = Uri.fromFile(outputImage);
        }
        /*if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                Manifest.permission.CAMERA) != PackageManager.
                PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegisterActivity.this, new
                    String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }*/
        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        // 调用摄像头
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, reg_imageUri);
        // 指定RequestCode为REG_TAKE_PHOTO
        startActivityForResult(intent, REG_TAKE_PHOTO);
    }

    /* 调用相册 */
    private void chosephoto() {
        outputImage = null;
        if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.
                PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegisterActivity.this, new
                    String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        } else {
            openAlbum();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        // RequestCode为CHOOSE_PHOTO
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }


    /* 相机和相册完成的回调函数 */
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, requestCode, data);
        // 根据传回的requestCode判断是相机还是相册
        switch (requestCode) {
            // 相机的返回
            case REG_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        // 将图片解析成Bitmap对象，并把它显示在iv_picture上
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(reg_imageUri));
                        iv_picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                }
                break;
            // 相册的返回
            case CHOOSE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    // 将相册照片Uri保存到reg_imageUri
                    reg_imageUri = data.getData();
                    // 判断手机系统版本号，显示图片
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


    // 显示图片
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
            case 2:

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
}

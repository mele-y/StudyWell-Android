package com.example.studywell.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.studywell.pojo.Res;
import com.example.studywell.utils.CallBackUtil;
import com.example.studywell.utils.OkhttpUtil;
import com.example.studywell.utils.RealFilePathUtil;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_bookname;
    private EditText et_author;
    private EditText et_publication;
    private EditText et_pubdate;
    private EditText et_description;
    private TextView btn_choosebook;
    private TextView btn_choosecover;
    private Button btn_upload;
    private String category;//类别
    private String bookpath;
    public static final int CHOOSE_PHOTO = 2;
    private Uri reg_imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        et_bookname = findViewById(R.id.et_bookname);
        et_author = findViewById(R.id.et_author);
        et_publication = findViewById(R.id.et_publication);
        et_pubdate = findViewById(R.id.et_pubdate);
        et_description = findViewById(R.id.et_description);

        btn_choosebook = findViewById(R.id.btn_choosebook);
        btn_choosebook.setOnClickListener(this);
        btn_choosecover = findViewById(R.id.btn_choosecover);
        btn_choosecover.setOnClickListener(this);
        btn_upload = findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(this);
        RadioGroup radioGroup=findViewById(R.id.upload_categoryGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb =findViewById(checkedId);
                category=rb.getText().toString();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_choosebook:
                chooseBook();
                break;
            case R.id.btn_choosecover:
                chooseCover();
                break;
            case R.id.btn_upload:
                uploadBook();
                break;
        }
    }

    private void chooseBook(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");  //设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    private void chooseCover(){
        Toast.makeText(UploadActivity.this, "选择照片", Toast.LENGTH_SHORT).show();
        if (ContextCompat.checkSelfPermission(UploadActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.
                PERMISSION_GRANTED) {
            Log.d("permission", "permission");
            ActivityCompat.requestPermissions(UploadActivity.this, new
                    String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Log.d("album", "openAlbum");
            openAlbum();
        }
    }

    private void uploadBook(){

        String bookname = String.valueOf(et_bookname.getText());
        if(bookname.equals("")){
            Toast.makeText(this, "书名不能为空", Toast.LENGTH_SHORT).show();
            Log.i("=====================书名", bookname);
            return;
        }
        String author = String.valueOf(et_author.getText());
        String publication = String.valueOf(et_publication.getText());
        String pubdate = String.valueOf(et_pubdate.getText());
        String description = String.valueOf(et_description.getText());

        String url = "http://121.196.150.190/upload_book";//替换成自己的服务器地址

        HashMap<String, String> params = new HashMap<>();
        params.put("book_name", bookname);
        params.put("author", author);
        params.put("publication", publication);
        params.put("description", description);
        params.put("publish_date", pubdate);
        params.put("category",category);
        if (bookpath == null) {
            Toast.makeText(this, "文件不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        File bookfile = new File(bookpath);

        if (reg_imageUri == null) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_book);
            reg_imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", ""));
//            Toast.makeText(this, "封面不能为空", Toast.LENGTH_SHORT).show();
            //return;
        }
        File imagefile = new File(RealFilePathUtil.getPath(this, reg_imageUri));

        HashMap<String, File> filemap = new HashMap<>();
        filemap.put("book_file",bookfile);
        filemap.put("book_cover",imagefile);

        LoadingDialog ld = new LoadingDialog(this);
        ld.setLoadingText("上传中")
                .setSuccessText("上传成功")//显示加载成功时的文字
                .setFailedText("上传失败")
                .show();

        OkhttpUtil.okHttpUploadMapFile(url, filemap, OkhttpUtil.FILE_TYPE_FILE, params, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Toast.makeText(UploadActivity.this, "不能正常连接到服务器", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                Res res = JSON.parseObject(response, Res.class);

                // 判断是否注册成功
                switch (res.getCode()) {
                    case 1:
                    {
                        Toast.makeText(UploadActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                        ld.loadSuccess();
                        break;
                    }
                    case 0:
                    {
                        Toast.makeText(UploadActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                        ld.loadFailed();
                        break;
                    }
                    default:
                        break;
                }
            }
        });
        et_bookname.setText("");
        et_author.setText("");
        et_publication.setText("");
        et_pubdate.setText("");
        et_description.setText("");
        btn_choosebook.setText("");
        btn_choosecover.setText("");
        bookpath=null;
        reg_imageUri=null;
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    bookpath = RealFilePathUtil.getPath(UploadActivity.this, uri);
                    Log.i("==================path",bookpath);
                    btn_choosebook.setText(bookpath);
                }
            }
        }
        else if (requestCode == 2){
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    reg_imageUri = uri;
                    String imagePath = RealFilePathUtil.getPath(UploadActivity.this, reg_imageUri);
                    btn_choosecover.setText(imagePath);
                }
            }
        }
    }
}

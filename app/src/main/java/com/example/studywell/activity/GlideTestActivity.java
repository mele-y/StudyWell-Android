package com.example.studywell.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GlideTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide_test);
        Button button1=(Button)findViewById(R.id.button1);
        ImageView image1 =(ImageView)findViewById(R.id.image1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(GlideTestActivity.this).load(
                        "http://121.196.150.190/static/meley.jpg"
                ).into(image1);
            }
        });

    }
}
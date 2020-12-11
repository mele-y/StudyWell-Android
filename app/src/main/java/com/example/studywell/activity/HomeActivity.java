package com.example.studywell.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.studywell.adapter.BookAdapter;
import com.example.studywell.pojo.Book;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private List<Book> books = new ArrayList<>();
    private ListView listView;
    // 存储
    private SharedPreferences mSpf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // 测试数据的读取
        mSpf = getSharedPreferences("user", MODE_PRIVATE);
        readInfo();
        /*
        // 传入测试数据
        initBooks();
        // 使用android内置的listItem控件
        BookAdapter bookAdapter = new BookAdapter(HomeActivity.this,
                R.layout.book_item, books);
        listView = findViewById(R.id.bookListView);
        listView.setAdapter(bookAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Book book = books.get(position);
                Toast.makeText(HomeActivity.this, book.getId()+"", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void initBooks() {
        for (int i = 0; i < 50; ++i) {
            Book book = new Book();
            book.setBook_name("简.爱");
            book.setAuthor("夏洛蒂·勃朗特");
            book.setBook_description("《简·爱》（Jane Eyre）是英国女作家夏洛蒂·勃朗特创作的长篇小说，是一部具有自传色彩的作品");
            book.setPublication("XXX出版社");
            book.setPublish_date("2018.8");
            book.setUpload_date("2020.12.3");
            book.setId(i);
            books.add(book);
        }
    }

    private void readInfo()
    {
        String username = mSpf.getString("username", "");
        String password = mSpf.getString("password", "");
        Toast.makeText(this, username + " " + password, Toast.LENGTH_SHORT).show();
    }
}

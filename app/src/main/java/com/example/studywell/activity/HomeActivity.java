package com.example.studywell.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.studywell.adapter.BookAdapter;
import com.example.studywell.pojo.Book;
import com.example.studywell.pojo.BookList;
import com.example.studywell.pojo.Res;
import com.example.studywell.utils.CallBackUtil;
import com.example.studywell.utils.OkhttpUtil;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private List<Book> books = new ArrayList<>();
    private ListView listView;
    // 存储
    private SharedPreferences mSpf;

    private BookAdapter bookAdapter;

    public static HomeActivity homeActivity;

    // 当前页
    private int curPage;
    private int pageNum;

    // 控件
    private FloatingActionButton nextPageBn;
    private FloatingActionButton previousPageBn;

    /* 调试 */
    final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        nextPageBn = findViewById(R.id.next_page_button);
        previousPageBn = findViewById(R.id.previous_page_button);

        /* 绑定点击事件 */
        nextPageBn.setOnClickListener(this);
        previousPageBn.setOnClickListener(this);

        // 方便其它类显示Toast
        homeActivity = this;

        // 测试数据的读取
        mSpf = getSharedPreferences("user", MODE_PRIVATE);
        readInfo();

        // 使用android内置的listItem控件
        bookAdapter = new BookAdapter(HomeActivity.this,
                R.layout.book_card, books);
        listView = findViewById(R.id.bookListView);
        listView.setAdapter(bookAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Book book = books.get(position);
                Toast.makeText(HomeActivity.this, book.getBook_id()+"", Toast.LENGTH_SHORT).show();
            }
        });

        // 获取书籍列表并将其与listView绑定
        initBooks();

    }

    private void initBooks() {
        curPage = 1;
        getBooks();
    }

    private void readInfo()
    {
        String username = mSpf.getString("username", "");
        String password = mSpf.getString("password", "");
        //Toast.makeText(this, username + " " + password, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.next_page_button:
                if (curPage >= pageNum)
                {
                    Toast.makeText(HomeActivity.this, "没有下一页了", Toast.LENGTH_SHORT).show();
                    return;
                }
                curPage++;
                getBooks();
                break;
            case R.id.previous_page_button:
                if (curPage <= 1)
                {
                    Toast.makeText(HomeActivity.this, "前面没有了", Toast.LENGTH_SHORT).show();
                    return;
                }
                curPage--;
                getBooks();
                break;
        }
    }


    private void getBooks()
    {
        String url = getString(R.string.baseUrl) + "/query";
        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(curPage));
        params.put("info", "");
        OkhttpUtil.okHttpGet(url, params,new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Toast.makeText(HomeActivity.this, "不能正常连接到服务器", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                Res res = JSON.parseObject(response, Res.class);
                switch (res.getCode()) {
                    // 查询成功
                    case 1:
                        //
                        BookList list = JSON.parseObject(res.getData(), BookList.class);
                        // 设置页数
                        pageNum = list.getPages();
                        // 加载一页数据前先清空原有的数据
                        books.clear();
                        books.addAll(list.getData_book());
                        // 更新视图
//                        bookAdapter = new BookAdapter(HomeActivity.this,
//                                R.layout.book_card, books);
//                        listView.setAdapter(bookAdapter);
                        bookAdapter.notifyDataSetChanged();
                        break;
                    // 出现错误
                    default:
                        Toast.makeText(HomeActivity.this, "未查询到书籍", Toast.LENGTH_SHORT).show();
                        break;

                }

            }
        });
    }
}

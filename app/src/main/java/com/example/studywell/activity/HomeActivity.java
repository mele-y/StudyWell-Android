package com.example.studywell.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.example.studywell.adapter.BookRecyclerViewAdapter;
import com.example.studywell.pojo.ASRresponse;
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

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, EventListener {

    private List<Book> books = new ArrayList<>();
    private ListView listView;
    // 存储
    private SharedPreferences mSpf;

    private BookRecyclerViewAdapter bookAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static HomeActivity homeActivity;

    // 当前页
    private int curPage;
    private int pageNum;

    // 控件
    private FloatingActionButton nextPageBn;
    private FloatingActionButton previousPageBn;
    private FloatingSearchView mSearchView; // 搜索框
    private FloatingActionButton uploadPageBn;
    private Button voiceBn;
    private EventManager asr;//语音识别核心库
    // 关键字，这里默认为空字符串，不然null查询不到结果
    private String mLastQuery = "";

    /* 调试 */
    final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /* 语音识别初始化工作 */
        initPermission();  // 麦克风动态获取权限
        asr = EventManagerFactory.create(this, "asr");  // 初始化EventManager对象
        asr.registerListener(this);  //注册自己的输出事件类

        /* 初始化控件 */
        nextPageBn = findViewById(R.id.next_page_button);
        previousPageBn = findViewById(R.id.previous_page_button);
        mSearchView = findViewById(R.id.floating_search_view);
        uploadPageBn = findViewById(R.id.upload_float_button);
        voiceBn = findViewById(R.id.action_voice_rec);  // 语音识别按钮
        /* 绑定点击事件 */
        nextPageBn.setOnClickListener(this);
        previousPageBn.setOnClickListener(this);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLastQuery = "";
                getBooks();
            }
        });
        uploadPageBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {

                mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;
                Toast.makeText(HomeActivity.this, mLastQuery, Toast.LENGTH_SHORT).show();
                getBooks();
            }
        });
        // 语音识别按钮的长按事件
//        voiceBn.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View arg0, MotionEvent event) {
//                // TODO Auto-generated method stub
//                int action = event.getAction();
//                // 按下
//                if (action == MotionEvent.ACTION_DOWN) {
//                    asr.send(SpeechConstant.ASR_START, null, null, 0, 0);
//
//                }
//                // 松开
//                else if (action == MotionEvent.ACTION_UP) {
//                    asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
//                }
//                return false;
//
//            }
//        });

        // 方便其它类显示Toast
        homeActivity = this;

        // 测试数据的读取
        mSpf = getSharedPreferences("user", MODE_PRIVATE);
        readInfo();

        RecyclerView recyclerView = findViewById(R.id.bookRecyclerView);
        StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        bookAdapter = new BookRecyclerViewAdapter(books);
        recyclerView.setAdapter(bookAdapter);

        // 获取书籍列表并将其与recyclerView绑定
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
        params.put("info", mLastQuery);
        OkhttpUtil.okHttpGet(url, params,new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(HomeActivity.this, "不能正常连接到服务器", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                swipeRefreshLayout.setRefreshing(false);
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

    /**
     * 自定义输出事件类 EventListener 回调方法
     */
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {

        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            // 识别相关的结果都在这里
            if (params == null || params.isEmpty()) {
                return;
            }
            if (params.contains("\"final_result\"")) {
                // 一句话的最终识别结果
                ASRresponse asRresponse = JSON.parseObject(params, ASRresponse.class);
                if (asRresponse == null) return;
                //从日志中，得出Best_result的值才是需要的，但是后面跟了一个中文输入法下的逗号，
                if (asRresponse.getBest_result().contains("，")) {//包含逗号  则将逗号替换为空格，这个地方还会问题，还可以进一步做出来，你知道吗？
                    mLastQuery = asRresponse.getBest_result().replace('，', ' ').trim();//替换为空格之后，通过trim去掉字符串的首尾空格
                } else {//不包含
                    mLastQuery = asRresponse.getBest_result().trim();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //发送取消事件
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        //退出事件管理器
        // 必须与registerListener成对出现，否则可能造成内存泄露
        asr.unregisterListener(this);
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }
}

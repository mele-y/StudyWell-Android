package com.example.studywell.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.studywell.activity.HomeActivity;
import com.example.studywell.activity.R;
import com.example.studywell.pojo.Book;

import java.util.List;

public class BookRecyclerViewAdapter extends RecyclerView.Adapter<BookRecyclerViewAdapter.ViewHolder> {
    private List<Book> mBookList;
    /* 调试 */
    final String TAG = getClass().getSimpleName();

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookName;
        TextView uploadTime;
        TextView bookDescription;
        ImageView coverImg;
        Button downloadBn;
        BookRecyclerViewAdapter.MyListener myListener;

        public ViewHolder(View view) {
            super(view);
            bookName = view.findViewById(R.id.book_name);
            bookDescription = view.findViewById(R.id.book_description);
            uploadTime = view.findViewById(R.id.upload_time);
            downloadBn = view.findViewById(R.id.downloadBn);
            coverImg = view.findViewById(R.id.cover_pic);
            // 初始化点击事件对象
//            myListener = new BookRecyclerViewAdapter.MyListener(position);
        }
    }

    public BookRecyclerViewAdapter(List<Book> bookList) {
        mBookList = bookList;
    }

    /* 加载外层布局文件，创建对应的ViewHolder */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_card, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    /* 屏幕滚动到对应子项执行 */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Book book = mBookList.get(position);
        holder.bookName.setText(book.getBook_name());
        holder.bookDescription.setText(book.getBook_description());
        holder.uploadTime.setText(book.getUpload_date());
        // 为book_card的下载按钮设置点击事件
        holder.downloadBn.setOnClickListener(new BookRecyclerViewAdapter.MyListener(position));
        // 设置书籍封面
        Glide.with(HomeActivity.homeActivity).load(
                book.getBook_cover_url()
        ).into(holder.coverImg);
    }

    /* 告诉RecyclerView有多少子项 */
    @Override
    public int getItemCount() {
        return mBookList.size();
    }

    private class MyListener implements View.OnClickListener {
        int mPosition;

        public MyListener(int inPosition) {
            mPosition = inPosition;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Toast.makeText(HomeActivity.homeActivity, mPosition + "", Toast.LENGTH_SHORT).show();
            Log.d(TAG, mPosition + "");
        }

    }
}

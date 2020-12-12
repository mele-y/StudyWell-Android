package com.example.studywell.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.example.studywell.activity.HomeActivity;
import com.example.studywell.activity.R;
import com.example.studywell.pojo.Book;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    private int resourceId;
//    private List<Book> books;

    /* 调试 */
    final String TAG = getClass().getSimpleName();

    class ViewHolder {
        //        ImageView bookImage;
        TextView bookName;
        TextView uploadTime;
        TextView bookDescription;
        ImageView coverImg;
        Button downloadBn;
        MyListener myListener;
    }

    public BookAdapter(@NonNull Context context, int resource, @NonNull List<Book> objects) {
        super(context, resource, objects);
//        books = objects;
        resourceId = resource;
    }




    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // 获取当前位置的book对象
        Book book = getItem(position);
        // 防止重复加载布局，复用之前缓存的布局
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent,
                    false);
            viewHolder = new ViewHolder();
            // viewHolder.fruitImage = (ImageView) view.findViewById(R.id.fruit_image);
            viewHolder.bookName = view.findViewById(R.id.book_name);
            viewHolder.bookDescription = view.findViewById(R.id.book_description);
            viewHolder.uploadTime = view.findViewById(R.id.upload_time);
            viewHolder.downloadBn = view.findViewById(R.id.downloadBn);
            viewHolder.coverImg = view.findViewById(R.id.cover_pic);
            // 初始化点击事件对象
            viewHolder.myListener = new MyListener(position);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.bookName.setText(book.getBook_name());
        viewHolder.bookDescription.setText(book.getBook_description());
        viewHolder.uploadTime.setText(book.getUpload_date());
        // 为book_card的下载按钮设置点击事件
        viewHolder.downloadBn.setOnClickListener(viewHolder.myListener);
        // 设置书籍封面
        Glide.with(HomeActivity.homeActivity).load(
                book.getBook_cover_url()
        ).into(viewHolder.coverImg);
        return view;
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

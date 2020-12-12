package com.example.studywell.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.studywell.activity.R;
import com.example.studywell.pojo.Book;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    private int resourceId;

    class ViewHolder {
//        ImageView bookImage;
        TextView bookName;
        TextView uploadTime;
        TextView bookDescription;
    }

    public BookAdapter(@NonNull Context context, int resource, @NonNull List<Book> objects) {
        super(context, resource, objects);
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
        if (convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent,
                    false);
            viewHolder = new ViewHolder();
            // viewHolder.fruitImage = (ImageView) view.findViewById(R.id.fruit_image);
            viewHolder.bookName = view.findViewById (R.id.book_name);
           viewHolder.bookDescription = view.findViewById(R.id.book_description);
           viewHolder.uploadTime = view.findViewById(R.id.upload_time);

            view.setTag(viewHolder); // 将ViewHolder存储在View中
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        // ImageView bookImage = (ImageView) view.findViewById(R.id.book_image);
        // TextView bookName = (TextView) view.findViewById(R.id.book_name);
        // bookImage.setImageResource();
        viewHolder.bookName.setText(book.getBook_name());
        viewHolder.bookDescription.setText(book.getBook_description());
        viewHolder.uploadTime.setText(book.getUpload_date());
        return view;
    }
}

package com.example.studywell.pojo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class Response {
    private int status;
    private String msg;
    private String data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static void main(String[] args) {
        // data是单个对象
        JSONObject object = new JSONObject();
        object.put("status", 0);
        object.put("msg", "login success");
        JSONObject data = new JSONObject();
        data.put("book_id", 1);
        data.put("book_name", "1984");
        data.put("author", "George Orwel");
        data.put("publication", "George Orwel");
        data.put("book_description", "George Orwel");
        data.put("publish_date", "George Orwel");
        data.put("upload_date", "George Orwel");
        object.put("data", data.toJSONString());
        Response response = JSON.parseObject(object.toJSONString(), Response.class);
        Book book = JSON.parseObject(response.getData(), Book.class);
        System.out.println(response);
        System.out.println(book);
        // data是列表
        JSONObject object2 = new JSONObject();
        object.put("status", 0);
        object.put("msg", "login success");
        JSONArray data2 = new JSONArray();
        data2.add(book);
        data2.add(book);
        object2.put("data", data2.toJSONString());
        Response response2 = JSON.parseObject(object2.toJSONString(), Response.class);
        List<Book> books = JSON.parseArray(response2.getData(), Book.class);
        System.out.println(response2);
        System.out.println(books);
    }
}

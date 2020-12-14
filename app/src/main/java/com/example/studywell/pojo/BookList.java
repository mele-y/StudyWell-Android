package com.example.studywell.pojo;

import java.util.List;

public class BookList {
    private int page;
    private int pages;
    private List<Book> data_book;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<Book> getData_book() {
        return data_book;
    }

    public void setData_book(List<Book> data_book) {
        this.data_book = data_book;
    }

    @Override
    public String toString() {
        return "BookList{" +
                "page=" + page +
                ", pages=" + pages +
                ", data_book=" + data_book +
                '}';
    }
}

package com.example.studywell.pojo;

public class Book {
    private int book_id;
    private String book_name;
    private String author;
    private String publication;
    private String book_description;
    private String publish_date;
    private String upload_date;
    private String book_location;
    private String book_cover_url;

    public String getBook_cover_url() {
        return book_cover_url;
    }

    public void setBook_cover_url(String book_cover_url) {
        this.book_cover_url = book_cover_url;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public String getBook_location() {
        return book_location;
    }

    public void setBook_location(String book_location) {
        this.book_location = book_location;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public String getBook_description() {
        return book_description;
    }

    public void setBook_description(String book_description) {
        this.book_description = book_description;
    }

    public String getPublish_date() {
        return publish_date;
    }

    public void setPublish_date(String publish_date) {
        this.publish_date = publish_date;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }

    @Override
    public String toString() {
        return "Book{" +
                "book_id=" + book_id +
                ", book_name='" + book_name + '\'' +
                ", author='" + author + '\'' +
                ", publication='" + publication + '\'' +
                ", book_description='" + book_description + '\'' +
                ", publish_date='" + publish_date + '\'' +
                ", upload_date='" + upload_date + '\'' +
                ", book_location='" + book_location + '\'' +
                ", book_cover_url='" + book_cover_url + '\'' +
                '}';
    }
}


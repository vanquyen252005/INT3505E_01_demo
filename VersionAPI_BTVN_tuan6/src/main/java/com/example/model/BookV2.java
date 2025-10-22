package com.example.model;

public class BookV2 {
    private String title;
    private String author;
    private String publisher;

    public BookV2(String title, String author, String publisher) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
    }

    // Getters & Setters
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }
}

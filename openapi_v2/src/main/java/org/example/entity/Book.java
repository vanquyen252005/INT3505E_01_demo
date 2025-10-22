package org.example.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Thông tin sách")
public class Book {
    @Schema(description = "ID sách", example = "1")
    private int id;
    @Schema(description = "Tiêu đề sách", example = "Clean Code")
    private String title;
    @Schema(description = "Tác giả", example = "Robert C. Martin")
    private String author;
    @Schema(description = "Nhà xuất bản", example = "Addison-Wesley")
    private String publisher;
    @Schema(description = "Số lượng tồn kho", example = "5")
    private int stockQuantity;
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}

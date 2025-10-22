package com.example.controller;

import com.example.model.BookV1;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookV1Controller {
    @GetMapping("")
    public List<BookV1> getBooksV1() {
        return List.of(
                new BookV1("Harry Potter", "J.K. Rowling"),
                new BookV1("Sapiens", "Yuval Noah Harari")
        );
    }
}

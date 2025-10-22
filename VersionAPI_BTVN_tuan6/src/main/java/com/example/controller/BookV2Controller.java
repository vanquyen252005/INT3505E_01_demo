package com.example.controller;

import com.example.model.BookV2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/books")
public class BookV2Controller {
    @GetMapping("")
    public List<BookV2> getBooksV2() {
        return List.of(
                new BookV2("Harry Potter", "J.K. Rowling", "Bloomsbury"),
                new BookV2("Sapiens", "Yuval Noah Harari", "Harvill Secker")
        );
    }
}

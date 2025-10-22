package org.example.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.example.entity.Book;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/books")
public class BookController {

    private final Map<Integer, Book> bookStore = new HashMap<>();
    private int currentId = 0;

    // 1. Lấy danh sách sach
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(new ArrayList<>(bookStore.values())); //200
    }

    // 2. Tạo mới sach
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo mới thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "409", description = "Sách đã tồn tại")
    })
    public ResponseEntity<?> createBook(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin sách cần thêm",
                    required = true
            )
            @RequestBody Book book) {
        if(book.getTitle() == null || book.getTitle().isEmpty()){
            return ResponseEntity.badRequest().build(); //400
        }
        if(book.getAuthor() == null || book.getAuthor().isEmpty()){
            return ResponseEntity.badRequest().build(); //400
        }
        if(book.getPublisher() == null || book.getPublisher().isEmpty()){
            return ResponseEntity.badRequest().build(); //400
        }
        if(book.getStockQuantity() == 0){
            return ResponseEntity.badRequest().build(); //400
        }
        // check trùng email
        boolean bookExists = bookStore.values().stream()
                .anyMatch(s -> s.getTitle().equalsIgnoreCase(book.getTitle()));
        if(bookExists){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Sach da ton tai"); // 409 Conflict
        }
        book.setId(currentId++);
       bookStore.put(book.getId(), book);
        return ResponseEntity.status(HttpStatus.CREATED).body("Create success book"); // 201 Created
    }

    // 3. Cập nhật sach
    @PutMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cap nhat thanh cong"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Khong co sach")
    })
    public ResponseEntity<String> updateBook(
            @Parameter(description = "ID của sách cần lấy", example = "1")
            @PathVariable int id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin sách cần thêm mới",
                    required = true
            )
            @RequestBody Book book) {
        Book existing = bookStore.get(id);
        if (existing == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        if(book.getTitle() == null || book.getTitle().isEmpty()){
            return ResponseEntity.badRequest().build(); //400
        }
        if(book.getAuthor() == null || book.getAuthor().isEmpty()){
            return ResponseEntity.badRequest().build(); //400
        }
        if(book.getPublisher() == null || book.getPublisher().isEmpty()){
            return ResponseEntity.badRequest().build(); //400
        }
        if(book.getStockQuantity() == 0){
            return ResponseEntity.badRequest().build(); //400
        }
        else {
            existing.setTitle(book.getTitle());
            existing.setAuthor(book.getAuthor());
            existing.setPublisher(book.getPublisher());
            existing.setStockQuantity(book.getStockQuantity());
        }
        return ResponseEntity.status(HttpStatus.OK).body("Update success book at " + id); // 200 OK
    }
    // 4. Cập nhật một phần thông tin sinh viên (PATCH)
    @PatchMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cap nhat thanh cong"),
            @ApiResponse(responseCode = "404",description = "Khong tim thay sach")
    })
    public ResponseEntity<String> patchBook(
                                            @Parameter(description = "ID của sách cần lấy", example = "1")
                                            @PathVariable int id,
                                            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                    description = "Thông tin sách cần thêm mới",
                                                    required = true
                                            )
                                            @RequestBody Map<String, String> updates) {
        Book existing = bookStore.get(id);
        if (existing == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        if (updates.containsKey("title")) {
            existing.setTitle(updates.get("title"));
        }
        if (updates.containsKey("author")) {
            existing.setAuthor(updates.get("author"));
        }
        if (updates.containsKey("publisher")) {
            existing.setPublisher(updates.get("publisher"));
        }
        if (updates.containsKey("stockQuantity")) {
            existing.setStockQuantity(Integer.parseInt(updates.get("stockQuantity")));
        }

        return ResponseEntity.status(HttpStatus.OK).body("Update book at" + id); // 200 OK
    }

    // 5. Xóa sach
    @DeleteMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Khong tim thay sach"),
            @ApiResponse(responseCode = "204", description = "Xoa sach thanh cong")
    })
    public ResponseEntity<String> deleteBook(
            @Parameter(description = "ID của sách cần lấy", example = "1")
            @PathVariable int id) {
        Book removed = bookStore.remove(id);
        if (removed == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Delete book at " + id); // 204 No Content
    }
}


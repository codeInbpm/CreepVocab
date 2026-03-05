package com.creepvocab.controller;

import com.creepvocab.common.result.Result;
import com.creepvocab.entity.Book;
import com.creepvocab.entity.BookCategory;
import com.creepvocab.service.BookService;
import com.creepvocab.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Book API")
@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(summary = "Get Category List")
    @GetMapping("/category/list")
    public Result<List<BookCategory>> getCategoryList() {
        return Result.success(bookService.getCategoryList());
    }

    @Operation(summary = "Get Books by Category")
    @GetMapping("/list")
    public Result<List<Book>> getBooks(@RequestParam Long categoryId) {
        return Result.success(bookService.getBooksByCategory(categoryId));
    }

    @Operation(summary = "Get Book Detail (Info + Articles)")
    @GetMapping("/detail")
    public Result<Map<String, Object>> getBookDetail(@RequestParam Long bookId) {
        Long userId = null;
        try { userId = SecurityUtils.getUserId(); } catch (Exception ignored) {}
        return Result.success(bookService.getBookDetail(userId, bookId));
    }

    @Operation(summary = "Get Vocabulary Stats for Book")
    @GetMapping("/vocab-stats")
    public Result<Map<String, Object>> getVocabStats(@RequestParam Long bookId) {
        Long userId = null;
        try { userId = SecurityUtils.getUserId(); } catch (Exception ignored) {}
        return Result.success(bookService.getVocabStats(userId, bookId));
    }

    @Operation(summary = "Toggle Favorite for a Book")
    @PostMapping("/favorite")
    public Result<?> toggleFavorite(@RequestBody Map<String, Long> payload) {
        Long userId = SecurityUtils.getUserId();
        bookService.toggleFavorite(userId, payload.get("bookId"));
        return Result.success();
    }
}

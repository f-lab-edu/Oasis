package com.flab.oasis.controller;

import com.flab.oasis.model.Book;
import com.flab.oasis.model.BookSearchRequest;
import com.flab.oasis.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("/{keyword}")
    public List<Book> findBookListByKeyword(@PathVariable String keyword) {
        return bookService.findBookListByKeyword(new BookSearchRequest(keyword));
    }
}

package com.flab.oasis.controller;

import com.flab.oasis.model.Book;
import com.flab.oasis.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("/{keyword}")
    public List<Book> findBookListByKeyword(@PathVariable String keyword) {
        return bookService.findBookListByKeyword(keyword);
    }
}

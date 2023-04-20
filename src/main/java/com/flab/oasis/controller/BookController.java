package com.flab.oasis.controller;

import com.flab.oasis.model.Book;
import com.flab.oasis.model.BookSearchRequest;
import com.flab.oasis.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("/search")
    public List<Book> getBookListByBookSearchRequest(BookSearchRequest bookSearchRequest) {
        return bookService.findBookListByBookSearchRequest(bookSearchRequest);
    }
}

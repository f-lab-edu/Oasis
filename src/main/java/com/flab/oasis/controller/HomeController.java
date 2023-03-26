package com.flab.oasis.controller;

import com.flab.oasis.model.BookSuggestion;
import com.flab.oasis.model.BookSuggestionRequest;
import com.flab.oasis.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;

    @PostMapping("/suggestion")
    public List<BookSuggestion> getBookSuggestionListByBookSuggestionRequest(
            @RequestBody BookSuggestionRequest bookSuggestionRequest) {
        return homeService.getBookSuggestionListByBookSuggestionRequest(bookSuggestionRequest);
    }
}

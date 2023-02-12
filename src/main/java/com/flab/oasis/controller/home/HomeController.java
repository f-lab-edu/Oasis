package com.flab.oasis.controller.home;

import com.flab.oasis.model.Book;
import com.flab.oasis.model.home.BookSuggestion;
import com.flab.oasis.model.home.BookSuggestionRequest;
import com.flab.oasis.service.home.HomeService;
import com.flab.oasis.constant.SuggestionType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;

    @PostMapping("/suggestion")
    public List<BookSuggestion> bookSuggestion(@RequestBody BookSuggestionRequest request) {
        return homeService.suggestion(request.getUid(), request.getSuggestionType());
    }
}

package com.flab.oasis.controller.home;

import com.flab.oasis.model.Book;
import com.flab.oasis.service.home.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;

    @Resource
    CacheManager cacheManager;

    @PostMapping("/suggestion")
    public List<Book> bookSuggestion(@RequestBody Map<String, String> map) {
        return homeService.suggestion(map.get("uid"), map.get("suggestionType"));
    }
}

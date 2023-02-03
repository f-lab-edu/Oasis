package com.flab.oasis.service.home;

import com.flab.oasis.constant.RedisKey;
import com.flab.oasis.constant.SuggestionType;
import com.flab.oasis.model.Book;
import com.flab.oasis.model.home.BookSuggestionRedis;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final BookSuggestionRedis bookSuggestionRedis;

    @Cacheable(cacheNames = "homeCache", keyGenerator = "oasisKeyGenerator")
    public List<Book> suggestion(String uid, SuggestionType suggestionType) {
        return bookSuggestionRedis.getBookList(RedisKey.HOME, uid, suggestionType);
    }
}

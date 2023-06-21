package com.flab.oasis.repository;

import com.flab.oasis.constant.SuggestionType;
import com.flab.oasis.mapper.book.BookSuggestionMapper;
import com.flab.oasis.model.BookSuggestion;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookSuggestionRepository {
    private final BookSuggestionMapper bookSuggestionMapper;

    @Cacheable(cacheNames = "BookSuggestion", key = "#suggestionType", cacheManager = "redisCacheManager")
    public List<BookSuggestion> getBookSuggestionListBySuggestionType(SuggestionType suggestionType) {
        return bookSuggestionMapper.getBookSuggestionBySuggestionType(suggestionType);
    }
}

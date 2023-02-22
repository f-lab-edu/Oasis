package com.flab.oasis.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.oasis.constant.SuggestionType;
import com.flab.oasis.mapper.BookSuggestionMapper;
import com.flab.oasis.model.BookSuggestion;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookSuggestionRepository {
    private final RedisTemplate<SuggestionType, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final BookSuggestionMapper bookSuggestionMapper;

    public List<BookSuggestion> getBookSuggestionListBySuggestionType(SuggestionType suggestionType) {
        return objectMapper.convertValue(
                Optional.ofNullable(
                        redisTemplate.opsForValue().get(suggestionType)
                ).orElse(
                        pushBookSuggestionToRedis(suggestionType)
                ),
                new TypeReference<List<BookSuggestion>>() {}
        );
    }

    private List<BookSuggestion> pushBookSuggestionToRedis(SuggestionType suggestionType) {
        List<BookSuggestion> bookSuggestionList =
                bookSuggestionMapper.findBookSuggestionBySuggestionType(suggestionType);
        redisTemplate.opsForValue().set(suggestionType, bookSuggestionList);

        return bookSuggestionList;
    }
}

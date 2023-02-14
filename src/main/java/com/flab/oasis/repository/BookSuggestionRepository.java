package com.flab.oasis.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.oasis.constant.SuggestionType;
import com.flab.oasis.mapper.HomeMapper;
import com.flab.oasis.model.BookSuggestion;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class BookSuggestionRepository {
    private final RedisTemplate<SuggestionType, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private final HomeMapper homeMapper;

    public List<BookSuggestion> getBookSuggestionList(SuggestionType suggestionType) {
        Object stringValue = redisTemplate.opsForValue().get(suggestionType);

        if (Objects.isNull(stringValue)) {
            return pushBookSuggestionToRedis(suggestionType);
        }

        return objectMapper.convertValue(stringValue, new TypeReference<List<BookSuggestion>>() {});
    }

    private List<BookSuggestion> pushBookSuggestionToRedis(SuggestionType suggestionType) {
        List<BookSuggestion> bookSuggestionList = homeMapper.getBookSuggestion(suggestionType);
        redisTemplate.opsForValue().set(suggestionType, bookSuggestionList);

        return bookSuggestionList;
    }
}

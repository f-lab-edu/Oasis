package com.flab.oasis.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.oasis.constant.RedisKey;
import com.flab.oasis.constant.SuggestionType;
import com.flab.oasis.mapper.home.HomeMapper;
import com.flab.oasis.model.home.BookSuggestion;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BookSuggestionRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final HomeMapper homeMapper;

    private final RedisKey key = RedisKey.HOME;

    public List<BookSuggestion> getBookSuggestionList(SuggestionType suggestionType) {
        return getBookSuggestionFromRedis(key, suggestionType);
    }

    private List<BookSuggestion> getBookSuggestionFromRedis(RedisKey key, SuggestionType field) {
        List<BookSuggestion> bookSuggestionList;
        Object hashValue = redisTemplate.opsForHash().get(key.name(), field);
        if (Objects.isNull(hashValue)) {
            Map<SuggestionType, List<BookSuggestion>> hashFieldValue = getBookSuggestionHashFieldValue();

            redisTemplate.opsForHash().putAll(key.name(), hashFieldValue);
            bookSuggestionList = hashFieldValue.get(field);
        } else {
            bookSuggestionList = objectMapper.convertValue(hashValue, new TypeReference<List<BookSuggestion>>() {});
        }

        return bookSuggestionList;
    }

    private Map<SuggestionType, List<BookSuggestion>> getBookSuggestionHashFieldValue() {
        return homeMapper.getBookSuggestion().stream()
                .collect(Collectors.groupingBy(BookSuggestion::getSuggestionType));
    }
}

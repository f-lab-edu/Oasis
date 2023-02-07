package com.flab.oasis.service.home;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public List<BookSuggestion> getBookSuggestionList(SuggestionType suggestionType) {
        return getBookSuggestionFromRedis(suggestionType);
    }

    private List<BookSuggestion> getBookSuggestionFromRedis(SuggestionType suggestionType) {
        Object stringValue = redisTemplate.opsForValue().get(suggestionType.name());

        if (Objects.isNull(stringValue)) {
            Map<SuggestionType, List<BookSuggestion>> stringKeyValue = getBookSuggestionStringKeyValue();
            stringKeyValue.forEach((key, value) -> redisTemplate.opsForValue().set(key.name(), value));

            return stringKeyValue.get(suggestionType);
        }
        return objectMapper.convertValue(stringValue, new TypeReference<List<BookSuggestion>>() {});
    }

    private Map<SuggestionType, List<BookSuggestion>> getBookSuggestionStringKeyValue() {
        return homeMapper.getBookSuggestion().stream()
                .collect(Collectors.groupingBy(BookSuggestion::getSuggestionType));
    }
}

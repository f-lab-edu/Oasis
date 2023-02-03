package com.flab.oasis.model.home;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.oasis.constant.RedisKey;
import com.flab.oasis.constant.SuggestionType;
import com.flab.oasis.mapper.home.HomeMapper;
import com.flab.oasis.model.Book;
import com.flab.oasis.utils.JsonUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookSuggestionRedis {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final HomeMapper homeMapper;

    public List<Book> getBookList(RedisKey key, String uid, SuggestionType suggestionType) {
        JsonNode redisData = JsonUtils.parseStringToJsonNode(getBookSuggestionFromRedis(key, suggestionType.name()));

        List<Book> bookList = new ArrayList<>();
        getUserCategoryByUid(uid).forEach(uci -> bookList.addAll(
            objectMapper.convertValue(redisData.get(String.valueOf(uci)), new TypeReference<List<Book>>() {})
        ));

        return bookList;
    }

    private String getBookSuggestionFromRedis(RedisKey key, String field) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        return Optional.ofNullable(hashOperations.get(key.name(), field)).orElseGet(() -> {
            pushNewBookSuggestion(getBookSuggestionHashFieldValue());

            return getBookSuggestionFromRedis(key, field);
        });
    }

    private void pushNewBookSuggestion(Map<String, String> hashFieldValue) {
        redisTemplate.opsForHash().putAll(RedisKey.HOME.name(), hashFieldValue);
    }

    private Map<String, String> getBookSuggestionHashFieldValue() {
        return new BookSuggestionCollection(homeMapper.getBookSuggestion()).parseToHashFieldValue();
    }

    private List<Integer> getUserCategoryByUid(String uid) {
        return new UserCategoryCollection(homeMapper.findUserCategoryByUid(uid)).getCategoryIdList();
    }
}

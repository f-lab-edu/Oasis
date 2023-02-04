package com.flab.oasis.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.oasis.constant.RedisKey;
import com.flab.oasis.constant.SuggestionType;
import com.flab.oasis.mapper.home.HomeMapper;
import com.flab.oasis.model.Book;
import com.flab.oasis.model.home.BookSuggestion;
import com.flab.oasis.model.home.UserCategory;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class BookSuggestionRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final HomeMapper homeMapper;

    public List<Book> getBookList(RedisKey key, String uid, SuggestionType suggestionType) {
        Map<Integer, List<Book>> redisData = getBookSuggestionFromRedis(key, suggestionType.name());

        return getBookList(uid, redisData);
    }

    private List<Book> getBookList(String uid, Map<Integer, List<Book>> redisData) {
        List<UserCategory> userCategory = homeMapper.findUserCategoryByUid(uid);
        List<Book> bookList = new ArrayList<>();

        if (userCategory.isEmpty()) {
            redisData.values().forEach(bl -> bookList.addAll(bl));
        } else {
            userCategory.stream().forEach(uci -> bookList.addAll(redisData.get(uci.getCategoryId())));
        }

        return bookList;
    }

    private Map<Integer, List<Book>> getBookSuggestionFromRedis(RedisKey key, String field) {
        List<BookSuggestion> bookSuggestionList;
        Object hashValue = redisTemplate.opsForHash().get(key.name(), field);
        if (Objects.isNull(hashValue)) {
            Map<String, List<BookSuggestion>> hashFieldValue = getBookSuggestionHashFieldValue();

            redisTemplate.opsForHash().putAll(key.name(), hashFieldValue);
            bookSuggestionList = hashFieldValue.get(field);
        } else {
            bookSuggestionList = objectMapper.convertValue(hashValue, new TypeReference<List<BookSuggestion>>() {});
        }

        return groupByCategoryId(bookSuggestionList);
    }

    private Map<String, List<BookSuggestion>> getBookSuggestionHashFieldValue() {
        return homeMapper.getBookSuggestion().stream()
                .collect(Collectors.groupingBy(BookSuggestion::getSuggestionType));
    }

    private static Map<Integer, List<Book>> groupByCategoryId(List<BookSuggestion> bookSuggestionList) {
        return bookSuggestionList.stream().collect(Collectors.groupingBy(Book::getCategoryId));
    }
}

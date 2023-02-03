package com.flab.oasis.model.home;

import com.flab.oasis.constant.RedisKey;
import com.flab.oasis.constant.SuggestionType;
import com.flab.oasis.mapper.home.HomeMapper;
import com.flab.oasis.model.Book;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookSuggestionRedis {
    private final RedisTemplate<String, Map<Integer, List<Book>>> redisTemplate;
    private final HomeMapper homeMapper;

    public List<Book> getBookList(RedisKey key, String uid, SuggestionType suggestionType) {
        Map<Integer, List<Book>> redisData = getBookSuggestionFromRedis(key, suggestionType.getSuggestionType());

        return getBookListByUserCategory(uid, redisData);
    }

    private Map<Integer, List<Book>> getBookSuggestionFromRedis(RedisKey key, String field) {
        HashOperations<String, String, Map<Integer, List<Book>>> hashOperations = redisTemplate.opsForHash();

        return Optional.ofNullable(hashOperations.get(key.name(), field))
                .orElseGet(() -> {
                    Map<String, Map<Integer, List<Book>>> hashFieldValue = getBookSuggestionHashFieldValue();
                    pushNewBookSuggestion(hashFieldValue);

                    return hashFieldValue.get(field);
                });
    }

    private List<Book> getBookListByUserCategory(String uid, Map<Integer, List<Book>> redisData) {
        List<Book> bookList = new ArrayList<>();

        List<Integer> userCategoryList = getUserCategoryByUid(uid);
        userCategoryList.forEach(uc -> bookList.addAll(redisData.get(uc)));

        return bookList;
    }

    private Map<String, Map<Integer, List<Book>>> getBookSuggestionHashFieldValue() {
        return new BookSuggestionCollection(homeMapper.getBookSuggestion()).parseToHashFieldValue();
    }

    private void pushNewBookSuggestion(Map<String, Map<Integer, List<Book>>> hashFieldValue) {
        HashOperations<String, String, Map<Integer, List<Book>>> hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(RedisKey.HOME.name(), hashFieldValue);
    }

    private List<Integer> getUserCategoryByUid(String uid) {
        return new UserCategoryCollection(homeMapper.findUserCategoryByUid(uid)).getCategoryIdList();
    }
}

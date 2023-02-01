package com.flab.oasis.service.home;

import com.flab.oasis.constant.RedisKey;
import com.flab.oasis.constant.SuggestionType;
import com.flab.oasis.mapper.home.HomeMapper;
import com.flab.oasis.model.Book;
import com.flab.oasis.model.home.BookSuggestionCollection;
import com.flab.oasis.model.home.UserCategoryCollection;
import com.flab.oasis.model.redis.BookSuggestionJson;
import com.flab.oasis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final HomeMapper homeMapper;
    private final RedisService redisService;

    @Cacheable(cacheNames = "homeCache", keyGenerator = "oasisKeyGenerator")
    public List<Book> suggestion(String uid, SuggestionType suggestionType) {
        return getBookSuggestionJsonFromRedis(suggestionType).getBookList(getUserCategoryByUid(uid));
    }

    private BookSuggestionJson getBookSuggestionJsonFromRedis(SuggestionType suggestionType) {
        if (redisService.existKey(RedisKey.HOME.name()).equals(Boolean.FALSE)) {
            pushNewBookSuggestion();
        }

        return (BookSuggestionJson) redisService.getHashData(RedisKey.HOME, suggestionType.getSuggestionType());
    }

    private List<String> getUserCategoryByUid(String uid) {
        return new UserCategoryCollection(homeMapper.findUserCategoryByUid(uid)).getCategoryIdList();
    }

    private void pushNewBookSuggestion() {
        redisService.putHashData(RedisKey.HOME.name(), getBookSuggestionHashFieldValue());
    }

    private Map<String, String> getBookSuggestionHashFieldValue() {
        return new BookSuggestionCollection(homeMapper.getBookSuggestion()).parseToHashFieldValue();
    }
}

package com.flab.oasis.service.home;

import com.fasterxml.jackson.databind.JsonNode;
import com.flab.oasis.config.RedisKey;
import com.flab.oasis.mapper.home.HomeMapper;
import com.flab.oasis.model.Book;
import com.flab.oasis.model.home.UserCategoryDTO;
import com.flab.oasis.service.RedisService;
import com.flab.oasis.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final HomeMapper homeMapper;
    private final RedisService redisService;

    @Cacheable(cacheNames = "homeCache", key = "#uid + #suggestionType")
    public List<Book> suggestion(String uid, String suggestionType) {
        return getSuggestionBookList(
                homeMapper.findUserCategoryByUid(uid),
                getBookSuggestionFromRedis(suggestionType)
        );
    }

    private String getBookSuggestionFromRedis(String suggestionType) {
        if (!redisService.checkKey(RedisKey.HOME)) {
            redisService.putHashData(
                    RedisKey.HOME,
                    new BookSuggestionParser().parse(
                            homeMapper.getBookSuggestion()
                    )
            );
        }

        return redisService.getHashData(RedisKey.HOME, suggestionType);
    }

    private List<Book> getSuggestionBookList(List<UserCategoryDTO> userCategoryDTO, String bookSuggestionJsonString) {
        JsonNode bookSuggestionJsonArray = JsonUtils.parseValueToJsonArray(bookSuggestionJsonString);
        List<JsonNode> bookSuggestionJsonArrayByUserCategory = userCategoryDTO.stream().map(
                u -> bookSuggestionJsonArray.get(u.getCategoryId())
        ).collect(Collectors.toList());

        return parseJsonToBookList(bookSuggestionJsonArrayByUserCategory);
    }

    private List<Book> parseJsonToBookList(List<JsonNode> bookSuggestionJsonArrayByUserCategory) {
        List<Book> bookSuggestionList = new ArrayList<>();
        for (JsonNode bookSuggestionJson : bookSuggestionJsonArrayByUserCategory) {
            bookSuggestionList.addAll(JsonUtils.parseValueToBookList(bookSuggestionJson));
        }

        return bookSuggestionList;
    }
}

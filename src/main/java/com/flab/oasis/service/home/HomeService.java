package com.flab.oasis.service.home;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.oasis.config.RedisKey;
import com.flab.oasis.mapper.home.HomeMapper;
import com.flab.oasis.model.Book;
import com.flab.oasis.model.home.UserCategory;
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
        if (!redisService.existKey(RedisKey.HOME)) {
            redisService.putHashData(
                    RedisKey.HOME,
                    BookSuggestionParser.parseBookSuggestion(
                            homeMapper.getBookSuggestion()
                    )
            );
        }

        return redisService.getHashData(RedisKey.HOME, suggestionType);
    }

    private List<Book> getSuggestionBookList(List<UserCategory> userCategory, String bookSuggestionJsonString) {
        return parseJsonToBookList(
                selectNodeByUserCategory(
                        userCategory,
                        JsonUtils.parseStringToJsonNode(bookSuggestionJsonString)
                )
        );
    }

    private static List<JsonNode> selectNodeByUserCategory(List<UserCategory> userCategory, JsonNode jsonNode) {
        return userCategory.stream().map(uc -> jsonNode.get(uc.getCategoryId())).collect(Collectors.toList());
    }

    private List<Book> parseJsonToBookList(List<JsonNode> bookSuggestionJsonArrayByUserCategory) {
        List<Book> bookSuggestionList = new ArrayList<>();
        for (JsonNode bookSuggestionJson : bookSuggestionJsonArrayByUserCategory) {
            bookSuggestionList.addAll(bookSuggestionJsonToBookList(bookSuggestionJson));
        }

        return bookSuggestionList;
    }

    private List<Book> bookSuggestionJsonToBookList(JsonNode bookSuggestionJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(bookSuggestionJson, new TypeReference<List<Book>>() {});
    }
}

package com.flab.oasis.service.home;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.oasis.constant.RedisKey;
import com.flab.oasis.constant.SuggestionType;
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

    @Cacheable(cacheNames = "homeCache", keyGenerator = "oasisKeyGenerator")
    public List<Book> suggestion(String uid, SuggestionType suggestionType) {
        return getSuggestionBookList(
                homeMapper.findUserCategoryByUid(uid),
                getBookSuggestionFromRedis(suggestionType)
        );
    }

    private String getBookSuggestionFromRedis(SuggestionType suggestionType) {
        if (redisService.existKey(RedisKey.HOME.name()).equals(Boolean.FALSE)) {
            pushNewBookSuggestion();
        }

        return redisService.getHashData(RedisKey.HOME.name(), suggestionType.getSuggestionType());
    }

    private void pushNewBookSuggestion() {
        redisService.putHashData(
                RedisKey.HOME.name(),
                BookSuggestionParser.parseBookSuggestion(
                        homeMapper.getBookSuggestion()
                )
        );
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
        if (userCategory.isEmpty()) {
            return JsonUtils.parseJsonNodeToJsonNodeList(jsonNode);
        }

        return JsonUtils.getValueListFromJsonNode(getCategoryIdList(userCategory), jsonNode);
    }

    private List<Book> parseJsonToBookList(List<JsonNode> bookSuggestionJsonArrayByUserCategory) {
        List<Book> bookSuggestionList = new ArrayList<>();
        bookSuggestionJsonArrayByUserCategory.forEach(
                jn -> bookSuggestionList.addAll(bookSuggestionJsonToBookList(jn))
        );

        return bookSuggestionList;
    }

    private static List<String> getCategoryIdList(List<UserCategory> userCategory) {
        return userCategory.stream().map(UserCategory::getCategoryId).collect(Collectors.toList());
    }

    private List<Book> bookSuggestionJsonToBookList(JsonNode bookSuggestionJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(bookSuggestionJson, new TypeReference<List<Book>>() {});
    }
}

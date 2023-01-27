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

    // 캐시키 재설정 필요
    @Cacheable(cacheNames = "homeCache", key = "#uid + #suggestionType.name()")
    public List<Book> suggestion(String uid, SuggestionType suggestionType) {
        return getSuggestionBookList(
                homeMapper.findUserCategoryByUid(uid),
                getBookSuggestionFromRedis(suggestionType)
        );
    }

    private String getBookSuggestionFromRedis(SuggestionType suggestionType) {
        // suggestionType enum 처리
        if (!redisService.existKey(RedisKey.HOME)) {
            redisService.putHashData(
                    RedisKey.HOME,
                    BookSuggestionParser.parseBookSuggestion(
                            homeMapper.getBookSuggestion()
                    )
            );
        }

        return redisService.getHashData(RedisKey.HOME, suggestionType.getSuggestionType());
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
        if (userCategory.size() == 0) {
            // 전체 카테고리 반환
        }

        // jsonNode에서 가져온 데이터가 null safe한지 확인 필요
        return userCategory.stream().map(uc -> jsonNode.get(uc.getCategoryId())).collect(Collectors.toList());
    }

    private List<Book> parseJsonToBookList(List<JsonNode> bookSuggestionJsonArrayByUserCategory) {
        List<Book> bookSuggestionList = new ArrayList<>();
        bookSuggestionJsonArrayByUserCategory.forEach(
                jn -> bookSuggestionList.addAll(bookSuggestionJsonToBookList(jn))
        );

        return bookSuggestionList;
    }

    private List<Book> bookSuggestionJsonToBookList(JsonNode bookSuggestionJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(bookSuggestionJson, new TypeReference<List<Book>>() {});
    }
}

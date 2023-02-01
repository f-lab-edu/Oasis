package com.flab.oasis.model.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.oasis.model.Book;
import com.flab.oasis.utils.JsonUtils;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class BookSuggestionJson {
    private String bookSuggestionJsonString;

    public List<Book> getBookList(List<String> userCategoryList) {
        return parseJsonToBookList(
                selectNodeByUserCategory(
                        userCategoryList,
                        JsonUtils.parseStringToJsonNode(bookSuggestionJsonString)
                )
        );
    }

    private static List<JsonNode> selectNodeByUserCategory(List<String> userCategory, JsonNode jsonNode) {
        if (userCategory.isEmpty()) {
            return JsonUtils.parseJsonNodeToJsonNodeList(jsonNode);
        }

        return JsonUtils.getValueListFromJsonNode(userCategory, jsonNode);
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

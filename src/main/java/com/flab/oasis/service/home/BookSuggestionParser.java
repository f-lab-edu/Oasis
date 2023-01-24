package com.flab.oasis.service.home;

import com.flab.oasis.model.Book;
import com.flab.oasis.model.home.BookSuggestionDTO;
import com.flab.oasis.utils.JsonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookSuggestionParser {
    public Map<String, String> parse(List<BookSuggestionDTO> bookSuggestionDTOList) {
        Map<String, List<BookSuggestionDTO>> suggestionTypeGroupMap = bookSuggestionDTOList.stream().collect(
                Collectors.groupingBy(BookSuggestionDTO::getSuggestionType)
        );

        Map<String, String> bookSuggestionMapForRedis = new HashMap<>();
        for (String type : suggestionTypeGroupMap.keySet()) {
            bookSuggestionMapForRedis.put(type, parseCategoryGroupToJsonString(suggestionTypeGroupMap.get(type)));
        }

        return bookSuggestionMapForRedis;
    }

    private String parseCategoryGroupToJsonString(List<BookSuggestionDTO> bookSuggestionGroup) {
        Map<Integer, List<Book>> categoryIdGroupMap = bookSuggestionGroup.stream().collect(
                Collectors.groupingBy(Book::getCategoryId)
        );

        return JsonUtils.parseValueToString(categoryIdGroupMap);
    }
}

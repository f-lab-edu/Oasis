package com.flab.oasis.model.home;

import com.flab.oasis.constant.SuggestionType;
import com.flab.oasis.model.Book;
import com.flab.oasis.utils.JsonUtils;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class BookSuggestionCollection {
    private final List<BookSuggestion> bookSuggestionList;

    public Map<String, String> parseToHashFieldValue() {
        Map<String, String> hashFieldValue = new HashMap<>();
        for (Map.Entry<SuggestionType, List<BookSuggestion>> entry : groupBySuggestionType().entrySet()) {
            hashFieldValue.put(
                    getSuggestionType(entry),
                    JsonUtils.parseObjectToString(groupByCategoryId(getBookSuggestionList(entry)))
            );
        }

        return hashFieldValue;
    }

    private Map<SuggestionType, List<BookSuggestion>> groupBySuggestionType() {
        return bookSuggestionList.stream().collect(Collectors.groupingBy(BookSuggestion::getSuggestionType));
    }

    private String getSuggestionType(Map.Entry<SuggestionType, List<BookSuggestion>> entry) {
        return entry.getKey().name();
    }

    private List<BookSuggestion> getBookSuggestionList(Map.Entry<SuggestionType, List<BookSuggestion>> entry) {
        return entry.getValue();
    }

    private Map<Integer, List<Book>> groupByCategoryId(List<BookSuggestion> bookSuggestionListBySuggestionType) {
        return bookSuggestionListBySuggestionType.stream().collect(Collectors.groupingBy(Book::getCategoryId));
    }
}

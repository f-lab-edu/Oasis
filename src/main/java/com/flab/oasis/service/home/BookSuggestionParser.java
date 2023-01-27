package com.flab.oasis.service.home;

import com.flab.oasis.model.Book;
import com.flab.oasis.model.home.BookSuggestion;
import com.flab.oasis.utils.JsonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class BookSuggestionParser {
    public static Map<String, String> parseBookSuggestion(List<BookSuggestion> bookSuggestionList) {
        return parseToHashFieldValue(groupBySuggestionType(bookSuggestionList));
    }

    private static Map<String, List<BookSuggestion>> groupBySuggestionType(List<BookSuggestion> bookSuggestionList) {
        return bookSuggestionList.stream().collect(Collectors.groupingBy(BookSuggestion::getSuggestionType));
    }

    private static Map<String, String> parseToHashFieldValue(
            Map<String, List<BookSuggestion>> bookSuggestionListBySuggestionType) {

        Map<String, String> hashFieldValue = new HashMap<>();
        for (Map.Entry<String, List<BookSuggestion>> entry : bookSuggestionListBySuggestionType.entrySet()) {
            hashFieldValue.put(
                    getSuggestionType(entry),
                    parseBookSuggestionListToJsonString(getBookSuggestionList(entry))
            );
        }

        return hashFieldValue;
    }

    private static String getSuggestionType(Map.Entry<String, List<BookSuggestion>> entry) {
        return entry.getKey();
    }

    private static List<BookSuggestion> getBookSuggestionList(Map.Entry<String, List<BookSuggestion>> entry) {
        return entry.getValue();
    }

    private static String parseBookSuggestionListToJsonString(List<BookSuggestion> bookSuggestionList) {
        return JsonUtils.parseObjectToString(groupByCategoryId(bookSuggestionList));
    }

    private static Map<Integer, List<Book>> groupByCategoryId(List<BookSuggestion> bookSuggestionList) {
        return bookSuggestionList.stream().collect(Collectors.groupingBy(Book::getCategoryId));
    }
}

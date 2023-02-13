package com.flab.oasis.service;

import com.flab.oasis.constant.SuggestionType;
import com.flab.oasis.mapper.HomeMapper;
import com.flab.oasis.model.BookSuggestion;
import com.flab.oasis.model.UserCategory;
import com.flab.oasis.repository.BookSuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final BookSuggestionRepository bookSuggestionRepository;
    private final HomeMapper homeMapper;

    @Cacheable(cacheNames = "homeCache", keyGenerator = "oasisKeyGenerator")
    public List<BookSuggestion> suggestion(String uid, SuggestionType suggestionType) {
        return getBookSuggestionListByUserCategory(
                uid,
                bookSuggestionRepository.getBookSuggestionList(suggestionType)
        );
    }

    private List<BookSuggestion> getBookSuggestionListByUserCategory(
            String uid, List<BookSuggestion> bookSuggestionList) {
        List<UserCategory> userCategory = homeMapper.findUserCategoryByUid(uid);

        if (userCategory.isEmpty()) {
            return bookSuggestionList;
        } else {
            Map<Integer, List<BookSuggestion>> bookListByCategoryId = groupByCategoryId(bookSuggestionList);
            List<BookSuggestion> bookList = new ArrayList<>();
            userCategory.forEach(uci -> bookList.addAll(bookListByCategoryId.get(uci.getCategoryId())));

            return bookList;
        }

    }

    private static Map<Integer, List<BookSuggestion>> groupByCategoryId(List<BookSuggestion> bookSuggestionList) {
        return bookSuggestionList.stream().collect(Collectors.groupingBy(BookSuggestion::getCategoryId));
    }
}

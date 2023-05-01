package com.flab.oasis.service;

import com.flab.oasis.constant.BookCategory;
import com.flab.oasis.mapper.user.UserCategoryMapper;
import com.flab.oasis.model.BookSuggestion;
import com.flab.oasis.model.BookSuggestionRequest;
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
    private final UserCategoryMapper userCategoryMapper;

    @Cacheable(cacheNames = "homeCache", keyGenerator = "oasisKeyGenerator", cacheManager = "ehCacheCacheManager")
    public List<BookSuggestion> getBookSuggestionListByBookSuggestionRequest(
            BookSuggestionRequest bookSuggestionRequest) {
        return matchBookCategoryName(
                filterBookSuggestionListByUserCategory(
                        bookSuggestionRepository.getBookSuggestionListBySuggestionType(
                                bookSuggestionRequest.getSuggestionType()
                        ),
                        bookSuggestionRequest.getUid()
                )
        );
    }

    private List<BookSuggestion> filterBookSuggestionListByUserCategory(
            List<BookSuggestion> bookSuggestionList, String uid) {
        List<UserCategory> userCategory = userCategoryMapper.findUserCategoryByUid(uid);

        if (userCategory.isEmpty()) {
            return bookSuggestionList;
        } else {
            Map<BookCategory, List<BookSuggestion>> bookListByCategoryId = groupByCategoryId(bookSuggestionList);
            List<BookSuggestion> bookList = new ArrayList<>();
            userCategory.forEach(uci -> bookList.addAll(bookListByCategoryId.get(uci.getBookCategory())));

            return bookList;
        }
    }

    private List<BookSuggestion> matchBookCategoryName(List<BookSuggestion> bookSuggestionList) {
        bookSuggestionList.forEach(bs -> bs.setBookCategoryName(bs.getBookCategory().getName()));

        return bookSuggestionList;
    }

    private Map<BookCategory, List<BookSuggestion>> groupByCategoryId(List<BookSuggestion> bookSuggestionList) {
        return bookSuggestionList.stream().collect(Collectors.groupingBy(BookSuggestion::getBookCategory));
    }
}

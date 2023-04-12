package com.flab.oasis.service;

import com.flab.oasis.constant.BookCategory;
import com.flab.oasis.mapper.UserCategoryMapper;
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
        return setBookCategoryName(
                getBookSuggestionListByUserCategory(
                        bookSuggestionRequest.getUid(),
                        bookSuggestionRepository.getBookSuggestionListBySuggestionType(
                                bookSuggestionRequest.getSuggestionType()

                        )
                )
        );
    }

    private List<BookSuggestion> setBookCategoryName(List<BookSuggestion> bookSuggestionList) {
        bookSuggestionList.forEach(bs -> bs.setBookCategoryName(bs.getBookCategory().getName()));

        return bookSuggestionList;
    }

    private List<BookSuggestion> getBookSuggestionListByUserCategory(
            String uid, List<BookSuggestion> bookSuggestionList) {
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

    private Map<BookCategory, List<BookSuggestion>> groupByCategoryId(List<BookSuggestion> bookSuggestionList) {
        return bookSuggestionList.stream().collect(Collectors.groupingBy(BookSuggestion::getBookCategory));
    }
}

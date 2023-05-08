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
        List<BookSuggestion> bookSuggestionList = filterBookSuggestionListByUserCategoryList(
                bookSuggestionRepository.getBookSuggestionListBySuggestionType(
                        bookSuggestionRequest.getSuggestionType()
                ),
                userCategoryMapper.findUserCategoryByUid(
                        bookSuggestionRequest.getUid()
                )
        );
        
        bookSuggestionList.forEach(bs -> bs.setBookCategoryName(bs.getBookCategory().getName()));

        return bookSuggestionList;
    }

    private List<BookSuggestion> filterBookSuggestionListByUserCategoryList(
            List<BookSuggestion> bookSuggestionList, List<UserCategory> userCategoryList) {
        if (userCategoryList.isEmpty()) {
            return bookSuggestionList;
        } else {
            Map<BookCategory, List<BookSuggestion>> bookListByCategoryId = groupByCategoryId(bookSuggestionList);
            List<BookSuggestion> bookList = new ArrayList<>();
            userCategoryList.forEach(uci -> bookList.addAll(bookListByCategoryId.get(uci.getBookCategory())));

            return bookList;
        }
    }

    private Map<BookCategory, List<BookSuggestion>> groupByCategoryId(List<BookSuggestion> bookSuggestionList) {
        return bookSuggestionList.stream().collect(Collectors.groupingBy(BookSuggestion::getBookCategory));
    }
}

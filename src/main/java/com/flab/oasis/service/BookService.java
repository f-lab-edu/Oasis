package com.flab.oasis.service;

import com.flab.oasis.mapper.book.BookMapper;
import com.flab.oasis.model.Book;
import com.flab.oasis.model.BookSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    BookMapper bookMapper;

    @Cacheable(cacheNames = "bookCache", keyGenerator = "oasisKeyGenerator", cacheManager = "ehCacheCacheManager")
    public List<Book> findBookListByBookSearchRequest(BookSearchRequest bookSearchRequest) {
        return bookMapper.findBookListByKeyword(
                parseKeywordToFullTextBooleanModeSearchFormat(
                        bookSearchRequest.getKeyword().trim()
                )
        );
    }

    private static String parseKeywordToFullTextBooleanModeSearchFormat(String keyword) {
        String[] keywords = keyword.split(" ");
        for(int i = 0; i < keywords.length; i++) {
            keywords[i] = String.format("+%s*", keywords[i]);
        }

        return String.join(" ", keywords);
    }
}

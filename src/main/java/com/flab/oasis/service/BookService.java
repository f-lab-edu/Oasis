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
    private final BookMapper bookMapper;

    @Cacheable(cacheNames = "bookCache", keyGenerator = "oasisKeyGenerator", cacheManager = "ehCacheCacheManager")
    public List<Book> findBookListByKeyword(BookSearchRequest bookSearchRequest) {
        List<Book> bookList = bookMapper.findBookListByBookSearchRequest(bookSearchRequest);
        bookList.forEach(b -> b.setBookCategoryName(b.getBookCategory().getName()));

        return bookList;
    }
}

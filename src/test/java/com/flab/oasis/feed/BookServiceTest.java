package com.flab.oasis.feed;

import com.flab.oasis.mapper.book.BookMapper;
import com.flab.oasis.model.Book;
import com.flab.oasis.model.BookSearchRequest;
import com.flab.oasis.service.BookService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @InjectMocks
    BookService bookService;

    @Mock
    BookMapper bookMapper;

    @Test
    void bookSearchTest() {
        BookSearchRequest bookSearchRequest = new BookSearchRequest();
        bookSearchRequest.setKeyword("");

        List<Book> bookList = new ArrayList<>();
        bookList.add(new Book());

        BDDMockito.given(bookMapper.findBookListByKeyword(ArgumentMatchers.any(String.class)))
                .willReturn(bookList);

        List<Book> result = bookService.findBookListByBookSearchRequest(bookSearchRequest);

        Assertions.assertEquals(bookList.get(0).getTitle(), result.get(0).getTitle());
    }
}
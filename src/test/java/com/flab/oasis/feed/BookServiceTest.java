package com.flab.oasis.feed;

import com.flab.oasis.constant.BookCategory;
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
        Book book = new Book();
        List<Book> bookList = new ArrayList<>();

        book.setBookCategory(BookCategory.BC105);
        bookList.add(book);

        BDDMockito.given(bookMapper.findBookListByBookSearchRequest(ArgumentMatchers.any(BookSearchRequest.class)))
                .willReturn(bookList);

        List<Book> result = bookService.findBookListByKeyword(new BookSearchRequest(""));

        Assertions.assertEquals(bookList.get(0).getBookCategory(), result.get(0).getBookCategory());
    }
}
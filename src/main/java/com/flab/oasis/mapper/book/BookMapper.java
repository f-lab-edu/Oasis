package com.flab.oasis.mapper.book;

import com.flab.oasis.model.Book;
import com.flab.oasis.model.BookSearchRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BookMapper {
    public List<Book> findBookListByBookSearchRequest(BookSearchRequest bookSearchRequest);
}

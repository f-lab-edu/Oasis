package com.flab.oasis.mapper;

import com.flab.oasis.model.BookSuggestion;
import com.flab.oasis.model.UserCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HomeMapper {
    public List<UserCategory> findUserCategoryByUid(String uid);
    public List<BookSuggestion> getBookSuggestion();
}

package com.flab.oasis.mapper.home;

import com.flab.oasis.model.home.BookSuggestion;
import com.flab.oasis.model.home.UserCategory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface HomeMapper {
    public List<UserCategory> findUserCategoryByUid(String uid);
    public List<BookSuggestion> getBookSuggestion();
}

package com.flab.oasis.mapper.home;

import com.flab.oasis.model.home.BookSuggestionDTO;
import com.flab.oasis.model.home.UserCategoryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface HomeMapper {
    public List<UserCategoryDTO> findUserCategoryByUid(String uid);
    public List<BookSuggestionDTO> getBookSuggestion();
}

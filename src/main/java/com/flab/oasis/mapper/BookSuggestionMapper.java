package com.flab.oasis.mapper;

import com.flab.oasis.constant.SuggestionType;
import com.flab.oasis.model.BookSuggestion;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BookSuggestionMapper {
    public List<BookSuggestion> findBookSuggestionBySuggestionType(SuggestionType suggestionType);
}

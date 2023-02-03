package com.flab.oasis.model.home;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flab.oasis.constant.SuggestionType;
import com.flab.oasis.model.Book;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BookSuggestion extends Book {
    @JsonIgnore
    private SuggestionType suggestionType;

    public BookSuggestion(String suggestionType) {
        this.suggestionType = SuggestionType.valueOf(suggestionType);
    }
}

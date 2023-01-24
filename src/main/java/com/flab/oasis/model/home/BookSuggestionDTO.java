package com.flab.oasis.model.home;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flab.oasis.model.Book;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class BookSuggestionDTO extends Book {
    @JsonIgnore
    private String suggestionType;
}

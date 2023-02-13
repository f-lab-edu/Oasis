package com.flab.oasis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flab.oasis.constant.SuggestionType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BookSuggestion extends Book {
    @JsonIgnore
    private SuggestionType suggestionType;
}

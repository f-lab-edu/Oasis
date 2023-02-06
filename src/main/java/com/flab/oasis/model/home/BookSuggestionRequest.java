package com.flab.oasis.model.home;

import com.flab.oasis.constant.SuggestionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSuggestionRequest {
    private String uid;
    private SuggestionType suggestionType;
}

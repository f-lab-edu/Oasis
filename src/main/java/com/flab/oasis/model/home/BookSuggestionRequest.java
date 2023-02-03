package com.flab.oasis.model.home;

import com.flab.oasis.constant.SuggestionType;
import lombok.Getter;
import lombok.Setter;

@Getter
public class BookSuggestionRequest {
    private String uid;
    private SuggestionType suggestionType;

    public BookSuggestionRequest(String uid, String suggestionType) {
        this.uid = uid;
        this.suggestionType = SuggestionType.valueOf(suggestionType);
    }
}

package com.flab.oasis.constant;

import lombok.Getter;

@Getter
public enum SuggestionType {
    RECOMMEND("recommend"),
    NEWBOOK("newBook"),
    BESTSELLER("bestSeller");

    private final String suggestionType;

    SuggestionType(String suggestionType) {
        this.suggestionType = suggestionType;
    }
}

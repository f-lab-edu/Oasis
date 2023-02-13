package com.flab.oasis.model;

import com.flab.oasis.constant.SuggestionType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BookSuggestionRequest implements Serializable {
    private String uid;
    private SuggestionType suggestionType;
}

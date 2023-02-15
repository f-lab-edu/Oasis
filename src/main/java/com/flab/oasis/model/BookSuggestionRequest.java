package com.flab.oasis.model;

import com.flab.oasis.constant.SuggestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookSuggestionRequest implements BaseRequest, Serializable {
    private String uid;
    private SuggestionType suggestionType;

    @Override
    public String combineKeyWithUnderBar() {
        return String.format("%s_%s", uid, suggestionType.name());
    }
}

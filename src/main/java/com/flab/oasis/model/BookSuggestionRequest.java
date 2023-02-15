package com.flab.oasis.model;

import com.flab.oasis.constant.SuggestionType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class BookSuggestionRequest implements BaseRequest, Serializable {
    private static final long serialVersionUID = -755154959612591958L;
    private String uid;
    private SuggestionType suggestionType;

    @Override
    public String combineKeyWithUnderBar() {
        return String.format("%s_%s", uid, suggestionType);
    }
}

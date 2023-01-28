package com.flab.oasis.home;

import com.flab.oasis.constant.SuggestionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SuggestionTypeTest {
    @Test
    void suggestionTypeTest() {
        String type = "bestSeller";
        SuggestionType suggestionType = SuggestionType.valueOf(type.toUpperCase());
        Assertions.assertEquals(type, suggestionType.getSuggestionType());
    }
}

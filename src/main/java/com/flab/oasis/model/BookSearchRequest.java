package com.flab.oasis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class BookSearchRequest implements Serializable, BaseRequest {
    private static final long serialVersionUID = -7352675876589140643L;

    private String keyword;
    private boolean naturalMode;

    public BookSearchRequest(String keyword) {
        this.keyword = keyword.trim();
        this.naturalMode = this.keyword.length() >= 2;
    }

    @Override
    public String generateEhCacheKey() {
        return keyword;
    }
}

package com.flab.oasis.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BookSearchRequest implements BaseRequest, Serializable {
    private static final long serialVersionUID = 3843061827562810923L;

    private String keyword;

    @Override
    public String generateEhCacheKey() {
        return keyword.trim();
    }
}

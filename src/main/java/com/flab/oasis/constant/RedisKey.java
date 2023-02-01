package com.flab.oasis.constant;

import com.flab.oasis.model.redis.BookSuggestionJson;

import java.util.function.Function;

public enum RedisKey {
    HOME(BookSuggestionJson::new),
    FEED(json -> new Object()),
    COMMENT(json -> new Object()),
    USER(json -> new Object());

    private final Function<String, Object> expression;

    RedisKey(Function<String, Object> expression) {
        this.expression = expression;
    }

    public Object jsonStringToModel(String json) {
        return expression.apply(json);
    }
}

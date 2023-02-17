package com.flab.oasis.config;

import com.flab.oasis.model.BaseRequest;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

public class OasisKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        return String.format("%s_%s", method.getName(), joinParams(params));
    }

    private String joinParams(Object... params) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object obj : params) {
            if (obj instanceof BaseRequest) {
                stringBuilder.append(((BaseRequest) obj).generateEhCacheKey());
            }
        }

        return stringBuilder.toString();
    }
}

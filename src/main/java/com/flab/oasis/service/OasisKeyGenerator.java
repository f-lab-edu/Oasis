package com.flab.oasis.service;

import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class OasisKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        return method.getName() + "_" + joinParams(params);
    }

    private String joinParams(Object... params) {
        List<String> stringList = new ArrayList<>();
        for (Object obj : params) {
            stringList.add(obj.toString());
        }

        return String.join("_", stringList);
    }
}

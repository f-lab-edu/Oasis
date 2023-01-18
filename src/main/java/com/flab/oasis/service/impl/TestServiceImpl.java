package com.flab.oasis.service.impl;

import com.flab.oasis.mapper.TestMapper;
import com.flab.oasis.model.Book;
import com.flab.oasis.model.TestModel;
import com.flab.oasis.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final TestMapper testMapper;

    public TestModel dbConnectionTest() {
        return testMapper.dbConnectionTest();
    }

    @Override
    @Cacheable(value = "bookList", key = "#bookId")
    public Book cacheTest() {
        return testMapper.cacheTest();
    }
}

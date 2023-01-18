package com.flab.oasis.service;

import com.flab.oasis.model.Book;
import com.flab.oasis.model.TestModel;

public interface TestService {
    public TestModel dbConnectionTest();
    public Book cacheTest();
}

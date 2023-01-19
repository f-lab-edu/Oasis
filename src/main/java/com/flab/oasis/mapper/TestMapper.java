package com.flab.oasis.mapper;


import com.flab.oasis.model.Book;
import com.flab.oasis.model.TestModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface TestMapper {
    public TestModel dbConnectionTest();
    public Book cacheTest();
}

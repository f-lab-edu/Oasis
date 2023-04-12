package com.flab.oasis.mapper.feed;

import com.flab.oasis.model.TestModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedMapper {
    public TestModel dbConnectionTest();
}

package com.flab.oasis.mapper.book;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TestMapper {
    @Select("SELECT 10 FROM DUAL")
    public int getTestData();
}

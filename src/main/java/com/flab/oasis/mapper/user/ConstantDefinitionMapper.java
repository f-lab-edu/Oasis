package com.flab.oasis.mapper.user;

import com.flab.oasis.constant.ConstantName;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConstantDefinitionMapper {
    public int getIntValueByConstantName(ConstantName constantName);
}

package com.flab.oasis.mapper.user;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoMapper {
    public boolean isExistsNickname(String nickname);
}

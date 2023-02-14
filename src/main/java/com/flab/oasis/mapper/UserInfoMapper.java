package com.flab.oasis.mapper;

import com.flab.oasis.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoMapper {
    public void insertUserInfo(UserInfo userInfo);
    public void deleteUserInfoByUid(String uid);
}

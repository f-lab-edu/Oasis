package com.flab.oasis.mapper.user;

import com.flab.oasis.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoMapper {
    public boolean isExistsNickname(String nickname);
    public void createUserInfo(UserInfo userInfo);
    public UserInfo getUserInfoByUid(String uid);
}

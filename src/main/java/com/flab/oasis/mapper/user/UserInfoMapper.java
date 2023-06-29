package com.flab.oasis.mapper.user;

import com.flab.oasis.model.UserFeedCount;
import com.flab.oasis.model.UserFeedCountSelect;
import com.flab.oasis.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserInfoMapper {
    public boolean isExistsNickname(String nickname);
    public void createUserInfo(UserInfo userInfo);
    public UserInfo getUserInfoByUid(String uid);
    public List<UserFeedCount> getUserFeedCountList(UserFeedCountSelect userFeedCountSelect);
}

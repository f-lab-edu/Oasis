package com.flab.oasis.mapper;

import com.flab.oasis.model.UserAuth;
import com.flab.oasis.model.UserCategory;
import com.flab.oasis.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    public void insertUserAuth(UserAuth userAuth);
    public void insertUserInfo(UserInfo userInfo);
    public void insertUserCategory(UserCategory userCategory);
    public List<UserCategory> findUserCategoryByUid(String uid);
}

package com.flab.oasis.mapper;

import com.flab.oasis.model.UserAuth;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAuthMapper {
    public void insertUserAuth(UserAuth userAuth);
    public void deleteUserAuthByUid(String uid);
}

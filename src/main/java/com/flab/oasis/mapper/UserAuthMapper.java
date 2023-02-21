package com.flab.oasis.mapper;

import com.flab.oasis.model.UserAuth;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAuthMapper {
    public UserAuth getUserAuthByUid(String uid);
    public boolean existUserAuthByUid(String uid);
    public boolean existUserAuthByUidAndRefreshToken(UserAuth userAuth);
    public void updateRefreshToken(UserAuth userAuth);
}

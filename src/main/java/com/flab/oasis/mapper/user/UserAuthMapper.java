package com.flab.oasis.mapper.user;

import com.flab.oasis.model.UserAuth;
import com.flab.oasis.model.UserSession;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAuthMapper {
    public UserAuth getUserAuthByUid(String uid);
    public UserSession getUserSessionByUid(String uid);
    public void updateRefreshToken(UserSession userSession);
}

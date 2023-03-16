package com.flab.oasis.mapper;

import com.flab.oasis.model.UserSession;
import com.flab.oasis.model.dao.UserAuth;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAuthMapper {
    public UserAuth getUserAuthByUid(String uid);
    public void updateRefreshToken(UserSession userSession);
}

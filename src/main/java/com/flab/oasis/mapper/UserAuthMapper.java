package com.flab.oasis.mapper;

import com.flab.oasis.model.UserSession;
import com.flab.oasis.model.dao.UserAuthDAO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAuthMapper {
    public UserAuthDAO getUserAuthDAOByUid(String uid);
    public void updateRefreshToken(UserSession userSession);
}

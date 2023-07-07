package com.flab.oasis.repository;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.mapper.user.UserInfoMapper;
import com.flab.oasis.model.UserInfo;
import com.flab.oasis.model.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserInfoRepository {
    private final UserInfoMapper userInfoMapper;

    public boolean isExistsNickname(String nickname) {
        return userInfoMapper.isExistsNickname(nickname);
    }

    public void createUserInfo(UserInfo userInfo) {
        userInfoMapper.createUserInfo(userInfo);
    }

    @Cacheable(cacheNames = "UserInfo", key = "#uid", cacheManager = "redisCacheManager")
    public UserInfo getUserInfoByUid(String uid) throws NotFoundException {
        return Optional.ofNullable(userInfoMapper.getUserInfoByUid(uid))
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.NOT_FOUND, "UserInfo does not created.", uid
                ));
    }

    public List<String> getDefaultRecommendUserList(String uid) {
        return userInfoMapper.getDefaultRecommendUserList(uid);
    }
}

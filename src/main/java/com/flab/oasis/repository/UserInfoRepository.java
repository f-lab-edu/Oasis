package com.flab.oasis.repository;

import com.flab.oasis.mapper.user.UserInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserInfoRepository {
    private final UserInfoMapper userInfoMapper;

    public boolean isExistsNickname(String nickname) {
        return userInfoMapper.isExistsNickname(nickname);
    }
}

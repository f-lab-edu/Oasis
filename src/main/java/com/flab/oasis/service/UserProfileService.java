package com.flab.oasis.service;

import com.flab.oasis.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserInfoRepository userInfoRepository;

    public boolean isExistsNickname(String nickname) {
        return userInfoRepository.isExistsNickname(nickname);
    }
}

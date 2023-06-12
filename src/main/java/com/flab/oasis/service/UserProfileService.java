package com.flab.oasis.service;

import com.flab.oasis.model.UserCategory;
import com.flab.oasis.model.UserInfo;
import com.flab.oasis.model.UserProfile;
import com.flab.oasis.repository.UserCategoryRepository;
import com.flab.oasis.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserInfoRepository userInfoRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final UserAuthService userAuthService;

    public boolean isExistsNickname(String nickname) {
        return userInfoRepository.isExistsNickname(nickname);
    }

    @Transactional
    public void createUserProfile(UserProfile userProfile) {
        userInfoRepository.createUserInfo(
                UserInfo.builder()
                        .uid(userAuthService.getAuthenticatedUid())
                        .nickname(userProfile.getNickname())
                        .introduce(userProfile.getIntroduce())
                        .build()
        );

        userCategoryRepository.createUserCategory(
                userProfile.getBookCategoryList().stream()
                        .map(
                                bookCategory -> UserCategory.builder()
                                        .uid(userProfile.getUid())
                                        .bookCategory(bookCategory)
                                        .build()
                        )
                        .collect(Collectors.toList())
        );
    }

    public UserProfile getUserProfileByUid() {
        String uid = userAuthService.getAuthenticatedUid();
        UserInfo userInfo = userInfoRepository.getUserInfoByUid(uid);
        List<UserCategory> userCategoryList = userCategoryRepository.getUserCategoryByUid(uid);

        return UserProfile.builder()
                .uid(uid)
                .nickname(userInfo.getNickname())
                .introduce(userInfo.getIntroduce())
                .bookCategoryList(
                        userCategoryList.stream()
                                .map(UserCategory::getBookCategory)
                                .collect(Collectors.toList())
                )
                .build();
    }
}

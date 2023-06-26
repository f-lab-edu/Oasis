package com.flab.oasis.service;

import com.flab.oasis.constant.BookCategory;
import com.flab.oasis.constant.ResponseCode;
import com.flab.oasis.model.UserCategory;
import com.flab.oasis.model.UserInfo;
import com.flab.oasis.model.UserProfile;
import com.flab.oasis.model.exception.NotFoundException;
import com.flab.oasis.model.response.UserProfileResponse;
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
        String uid = userAuthService.getAuthenticatedUid();

        userInfoRepository.createUserInfo(
                UserInfo.builder()
                        .uid(uid)
                        .nickname(userProfile.getNickname())
                        .introduce(userProfile.getIntroduce())
                        .build()
        );

        if (!userProfile.getBookCategoryList().isEmpty()) {
            userCategoryRepository.createUserCategory(
                    userProfile.getBookCategoryList().stream()
                            .map(
                                    bookCategory -> UserCategory.builder()
                                            .uid(uid)
                                            .bookCategory(bookCategory)
                                            .build()
                            )
                            .collect(Collectors.toList())
            );
        }
    }

    public UserProfileResponse getUserProfileByUid() throws NotFoundException {
        String uid = userAuthService.getAuthenticatedUid();

        try{
            UserInfo userInfo = userInfoRepository.getUserInfoByUid(uid);
            List<UserCategory> userCategoryList = userCategoryRepository.getUserCategoryListByUid(uid);
            List<BookCategory> bookCategoryList = userCategoryList.stream()
                    .map(UserCategory::getBookCategory)
                    .collect(Collectors.toList());

            UserProfile userProfile = UserProfile.builder()
                    .uid(uid)
                    .nickname(userInfo.getNickname())
                    .introduce(userInfo.getIntroduce())
                    .bookCategoryList(bookCategoryList)
                    .build();

            return UserProfileResponse.builder()
                    .code(ResponseCode.OK.getCode())
                    .userProfile(userProfile)
                    .build();
        } catch (NotFoundException e) {
            return UserProfileResponse.builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        }


    }
}

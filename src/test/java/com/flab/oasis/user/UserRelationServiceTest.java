package com.flab.oasis.user;

import com.flab.oasis.constant.BookCategory;
import com.flab.oasis.model.*;
import com.flab.oasis.repository.UserCategoryRepository;
import com.flab.oasis.repository.UserInfoRepository;
import com.flab.oasis.repository.UserRelationRepository;
import com.flab.oasis.service.UserAuthService;
import com.flab.oasis.service.UserRelationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserRelationServiceTest {
    @InjectMocks
    UserRelationService userRelationService;

    @Mock
    UserAuthService userAuthService;

    @Mock
    UserCategoryRepository userCategoryRepository;

    @Mock
    UserRelationRepository userRelationRepository;

    @Mock
    UserInfoRepository userInfoRepository;

    @DisplayName("User Category가 존재하지 않을 경우")
    @Test
    void testNotExistUserCategory() {
        String uid = "test";
        String expectedUid = "uid";

        // User Category가 Empty일 경우
        BDDMockito.given(userCategoryRepository.getUserCategoryListByUid(uid))
                .willReturn(new ArrayList<>());

        // 본인을 제외하고, 피드 작성량이 많은 순서대로 추천 User를 최대 30명을 가져온다.
        BDDMockito.given(userAuthService.getAuthenticatedUid())
                .willReturn(uid);
        BDDMockito.given(userInfoRepository.getUserFeedCountList(BDDMockito.any(UserFeedCountSelect.class)))
                .willReturn(Collections.singletonList(
                        UserFeedCount.builder()
                                .uid(expectedUid)
                                .feedCount(0)
                                .build()
                ));

        List<String> uidList = userRelationService.getRecommendUserList(uid);

        Assertions.assertEquals(expectedUid, uidList.get(0));
    }

    @DisplayName("겹치는 User Category가 존재하지 않을 경우")
    @Test
    void testNotExistOverlappingUserCategory() {
        String uid = "test";
        String expectedUid = "uid";

        BDDMockito.given(userCategoryRepository.getUserCategoryListByUid(uid))
                .willReturn(Collections.singletonList(
                        UserCategory.builder()
                                .uid(uid)
                                .bookCategory(BookCategory.BC101)
                                .build()
                ));
        BDDMockito.given(userRelationRepository.getUserRelationListByUid(uid))
                .willReturn(new ArrayList<>());

        // 겹치는 카테고리가 존재하는 유저가 없을 경우
        BDDMockito.given(userCategoryRepository.getUidListWithOverlappingBookCategory(uid))
                .willReturn(new ArrayList<>());

        // 본인을 제외하고, 피드 작성량이 많은 순서대로 추천 User를 최대 30명을 가져온다.
        BDDMockito.given(userAuthService.getAuthenticatedUid())
                .willReturn(uid);
        BDDMockito.given(userInfoRepository.getUserFeedCountList(BDDMockito.any(UserFeedCountSelect.class)))
                .willReturn(Collections.singletonList(
                        UserFeedCount.builder()
                                .uid(expectedUid)
                                .feedCount(0)
                                .build()
                ));

        List<String> uidList = userRelationService.getRecommendUserList(uid);

        Assertions.assertEquals(expectedUid, uidList.get(0));
    }

    @DisplayName("생성된 추천 유저가 30명이 안될 경우")
    @Test
    void testRecommendUserSizeLessThen30() {
        String uid = "test";
        String expectedUid1 = "uid";
        String expectedUid2 = "add";

        BDDMockito.given(userCategoryRepository.getUserCategoryListByUid(uid))
                .willReturn(Collections.singletonList(
                        UserCategory.builder()
                                .uid(uid)
                                .bookCategory(BookCategory.BC101)
                                .build()
                ));
        BDDMockito.given(userRelationRepository.getUserRelationListByUid(uid))
                .willReturn(new ArrayList<>());

        // 카테고리가 겹치는 유저가 존재하나 30명이 안될 경우
        BDDMockito.given(userCategoryRepository.getUidListWithOverlappingBookCategory(uid))
                .willReturn(Collections.singletonList(expectedUid1));

        // 생성된 추천 유저들을 겹치는 카테고리를 제외한 카테고리의 개수가 많고, 피드 작성량이 많은 순으로 정렬한다.
        // 본인과 생성된 추천 유저들을 제외하고, 피드 작성량이 많은 순서대로 추천 User를 최대 30명을 가져온다.
        BDDMockito.given(userAuthService.getAuthenticatedUid())
                .willReturn(uid);
        BDDMockito.given(userCategoryRepository.getUserCategoryCountList(BDDMockito.any(UserCategoryCountSelect.class)))
                        .willReturn(Collections.singletonList(
                                UserCategoryCount.builder()
                                        .uid(expectedUid1)
                                        .bookCategoryCount(0)
                                        .build()
                        ));
        BDDMockito.given(userInfoRepository.getUserFeedCountList(BDDMockito.any(UserFeedCountSelect.class)))
                .willReturn(Collections.singletonList(
                        UserFeedCount.builder()
                                .uid(expectedUid1)
                                .feedCount(0)
                                .build()
                )).willReturn(Collections.singletonList(
                        UserFeedCount.builder()
                                .uid(expectedUid2)
                                .feedCount(0)
                                .build()
                ));

        List<String> uidList = userRelationService.getRecommendUserList(uid);

        // 겹치는 카테고리가 존재하는 유저들이 순서상 우선 순위에 있어야 하고, 부족한만큼 채운 추천 유저는 후 순위에 있어야 한다.
        Assertions.assertAll(
                () -> assertEquals(expectedUid1, uidList.get(0)),
                () -> assertEquals(expectedUid2, uidList.get(1))
        );
    }
}
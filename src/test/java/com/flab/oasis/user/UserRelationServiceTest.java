package com.flab.oasis.user;

import com.flab.oasis.constant.BookCategory;
import com.flab.oasis.mapper.user.FeedMapper;
import com.flab.oasis.model.Feed;
import com.flab.oasis.model.UserCategory;
import com.flab.oasis.repository.UserCategoryRepository;
import com.flab.oasis.repository.UserInfoRepository;
import com.flab.oasis.repository.UserRelationRepository;
import com.flab.oasis.service.UserAuthService;
import com.flab.oasis.service.UserRelationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
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

    @Mock
    FeedMapper feedMapper;

    @DisplayName("카테고리가 겹치는 유저가 존재하지 않을 경우")
    @Test
    void testOverlappingCategoryIsNotExists() {
        String uid = "test";
        String expectedUid = "uid";

        // relation이 존재하는 유저 목록 가져오기
        BDDMockito.given(userRelationRepository.getUserRelationListByUid(uid))
                .willReturn(new ArrayList<>());

        // 카테고리가 겹치는 유저 목록 가져오기
        BDDMockito.given(userCategoryRepository.getUidListWithOverlappingBookCategory(uid))
                .willReturn(new ArrayList<>());

        // 본인, relation이 존재하는 유저들을 제외하고, 기본 추천 유저 최대 30명 가져오기
        BDDMockito.given(userInfoRepository.getDefaultRecommendUserExcludeUidList(ArgumentMatchers.anyList()))
                .willReturn(Collections.singletonList(expectedUid));

        List<String> uidList = userRelationService.getRecommendUserList(uid);

        Assertions.assertEquals(expectedUid, uidList.get(0));
    }

    @DisplayName("생성된 카테고리 추천 유저가 30명이 안될 경우")
    @Test
    void testRecommendUserSizeLessThen30() {
        String uid = "test";
        String expectedUid1 = "uid";
        String expectedUid2 = "add";

        // relation이 존재하는 유저 목록 가져오기
        BDDMockito.given(userRelationRepository.getUserRelationListByUid(uid))
                .willReturn(new ArrayList<>());

        // 카테고리가 겹치는 유저 목록 가져오기
        BDDMockito.given(userCategoryRepository.getUidListWithOverlappingBookCategory(uid))
                .willReturn(Collections.singletonList(expectedUid1));

        // 겹치는 카테고리를 제외하기 위한 "uid"의 카테고리 가져오기
        BDDMockito.given(userCategoryRepository.getUserCategoryListByUid(uid))
                .willReturn(Collections.singletonList(
                        UserCategory.builder()
                                .uid(uid)
                                .bookCategory(BookCategory.BC105)
                                .build()
                ));

        // 카테고리가 겹치는 유저들의 카테고리 목록 가져오기
        BDDMockito.given(userCategoryRepository.getUserCategoryListByUidList(ArgumentMatchers.anyList()))
                .willReturn(Collections.singletonList(
                        UserCategory.builder()
                                .uid(expectedUid1)
                                .bookCategory(BookCategory.BC101)
                                .build()
                ));

        // 카테고리가 겹치는 유저들의 피드 목록 가져오기
        BDDMockito.given(feedMapper.getFeedListByUidList(ArgumentMatchers.anyList()))
                        .willReturn(Collections.singletonList(
                                Feed.builder()
                                        .uid(expectedUid1)
                                        .feedId(0)
                                        .build()
                        ));

        // 본인, 카테고리 추천 유저들, relation이 존재하는 유저들을 제외하고, 기본 추천 유저 최대 30명 가져오기
        BDDMockito.given(userAuthService.getAuthenticatedUid())
                .willReturn(uid);
        BDDMockito.given(userInfoRepository.getDefaultRecommendUserExcludeUidList(ArgumentMatchers.anyList()))
                .willReturn(Collections.singletonList(expectedUid2));

        List<String> uidList = userRelationService.getRecommendUserList(uid);

        // 겹치는 카테고리가 존재하는 유저들이 순서상 우선 순위에 있어야 하고, 부족한만큼 채운 추천 유저는 후 순위에 있어야 한다.
        Assertions.assertAll(
                () -> assertEquals(expectedUid1, uidList.get(0)),
                () -> assertEquals(expectedUid2, uidList.get(1))
        );
    }
}
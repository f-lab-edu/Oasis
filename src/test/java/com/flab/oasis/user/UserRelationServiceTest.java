package com.flab.oasis.user;

import com.flab.oasis.constant.BookCategory;
import com.flab.oasis.mapper.user.FeedMapper;
import com.flab.oasis.model.Feed;
import com.flab.oasis.model.RecommendUserRequest;
import com.flab.oasis.model.UserCategory;
import com.flab.oasis.repository.UserCategoryRepository;
import com.flab.oasis.repository.UserInfoRepository;
import com.flab.oasis.repository.UserRelationRepository;
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

        BDDMockito.given(userCategoryRepository.getUserCategoryListByUid(uid))
                .willReturn(new ArrayList<>());

        BDDMockito.given(
                userCategoryRepository.getUidListByBookCategory(BookCategory.BC101)
        ).willReturn(new ArrayList<>());

        // excludeUidList에 해당하는 유저를 제외하고, 기본 추천 유저 최대 30명 가져오기
        BDDMockito.given(userInfoRepository.getDefaultRecommendUserList(uid))
                .willReturn(Collections.singletonList(expectedUid));

        List<String> uidList = userRelationService.getRecommendUserList(
                RecommendUserRequest.builder()
                        .uid(uid)
                        .checkSize(1)
                        .build()
        );

        Assertions.assertEquals(expectedUid, uidList.get(0));
    }

    @DisplayName("생성된 카테고리 추천 유저가 check size보다 적을 경우")
    @Test
    void testRecommendUserSizeLessThenCheckSize() {
        String uid = "test";
        String expectedUid1 = "uid";
        String expectedUid2 = "add";

        // relation이 존재하는 유저 목록 가져오기
        BDDMockito.given(userRelationRepository.getUserRelationListByUid(uid))
                .willReturn(new ArrayList<>());

        BDDMockito.given(userCategoryRepository.getUserCategoryListByUid(uid))
                .willReturn(Collections.singletonList(
                        UserCategory.builder()
                                .uid(uid)
                                .bookCategory(BookCategory.BC101)
                                .build()
                ));

        // 카테고리별 uid 목록 가져오기
        for (BookCategory bookCategory : BookCategory.values()) {
            BDDMockito.given(
                    userCategoryRepository.getUidListByBookCategory(bookCategory)
            ).willReturn(Collections.singletonList(expectedUid1));
        }

        // 카테고리가 겹치는 유저들의 피드 목록 가져오기
        BDDMockito.given(feedMapper.getFeedListByUidList(ArgumentMatchers.anyList()))
                .willReturn(Collections.singletonList(
                        Feed.builder()
                                .uid(expectedUid1)
                                .feedId(0)
                                .build()
                ));

        // excludeUidList에 해당하는 유저를 제외하고, 기본 추천 유저 최대 30명 가져오기
        BDDMockito.given(userInfoRepository.getDefaultRecommendUserList(uid))
                .willReturn(Collections.singletonList(expectedUid2));

        // 카테고리 추천 유저를 생성 후, 크기가 30명 미만이면 기본 추천 유저 생성 로직 실행
        List<String> uidList = userRelationService.getRecommendUserList(
                RecommendUserRequest.builder()
                        .uid(uid)
                        .checkSize(2)
                        .build()
        );

        // 겹치는 카테고리가 존재하는 유저들이 순서상 우선 순위에 있어야 하고, 부족한만큼 채운 추천 유저는 후 순위에 있어야 한다.
        Assertions.assertAll(
                () -> assertEquals(expectedUid1, uidList.get(0)),
                () -> assertEquals(expectedUid2, uidList.get(1))
        );
    }

    @DisplayName("생성된 카테고리 추천 유저가 check size를 충족할 경우")
    @Test
    void testRecommendUserSizeIsCheckSize() {
        String uid = "test";
        String expectedUid1 = "uid";
        String expectedUid2 = "add";

        // relation이 존재하는 유저 목록 가져오기
        BDDMockito.given(userRelationRepository.getUserRelationListByUid(uid))
                .willReturn(new ArrayList<>());

        BDDMockito.given(userCategoryRepository.getUserCategoryListByUid(uid))
                .willReturn(Collections.singletonList(
                        UserCategory.builder()
                                .uid(uid)
                                .bookCategory(BookCategory.BC101)
                                .build()
                ));

        // 카테고리별 uid 목록 가져오기
        for (BookCategory bookCategory : BookCategory.values()) {
            BDDMockito.given(
                    userCategoryRepository.getUidListByBookCategory(bookCategory)
            ).willReturn(Collections.singletonList(expectedUid1));
        }

        // 카테고리가 겹치는 유저들의 피드 목록 가져오기
        BDDMockito.given(feedMapper.getFeedListByUidList(ArgumentMatchers.anyList()))
                .willReturn(Collections.singletonList(
                        Feed.builder()
                                .uid(expectedUid1)
                                .feedId(0)
                                .build()
                ));

        // 카테고리 추천 유저를 생성 후, 크기가 30명 미만이면 기본 추천 유저 생성 로직 실행
        List<String> uidList = userRelationService.getRecommendUserList(
                RecommendUserRequest.builder()
                        .uid(uid)
                        .checkSize(1)
                        .build()
        );

        // 겹치는 카테고리가 존재하는 유저들이 순서상 우선 순위에 있어야 하고, 부족한만큼 채운 추천 유저는 후 순위에 있어야 한다.
        Assertions.assertEquals(expectedUid1, uidList.get(0));
    }
}
package com.flab.oasis.user;

import com.flab.oasis.constant.BookCategory;
import com.flab.oasis.mapper.user.FeedMapper;
import com.flab.oasis.model.UserCategory;
import com.flab.oasis.model.UserRelation;
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

    @DisplayName("설정한 카테고리가 존재하지 않을 경우")
    @Test
    void testUserCategoryIsEmpty() {
        String uid = "test";
        String expectedUid = "uid";

        // relation이 존재하는 유저 목록 가져오기
        BDDMockito.given(userRelationRepository.getUserRelationListByUid(uid))
                .willReturn(new ArrayList<>());

        // 추천받을 유저의 category 목록 가져오기
        BDDMockito.given(userCategoryRepository.getUserCategoryListByUid(uid))
                .willReturn(new ArrayList<>());

        // excludeUidList에 해당하는 유저를 제외하고, 기본 추천 유저 가져오기
        BDDMockito.given(userInfoRepository.getDefaultRecommendUserList())
                .willReturn(Collections.singletonList(expectedUid));

        // 기본 추천 유저의 피드 목록 가져오기
        BDDMockito.given(feedMapper.getFeedListByUid(expectedUid))
                .willReturn(new ArrayList<>());

        List<String> uidList = userRelationService.getRecommendUserListByUidAndCheckSize(uid, 1);

        Assertions.assertEquals(expectedUid, uidList.get(0));
    }

    @DisplayName("카테고리가 겹치는 유저가 존재하지 않을 경우")
    @Test
    void testOverlappingCategoryIsNotExists() {
        String uid = "test";
        String expectedUid = "uid";

        // relation이 존재하는 유저 목록 가져오기
        BDDMockito.given(userRelationRepository.getUserRelationListByUid(uid))
                .willReturn(new ArrayList<>());

        // 추천받을 유저의 category 목록 가져오기
        BDDMockito.given(userCategoryRepository.getUserCategoryListByUid(uid))
                .willReturn(Collections.singletonList(
                        UserCategory.builder()
                                .uid(uid)
                                .bookCategory(BookCategory.BC101)
                                .build()
                ));

        // 카테고리별 uid 목록 가져오기
        BDDMockito.given(
                userCategoryRepository.getUidListByBookCategory(ArgumentMatchers.any(BookCategory.class))
        ).willReturn(new ArrayList<>());

        // excludeUidList에 해당하는 유저를 제외하고, 기본 추천 유저 가져오기
        BDDMockito.given(userInfoRepository.getDefaultRecommendUserList())
                .willReturn(Collections.singletonList(expectedUid));

        // 기본 추천 유저의 피드 목록 가져오기
        BDDMockito.given(feedMapper.getFeedListByUid(expectedUid))
                .willReturn(new ArrayList<>());

        List<String> uidList = userRelationService.getRecommendUserListByUidAndCheckSize(uid, 1);

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

        // 추천받을 유저의 category 목록 가져오기
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

        // excludeUidList에 해당하는 유저를 제외하고, 기본 추천 유저 가져오기
        BDDMockito.given(userInfoRepository.getDefaultRecommendUserList())
                .willReturn(Collections.singletonList(expectedUid2));

        // 카테고리/기본 추천 유저의 피드 목록 가져오기
        BDDMockito.given(feedMapper.getFeedListByUid(ArgumentMatchers.any()))
                .willReturn(new ArrayList<>());

        List<String> uidList = userRelationService.getRecommendUserListByUidAndCheckSize(uid, 2);

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
        String expectedUid = "uid";

        // relation이 존재하는 유저 목록 가져오기
        BDDMockito.given(userRelationRepository.getUserRelationListByUid(uid))
                .willReturn(new ArrayList<>());

        // 추천받을 유저의 category 목록 가져오기
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
            ).willReturn(Collections.singletonList(expectedUid));
        }

        // 카테고리 추천 유저의 피드 목록 가져오기
        BDDMockito.given(feedMapper.getFeedListByUid(expectedUid))
                .willReturn(new ArrayList<>());

        List<String> uidList = userRelationService.getRecommendUserListByUidAndCheckSize(uid, 1);

        Assertions.assertEquals(expectedUid, uidList.get(0));
    }

    @DisplayName("생성된 추천 유저가 exclude uid에 해당할 경우")
    @Test
    void testRecommendUserIsExcludeUser() {
        String uid = "test";
        String excludeUid = "uid";

        // relation이 존재하는 유저 목록 가져오기
        BDDMockito.given(userRelationRepository.getUserRelationListByUid(uid))
                .willReturn(Collections.singletonList(
                        UserRelation.builder()
                                .uid(uid)
                                .relationUser(excludeUid)
                                .build()
                ));

        // 추천받을 유저의 category 목록 가져오기
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
            ).willReturn(Collections.singletonList(excludeUid));
        }

        // excludeUidList에 해당하는 유저를 제외하고, 기본 추천 유저 가져오기
        BDDMockito.given(userInfoRepository.getDefaultRecommendUserList())
                .willReturn(Collections.singletonList(excludeUid));

        List<String> uidList = userRelationService.getRecommendUserListByUidAndCheckSize(uid, 1);

        Assertions.assertTrue(uidList.isEmpty());
    }
}
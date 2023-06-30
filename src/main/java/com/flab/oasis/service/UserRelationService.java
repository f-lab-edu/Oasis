package com.flab.oasis.service;

import com.flab.oasis.model.*;
import com.flab.oasis.repository.UserCategoryRepository;
import com.flab.oasis.repository.UserInfoRepository;
import com.flab.oasis.repository.UserRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRelationService {
    private final UserAuthService userAuthService;
    private final UserCategoryRepository userCategoryRepository;
    private final UserRelationRepository userRelationRepository;
    private final UserInfoRepository userInfoRepository;

    @Cacheable(cacheNames = "recommendUserCache", key = "#uid", cacheManager = "ehCacheCacheManager")
    public List<String> getRecommendUserList(String uid) {
        List<UserCategory> userCategoryList = userCategoryRepository.getUserCategoryListByUid(uid);
        List<UserRelation> userRelationList = userRelationRepository.getUserRelationListByUid(uid);

        if (userCategoryList.isEmpty()) {
            return getRecommendUserAsManyAsNeed(
                    getUidListFromUserRelationList(userRelationList), 30
            );
        }

        List<String> overlappingCategoryUserList = userCategoryRepository.getUidListWithOverlappingBookCategory(
                OverlappingCategoryUserSelect.builder()
                        .uid(uid)
                        .userCategoryList(userCategoryList)
                        .userRelationList(userRelationList)
                        .build()
        );

        if (overlappingCategoryUserList.isEmpty()) {
            return getRecommendUserAsManyAsNeed(
                    getUidListFromUserRelationList(userRelationList), 30
            );
        }

        List<String> recommendUserList = makeRecommendUserList(userCategoryList, overlappingCategoryUserList);

        // 추천 유저가 30명이 안될 경우, 부족한 추천 user를 채운다.
        if (recommendUserList.size() < 30) {
            // exclude 대상으로 relation이 있는 user와 생성된 recommend user를 추가한다.
            List<String> excludeUidList = getUidListFromUserRelationList(userRelationList);
            excludeUidList.addAll(recommendUserList);

            recommendUserList.addAll(
                    getRecommendUserAsManyAsNeed(excludeUidList, 30 - recommendUserList.size())
            );
        }

        return recommendUserList;
    }

    public void createUserRelation(UserRelation userRelation) {
        userRelation.setUid(userAuthService.getAuthenticatedUid());

        userRelationRepository.createUserRelation(userRelation);
    }

    // '본인 | relation이 있는 user | 생성된 recommend user'를 제외하고 needSize만큼 recommend user를 가져온다.
    private List<String> getRecommendUserAsManyAsNeed(List<String> excludeUidList, int needSize) {
        excludeUidList.add(userAuthService.getAuthenticatedUid());

        return userInfoRepository.getUserFeedCountList(
                UserFeedCountSelect.builder()
                        .uidList(excludeUidList)
                        .needSize(needSize)
                        .build()
        )
                .stream()
                .map(UserFeedCount::getUid)
                .collect(Collectors.toList());
    }

    private List<String> getUidListFromUserRelationList(List<UserRelation> userRelationList) {
        return userRelationList.stream()
                .map(UserRelation::getRelationUser)
                .collect(Collectors.toList());
    }

    private List<String> makeRecommendUserList(
            List<UserCategory> userCategoryList, List<String> overlappingCategoryUserList) {
        Map<String, Integer> userCategoryCountMap = userCategoryRepository.getUserCategoryCountList(
                UserCategoryCountSelect.builder()
                        .overlappingCategoryUserList(overlappingCategoryUserList)
                        .userCategoryList(userCategoryList)
                        .build()
        )
                .stream()
                .collect(Collectors.toMap(UserCategoryCount::getUid, UserCategoryCount::getBookCategoryCount));

        List<UserFeedCount> userFeedCountList = userInfoRepository.getUserFeedCountList(
                UserFeedCountSelect.builder()
                        .uidList(overlappingCategoryUserList)
                        .needSize(-1)
                        .build()
        );

        List<RecommendUser> recommendUserList = userFeedCountList.stream()
                .map(userFeedCount -> RecommendUser.builder()
                        .uid(userFeedCount.getUid())
                        .feedCount(userFeedCount.getFeedCount())
                        .categoryCount(userCategoryCountMap.get(userFeedCount.getUid()))
                        .build()
                ).
                collect(Collectors.toList());

        return recommendUserList.stream()
                .map(RecommendUser::getUid)
                .collect(Collectors.toList());
    }
}

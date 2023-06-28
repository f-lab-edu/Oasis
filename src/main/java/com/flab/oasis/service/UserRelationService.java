package com.flab.oasis.service;

import com.flab.oasis.mapper.user.FeedMapper;
import com.flab.oasis.model.*;
import com.flab.oasis.repository.UserCategoryRepository;
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
    private final FeedMapper feedMapper;
    private final UserCategoryRepository userCategoryRepository;
    private final UserRelationRepository userRelationRepository;

    @Cacheable(cacheNames = "recommendUserCache", key = "#uid", cacheManager = "ehCacheCacheManager")
    public List<String> getRecommendUserList(String uid) {
        List<UserCategory> userCategoryList = userCategoryRepository.getUserCategoryListByUid(uid);
        List<UserRelation> userRelationList = userRelationRepository.getUserRelationListByUid(uid);

        if (userCategoryList.isEmpty()) {
            return getUidListFromRecommendUserList(
                    getRecommendUserAsManyAsNeed(
                            getUidListFromUserRelationList(userRelationList), 30
                    )
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
            return getUidListFromRecommendUserList(
                    getRecommendUserAsManyAsNeed(
                            getUidListFromUserRelationList(userRelationList), 30
                    )
            );
        }

        List<RecommendUser> recommendUserList = makeRecommendUserList(userCategoryList, overlappingCategoryUserList);

        // 추천 유저가 30명이 안될 경우, 부족한 추천 user를 채운다.
        if (recommendUserList.size() < 30) {
            List<String> uidList = getUidListFromUserRelationList(userRelationList);
            uidList.addAll(
                    getUidListFromRecommendUserList(recommendUserList)
            );

            recommendUserList.addAll(
                    getRecommendUserAsManyAsNeed(uidList, 30 - recommendUserList.size())
            );
        }

        return getUidListFromRecommendUserList(recommendUserList);
    }

    public void createUserRelation(UserRelation userRelation) {
        userRelation.setUid(userAuthService.getAuthenticatedUid());

        userRelationRepository.createUserRelation(userRelation);
    }

    // '본인 | relation이 있는 user | 생성된 recommend user'를 제외하고 needSize만큼 recommend user를 가져온다.
    private List<RecommendUser> getRecommendUserAsManyAsNeed(List<String> uidList, int needSize) {
        uidList.add(userAuthService.getAuthenticatedUid());

        List<FeedCount> feedCountList = feedMapper.getFeedCountList(
                FeedCountSelect.builder()
                        .uidList(uidList)
                        .needSize(needSize)
                        .build()
        );

        return feedCountList.stream()
                .map(feedCount -> RecommendUser.builder()
                                .uid(feedCount.getUid())
                                .feedCount(feedCount.getFeedCount())
                                .build()
                )
                .sorted()
                .collect(Collectors.toList());
    }

    private List<String> getUidListFromUserRelationList(List<UserRelation> userRelationList) {
        return userRelationList.stream()
                .map(UserRelation::getRelationUser)
                .collect(Collectors.toList());
    }

    private List<String> getUidListFromRecommendUserList(List<RecommendUser> recommendUserList) {
        return recommendUserList.stream()
                .map(RecommendUser::getUid)
                .collect(Collectors.toList());
    }

    private List<RecommendUser> makeRecommendUserList(
            List<UserCategory> userCategoryList, List<String> overlappingCategoryUserList) {
        Map<String, Integer> userCategoryCountMap = userCategoryRepository.getUserCategoryCountList(
                UserCategoryCountSelect.builder()
                        .uidList(overlappingCategoryUserList)
                        .userCategoryList(userCategoryList)
                        .build()
        )
                .stream()
                .collect(Collectors.toMap(UserCategoryCount::getUid, UserCategoryCount::getBookCategoryCount));

        List<FeedCount> feedCountList = feedMapper.getFeedCountList(
                FeedCountSelect.builder()
                        .uidList(overlappingCategoryUserList)
                        .needSize(-1)
                        .build()
        );

        return feedCountList.stream()
                .map(feedCount -> RecommendUser.builder()
                        .uid(feedCount.getUid())
                        .feedCount(feedCount.getFeedCount())
                        .categoryCount(userCategoryCountMap.get(feedCount.getUid()))
                        .build()
                ).
                collect(Collectors.toList());
    }
}

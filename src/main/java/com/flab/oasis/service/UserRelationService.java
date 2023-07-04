package com.flab.oasis.service;

import com.flab.oasis.constant.BookCategory;
import com.flab.oasis.mapper.user.FeedMapper;
import com.flab.oasis.model.*;
import com.flab.oasis.repository.UserCategoryRepository;
import com.flab.oasis.repository.UserInfoRepository;
import com.flab.oasis.repository.UserRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRelationService {
    private final UserAuthService userAuthService;
    private final UserCategoryRepository userCategoryRepository;
    private final UserRelationRepository userRelationRepository;
    private final UserInfoRepository userInfoRepository;
    private final FeedMapper feedMapper;

    @Cacheable(cacheNames = "recommendUserCache", key = "#uid", cacheManager = "ehCacheCacheManager")
    public List<String> getRecommendUserList(String uid) {
        List<String> relationUserList = userRelationRepository.getUserRelationListByUid(uid).stream()
                .map(UserRelation::getRelationUser)
                .collect(Collectors.toList());

        List<String> overlappingCategoryUserList = userCategoryRepository.getUidListWithOverlappingBookCategory(uid);

        Set<String> relationUserSet = new HashSet<>(relationUserList);
        overlappingCategoryUserList = overlappingCategoryUserList.stream()
                .filter(s -> !relationUserSet.contains(s))
                 .collect(Collectors.toList());

        if (overlappingCategoryUserList.isEmpty()) {
            return getRecommendUserAsManyAsNeed(relationUserList, 30);
        }

        List<String> recommendUserList = makeRecommendUserList(uid, overlappingCategoryUserList);

        // 추천 유저가 30명이 안될 경우, 부족한 추천 user를 채운다.
        if (recommendUserList.size() < 30) {
            // exclude 대상으로 relation이 있는 user와 생성된 recommend user를 추가한다.
            relationUserList.addAll(recommendUserList);

            recommendUserList.addAll(
                    getRecommendUserAsManyAsNeed(relationUserList, 30 - recommendUserList.size())
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

    private List<String> makeRecommendUserList(String uid, List<String> overlappingCategoryUserList) {
        Set<BookCategory> userCategorySet = new HashSet<>(userCategoryRepository.getUserCategoryListByUid(uid))
                .stream()
                .map(UserCategory::getBookCategory)
                .collect(Collectors.toSet());

        // 겹치는 카테고리를 제외한 카테고리의 개수 카운트
        Map<String, Long> userCategoryCountMap = userCategoryRepository.getOverlappingUserCategoryList(
                overlappingCategoryUserList
                ).stream()
                .filter(userCategory -> !userCategorySet.contains(userCategory.getBookCategory()))
                .map(UserCategory::getUid)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // feed count는 0으로 두고, category count로 recommend user 생성
        Map<String, RecommendUser> recommendUserMap = userCategoryCountMap.entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> RecommendUser.builder()
                                        .uid(entry.getKey())
                                        .categoryCount(entry.getValue())
                                        .feedCount(0)
                                        .build()
                        )
                );

        // 작성한 feed가 존재할 경우, 해당 feed의 개수를 count하여 recommend user에 set
        feedMapper.getFeedListByUidList(overlappingCategoryUserList).stream()
                .map(Feed::getUid)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .forEach((key, value) -> recommendUserMap.get(key).setFeedCount(value));

        List<RecommendUser> recommendUserList = new ArrayList<>(recommendUserMap.values());

        Collections.sort(recommendUserList);

        return recommendUserList.stream()
                .map(RecommendUser::getUid)
                .collect(Collectors.toList());
    }
}

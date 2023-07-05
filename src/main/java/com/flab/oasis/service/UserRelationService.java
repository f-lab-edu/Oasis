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
import java.util.stream.Stream;

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

        // 카테고리가 겹치는 유저들 중 이미 relation이 존재하는 유저는 제외한다.
        Set<String> relationUserSet = new HashSet<>(relationUserList);
        overlappingCategoryUserList = overlappingCategoryUserList.stream()
                .filter(s -> !relationUserSet.contains(s))
                .collect(Collectors.toList());

        if (overlappingCategoryUserList.isEmpty()) {
            return getDefaultRecommendUserExcludeUidList(relationUserList);
        }

        List<String> recommendUserList = makeRecommendUserListByCategory(uid, overlappingCategoryUserList);

        // 카테고리 추천 유저가 30명이 안될 경우, 기본 추천 유저를 가져온다.
        if (recommendUserList.size() < 30) {
            recommendUserList.addAll(
                    getDefaultRecommendUserExcludeUidList(
                            Stream.concat(relationUserList.stream(), recommendUserList.stream())
                                    .collect(Collectors.toList())
                    )
            );
        }

        return recommendUserList;
    }

    public void createUserRelation(UserRelation userRelation) {
        userRelation.setUid(userAuthService.getAuthenticatedUid());

        userRelationRepository.createUserRelation(userRelation);
    }

    private List<String> getDefaultRecommendUserExcludeUidList(List<String> excludeUidList) {
        excludeUidList.add(userAuthService.getAuthenticatedUid());

        // '본인 | excludeUidList'를 제외하고 recommend user를 최대 30명 가져온다.
        return userInfoRepository.getDefaultRecommendUserExcludeUidList(excludeUidList);
    }

    private List<String> makeRecommendUserListByCategory(String uid, List<String> overlappingCategoryUserList) {
        Set<BookCategory> userCategorySet = new HashSet<>(userCategoryRepository.getUserCategoryListByUid(uid))
                .stream()
                .map(UserCategory::getBookCategory)
                .collect(Collectors.toSet());

        // 겹치는 카테고리를 제외한 카테고리의 개수 카운트
        Map<String, Long> userCategoryCountMap = userCategoryRepository
                .getUserCategoryListByUidList(overlappingCategoryUserList)
                .stream()
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

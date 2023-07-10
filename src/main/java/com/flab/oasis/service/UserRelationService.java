package com.flab.oasis.service;

import com.flab.oasis.constant.BookCategory;
import com.flab.oasis.mapper.user.FeedMapper;
import com.flab.oasis.model.*;
import com.flab.oasis.repository.UserCategoryRepository;
import com.flab.oasis.repository.UserInfoRepository;
import com.flab.oasis.repository.UserRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRelationService {
    private final UserCategoryRepository userCategoryRepository;
    private final UserRelationRepository userRelationRepository;
    private final UserInfoRepository userInfoRepository;
    private final FeedMapper feedMapper;

    @Cacheable(cacheNames = "recommendUserCache", key = "#recommendUserRequest.uid", cacheManager = "redisCacheManager")
    public List<String> getRecommendUserList(RecommendUserRequest recommendUserRequest) {
        // 추천 유저에서 제외할 목록을 가져온다.
        Set<String> excludeUidSet = userRelationRepository.getUserRelationListByUid(recommendUserRequest.getUid())
                .stream()
                .map(UserRelation::getRelationUser)
                .collect(Collectors.toSet());
        excludeUidSet.add(recommendUserRequest.getUid());

        // 추천받을 유저의 category 목록을 가져온다.
        Set<BookCategory> bookCategorySet = userCategoryRepository.getUserCategoryListByUid(
                        recommendUserRequest.getUid()
                ).stream()
                .map(UserCategory::getBookCategory)
                .collect(Collectors.toSet());

        // 설정한 category가 없으면 기본 추천 유저를 반환한다.
        if (bookCategorySet.isEmpty()) {
            return getDefaultRecommendUserExcludeUidList(recommendUserRequest.getUid(), excludeUidSet);
        }

        // category별로 6개월 안에 가입했거나 category를 수정한 uid 목록을 가져온다.
        Map<BookCategory, Set<String>> bookCategoryUidSetMap = Arrays.stream(BookCategory.values())
                .collect(Collectors.toMap(
                        bookCategory -> bookCategory,
                        bookCategory -> new HashSet<>(userCategoryRepository.getUidListByBookCategory(bookCategory))
                ));

        Map<String, RecommendUser> recommendUserMap = new HashMap<>();

        // book category가 겹치면서 excludeUidSet에 해당하지 않는 uid를 추천 유저로 만든다.
        bookCategorySet.forEach(bookCategory -> bookCategoryUidSetMap.get(bookCategory).stream()
                .filter(uid -> !recommendUserMap.containsKey(uid) && !excludeUidSet.contains(uid))
                .forEach(uid -> recommendUserMap.put(
                        uid,
                        RecommendUser.builder()
                                .uid(uid)
                                .categoryCount(0)
                                .feedCount(feedMapper.getFeedListByUid(uid).size())
                                .build()
                ))
        );

        // 겹치는 카테고리를 제외한 나머지 카테고리의 개수를 카운트한다.
        bookCategoryUidSetMap.entrySet().stream()
                .filter(entry -> !bookCategorySet.contains(entry.getKey()))
                .forEach(entry -> entry.getValue().stream()
                        .filter(recommendUserMap::containsKey)
                        .forEach(uid -> recommendUserMap.get(uid)
                                .setCategoryCount(
                                        recommendUserMap.get(uid).getCategoryCount() + 1
                                )
                        )
                );

        List<String> recommendUserList = recommendUserMap.values().stream()
                .sorted()
                .map(RecommendUser::getUid)
                .collect(Collectors.toList());

        // 카테고리 추천 유저가 30명이 안될 경우, 기본 추천 유저를 가져온다.
        if (recommendUserList.size() < recommendUserRequest.getCheckSize()) {
            excludeUidSet.addAll(recommendUserList);

            recommendUserList.addAll(
                    getDefaultRecommendUserExcludeUidList(recommendUserRequest.getUid(), excludeUidSet)
            );
        }

        return recommendUserList;
    }

    @CachePut(cacheNames = "UserRelation", key = "#userRelation.uid", cacheManager = "redisCacheManager")
    public List<UserRelation> createUserRelation(UserRelation userRelation) {
        userRelationRepository.createUserRelation(userRelation);

        List<UserRelation> userRelationList = userRelationRepository.getUserRelationListByUid(userRelation.getUid());
        userRelationList.add(userRelation);

        return userRelationList;
    }
    
    private List<String> getDefaultRecommendUserExcludeUidList(String uid, Set<String> excludeUidSet) {
        // 6개월 안에 가입한 default recommend user를 최대 30명 가져와서 exclude uid를 제외한 유저를 반환한다.
        return userInfoRepository.getDefaultRecommendUserList(uid).stream()
                .filter(s -> !excludeUidSet.contains(s))
                .collect(Collectors.toList());
    }
}

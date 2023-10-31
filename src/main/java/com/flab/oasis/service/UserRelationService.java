package com.flab.oasis.service;

import com.flab.oasis.constant.BookCategory;
import com.flab.oasis.constant.ConstantName;
import com.flab.oasis.mapper.user.FeedMapper;
import com.flab.oasis.model.RecommendCandidateUser;
import com.flab.oasis.model.RecommendUser;
import com.flab.oasis.model.UserCategory;
import com.flab.oasis.model.UserRelation;
import com.flab.oasis.repository.ConstantDefinitionRepository;
import com.flab.oasis.repository.UserCategoryRepository;
import com.flab.oasis.repository.UserInfoRepository;
import com.flab.oasis.repository.UserRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRelationService {
    private final ConstantDefinitionRepository constantDefinitionRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final UserRelationRepository userRelationRepository;
    private final UserInfoRepository userInfoRepository;
    private final FeedMapper feedMapper;

    @Cacheable(cacheNames = "RecommendUserList", key = "#uid", cacheManager = "redisCacheManager")
    public List<String> getRecommendUserListByUidAndCheckSize(String uid) {
        // 추천 유저에서 제외할 목록을 가져온다.
        Set<String> excludeUidSet = userRelationRepository.getUserRelationListByUid(uid)
                .stream()
                .map(UserRelation::getRelationUser)
                .collect(Collectors.toSet());
        excludeUidSet.add(uid);

        // 추천받을 유저의 category 목록을 가져온다.
        Set<BookCategory> bookCategorySet = userCategoryRepository.getUserCategoryListByUid(uid).stream()
                .map(UserCategory::getBookCategory)
                .collect(Collectors.toSet());

        // 설정한 category가 없으면 기본 추천 유저를 반환한다.
        if (bookCategorySet.isEmpty()) {
            return getDefaultRecommendUserExcludeUidList(excludeUidSet);
        }

        // category별로 6개월 안에 가입했거나 category를 수정한 uid 목록을 가져온다.
        Map<BookCategory, Set<String>> bookCategoryUidSetMap = Arrays.stream(BookCategory.values())
                .collect(Collectors.toMap(
                        bookCategory -> bookCategory,
                        bookCategory -> userCategoryRepository
                                .getRecommendCandidateUserListByBookCategory(bookCategory).stream()
                                .filter(recommendCandidateUser -> LocalDateTime.now()
                                        .plusMonths(-6)
                                        .isBefore(recommendCandidateUser.getModifyDatetime())
                                ).map(RecommendCandidateUser::getUid)
                                .collect(Collectors.toSet())
                ));

        Map<String, RecommendUser> recommendUserMap = new HashMap<>();

        // book category가 겹치면서 excludeUidSet에 해당하지 않는 uid를 추천 유저로 만든다.
        bookCategorySet.forEach(bookCategory -> bookCategoryUidSetMap.get(bookCategory).stream()
                .filter(recommendCandidateUid ->
                                !recommendUserMap.containsKey(recommendCandidateUid) &&
                                        !excludeUidSet.contains(recommendCandidateUid)
                )
                .forEach(recommendUid -> recommendUserMap.put(
                        recommendUid,
                        RecommendUser.builder()
                                .uid(recommendUid)
                                .categoryCount(0)
                                .feedCount(feedMapper.getFeedListByUid(recommendUid).size())
                                .build()
                ))
        );

        // 겹치는 카테고리를 제외한 나머지 카테고리의 개수를 카운트한다.
        bookCategoryUidSetMap.entrySet().stream()
                .filter(entry -> !bookCategorySet.contains(entry.getKey()))
                .forEach(entry -> entry.getValue().stream()
                        .filter(recommendUserMap::containsKey)
                        .forEach(recommendUid -> recommendUserMap.get(recommendUid)
                                .setCategoryCount(
                                        recommendUserMap.get(recommendUid).getCategoryCount() + 1
                                )
                        )
                );

        List<String> recommendUserList = recommendUserMap.values().stream()
                .sorted()
                .map(RecommendUser::getUid)
                .collect(Collectors.toList());

        // 카테고리 추천 유저가 check size보다 적을 경우, 기본 추천 유저를 추가한다.
        int checkSize = constantDefinitionRepository.getIntValueByConstantName(
                ConstantName.RECOMMEND_USER_CHECK_SIZE
        );
        if (recommendUserList.size() < checkSize) {
            excludeUidSet.addAll(recommendUserList);

            recommendUserList.addAll(
                    getDefaultRecommendUserExcludeUidList(excludeUidSet)
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

    private List<String> getDefaultRecommendUserExcludeUidList(Set<String> excludeUidSet) {
        // 6개월 안에 가입했거나 정보를 수정한 user를 가져와서 exclude uid를 제외하고 default recommend user로 반환한다.
        return userInfoRepository.getDefaultRecommendUserList().stream()
                .filter(recommendCandidateUid -> !excludeUidSet.contains(recommendCandidateUid))
                .map(recommendUid -> RecommendUser.builder()
                            .uid(recommendUid)
                            .categoryCount(0)
                            .feedCount(feedMapper.getFeedListByUid(recommendUid).size())
                            .build()
                ).sorted()
                .map(RecommendUser::getUid)
                .collect(Collectors.toList());
    }
}

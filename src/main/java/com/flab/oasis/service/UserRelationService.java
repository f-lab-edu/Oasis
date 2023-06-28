package com.flab.oasis.service;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.mapper.user.FeedMapper;
import com.flab.oasis.model.*;
import com.flab.oasis.model.exception.NotFoundException;
import com.flab.oasis.repository.UserCategoryRepository;
import com.flab.oasis.repository.UserRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRelationService {
    private final UserAuthService userAuthService;
    private final FeedMapper feedMapper;
    private final UserCategoryRepository userCategoryRepository;
    private final UserRelationRepository userRelationRepository;

    public List<String> getRecommendUserList() {
        String uid = userAuthService.getAuthenticatedUid();

        List<UserCategory> userCategoryList = userCategoryRepository.getUserCategoryListByUid(uid);
        List<UserRelation> userRelationList = userRelationRepository.getUserRelationListByUid(uid);

        List<String> overlappingCategoryUserList = userCategoryRepository.getUidListWithOverlappingBookCategory(
                OverlappingCategoryUserSelect.builder()
                        .uid(uid)
                        .userCategoryList(userCategoryList)
                        .userRelationList(userRelationList)
                        .build()
        );

        List<RecommendUser> recommendUserList = new ArrayList<>();

        if (overlappingCategoryUserList.isEmpty()) {
            recommendUserList.addAll(getRecommendUserAsManyAsNeed(uid, 30));
        } else {
            List<UserCategoryCount> userCategoryCountList = userCategoryRepository.getUserCategoryCountList(
                    UserCategoryCountSelect.builder()
                            .uidList(overlappingCategoryUserList)
                            .userCategoryList(userCategoryList)
                            .build()
            );

            List<FeedCount> feedCountList = feedMapper.getFeedCountList(
                    FeedCountSelect.builder()
                            .uidList(overlappingCategoryUserList)
                            .needSize(-1)
                            .build()
            );

            recommendUserList.addAll(
                    userCategoryCountList.stream()
                            .map(
                                    userCategoryCount -> RecommendUser.builder()
                                            .uid(userCategoryCount.getUid())
                                            .categoryCount(userCategoryCount.getBookCategoryCount())
                                            .build()
                            ).sorted().collect(Collectors.toList())
            );

            feedCountList.forEach(
                    feedCount -> recommendUserList.stream()
                                .filter(user -> user.getUid().equals(feedCount.getUid()))
                                .findFirst()
                                .orElseThrow(() -> new NotFoundException(
                                        ErrorCode.NOT_FOUND, "Can't find recommend user's uid", feedCount.getUid()
                                ))
                                .setFeedCount(feedCount.getFeedCount())
            );
        }

        if (recommendUserList.size() < 30) {
            recommendUserList.addAll(
                    getRecommendUserAsManyAsNeed(uid, 30 - recommendUserList.size())
            );
        }

        return recommendUserList.stream()
                .map(RecommendUser::getUid)
                .collect(Collectors.toList());
    }

    private List<RecommendUser> getRecommendUserAsManyAsNeed(String uid, int needSize) {
        List<FeedCount> feedCountList = feedMapper.getFeedCountList(
                FeedCountSelect.builder()
                        .uid(uid)
                        .needSize(needSize)
                        .build()
        );

        return feedCountList.stream()
                .map(
                        feedCount -> RecommendUser.builder()
                                .uid(feedCount.getUid())
                                .feedCount(feedCount.getFeedCount())
                                .build()
                ).sorted()
                .collect(Collectors.toList());
    }
}

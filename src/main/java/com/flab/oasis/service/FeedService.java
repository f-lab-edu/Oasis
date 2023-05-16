package com.flab.oasis.service;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.mapper.user.FeedMapper;
import com.flab.oasis.model.Feed;
import com.flab.oasis.model.FeedDeleteRequest;
import com.flab.oasis.model.FeedUpdateRequest;
import com.flab.oasis.model.FeedWriteRequest;
import com.flab.oasis.model.exception.AuthorizationException;
import com.flab.oasis.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedMapper feedMapper;

    public void writeFeed(FeedWriteRequest feedWriteRequest) {
        String uid = getUid();

        int maxFeedId = Optional.ofNullable(feedMapper.getMaxFeedIdByUid(uid))
                .orElse(0);

        Feed newFeed = Feed.builder()
                .uid(uid)
                .feedId(maxFeedId + 1)
                .writeDate(new Date())
                .bookId(feedWriteRequest.getBookId())
                .report(feedWriteRequest.getReport())
                .feedLike(0)
                .build();

        feedMapper.writeFeed(newFeed);

        LogUtils.info(String.format("User \"%s\"'s new feed has been created.", uid), newFeed.toString());
    }

    public void updateFeed(FeedUpdateRequest feedUpdateRequest) {
        String uid = getUid();

        Feed updateFeed = Feed.builder()
                .uid(uid)
                .feedId(feedUpdateRequest.getFeedId())
                .report(feedUpdateRequest.getReport())
                .build();

        feedMapper.updateFeed(updateFeed);

        LogUtils.info(String.format("User \"%s\"'s feed has been modified.", uid), updateFeed.toString());
    }

    public void deleteFeed(FeedDeleteRequest feedDeleteRequest) {
        String uid = getUid();

        Feed deleteFeed = Feed.builder()
                .uid(uid)
                .feedId(feedDeleteRequest.getFeedId())
                .build();

        feedMapper.deleteFeed(deleteFeed);

        LogUtils.info(String.format("User \"%s\"'s feed has been deleted.", uid), deleteFeed.toString());
    }

    private String getUid() {
        return Optional.ofNullable(
                SecurityContextHolder.getContext()
        ).orElseThrow(
                () -> new AuthorizationException(ErrorCode.UNAUTHORIZED, "Unauthorized user.")
        ).getAuthentication().getPrincipal().toString();
    }
}

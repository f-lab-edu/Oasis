package com.flab.oasis.service;

import com.flab.oasis.interceptor.UidInterceptor;
import com.flab.oasis.mapper.user.FeedMapper;
import com.flab.oasis.model.Feed;
import com.flab.oasis.model.FeedDeleteRequest;
import com.flab.oasis.model.FeedUpdateRequest;
import com.flab.oasis.model.FeedWriteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedMapper feedMapper;

    public void writeFeed(FeedWriteRequest feedWriteRequest) {
        String uid = UidInterceptor.STRING_THREAD_LOCAL.get();
        int maxFeedId = Optional.ofNullable(feedMapper.getMaxFeedIdByUid(uid))
                .orElse(0);

        feedMapper.writeFeed(
                Feed.builder()
                        .uid(uid)
                        .feedId(maxFeedId + 1)
                        .writeDate(new Date())
                        .bookId(feedWriteRequest.getBookId())
                        .report(feedWriteRequest.getReport())
                        .feedLike(0)
                        .build()
        );
    }

    public void updateFeed(FeedUpdateRequest feedUpdateRequest) {
        feedMapper.updateFeed(
                Feed.builder()
                        .uid(UidInterceptor.STRING_THREAD_LOCAL.get())
                        .feedId(feedUpdateRequest.getFeedId())
                        .report(feedUpdateRequest.getReport())
                        .build()
        );
    }

    public void deleteFeed(FeedDeleteRequest feedDeleteRequest) {
        feedMapper.deleteFeed(
                Feed.builder()
                        .uid(UidInterceptor.STRING_THREAD_LOCAL.get())
                        .feedId(feedDeleteRequest.getFeedId())
                        .build()
        );
    }
}

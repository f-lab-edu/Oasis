package com.flab.oasis.service;

import com.flab.oasis.mapper.user.FeedMapper;
import com.flab.oasis.model.Feed;
import com.flab.oasis.model.FeedDeleteRequest;
import com.flab.oasis.model.FeedUpdateRequest;
import com.flab.oasis.model.FeedWriteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedMapper feedMapper;

    public void writeFeed(FeedWriteRequest feedWriteRequest) {
        int maxFeedId = feedMapper.getMaxFeedIdByUid(feedWriteRequest.getUid());

        feedMapper.writeFeed(
                Feed.builder()
                        .uid(feedWriteRequest.getUid())
                        .feedId(maxFeedId + 1)
                        .writeDate(new Date())
                        .bookId(feedWriteRequest.getBookId())
                        .report(feedWriteRequest.getReport())
                        .feedLike(0)
                        .build()
        );
    }

    public void updateFeed(FeedUpdateRequest feedUpdateRequest) {
        feedMapper.updateFeed(feedUpdateRequest);
    }

    public void deleteFeed(FeedDeleteRequest feedDeleteRequest) {
        feedMapper.deleteFeed(feedDeleteRequest);
    }
}

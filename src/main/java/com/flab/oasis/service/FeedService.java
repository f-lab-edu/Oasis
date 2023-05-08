package com.flab.oasis.service;

import com.flab.oasis.mapper.user.FeedMapper;
import com.flab.oasis.model.Feed;
import com.flab.oasis.model.FeedDeleteRequest;
import com.flab.oasis.model.FeedUpdateRequest;
import com.flab.oasis.model.FeedWriteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedMapper feedMapper;

    @Transactional
    public void writeFeed(FeedWriteRequest feedWriteRequest) {
        int maxFeedId = Optional.ofNullable(feedMapper.getMaxFeedIdByUid(feedWriteRequest.getUid())).orElse(0);

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

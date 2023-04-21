package com.flab.oasis.service;

import com.flab.oasis.mapper.user.FeedMapper;
import com.flab.oasis.model.FeedUpdateRequest;
import com.flab.oasis.model.FeedWriteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedMapper feedMapper;

    public void writeFeedByFeedWriteRequest(FeedWriteRequest feedWriteRequest) {
        feedMapper.writeFeedByFeedWriteRequest(feedWriteRequest);
    }

    public void updateFeedByFeedUpdateRequest(FeedUpdateRequest feedUpdateRequest) {
        feedMapper.updateFeedByFeedUpdateRequest(feedUpdateRequest);
    }
}

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

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedMapper feedMapper;

    // 한 유저가 동시에 여러 개의 요청을 날리게 되면 중복된 feed id가 생성될 수 있기에 transaction으로 중복 무결성을 보장해줘야 한다.
    @Transactional
    public void writeFeed(FeedWriteRequest feedWriteRequest) {
        // 작성한 피드가 존재하지 않으면 0을 반환한다.
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

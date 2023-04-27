package com.flab.oasis.mapper.user;

import com.flab.oasis.model.Feed;
import com.flab.oasis.model.FeedDeleteRequest;
import com.flab.oasis.model.FeedUpdateRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedMapper {
    public int getMaxFeedIdByUid(String uid);
    public void writeFeed(Feed feed);
    public void updateFeed(FeedUpdateRequest feedUpdateRequest);
    public void deleteFeed(FeedDeleteRequest feedDeleteRequest);
}

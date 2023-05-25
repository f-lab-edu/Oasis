package com.flab.oasis.mapper.user;

import com.flab.oasis.model.Feed;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedMapper {
    public Integer getMaxFeedIdByUid(String uid);
    public void writeFeed(Feed feed);
    public void updateFeed(Feed feed);
    public void deleteFeed(Feed feed);
}

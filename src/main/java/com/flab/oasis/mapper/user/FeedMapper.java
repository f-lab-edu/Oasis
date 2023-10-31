package com.flab.oasis.mapper.user;

import com.flab.oasis.model.Feed;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FeedMapper {
    public Integer getMaxFeedIdByUid(String uid);
    public void writeFeed(Feed feed);
    public List<Feed> getFeedListByUid(String uid);
    public void updateFeed(Feed feed);
    public void deleteFeed(Feed feed);
}

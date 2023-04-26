package com.flab.oasis.mapper.user;

import com.flab.oasis.model.FeedDeleteRequest;
import com.flab.oasis.model.FeedUpdateRequest;
import com.flab.oasis.model.FeedWriteRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedMapper {
    public void writeFeed(FeedWriteRequest feedWriteRequest);
    public void updateFeed(FeedUpdateRequest feedUpdateRequest);
    public void deleteFeed(FeedDeleteRequest feedDeleteRequest);
}

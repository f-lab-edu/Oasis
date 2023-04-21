package com.flab.oasis.mapper.user;

import com.flab.oasis.model.FeedUpdateRequest;
import com.flab.oasis.model.FeedWriteRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedMapper {
    public void writeFeedByFeedWriteRequest(FeedWriteRequest feedWriteRequest);
    public void updateFeedByFeedUpdateRequest(FeedUpdateRequest feedUpdateRequest);
}

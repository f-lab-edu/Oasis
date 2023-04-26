package com.flab.oasis.controller;

import com.flab.oasis.model.FeedDeleteRequest;
import com.flab.oasis.model.FeedUpdateRequest;
import com.flab.oasis.model.FeedWriteRequest;
import com.flab.oasis.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @PostMapping("/feed")
    public void writeFeedByFeedWriteRequest(@RequestBody FeedWriteRequest feedWriteRequest) {
        feedService.writeFeedByFeedWriteRequest(feedWriteRequest);
    }

    @PatchMapping("/feed")
    public void writeFeedByFeedWriteRequest(@RequestBody FeedUpdateRequest feedUpdateRequest) {
        feedService.updateFeedByFeedUpdateRequest(feedUpdateRequest);
    }

    @DeleteMapping("/feed")
    public void deleteFeed(@RequestBody FeedDeleteRequest feedDeleteRequest) {
        feedService.deleteFeedByFeedDeleteRequest(feedDeleteRequest);
    }
}

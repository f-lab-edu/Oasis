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
    public boolean writeFeed(@RequestBody FeedWriteRequest feedWriteRequest) {
        feedService.writeFeed(feedWriteRequest);

        return true;
    }

    @PatchMapping("/feed")
    public boolean updateFeed(@RequestBody FeedUpdateRequest feedUpdateRequest) {
        feedService.updateFeed(feedUpdateRequest);

        return true;
    }

    @DeleteMapping("/feed")
    public boolean deleteFeed(@RequestBody FeedDeleteRequest feedDeleteRequest) {
        feedService.deleteFeed(feedDeleteRequest);

        return true;
    }
}

package com.flab.oasis.controller;

import com.flab.oasis.model.FeedDeleteRequest;
import com.flab.oasis.model.FeedUpdateRequest;
import com.flab.oasis.model.FeedWriteRequest;
import com.flab.oasis.model.response.SuccessResponse;
import com.flab.oasis.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @PostMapping("/feed")
    public SuccessResponse writeFeed(@RequestBody FeedWriteRequest feedWriteRequest) {
        feedService.writeFeed(feedWriteRequest);

        return new SuccessResponse();
    }

    @PatchMapping("/feed")
    public SuccessResponse updateFeed(@RequestBody FeedUpdateRequest feedUpdateRequest) {
        feedService.updateFeed(feedUpdateRequest);

        return new SuccessResponse();
    }

    @DeleteMapping("/feed")
    public SuccessResponse deleteFeed(@RequestBody FeedDeleteRequest feedDeleteRequest) {
        feedService.deleteFeed(feedDeleteRequest);

        return new SuccessResponse();
    }
}

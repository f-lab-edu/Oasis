package com.flab.oasis.controller;

import com.flab.oasis.model.UserRelation;
import com.flab.oasis.model.response.SuccessResponse;
import com.flab.oasis.service.UserRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRelationController {
    private final UserRelationService userRelationService;

    @GetMapping("/relation/recommend")
    public List<String> getRecommendUserList() {
        return userRelationService.getRecommendUserList();
    }

    @PostMapping("/relation")
    public SuccessResponse createUserRelation(@RequestBody UserRelation userRelation) {
        userRelationService.createUserRelation(userRelation);

        return new SuccessResponse();
    }
}

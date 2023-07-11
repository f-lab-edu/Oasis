package com.flab.oasis.controller;

import com.flab.oasis.model.UserRelation;
import com.flab.oasis.service.UserAuthService;
import com.flab.oasis.service.UserRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRelationController {
    private final UserAuthService userAuthService;
    private final UserRelationService userRelationService;

    @GetMapping("/relation/recommend")
    public List<String> getRecommendUserList() {
        return userRelationService.getRecommendUserListByUidAndCheckSize(
                userAuthService.getAuthenticatedUid(), 30
        );
    }

    @PostMapping("/relation")
    public List<UserRelation> createUserRelation(@RequestBody UserRelation userRelation) {
        userRelation.setUid(userAuthService.getAuthenticatedUid());

        return userRelationService.createUserRelation(userRelation);
    }
}

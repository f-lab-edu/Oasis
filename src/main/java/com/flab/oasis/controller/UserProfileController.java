package com.flab.oasis.controller;

import com.flab.oasis.model.ResultResponse;
import com.flab.oasis.model.UserProfile;
import com.flab.oasis.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;

    @GetMapping("/profiles/duplicate/{nickname}")
    public ResultResponse<Boolean> isExistsNickname(@PathVariable String nickname) {
        return userProfileService.isExistsNickname(nickname);
    }

    @PostMapping("/profile")
    public ResultResponse<Boolean> createUserProfile(@RequestBody UserProfile userProfile) {
        return userProfileService.createUserProfile(userProfile);
    }

    @GetMapping("/profile")
    public ResultResponse<UserProfile> getUserProfileByUid() {
        return userProfileService.getUserProfileByUid();
    }
}

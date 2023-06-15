package com.flab.oasis.controller;

import com.flab.oasis.model.GeneralResponse;
import com.flab.oasis.model.UserProfile;
import com.flab.oasis.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;

    @GetMapping("/profiles/duplicate/{nickname}")
    public GeneralResponse<Boolean> isExistsNickname(@PathVariable String nickname) {
        return userProfileService.isExistsNickname(nickname);
    }

    @PostMapping("/profile")
    public GeneralResponse<Boolean> createUserProfile(@RequestBody UserProfile userProfile) {
        return userProfileService.createUserProfile(userProfile);
    }

    @GetMapping("/profile")
    public GeneralResponse<UserProfile> getUserProfileByUid() {
        return userProfileService.getUserProfileByUid();
    }
}

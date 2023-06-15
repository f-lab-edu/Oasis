package com.flab.oasis.controller;

import com.flab.oasis.model.GeneralResponse;
import com.flab.oasis.model.UserProfile;
import com.flab.oasis.model.exception.NotFoundException;
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
        return GeneralResponse.<Boolean>builder()
                .code(0)
                .data(userProfileService.isExistsNickname(nickname))
                .build();
    }

    @PostMapping("/profile")
    public GeneralResponse<Boolean> createUserProfile(@RequestBody UserProfile userProfile) {
        userProfileService.createUserProfile(userProfile);

        return GeneralResponse.<Boolean>builder()
                .code(0)
                .data(true)
                .build();
    }

    @GetMapping("/profile")
    public GeneralResponse<UserProfile> getUserProfileByUid() {
        try{
            return GeneralResponse.<UserProfile>builder()
                    .code(0)
                    .data(userProfileService.getUserProfileByUid())
                    .build();
        } catch (NotFoundException e) {
            return GeneralResponse.<UserProfile>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        }
    }
}

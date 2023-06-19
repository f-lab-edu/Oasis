package com.flab.oasis.controller;

import com.flab.oasis.constant.ResponseCode;
import com.flab.oasis.model.GeneralResponse;
import com.flab.oasis.model.UserProfile;
import com.flab.oasis.model.exception.NotFoundException;
import com.flab.oasis.model.response.SuccessResponse;
import com.flab.oasis.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;

    @GetMapping("/profiles/duplicate/{nickname}")
    public boolean isExistsNickname(@PathVariable String nickname) {
        return userProfileService.isExistsNickname(nickname);
    }

    @PostMapping("/profile")
    public SuccessResponse createUserProfile(@RequestBody UserProfile userProfile) {
        userProfileService.createUserProfile(userProfile);

        return new SuccessResponse();
    }

    @GetMapping("/profile")
    public GeneralResponse<UserProfile> getUserProfileByUid() {
        try{
            return GeneralResponse.<UserProfile>builder()
                    .code(ResponseCode.OK.getCode())
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

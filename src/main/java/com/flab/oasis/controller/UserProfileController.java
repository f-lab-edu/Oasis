package com.flab.oasis.controller;

import com.flab.oasis.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;

    @GetMapping("/profiles/duplicate/{nickname}")
    public boolean isExistsNickname(@PathVariable String nickname) {
        return userProfileService.isExistsNickname(nickname);
    }
}

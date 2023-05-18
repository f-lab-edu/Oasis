package com.flab.oasis.service;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.model.exception.AuthorizationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SecurityContextService {
    public String getAuthorizedUid() {
        return Optional.ofNullable(
                SecurityContextHolder.getContext()
        ).orElseThrow(
                () -> new AuthorizationException(ErrorCode.UNAUTHORIZED, "Unauthorized user.")
        ).getAuthentication().getPrincipal().toString();
    }
}

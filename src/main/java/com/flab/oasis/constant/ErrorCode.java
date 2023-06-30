package com.flab.oasis.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500),
    EXPECTATION_FAILED(417),
    SERVICE_UNAVAILABLE(503);

    final int code;
}

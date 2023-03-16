package com.flab.oasis.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    RESET_CONTENT(205),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    SERVICE_UNAVAILABLE(503);

    final int code;
}

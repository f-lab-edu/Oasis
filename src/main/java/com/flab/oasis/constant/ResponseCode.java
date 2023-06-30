package com.flab.oasis.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    OK(200),
    RESET_CONTENT(205);

    private final int code;
}

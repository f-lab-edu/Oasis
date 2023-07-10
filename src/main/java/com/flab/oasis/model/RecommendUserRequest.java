package com.flab.oasis.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendUserRequest {
    private String uid;
    private int checkSize;
}

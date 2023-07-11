package com.flab.oasis.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RecommendCandidateUser implements Serializable {
    private static final long serialVersionUID = -8292949726551011104L;

    private String uid;
    private LocalDateTime modifyDatetime;
}

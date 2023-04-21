package com.flab.oasis.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FeedUpdateRequest implements Serializable {
    private static final long serialVersionUID = 446411284628666675L;
    
    private String uid;
    private String feedId;
    private String report;
}

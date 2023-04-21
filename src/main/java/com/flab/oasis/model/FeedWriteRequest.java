package com.flab.oasis.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FeedWriteRequest implements Serializable {
    private static final long serialVersionUID = 5985546037987964046L;

    private String uid;
    private String bookId;
    private String report;
}

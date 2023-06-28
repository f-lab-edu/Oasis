package com.flab.oasis.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FeedCount implements Serializable {
    private static final long serialVersionUID = 7061404739566319032L;

    private String uid;
    private int feedCount;
}

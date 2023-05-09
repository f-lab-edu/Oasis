package com.flab.oasis.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FeedDeleteRequest implements Serializable {
    private static final long serialVersionUID = 1600046029481345714L;

    private int feedId;
}

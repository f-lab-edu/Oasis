package com.flab.oasis.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedCountSelect implements Serializable {
    private static final long serialVersionUID = 7061404739566319032L;

    private String uid;
    private List<String> uidList;
    private int needSize;
}

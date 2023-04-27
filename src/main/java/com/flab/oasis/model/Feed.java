package com.flab.oasis.model;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feed implements Serializable {
    private static final long serialVersionUID = 7061404739566319032L;

    private String uid;
    private int feedId;
    private Date writeDate;
    private String bookId;
    private String report;
    private int feedLike;
}

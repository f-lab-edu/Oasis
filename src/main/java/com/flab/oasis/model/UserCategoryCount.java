package com.flab.oasis.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class UserCategoryCount implements Serializable {
    private static final long serialVersionUID = 3812763620765883807L;

    private String uid;
    private int bookCategoryCount;
}

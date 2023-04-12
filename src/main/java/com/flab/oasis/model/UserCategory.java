package com.flab.oasis.model;

import com.flab.oasis.constant.BookCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCategory {
    private String uid;
    private BookCategory bookCategory;
}

package com.flab.oasis.model;

import com.flab.oasis.constant.BookCategory;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile implements Serializable {
    private static final long serialVersionUID = -2339436237546446477L;

    private String uid;
    private String nickname;
    private String introduce;
    private List<BookCategory> bookCategoryList;
}

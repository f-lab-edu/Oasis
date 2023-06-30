package com.flab.oasis.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCategoryCountSelect implements Serializable {
    private static final long serialVersionUID = 1389467238782839862L;

    private List<String> overlappingCategoryUserList;
    private List<UserCategory> userCategoryList;
}

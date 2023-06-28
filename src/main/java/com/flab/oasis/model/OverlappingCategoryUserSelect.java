package com.flab.oasis.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverlappingCategoryUserSelect implements Serializable {
    private static final long serialVersionUID = 4206798667957203014L;

    private String uid;
    private List<UserCategory> userCategoryList;
    private List<UserRelation> userRelationList;
}

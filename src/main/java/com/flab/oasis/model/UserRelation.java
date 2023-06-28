package com.flab.oasis.model;

import com.flab.oasis.constant.RelationType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserRelation implements Serializable {
    private static final long serialVersionUID = 9092423039001303261L;

    private String uid;
    private String relationUser;
    private RelationType relationType;
}

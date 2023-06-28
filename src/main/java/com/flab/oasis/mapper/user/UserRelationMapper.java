package com.flab.oasis.mapper.user;

import com.flab.oasis.model.UserRelation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRelationMapper {
    public List<UserRelation> getUserRelationListByUid(String uid);
}

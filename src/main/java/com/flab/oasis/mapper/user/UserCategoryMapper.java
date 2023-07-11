package com.flab.oasis.mapper.user;

import com.flab.oasis.constant.BookCategory;
import com.flab.oasis.model.RecommendCandidateUser;
import com.flab.oasis.model.UserCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserCategoryMapper {
    public void createUserCategory(List<UserCategory> userCategoryList);
    public List<UserCategory> getUserCategoryListByUid(String uid);
    public List<RecommendCandidateUser> getRecommendCandidateUserListByBookCategory(BookCategory bookCategory);
}

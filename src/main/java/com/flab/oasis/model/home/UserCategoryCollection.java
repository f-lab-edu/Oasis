package com.flab.oasis.model.home;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class UserCategoryCollection {
    private final List<UserCategory> userCategoryList;

    public List<Integer> getCategoryIdList() {
        return userCategoryList.stream().map(UserCategory::getCategoryId).collect(Collectors.toList());
    }
}

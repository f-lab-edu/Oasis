package com.flab.oasis.repository;

import com.flab.oasis.constant.ConstantName;
import com.flab.oasis.mapper.user.ConstantDefinitionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConstantDefinitionRepository {
    private final ConstantDefinitionMapper constantDefinitionMapper;

    @Cacheable(cacheNames = "defaultCache", key = "#constantName", cacheManager = "ehCacheCacheManager")
    public int getIntValueByConstantName(ConstantName constantName) {
        return constantDefinitionMapper.getIntValueByConstantName(constantName);
    }
}

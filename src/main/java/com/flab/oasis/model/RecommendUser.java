package com.flab.oasis.model;

import com.google.common.collect.ComparisonChain;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class RecommendUser implements Comparable<RecommendUser> {
    private String uid;
    private long categoryCount;
    private long feedCount;

    @Override
    public int compareTo(RecommendUser o) {
        return ComparisonChain.start()
                .compare(categoryCount, o.categoryCount)
                .compare(feedCount, o.feedCount)
                .result();
    }
}

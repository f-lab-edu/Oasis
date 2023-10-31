package com.flab.oasis.model;

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
        long compareCategoryCount = o.getCategoryCount() - this.categoryCount;
        if (compareCategoryCount != 0) {
            return (int) compareCategoryCount;
        }

        return (int) (o.getFeedCount() - this.feedCount);
    }
}

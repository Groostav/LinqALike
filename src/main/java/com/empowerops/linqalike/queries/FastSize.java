package com.empowerops.linqalike.queries;

/**
 * interface to denote that a particular Queryable implementation
 * has custom (fast) handling for size() access.
 *
 * Created by Geoff on 2015-10-29.
 */
// TODO this is broken, it should be a tree-model wherein we iterate down,
// but as is a lot of cappedCount methods dont properly recurse.
public interface FastSize{

    int size();

    /**
     * Counts the number of elements up to the supplied <code>maxValueToReturn</code>,
     * at which point <i>no further counting</i> should be done.
     */
    default int cappedCount(int maxValueToReturn){
        int size = size();
        return Math.min(maxValueToReturn, size);
    }
}

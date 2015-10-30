package com.empowerops.linqalike.queries;

/**
 * interface to denote that a particular Queryable implementation
 * has custom (fast) handling for size() access.
 *
 * Created by Geoff on 2015-10-29.
 */
public interface FastSize{

    int size();

    default int cappedCount(int maxValueToReturn){
        int size = size();
        return Math.min(maxValueToReturn, size);
    }
}

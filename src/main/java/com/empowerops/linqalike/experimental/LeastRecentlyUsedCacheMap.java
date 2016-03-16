package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.LinqingMap;

import java.util.Map;

public class LeastRecentlyUsedCacheMap<TKey, TValue> extends LinqingMap<TKey, TValue> {

    private static final long serialVersionUID = - 702432047434624162L;
    private final int cacheSize;

    public LeastRecentlyUsedCacheMap(int cacheSize, float loadFactor) {
        super(cacheSize, loadFactor);
        this.cacheSize = cacheSize;
    }

    public LeastRecentlyUsedCacheMap(int cacheSize) {
        super(cacheSize);
        this.cacheSize = cacheSize;
    }

    @Override protected boolean removeEldestEntry(Map.Entry<TKey, TValue> eldest) {
        return size() > cacheSize;
    }
}

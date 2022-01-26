package com.suneo.flag.cache;

public interface CacheStrategy<T> {
    boolean shouldCache(T item);
}

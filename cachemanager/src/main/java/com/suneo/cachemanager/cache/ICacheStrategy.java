package com.suneo.cachemanager.cache;

public interface ICacheStrategy<T> {
    boolean shouldCache(T item);
}

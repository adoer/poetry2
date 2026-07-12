package com.news.util;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CaptchaCache {

    private static class Entry {
        final String code;
        final long expireAt;

        Entry(String code, long ttlMillis) {
            this.code = code;
            this.expireAt = System.currentTimeMillis() + ttlMillis;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expireAt;
        }
    }

    private final Map<String, Entry> cache = new ConcurrentHashMap<>();

    public void put(String key, String code, long ttlMillis) {
        cache.put(key, new Entry(code, ttlMillis));
    }

    public String get(String key) {
        Entry entry = cache.get(key);
        if (entry == null) return null;
        if (entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return entry.code;
    }

    public void remove(String key) {
        cache.remove(key);
    }

    public void evictExpired() {
        cache.entrySet().removeIf(e -> e.getValue().isExpired());
    }
}

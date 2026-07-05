package com.news.util;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklist {

    private static final int MAX_SIZE = 10_000;

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void add(String token) {
        if (blacklistedTokens.size() >= MAX_SIZE) {
            blacklistedTokens.clear();
        }
        blacklistedTokens.add(token);
    }

    public boolean contains(String token) {
        return blacklistedTokens.contains(token);
    }
}

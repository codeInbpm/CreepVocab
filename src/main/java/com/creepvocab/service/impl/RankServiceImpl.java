package com.creepvocab.service.impl;

import com.creepvocab.service.RankService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RankServiceImpl implements RankService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String RANK_KEY = "rank:global:word_power";

    public RankServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void updateScore(Long userId, double score) {
        redisTemplate.opsForZSet().add(RANK_KEY, userId, score);
    }

    @Override
    public Set<Object> getTopPlayers(int limit) {
        // Get top N players (reverse range for high score first)
        return redisTemplate.opsForZSet().reverseRange(RANK_KEY, 0, limit - 1);
    }
}

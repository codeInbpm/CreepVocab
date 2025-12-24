package com.creepvocab.service.impl;

import com.creepvocab.entity.User;
import com.creepvocab.service.MatchService;
import com.creepvocab.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class MatchServiceImpl implements MatchService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserService userService;

    public MatchServiceImpl(RedisTemplate<String, Object> redisTemplate, UserService userService) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
    }

    @Override
    public void addToQueue(Long userId, String mode) {
        String key = "match:queue:" + mode;
        redisTemplate.opsForList().rightPush(key, userId);
        // Expire queue to avoid stale users
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
    }

    @Override
    public void removeFromQueue(Long userId, String mode) {
        String key = "match:queue:" + mode;
        redisTemplate.opsForList().remove(key, 0, userId); // 0 means remove all occurrences
    }

    @Override
    public User findOpponent(Long userId, String mode) {
        String key = "match:queue:" + mode;
        Object opponentIdObj = redisTemplate.opsForList().leftPop(key);
        
        if (opponentIdObj != null) {
            Long opponentId = ((Number) opponentIdObj).longValue();
            if (opponentId.equals(userId)) {
                // If popped self, push back (or handle differently, simplified here)
                redisTemplate.opsForList().rightPush(key, userId);
                return null; 
            }
            return userService.getById(opponentId);
        }
        return null;
    }
}

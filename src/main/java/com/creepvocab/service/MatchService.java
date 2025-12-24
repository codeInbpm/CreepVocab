package com.creepvocab.service;

import com.creepvocab.entity.User;

public interface MatchService {
    void addToQueue(Long userId, String mode);
    void removeFromQueue(Long userId, String mode);
    User findOpponent(Long userId, String mode);
}

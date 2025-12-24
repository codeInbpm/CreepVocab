package com.creepvocab.service;

import com.creepvocab.entity.User;
import java.util.List;
import java.util.Set;

public interface RankService {
    void updateScore(Long userId, double score);
    Set<Object> getTopPlayers(int limit);
}

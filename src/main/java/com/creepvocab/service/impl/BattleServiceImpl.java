package com.creepvocab.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creepvocab.entity.BattleRoom;
import com.creepvocab.entity.User;
import com.creepvocab.entity.Word;
import com.creepvocab.mapper.BattleRoomMapper;
import com.creepvocab.service.BattleService;
import com.creepvocab.service.UserService;
import com.creepvocab.service.WordService;
import com.creepvocab.vo.BattleAction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BattleServiceImpl extends ServiceImpl<BattleRoomMapper, BattleRoom> implements BattleService {

    private final WordService wordService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserService userService;

    public BattleServiceImpl(WordService wordService, RedisTemplate<String, Object> redisTemplate, UserService userService) {
        this.wordService = wordService;
        this.redisTemplate = redisTemplate;
        this.userService = userService;
    }

    @Override
    public BattleRoom createRoom(Long creatorId, String type, boolean aiMode) {
        BattleRoom room = new BattleRoom();
        room.setCreatorId(creatorId);
        room.setPlayer1Id(creatorId);
        room.setWordBook(type);
        room.setStatus("waiting");
        room.setAiMode(aiMode);
        room.setCreateTime(LocalDateTime.now());
        room.setRoomCode(UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        
        // Generate Questions (Simplified: Random 10 words)
        List<Word> allWords = wordService.list(); // Optimize in real app
        Collections.shuffle(allWords);
        List<Long> questionIds = allWords.stream().limit(10).map(Word::getId).collect(Collectors.toList());
        room.setQuestions(questionIds);
        room.setQuestionCount(10);

        if (aiMode) {
            room.setPlayer2Id(0L); // 0 for AI
            room.setStatus("playing");
            room.setStartTime(LocalDateTime.now());
        }

        save(room);
        
        // Cache to Redis
        String key = "battle:room:" + room.getId();
        redisTemplate.opsForValue().set(key, room);
        
        return room;
    }

    @Override
    public BattleRoom joinRoom(String roomCode, Long userId) {
        // Logic to join room (DB lookup -> update -> Redis update)
        // Simplified for this step
        return null; 
    }

    @Override
    public void handleAction(Long userId, BattleAction action) {
        // Handle answers, broadcast via Redis
        String roomId = action.getRoomId();
        String channel = "/topic/room/" + roomId;
        
        // Process logic...
        
        // Broadcast result
        redisTemplate.convertAndSend(channel, action); // Echo back for now
    }
}

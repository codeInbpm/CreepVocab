package com.creepvocab.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creepvocab.entity.BattleRoom;
import com.creepvocab.vo.BattleAction;

public interface BattleService extends IService<BattleRoom> {
    BattleRoom createRoom(Long creatorId, String type, boolean aiMode);
    BattleRoom joinRoom(String roomCode, Long userId);
    void handleAction(Long userId, BattleAction action);
}

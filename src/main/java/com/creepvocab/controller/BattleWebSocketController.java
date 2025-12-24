package com.creepvocab.controller;

import com.creepvocab.service.BattleService;
import com.creepvocab.utils.JwtUtil;
import com.creepvocab.vo.BattleAction;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class BattleWebSocketController {

    private final BattleService battleService;
    private final JwtUtil jwtUtil;

    public BattleWebSocketController(BattleService battleService, JwtUtil jwtUtil) {
        this.battleService = battleService;
        this.jwtUtil = jwtUtil;
    }

    @MessageMapping("/battle.action")
    public void handleAction(@Payload BattleAction action, SimpMessageHeaderAccessor headerAccessor) {
        // Extract user from header (set during handshake)
        // For MVP, assume payload contains token or we trust the sender for now
        // In real app: configure ChannelInterceptor to validate token in Connect and set User Principal
        
        // Simplified: use a dummy ID or passed in payload if needed. 
        // Better: Use SimpUserRegistry or HeaderAccessor to get Authentication.
        Long userId = 1L; // Placeholder
        
        battleService.handleAction(userId, action);
    }
}

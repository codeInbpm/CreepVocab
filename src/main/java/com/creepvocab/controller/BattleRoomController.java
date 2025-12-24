package com.creepvocab.controller;

import com.creepvocab.common.result.Result;
import com.creepvocab.entity.BattleRoom;
import com.creepvocab.service.BattleService;
import com.creepvocab.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/battle/room")
public class BattleRoomController {

    private final BattleService battleService;
    private final MatchService matchService;

    public BattleRoomController(BattleService battleService, MatchService matchService) {
        this.battleService = battleService;
        this.matchService = matchService;
    }

    @Operation(summary = "Create Room")
    @PostMapping("/create")
    public Result<BattleRoom> create(@RequestParam String type, @RequestParam boolean aiMode) {
        // Assuming userId 1 for demo
        return Result.success(battleService.createRoom(1L, type, aiMode));
    }

    @Operation(summary = "Join Match Queue")
    @PostMapping("/match/join")
    public Result<String> joinMatch(@RequestParam String mode) {
        matchService.addToQueue(1L, mode);
        return Result.success("Joined queue");
    }
}

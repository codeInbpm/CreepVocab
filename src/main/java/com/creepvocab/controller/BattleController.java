package com.creepvocab.controller;

import com.creepvocab.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Battle", description = "Battle API")
@RestController
@RequestMapping("/battle")
public class BattleController {

    @Operation(summary = "Start Matching")
    @PostMapping("/match")
    public Result<String> match() {
        return Result.success("Match started");
    }
}

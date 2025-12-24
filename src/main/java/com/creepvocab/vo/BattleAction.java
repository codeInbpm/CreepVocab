package com.creepvocab.vo;

import lombok.Data;

@Data
public class BattleAction {
    private String type; // ready, answer, emote, leave
    private String roomId;
    private Object payload;
}

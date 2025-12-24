package com.creepvocab.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("battle_record")
public class BattleRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long user1Id;
    private Long user2Id; // Can be null for AI/Matching
    private Long winnerId;
    private Integer score1;
    private Integer score2;
    private LocalDateTime battleTime;
    private String mode; // random, friend, ai
}

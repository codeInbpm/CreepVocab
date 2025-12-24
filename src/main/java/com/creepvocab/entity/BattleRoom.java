package com.creepvocab.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "battle_room", autoResultMap = true)
public class BattleRoom {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String roomCode;
    private Long creatorId;
    private Long player1Id;
    private Long player2Id;
    private String wordBook;
    private Integer questionCount;
    private String status; // waiting, playing, finished, leave
    private Integer currentIndex;
    
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> questions;
    
    private Boolean aiMode;
    private LocalDateTime createTime;
    private LocalDateTime startTime;
}

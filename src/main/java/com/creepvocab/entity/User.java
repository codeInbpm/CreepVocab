package com.creepvocab.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String openid;
    private String wxSessionKey;
    private String phone;
    private String nickname;
    private String avatar;
    private Integer coins;
    private Integer streak; // Check-in streak
    private Integer level;
    
    private Integer wordPower;
    private Integer challengeHighScore;
    private Integer totalBattles;
    private Integer winCount;
    private Integer hintCards;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

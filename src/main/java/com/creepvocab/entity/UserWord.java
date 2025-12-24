package com.creepvocab.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_word")
public class UserWord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long wordId;
    private Integer mastery; // Mastery level (0-100) or count of correct answers
    private LocalDateTime reviewTime;
    private LocalDateTime nextReviewTime; // For Spaced Repetition (optional)
}

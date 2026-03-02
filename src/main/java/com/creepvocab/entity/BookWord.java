package com.creepvocab.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("book_word")
public class BookWord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookId;
    private Long wordId;
}

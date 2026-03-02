package com.creepvocab.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("book_article")
public class BookArticle {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookId;
    private String title;
    private String durationStr;
    private String sizeStr;
    private LocalDate publishDate;
    private Integer sortOrder;
}

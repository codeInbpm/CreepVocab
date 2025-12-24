package com.creepvocab.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("course_pack")
public class CoursePack {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String subTitle;
    private String coverColor;
    private String category;
    private Integer wordCount;
    private Integer price;
}

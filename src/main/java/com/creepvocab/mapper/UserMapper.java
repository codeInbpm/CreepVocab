package com.creepvocab.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creepvocab.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {}


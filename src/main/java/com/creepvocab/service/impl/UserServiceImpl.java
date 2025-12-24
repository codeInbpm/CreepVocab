package com.creepvocab.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creepvocab.entity.User;
import com.creepvocab.mapper.UserMapper;
import com.creepvocab.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}

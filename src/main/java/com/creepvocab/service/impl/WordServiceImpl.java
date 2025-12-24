package com.creepvocab.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creepvocab.entity.Word;
import com.creepvocab.mapper.WordMapper;
import com.creepvocab.service.WordService;
import org.springframework.stereotype.Service;

@Service
public class WordServiceImpl extends ServiceImpl<WordMapper, Word> implements WordService {
}

package com.creepvocab.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creepvocab.entity.BattleRecord;
import com.creepvocab.mapper.BattleRecordMapper;
import com.creepvocab.service.BattleRecordService;
import org.springframework.stereotype.Service;

@Service
public class BattleRecordServiceImpl extends ServiceImpl<BattleRecordMapper, BattleRecord> implements BattleRecordService {
}

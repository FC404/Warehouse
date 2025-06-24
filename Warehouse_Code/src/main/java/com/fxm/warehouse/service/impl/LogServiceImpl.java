package com.fxm.warehouse.service.impl;

import com.fxm.warehouse.mapper.LogMapper;
import com.fxm.warehouse.pojo.entity.OperateLog;
import com.fxm.warehouse.pojo.entity.PageBean;
import com.fxm.warehouse.service.LogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogMapper logMapper;


    @Override
    public PageBean<OperateLog> log(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<OperateLog> logList = logMapper.log();
        PageInfo<OperateLog> pageInfo = new PageInfo<>(logList);
        return new PageBean<>(pageInfo.getTotal(), pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

}

package com.fxm.warehouse.service;

import com.fxm.warehouse.pojo.entity.OperateLog;
import com.fxm.warehouse.pojo.entity.PageBean;

public interface LogService {


    PageBean<OperateLog> log(Integer pageNum, Integer pageSize);
}

package com.fxm.warehouse.mapper;

import com.fxm.warehouse.pojo.entity.OperateLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LogMapper {

    @Select("SELECT * FROM operate_log ORDER BY operate_time DESC")
    List<OperateLog> log();
}

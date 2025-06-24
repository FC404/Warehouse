package com.fxm.warehouse.controller;


import com.fxm.warehouse.pojo.entity.OperateLog;
import com.fxm.warehouse.pojo.entity.PageBean;
import com.fxm.warehouse.pojo.entity.Result;
import com.fxm.warehouse.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/log")
public class LogCollector {

    @Autowired
    private LogService logService;

    @GetMapping("/list")
    public Result<PageBean<OperateLog>> log(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        // 调用 service 进行分页查询
        PageBean<OperateLog> pageBean = logService.log(pageNum, pageSize);
        return Result.success(pageBean);
    }

}

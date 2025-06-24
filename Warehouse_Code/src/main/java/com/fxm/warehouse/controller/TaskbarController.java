package com.fxm.warehouse.controller;


import com.fxm.warehouse.anno.Log;
import com.fxm.warehouse.pojo.entity.PageBean;
import com.fxm.warehouse.pojo.entity.Result;
import com.fxm.warehouse.pojo.entity.Taskbar;
import com.fxm.warehouse.service.TaskbarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/taskbar")
@Slf4j
public class TaskbarController {
    @Autowired
    private TaskbarService taskbarService;

    /**
     * 分页查询订单
     */
    @GetMapping("/list")
    public Result<PageBean<Taskbar>> getAllOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String taskname,
            @RequestParam(required = false) String status
    ) {
        // 调用 service 进行查询
        PageBean<Taskbar> pageBean = taskbarService.page(pageNum, pageSize, taskname, status);
        return Result.success(pageBean);
    }

    // 根据 ID 获取任务
    @GetMapping("/{id}")
    public Taskbar getTaskById(@PathVariable Integer id) {
        return taskbarService.getTaskById(id);
    }

    // 添加任务
    @Log
    @PostMapping("/add")
    public String addTask(@RequestBody Taskbar taskbar) {
        int result = taskbarService.addTask(taskbar);
        return result > 0 ? "任务添加成功" : "任务添加失败";
    }

    // 更新任务
    @Log
    @PutMapping("/update")
    public String updateTask(@RequestBody Taskbar taskbar) {
        int result = taskbarService.updateTask(taskbar);
        return result > 0 ? "任务更新成功" : "任务更新失败";
    }

    // 批量删除任务
    @PostMapping("/deleteBatch")
    @Log
    public ResponseEntity<String> deleteTasks(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body("未提供要删除的任务 ID");
        }
        int result = taskbarService.deleteTasks(ids);
        return result > 0 ? ResponseEntity.ok("批量删除成功") : ResponseEntity.status(400).body("批量删除失败");
    }



}

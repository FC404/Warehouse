package com.fxm.warehouse.service.impl;

import com.fxm.warehouse.mapper.TaskbarMapper;
import com.fxm.warehouse.pojo.entity.PageBean;
import com.fxm.warehouse.pojo.entity.Taskbar;
import com.fxm.warehouse.service.TaskbarService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskbarServiceImpl implements TaskbarService {
    @Autowired
    private TaskbarMapper taskbarMapper;

    @Override
    public PageBean<Taskbar> page(Integer pageNum, Integer pageSize, String taskname,String status) {
        PageHelper.startPage(pageNum, pageSize);
        List<Taskbar> taskList = taskbarMapper.page(taskname,status);
        PageInfo<Taskbar> pageInfo = new PageInfo<>(taskList);
        return new PageBean<>(pageInfo.getTotal(), pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public Taskbar getTaskById(Integer id) {
        return taskbarMapper.getTaskById(id);
    }

    @Override
    public int addTask(Taskbar taskbar) {
        return taskbarMapper.addTask(taskbar);
    }

    @Override
    public int updateTask(Taskbar taskbar) {
        return taskbarMapper.updateTask(taskbar);
    }

    @Override
    public int deleteTasks(List<Integer> ids) {
        return taskbarMapper.deleteTasks(ids);
    }

}

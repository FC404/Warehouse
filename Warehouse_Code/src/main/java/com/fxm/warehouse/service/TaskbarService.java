package com.fxm.warehouse.service;

import com.fxm.warehouse.pojo.entity.PageBean;
import com.fxm.warehouse.pojo.entity.Taskbar;

import java.util.List;

public interface TaskbarService {
    PageBean<Taskbar> page(Integer pageNum, Integer pageSize, String taskname,String status);

    Taskbar getTaskById(Integer id);

    int addTask(Taskbar taskbar);

    int updateTask(Taskbar taskbar);


    int deleteTasks(List<Integer> ids);
}

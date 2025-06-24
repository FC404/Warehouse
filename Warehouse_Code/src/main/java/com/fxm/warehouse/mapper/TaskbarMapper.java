package com.fxm.warehouse.mapper;


import com.fxm.warehouse.pojo.entity.Taskbar;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskbarMapper {

    List<Taskbar> page(String taskname,String status);

    Taskbar getTaskById(Integer id);

    int addTask(Taskbar taskbar);

    int updateTask(Taskbar taskbar);


    int deleteTasks(List<Integer> ids);
}

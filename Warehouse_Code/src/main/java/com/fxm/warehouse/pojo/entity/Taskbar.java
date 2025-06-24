package com.fxm.warehouse.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Taskbar {
    private Long id;
    private String taskname;
    private String status;
}

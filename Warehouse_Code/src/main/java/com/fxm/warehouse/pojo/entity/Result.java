package com.fxm.warehouse.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private  Integer code;//0:成功；1：失败
    private String msg;//提示信息
    private T data;//响应数据

    //成功的返回（带响应数据）
    public static <E> Result<E> success(E data){
        return new Result<>(0,"succ", data);
    }
    //成功的返回（无响应数据）
    public static Result success(){
        return new Result(0,"succ",null);
    }
    //失败返回
    public static Result error(String message){
        return new Result(1, message, null);
    }
}
package com.liuqiya.dormitoryrepairsystem.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;//业务状态码0-成功,1-失败
    private String message;//提示信息
    private T data;//响应数据

    //三种响应结果
    //1.响应成功,带响应数据
    public static <E>Result<E>success(E data){
        return new Result<>(0,"success",data);
    }
    //2.响应成功,不带响应数据
    public static <E>Result <E>success(){
        return new Result<>(0,"success",null);
    }
    //3.响应失败,带回提示信息
    public static <E>Result<E>error(String message){
        return new Result<>(1,message,null);
    }

}
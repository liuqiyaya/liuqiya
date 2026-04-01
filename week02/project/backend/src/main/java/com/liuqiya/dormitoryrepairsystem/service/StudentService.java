package com.liuqiya.dormitoryrepairsystem.service;

import com.liuqiya.dormitoryrepairsystem.pojo.DeviceType;
import com.liuqiya.dormitoryrepairsystem.pojo.Dormitory;
import com.liuqiya.dormitoryrepairsystem.pojo.RepairOrder;
import com.liuqiya.dormitoryrepairsystem.pojo.User;

import java.util.List;

public interface StudentService {
    //查询所有宿舍列表
    List<Dormitory> findAllDormitory();

    //根据学号查询用户信息
    User findByAccount(String account);

    //绑定或修改宿舍
    void bindOrUpdateDormitory(String account, String building, String room);

    //查询所有设备类型
    List<DeviceType> findAllDeviceType();

    //创建报修单
    void creatRepairOrder(RepairOrder repairOrder);

    //查询用户报修单列表
    List<RepairOrder> findUserRepairOrder(Integer studentId);

    //根据报修单ID查询报修单
    RepairOrder findRepairOrder(Integer id);

    //取消报修单
    void cancelRepairOrder(Integer id);

}


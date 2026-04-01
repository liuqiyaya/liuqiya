package com.liuqiya.dormitoryrepairsystem.service;

import com.liuqiya.dormitoryrepairsystem.pojo.RepairOrder;
import com.liuqiya.dormitoryrepairsystem.pojo.Result;

import java.util.List;


public interface AdminService {
    //查询所有报修单
    List<RepairOrder> findAllRepairOrder();

    //根据报修单ID查询报修单
    RepairOrder findRepairOrder(Integer id);

    //更新报修单状态
    void updateStatus(Integer id, String status);

    //删除报修单
    Result<String> deleteRepairOrder(Integer id);

    //按状态筛选
    List<RepairOrder> findRepairOrdersByStatus(String status);
}

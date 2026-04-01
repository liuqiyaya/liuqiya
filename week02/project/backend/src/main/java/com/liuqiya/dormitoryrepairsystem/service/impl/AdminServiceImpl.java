package com.liuqiya.dormitoryrepairsystem.service.impl;

import com.liuqiya.dormitoryrepairsystem.mapper.RepairOrderMapper;
import com.liuqiya.dormitoryrepairsystem.pojo.RepairOrder;
import com.liuqiya.dormitoryrepairsystem.pojo.Result;
import com.liuqiya.dormitoryrepairsystem.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private RepairOrderMapper repairOrderMapper;

    //查询所有报修单
    @Override
    public List<RepairOrder> findAllRepairOrder(){
        return repairOrderMapper.findAll();
    }

    //按状态筛选
    @Override
    public List<RepairOrder> findRepairOrdersByStatus(String status) {
        return repairOrderMapper.findByStatus(status);
    }


    //根据报修单ID查询报修单
    @Override
    public RepairOrder findRepairOrder(Integer id){
        return repairOrderMapper.selectById(id);
    }

    //更新报修单状态
    @Override
   public void updateStatus(Integer id, String status){
         repairOrderMapper.updateStatus(id,status);
    }

    //删除报修单
    @Override
    public Result<String> deleteRepairOrder(Integer id) {
        // 检查报修单是否存在
        RepairOrder repairOrder = repairOrderMapper.selectById(id);
        if (repairOrder == null) {
            return Result.error("报修单不存在");
        }

        // 检查状态是否允许删除
        if (!"待处理".equals(repairOrder.getStatus())) {
            return Result.error("只能删除待处理状态的报修单");
        }

        // 执行删除
        repairOrderMapper.deleteById(id);
        return Result.success("删除成功");
    }
}

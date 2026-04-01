package com.liuqiya.dormitoryrepairsystem.controller;

import com.liuqiya.dormitoryrepairsystem.pojo.RepairOrder;
import com.liuqiya.dormitoryrepairsystem.pojo.Result;
import com.liuqiya.dormitoryrepairsystem.service.AdminService;
import com.liuqiya.dormitoryrepairsystem.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    //查看所有报修单
    @GetMapping("/repairs")
    public Result<List<RepairOrder>> findAllRepairOrder() {
        List<RepairOrder> allRepairOrderList=adminService.findAllRepairOrder();
        return Result.success(allRepairOrderList);
    }

    //按状态筛选
    @GetMapping("/repairs/status")
    public Result<List<RepairOrder>> findRepairOrdersByStatus(@RequestParam String status) {
        List<RepairOrder> repairOrderList = adminService.findRepairOrdersByStatus(status);
        return Result.success(repairOrderList);
    }

    //查看单个报修单详情
    @GetMapping("/repairs/{id}")
    public Result<RepairOrder>findRepairOrder(@PathVariable Integer id){
        RepairOrder repairOrder=adminService.findRepairOrder(id);
        return Result.success(repairOrder);
    }

    // 修改报修单状态
    @PostMapping("/repairs/{id}")
    public Result<String> updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> map) {
        String status = map.get("status");

        // 验证状态值是否合法
        List<String> validStatuses = Arrays.asList("待处理", "处理中", "已完成", "已取消");
        if (!validStatuses.contains(status)) {
            return Result.error("无效的状态值，合法状态为：待处理、处理中、已完成、已取消");
        }

        // 检查报修单是否存在
        RepairOrder repairOrder = adminService.findRepairOrder(id);
        if (repairOrder == null) {
            return Result.error("报修单不存在");
        }

        // 检查状态转换是否合法
        if ("已取消".equals(repairOrder.getStatus()) || "已完成".equals(repairOrder.getStatus())) {
            return Result.error("已取消或已完成的报修单不能修改状态");
        }

        adminService.updateStatus(id, status);
        return Result.success("修改成功");
    }

    // 删除报修单
    @DeleteMapping("/repairs/{id}")
    public Result<String> deleteRepairOrder(@PathVariable Integer id){
        return adminService.deleteRepairOrder(id);
    }

}

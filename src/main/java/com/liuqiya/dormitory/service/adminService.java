package com.liuqiya.dormitory.service;

import com.liuqiya.dormitory.entity.RepairOrder;
import com.liuqiya.dormitory.mapper.RepairOrderMapper;
import com.liuqiya.dormitory.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class adminService {

    // 查看所有报修单
    public List<RepairOrder> getAllRepairs() {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            return mapper.findAll();
        }
    }

    // 按状态筛选报修单
    public List<RepairOrder> getRepairsByStatus(String status) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            return mapper.findByStatus(status);
        }
    }

    // 查看报修单详情
    public RepairOrder getRepairDetail(int orderId) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            return mapper.findById(orderId);
        }
    }

    // 修改报修单状态
    public String updateStatus(int orderId, String status) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            int result = mapper.updateStatus(orderId, status);
            session.commit();
            if (result > 0) {
                return "success";
            }
            return "修改失败，报修单不存在";
        } catch (Exception e) {
            return "修改失败：" + e.getMessage();
        }
    }

    // 删除报修单
    public String deleteRepair(int orderId) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            int result = mapper.deleteById(orderId);
            session.commit();
            if (result > 0) {
                return "success";
            }
            return "删除失败，报修单不存在";
        } catch (Exception e) {
            return "删除失败：" + e.getMessage();
        }
    }
}
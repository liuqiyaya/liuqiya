package com.liuqiya.dormitory.service;

import com.liuqiya.dormitory.entity.DeviceType;
import com.liuqiya.dormitory.entity.RepairOrder;
import com.liuqiya.dormitory.mapper.DeviceTypeMapper;
import com.liuqiya.dormitory.mapper.RepairOrderMapper;
import com.liuqiya.dormitory.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class repairService {

    // 获取所有设备类型
    public List<DeviceType> getAllDeviceTypes() {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            DeviceTypeMapper mapper = session.getMapper(DeviceTypeMapper.class);
            return mapper.findAll();
        }
    }

    // 创建报修单
    public String createRepairOrder(int studentId, String dormBuilding, String dormRoom, int deviceTypeId, String description) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            RepairOrder order = new RepairOrder();
            order.setStudentId(studentId);
            order.setDormBuilding(dormBuilding);
            order.setDormRoom(dormRoom);
            order.setDeviceTypeId(deviceTypeId);
            order.setDescription(description);
            order.setStatus("待处理");
            mapper.insert(order);
            session.commit();
            return "success";
        } catch (Exception e) {
            return "创建失败：" + e.getMessage();
        }
    }

    // 查看学生自己的报修记录
    public List<RepairOrder> getMyRepairs(int studentId) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            return mapper.findByStudentId(studentId);
        }
    }

    // 取消报修单
    public String cancelRepair(int orderId) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            int result = mapper.cancelById(orderId);
            session.commit();
            if (result > 0) {
                return "success";
            }
            return "取消失败，只能取消'待处理'的报修单";
        } catch (Exception e) {
            return "取消失败：" + e.getMessage();
        }
    }
}
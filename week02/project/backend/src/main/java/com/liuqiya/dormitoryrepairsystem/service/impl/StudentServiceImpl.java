package com.liuqiya.dormitoryrepairsystem.service.impl;

import com.liuqiya.dormitoryrepairsystem.mapper.DeviceTypeMapper;
import com.liuqiya.dormitoryrepairsystem.mapper.DormitoryMapper;
import com.liuqiya.dormitoryrepairsystem.mapper.RepairOrderMapper;
import com.liuqiya.dormitoryrepairsystem.mapper.UserMapper;
import com.liuqiya.dormitoryrepairsystem.pojo.DeviceType;
import com.liuqiya.dormitoryrepairsystem.pojo.Dormitory;
import com.liuqiya.dormitoryrepairsystem.pojo.RepairOrder;
import com.liuqiya.dormitoryrepairsystem.pojo.User;
import com.liuqiya.dormitoryrepairsystem.service.StudentService;
import com.liuqiya.dormitoryrepairsystem.utils.Md5Util;
import com.liuqiya.dormitoryrepairsystem.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    public DormitoryMapper dormitoryMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    DeviceTypeMapper deviceTypeMapper;
    @Autowired
    RepairOrderMapper repairOrderMapper;


    //根据学号查询用户信息
    @Override
    public User findByAccount(String account){
        return userMapper.findByAccount(account);
    }

    //查询所有宿舍列表
    @Override
    public List<Dormitory>  findAllDormitory(){
        return dormitoryMapper.selectAll();
    }

    //绑定或修改宿舍
    @Override
    public void bindOrUpdateDormitory(String account,String building,String room){
        userMapper.bindOrUpdateDormitoryByAccount(account,building,room);
    }

    //查询所有设备类型
    @Override
    public List<DeviceType>  findAllDeviceType(){
        return deviceTypeMapper.selectAll();
    }

    //创建报修单
    @Override
    public void  creatRepairOrder(RepairOrder repairOrder){
        repairOrderMapper.creatRepairOrder(repairOrder);
    }

    //查询用户报修单
    @Override
    public List<RepairOrder> findUserRepairOrder(Integer studentId){
        return repairOrderMapper.findUserRepairOrder(studentId);
    }

    //根据报修单ID查询报修单
    @Override
    public RepairOrder findRepairOrder(Integer id){
        return repairOrderMapper.selectById(id);
    }

    //取消报修单
    @Override
    public void cancelRepairOrder(Integer id){
        repairOrderMapper.updateStatusToCancel(id);
    }

}


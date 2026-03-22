package com.liuqiya.dormitory.mapper;

import com.liuqiya.dormitory.entity.RepairOrder;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface RepairOrderMapper {

    @Insert("INSERT INTO repairOrder (studentId, dormBuilding, dormRoom, deviceTypeId, description, status) VALUES (#{studentId}, #{dormBuilding}, #{dormRoom}, #{deviceTypeId}, #{description}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RepairOrder order);

    @Select("SELECT r.id, r.studentId, r.dormBuilding, r.dormRoom, r.deviceTypeId, r.description, r.status, r.createTime, r.updateTime, u.name as studentName, u.account as studentAccount, d.typeName as deviceTypeName FROM repairOrder r LEFT JOIN user u ON r.studentId = u.id LEFT JOIN deviceType d ON r.deviceTypeId = d.id ORDER BY r.createTime DESC")
    List<RepairOrder> findAll();

    @Select("SELECT r.id, r.studentId, r.dormBuilding, r.dormRoom, r.deviceTypeId, r.description, r.status, r.createTime, r.updateTime, u.name as studentName, u.account as studentAccount, d.typeName as deviceTypeName FROM repairOrder r LEFT JOIN user u ON r.studentId = u.id LEFT JOIN deviceType d ON r.deviceTypeId = d.id WHERE r.id = #{id}")
    RepairOrder findById(Integer id);

    @Update("UPDATE repairOrder SET status = #{status}, updateTime = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status);

    @Delete("DELETE FROM repairOrder WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT r.id, r.studentId, r.dormBuilding, r.dormRoom, r.deviceTypeId, r.description, r.status, r.createTime, r.updateTime, u.name as studentName, u.account as studentAccount, d.typeName as deviceTypeName FROM repairOrder r LEFT JOIN user u ON r.studentId = u.id LEFT JOIN deviceType d ON r.deviceTypeId = d.id WHERE r.studentId = #{studentId} ORDER BY r.id ASC")
    List<RepairOrder> findByStudentId(Integer studentId);

    @Update("UPDATE repairOrder SET status = '已取消', updateTime = NOW() WHERE id = #{id} AND status = '待处理'")
    int cancelById(Integer id);

    @Select("SELECT r.id, r.studentId, r.dormBuilding, r.dormRoom, r.deviceTypeId, r.description, r.status, r.createTime, r.updateTime, u.name as studentName, u.account as studentAccount, d.typeName as deviceTypeName FROM repairOrder r LEFT JOIN user u ON r.studentId = u.id LEFT JOIN deviceType d ON r.deviceTypeId = d.id WHERE r.status = #{status} ORDER BY r.createTime DESC")
    List<RepairOrder> findByStatus(String status);
}

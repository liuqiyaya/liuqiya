package com.liuqiya.dormitoryrepairsystem.mapper;

import com.liuqiya.dormitoryrepairsystem.pojo.RepairOrder;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Mapper
public interface RepairOrderMapper {

    //创建报修单
    @Insert("insert into repair_order(student_id,dorm_building,dorm_room,"+
            "device_type_id,problem_description,image_url,status)values (#{studentId},"+
            "#{dormBuilding},#{dormRoom},#{deviceTypeId},#{problemDescription},"+
            "#{imageUrl},#{status})")
    void creatRepairOrder(RepairOrder repairOrder);

    //查询用户报修单列表
    @Select("select * from repair_order where student_id=#{studentId}")
    List<RepairOrder> findUserRepairOrder(@Param("studentId")Integer studentId);

    //查询所有报修单
    @Select("select * from repair_order")
    List<RepairOrder> findAll();

    //取消报修单
    @Update("update repair_order set status='已取消' where id=#{id}")
    void updateStatusToCancel(@Param("id")Integer id);

    //根据报修单ID查询报修单
    @Select("select * from repair_order where id=#{id}")
    RepairOrder selectById(@Param("id")Integer id);

    //更新报修单状态
    @Update("update repair_order set status=#{status} where id=#{id}")
    void updateStatus(@Param("id")Integer id,@Param("status")String status);

    //根据报修单ID删除报修单
    @Delete("delete from repair_order where id=#{id}")
    void deleteById(@Param("id")Integer id);

    //按状态筛选
    @Select("select * from repair_order where status=#{status}")
    List<RepairOrder> findByStatus(@Param("status")String status);
}

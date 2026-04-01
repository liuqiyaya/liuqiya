package com.liuqiya.dormitoryrepairsystem.mapper;

import com.liuqiya.dormitoryrepairsystem.pojo.DeviceType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface DeviceTypeMapper {

    //查询所有设备类型
    @Select("select * from device_type")
    List<DeviceType> selectAll();
}

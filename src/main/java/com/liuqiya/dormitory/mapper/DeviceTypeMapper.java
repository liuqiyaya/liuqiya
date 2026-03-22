package com.liuqiya.dormitory.mapper;

import com.liuqiya.dormitory.entity.DeviceType;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface DeviceTypeMapper {

    @Select("SELECT id, typeName FROM deviceType")
    List<DeviceType> findAll();

    @Select("SELECT id, typeName FROM deviceType WHERE id = #{id}")
    DeviceTypeMapper findById(Integer id);
}
package com.liuqiya.dormitoryrepairsystem.mapper;

import com.liuqiya.dormitoryrepairsystem.pojo.Dormitory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface DormitoryMapper {

    //查询所有宿舍列表
    @Select("select * from dormitory")
    List<Dormitory> selectAll();
}

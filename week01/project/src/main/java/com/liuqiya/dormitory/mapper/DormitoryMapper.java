package com.liuqiya.dormitory.mapper;

import com.liuqiya.dormitory.entity.Dormitory;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface DormitoryMapper {

    @Select("SELECT building, room FROM dormitory")
    List<Dormitory> findAll();
}
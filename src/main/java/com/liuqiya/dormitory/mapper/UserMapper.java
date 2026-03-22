package com.liuqiya.dormitory.mapper;

import com.liuqiya.dormitory.entity.User;
import org.apache.ibatis.annotations.*;

public interface UserMapper {

    @Select("SELECT id, account, password, role, name, dormBuilding, dormRoom, createdTime FROM user WHERE account = #{account}")
    User findByAccount(String account);

    @Insert("INSERT INTO user (account, password, role, name, dormBuilding, dormRoom) VALUES (#{account}, #{password}, #{role}, #{name}, #{dormBuilding}, #{dormRoom})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Integer id, @Param("password") String password);

    @Update("UPDATE user SET dormBuilding = #{dormBuilding}, dormRoom = #{dormRoom} WHERE id = #{id}")
    int updateDorm(@Param("id") Integer id, @Param("dormBuilding") String dormBuilding, @Param("dormRoom") String dormRoom);

    @Select("SELECT id, account, password, role, name, dormBuilding, dormRoom, createdTime FROM user WHERE id = #{id}")
    User findById(Integer id);
}

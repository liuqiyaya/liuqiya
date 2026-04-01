package com.liuqiya.dormitoryrepairsystem.mapper;

import com.liuqiya.dormitoryrepairsystem.pojo.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    //根据学号查询用户信息
    @Select("select * from user where account=#{account}")
    User findByAccount(@Param("account")String account) ;

    //添加用户
    @Insert("insert into user(account,password,role) values (#{account},#{password},#{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addUser(@Param("account") String account, @Param("password") String password, @Param("role") String role);

    //绑定或修改宿舍
    @Update("update user set dorm_building=#{building},dorm_room=#{room} where account=#{account}")
    void bindOrUpdateDormitoryByAccount(@Param("account") String account, @Param("building") String building, @Param("room") String room);

    //更新密码
    @Update("update user set password=#{md5String} where account=#{account}")
    void updatePwdByAccount(@Param("account")String account, @Param("md5String")String md5String);
}

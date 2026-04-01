package com.liuqiya.dormitoryrepairsystem.service;

import com.liuqiya.dormitoryrepairsystem.pojo.User;

public interface AuthService {
   //查询用户
    User findByAccount(String account);
    //注册
    void register(String account, String password,String role);
    //修改密码
    void updatePwd(String newPwd);
}


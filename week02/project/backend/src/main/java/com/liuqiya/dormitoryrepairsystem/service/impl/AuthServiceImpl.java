package com.liuqiya.dormitoryrepairsystem.service.impl;

import com.liuqiya.dormitoryrepairsystem.mapper.UserMapper;
import com.liuqiya.dormitoryrepairsystem.pojo.User;
import com.liuqiya.dormitoryrepairsystem.service.AuthService;
import com.liuqiya.dormitoryrepairsystem.utils.Md5Util;
import com.liuqiya.dormitoryrepairsystem.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class AuthServiceImpl implements AuthService {
   @Autowired
   private UserMapper userMapper;

   //查询用户
   @Override
   public User findByAccount(String account) {
      return userMapper.findByAccount(account);
   }

   //注册
   @Override
   public void register(String account, String password, String role) {
      // 1. 检查账号是否已存在
      User exist = userMapper.findByAccount(account);
      if (exist != null) {
         throw new RuntimeException("账号已存在");
      }

      // 2. 加密密码
      String md5String = Md5Util.getMD5String(password);

      // 3. 插入数据库
      userMapper.addUser(account, md5String, role);
   }


   //修改密码
   @Override
   public void updatePwd(String newPwd) {
      Map<String, Object> map = ThreadLocalUtil.get();
      String account = (String) map.get("account");
      String md5String = Md5Util.getMD5String(newPwd);
      userMapper.updatePwdByAccount(account, md5String);

   }
}

package com.liuqiya.dormitory.service;

import com.liuqiya.dormitory.entity.User;
import com.liuqiya.dormitory.mapper.UserMapper;
import com.liuqiya.dormitory.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

public class userService {

    // 注册
    public String register(String account, String password, String confirm, String role, String name) {
        if (!password.equals(confirm)) {
            return "两次密码不一致";
        }

        if (role.equals("student") && !account.matches("^(3125|3225)\\d+$")) {
            return "学号必须以3125或3225开头,注册失败";
        }
        if (role.equals("admin") && !account.matches("^0025\\d+$")) {
            return "工号必须以0025开头,注册失败";
        }

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            if (mapper.findByAccount(account) != null) {
                return "账号已存在";
            }

            User user = new User();
            user.setAccount(account);
            user.setPassword(password);
            user.setRole(role);
            user.setName(name);
            mapper.insert(user);
            session.commit();
            return "success";
        } catch (Exception e) {
            return "注册失败：" + e.getMessage();
        }
    }

    // 登录
    public User login(String account, String password) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.findByAccount(account);
            if (user != null && user.getPassword().equals(password)) {
                return user;
            }
            return null;
        }
    }

    // 修改密码
    public String changePassword(int userId, String oldPassword, String newPassword, String confirm) {
        if (!newPassword.equals(confirm)) {
            return "两次新密码不一致";
        }

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.findById(userId);
            if (user == null || !user.getPassword().equals(oldPassword)) {
                return "原密码错误";
            }

            mapper.updatePassword(userId, newPassword);
            session.commit();
            return "success";
        } catch (Exception e) {
            return "修改失败：" + e.getMessage();
        }
    }

    // 绑定/修改宿舍
    public String bindDormitory(int userId, String building, String room) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            mapper.updateDorm(userId, building, room);
            session.commit();
            return "success";
        } catch (Exception e) {
            return "绑定失败：" + e.getMessage();
        }
    }

    // 获取用户信息
    public User getUserById(int userId) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            return mapper.findById(userId);
        }
    }
}
package com.liuqiya.dormitory.entity;

import java.util.Date;

public class User {
    private Integer id;
    private String account;
    private  String password;
    private  String role;
    private String name;
    private String dormBuilding;
    private String dormRoom;
    private Date createdTime;

    public User() {
    }


public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDormBuilding() {
        return dormBuilding;
    }

    public void setDormBuilding(String dormBuilding) {
        this.dormBuilding = dormBuilding;
    }

    public String getDormRoom() {
        return dormRoom;
    }

    public void setDormRoom(String dormRoom) {
        this.dormRoom = dormRoom;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    // 获取完整宿舍地址
    public String getFullDorm() {
        if (dormBuilding != null && dormRoom != null) {
            return dormBuilding + dormRoom;
        }
        return "未绑定";
    }

    @Override
    public String toString() {
        return "账号: " + account + ", 姓名: " + name + ", 角色: " + role;
    }
}


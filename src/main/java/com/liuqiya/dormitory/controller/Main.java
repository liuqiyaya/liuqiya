package com.liuqiya.dormitory.controller;

import com.liuqiya.dormitory.entity.*;
import com.liuqiya.dormitory.service.*;
import com.liuqiya.dormitory.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner sc = new Scanner(System.in);

    private static User currentUser = null;
    private static userService userService = new userService();
    private static repairService repairService = new repairService();
    private static adminService adminService = new adminService();

    public static void main(String[] args) {
        while (true) {
            if (currentUser == null) {
                showMainMenu();
            } else if ("student".equals(currentUser.getRole())) {
                showStudentMenu();
            } else {
                showAdminMenu();
            }
        }
    }

    // 主菜单
    private static void showMainMenu() {
        System.out.println("\n===========================");
        System.out.println("🏠 宿舍报修管理系统");
        System.out.println("===========================");
        System.out.println("1. 登录");
        System.out.println("2. 注册");
        System.out.println("3. 退出");
        System.out.print("请选择操作（输入 1-3）：");
        String choice = sc.next();

        switch (choice) {
            case "1":
                login();
                break;
            case "2":
                register();
                break;
            case "3":
                System.out.println("再见！");
                System.exit(0);
                break;
            default:
                System.out.println("输入错误，请重新选择");
        }
    }

    // 登录
    private static void login() {
        System.out.println("\n===== 用户登录 =====");
        System.out.print("请输入账号：");
        String account = sc.next();
        System.out.print("请输入密码：");
        String password = sc.next();

        User user = userService.login(account, password);
        if (user != null) {
            currentUser = user;
            System.out.println("登录成功！角色：" + ("student".equals(user.getRole()) ? "学生" : "维修人员"));
        } else {
            System.out.println("账号或密码错误,请重新登录！");
        }
    }

    // 注册
    private static void register() {
        System.out.println("\n===== 用户注册 =====");
        System.out.print("请选择角色（1-学生，2-维修人员）：");
        String roleChoice = sc.next();
        String role = roleChoice.equals("1") ? "student" : "admin";

        System.out.print("请输入账号（学生以3125或3225开头，管理员以0025开头）：");
        String account = sc.next();
        System.out.print("请输入姓名：");
        String name = sc.next();
        System.out.print("请输入密码：");
        String password = sc.next();
        System.out.print("请确认密码：");
        String confirm = sc.next();

        String result = userService.register(account, password, confirm, role, name);
        if ("success".equals(result)) {
            System.out.println("注册成功！请返回主界面登录。");
        } else {
            System.out.println(result);
        }
    }

    // 学生菜单
    private static void showStudentMenu() {
        System.out.println("\n===== 学生菜单 =====");
        System.out.println("1. 绑定/修改宿舍");
        System.out.println("2. 创建报修单");
        System.out.println("3. 查看我的报修记录");
        System.out.println("4. 取消报修单");
        System.out.println("5. 修改密码");
        System.out.println("6. 退出");
        System.out.print("请选择操作（输入 1-6）：");
        String choice = sc.next();

        switch (choice) {
            case "1":
                bindDormitory();
                break;
            case "2":
                createRepair();
                break;
            case "3":
                viewMyRepairs();
                break;
            case "4":
                cancelRepair();
                break;
            case "5":
                changePassword();
                break;
            case "6":
                currentUser = null;
                System.out.println("已退出登录");
                break;
            default:
                System.out.println("输入错误，请重新选择");
        }
    }

    // 绑定/修改宿舍
    private static void bindDormitory() {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            com.liuqiya.dormitory.mapper.DormitoryMapper dormMapper =
                    session.getMapper(com.liuqiya.dormitory.mapper.DormitoryMapper.class);
            List<Dormitory> list = dormMapper.findAll();
            if (list.isEmpty()) {
                System.out.println("暂无宿舍可选");
                return;
            }

            System.out.println("\n可选宿舍：");
            for (int i = 0; i < list.size(); i++) {
                System.out.println((i + 1) + ". " + list.get(i).getBuilding() + list.get(i).getRoom());
            }
            System.out.print("请选择宿舍编号：");
            int index = sc.nextInt() - 1;
            sc.nextLine(); // 清除换行
            if (index < 0 || index >= list.size()) {
                System.out.println("无效选择");
                return;
            }

            Dormitory d = list.get(index);
            String result = userService.bindDormitory(currentUser.getId(), d.getBuilding(), d.getRoom());
            if ("success".equals(result)) {
                currentUser.setDormBuilding(d.getBuilding());
                currentUser.setDormRoom(d.getRoom());
                System.out.println("绑定成功！当前宿舍：" + d.getBuilding() + d.getRoom());
            } else {
                System.out.println(result);
            }
        }
    }

    // 创建报修单
    private static void createRepair() {
        if (currentUser.getDormBuilding() == null) {
            System.out.println("请先绑定宿舍！");
            return;
        }

        List<DeviceType> devices = repairService.getAllDeviceTypes();
        if (devices.isEmpty()) {
            System.out.println("暂无设备类型");
            return;
        }

        System.out.println("\n设备类型：");
        for (int i = 0; i < devices.size(); i++) {
            System.out.println((i + 1) + ". " + devices.get(i).getTypeName());
        }
        System.out.print("请选择设备类型：");
        int index = sc.nextInt() - 1;
        sc.nextLine();
        if (index < 0 || index >= devices.size()) {
            System.out.println("无效选择");
            return;
        }

        System.out.print("问题描述：");
        String desc = sc.nextLine();

        String result = repairService.createRepairOrder(
                currentUser.getId(),
                currentUser.getDormBuilding(),
                currentUser.getDormRoom(),
                devices.get(index).getId(),
                desc
        );
        System.out.println("success".equals(result) ? "报修单创建成功！" : result);
    }

    // 查看我的报修记录
    private static void viewMyRepairs() {
        List<RepairOrder> list = repairService.getMyRepairs(currentUser.getId());
        if (list.isEmpty()) {
            System.out.println("暂无报修记录");
            return;
        }

        System.out.println("\n===== 我的报修记录 =====");

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (RepairOrder r : list) {
            String timeStr = sdf.format(r.getCreateTime());
            System.out.println("编号: " + r.getId());
            System.out.println("设备类型: " + r.getDeviceTypeName());
            System.out.println("状态: " + r.getStatus());
            System.out.println("创建时间: " + timeStr);
            System.out.println("----------------------------------------");
        }
    }




    // 取消报修单
    private static void cancelRepair() {
        List<RepairOrder> list = repairService.getMyRepairs(currentUser.getId());
        if (list.isEmpty()) {
            System.out.println("暂无报修记录");
            return;
        }

        System.out.println("\n可取消的报修单：");
        for (RepairOrder r : list) {
            if ("待处理".equals(r.getStatus())) {
                System.out.println(r.getId() + ". " + r.getDeviceTypeName());
            }
        }
        System.out.print("请输入要取消的报修单号：");
        int id = sc.nextInt();
        String result = repairService.cancelRepair(id);
        System.out.println("success".equals(result) ? "取消成功" : result);
    }

    // 修改密码
    private static void changePassword() {
        System.out.println("\n===== 修改密码 =====");
        System.out.print("请输入原密码：");
        String oldPwd = sc.next();
        System.out.print("请输入新密码：");
        String newPwd = sc.next();
        System.out.print("请确认新密码：");
        String confirm = sc.next();

        String result = userService.changePassword(currentUser.getId(), oldPwd, newPwd, confirm);
        if ("success".equals(result)) {
            currentUser.setPassword(newPwd);
            System.out.println("密码修改成功！");
        } else {
            System.out.println(result);
        }
    }

    // 管理员菜单
    private static void showAdminMenu() {
        System.out.println("\n===== 管理员菜单 =====");
        System.out.println("1. 查看所有报修单");
        System.out.println("2. 查看报修单详情");
        System.out.println("3. 更新报修单状态");
        System.out.println("4. 删除报修单");
        System.out.println("5. 修改密码");
        System.out.println("6. 退出");
        System.out.print("请选择操作（输入 1-6）：");
        String choice = sc.next();

        switch (choice) {
            case "1":
                viewAllRepairs();
                break;
            case "2":
                viewRepairDetail();
                break;
            case "3":
                updateRepairStatus();
                break;
            case "4":
                deleteRepair();
                break;
            case "5":
                changePassword();
                break;
            case "6":
                currentUser = null;
                System.out.println("已退出登录");
                break;
            default:
                System.out.println("输入错误，请重新选择");
        }
    }

    // 查看所有报修单
    private static void viewAllRepairs() {
        List<RepairOrder> list = adminService.getAllRepairs();
        if (list.isEmpty()) {
            System.out.println("暂无报修单");
            return;
        }

        System.out.println("\n===== 所有报修单 =====");

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (RepairOrder r : list) {
            String timeStr = sdf.format(r.getCreateTime());
            System.out.println("编号: " + r.getId());
            System.out.println("学生: " + r.getStudentName() + " (" + r.getStudentAccount() + ")");
            System.out.println("宿舍: " + r.getFullDorm());
            System.out.println("设备: " + r.getDeviceTypeName());
            System.out.println("状态: " + r.getStatus());
            System.out.println("创建时间: " + timeStr);
            System.out.println("----------------------------------------");
        }
    }

    // 查看报修单详情
    private static void viewRepairDetail() {
        System.out.print("请输入报修单号：");
        int id = sc.nextInt();
        RepairOrder r = adminService.getRepairDetail(id);
        if (r == null) {
            System.out.println("报修单不存在");
            return;
        }

        System.out.println("\n===== 报修单详情 =====");
        System.out.println("报修单号：" + r.getId());
        System.out.println("学生：" + r.getStudentName() + " (" + r.getStudentAccount() + ")");
        System.out.println("宿舍：" + r.getFullDorm());
        System.out.println("设备：" + r.getDeviceTypeName());
        System.out.println("描述：" + r.getDescription());
        System.out.println("状态：" + r.getStatus());
        System.out.println("创建时间：" + r.getCreateTime());
        System.out.println("最后修改：" + r.getUpdateTime());
    }

    // 更新报修单状态
    private static void updateRepairStatus() {
        System.out.print("请输入报修单号：");
        int id = sc.nextInt();
        System.out.print("新状态（待处理/处理中/已完成/已取消）：");
        String status = sc.next();

        String result = adminService.updateStatus(id, status);
        System.out.println("success".equals(result) ? "状态修改成功" : result);
    }

    // 删除报修单
    private static void deleteRepair() {
        System.out.print("请输入报修单号：");
        int id = sc.nextInt();
        System.out.print("确认删除？(y/n)：");
        String confirm = sc.next();
        if (!"y".equalsIgnoreCase(confirm)) {
            System.out.println("已取消删除");
            return;
        }

        String result = adminService.deleteRepair(id);
        System.out.println("success".equals(result) ? "删除成功" : result);
    }
}
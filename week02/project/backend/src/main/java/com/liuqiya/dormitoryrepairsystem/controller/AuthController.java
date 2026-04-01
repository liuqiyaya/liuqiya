package com.liuqiya.dormitoryrepairsystem.controller;

import com.liuqiya.dormitoryrepairsystem.pojo.Result;
import com.liuqiya.dormitoryrepairsystem.pojo.User;
import com.liuqiya.dormitoryrepairsystem.service.AuthService;
import com.liuqiya.dormitoryrepairsystem.utils.JwtUtil;
import com.liuqiya.dormitoryrepairsystem.utils.Md5Util;
import com.liuqiya.dormitoryrepairsystem.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class AuthController {

    @Autowired
    private AuthService authService;//创建对象

    //注册
    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");
        String confirmPassword = params.get("confirmPassword");

        // 验证参数是否为空
        if (account == null || password == null || confirmPassword == null) {
            return Result.error("参数不能为空");
        }

        // 验证账号格式
        if (!account.matches("^(3125|3225|0025)\\d{1,12}$")) {
            return Result.error("账号格式不正确，学号前缀应为3125或3225，工号前缀应为0025");
        }
        // 验证密码长度
        if (password == null || password.length() < 5 || password.length() > 16) {
            return Result.error("密码长度应在5-16位之间");
        }

        // 验证两次密码是否一致
        if (!password.equals(confirmPassword)) {
            return Result.error("两次输入的密码不一致");
        }

        // 根据账号前缀判断角色
        String role;
        if (account.startsWith("3125") || account.startsWith("3225")) {
            role = "student"; // 学号前缀为3125或3225的是学生
        } else if (account.startsWith("0025")) {
            role = "admin"; // 工号前缀为0025的是维修人员
        } else {
            return Result.error("账号前缀不正确");
        }
        // 检查账号是否已经存在
        User u = authService.findByAccount(account);
        if (u != null) {
            return Result.error("账号已存在");
        }

// 账号不存在，执行注册
        try {
            authService.register(account, password, role);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("注册失败，请稍后重试");
        }
    }

    //登录
    @PostMapping("/login")
    public Result<String> login(@RequestBody Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");

        // 验证参数是否为空
        if (account == null || password == null) {
            return Result.error("参数不能为空");
        }

        // 验证账号格式
        if (!account.matches("^(3125|3225|0025)\\d{1,12}$")) {
            return Result.error("账号格式不正确，学号前缀应为3125或3225，工号前缀应为0025");
        }
        //账号格式正确,检验是否存在即已注册
        User loginUser = authService.findByAccount(account);
        if (loginUser == null) {
            return Result.error("账号不存在");
        } else {//账号存在,判断密码是否正确
            //密码正确
            if (Md5Util.getMD5String(password).equals(loginUser.getPassword())) {
                //账号正确,密码正确,登录成功,生成token
                Map<String, Object> claims = new HashMap<>();
                claims.put("id", loginUser.getId());
                claims.put("account", loginUser.getAccount());
                String token = JwtUtil.genToken(claims);
                return Result.success(token);
            }
            //密码错误
            else {
                return Result.error("密码错误");
            }
        }
    }

    //更新密码
    @PostMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String,String> params) {
        String oldPwd = params.get("oldPwd");
        String newPwd = params.get("newPwd");
        String rePwd = params.get("rePwd");
        //检查是否缺少必要的参数
        if(!StringUtils.hasLength(oldPwd)||!StringUtils.hasLength(newPwd)||!StringUtils.hasLength(rePwd)){
            return Result.error("参数不能为空");}
        //解析token获取当前登录用户的账号
        Map<String ,Object>map= ThreadLocalUtil.get();
        String account=(String) map.get("account");
        User user=authService.findByAccount(account);
        // 验证旧密码是否正确
        if(!user.getPassword().equals(Md5Util.getMD5String(oldPwd))){
            return Result.error("原密码不正确");
        }
        //检验两次密码是否输入一致
        if(!newPwd.equals(rePwd)){
            return  Result.error("两次密码输入不一致");}
        //更新密码
        authService.updatePwd(newPwd);
        return Result.success();
        }
    }


package com.liuqiya.dormitoryrepairsystem.controller;

import com.liuqiya.dormitoryrepairsystem.dto.SubmitRepairRequest;
import com.liuqiya.dormitoryrepairsystem.pojo.*;
import com.liuqiya.dormitoryrepairsystem.service.StudentService;
import com.liuqiya.dormitoryrepairsystem.utils.JwtUtil;
import com.liuqiya.dormitoryrepairsystem.utils.ThreadLocalUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired StudentService studentService;

    //查询自己的信息
    @GetMapping("/info")
    public Result<User> studentinfo(){
        // 根据学号查询用户信息
        Map<String ,Object> map= ThreadLocalUtil.get();
        String account=(String) map.get("account");
        User student = studentService.findByAccount(account);
        return Result.success(student);
    }


    //查询所有宿舍
    @GetMapping("/dormitory")
    public Result <List<Dormitory>> findAllDormitory(){
        List<Dormitory> dormitoryList=studentService.findAllDormitory();
        return Result.success(dormitoryList);
    }

    //绑定或修改宿舍
    @PostMapping("/dormitory")
    public Result bindOrUpdateDormitory(@RequestBody Dormitory dormitory ){
        Map<String ,Object>map= ThreadLocalUtil.get();
        String account=(String) map.get("account");
        studentService.bindOrUpdateDormitory(account,dormitory.getBuilding(),dormitory.getRoom());
        return Result.success();
    }

    //查询所有设备类型
    @GetMapping("/deviceTypes")
    public Result <List<DeviceType>> findAllDeviceType(){
        List<DeviceType> deviceTypeList=studentService.findAllDeviceType();
        return Result.success(deviceTypeList);
    }

    //创建报修单
    @PostMapping("/repairs")
    public Result creatRepairOrder(@Valid @RequestBody SubmitRepairRequest request){
        RepairOrder repairOrder=new RepairOrder();
        Map<String ,Object>map= ThreadLocalUtil.get();
        Integer studentId=(Integer) map.get("id");
        repairOrder.setStudentId(studentId);
        repairOrder.setDormBuilding(request.getDormBuilding());
        repairOrder.setDormRoom(request.getDormRoom());
        repairOrder.setDeviceTypeId(request.getDeviceTypeId());
        repairOrder.setProblemDescription(request.getProblemDescription());
        repairOrder.setImageUrl(request.getImageUrl());
        repairOrder.setStatus("待处理");
        studentService.creatRepairOrder(repairOrder);
        return Result.success("提交报修单成功");

    }


    // 上传图片
    @PostMapping("/upload")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            // 生成唯一文件名
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // 定义文件保存路径
            String uploadPath = "D:/uploads/";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 保存文件
            File dest = new File(uploadPath + fileName);
            file.transferTo(dest);

            // 生成文件 URL
            String fileUrl = "http://localhost:8080/uploads/" + fileName;

            return Result.success(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败");
        }
    }

    //查看用户的报修单列表
    @GetMapping("/repairs")
    public Result<List<RepairOrder>> findUserRepairOrder() {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer studentId = (Integer) map.get("id");
        List<RepairOrder> userRepairOrderList=studentService.findUserRepairOrder(studentId);
        return Result.success(userRepairOrderList);
    }

    //查看用户自己的报修单详情
    @GetMapping("/repairs/{id}")
    public Result<RepairOrder>findRepairOrder(@PathVariable Integer id){
        // 获取当前登录学生的id
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer currentStudentId = (Integer) map.get("id");
        RepairOrder repairOrder=studentService.findRepairOrder(id);
        //权限验证：检查报修单的studentId是否与当前学生的id一致
        if (repairOrder == null) {
            return Result.error("报修单不存在");
        }

        if (!repairOrder.getStudentId().equals(currentStudentId)) {
            return Result.error("无权查看此报修单");
        }
        return Result.success(repairOrder);
    }


    //取消用户的报修单
    @DeleteMapping("/repair/{id}")
    public Result cancelRepairOrder(@PathVariable Integer id){
        // 获取当前登录学生的id
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer currentStudentId = (Integer) map.get("id");
        RepairOrder repairOrder=studentService.findRepairOrder(id);
        //权限验证：检查报修单的studentId是否与当前学生的id一致
        if (repairOrder == null) {
            return Result.error("报修单不存在");
        }

        if (!repairOrder.getStudentId().equals(currentStudentId)) {
            return Result.error("无权取消此报修单");
        }
        studentService.cancelRepairOrder(id);
        return Result.success("取消报修单成功");
    }

}

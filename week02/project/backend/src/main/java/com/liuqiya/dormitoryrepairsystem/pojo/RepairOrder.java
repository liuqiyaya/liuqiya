package com.liuqiya.dormitoryrepairsystem.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RepairOrder {
   private Integer id;
   private Integer studentId;
   private String dormBuilding;
   private String dormRoom;
   private Integer deviceTypeId;
   private String problemDescription;
   private String imageUrl;
   private String status;
   private LocalDateTime createTime;
   private LocalDateTime updateTime;

}

package com.liuqiya.dormitoryrepairsystem.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class User {
    private Integer id;
    private String account;
    @JsonIgnore
    private String password;
    private String role;
    private String dormBuilding;
    private String dormRoom;
}

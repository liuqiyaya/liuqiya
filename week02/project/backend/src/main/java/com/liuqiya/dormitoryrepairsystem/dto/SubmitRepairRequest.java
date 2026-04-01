package com.liuqiya.dormitoryrepairsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitRepairRequest {
    @NotBlank
    private String dormBuilding;
    @NotBlank
    private String dormRoom;
    @NotNull
    private Integer deviceTypeId;
    private String problemDescription;
    private String imageUrl;
}

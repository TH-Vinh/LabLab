package com.example.springmvc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) //Chỉ trả về các trường khác null
public class ItemResponse {
    private Integer itemId;
    private String name;
    private String categoryType;
    private String unit;
    private Integer yearInUse;

    private String formula;
    private String packaging;
    private String supplier;
}
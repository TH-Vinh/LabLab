package com.example.springmvc.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Entity
@Table(name = "chemicals")
@PrimaryKeyJoinColumn(name = "item_id") // Bắt buộc phải khớp với tên cột FK trong DB
@Data
@EqualsAndHashCode(callSuper = true)
public class Chemical extends Item {
    @Column(name = "spec_id", insertable = false, updatable = false)

    private Integer specId;

    private String formula;
    private String supplier;
    private String packaging;

    @Column(name = "storage_location")
    private String storageLocation;

    @Column(name = "original_price")
    private BigDecimal originalPrice;
}
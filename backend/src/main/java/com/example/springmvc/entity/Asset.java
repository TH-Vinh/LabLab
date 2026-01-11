package com.example.springmvc.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Entity
@Table(name = "assets")
@PrimaryKeyJoinColumn(name = "item_id")
@Data
@EqualsAndHashCode(callSuper = true)
public class Asset extends Item {

    @Column(name = "spec_id", insertable = false, updatable = false)
    private Integer specId;

    @Column(name = "residual_value")
    private BigDecimal residualValue;

    @Column(name = "accounting_quantity")
    private Integer accountingQuantity;

    @Column(name = "inventory_quantity")
    private Integer inventoryQuantity;

    @Column(name = "status_detail")
    private String statusDetail;

    private String supplier;

    @Column(name = "storage_location")
    private String storageLocation;

    @Column(name = "original_price")
    private BigDecimal originalPrice;
}
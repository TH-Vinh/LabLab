package com.example.springmvc.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@Inheritance(strategy = InheritanceType.JOINED) // Quan trọng: Chiến lược nối bảng
@Data
public abstract class Item { // Abstract vì không ai tạo một "Item" chung chung

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "item_code", unique = true, nullable = false)
    private String itemCode;

    @Column(nullable = false)
    private String name;

    @Column(name = "category_type", nullable = false)
    private String categoryType; // CHEMICAL, DEVICE, TOOL

    private String unit;

    @Column(name = "current_quantity")
    private BigDecimal currentQuantity;

    @Column(name = "locked_quantity")
    private BigDecimal lockedQuantity;

    @Column(name = "year_in_use")
    private Integer yearInUse;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

}
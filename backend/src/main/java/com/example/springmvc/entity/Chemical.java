package com.example.springmvc.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "chemicals")
@PrimaryKeyJoinColumn(name = "item_id")
@Data
@EqualsAndHashCode(callSuper = true)
public class Chemical extends Item {

    @Column(name = "spec_id", insertable = false, updatable = false)
    private Integer specId;

    private String formula;
    private String packaging;

}
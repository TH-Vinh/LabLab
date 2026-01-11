package com.example.springmvc.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "rent_ticket_details")
@Data
public class RentTicketDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Integer detailId;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private RentTicket rentTicket;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "return_status")
    private String returnStatus;
}
package com.example.springmvc.dto.rent;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RentListResponse {
    private Integer ticketId;
    private String roomName;
    private String borrowerName;
    private LocalDateTime borrowDate;
    private LocalDateTime expectedReturnDate;
    private String status;
    private LocalDateTime createdDate;
}
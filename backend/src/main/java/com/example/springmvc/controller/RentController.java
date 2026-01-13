package com.example.springmvc.controller;

import com.example.springmvc.dto.rent.RentListResponse;
import com.example.springmvc.dto.rent.RentRequest;
import com.example.springmvc.entity.RentTicket;
import com.example.springmvc.service.RentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rent")
@RequiredArgsConstructor
public class RentController {

    private final RentService rentService;

    // Tạo phiếu mượn mới
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createRentRequest(@Valid @RequestBody RentRequest request) {
        rentService.createRentRequest(request);
        return ResponseEntity.ok(Map.of("message", "Gửi yêu cầu thành công! Vui lòng chờ Admin duyệt."));
    }

    // Duyệt hoặc từ chối phiếu (Admin)
    @PostMapping("/review/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> reviewTicket(
            @PathVariable("id") Integer id,
            @RequestParam("status") String status
    ) {
        rentService.reviewRentTicket(id, status);
        String msg = "APPROVED".equalsIgnoreCase(status) ? "Đã DUYỆT phiếu mượn!" : "Đã TỪ CHỐI phiếu mượn!";
        return ResponseEntity.ok(Map.of("message", msg));
    }

    // Lấy danh sách tất cả các phiếu (Admin) - Lọc ?status=PENDING
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<RentListResponse>> getAllTickets(
            @RequestParam(value = "status", required = false) String status
    ) {
        return ResponseEntity.ok(rentService.getAllTickets(status));
    }

    // Lấy dữ liệu (user)
    @GetMapping("/monitor-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RentListResponse>> getMonitorAllTickets(
            @RequestParam(value = "status", required = false) String status
    ) {
        return ResponseEntity.ok(rentService.getAllTickets(status));
    }

    // Lấy lịch sử mượn của một user bất kỳ theo ID
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RentListResponse>> getHistoryByUser(@PathVariable("userId") Integer userId) {
        return ResponseEntity.ok(rentService.getRentHistoryByUserId(userId));
    }

    // Lấy lịch sử của người đang đăng nhập
    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RentListResponse>> getMyHistory() {
        return ResponseEntity.ok(rentService.getMyRentHistory());
    }

    // Xem chi tiết một phiếu (Admin hoặc chính chủ)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RentTicket> getTicketDetail(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(rentService.getTicketDetail(id));
    }

    // Lấy 5 phiếu mới nhất
    @GetMapping("/recent-activity")
    public ResponseEntity<List<RentListResponse>> getRecentActivity() {
        return ResponseEntity.ok(rentService.getRecentActivity());
    }
}
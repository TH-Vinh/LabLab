package com.example.springmvc.service;

import com.example.springmvc.dto.rent.RentListResponse;
import com.example.springmvc.dto.rent.RentRequest;
import com.example.springmvc.entity.RentTicket;
import java.util.List;

public interface RentService {
    // Tạo phiếu mượn mới
    void createRentRequest(RentRequest request);

    // Duyệt hoặc từ chối phiếu
    void reviewRentTicket(Integer ticketId, String status);

    // Lấy danh sách phiếu mượn
    List<RentListResponse> getAllTickets(String status);

    // Lấy lịch sử mượn của một user bất kỳ theo ID
    List<RentListResponse> getRentHistoryByUserId(Integer userId);

    // Lấy lịch sử mượn của chính người đang đăng nhập (Dựa vào Token)
    List<RentListResponse> getMyRentHistory();

    // Xem chi tiết một phiếu mượn cụ thể (Admin)
    RentTicket getTicketDetail(Integer ticketId);

    // Lấy danh sách rút gọn 5 hoạt động gần nhất
    List<RentListResponse> getRecentActivity();
}
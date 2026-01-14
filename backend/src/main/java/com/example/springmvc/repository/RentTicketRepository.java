package com.example.springmvc.repository;

import com.example.springmvc.entity.RentTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentTicketRepository extends JpaRepository<RentTicket, Integer> {

    // Lấy đúng 5 phiếu mới nhất để hiển thị lên Widget "Hoạt động (Live)"
    List<RentTicket> findTop5ByOrderByCreatedDateDesc();

    // Lấy danh sách phiếu lọc theo trạng thái cụ thể (PENDING/APPROVED/REJECTED), sắp xếp mới nhất trước
    List<RentTicket> findByStatusOrderByCreatedDateDesc(String status);

    // Lấy lịch sử mượn của một user cụ thể, sắp xếp mới nhất trước
    List<RentTicket> findByUser_UserIdOrderByCreatedDateDesc(Integer userId);

    // Đếm số lượng phiếu đã đặt phòng trong khoảng thời gian cụ thể (Dùng để check trùng lịch)
    // Nếu kết quả > 0 nghĩa là phòng đã bị bận
    long countActiveBookings(
            @Param("roomId") Integer roomId,
            @Param("reqStart") LocalDateTime reqStart,
            @Param("reqEnd") LocalDateTime reqEnd
    );

    // Lấy toàn bộ danh sách phiếu mượn trong hệ thống (Dùng cho Admin xem tất cả)
    List<RentTicket> findAllByOrderByCreatedDateDesc();
}
package com.example.springmvc.service.impl;

import com.example.springmvc.dto.rent.RentListResponse;
import com.example.springmvc.dto.rent.RentRequest;
import com.example.springmvc.entity.*;
import com.example.springmvc.exception.BusinessException;
import com.example.springmvc.mapper.RentMapper;
import com.example.springmvc.repository.*;
import com.example.springmvc.service.RentService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RentServiceImpl implements RentService {

    private final RentTicketRepository rentTicketRepository;
    private final ItemRepository itemRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RentMapper rentMapper;
    private final SimpMessagingTemplate messagingTemplate;

    // --- 1. USER TẠO YÊU CẦU ---
    @Override
    @Transactional
    public void createRentRequest(RentRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        if (user == null) throw new BusinessException("Lỗi: Không tìm thấy người dùng!");

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new BusinessException("Phòng không tồn tại!"));

        if (!"LAB".equals(room.getType())) {
            throw new BusinessException("Phòng '" + room.getRoomName() + "' không phải là Phòng Thí Nghiệm (LAB).");
        }

        LocalDateTime start = request.getBorrowDate() != null ? request.getBorrowDate() : LocalDateTime.now();
        LocalDateTime end = request.getExpectedReturnDate();

        if (end == null || end.isBefore(start)) throw new BusinessException("Thời gian trả không hợp lệ!");
        if (start.isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new BusinessException("Thời gian mượn không được ở quá khứ!");
        }

        // Kiểm tra xem phòng có bị trùng lịch không
        long conflicts = rentTicketRepository.countActiveBookings(request.getRoomId(), start, end);
        if (conflicts > 0) throw new BusinessException("Phòng " + room.getRoomName() + " đã bị đặt kín trong khung giờ này.");

        try {
            // Tạo object phiếu mượn
            RentTicket ticket = new RentTicket();
            ticket.setUser(user);
            ticket.setRoom(room);
            ticket.setStatus("PENDING");
            ticket.setBorrowDate(start);
            ticket.setExpectedReturnDate(end);
            ticket.setCreatedDate(LocalDateTime.now());
            ticket.setDetails(new ArrayList<>());

            // Duyệt qua từng món đồ để giữ hàng (Reserve Stock)
            for (RentRequest.RentItemDto dto : request.getItems()) {
                Item item = itemRepository.findById(dto.getItemId())
                        .orElseThrow(() -> new BusinessException("Món đồ ID " + dto.getItemId() + " không tồn tại"));

                BigDecimal requestQty = dto.getQuantity();
                // Trừ số lượng khả dụng, tăng số lượng locked (tạm giữ)
                int updatedRows = itemRepository.reserveStock(item.getItemId(), dto.getQuantity());

                if (updatedRows == 0) {
                    throw new BusinessException("Món '" + item.getName() + "' không đủ số lượng khả dụng!");
                }

                // Tạo chi tiết phiếu
                RentTicketDetail detail = new RentTicketDetail();
                detail.setRentTicket(ticket);
                detail.setItem(item);
                detail.setQuantityBorrowed(requestQty);
                detail.setReturnStatus("NOT_RETURNED");
                ticket.getDetails().add(detail);
            }
            RentTicket savedTicket = rentTicketRepository.save(ticket);

            // Gửi thông báo Real-time qua WebSocket
            broadcastTicketUpdate(savedTicket);

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new BusinessException("Hệ thống đang bận, vui lòng thử lại sau giây lát.");
        }
    }

    // --- 2. ADMIN XỬ LÝ PHIẾU ---
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewRentTicket(Integer ticketId, String status) {
        String finalStatus = status.trim().toUpperCase();
        if (!"APPROVED".equals(finalStatus) && !"REJECTED".equals(finalStatus)) {
            throw new BusinessException("Trạng thái không hợp lệ!");
        }

        RentTicket ticket = rentTicketRepository.findById(ticketId)
                .orElseThrow(() -> new BusinessException("Phiếu mượn không tồn tại!"));

        // Chỉ được duyệt các phiếu đang ở trạng thái CHỜ
        if (!"PENDING".equals(ticket.getStatus())) {
            throw new BusinessException("Chỉ có thể xử lý các phiếu đang ở trạng thái CHỜ (PENDING)!");
        }

        try {
            // Xử lý kho dựa trên quyết định duyệt hay từ chối
            for (RentTicketDetail detail : ticket.getDetails()) {
                Item item = detail.getItem();
                BigDecimal qty = detail.getQuantityBorrowed();
                BigDecimal locked = item.getLockedQuantity() == null ? BigDecimal.ZERO : item.getLockedQuantity();

                if ("APPROVED".equals(finalStatus)) {
                    // Nếu Duyệt: Trừ kho thật (currentQuantity), giảm kho tạm giữ (lockedQuantity)
                    BigDecimal current = item.getCurrentQuantity();
                    if (current.compareTo(qty) < 0) {
                        throw new BusinessException("Lỗi kho: Món '" + item.getName() + "' không đủ hàng!");
                    }
                    item.setCurrentQuantity(current.subtract(qty));
                    item.setLockedQuantity(locked.subtract(qty).max(BigDecimal.ZERO));
                } else {
                    // Nếu Từ chối: Trả lại kho tạm giữ (giảm lockedQuantity)
                    item.setLockedQuantity(locked.subtract(qty).max(BigDecimal.ZERO));
                }
                itemRepository.save(item);
            }

            ticket.setStatus(finalStatus);
            RentTicket updatedTicket = rentTicketRepository.save(ticket);

            broadcastTicketUpdate(updatedTicket);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("Lỗi hệ thống khi xử lý phiếu.");
        }
    }

    // --- 3. LẤY DANH SÁCH ---
    @Override
    @Transactional(readOnly = true)
    public List<RentListResponse> getAllTickets(String status) {
        List<RentTicket> tickets;

        if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            tickets = rentTicketRepository.findByStatusOrderByCreatedDateDesc(status.toUpperCase());
        } else {
            tickets = rentTicketRepository.findAllByOrderByCreatedDateDesc();
        }

        return tickets.stream()
                .map(rentMapper::toRentListResponse)
                .collect(Collectors.toList());
    }

    // --- 4. LẤY LỊCH SỬ THEO USER ID ---
    @Override
    @Transactional(readOnly = true)
    public List<RentListResponse> getRentHistoryByUserId(Integer targetUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName());

        // Chỉ Admin hoặc chính chủ mới được xem
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = currentUser.getUserId().equals(targetUserId);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Bạn không có quyền xem lịch sử mượn của người khác!");
        }

        return rentTicketRepository.findByUser_UserIdOrderByCreatedDateDesc(targetUserId).stream()
                .map(rentMapper::toRentListResponse)
                .collect(Collectors.toList());
    }

    // --- 5. TỰ LẤY LỊCH SỬ CỦA MÌNH ---
    @Override
    @Transactional(readOnly = true)
    public List<RentListResponse> getMyRentHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        if (user == null) throw new BusinessException("Không tìm thấy thông tin người dùng!");

        return rentTicketRepository.findByUser_UserIdOrderByCreatedDateDesc(user.getUserId()).stream()
                .map(rentMapper::toRentListResponse)
                .collect(Collectors.toList());
    }

    // --- 6. LẤY CHI TIẾT PHIẾU ---
    @Override
    @Transactional(readOnly = true)
    public RentTicket getTicketDetail(Integer ticketId) {
        RentTicket ticket = rentTicketRepository.findById(ticketId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy phiếu mượn!"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = ticket.getUser().getUserId().equals(currentUser.getUserId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Bạn không được phép xem chi tiết phiếu này.");
        }
        return ticket;
    }

    // --- 7. LẤY HOẠT ĐỘNG GẦN ĐÂY ---
    @Override
    @Transactional(readOnly = true)
    public List<RentListResponse> getRecentActivity() {
        // Chỉ lấy 5 phiếu mới nhất, không quan tâm trạng thái
        return rentTicketRepository.findTop5ByOrderByCreatedDateDesc().stream()
                .map(rentMapper::toRentListResponse)
                .collect(Collectors.toList());
    }

    // Hàm phụ trợ: Gửi dữ liệu qua WebSocket
    private void broadcastTicketUpdate(RentTicket ticket) {
        try {
            RentListResponse responseDto = rentMapper.toRentListResponse(ticket);
            messagingTemplate.convertAndSend("/topic/rent-updates", responseDto);
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi gửi WebSocket Sync: " + e.getMessage());
        }
    }
}
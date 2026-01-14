import React, { useState, useEffect } from "react";
import Swal from "sweetalert2";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import api from "../../../services/api";
import "./TeacherBorrow.css";
import InventorySection from "./components/InventorySection/InventorySection";
import BookingForm from "./components/BookingForm/BookingForm";
import LiveMonitor from "./components/LiveMonitor/LiveMonitor";
import UserHistory from "./components/UserHistory/UserHistory";

const TeacherBorrow = () => {
  const [inventory, setInventory] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [basket, setBasket] = useState([]);
  const [loading, setLoading] = useState(true);

  // State lưu trữ logs Real-time
  const [liveLogs, setLiveLogs] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [itemRes, roomRes, logRes] = await Promise.all([
          api.get("/items", { params: { fetchStock: true } }),
          api.get("/rooms/booking-available"),
          api.get("/rent/recent-activity"),
        ]);

        setInventory(itemRes.data);
        setRooms(roomRes.data);
        setLiveLogs(logRes.data);
      } catch (error) {
        console.error("Lỗi tải dữ liệu:", error);
        Swal.fire("Lỗi", "Không thể tải dữ liệu kho hoặc phòng!", "error");
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  // 2. KẾT NỐI WEBSOCKET
  useEffect(() => {
    let stompClient = null;

    const connectWebSocket = () => {
      try {
        const socket = new SockJS("http://localhost:8080/springmvc/ws");
        stompClient = Stomp.over(socket);
        stompClient.debug = null;

        stompClient.connect(
          {},
          () => {
            stompClient.subscribe("/topic/rent-updates", (message) => {
              if (message.body) {
                try {
                  const newLog = JSON.parse(message.body);

                  setLiveLogs((prevLogs) => {
                    const index = prevLogs.findIndex(
                      (log) => log.ticketId === newLog.ticketId
                    );

                    if (index !== -1) {
                      const updatedLogs = [...prevLogs];
                      updatedLogs[index] = newLog;
                      return updatedLogs;
                    } else {
                      return [newLog, ...prevLogs];
                    }
                  });
                } catch (e) {
                  console.error("Lỗi parse JSON socket:", e);
                }
              }
            });
          },
          (error) => {
            console.error("❌ Lỗi kết nối Socket:", error);
          }
        );
      } catch (err) {
        console.error("Lỗi khởi tạo SockJS:", err);
      }
    };

    connectWebSocket();

    return () => {
      if (stompClient && stompClient.connected) {
        stompClient.disconnect();
      }
    };
  }, []);

  // --- Logic Giỏ Hàng & Submit ---
  const handleToggleBasket = (item) => {
    setBasket((prev) => {
      const exists = prev.find((i) => i.id === item.itemId);
      if (exists) {
        return prev.filter((i) => i.id !== item.itemId);
      } else {
        return [
          ...prev,
          {
            id: item.itemId,
            name: item.name,
            unit: item.unit,
            availableQty:
              item.availableQuantity !== undefined ? item.availableQuantity : 0,
            orderQty: 1,
          },
        ];
      }
    });
  };

  const clearAllBasket = () => {
    setBasket([]);
  };

  const handleCreateTicket = async (payload) => {
    try {
      Swal.fire({
        title: "Đang xử lý...",
        text: "Đang gửi yêu cầu mượn vật tư",
        didOpen: () => Swal.showLoading(),
        allowOutsideClick: false,
      });

      const response = await api.post("/rent/create", payload);

      Swal.fire({
        icon: "success",
        title: "Thành công!",
        text:
          response.data?.message || "Phiếu mượn đã được tạo và đang chờ duyệt.",
        confirmButtonColor: "#2563eb",
      });

      setBasket([]);
    } catch (error) {
      console.error("Lỗi tạo phiếu:", error);
      const errorMsg =
        error.response?.data?.message || "Có lỗi xảy ra khi tạo phiếu mượn!";
      Swal.fire({
        icon: "error",
        title: "Thất bại",
        text: errorMsg,
        confirmButtonColor: "#ef4444",
      });
    }
  };

  if (loading)
    return <div className="loading-spinner">Đang tải dữ liệu...</div>;

  return (
    <div className="borrow-container">
      <div className="top-section-grid">
        <InventorySection
          inventory={inventory}
          onAddToBasket={handleToggleBasket}
          basket={basket}
        />

        <BookingForm
          basket={basket}
          setBasket={setBasket}
          clearAll={clearAllBasket}
          rooms={rooms}
          onSubmit={handleCreateTicket}
        />
      </div>

      <div className="bottom-section-grid">
        <LiveMonitor logs={liveLogs} />
        <UserHistory />
      </div>
    </div>
  );
};

export default TeacherBorrow;

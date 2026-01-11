import React, { useState, useMemo } from "react";
import {
  ClipboardCheck,
  Search,
  Filter,
  Beaker,
  Cpu,
  Hammer,
  Hash,
  Clock,
  DoorOpen,
  Trash2,
  Plus,
  Users,
  Info,
  Calendar,
  MapPin,
  Activity,
  CheckCircle,
<<<<<<< HEAD
  FileText,
=======
>>>>>>> a90f4ec487a7456d1438595f75520a34fda69c41
} from "lucide-react";
import Swal from "sweetalert2";
import api from "../../../services/api";
import "./TeacherBorrow.css";

// Toast cấu hình z-index cực cao để nổi trên Modal
const Toast = Swal.mixin({
  toast: true,
  position: "top-end",
  showConfirmButton: false,
  timer: 4000,
  timerProgressBar: true,
  target: "body",
});

const TeacherBorrow = () => {
  // --- STATES ---
  const [searchTerm, setSearchTerm] = useState("");
  const [activeType, setActiveType] = useState("ALL");
  const [basket, setBasket] = useState([]);
  const [borrowInfo, setBorrowInfo] = useState({
    room: "",
    startTime: "",
    endTime: "",
    purpose: "",
  });

  // --- DỮ LIỆU GIẢ LẬP (Mô phỏng Warehouse thực tế) ---
  const [inventory] = useState([
    {
      id: 1,
      name: "Axit Sunfuric H2SO4 (98%)",
      type: "CHEMICAL",
      stock: 2500,
      unit: "ml",
      location: "Tủ hóa chất A",
    },
    {
      id: 2,
      name: "Máy ly tâm đa năng 15000rpm",
      type: "EQUIPMENT",
      stock: 4,
      unit: "máy",
      location: "Kho thiết bị 1",
    },
    {
      id: 3,
      name: "Bộ dụng cụ cơ khí cầm tay",
      type: "TOOL",
      stock: 20,
      unit: "bộ",
      location: "Kệ công cụ 2",
    },
    {
      id: 4,
      name: "NaOH tinh thể",
      type: "CHEMICAL",
      stock: 1000,
      unit: "g",
      location: "Tủ hóa chất B",
    },
    {
      id: 5,
      name: "Kính hiển vi quang học Nikon",
      type: "EQUIPMENT",
      stock: 32,
      unit: "chiếc",
      location: "Phòng LAB 01",
    },
  ]);

  // Tạo danh sách 35 phòng thực hành
  const labRooms = Array.from(
    { length: 35 },
    (_, i) => `Phòng thực hành ${301 + i}`
  );

  // Dữ liệu mượn của các đồng nghiệp khác (Public View)
  const [publicLogs] = useState([
    {
      id: 101,
      teacher: "GV. Trần Văn An",
      room: "P.305",
      items: "H2SO4, NaOH",
      time: "07:30 - 09:45",
    },
    {
      id: 102,
      teacher: "GV. Lê Thị Bình",
      room: "P.312",
      items: "Máy ly tâm, Panme",
      time: "13:30 - 16:00",
    },
  ]);

  // --- LOGIC ---
  const filteredInventory = useMemo(() => {
    return inventory.filter((item) => {
      const matchSearch = item.name
        .toLowerCase()
        .includes(searchTerm.toLowerCase());
      const matchType = activeType === "ALL" || item.type === activeType;
      return matchSearch && matchType;
    });
  }, [searchTerm, activeType, inventory]);

  const addToBasket = (item) => {
    if (basket.find((i) => i.id === item.id)) {
      Toast.fire({ icon: "info", title: "Vật tư đã có trong danh sách mượn" });
      return;
    }
    setBasket([...basket, { ...item, orderQty: 1 }]);
  };

  const updateBasketQty = (id, qty) => {
    setBasket(
      basket.map((i) =>
        i.id === id ? { ...i, orderQty: Math.max(1, qty) } : i
      )
    );
  };

  const handleSendRequest = async () => {
    if (
      basket.length === 0 ||
      !borrowInfo.room ||
      !borrowInfo.startTime ||
      !borrowInfo.endTime
    ) {
      Toast.fire({ icon: "warning", title: "Vui lòng hoàn tất phiếu đăng ký" });
      return;
    }
    Toast.fire({
      icon: "success",
      title: "Đã gửi yêu cầu mượn vật tư thành công",
    });
    setBasket([]);
  };

  return (
    <div className="borrow-main-container">
      {/* HEADER TỔNG QUÁT */}
      <div className="borrow-page-header">
        <div className="header-icon">
          <ClipboardCheck size={32} />
        </div>
        <div className="header-title">
          <h1>Đăng ký Vật tư & Phòng thực hành</h1>
          <p>
            Tìm kiếm hóa chất, thiết bị và quản lý lịch mượn phòng tập trung.
          </p>
        </div>
      </div>

      <div className="borrow-grid-layout">
        {/* CỘT TRÁI: KHO VÀ THEO DÕI */}
        <div className="borrow-col-left">
          {/* SECTION 1: KHO VẬT TƯ (TÌM KIẾM & LỌC) */}
          <section className="glass-card">
            <div className="section-header">
              <div className="title-with-icon">
                <Activity size={20} /> <h3>Kho Vật tư Hệ thống</h3>
              </div>
              <div className="tabs-navigation">
                {["ALL", "CHEMICAL", "EQUIPMENT", "TOOL"].map((type) => (
                  <button
                    key={type}
                    className={activeType === type ? "active" : ""}
                    onClick={() => setActiveType(type)}
                  >
                    {type === "ALL"
                      ? "Tất cả"
                      : type === "CHEMICAL"
                      ? "Hóa chất"
                      : type === "EQUIPMENT"
                      ? "Máy móc"
                      : "Công cụ"}
                  </button>
                ))}
              </div>
            </div>

            <div className="search-bar-modern">
              <Search size={18} />
              <input
                type="text"
                placeholder="Tìm nhanh theo tên vật tư hoặc hóa chất..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>

            <div className="inventory-scroll-area">
              <div className="inventory-cards">
                {filteredInventory.map((item) => (
                  <div
                    key={item.id}
                    className={`item-row-card ${item.type.toLowerCase()}`}
                  >
                    <div className="item-main-info">
                      <div className="type-badge">
                        {item.type === "CHEMICAL" ? (
                          <Beaker size={18} />
                        ) : item.type === "EQUIPMENT" ? (
                          <Cpu size={18} />
                        ) : (
                          <Hammer size={18} />
                        )}
                      </div>
                      <div className="text-info">
                        <span className="name">{item.name}</span>
                        <span className="loc">
                          <MapPin size={12} /> {item.location}
                        </span>
                      </div>
                    </div>
                    <div className="item-stock-info">
                      <p>
                        Hiện có: <strong>{item.stock}</strong> {item.unit}
                      </p>
                      <button
                        className="btn-add-basket"
                        onClick={() => addToBasket(item)}
                      >
                        <Plus size={16} />
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </section>

          {/* SECTION 2: THEO DÕI CÔNG KHAI (PUBLIC MONITOR) */}
          <section className="glass-card monitor-section">
            <div className="section-header">
              <div className="title-with-icon">
                <Users size={20} /> <h3>Hoạt động mượn vật tư (Công khai)</h3>
              </div>
            </div>
            <div className="public-tracking-list">
              {publicLogs.map((log) => (
                <div key={log.id} className="tracking-log-item">
                  <div className="log-room">
                    <strong>{log.room}</strong>
                  </div>
                  <div className="log-details">
                    <p className="teacher-name">{log.teacher}</p>
                    <p className="item-summary">{log.items}</p>
                  </div>
                  <div className="log-time">
                    <Clock size={12} /> {log.time}
                  </div>
                </div>
              ))}
            </div>
          </section>
        </div>

        {/* CỘT PHẢI: PHIẾU MƯỢN (SHOPPING CART STYLE) */}
        <div className="borrow-col-right">
          <section className="glass-card booking-form-card sticky-sidebar">
            <div className="booking-header">
              <div className="title-with-icon">
                <FileText size={20} /> <h3>Phiếu Đăng ký Mượn</h3>
              </div>
              <span className="count-badge">{basket.length} vật tư</span>
            </div>

            <div className="selected-items-area">
              {basket.length === 0 ? (
                <div className="empty-cart-msg">
                  <Info size={40} />
                  <p>
                    Bạn chưa chọn vật tư nào.
                    <br />
                    Vui lòng chọn từ kho bên trái.
                  </p>
                </div>
              ) : (
                <div className="basket-items-list">
                  {basket.map((item) => (
                    <div key={item.id} className="basket-row">
                      <div className="b-info">
                        <span className="b-name">{item.name}</span>
                        <span className="b-limit">
                          Tối đa: {item.stock} {item.unit}
                        </span>
                      </div>
                      <div className="b-actions">
                        <div className="qty-picker">
                          <input
                            type="number"
                            value={item.orderQty}
                            onChange={(e) =>
                              updateBasketQty(item.id, parseInt(e.target.value))
                            }
                          />
                        </div>
                        <button
                          className="btn-remove"
                          onClick={() =>
                            setBasket(basket.filter((i) => i.id !== item.id))
                          }
                        >
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="form-details-area">
              <div className="form-group">
                <label>
                  <Clock size={16} /> Khung giờ mượn
                </label>
                <div className="time-input-group">
                  <input
                    type="time"
                    value={borrowInfo.startTime}
                    onChange={(e) =>
                      setBorrowInfo({
                        ...borrowInfo,
                        startTime: e.target.value,
                      })
                    }
                  />
                  <span>đến</span>
                  <input
                    type="time"
                    value={borrowInfo.endTime}
                    onChange={(e) =>
                      setBorrowInfo({ ...borrowInfo, endTime: e.target.value })
                    }
                  />
                </div>
              </div>

              <div className="form-group">
                <label>
                  <DoorOpen size={16} /> Chọn phòng thực hành (35 phòng)
                </label>
                <select
                  value={borrowInfo.room}
                  onChange={(e) =>
                    setBorrowInfo({ ...borrowInfo, room: e.target.value })
                  }
                >
                  <option value="">-- Click để xem danh sách phòng --</option>
                  {labRooms.map((r) => (
                    <option key={r} value={r}>
                      {r}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <button
              className="btn-submit-final"
              onClick={handleSendRequest}
              disabled={basket.length === 0}
            >
              Gửi yêu cầu phê duyệt
            </button>
          </section>
        </div>
      </div>
    </div>
  );
};

export default TeacherBorrow;
<<<<<<< HEAD
=======

// Biến FileText chưa được import trong Lucide, hãy đảm bảo import từ lucide-react
import { FileText } from "lucide-react";
>>>>>>> a90f4ec487a7456d1438595f75520a34fda69c41

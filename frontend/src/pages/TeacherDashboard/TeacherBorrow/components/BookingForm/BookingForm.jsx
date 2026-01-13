import React, { useState } from "react";
import {
  ClipboardList,
  Trash2,
  Calendar,
  Clock,
  MapPin,
  Send,
} from "lucide-react";
import Swal from "sweetalert2";
import "./BookingForm.css";

const BookingForm = ({ basket, setBasket, clearAll, rooms, onSubmit }) => {
  const [form, setForm] = useState({
    roomId: "",
    startDate: "",
    startTime: "07:00",
    endDate: "",
    endTime: "11:00",
  });

  const handleQtyChange = (id, value) => {
    const qty = parseFloat(value);
    if (qty < 0) return;

    setBasket((prev) =>
      prev.map((item) =>
        item.id === id ? { ...item, orderQty: qty || 0 } : item
      )
    );
  };

  const handleRemoveItem = (id) => {
    setBasket((prev) => prev.filter((item) => item.id !== id));
  };

  const handleSubmit = () => {
    if (basket.length === 0) {
      Swal.fire(
        "Giỏ hàng trống",
        "Vui lòng chọn ít nhất một vật tư!",
        "warning"
      );
      return;
    }
    if (!form.roomId) {
      Swal.fire(
        "Chưa chọn phòng",
        "Vui lòng chọn phòng thí nghiệm!",
        "warning"
      );
      return;
    }
    if (!form.startDate || !form.endDate) {
      Swal.fire(
        "Thiếu thời gian",
        "Vui lòng chọn đầy đủ ngày mượn và ngày trả!",
        "warning"
      );
      return;
    }

    const borrowDate = `${form.startDate}T${form.startTime}:00`;
    const expectedReturnDate = `${form.endDate}T${form.endTime}:00`;

    if (new Date(borrowDate) >= new Date(expectedReturnDate)) {
      Swal.fire(
        "Thời gian không hợp lệ",
        "Thời gian trả phải sau thời gian mượn!",
        "error"
      );
      return;
    }

    const payload = {
      roomId: parseInt(form.roomId),
      borrowDate: borrowDate,
      expectedReturnDate: expectedReturnDate,
      items: basket.map((item) => ({
        itemId: item.id,
        quantity: item.orderQty,
      })),
    };

    onSubmit(payload);
  };

  return (
    <div className="dashboard-card booking-wrapper">
      {/* Header */}
      <div className="card-header">
        <h3>
          <ClipboardList size={20} color="#2563eb" /> Phiếu Đăng Ký
        </h3>
        {basket.length > 0 && (
          <button className="clear-all-btn" onClick={clearAll}>
            <Trash2 size={14} /> Xóa hết
          </button>
        )}
      </div>

      {/* Form nhập liệu */}
      <div className="booking-form-fields">
        {/* Chọn Phòng */}
        <div className="form-group">
          <label>
            <MapPin size={14} /> Phòng thí nghiệm
          </label>
          <select
            className="form-control"
            value={form.roomId}
            onChange={(e) => setForm({ ...form, roomId: e.target.value })}
          >
            <option value="">-- Chọn phòng --</option>
            {rooms.map((r) => (
              <option key={r.roomId} value={r.roomId}>
                {r.roomName} {r.floor ? `(Tầng ${r.floor})` : ""}
              </option>
            ))}
          </select>
        </div>

        {/* Thời gian mượn */}
        <div className="time-input-row">
          <div className="form-group">
            <label>
              <Calendar size={14} /> Ngày mượn
            </label>
            <input
              type="date"
              className="form-control"
              value={form.startDate}
              onChange={(e) => setForm({ ...form, startDate: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label>
              <Clock size={14} /> Bắt đầu
            </label>
            <input
              type="time"
              className="form-control"
              value={form.startTime}
              onChange={(e) => setForm({ ...form, startTime: e.target.value })}
            />
          </div>
        </div>

        {/* Thời gian trả */}
        <div className="time-input-row">
          <div className="form-group">
            <label>
              <Calendar size={14} /> Ngày trả
            </label>
            <input
              type="date"
              className="form-control"
              value={form.endDate}
              onChange={(e) => setForm({ ...form, endDate: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label>
              <Clock size={14} /> Kết thúc
            </label>
            <input
              type="time"
              className="form-control"
              value={form.endTime}
              onChange={(e) => setForm({ ...form, endTime: e.target.value })}
            />
          </div>
        </div>
      </div>

      {/* Danh sách vật tư (Giỏ hàng) */}
      <div className="basket-section-title">
        Danh sách vật tư ({basket.length})
      </div>
      <div className="basket-scroll-area">
        {basket.length === 0 ? (
          <div className="empty-basket-msg">Chưa chọn vật tư nào</div>
        ) : (
          basket.map((item) => (
            <div key={item.id} className="basket-row-item">
              <div className="item-display-name">
                {item.name}
                <span className="stock-hint">
                  {" "}
                  (Kho: {item.availableQty} {item.unit})
                </span>
              </div>

              <div className="item-row-actions">
                <input
                  type="number"
                  className="form-control qty-small no-spinners"
                  value={item.orderQty}
                  min="1"
                  onChange={(e) => handleQtyChange(item.id, e.target.value)}
                />
                <button
                  className="icon-btn-delete"
                  onClick={() => handleRemoveItem(item.id)}
                  title="Xóa món này"
                >
                  <Trash2 size={16} />
                </button>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Nút Gửi */}
      <button className="submit-ticket-btn" onClick={handleSubmit}>
        <Send size={18} /> GỬI YÊU CẦU
      </button>
    </div>
  );
};

export default BookingForm;

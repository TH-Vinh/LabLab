import React, { useState } from "react";
import {
  Lock,
  Bell,
  Globe,
  Trash2,
  ChevronRight,
  Moon,
  Sun,
} from "lucide-react";
import "./TeacherSettings.css";

const TeacherSettings = () => {
  const [isDarkMode, setIsDarkMode] = useState(false);

  return (
    <div className="settings-container">
      <div className="settings-header">
        <h2>⚙️ Cài đặt hệ thống</h2>
        <p>Quản lý tùy chọn cá nhân và bảo mật tài khoản của bạn.</p>
      </div>

      <div className="settings-grid">
        {/* NHÓM 1: BẢO MẬT & ĐĂNG NHẬP */}
        <section className="settings-section">
          <div className="section-title">
            <Lock size={20} />
            <h3>Bảo mật & Đăng nhập</h3>
          </div>
          <div className="settings-list">
            <div
              className="settings-item"
              onClick={() => alert("Mở form đổi mật khẩu...")}
            >
              <div className="item-info">
                <p className="item-label">Đổi mật khẩu</p>
                <p className="item-desc">
                  Cập nhật mật khẩu định kỳ để bảo vệ tài khoản của bạn.
                </p>
              </div>
              <ChevronRight size={18} color="#999" />
            </div>
            <div className="settings-item">
              <div className="item-info">
                <p className="item-label">Xác thực 2 lớp (2FA)</p>
                <p className="item-desc">
                  Yêu cầu mã xác nhận khi đăng nhập trên thiết bị lạ.
                </p>
              </div>
              <label className="switch">
                <input type="checkbox" />
                <span className="slider round"></span>
              </label>
            </div>
          </div>
        </section>

        {/* NHÓM 2: HIỂN THỊ */}
        <section className="settings-section">
          <div className="section-title">
            <Sun size={20} />
            <h3>Giao diện & Hiển thị</h3>
          </div>
          <div className="settings-list">
            <div className="settings-item no-border">
              <div className="item-info">
                <p className="item-label">Chế độ giao diện</p>
                <p className="item-desc">
                  Chạm vào icon để chuyển đổi giữa chế độ Sáng và Tối.
                </p>
              </div>
              
              <button
                className={`icon-only-toggle ${isDarkMode ? "dark" : "light"}`}
                onClick={() => setIsDarkMode(!isDarkMode)}
                title={
                  isDarkMode
                    ? "Chuyển sang chế độ sáng"
                    : "Chuyển sang chế độ tối"
                }
              >
                {isDarkMode ? (
                  <Moon size={32} strokeWidth={2.5} className="theme-icon" />
                ) : (
                  <Sun size={32} strokeWidth={2.5} className="theme-icon" />
                )}
              </button>
            </div>
          </div>
        </section>

        {/* NHÓM 3: THÔNG BÁO & HỆ THỐNG */}
        <section className="settings-section">
          <div className="section-title">
            <Bell size={20} />
            <h3>Thông báo & Hệ thống</h3>
          </div>
          <div className="settings-list">
            <div className="settings-item">
              <div className="item-info">
                <p className="item-label">Thông báo đẩy</p>
                <p className="item-desc">
                  Nhận thông báo khi yêu cầu mượn thiết bị được duyệt.
                </p>
              </div>
              <label className="switch">
                <input type="checkbox" defaultChecked />
                <span className="slider round"></span>
              </label>
            </div>
            <div className="settings-item delete-action">
              <div className="item-info">
                <p className="item-label">Xóa bộ nhớ đệm</p>
                <p className="item-desc">
                  Xóa dữ liệu tạm thời để làm nhẹ ứng dụng.
                </p>
              </div>
              <Trash2 size={18} />
            </div>
          </div>
        </section>
      </div>
    </div>
  );
};

export default TeacherSettings;

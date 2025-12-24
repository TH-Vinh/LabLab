import React, { useState, useEffect } from "react";
import {
  User,
  Home,
  Camera, // Icon máy ảnh
  Shield,
} from "lucide-react";
import api from "../../../services/api";
import "./TeacherProfile.css";

const TeacherProfile = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const handleFileChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      alert(`Đã chọn ảnh: ${file.name}`);
      console.log("File selected:", file);
    }
  };

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const token = localStorage.getItem("token");

        if (!token) {
          setError("Bạn chưa đăng nhập!");
          setLoading(false);
          return;
        }

        const response = await api.get("/users/profile", {
          headers: { Authorization: `Bearer ${token}` },
        });

        setProfile(response.data);
      } catch (err) {
        console.error("Lỗi tải profile:", err);
        setError("Không thể tải thông tin. Vui lòng thử lại!");
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, []);

  if (loading) return <div className="loading-state">Đang tải dữ liệu...</div>;
  if (error) return <div className="error-state">{error}</div>;
  if (!profile) return null;

  return (
    <div className="profile-container">
      <div className="profile-card">
        <div className="profile-banner"></div>

        <div className="profile-header-main">
          <div className="avatar-wrapper">
            {/* 1. Hiển thị ảnh Avatar */}
            {profile.avatar ? (
              <img src={profile.avatar} alt="Avatar" className="avatar-image" />
            ) : (
              <div className="avatar-placeholder">
                {profile.fullName
                  ? profile.fullName.charAt(0).toUpperCase()
                  : "U"}
              </div>
            )}

            <input
              type="file"
              id="upload-avatar-input"
              hidden
              accept="image/*"
              onChange={handleFileChange}
            />
            
            <label htmlFor="upload-avatar-input" className="btn-upload-icon">
              <Camera size={18} strokeWidth={2.5} />
            </label>
          </div>
          {/* ----------------------------------------- */}

          <div className="header-info">
            <h1>{profile.fullName}</h1>
            <div className="role-tag">
              <Shield size={14} style={{ marginRight: 5 }} />
              {profile.role ? profile.role.replace("ROLE_", "") : "N/A"}
            </div>
          </div>
        </div>

        <div className="profile-details-grid">
          {/* Cột 1: Thông tin cá nhân */}
          <div className="detail-group">
            <h3>
              <User size={18} /> Thông tin cá nhân
            </h3>
            <div className="detail-item">
              <label>Họ và tên</label>
              <p>{profile.fullName || "Chưa cập nhật"}</p>
            </div>
            <div className="detail-item">
              <label>Số điện thoại</label>
              <p>{profile.phoneNumber || "Chưa cập nhật"}</p>
            </div>
            <div className="detail-item">
              <label>Email liên hệ</label>
              <p>{profile.email || "Chưa cập nhật"}</p>
            </div>
          </div>

          {/* Cột 2: Đơn vị công tác */}
          <div className="detail-group">
            <h3>
              <Home size={18} /> Đơn vị công tác
            </h3>
            <div className="detail-item">
              <label>Khoa</label>
              <p>{profile.faculty || "Chưa cập nhật"}</p>
            </div>
            <div className="detail-item">
              <label>Bộ môn / Phòng ban</label>
              <p>{profile.department || "Chưa cập nhật"}</p>
            </div>
            <div className="detail-item">
              <label>Trạng thái</label>
              <p className="status-online">Đang hoạt động</p>
            </div>
          </div>
        </div>

        <div className="profile-footer">
          <button className="btn-edit-main">Cập nhật thông tin</button>
          <button className="btn-outline">Đổi mật khẩu</button>
        </div>
      </div>
    </div>
  );
};

export default TeacherProfile;

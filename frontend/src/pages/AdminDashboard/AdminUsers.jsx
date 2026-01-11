import React, { useEffect, useState } from "react";
import api from "../../services/api";
import "./AdminDashboard.css";

const AdminUsers = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [userToDelete, setUserToDelete] = useState(null);
  const [searchKeyword, setSearchKeyword] = useState("");
  const [filterRole, setFilterRole] = useState("ALL"); // ALL, ROLE_ADMIN, ROLE_TEACHER
  const [filterStatus, setFilterStatus] = useState("ALL"); // ALL, ACTIVE, INACTIVE

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const response = await api.get("/admin/users");
      setUsers(response.data);
    } catch (error) {
      console.error("Error fetching users:", error);
      alert("Không thể tải danh sách người dùng!");
    } finally {
      setLoading(false);
    }
  };

  const handleToggleStatus = async (userId, currentStatus, username) => {
    const action = !currentStatus ? "kích hoạt" : "vô hiệu hóa";
    if (!window.confirm(`Bạn có chắc muốn ${action} tài khoản "${username}"?`)) {
      return;
    }

    try {
      const response = await api.put(`/admin/users/${userId}/status?isActive=${!currentStatus}`);
      alert(`Đã ${action} tài khoản "${username}" thành công!`);
      fetchUsers();
    } catch (error) {
      console.error("Error updating user status:", error);
      let errorMessage = "Có lỗi xảy ra!";
      
      if (error.response?.data) {
        // Xử lý ApiResponse object
        if (error.response.data.error) {
          errorMessage = error.response.data.error;
        } else if (error.response.data.message) {
          errorMessage = error.response.data.message;
        } else if (typeof error.response.data === 'string') {
          errorMessage = error.response.data;
        }
      }
      
      alert(errorMessage);
    }
  };

  const handleDeleteClick = (user) => {
    setUserToDelete(user);
    setShowDeleteModal(true);
  };

  const handleDeleteConfirm = async () => {
    if (!userToDelete) return;

    try {
      const response = await api.delete(`/admin/users/${userToDelete.userId}`);
      
      // Kiểm tra response - ApiResponse object
      if (response.data) {
        if (response.data.error) {
          alert(response.data.error);
          setShowDeleteModal(false);
          setUserToDelete(null);
          return;
        } else if (response.data.message) {
          alert(response.data.message);
        }
      }
      
      alert(`Đã xóa tài khoản "${userToDelete.username}" thành công!`);
      fetchUsers();
    } catch (error) {
      console.error("Error deleting user:", error);
      let errorMessage = "Có lỗi xảy ra khi xóa tài khoản!";
      
      if (error.response?.data) {
        // Xử lý ApiResponse object
        if (error.response.data.error) {
          errorMessage = error.response.data.error;
        } else if (error.response.data.message) {
          errorMessage = error.response.data.message;
        } else if (typeof error.response.data === 'string') {
          errorMessage = error.response.data;
        }
      }
      
      alert(errorMessage);
    } finally {
      setShowDeleteModal(false);
      setUserToDelete(null);
    }
  };

  const handleDeleteCancel = () => {
    setShowDeleteModal(false);
    setUserToDelete(null);
  };

  // Lọc danh sách người dùng - tự động filter khi searchKeyword thay đổi
  const filteredUsers = users.filter((user) => {
    const keyword = searchKeyword.toLowerCase().trim();
    const matchesSearch =
      !keyword ||
      user.username?.toLowerCase().includes(keyword) ||
      user.fullName?.toLowerCase().includes(keyword) ||
      user.email?.toLowerCase().includes(keyword) ||
      user.phoneNumber?.toLowerCase().includes(keyword) ||
      user.faculty?.toLowerCase().includes(keyword) ||
      user.department?.toLowerCase().includes(keyword);

    const matchesRole =
      filterRole === "ALL" ||
      (filterRole === "ROLE_ADMIN" && user.role === "ROLE_ADMIN") ||
      (filterRole === "ROLE_TEACHER" && user.role === "ROLE_TEACHER");

    const matchesStatus =
      filterStatus === "ALL" ||
      (filterStatus === "ACTIVE" && user.isActive) ||
      (filterStatus === "INACTIVE" && !user.isActive);

    return matchesSearch && matchesRole && matchesStatus;
  });

  // Lấy username hiện tại từ localStorage
  const getCurrentUsername = () => {
    // Có thể lưu username khi login, hoặc decode từ token
    // Tạm thời dùng cách đơn giản
    return null; // Sẽ được xử lý ở backend
  };

  if (loading) {
    return <div className="admin-loading">Đang tải danh sách người dùng...</div>;
  }

  return (
    <div>
      {/* Thanh tìm kiếm và lọc */}
      <div className="admin-form-container" style={{ marginBottom: "24px" }}>
        <div className="admin-search-bar">
          <input
            type="text"
            className="admin-search-input"
            placeholder="Tìm kiếm theo username, tên, email..."
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
          />
          <select
            className="admin-select"
            value={filterRole}
            onChange={(e) => setFilterRole(e.target.value)}
          >
            <option value="ALL">Tất cả vai trò</option>
            <option value="ROLE_ADMIN">Admin</option>
            <option value="ROLE_TEACHER">Giảng viên</option>
          </select>
          <select
            className="admin-select"
            value={filterStatus}
            onChange={(e) => setFilterStatus(e.target.value)}
          >
            <option value="ALL">Tất cả trạng thái</option>
            <option value="ACTIVE">Hoạt động</option>
            <option value="INACTIVE">Vô hiệu hóa</option>
          </select>
        </div>
      </div>

      {/* Bảng danh sách người dùng */}
      <div className="admin-table-container">
        <div className="admin-section-header">
          <h3>Danh sách người dùng</h3>
          <span>Tổng: {filteredUsers.length} / {users.length}</span>
        </div>

        {filteredUsers.length === 0 ? (
          <div className="admin-empty">
            <p>Không tìm thấy người dùng nào.</p>
          </div>
        ) : (
          <table className="admin-table">
            <thead>
              <tr>
                <th>Username</th>
                <th>Họ tên</th>
                <th>Email</th>
                <th>Khoa</th>
                <th style={{ textAlign: "center" }}>Vai trò</th>
                <th style={{ textAlign: "center" }}>Trạng thái</th>
                <th style={{ textAlign: "center" }}>Hành động</th>
              </tr>
            </thead>
            <tbody>
              {filteredUsers.map((user) => (
                <tr key={user.userId}>
                  <td><strong>{user.username}</strong></td>
                  <td>{user.fullName || "N/A"}</td>
                  <td>{user.email || "N/A"}</td>
                  <td>{user.faculty || "N/A"}</td>
                  <td style={{ textAlign: "center" }}>
                    <span className="admin-badge">
                      {user.role === "ROLE_ADMIN" ? "Admin" : "Giảng viên"}
                    </span>
                  </td>
                  <td style={{ textAlign: "center" }}>
                    <span className={`admin-badge ${user.isActive ? "admin-badge-success" : ""}`}>
                      {user.isActive ? "Hoạt động" : "Vô hiệu hóa"}
                    </span>
                  </td>
                  <td style={{ textAlign: "center" }}>
                    <div style={{ display: "flex", gap: "8px", justifyContent: "center" }}>
                      <button
                        className="admin-button"
                        onClick={() => handleToggleStatus(user.userId, user.isActive, user.username)}
                      >
                        {user.isActive ? "Vô hiệu hóa" : "Kích hoạt"}
                      </button>
                      <button
                        className={`admin-button ${user.role === "ROLE_ADMIN" ? "" : "admin-button-danger"}`}
                        onClick={() => handleDeleteClick(user)}
                        disabled={user.role === "ROLE_ADMIN"}
                        title={
                          user.role === "ROLE_ADMIN"
                            ? "Không thể xóa tài khoản Admin"
                            : "Xóa tài khoản"
                        }
                      >
                        Xóa
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Modal xác nhận xóa */}
      {showDeleteModal && userToDelete && (
        <div className="admin-modal-overlay" onClick={handleDeleteCancel}>
          <div className="admin-modal" onClick={(e) => e.stopPropagation()}>
            <h3>Xác nhận xóa tài khoản</h3>
            <p>
              Bạn có chắc chắn muốn xóa tài khoản <strong>{userToDelete.username}</strong>?
            </p>
            {userToDelete.fullName && (
              <p>
                <strong>Họ tên:</strong> {userToDelete.fullName}
              </p>
            )}
            <p style={{ color: "#d32f2f", fontSize: "14px", fontWeight: "500" }}>
              Hành động này không thể hoàn tác!
            </p>
            <div className="admin-modal-actions">
              <button className="admin-button" onClick={handleDeleteCancel}>
                Hủy
              </button>
              <button className="admin-button admin-button-danger" onClick={handleDeleteConfirm}>
                Xóa
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminUsers;

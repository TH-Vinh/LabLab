import React, { useEffect, useState } from "react";
import api from "../../services/api";
import "./AdminDashboard.css";

const AdminUsers = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showAddForm, setShowAddForm] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [userToDelete, setUserToDelete] = useState(null);
  const [searchKeyword, setSearchKeyword] = useState("");
  const [filterRole, setFilterRole] = useState("ALL");

  const [newUser, setNewUser] = useState({
    username: "",
    password: "",
    fullName: "",
    email: "",
    role: "ROLE_TEACHER",
  });

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

  const handleAddUser = async (e) => {
    e.preventDefault();
    try {
      await api.post("/admin/users", newUser);
      alert("Tạo tài khoản thành công!");
      setShowAddForm(false);
      setNewUser({
        username: "",
        password: "",
        fullName: "",
        email: "",
        role: "ROLE_TEACHER",
      });
      fetchUsers();
    } catch (error) {
      alert(error.response?.data?.message || "Lỗi khi tạo tài khoản!");
    }
  };

  const handleToggleStatus = async (userId, currentStatus, username) => {
    const action = !currentStatus ? "kích hoạt" : "vô hiệu hóa";
    if (!window.confirm(`Bạn có chắc muốn ${action} tài khoản "${username}"?`))
      return;
    try {
      await api.put(`/admin/users/${userId}/status?isActive=${!currentStatus}`);
      alert(`Thao tác thành công!`);
      fetchUsers();
    } catch (error) {
      alert("Lỗi khi cập nhật trạng thái!");
    }
  };

  const handleDeleteConfirm = async () => {
    if (!userToDelete) return;
    try {
      await api.delete(`/admin/users/${userToDelete.userId}`);
      alert(`Đã xóa tài khoản thành công!`);
      fetchUsers();
    } catch (error) {
      alert("Lỗi khi xóa tài khoản!");
    } finally {
      setShowDeleteModal(false);
      setUserToDelete(null);
    }
  };

  const filteredUsers = users.filter((user) => {
    const keyword = searchKeyword.toLowerCase().trim();
    const matchesSearch =
      !keyword ||
      user.username?.toLowerCase().includes(keyword) ||
      user.fullName?.toLowerCase().includes(keyword);
    const matchesRole = filterRole === "ALL" || user.role === filterRole;
    return matchesSearch && matchesRole;
  });

  if (loading) return <div className="admin-loading">Đang tải dữ liệu...</div>;

  return (
    <div className="admin-users-container">
      <div className="admin-search-bar" style={{ marginBottom: "24px" }}>
        <input
          type="text"
          className="admin-search-input"
          placeholder="Tìm kiếm người dùng..."
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
        <button
          className="admin-button admin-button-primary"
          onClick={() => setShowAddForm(true)}
        >
          Thêm người dùng
        </button>
      </div>

      {showAddForm && (
        <div className="admin-form-container" style={{ marginBottom: "24px" }}>
          <h3>Tạo tài khoản mới</h3>
          <form onSubmit={handleAddUser}>
            <div
              style={{
                display: "grid",
                gridTemplateColumns: "1fr 1fr",
                gap: "12px",
                marginBottom: "16px",
              }}
            >
              <input
                type="text"
                className="admin-input"
                placeholder="Username"
                value={newUser.username}
                onChange={(e) =>
                  setNewUser({ ...newUser, username: e.target.value })
                }
                required
              />
              <input
                type="password"
                className="admin-input"
                placeholder="Mật khẩu"
                value={newUser.password}
                onChange={(e) =>
                  setNewUser({ ...newUser, password: e.target.value })
                }
                required
              />
              <input
                type="text"
                className="admin-input"
                placeholder="Họ và tên"
                value={newUser.fullName}
                onChange={(e) =>
                  setNewUser({ ...newUser, fullName: e.target.value })
                }
                required
              />
              <input
                type="email"
                className="admin-input"
                placeholder="Email"
                value={newUser.email}
                onChange={(e) =>
                  setNewUser({ ...newUser, email: e.target.value })
                }
                required
              />
              <select
                className="admin-input"
                value={newUser.role}
                onChange={(e) =>
                  setNewUser({ ...newUser, role: e.target.value })
                }
              >
                <option value="ROLE_TEACHER">Giảng viên</option>
                <option value="ROLE_ADMIN">Admin</option>
              </select>
            </div>
            <div style={{ display: "flex", gap: "12px" }}>
              <button
                type="submit"
                className="admin-button admin-button-primary"
              >
                Tạo mới
              </button>
              <button
                type="button"
                className="admin-button"
                onClick={() => setShowAddForm(false)}
              >
                Hủy
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="admin-table-container">
        <table className="admin-table">
          <thead>
            <tr>
              <th>Username</th>
              <th>Họ tên</th>
              <th>Vai trò</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {filteredUsers.map((user) => (
              <tr key={user.userId}>
                <td>
                  <strong>{user.username}</strong>
                </td>
                <td>{user.fullName || "N/A"}</td>
                <td>{user.role === "ROLE_ADMIN" ? "Admin" : "Giảng viên"}</td>
                <td>
                  <span
                    className={`admin-badge ${user.isActive ? "admin-badge-success" : "admin-badge-error"}`}
                  >
                    {user.isActive ? "Hoạt động" : "Vô hiệu hóa"}
                  </span>
                </td>
                <td>
                  <div style={{ display: "flex", gap: "8px" }}>
                    <button
                      className="admin-button"
                      onClick={() =>
                        handleToggleStatus(
                          user.userId,
                          user.isActive,
                          user.username,
                        )
                      }
                    >
                      {user.isActive ? "Vô hiệu" : "Kích hoạt"}
                    </button>
                    <button
                      className="admin-button admin-button-danger"
                      onClick={() => {
                        setUserToDelete(user);
                        setShowDeleteModal(true);
                      }}
                      disabled={user.role === "ROLE_ADMIN"}
                    >
                      Xóa
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showDeleteModal && (
        <div className="admin-modal-overlay">
          <div className="admin-modal">
            <h3>Xác nhận xóa</h3>
            <p>
              Xóa tài khoản <strong>{userToDelete?.username}</strong>?
            </p>
            <div className="admin-modal-actions">
              <button
                className="admin-button"
                onClick={() => setShowDeleteModal(false)}
              >
                Hủy
              </button>
              <button
                className="admin-button admin-button-danger"
                onClick={handleDeleteConfirm}
              >
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

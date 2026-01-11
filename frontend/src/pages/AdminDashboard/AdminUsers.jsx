import React, { useEffect, useState } from "react";
import api from "../../services/api";
import "./AdminDashboard.css";

const AdminUsers = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

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
    } finally {
      setLoading(false);
    }
  };

  const handleToggleStatus = async (userId, currentStatus) => {
    try {
      await api.put(`/admin/users/${userId}/status?isActive=${!currentStatus}`);
      alert(`Đã ${!currentStatus ? "kích hoạt" : "vô hiệu hóa"} người dùng thành công!`);
      fetchUsers();
    } catch (error) {
      console.error("Error updating user status:", error);
      alert("Có lỗi xảy ra!");
    }
  };

  const handleDelete = async (userId) => {
    if (!window.confirm("Bạn có chắc muốn xóa người dùng này?")) return;
    try {
      await api.delete(`/admin/users/${userId}`);
      alert("Xóa người dùng thành công!");
      fetchUsers();
    } catch (error) {
      console.error("Error deleting user:", error);
      alert("Có lỗi xảy ra!");
    }
  };

  if (loading) {
    return <div>Đang tải...</div>;
  }

  return (
    <div>
      <div style={{ background: "white", padding: "20px", borderRadius: "8px" }}>
        <h3>👥 Danh sách người dùng</h3>
        {users.length === 0 ? (
          <p>Không có người dùng nào.</p>
        ) : (
          <table
            style={{
              width: "100%",
              borderCollapse: "collapse",
              marginTop: "15px",
            }}
          >
            <thead style={{ background: "#f1f5f9" }}>
              <tr>
                <th style={{ padding: "10px", textAlign: "left" }}>Username</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Họ tên</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Email</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Khoa</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Vai trò</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Trạng thái</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Hành động</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr key={user.userId}>
                  <td style={{ padding: "10px", borderBottom: "1px solid #eee" }}>
                    {user.username}
                  </td>
                  <td>{user.fullName || "N/A"}</td>
                  <td>{user.email || "N/A"}</td>
                  <td>{user.faculty || "N/A"}</td>
                  <td>
                    <span
                      style={{
                        padding: "4px 8px",
                        borderRadius: "4px",
                        fontSize: "12px",
                        background: user.role === "ROLE_ADMIN" ? "#ef4444" : "#60a5fa",
                        color: "white",
                      }}
                    >
                      {user.role === "ROLE_ADMIN" ? "Admin" : "Giảng viên"}
                    </span>
                  </td>
                  <td>
                    <span
                      style={{
                        padding: "4px 8px",
                        borderRadius: "4px",
                        fontSize: "12px",
                        background: user.isActive ? "#10b981" : "#64748b",
                        color: "white",
                      }}
                    >
                      {user.isActive ? "Hoạt động" : "Vô hiệu hóa"}
                    </span>
                  </td>
                  <td>
                    <button
                      onClick={() => handleToggleStatus(user.userId, user.isActive)}
                      style={{
                        padding: "4px 8px",
                        background: user.isActive ? "#f59e0b" : "#10b981",
                        color: "white",
                        border: "none",
                        borderRadius: "4px",
                        cursor: "pointer",
                        marginRight: "5px",
                      }}
                    >
                      {user.isActive ? "Vô hiệu hóa" : "Kích hoạt"}
                    </button>
                    <button
                      onClick={() => handleDelete(user.userId)}
                      style={{
                        padding: "4px 8px",
                        background: "#ef4444",
                        color: "white",
                        border: "none",
                        borderRadius: "4px",
                        cursor: "pointer",
                      }}
                    >
                      Xóa
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default AdminUsers;


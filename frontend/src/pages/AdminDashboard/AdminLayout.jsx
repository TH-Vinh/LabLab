import React from "react";
import { Routes, Route, useNavigate, useLocation } from "react-router-dom";
import AdminOverview from "./AdminOverview";
import AdminRentTickets from "./AdminRentTickets";
import AdminChemicals from "./AdminChemicals";
import AdminDevices from "./AdminDevices";
import AdminUsers from "./AdminUsers";
import "./AdminDashboard.css";

const AdminLayout = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  const menuItems = [
    { path: "/admin/overview", label: "📊 Tổng quan hệ thống", component: AdminOverview },
    { path: "/admin/tickets", label: "✅ Duyệt phiếu mượn", component: AdminRentTickets },
    { path: "/admin/chemicals", label: "🧪 Quản lý Hóa chất", component: AdminChemicals },
    { path: "/admin/devices", label: "🔬 Quản lý Thiết bị", component: AdminDevices },
    { path: "/admin/users", label: "👥 Quản lý Người dùng", component: AdminUsers },
  ];

  const isActive = (path) => {
    return location.pathname === path || location.pathname.startsWith(path + "/");
  };

  return (
    <div className="admin-container">
      {/* Sidebar */}
      <aside className="admin-sidebar">
        <div className="admin-brand">🛡️ LabLab ADMIN</div>
        {menuItems.map((item) => (
          <div
            key={item.path}
            className={`admin-menu-item ${isActive(item.path) ? "active" : ""}`}
            onClick={() => navigate(item.path)}
          >
            {item.label}
          </div>
        ))}
      </aside>

      {/* Main Content */}
      <main className="admin-main">
        <div className="admin-header">
          <h2>Quản Trị Hệ Thống</h2>
          <button onClick={handleLogout} className="btn-admin-logout">
            Đăng xuất
          </button>
        </div>

        <Routes>
          <Route index element={<AdminOverview />} />
          <Route path="overview" element={<AdminOverview />} />
          <Route path="tickets" element={<AdminRentTickets />} />
          <Route path="chemicals" element={<AdminChemicals />} />
          <Route path="devices" element={<AdminDevices />} />
          <Route path="users" element={<AdminUsers />} />
        </Routes>
      </main>
    </div>
  );
};

export default AdminLayout;


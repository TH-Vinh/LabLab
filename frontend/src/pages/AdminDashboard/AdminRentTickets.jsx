import React, { useEffect, useState } from "react";
import api from "../../services/api";
import "./AdminDashboard.css";

const AdminRentTickets = () => {
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState("PENDING"); // PENDING, APPROVED, REJECTED

  useEffect(() => {
    fetchTickets();
  }, [filter]);

  const fetchTickets = async () => {
    try {
      setLoading(true);
      const response = await api.get(`/admin/tickets${filter ? `?status=${filter}` : ""}`);
      setTickets(response.data);
    } catch (error) {
      console.error("Error fetching tickets:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateStatus = async (ticketId, status) => {
    try {
      await api.put(`/admin/tickets/${ticketId}/status`, { status });
      alert(`Đã ${status === "APPROVED" ? "duyệt" : "từ chối"} phiếu mượn thành công!`);
      fetchTickets();
    } catch (error) {
      console.error("Error updating ticket status:", error);
      alert("Có lỗi xảy ra!");
    }
  };

  if (loading) {
    return <div className="admin-loading">Đang tải...</div>;
  }

  return (
    <div>
      <div className="admin-search-bar">
        <button
          className={`admin-button ${filter === "PENDING" ? "admin-button-primary" : ""}`}
          onClick={() => setFilter("PENDING")}
        >
          Chờ duyệt
        </button>
        <button
          className={`admin-button ${filter === "APPROVED" ? "admin-button-primary" : ""}`}
          onClick={() => setFilter("APPROVED")}
        >
          Đã duyệt
        </button>
        <button
          className={`admin-button ${filter === "REJECTED" ? "admin-button-primary" : ""}`}
          onClick={() => setFilter("REJECTED")}
        >
          Đã từ chối
        </button>
        <button
          className={`admin-button ${filter === "" ? "admin-button-primary" : ""}`}
          onClick={() => setFilter("")}
        >
          Tất cả
        </button>
      </div>

      <div className="admin-table-container">
        <div className="admin-section-header">
          <h3>Danh sách phiếu mượn</h3>
        </div>
        {tickets.length === 0 ? (
          <div className="admin-empty">
            <p>Không có phiếu mượn nào.</p>
          </div>
        ) : (
          <table className="admin-table">
            <thead>
              <tr>
                <th>Người mượn</th>
                <th>Phòng</th>
                <th>Vật tư</th>
                <th>Ngày tạo</th>
                <th>Trạng thái</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody>
              {tickets.map((ticket) => (
                <tr key={ticket.ticketId}>
                  <td>{ticket.fullName || ticket.username}</td>
                  <td>{ticket.roomName || "N/A"}</td>
                  <td>
                    {ticket.details?.map((detail) => (
                      <div key={detail.detailId} style={{ marginBottom: "4px" }}>
                        {detail.itemName} ({detail.quantity} {detail.unit})
                      </div>
                    ))}
                  </td>
                  <td>
                    {ticket.createdDate
                      ? new Date(ticket.createdDate).toLocaleDateString("vi-VN")
                      : "N/A"}
                  </td>
                  <td>
                    <span
                      className={`admin-badge ${
                        ticket.status === "APPROVED"
                          ? "admin-badge-success"
                          : ticket.status === "REJECTED"
                          ? "admin-badge-error"
                          : "admin-badge-warning"
                      }`}
                    >
                      {ticket.status === "APPROVED"
                        ? "Đã duyệt"
                        : ticket.status === "REJECTED"
                        ? "Đã từ chối"
                        : "Chờ duyệt"}
                    </span>
                  </td>
                  <td>
                    {ticket.status === "PENDING" && (
                      <>
                        <button
                          className="btn-approve"
                          onClick={() => handleUpdateStatus(ticket.ticketId, "APPROVED")}
                        >
                          Duyệt
                        </button>
                        <button
                          className="btn-reject"
                          onClick={() => handleUpdateStatus(ticket.ticketId, "REJECTED")}
                        >
                          Từ chối
                        </button>
                      </>
                    )}
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

export default AdminRentTickets;

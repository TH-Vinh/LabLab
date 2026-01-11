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
    return <div>Đang tải...</div>;
  }

  return (
    <div>
      <div style={{ marginBottom: "20px", display: "flex", gap: "10px" }}>
        <button
          onClick={() => setFilter("PENDING")}
          style={{
            padding: "8px 16px",
            background: filter === "PENDING" ? "#60a5fa" : "#e2e8f0",
            color: filter === "PENDING" ? "white" : "black",
            border: "none",
            borderRadius: "4px",
            cursor: "pointer",
          }}
        >
          Chờ duyệt
        </button>
        <button
          onClick={() => setFilter("APPROVED")}
          style={{
            padding: "8px 16px",
            background: filter === "APPROVED" ? "#10b981" : "#e2e8f0",
            color: filter === "APPROVED" ? "white" : "black",
            border: "none",
            borderRadius: "4px",
            cursor: "pointer",
          }}
        >
          Đã duyệt
        </button>
        <button
          onClick={() => setFilter("REJECTED")}
          style={{
            padding: "8px 16px",
            background: filter === "REJECTED" ? "#ef4444" : "#e2e8f0",
            color: filter === "REJECTED" ? "white" : "black",
            border: "none",
            borderRadius: "4px",
            cursor: "pointer",
          }}
        >
          Đã từ chối
        </button>
        <button
          onClick={() => setFilter("")}
          style={{
            padding: "8px 16px",
            background: filter === "" ? "#64748b" : "#e2e8f0",
            color: filter === "" ? "white" : "black",
            border: "none",
            borderRadius: "4px",
            cursor: "pointer",
          }}
        >
          Tất cả
        </button>
      </div>

      <div style={{ background: "white", padding: "20px", borderRadius: "8px" }}>
        <h3>📋 Danh sách phiếu mượn</h3>
        {tickets.length === 0 ? (
          <p>Không có phiếu mượn nào.</p>
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
                <th style={{ padding: "10px", textAlign: "left" }}>Người mượn</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Phòng</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Vật tư</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Ngày tạo</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Trạng thái</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Hành động</th>
              </tr>
            </thead>
            <tbody>
              {tickets.map((ticket) => (
                <tr key={ticket.ticketId}>
                  <td style={{ padding: "10px", borderBottom: "1px solid #eee" }}>
                    {ticket.fullName || ticket.username}
                  </td>
                  <td>{ticket.roomName || "N/A"}</td>
                  <td>
                    {ticket.details?.map((detail) => (
                      <div key={detail.detailId}>
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
                      style={{
                        padding: "4px 8px",
                        borderRadius: "4px",
                        fontSize: "12px",
                        background:
                          ticket.status === "APPROVED"
                            ? "#10b981"
                            : ticket.status === "REJECTED"
                            ? "#ef4444"
                            : "#f59e0b",
                        color: "white",
                      }}
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


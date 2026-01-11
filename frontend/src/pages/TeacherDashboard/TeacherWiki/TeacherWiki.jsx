import React, { useState, useEffect } from "react";
<<<<<<< HEAD
// Import axios instance từ folder services của bạn
=======
import { useSearchParams } from "react-router-dom";
>>>>>>> origin/main
import api from "../../../services/api";

import {
  Search,
  FlaskConical,
  Microscope,
  TestTube,
  Filter,
  ArrowRight,
  X,
  Package,
  Factory,
  Calendar,
} from "lucide-react";
import "./TeacherWiki.css";

const TeacherWiki = () => {
<<<<<<< HEAD
  const [items, setItems] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
=======
  const [searchParams] = useSearchParams();
  const queryKeyword = searchParams.get("keyword") || "";

  const [items, setItems] = useState([]);
  const [searchTerm, setSearchTerm] = useState(queryKeyword);
>>>>>>> origin/main
  const [activeTab, setActiveTab] = useState("all");
  const [selectedItem, setSelectedItem] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
<<<<<<< HEAD
=======
    if (queryKeyword) {
      setSearchTerm(queryKeyword);
    }
  }, [queryKeyword]);

  useEffect(() => {
>>>>>>> origin/main
    const handleKeyDown = (event) => {
      if (event.key === "Escape") {
        handleCloseDetail();
      }
    };
<<<<<<< HEAD

    if (selectedItem) {
      window.addEventListener("keydown", handleKeyDown);
    }

=======
    if (selectedItem) {
      window.addEventListener("keydown", handleKeyDown);
    }
>>>>>>> origin/main
    return () => {
      window.removeEventListener("keydown", handleKeyDown);
    };
  }, [selectedItem]);

  useEffect(() => {
    const fetchItems = async () => {
      setLoading(true);
      try {
        const token = localStorage.getItem("token");
        if (!token) return;

        let categoryParam = "ALL";
        if (activeTab === "chemical") categoryParam = "CHEMICAL";
        if (activeTab === "machine") categoryParam = "DEVICE";
        if (activeTab === "tool") categoryParam = "TOOL";

        const response = await api.get("/items", {
          headers: { Authorization: `Bearer ${token}` },
          params: {
            category: categoryParam,
            keyword: searchTerm,
          },
        });

        setItems(response.data);
      } catch (error) {
        console.error("Lỗi tải dữ liệu:", error);
      } finally {
        setLoading(false);
      }
    };

    const timeoutId = setTimeout(() => {
      fetchItems();
    }, 300);

    return () => clearTimeout(timeoutId);
  }, [activeTab, searchTerm]);

  const getEmoji = (type) => {
    if (type === "CHEMICAL") return "🧪";
    if (type === "DEVICE") return "🔬";
    if (type === "TOOL") return "🛠️";
    return "📦";
  };

  const getTypeClass = (type) => {
    if (type === "CHEMICAL") return "chemical";
    if (type === "DEVICE") return "machine";
    if (type === "TOOL") return "tool";
    return "";
  };

  const getTypeName = (type) => {
    if (type === "CHEMICAL") return "Hóa chất";
    if (type === "DEVICE") return "Máy móc";
    if (type === "TOOL") return "Dụng cụ";
    return type;
  };

  const handleOpenDetail = (item) => setSelectedItem(item);
  const handleCloseDetail = () => setSelectedItem(null);

  return (
    <div className="wiki-container">
      <div className="wiki-header">
        <h2>📖 Từ điển Thiết bị & Hóa chất</h2>
        <p>
          Tra cứu thông tin kỹ thuật, công thức và đặc tính của vật tư phòng thí
          nghiệm.
        </p>
      </div>

      <div className="wiki-controls">
        <div className="search-box">
          <Search size={20} color="#888" />
          <input
            type="text"
            placeholder="Nhập tên hóa chất, công thức hoặc máy móc..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        <div className="category-tabs">
          <button
            className={`tab-btn ${activeTab === "all" ? "active" : ""}`}
            onClick={() => setActiveTab("all")}
          >
            Tất cả
          </button>
          <button
            className={`tab-btn ${activeTab === "chemical" ? "active" : ""}`}
            onClick={() => setActiveTab("chemical")}
          >
            <FlaskConical size={16} /> Hóa chất
          </button>
          <button
            className={`tab-btn ${activeTab === "machine" ? "active" : ""}`}
            onClick={() => setActiveTab("machine")}
          >
            <Microscope size={16} /> Máy móc
          </button>
          <button
            className={`tab-btn ${activeTab === "tool" ? "active" : ""}`}
            onClick={() => setActiveTab("tool")}
          >
            <TestTube size={16} /> Dụng cụ
          </button>
        </div>
      </div>

      <div className="wiki-grid">
        {loading ? (
          <div className="no-result">
            <p>⏳ Đang tải dữ liệu...</p>
          </div>
        ) : items.length > 0 ? (
          items.map((item) => (
            <div
              className="wiki-card"
              key={item.itemId}
              onClick={() => handleOpenDetail(item)}
            >
              <div
                className={`card-icon-bg bg-${getTypeClass(item.categoryType)}`}
              >
                <span className="emoji-icon">
                  {getEmoji(item.categoryType)}
                </span>
              </div>
              <div className="card-content">
                <div className="card-badges">
                  <span
                    className={`badge-type type-${getTypeClass(
                      item.categoryType
                    )}`}
                  >
                    {getTypeName(item.categoryType)}
                  </span>
                </div>
                <h3>{item.name}</h3>
                <p style={{ fontSize: "13px", color: "#666" }}>
                  Đơn vị: <strong>{item.unit}</strong>
                </p>
                <button
                  className="btn-detail"
                  onClick={(e) => {
                    e.stopPropagation();
                    handleOpenDetail(item);
                  }}
                >
                  Xem chi tiết <ArrowRight size={14} />
                </button>
              </div>
            </div>
          ))
        ) : (
          <div className="no-result">
            <Filter size={48} color="#ddd" />
            <p>Không tìm thấy kết quả.</p>
          </div>
        )}
      </div>

      {selectedItem && (
        <div className="wiki-modal-overlay" onClick={handleCloseDetail}>
          <div
            className="wiki-modal-content"
            onClick={(e) => e.stopPropagation()}
          >
            <button className="btn-close-modal" onClick={handleCloseDetail}>
              <X size={24} color="#555" />
            </button>

            <div
              className={`modal-header-bg bg-${getTypeClass(
                selectedItem.categoryType
              )}`}
            >
              <span style={{ fontSize: "50px" }}>
                {getEmoji(selectedItem.categoryType)}
              </span>
            </div>

            <div className="modal-body">
              <div className="modal-badges">
                <span
                  className={`badge-type type-${getTypeClass(
                    selectedItem.categoryType
                  )}`}
                >
                  {getTypeName(selectedItem.categoryType)}
                </span>
              </div>

              <h2>{selectedItem.name}</h2>

              {selectedItem.categoryType === "CHEMICAL" && (
                <>
                  <div className="info-row">
                    <div className="info-label">
                      <FlaskConical size={16} /> Công thức:
                    </div>
                    <div
                      className="info-value"
                      style={{ fontWeight: "bold", fontFamily: "monospace" }}
                    >
                      {selectedItem.formula || "N/A"}
                    </div>
                  </div>
                  <div className="info-row">
                    <div className="info-label">
                      <Package size={16} /> Quy cách:
                    </div>
                    <div className="info-value">
                      {selectedItem.packaging || "N/A"}
                    </div>
                  </div>
                </>
              )}

              <div className="info-row">
                <div className="info-label">
                  <Factory size={16} /> Nhà cung cấp:
                </div>
                <div className="info-value">
                  {selectedItem.supplier || "Không rõ"}
                </div>
              </div>
              <div className="info-row">
                <div className="info-label">
                  <Calendar size={16} /> Năm sử dụng:
                </div>
                <div className="info-value">
                  {selectedItem.yearInUse || "N/A"}
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default TeacherWiki;

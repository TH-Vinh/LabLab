import React, { useEffect, useState, useRef } from "react";
import api from "../../services/api";
import "./AdminDashboard.css";

const AdminChemicals = () => {
  const [chemicals, setChemicals] = useState([]);
  const [allChemicals, setAllChemicals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchKeyword, setSearchKeyword] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [editingChemical, setEditingChemical] = useState(null);
  const [formData, setFormData] = useState({
    itemCode: "",
    name: "",
    unit: "",
    currentQuantity: "",
    formula: "",
    supplier: "",
    packaging: "",
    storageLocation: "",
    originalPrice: "",
  });
  const searchTimeoutRef = useRef(null);

  useEffect(() => {
    fetchChemicals();
  }, []);

  useEffect(() => {
    // Clear previous timeout
    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }

    // Debounce search - wait 300ms after user stops typing
    searchTimeoutRef.current = setTimeout(() => {
      performSearch();
    }, 300);

    return () => {
      if (searchTimeoutRef.current) {
        clearTimeout(searchTimeoutRef.current);
      }
    };
  }, [searchKeyword]);

  const fetchChemicals = async () => {
    try {
      setLoading(true);
      const response = await api.get("/admin/chemicals");
      setAllChemicals(response.data);
      setChemicals(response.data);
    } catch (error) {
      console.error("Error fetching chemicals:", error);
    } finally {
      setLoading(false);
    }
  };

  const performSearch = () => {
    if (!searchKeyword.trim()) {
      setChemicals(allChemicals);
      return;
    }

    const keyword = searchKeyword.toLowerCase().trim();
    const filtered = allChemicals.filter((chemical) => {
      return (
        chemical.name?.toLowerCase().includes(keyword) ||
        chemical.itemCode?.toLowerCase().includes(keyword) ||
        chemical.formula?.toLowerCase().includes(keyword) ||
        chemical.supplier?.toLowerCase().includes(keyword) ||
        chemical.storageLocation?.toLowerCase().includes(keyword)
      );
    });
    setChemicals(filtered);
  };

  const handleCreate = () => {
    setEditingChemical(null);
    setFormData({
      itemCode: "",
      name: "",
      unit: "",
      currentQuantity: "",
      formula: "",
      supplier: "",
      packaging: "",
      storageLocation: "",
      originalPrice: "",
    });
    setShowForm(true);
  };

  const handleEdit = (chemical) => {
    setEditingChemical(chemical);
    setFormData({
      itemCode: chemical.itemCode,
      name: chemical.name,
      unit: chemical.unit || "",
      currentQuantity: chemical.currentQuantity || "",
      formula: chemical.formula || "",
      supplier: chemical.supplier || "",
      packaging: chemical.packaging || "",
      storageLocation: chemical.storageLocation || "",
      originalPrice: chemical.originalPrice || "",
    });
    setShowForm(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...formData,
        currentQuantity: parseFloat(formData.currentQuantity) || 0,
        originalPrice: parseFloat(formData.originalPrice) || 0,
      };

      if (editingChemical) {
        await api.put(`/admin/chemicals/${editingChemical.itemId}`, payload);
        alert("Cập nhật hóa chất thành công!");
      } else {
        await api.post("/admin/chemicals", payload);
        alert("Tạo hóa chất thành công!");
      }
      setShowForm(false);
      fetchChemicals();
    } catch (error) {
      console.error("Error saving chemical:", error);
      let errorMessage = "Có lỗi xảy ra khi lưu hóa chất!";
      
      if (error.response) {
        if (error.response.data) {
          if (error.response.data.error) {
            errorMessage = error.response.data.error;
          } else if (error.response.data.message) {
            errorMessage = error.response.data.message;
          } else if (typeof error.response.data === 'string') {
            errorMessage = error.response.data;
          }
        }
        errorMessage += `\nStatus: ${error.response.status}`;
      } else if (error.message) {
        errorMessage = error.message;
      }
      
      alert(errorMessage);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Bạn có chắc muốn xóa hóa chất này?")) return;
    try {
      await api.delete(`/admin/chemicals/${id}`);
      alert("Xóa hóa chất thành công!");
      fetchChemicals();
    } catch (error) {
      console.error("Error deleting chemical:", error);
      let errorMessage = "Có lỗi xảy ra khi xóa hóa chất!";
      
      if (error.response) {
        if (error.response.data) {
          if (error.response.data.error) {
            errorMessage = error.response.data.error;
          } else if (error.response.data.message) {
            errorMessage = error.response.data.message;
          } else if (typeof error.response.data === 'string') {
            errorMessage = error.response.data;
          }
        }
        errorMessage += `\nStatus: ${error.response.status}`;
      } else if (error.message) {
        errorMessage = error.message;
      }
      
      alert(errorMessage);
    }
  };

  if (loading) {
    return <div className="admin-loading">Đang tải...</div>;
  }

  return (
    <div>
      <div className="admin-search-bar" style={{ marginBottom: "24px" }}>
        <input
          type="text"
          className="admin-search-input"
          placeholder="Tìm kiếm hóa chất (tên, mã, công thức, nhà cung cấp)..."
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
        />
        <button className="admin-button admin-button-primary" onClick={handleCreate}>
          Thêm hóa chất
        </button>
      </div>

      {showForm && (
        <div className="admin-form-container">
          <h3>{editingChemical ? "Sửa hóa chất" : "Thêm hóa chất mới"}</h3>
          <form onSubmit={handleSubmit}>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "12px", marginBottom: "16px" }}>
              <input
                type="text"
                className="admin-input"
                placeholder="Mã hóa chất"
                value={formData.itemCode}
                onChange={(e) => setFormData({ ...formData, itemCode: e.target.value })}
                required
              />
              <input
                type="text"
                className="admin-input"
                placeholder="Tên hóa chất"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                required
              />
              <input
                type="text"
                className="admin-input"
                placeholder="Công thức"
                value={formData.formula}
                onChange={(e) => setFormData({ ...formData, formula: e.target.value })}
              />
              <input
                type="text"
                className="admin-input"
                placeholder="Đơn vị"
                value={formData.unit}
                onChange={(e) => setFormData({ ...formData, unit: e.target.value })}
              />
              <input
                type="number"
                className="admin-input"
                placeholder="Số lượng hiện tại"
                value={formData.currentQuantity}
                onChange={(e) => setFormData({ ...formData, currentQuantity: e.target.value })}
              />
              <input
                type="text"
                className="admin-input"
                placeholder="Nhà cung cấp"
                value={formData.supplier}
                onChange={(e) => setFormData({ ...formData, supplier: e.target.value })}
              />
              <input
                type="text"
                className="admin-input"
                placeholder="Bao bì"
                value={formData.packaging}
                onChange={(e) => setFormData({ ...formData, packaging: e.target.value })}
              />
              <input
                type="text"
                className="admin-input"
                placeholder="Vị trí lưu trữ"
                value={formData.storageLocation}
                onChange={(e) => setFormData({ ...formData, storageLocation: e.target.value })}
              />
              <input
                type="number"
                className="admin-input"
                placeholder="Giá gốc"
                value={formData.originalPrice}
                onChange={(e) => setFormData({ ...formData, originalPrice: e.target.value })}
              />
            </div>
            <div style={{ display: "flex", gap: "12px" }}>
              <button type="submit" className="admin-button admin-button-primary">
                {editingChemical ? "Cập nhật" : "Tạo mới"}
              </button>
              <button type="button" className="admin-button" onClick={() => setShowForm(false)}>
                Hủy
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="admin-table-container">
        <div className="admin-section-header">
          <h3>Danh sách hóa chất</h3>
        </div>
        {chemicals.length === 0 ? (
          <div className="admin-empty">
            <p>Không có hóa chất nào.</p>
          </div>
        ) : (
          <table className="admin-table">
            <thead>
              <tr>
                <th>Mã</th>
                <th>Tên</th>
                <th>Công thức</th>
                <th>Số lượng</th>
                <th>Đơn vị</th>
                <th>Vị trí</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody>
              {chemicals.map((chemical) => (
                <tr key={chemical.itemId}>
                  <td>{chemical.itemCode}</td>
                  <td>{chemical.name}</td>
                  <td>{chemical.formula || "N/A"}</td>
                  <td>{chemical.currentQuantity || 0}</td>
                  <td>{chemical.unit || "N/A"}</td>
                  <td>{chemical.storageLocation || "N/A"}</td>
                  <td>
                    <button
                      className="admin-button"
                      onClick={() => handleEdit(chemical)}
                      style={{ marginRight: "8px" }}
                    >
                      Sửa
                    </button>
                    <button
                      className="admin-button admin-button-danger"
                      onClick={() => handleDelete(chemical.itemId)}
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

export default AdminChemicals;


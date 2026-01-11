import React, { useEffect, useState } from "react";
import api from "../../services/api";
import "./AdminDashboard.css";

const AdminChemicals = () => {
  const [chemicals, setChemicals] = useState([]);
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

  useEffect(() => {
    fetchChemicals();
  }, []);

  const fetchChemicals = async () => {
    try {
      setLoading(true);
      const response = await api.get("/admin/chemicals");
      setChemicals(response.data);
    } catch (error) {
      console.error("Error fetching chemicals:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    try {
      setLoading(true);
      const response = await api.get(`/admin/chemicals?keyword=${searchKeyword}`);
      setChemicals(response.data);
    } catch (error) {
      console.error("Error searching chemicals:", error);
    } finally {
      setLoading(false);
    }
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
      alert("Có lỗi xảy ra!");
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
      alert("Có lỗi xảy ra!");
    }
  };

  if (loading) {
    return <div>Đang tải...</div>;
  }

  return (
    <div>
      <div style={{ marginBottom: "20px", display: "flex", gap: "10px", alignItems: "center" }}>
        <input
          type="text"
          placeholder="Tìm kiếm hóa chất..."
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
          style={{ padding: "8px", flex: 1, borderRadius: "4px", border: "1px solid #ddd" }}
        />
        <button
          onClick={handleSearch}
          style={{
            padding: "8px 16px",
            background: "#60a5fa",
            color: "white",
            border: "none",
            borderRadius: "4px",
            cursor: "pointer",
          }}
        >
          Tìm kiếm
        </button>
        <button
          onClick={handleCreate}
          style={{
            padding: "8px 16px",
            background: "#10b981",
            color: "white",
            border: "none",
            borderRadius: "4px",
            cursor: "pointer",
          }}
        >
          + Thêm hóa chất
        </button>
      </div>

      {showForm && (
        <div
          style={{
            background: "white",
            padding: "20px",
            borderRadius: "8px",
            marginBottom: "20px",
          }}
        >
          <h3>{editingChemical ? "Sửa hóa chất" : "Thêm hóa chất mới"}</h3>
          <form onSubmit={handleSubmit}>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px", marginBottom: "10px" }}>
              <input
                type="text"
                placeholder="Mã hóa chất"
                value={formData.itemCode}
                onChange={(e) => setFormData({ ...formData, itemCode: e.target.value })}
                required
                style={{ padding: "8px", borderRadius: "4px", border: "1px solid #ddd" }}
              />
              <input
                type="text"
                placeholder="Tên hóa chất"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                required
                style={{ padding: "8px", borderRadius: "4px", border: "1px solid #ddd" }}
              />
              <input
                type="text"
                placeholder="Công thức"
                value={formData.formula}
                onChange={(e) => setFormData({ ...formData, formula: e.target.value })}
                style={{ padding: "8px", borderRadius: "4px", border: "1px solid #ddd" }}
              />
              <input
                type="text"
                placeholder="Đơn vị"
                value={formData.unit}
                onChange={(e) => setFormData({ ...formData, unit: e.target.value })}
                style={{ padding: "8px", borderRadius: "4px", border: "1px solid #ddd" }}
              />
              <input
                type="number"
                placeholder="Số lượng hiện tại"
                value={formData.currentQuantity}
                onChange={(e) => setFormData({ ...formData, currentQuantity: e.target.value })}
                style={{ padding: "8px", borderRadius: "4px", border: "1px solid #ddd" }}
              />
              <input
                type="text"
                placeholder="Nhà cung cấp"
                value={formData.supplier}
                onChange={(e) => setFormData({ ...formData, supplier: e.target.value })}
                style={{ padding: "8px", borderRadius: "4px", border: "1px solid #ddd" }}
              />
              <input
                type="text"
                placeholder="Bao bì"
                value={formData.packaging}
                onChange={(e) => setFormData({ ...formData, packaging: e.target.value })}
                style={{ padding: "8px", borderRadius: "4px", border: "1px solid #ddd" }}
              />
              <input
                type="text"
                placeholder="Vị trí lưu trữ"
                value={formData.storageLocation}
                onChange={(e) => setFormData({ ...formData, storageLocation: e.target.value })}
                style={{ padding: "8px", borderRadius: "4px", border: "1px solid #ddd" }}
              />
              <input
                type="number"
                placeholder="Giá gốc"
                value={formData.originalPrice}
                onChange={(e) => setFormData({ ...formData, originalPrice: e.target.value })}
                style={{ padding: "8px", borderRadius: "4px", border: "1px solid #ddd" }}
              />
            </div>
            <div style={{ display: "flex", gap: "10px" }}>
              <button
                type="submit"
                style={{
                  padding: "8px 16px",
                  background: "#10b981",
                  color: "white",
                  border: "none",
                  borderRadius: "4px",
                  cursor: "pointer",
                }}
              >
                {editingChemical ? "Cập nhật" : "Tạo mới"}
              </button>
              <button
                type="button"
                onClick={() => setShowForm(false)}
                style={{
                  padding: "8px 16px",
                  background: "#64748b",
                  color: "white",
                  border: "none",
                  borderRadius: "4px",
                  cursor: "pointer",
                }}
              >
                Hủy
              </button>
            </div>
          </form>
        </div>
      )}

      <div style={{ background: "white", padding: "20px", borderRadius: "8px" }}>
        <h3>🧪 Danh sách hóa chất</h3>
        {chemicals.length === 0 ? (
          <p>Không có hóa chất nào.</p>
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
                <th style={{ padding: "10px", textAlign: "left" }}>Mã</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Tên</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Công thức</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Số lượng</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Đơn vị</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Vị trí</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Hành động</th>
              </tr>
            </thead>
            <tbody>
              {chemicals.map((chemical) => (
                <tr key={chemical.itemId}>
                  <td style={{ padding: "10px", borderBottom: "1px solid #eee" }}>
                    {chemical.itemCode}
                  </td>
                  <td>{chemical.name}</td>
                  <td>{chemical.formula || "N/A"}</td>
                  <td>{chemical.currentQuantity || 0}</td>
                  <td>{chemical.unit || "N/A"}</td>
                  <td>{chemical.storageLocation || "N/A"}</td>
                  <td>
                    <button
                      onClick={() => handleEdit(chemical)}
                      style={{
                        padding: "4px 8px",
                        background: "#60a5fa",
                        color: "white",
                        border: "none",
                        borderRadius: "4px",
                        cursor: "pointer",
                        marginRight: "5px",
                      }}
                    >
                      Sửa
                    </button>
                    <button
                      onClick={() => handleDelete(chemical.itemId)}
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

export default AdminChemicals;


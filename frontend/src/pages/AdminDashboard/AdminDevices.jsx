import React, { useEffect, useState } from "react";
import api from "../../services/api";
import "./AdminDashboard.css";

const AdminDevices = () => {
  const [assets, setAssets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchKeyword, setSearchKeyword] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [editingAsset, setEditingAsset] = useState(null);
  const [formData, setFormData] = useState({
    itemCode: "",
    name: "",
    unit: "",
    yearInUse: "",
    statusDetail: "",
    supplier: "",
    storageLocation: "",
    originalPrice: "",
    accountingQuantity: "",
    inventoryQuantity: "",
    residualValue: "",
  });

  useEffect(() => {
    fetchAssets();
  }, []);

  const fetchAssets = async () => {
    try {
      setLoading(true);
      const response = await api.get("/admin/assets");
      setAssets(response.data);
    } catch (error) {
      console.error("Error fetching assets:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    try {
      setLoading(true);
      const response = await api.get(`/admin/assets?keyword=${searchKeyword}`);
      setAssets(response.data);
    } catch (error) {
      console.error("Error searching assets:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setEditingAsset(null);
    setFormData({
      itemCode: "",
      name: "",
      unit: "",
      yearInUse: "",
      statusDetail: "",
      supplier: "",
      storageLocation: "",
      originalPrice: "",
      accountingQuantity: "",
      inventoryQuantity: "",
      residualValue: "",
    });
    setShowForm(true);
  };

  const handleEdit = (asset) => {
    setEditingAsset(asset);
    setFormData({
      itemCode: asset.itemCode,
      name: asset.name,
      unit: asset.unit || "",
      yearInUse: asset.yearInUse || "",
      statusDetail: asset.statusDetail || "",
      supplier: asset.supplier || "",
      storageLocation: asset.storageLocation || "",
      originalPrice: asset.originalPrice || "",
      accountingQuantity: asset.accountingQuantity || "",
      inventoryQuantity: asset.inventoryQuantity || "",
      residualValue: asset.residualValue || "",
    });
    setShowForm(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...formData,
        originalPrice: parseFloat(formData.originalPrice) || 0,
        accountingQuantity: parseInt(formData.accountingQuantity) || 0,
        inventoryQuantity: parseInt(formData.inventoryQuantity) || 0,
        residualValue: parseFloat(formData.residualValue) || 0,
        yearInUse: parseInt(formData.yearInUse) || null,
      };

      if (editingAsset) {
        await api.put(`/admin/assets/${editingAsset.itemId}`, payload);
        alert("Cập nhật thiết bị thành công!");
      } else {
        await api.post("/admin/assets", payload);
        alert("Tạo thiết bị thành công!");
      }
      setShowForm(false);
      fetchAssets();
    } catch (error) {
      console.error("Error saving asset:", error);
      alert("Có lỗi xảy ra!");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Bạn có chắc muốn xóa thiết bị này?")) return;
    try {
      await api.delete(`/admin/assets/${id}`);
      alert("Xóa thiết bị thành công!");
      fetchAssets();
    } catch (error) {
      console.error("Error deleting asset:", error);
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
          placeholder="Tìm kiếm thiết bị..."
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
          + Thêm thiết bị
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
          <h3>{editingAsset ? "Sửa thiết bị" : "Thêm thiết bị mới"}</h3>
          <form onSubmit={handleSubmit}>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px", marginBottom: "10px" }}>
              <input
                type="text"
                placeholder="Mã thiết bị"
                value={formData.itemCode}
                onChange={(e) => setFormData({ ...formData, itemCode: e.target.value })}
                required
                style={{ padding: "8px", borderRadius: "4px", border: "1px solid #ddd" }}
              />
              <input
                type="text"
                placeholder="Tên thiết bị"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                required
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
                placeholder="Năm sử dụng"
                value={formData.yearInUse}
                onChange={(e) => setFormData({ ...formData, yearInUse: e.target.value })}
                style={{ padding: "8px", borderRadius: "4px", border: "1px solid #ddd" }}
              />
              <input
                type="text"
                placeholder="Trạng thái"
                value={formData.statusDetail}
                onChange={(e) => setFormData({ ...formData, statusDetail: e.target.value })}
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
              <input
                type="number"
                placeholder="Số lượng kế toán"
                value={formData.accountingQuantity}
                onChange={(e) => setFormData({ ...formData, accountingQuantity: e.target.value })}
                style={{ padding: "8px", borderRadius: "4px", border: "1px solid #ddd" }}
              />
              <input
                type="number"
                placeholder="Số lượng tồn kho"
                value={formData.inventoryQuantity}
                onChange={(e) => setFormData({ ...formData, inventoryQuantity: e.target.value })}
                style={{ padding: "8px", borderRadius: "4px", border: "1px solid #ddd" }}
              />
              <input
                type="number"
                placeholder="Giá trị còn lại"
                value={formData.residualValue}
                onChange={(e) => setFormData({ ...formData, residualValue: e.target.value })}
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
                {editingAsset ? "Cập nhật" : "Tạo mới"}
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
        <h3>🔬 Danh sách thiết bị</h3>
        {assets.length === 0 ? (
          <p>Không có thiết bị nào.</p>
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
                <th style={{ padding: "10px", textAlign: "left" }}>Trạng thái</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Số lượng</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Vị trí</th>
                <th style={{ padding: "10px", textAlign: "left" }}>Hành động</th>
              </tr>
            </thead>
            <tbody>
              {assets.map((asset) => (
                <tr key={asset.itemId}>
                  <td style={{ padding: "10px", borderBottom: "1px solid #eee" }}>
                    {asset.itemCode}
                  </td>
                  <td>{asset.name}</td>
                  <td>{asset.statusDetail || "N/A"}</td>
                  <td>
                    Kế toán: {asset.accountingQuantity || 0} | Tồn kho: {asset.inventoryQuantity || 0}
                  </td>
                  <td>{asset.storageLocation || "N/A"}</td>
                  <td>
                    <button
                      onClick={() => handleEdit(asset)}
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
                      onClick={() => handleDelete(asset.itemId)}
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

export default AdminDevices;


import React, { useState, useEffect, useRef } from "react";
import { User } from "lucide-react";
import api from "../../../services/api";
import "./TeacherProfile.css";

import ProfileHeader from "./components/ProfileHeader";
import ProfileDetails from "./components/ProfileDetails";
import ProfileActions from "./components/ProfileActions";

const TeacherProfile = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    fullName: "",
    phoneNumber: "",
    email: "",
  });

  const [previewUrl, setPreviewUrl] = useState(null);
  const [base64Image, setBase64Image] = useState(null);

  const [imgError, setImgError] = useState(false);

  const fileInputRef = useRef(null);

  useEffect(() => {
    fetchProfile();
  }, []);

  useEffect(() => {
    setImgError(false);
  }, [profile?.avatar, previewUrl]);

  const fetchProfile = async () => {
    try {
      const token = localStorage.getItem("token");
      if (!token) return;
      const response = await api.get("/users/profile", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setProfile(response.data);

      if (response.data.avatar)
        localStorage.setItem("user_avatar", response.data.avatar);
      if (response.data.fullName)
        localStorage.setItem("user", response.data.fullName);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleFileChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      setPreviewUrl(URL.createObjectURL(file));
      const reader = new FileReader();
      reader.onloadend = () => setBase64Image(reader.result);
      reader.readAsDataURL(file);
    }
  };

  const handleInputChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleEditClick = () => {
    setFormData({
      fullName: profile.fullName || "",
      phoneNumber: profile.phoneNumber || "",
      email: profile.email || "",
    });
    setIsEditing(true);
  };

  const handleCancel = () => {
    setIsEditing(false);
    setPreviewUrl(null);
    setBase64Image(null);
  };

  const handleSave = async () => {
    try {
      const token = localStorage.getItem("token");
      const payload = {
        fullName: formData.fullName,
        phoneNumber: formData.phoneNumber,
        email: formData.email,
        avatar: base64Image || null,
      };

      const response = await api.patch("/users/profile", payload, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setProfile(response.data);

      if (response.data.avatar)
        localStorage.setItem("user_avatar", response.data.avatar);
      if (response.data.fullName)
        localStorage.setItem("user", response.data.fullName);

      window.dispatchEvent(new Event("user_update"));

      setIsEditing(false);
      setPreviewUrl(null);
      setBase64Image(null);
      alert("Cập nhật thành công!");
    } catch (err) {
      console.error("Lỗi:", err);
      alert("Cập nhật thất bại.");
    }
  };

  const getAvatarContent = () => {
    if (previewUrl) return previewUrl;

    if (profile?.avatar && !imgError) {
      return `http://localhost:8080/springmvc/avatars/${
        profile.avatar
      }?t=${Date.now()}`;
    }

    return null;
  };

  if (loading) return <div className="loading-state">Đang tải...</div>;
  if (!profile) return null;

  return (
    <div className="profile-container">
      <div className="profile-card">
        <div className="profile-banner"></div>

        <ProfileHeader
          profile={profile}
          isEditing={isEditing}
          formData={formData}
          avatarSrc={getAvatarContent()}
          onImageError={() => setImgError(true)}
          fileInputRef={fileInputRef}
          onFileChange={handleFileChange}
          onCameraClick={() => fileInputRef.current.click()}
        />

        <ProfileDetails
          profile={profile}
          isEditing={isEditing}
          formData={formData}
          onInputChange={handleInputChange}
        />

        <ProfileActions
          isEditing={isEditing}
          onEdit={handleEditClick}
          onSave={handleSave}
          onCancel={handleCancel}
        />
      </div>
    </div>
  );
};

export default TeacherProfile;

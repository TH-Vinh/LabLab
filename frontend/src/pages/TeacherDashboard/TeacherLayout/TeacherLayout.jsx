import React, { useState, useEffect, useRef } from "react";
import { Outlet, useNavigate, useLocation } from "react-router-dom";
import {
  FlaskConical,
  Search,
  X,
  Bell,
  Check,
  User,
  Settings,
  LogOut,
} from "lucide-react";

import "./TeacherLayout.css";

const TeacherLayout = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const user = localStorage.getItem("user") || "GV";

  const [showNotiMenu, setShowNotiMenu] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);

  const [isSearchOpen, setIsSearchOpen] = useState(false);

  const menuRef = useRef(null);
  const searchInputRef = useRef(null);

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  const toggleNoti = () => {
    setShowNotiMenu(!showNotiMenu);
    setShowUserMenu(false);
    setIsSearchOpen(false);
  };

  const toggleUser = () => {
    setShowUserMenu(!showUserMenu);
    setShowNotiMenu(false);
    setIsSearchOpen(false);
  };

  const toggleSearch = () => {
    if (!isSearchOpen) {
      setIsSearchOpen(true);
      setTimeout(() => searchInputRef.current?.focus(), 100);
      setShowNotiMenu(false);
      setShowUserMenu(false);
    } else {
      setIsSearchOpen(false);
    }
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setShowNotiMenu(false);
        setShowUserMenu(false);
        if (!event.target.closest(".search-wrapper")) {
          setIsSearchOpen(false);
        }
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const isActive = (path) => (location.pathname.includes(path) ? "active" : "");

  return (
    <div className="teacher-container">
      <header className="navbar">
        <div className="nav-brand" onClick={() => navigate("/teacher/home")}>
          <FlaskConical size={28} strokeWidth={2.5} color="#1a73e8" />
          <h1>LabLab</h1>
        </div>

        <nav className="nav-menu">
          <div
            className={`nav-item ${isActive("home")}`}
            onClick={() => navigate("/teacher/home")}
          >
            Trang chủ
          </div>
          <div
            className={`nav-item ${isActive("wiki")}`}
            onClick={() => navigate("/teacher/wiki")}
          >
            Wiki
          </div>
          <div
            className={`nav-item ${isActive("borrow")}`}
            onClick={() => navigate("/teacher/borrow")}
          >
            Mượn thiết bị
          </div>
          <div
            className={`nav-item ${isActive("report")}`}
            onClick={() => navigate("/teacher/report")}
          >
            Báo cáo
          </div>
        </nav>

        <div className="nav-actions" ref={menuRef}>
          <div className={`search-wrapper ${isSearchOpen ? "open" : ""}`}>
            <div className="search-input-slide">
              <input
                ref={searchInputRef}
                type="text"
                placeholder="Tra cứu nhanh hóa chất..."
              />
            </div>

            <button
              className="icon-btn search-btn-trigger"
              onClick={toggleSearch}
            >
              {isSearchOpen ? (
                <X size={22} color="#333" strokeWidth={2} />
              ) : (
                <Search size={22} color="#333" strokeWidth={2} />
              )}
            </button>
          </div>

          <div style={{ position: "relative" }}>
            <button
              className={`icon-btn ${showNotiMenu ? "active" : ""}`}
              onClick={toggleNoti}
            >
              <Bell size={22} color="#333" strokeWidth={2} />
              <span className="badge-dot"></span>
            </button>

            {showNotiMenu && (
              <div className="dropdown-menu">
                <div className="dropdown-header">Thông báo mới</div>
                <div className="dropdown-item">
                  <div className="noti-content">
                    <span
                      className="noti-text"
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "5px",
                      }}
                    >
                      <Check size={14} color="green" /> Phiếu #REQ-088 đã được
                      duyệt.
                    </span>
                    <span className="noti-time">2 phút trước</span>
                  </div>
                </div>
                <div
                  className="dropdown-item"
                  style={{
                    justifyContent: "center",
                    color: "#1a73e8",
                    fontWeight: "bold",
                  }}
                >
                  Xem tất cả
                </div>
              </div>
            )}
          </div>

          <div style={{ position: "relative" }}>
            <div
              className={`user-avatar ${showUserMenu ? "active" : ""}`}
              onClick={toggleUser}
            >
              {user.charAt(0).toUpperCase()}
            </div>

            {showUserMenu && (
              <div className="dropdown-menu">
                <div className="dropdown-header">Tài khoản: {user}</div>
                <div className="dropdown-item">
                  <User size={18} /> Hồ sơ cá nhân
                </div>
                <div className="dropdown-item">
                  <Settings size={18} /> Cài đặt
                </div>
                <div
                  className="dropdown-item"
                  onClick={handleLogout}
                  style={{ color: "#d93025" }}
                >
                  <LogOut size={18} /> Đăng xuất
                </div>
              </div>
            )}
          </div>
        </div>
      </header>

      <main className="container-max">
        <Outlet />
      </main>
    </div>
  );
};

export default TeacherLayout;

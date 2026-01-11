package com.example.springmvc.repository;

import com.example.springmvc.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Integer> {
    
    // Tìm tất cả thiết bị
    @Query("SELECT a FROM Asset a")
    List<Asset> findAllAssets();
    
    // Tìm kiếm thiết bị theo tên
    @Query("SELECT a FROM Asset a WHERE a.name LIKE CONCAT('%', :keyword, '%')")
    List<Asset> searchAssets(@Param("keyword") String keyword);
    
    // Tìm thiết bị theo trạng thái
    @Query("SELECT a FROM Asset a WHERE a.statusDetail = :status")
    List<Asset> findByStatus(@Param("status") String status);
}


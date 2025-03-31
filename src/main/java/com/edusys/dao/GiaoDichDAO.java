/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.edusys.dao;

import com.edusys.enity.GiaoDich;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tinhn
 */
public class GiaoDichDAO {
    private Connection conn;
    
    // Constructor - Giả sử bạn có một class quản lý kết nối database
    public GiaoDichDAO() {
        // Thay đổi thông tin kết nối theo hệ thống của bạn
        String url = "jdbc:sqlserver://localhost:1433;databaseName=EduSys";
        String username = "sa";
        String password = "your_password";
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Không thể kết nối database", e);
        }
    }
    
    // Thêm mới giao dịch
    public void insert(GiaoDich gd) {
        String sql = "INSERT INTO GiaoDich (MaGiaoDich, LoaiGiaoDich, SoTien, NgayGiaoDich, MoTa, MaTaiKhoan) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gd.getMaGiaoDich());
            ps.setString(2, gd.getLoaiGiaoDich());
            ps.setDouble(3, gd.getSoTien());
            ps.setDate(4, new java.sql.Date(gd.getNgayGiaoDich().getTime()));
            ps.setString(5, gd.getMoTa());
            ps.setString(6, gd.getMaTaiKhoan());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm giao dịch", e);
        }
    }
    
    // Cập nhật giao dịch
    public void update(GiaoDich gd) {
        String sql = "UPDATE GiaoDich SET LoaiGiaoDich=?, SoTien=?, NgayGiaoDich=?, MoTa=?, MaTaiKhoan=? " +
                    "WHERE MaGiaoDich=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gd.getLoaiGiaoDich());
            ps.setDouble(2, gd.getSoTien());
            ps.setDate(3, new java.sql.Date(gd.getNgayGiaoDich().getTime()));
            ps.setString(4, gd.getMoTa());
            ps.setString(5, gd.getMaTaiKhoan());
            ps.setString(6, gd.getMaGiaoDich());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật giao dịch", e);
        }
    }
    
    // Xóa giao dịch
    public void delete(String maGiaoDich) {
        String sql = "DELETE FROM GiaoDich WHERE MaGiaoDich=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maGiaoDich);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa giao dịch", e);
        }
    }
    
    // Lấy tất cả giao dịch
    public List<GiaoDich> selectAll() {
        List<GiaoDich> list = new ArrayList<>();
        String sql = "SELECT * FROM GiaoDich";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                GiaoDich gd = new GiaoDich();
                gd.setMaGiaoDich(rs.getString("MaGiaoDich"));
                gd.setLoaiGiaoDich(rs.getString("LoaiGiaoDich"));
                gd.setSoTien(rs.getDouble("SoTien"));
                gd.setNgayGiaoDich(rs.getDate("NgayGiaoDich"));
                gd.setMoTa(rs.getString("MoTa"));
                gd.setMaTaiKhoan(rs.getString("MaTaiKhoan"));
                list.add(gd);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách giao dịch", e);
        }
        return list;
    }
    
    // Tìm giao dịch theo mã
    public GiaoDich selectById(String maGiaoDich) {
        String sql = "SELECT * FROM GiaoDich WHERE MaGiaoDich=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maGiaoDich);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    GiaoDich gd = new GiaoDich();
                    gd.setMaGiaoDich(rs.getString("MaGiaoDich"));
                    gd.setLoaiGiaoDich(rs.getString("LoaiGiaoDich"));
                    gd.setSoTien(rs.getDouble("SoTien"));
                    gd.setNgayGiaoDich(rs.getDate("NgayGiaoDich"));
                    gd.setMoTa(rs.getString("MoTa"));
                    gd.setMaTaiKhoan(rs.getString("MaTaiKhoan"));
                    return gd;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm giao dịch", e);
        }
        return null;
    }
    
    // Đóng kết nối khi cần
    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đóng kết nối", e);
        }
    }
}
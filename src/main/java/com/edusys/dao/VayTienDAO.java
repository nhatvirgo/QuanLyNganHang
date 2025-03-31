/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.edusys.dao;

import com.edusys.enity.VayTien;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tinhn
 */
public class VayTienDAO {
    private Connection conn;
    
    // Constructor
    public VayTienDAO() {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=EduSys";
        String username = "sa";
        String password = "your_password";
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Không thể kết nối database", e);
        }
    }
    
    // Thêm mới khoản vay
    public void insert(VayTien vt) {
        String sql = "INSERT INTO VayTien (MaVay, SoTienVay, LaiSuat, ThoiGianVay, NgayVay, TrangThai, MaKhachHang) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vt.getMaVay());
            ps.setDouble(2, vt.getSoTienVay());
            ps.setDouble(3, vt.getLaiSuat());
            ps.setInt(4, vt.getThoiGianVay());
            ps.setDate(5, new java.sql.Date(vt.getNgayVay().getTime()));
            ps.setString(6, vt.getTrangThai());
            ps.setString(7, vt.getMaKhachHang());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm khoản vay", e);
        }
    }
    
    // Cập nhật khoản vay
    public void update(VayTien vt) {
        String sql = "UPDATE VayTien SET SoTienVay=?, LaiSuat=?, ThoiGianVay=?, NgayVay=?, TrangThai=?, MaKhachHang=? " +
                    "WHERE MaVay=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, vt.getSoTienVay());
            ps.setDouble(2, vt.getLaiSuat());
            ps.setInt(3, vt.getThoiGianVay());
            ps.setDate(4, new java.sql.Date(vt.getNgayVay().getTime()));
            ps.setString(5, vt.getTrangThai());
            ps.setString(6, vt.getMaKhachHang());
            ps.setString(7, vt.getMaVay());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật khoản vay", e);
        }
    }
    
    // Xóa khoản vay
    public void delete(String maVay) {
        String sql = "DELETE FROM VayTien WHERE MaVay=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maVay);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa khoản vay", e);
        }
    }
    
    // Lấy tất cả khoản vay
    public List<VayTien> selectAll() {
        List<VayTien> list = new ArrayList<>();
        String sql = "SELECT * FROM VayTien";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                VayTien vt = new VayTien();
                vt.setMaVay(rs.getString("MaVay"));
                vt.setSoTienVay(rs.getDouble("SoTienVay"));
                vt.setLaiSuat(rs.getDouble("LaiSuat"));
                vt.setThoiGianVay(rs.getInt("ThoiGianVay"));
                vt.setNgayVay(rs.getDate("NgayVay"));
                vt.setTrangThai(rs.getString("TrangThai"));
                vt.setMaKhachHang(rs.getString("MaKhachHang"));
                list.add(vt);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách khoản vay", e);
        }
        return list;
    }
    
    // Tìm khoản vay theo mã
    public VayTien selectById(String maVay) {
        String sql = "SELECT * FROM VayTien WHERE MaVay=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maVay);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    VayTien vt = new VayTien();
                    vt.setMaVay(rs.getString("MaVay"));
                    vt.setSoTienVay(rs.getDouble("SoTienVay"));
                    vt.setLaiSuat(rs.getDouble("LaiSuat"));
                    vt.setThoiGianVay(rs.getInt("ThoiGianVay"));
                    vt.setNgayVay(rs.getDate("NgayVay"));
                    vt.setTrangThai(rs.getString("TrangThai"));
                    vt.setMaKhachHang(rs.getString("MaKhachHang"));
                    return vt;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm khoản vay", e);
        }
        return null;
    }
    
    // Đóng kết nối
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
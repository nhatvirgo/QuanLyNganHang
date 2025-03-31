/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.edusys.dao;

import com.edusys.enity.TraGop;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tinhn
 */
public class TraGopDAO {
    private Connection conn;
    
    // Constructor
    public TraGopDAO() {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=EduSys";
        String username = "sa";
        String password = "your_password";
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Không thể kết nối database", e);
        }
    }
    
    // Thêm mới trả góp
    public void insert(TraGop tg) {
        String sql = "INSERT INTO TraGop (MaTraGop, SoTienTra, NgayTra, TrangThai, MaVay) " +
                    "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tg.getMaTraGop());
            ps.setDouble(2, tg.getSoTienTra());
            ps.setDate(3, new java.sql.Date(tg.getNgayTra().getTime()));
            ps.setString(4, tg.getTrangThai());
            ps.setString(5, tg.getMaVay());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm trả góp", e);
        }
    }
    
    // Cập nhật trả góp
    public void update(TraGop tg) {
        String sql = "UPDATE TraGop SET SoTienTra=?, NgayTra=?, TrangThai=?, MaVay=? " +
                    "WHERE MaTraGop=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, tg.getSoTienTra());
            ps.setDate(2, new java.sql.Date(tg.getNgayTra().getTime()));
            ps.setString(3, tg.getTrangThai());
            ps.setString(4, tg.getMaVay());
            ps.setString(5, tg.getMaTraGop());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật trả góp", e);
        }
    }
    
    // Xóa trả góp
    public void delete(String maTraGop) {
        String sql = "DELETE FROM TraGop WHERE MaTraGop=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTraGop);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa trả góp", e);
        }
    }
    
    // Lấy tất cả trả góp
    public List<TraGop> selectAll() {
        List<TraGop> list = new ArrayList<>();
        String sql = "SELECT * FROM TraGop";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TraGop tg = new TraGop();
                tg.setMaTraGop(rs.getString("MaTraGop"));
                tg.setSoTienTra(rs.getDouble("SoTienTra"));
                tg.setNgayTra(rs.getDate("NgayTra"));
                tg.setTrangThai(rs.getString("TrangThai"));
                tg.setMaVay(rs.getString("MaVay"));
                list.add(tg);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách trả góp", e);
        }
        return list;
    }
    
    // Tìm trả góp theo mã
    public TraGop selectById(String maTraGop) {
        String sql = "SELECT * FROM TraGop WHERE MaTraGop=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTraGop);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TraGop tg = new TraGop();
                    tg.setMaTraGop(rs.getString("MaTraGop"));
                    tg.setSoTienTra(rs.getDouble("SoTienTra"));
                    tg.setNgayTra(rs.getDate("NgayTra"));
                    tg.setTrangThai(rs.getString("TrangThai"));
                    tg.setMaVay(rs.getString("MaVay"));
                    return tg;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm trả góp", e);
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
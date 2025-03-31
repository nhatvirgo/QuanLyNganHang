/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.edusys.dao;

import com.edusys.enity.KhachHang;
import com.edusys.utils.XJdbc;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tinhn
 */

public class KhachHangDAO extends EduSysDAO<KhachHang, String> {
    
    public void insert(KhachHang model) {
        String sql = "INSERT INTO KHACH_HANG (MaKhachHang, HoTen, NgaySinh, GioiTinh, SoCCCD, DiaChi, SoDienThoai, Email, MatKhau, Hinh, MaNhanVien, NgayTao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        XJdbc.update(sql,
                model.getMaKhachHang(),
                model.getHoTen(),
                model.getNgaySinh(),
                model.isGioiTinh(),
                model.getSoCCCD(),
                model.getDiaChi(),
                model.getSoDienThoai(),
                model.getEmail(),
                model.getMatKhau(),
                model.getHinh(),
                model.getMaNhanVien(),
                model.getNgayTao());
    }

    public void update(KhachHang model) {
        String sql = "UPDATE KHACH_HANG SET HoTen=?, NgaySinh=?, GioiTinh=?, SoCCCD=?, DiaChi=?, SoDienThoai=?, Email=?, MatKhau=?, Hinh=?, MaNhanVien=?, NgayTao=? WHERE MaKhachHang=?";
        XJdbc.update(sql,
                model.getHoTen(),
                model.getNgaySinh(),
                model.isGioiTinh(),
                model.getSoCCCD(),
                model.getDiaChi(),
                model.getSoDienThoai(),
                model.getEmail(),
                model.getMatKhau(),
                model.getHinh(),
                model.getMaNhanVien(),
                model.getNgayTao(),
                model.getMaKhachHang());
    }

    public void delete(String maKhachHang) {
        String sql = "DELETE FROM KHACH_HANG WHERE MaKhachHang=?";
        XJdbc.update(sql, maKhachHang);
    }

    public List<KhachHang> selectAll() {
        String sql = "SELECT * FROM KHACH_HANG";
        return this.selectBySql(sql);
    }

    public KhachHang selectById(String maKhachHang) {
        String sql = "SELECT * FROM KHACH_HANG WHERE MaKhachHang=?";
        List<KhachHang> list = this.selectBySql(sql, maKhachHang);
        return list.isEmpty() ? null : list.get(0);
    }

    protected List<KhachHang> selectBySql(String sql, Object... args) {
        List<KhachHang> list = new ArrayList<>();
        try (ResultSet rs = XJdbc.query(sql, args)) {
            while (rs.next()) {
                KhachHang entity = new KhachHang();
                entity.setMaKhachHang(rs.getString("MaKhachHang"));
                entity.setHoTen(rs.getString("HoTen"));
                entity.setNgaySinh(rs.getDate("NgaySinh"));
                entity.setGioiTinh(rs.getBoolean("GioiTinh"));
                entity.setSoCCCD(rs.getString("SoCCCD"));
                entity.setDiaChi(rs.getString("DiaChi"));
                entity.setSoDienThoai(rs.getString("SoDienThoai"));
                entity.setEmail(rs.getString("Email"));
                entity.setMatKhau(rs.getString("MatKhau"));
                entity.setHinh(rs.getString("Hinh"));
                entity.setMaNhanVien(rs.getString("MaNhanVien"));
                entity.setNgayTao(rs.getDate("NgayTao"));
                list.add(entity);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu khách hàng", ex);
        }
        return list;
    }
}


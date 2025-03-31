/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.edusys.dao;

import com.edusys.enity.TaiKhoan;
import com.edusys.utils.XJdbc;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tinhn
 */
public class TaiKhoanDAO extends EduSysDAO<TaiKhoan, String> {
    
    public void insert(TaiKhoan model) {
        String sql = "INSERT INTO TAI_KHOAN (MaTaiKhoan, SoTaiKhoan, LoaiTaiKhoan, SoDu, NgayMo, TrangThai, MaKhachHang) VALUES (?, ?, ?, ?, ?, ?, ?)";
        XJdbc.update(sql,
                model.getMaTaiKhoan(),
                model.getSoTaiKhoan(),
                model.getLoaiTaiKhoan(),
                model.getSoDu(),
                model.getNgayMo(),
                model.getTrangThai(),
                model.getMaKhachHang());
    }

    public void update(TaiKhoan model) {
        String sql = "UPDATE TAI_KHOAN SET SoTaiKhoan=?, LoaiTaiKhoan=?, SoDu=?, NgayMo=?, TrangThai=?, MaKhachHang=? WHERE MaTaiKhoan=?";
        XJdbc.update(sql,
                model.getSoTaiKhoan(),
                model.getLoaiTaiKhoan(),
                model.getSoDu(),
                model.getNgayMo(),
                model.getTrangThai(),
                model.getMaKhachHang(),
                model.getMaTaiKhoan());
    }

    public void delete(String maTaiKhoan) {
        String sql = "DELETE FROM TAI_KHOAN WHERE MaTaiKhoan=?";
        XJdbc.update(sql, maTaiKhoan);
    }

    public List<TaiKhoan> selectAll() {
        String sql = "SELECT * FROM TAI_KHOAN";
        return this.selectBySql(sql);
    }

    public TaiKhoan selectById(String maTaiKhoan) {
        String sql = "SELECT * FROM TAI_KHOAN WHERE MaTaiKhoan=?";
        List<TaiKhoan> list = this.selectBySql(sql, maTaiKhoan);
        return list.isEmpty() ? null : list.get(0);
    }

    protected List<TaiKhoan> selectBySql(String sql, Object... args) {
        List<TaiKhoan> list = new ArrayList<>();
        try (ResultSet rs = XJdbc.query(sql, args)) {
            while (rs.next()) {
                TaiKhoan entity = new TaiKhoan();
                entity.setMaTaiKhoan(rs.getString("MaTaiKhoan"));
                entity.setSoTaiKhoan(rs.getString("SoTaiKhoan"));
                entity.setLoaiTaiKhoan(rs.getString("LoaiTaiKhoan"));
                entity.setSoDu(rs.getDouble("SoDu"));
                entity.setNgayMo(rs.getDate("NgayMo"));
                entity.setTrangThai(rs.getString("TrangThai"));
                entity.setMaKhachHang(rs.getString("MaKhachHang"));
                list.add(entity);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu tài khoản", ex);
        }
        return list;
    }
}

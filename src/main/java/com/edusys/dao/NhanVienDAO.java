package com.edusys.dao;

import com.edusys.enity.NhanVien;
import com.edusys.utils.XJdbc;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO extends EduSysDAO<NhanVien, String>{
     public void insert(NhanVien model) {
        String sql = "INSERT INTO NHAN_VIEN (MaNhanVien, HoTen, NgaySinh, GioiTinh, SoDienThoai, Email, MatKhau) VALUES (?, ?, ?, ?, ?, ?, ?)";
        XJdbc.update(sql, 
                model.getMaNhanVien(), 
                model.getHoTen(), 
                model.getNgaySinh(), 
                model.isGioiTinh(), 
                model.getSoDienThoai(), 
                model.getEmail(), 
                model.getMatKhau());
    }

    public void update(NhanVien model) {
        String sql = "UPDATE NHAN_VIEN SET HoTen=?, NgaySinh=?, GioiTinh=?, SoDienThoai=?, Email=?, MatKhau=? WHERE MaNhanVien=?";
        XJdbc.update(sql, 
                model.getHoTen(), 
                model.getNgaySinh(), 
                model.isGioiTinh(), 
                model.getSoDienThoai(), 
                model.getEmail(), 
                model.getMatKhau(),
                model.getMaNhanVien());
    }

    public void delete(String maNhanVien) {
        String sql = "DELETE FROM NHAN_VIEN WHERE MaNhanVien=?";
        XJdbc.update(sql, maNhanVien);
    }

    public List<NhanVien> selectAll() {
        String sql = "SELECT * FROM NHAN_VIEN";
        return this.selectBySql(sql);
    }

    public NhanVien selectById(String maNhanVien) {
        String sql = "SELECT * FROM NHAN_VIEN WHERE MaNhanVien=?";
        List<NhanVien> list = this.selectBySql(sql, maNhanVien);
        return list.isEmpty() ? null : list.get(0);
    }

    protected List<NhanVien> selectBySql(String sql, Object... args) {
        List<NhanVien> list = new ArrayList<>();
        try (ResultSet rs = XJdbc.query(sql, args)) {
            while (rs.next()) {
                NhanVien entity = new NhanVien();
                entity.setMaNhanVien(rs.getString("MaNhanVien"));
                entity.setHoTen(rs.getString("HoTen"));
                entity.setNgaySinh(rs.getDate("NgaySinh"));
                entity.setGioiTinh(rs.getBoolean("GioiTinh"));
                entity.setSoDienThoai(rs.getString("SoDienThoai"));
                entity.setEmail(rs.getString("Email"));
                entity.setMatKhau(rs.getString("MatKhau"));
                list.add(entity);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu nhân viên", ex);
        }
        return list;
    }
}

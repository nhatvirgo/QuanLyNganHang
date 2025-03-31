/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.edusys.dao;

import com.edusys.utils.XJdbc;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tinhn
 */
public class ThongKeDAO {
    private List<Object[]> getListOfArray(String sql, String[] cols, Object... args) {
        try {
            List<Object[]> list = new ArrayList<>();
            ResultSet rs = XJdbc.query(sql, args);
            while (rs.next()) {
                Object[] vals = new Object[cols.length];
                for (int i = 0; i < cols.length; i++) {
                    vals[i] = rs.getObject(cols[i]);
                }
                list.add(vals);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Object[]> getThongKeKhachHang(int thang, int nam) {
        String sql = "{CALL ThongKeKhachHang(?, ?)}";
        String[] cols = {"MaKhachHang", "HoTen", "NgaySinh", "GioiTinh", "SoCCCD", "DiaChi", "SoDienThoai", "Email", "MatKhau", "MaNhanVien", "NgayTao"};
        return this.getListOfArray(sql, cols, thang, nam);
    }

    public List<Object[]> getThongKeGiaoDich(int thang, int nam) {
        String sql = "{CALL ThongKeGiaoDich(?, ?)}";
        String[] cols = {"MaGiaoDich", "LoaiGiaoDich", "SoTien", "NgayGiaoDich", "TrangThai", "TaiKhoanNguoiGui", "TenNguoiGui", "TaiKhoanNguoiNhan", "TenNguoiNhan"};
        return this.getListOfArray(sql, cols, thang, nam);
    }

    public List<Object[]> getThongKeTaiKhoan(int thang, int nam) {
        String sql = "{CALL ThongKeTaiKhoan(?, ?)}";
        String[] cols = {"MaTaiKhoan", "SoTaiKhoan", "LoaiTaiKhoan", "SoDu", "NgayMo", "TrangThai", "MaKhachHang"};
        return this.getListOfArray(sql, cols, thang, nam);
    }

    public List<Object[]> getThongKeVayTien(int thang, int nam) {
        String sql = "{CALL ThongKeVayTien(?, ?)}";
        String[] cols = {"MaVay", "MaKhachHang", "HoTen", "SoTienVay", "LaiSuat", "ThoiGianVay", "NgayVay", "TrangThai"};
        return this.getListOfArray(sql, cols, thang, nam);
    }
    
    
    public List<Integer> selectYearsKhachHang() {
        String sql="SELECT DISTINCT year(NgayTao) Year FROM KHACH_HANG ORDER BY Year DESC";
        List<Integer> list=new ArrayList<>();
        try {
           ResultSet rs = XJdbc.query(sql);
           while(rs.next()){
                 list.add(rs.getInt(1));
            }
            rs.getStatement().getConnection().close();
            return list;
        } 
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public List<Integer> selectYearsGiaoDich() {
        String sql="SELECT DISTINCT year(NgayGiaoDich) Year FROM GIAO_DICH ORDER BY Year DESC";
        List<Integer> list=new ArrayList<>();
        try {
           ResultSet rs = XJdbc.query(sql);
           while(rs.next()){
                 list.add(rs.getInt(1));
            }
            rs.getStatement().getConnection().close();
            return list;
        } 
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public List<Integer> selectYearsTaiKhoan() {
        String sql="SELECT DISTINCT year(NgayMo) Year FROM TAI_KHOAN ORDER BY Year DESC";
        List<Integer> list=new ArrayList<>();
        try {
           ResultSet rs = XJdbc.query(sql);
           while(rs.next()){
                 list.add(rs.getInt(1));
            }
            rs.getStatement().getConnection().close();
            return list;
        } 
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public List<Integer> selectYearsVayTien() {
        String sql="SELECT DISTINCT year(NgayVay) Year FROM VAY_TIEN ORDER BY Year DESC";
        List<Integer> list=new ArrayList<>();
        try {
           ResultSet rs = XJdbc.query(sql);
           while(rs.next()){
                 list.add(rs.getInt(1));
            }
            rs.getStatement().getConnection().close();
            return list;
        } 
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}

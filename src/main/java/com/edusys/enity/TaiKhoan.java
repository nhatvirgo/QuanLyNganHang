/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.edusys.enity;

import java.util.Date;

/**
 *
 * @author tinhn
 */
public class TaiKhoan {
    private String maTaiKhoan;
    private String soTaiKhoan;
    private String loaiTaiKhoan;
    private double soDu;
    private Date ngayMo;
    private String trangThai;
    private String maKhachHang;

    public TaiKhoan() {
    }

    public TaiKhoan(String maTaiKhoan, String soTaiKhoan, String loaiTaiKhoan, double soDu, Date ngayMo, String trangThai, String maKhachHang) {
        this.maTaiKhoan = maTaiKhoan;
        this.soTaiKhoan = soTaiKhoan;
        this.loaiTaiKhoan = loaiTaiKhoan;
        this.soDu = soDu;
        this.ngayMo = ngayMo;
        this.trangThai = trangThai;
        this.maKhachHang = maKhachHang;
    }

    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(String maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getSoTaiKhoan() {
        return soTaiKhoan;
    }

    public void setSoTaiKhoan(String soTaiKhoan) {
        this.soTaiKhoan = soTaiKhoan;
    }

    public String getLoaiTaiKhoan() {
        return loaiTaiKhoan;
    }

    public void setLoaiTaiKhoan(String loaiTaiKhoan) {
        this.loaiTaiKhoan = loaiTaiKhoan;
    }

    public double getSoDu() {
        return soDu;
    }

    public void setSoDu(double soDu) {
        this.soDu = soDu;
    }

    public Date getNgayMo() {
        return ngayMo;
    }

    public void setNgayMo(Date ngayMo) {
        this.ngayMo = ngayMo;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }
    
    
}

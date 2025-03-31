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
public class KhachHang {
    private String maKhachHang;
    private String hoTen;
    private Date ngaySinh;
    private boolean gioiTinh;
    private String soCCCD;
    private String diaChi;
    private String soDienThoai;
    private String email;
    private String matKhau;
    private String hinh;
    private String maNhanVien;
    private Date ngayTao;

    public KhachHang() {
    }

    public KhachHang(String maKhachHang, String hoTen, Date ngaySinh, boolean gioiTinh, String soCCCD, String diaChi, String soDienThoai, String email, String matKhau, String hinh, String maNhanVien, Date ngayTao) {
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.soCCCD = soCCCD;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.matKhau = matKhau;
        this.hinh = hinh;
        this.maNhanVien = maNhanVien;
        this.ngayTao = ngayTao;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public boolean isGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getSoCCCD() {
        return soCCCD;
    }

    public void setSoCCCD(String soCCCD) {
        this.soCCCD = soCCCD;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getHinh() {
        return hinh;
    }

    public void setHinh(String hinh) {
        this.hinh = hinh;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    @Override
    public String toString() {
        return "KhachHang{" + "maKhachHang=" + maKhachHang + ", hoTen=" + hoTen + ", ngaySinh=" + ngaySinh + ", gioiTinh=" + gioiTinh + ", soCCCD=" + soCCCD + ", diaChi=" + diaChi + ", soDienThoai=" + soDienThoai + ", email=" + email + ", matKhau=" + matKhau + ", hinh=" + hinh + ", maNhanVien=" + maNhanVien + ", ngayTao=" + ngayTao + '}';
    }
    
    
}

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
public class VayTien {
    private String maVay;
    private double soTienVay;
    private double laiSuat;
    private int thoiGianVay;
    private Date ngayVay;
    private String trangThai;
    private String maKhachHang;

    public VayTien() {
    }

    public VayTien(String maVay, double soTienVay, double laiSuat, int thoiGianVay, Date ngayVay, String trangThai, String maKhachHang) {
        this.maVay = maVay;
        this.soTienVay = soTienVay;
        this.laiSuat = laiSuat;
        this.thoiGianVay = thoiGianVay;
        this.ngayVay = ngayVay;
        this.trangThai = trangThai;
        this.maKhachHang = maKhachHang;
    }

    public String getMaVay() {
        return maVay;
    }

    public void setMaVay(String maVay) {
        this.maVay = maVay;
    }

    public double getSoTienVay() {
        return soTienVay;
    }

    public void setSoTienVay(double soTienVay) {
        this.soTienVay = soTienVay;
    }

    public double getLaiSuat() {
        return laiSuat;
    }

    public void setLaiSuat(double laiSuat) {
        this.laiSuat = laiSuat;
    }

    public int getThoiGianVay() {
        return thoiGianVay;
    }

    public void setThoiGianVay(int thoiGianVay) {
        this.thoiGianVay = thoiGianVay;
    }

    public Date getNgayVay() {
        return ngayVay;
    }

    public void setNgayVay(Date ngayVay) {
        this.ngayVay = ngayVay;
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

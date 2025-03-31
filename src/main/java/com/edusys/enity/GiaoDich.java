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
public class GiaoDich {
    private String maGiaoDich;
    private String loaiGiaoDich;
    private double soTien;
    private Date ngayGiaoDich;
    private String moTa;
    private String maTaiKhoan;

    public GiaoDich() {
    }

    public GiaoDich(String maGiaoDich, String loaiGiaoDich, double soTien, Date ngayGiaoDich, String moTa, String maTaiKhoan) {
        this.maGiaoDich = maGiaoDich;
        this.loaiGiaoDich = loaiGiaoDich;
        this.soTien = soTien;
        this.ngayGiaoDich = ngayGiaoDich;
        this.moTa = moTa;
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getMaGiaoDich() {
        return maGiaoDich;
    }

    public void setMaGiaoDich(String maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }

    public String getLoaiGiaoDich() {
        return loaiGiaoDich;
    }

    public void setLoaiGiaoDich(String loaiGiaoDich) {
        this.loaiGiaoDich = loaiGiaoDich;
    }

    public double getSoTien() {
        return soTien;
    }

    public void setSoTien(double soTien) {
        this.soTien = soTien;
    }

    public Date getNgayGiaoDich() {
        return ngayGiaoDich;
    }

    public void setNgayGiaoDich(Date ngayGiaoDich) {
        this.ngayGiaoDich = ngayGiaoDich;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(String maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }
    
    
    
}

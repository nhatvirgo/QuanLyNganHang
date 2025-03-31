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
public class TraGop {
    private String maTraGop;
    private double soTienTra;
    private Date ngayTra;
    private String trangThai;
    private String maVay;

    public TraGop() {
    }

    public TraGop(String maTraGop, double soTienTra, Date ngayTra, String trangThai, String maVay) {
        this.maTraGop = maTraGop;
        this.soTienTra = soTienTra;
        this.ngayTra = ngayTra;
        this.trangThai = trangThai;
        this.maVay = maVay;
    }

    public String getMaTraGop() {
        return maTraGop;
    }

    public void setMaTraGop(String maTraGop) {
        this.maTraGop = maTraGop;
    }

    public double getSoTienTra() {
        return soTienTra;
    }

    public void setSoTienTra(double soTienTra) {
        this.soTienTra = soTienTra;
    }

    public Date getNgayTra() {
        return ngayTra;
    }

    public void setNgayTra(Date ngayTra) {
        this.ngayTra = ngayTra;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMaVay() {
        return maVay;
    }

    public void setMaVay(String maVay) {
        this.maVay = maVay;
    }
    
    
}

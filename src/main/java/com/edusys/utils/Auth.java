package com.edusys.utils;

import com.edusys.enity.KhachHang;
import com.edusys.enity.NhanVien;

public class Auth {
    /**
     * Đối tượng này chứa thông tin người sử dụng sau khi đăng nhập
     */
    public static NhanVien userNhanVien = null;
    
     /**
     * Đối tượng này chứa thông tin khách hàng sau khi đăng nhập
     */
    public static KhachHang userKhachHang = null;
    
    /**
     * Xóa thông tin của người sử dụng khi có yêu cầu đăng xuất
     */
    public static void clear() {
        Auth.userNhanVien = null;
        Auth.userKhachHang = null;
    }
    
    /**
     * Kiểm tra xem đăng nhập hay chưa
     */
    public static boolean isLogin() {
        return Auth.userNhanVien != null || Auth.userKhachHang != null;
    }
     /**
     * Kiểm tra xem có phải là nhân viên hay không
     */
    public static boolean isEmployee() {
        return Auth.userNhanVien != null;
    }
    
    /**
     * Kiểm tra xem có phải là khách hàng hay không
     */
    public static boolean isCustomer() {
        return Auth.userKhachHang != null;
    }
}

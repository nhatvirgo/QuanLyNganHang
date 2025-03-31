/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.edusys.utils;

import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.ImageIcon;

/**
 *
 * @author Trong Tinh
 */
public class XImage {
    /**
     * Ảnh biểu tượng của ứng dụng, xuất hiện trên mọi cửa sổ
     */ 
    public static Image getAppIcon(){
        URL url = XImage.class.getResource("/icon/A3.png");
        return new ImageIcon(url).getImage();
    }
    

    /**
     * Sao chép file logo chuyên đề vào thư mục logo
     * @param src là đối tượng file ảnh
     */   
    public static void save(File src){
        File dst = new File("logos", src.getName());
        if(!dst.getParentFile().exists()){
            dst.getParentFile().mkdirs();
        }
        try {
            Path from = Paths.get(src.getAbsolutePath());
            Path to = Paths.get(dst.getAbsolutePath());
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
        } 
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * Đọc hình ảnh logo chuyên đề
     * @param fileName  là tên file logo
     * @return ảnh đọc được
     */   
    public static ImageIcon read(String fileName){
        File path = new File("logos", fileName);
        return new ImageIcon(path.getAbsolutePath());
    }
    
    /*public static ImageIcon read(String fileName) {
        File path = new File("src/main/resources/logos", fileName);
        if (!path.exists()) {
            return null; // Trả về null nếu file không tồn tại
        }
        return new ImageIcon(path.getAbsolutePath());
    }*/

}

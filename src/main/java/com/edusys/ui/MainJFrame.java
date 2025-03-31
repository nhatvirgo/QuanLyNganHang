/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edusys.ui;

import com.edusys.utils.Auth;
import com.edusys.utils.MsgBox;
import com.edusys.utils.XImage;
import com.edusys.utils.XJdbc;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Timer;

public class MainJFrame extends javax.swing.JFrame {
    
    public MainJFrame() {
        initComponents();
        init();
    }

    private void init() {
        this.setLocationRelativeTo(null);
        this.setIconImage(XImage.getAppIcon());

        new ChaoJDialog(this, true).setVisible(true);
        new DangNhapJDialog(this, true).setVisible(true);

        // Cập nhật số dư sau khi đăng nhập
        updateSoDu();

        new Timer(1000, e -> {
            Date now = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
            String text = formatter.format(now);
            lblDongHo.setText(text);
        }).start();
    }

    private void updateSoDu() {
        if (Auth.isLogin()) {
            if (Auth.isCustomer()) {
                try {
                    String sql = "SELECT SUM(SoDu) AS TongSoDu FROM TAI_KHOAN WHERE MaKhachHang = ?";
                    ResultSet rs = XJdbc.query(sql, Auth.userKhachHang.getMaKhachHang());
                    if (rs.next()) {
                        double soDu = rs.getDouble("TongSoDu");
                        lblSoDu.setText("Số dư: " + String.format("%,.2f", soDu) + " VND");
                    } else {
                        lblSoDu.setText("Số dư: 0 VND");
                    }
                    rs.close();
                } catch (SQLException e) {
                    MsgBox.alert(this, "Lỗi khi tải số dư: " + e.getMessage());
                    lblSoDu.setText("Số dư: Lỗi");
                }
            } else if (Auth.isEmployee()) {
                lblSoDu.setText("Số dư: N/A (Nhân viên)");
            }
        } else {
            lblSoDu.setText("Số dư: Chưa đăng nhập");
        }
    }

    void openDoiMatKhau() {
        if (Auth.isLogin()) {
            new DoiMatKhauJDialog(this, true).setVisible(true);
        } else {
            MsgBox.alert(this, "Vui Lòng đăng nhập!");
        }
    }

    void dangXuat() {
        Auth.clear();
        new DangNhapJDialog(this, true).setVisible(true);
        updateSoDu(); // Cập nhật số dư sau khi đăng nhập lại
    }

    void ketThuc() {
        if (MsgBox.confirm(this, "Bạn muốn kết thúc làm việc?")) {
            System.exit(0);
        }
    }

    void openNhanVien() {
        if (Auth.isLogin()) {
            new NhanVienJDialog(this, true).setVisible(true);
        } else {
            MsgBox.alert(this, "Vui Lòng đăng nhập!");
        }
    }

    void openKhachHang() {
        if (Auth.isLogin()) {
            new KhachHangJDialog(this, true).setVisible(true);
        } else {
            MsgBox.alert(this, "Vui Lòng đăng nhập!");
        }
    }

    void openGiaoDich() {
        if (Auth.isLogin()) {
            new GiaoDichJDialog(this, true).setVisible(true);
            updateSoDu(); // Cập nhật số dư sau khi giao dịch
        } else {
            MsgBox.alert(this, "Vui Lòng đăng nhập!");
        }
    }

    void openVayTien(int index) {
    if (Auth.isLogin()) {
        VayTienJDialog tkwin = new VayTienJDialog(this, true);
        tkwin.selectTab(index);
        tkwin.setVisible(true);
        updateSoDu(); // Cập nhật số dư sau khi vay tiền hoặc trả góp
    } else {
        MsgBox.alert(this, "Vui lòng đăng nhập");
    }
}

    void openTaiKhoan() {
        if (Auth.isLogin()) {
            new TaiKhoanJDialog(this, true).setVisible(true);
            updateSoDu(); // Cập nhật số dư sau khi quản lý tài khoản
        } else {
            MsgBox.alert(this, "Vui Lòng đăng nhập!");
        }
    }

    void openThongKe(int index) {
        if (Auth.isLogin()) {
            if (index == 0 && !Auth.isEmployee()) {
                MsgBox.alert(this, "Bạn không có quyền xem thông tin thống kê");
            } else {
                ThongKeJDialog tkwin = new ThongKeJDialog(this, true);
                tkwin.selectTab(index);
                tkwin.setVisible(true);
            }
        } else {
            MsgBox.alert(this, "Vui lòng đăng nhập");
        }
    }

    void openGioiThieu() {
        // TODO: Add logic if needed
    }

    void openHuongDan() {
        try {
            Desktop.getDesktop().browse(new File("src/main/resources/help/index.html").toURI());
        } catch (IOException e) {
            MsgBox.alert(this, "Không tìm thấy file đường dẫn");
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBar = new javax.swing.JToolBar();
        btnDangXuat = new javax.swing.JButton();
        btnKetThuc = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        btnGiaoDich = new javax.swing.JButton();
        btnVayTien = new javax.swing.JButton();
        btnTraGop = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        btnHuongDan = new javax.swing.JButton();
        pnlTrangThai = new javax.swing.JPanel();
        lblTrangThai = new javax.swing.JLabel();
        lblDongHo = new javax.swing.JLabel();
        lblSoDu = new javax.swing.JLabel();
        lblLogo = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        mnuHeThong = new javax.swing.JMenu();
        mniDangNhap = new javax.swing.JMenuItem();
        mniDangXuat = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mniDoiMatKhau = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mniKetThuc = new javax.swing.JMenuItem();
        mnuQuanLy = new javax.swing.JMenu();
        mniKhachHang = new javax.swing.JMenuItem();
        mniTaiKhoan = new javax.swing.JMenuItem();
        mniGiaoDich = new javax.swing.JMenuItem();
        mniVayTien = new javax.swing.JMenuItem();
        mniTraGop = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        mniNhanVien = new javax.swing.JMenuItem();
        mnuThongKe = new javax.swing.JMenu();
        mniThongKeKhachHang = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mniThongKeGiaoDich = new javax.swing.JMenuItem();
        mniThongKeTaiKhoan = new javax.swing.JMenuItem();
        mniThongKeVayTien = new javax.swing.JMenuItem();
        mnuTroGiup = new javax.swing.JMenu();
        mniHuongDan = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        mniGioiThieu = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Hệ Thống Quản Lý Ngân Hàng");

        toolBar.setBackground(new java.awt.Color(0, 0, 204));
        toolBar.setRollover(true);

        btnDangXuat.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnDangXuat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Exit.png"))); // NOI18N
        btnDangXuat.setText("Đăng xuất");
        btnDangXuat.setFocusable(false);
        btnDangXuat.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDangXuat.setMargin(new java.awt.Insets(2, 10, 2, 10));
        btnDangXuat.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDangXuat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDangXuatActionPerformed(evt);
            }
        });
        toolBar.add(btnDangXuat);

        btnKetThuc.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnKetThuc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Stop.png"))); // NOI18N
        btnKetThuc.setText("Kết thúc");
        btnKetThuc.setFocusable(false);
        btnKetThuc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnKetThuc.setMargin(new java.awt.Insets(2, 10, 2, 10));
        btnKetThuc.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnKetThuc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKetThucActionPerformed(evt);
            }
        });
        toolBar.add(btnKetThuc);
        toolBar.add(jSeparator6);

        btnGiaoDich.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGiaoDich.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/people2.jpg"))); // NOI18N
        btnGiaoDich.setText("Giao dịch");
        btnGiaoDich.setFocusable(false);
        btnGiaoDich.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGiaoDich.setMargin(new java.awt.Insets(2, 10, 2, 10));
        btnGiaoDich.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGiaoDich.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGiaoDichActionPerformed(evt);
            }
        });
        toolBar.add(btnGiaoDich);

        btnVayTien.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnVayTien.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/vaytien41.png"))); // NOI18N
        btnVayTien.setText("Vay tiền");
        btnVayTien.setFocusable(false);
        btnVayTien.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVayTien.setMargin(new java.awt.Insets(2, 10, 2, 10));
        btnVayTien.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnVayTien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVayTienActionPerformed(evt);
            }
        });
        toolBar.add(btnVayTien);

        btnTraGop.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTraGop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/tragop01.png"))); // NOI18N
        btnTraGop.setText("Trả góp");
        btnTraGop.setFocusable(false);
        btnTraGop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTraGop.setMargin(new java.awt.Insets(2, 10, 2, 10));
        btnTraGop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTraGop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTraGopActionPerformed(evt);
            }
        });
        toolBar.add(btnTraGop);
        toolBar.add(jSeparator7);

        btnHuongDan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHuongDan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/trogiup.png"))); // NOI18N
        btnHuongDan.setText("Hướng dẫn");
        btnHuongDan.setFocusable(false);
        btnHuongDan.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnHuongDan.setMargin(new java.awt.Insets(2, 10, 2, 10));
        btnHuongDan.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnHuongDan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHuongDanActionPerformed(evt);
            }
        });
        toolBar.add(btnHuongDan);

        pnlTrangThai.setBackground(new java.awt.Color(255, 255, 255));

        lblTrangThai.setFont(new java.awt.Font("Segoe UI", 3, 13)); // NOI18N
        lblTrangThai.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Info.png"))); // NOI18N
        lblTrangThai.setText("Hệ thống quản lý ngân hàng");

        lblDongHo.setFont(new java.awt.Font("Sitka Text", 3, 13)); // NOI18N
        lblDongHo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Alarm.png"))); // NOI18N
        lblDongHo.setText("10:11 AM");

        lblSoDu.setFont(new java.awt.Font("Serif", 3, 18)); // NOI18N
        lblSoDu.setText("Số dư:");

        javax.swing.GroupLayout pnlTrangThaiLayout = new javax.swing.GroupLayout(pnlTrangThai);
        pnlTrangThai.setLayout(pnlTrangThaiLayout);
        pnlTrangThaiLayout.setHorizontalGroup(
            pnlTrangThaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTrangThaiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTrangThai)
                .addGap(193, 193, 193)
                .addComponent(lblSoDu, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblDongHo)
                .addContainerGap())
        );
        pnlTrangThaiLayout.setVerticalGroup(
            pnlTrangThaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTrangThaiLayout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addGroup(pnlTrangThaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDongHo, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTrangThaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblTrangThai)
                        .addComponent(lblSoDu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        lblLogo.setBackground(new java.awt.Color(255, 255, 255));
        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Logo_MB_new.png"))); // NOI18N
        lblLogo.setOpaque(true);
        lblLogo.setRequestFocusEnabled(false);

        menuBar.setBackground(new java.awt.Color(255, 51, 51));

        mnuHeThong.setText("Hệ thống");

        mniDangNhap.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mniDangNhap.setText("Đăng nhập");
        mniDangNhap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniDangNhapActionPerformed(evt);
            }
        });
        mnuHeThong.add(mniDangNhap);

        mniDangXuat.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mniDangXuat.setText("Đăng xuất");
        mniDangXuat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniDangXuatActionPerformed(evt);
            }
        });
        mnuHeThong.add(mniDangXuat);
        mnuHeThong.add(jSeparator2);

        mniDoiMatKhau.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mniDoiMatKhau.setText("Đổi mật khẩu");
        mniDoiMatKhau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniDoiMatKhauActionPerformed(evt);
            }
        });
        mnuHeThong.add(mniDoiMatKhau);
        mnuHeThong.add(jSeparator1);

        mniKetThuc.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F10, 0));
        mniKetThuc.setText("Kết thúc");
        mniKetThuc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniKetThucActionPerformed(evt);
            }
        });
        mnuHeThong.add(mniKetThuc);

        menuBar.add(mnuHeThong);

        mnuQuanLy.setText("Quản lý");

        mniKhachHang.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mniKhachHang.setText("Khách hàng");
        mniKhachHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniKhachHangActionPerformed(evt);
            }
        });
        mnuQuanLy.add(mniKhachHang);

        mniTaiKhoan.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mniTaiKhoan.setText("Tài khoản");
        mniTaiKhoan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniTaiKhoanActionPerformed(evt);
            }
        });
        mnuQuanLy.add(mniTaiKhoan);

        mniGiaoDich.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mniGiaoDich.setText("Giao dịch");
        mniGiaoDich.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniGiaoDichActionPerformed(evt);
            }
        });
        mnuQuanLy.add(mniGiaoDich);

        mniVayTien.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mniVayTien.setText("Vay tiền");
        mniVayTien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniVayTienActionPerformed(evt);
            }
        });
        mnuQuanLy.add(mniVayTien);

        mniTraGop.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mniTraGop.setText("Trả góp");
        mniTraGop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniTraGopActionPerformed(evt);
            }
        });
        mnuQuanLy.add(mniTraGop);
        mnuQuanLy.add(jSeparator4);

        mniNhanVien.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mniNhanVien.setText("Nhân viên");
        mniNhanVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniNhanVienActionPerformed(evt);
            }
        });
        mnuQuanLy.add(mniNhanVien);

        menuBar.add(mnuQuanLy);

        mnuThongKe.setText("Thống kê");

        mniThongKeKhachHang.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        mniThongKeKhachHang.setText("Khách hàng");
        mniThongKeKhachHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniThongKeKhachHangActionPerformed(evt);
            }
        });
        mnuThongKe.add(mniThongKeKhachHang);
        mnuThongKe.add(jSeparator3);

        mniThongKeGiaoDich.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        mniThongKeGiaoDich.setText("Giao dịch");
        mniThongKeGiaoDich.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniThongKeGiaoDichActionPerformed(evt);
            }
        });
        mnuThongKe.add(mniThongKeGiaoDich);

        mniThongKeTaiKhoan.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        mniThongKeTaiKhoan.setText("Tài khoản");
        mniThongKeTaiKhoan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniThongKeTaiKhoanActionPerformed(evt);
            }
        });
        mnuThongKe.add(mniThongKeTaiKhoan);

        mniThongKeVayTien.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        mniThongKeVayTien.setText("Vay tiền");
        mniThongKeVayTien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniThongKeVayTienActionPerformed(evt);
            }
        });
        mnuThongKe.add(mniThongKeVayTien);

        menuBar.add(mnuThongKe);

        mnuTroGiup.setText("Trợ giúp");

        mniHuongDan.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        mniHuongDan.setText("Hướng dẫn sử dụng");
        mniHuongDan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniHuongDanActionPerformed(evt);
            }
        });
        mnuTroGiup.add(mniHuongDan);
        mnuTroGiup.add(jSeparator5);

        mniGioiThieu.setText("Giới thiệu sản phẩm");
        mniGioiThieu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniGioiThieuActionPerformed(evt);
            }
        });
        mnuTroGiup.add(mniGioiThieu);

        menuBar.add(mnuTroGiup);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlTrangThai, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDangXuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDangXuatActionPerformed
        // TODO add your handling code here:
        this.dangXuat();
    }//GEN-LAST:event_btnDangXuatActionPerformed

    private void btnKetThucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKetThucActionPerformed
        // TODO add your handling code here:
        this.ketThuc();
    }//GEN-LAST:event_btnKetThucActionPerformed

    private void btnGiaoDichActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGiaoDichActionPerformed
        // TODO add your handling code here:
        this.openGiaoDich();
    }//GEN-LAST:event_btnGiaoDichActionPerformed

    private void btnVayTienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVayTienActionPerformed
        // TODO add your handling code here:
        this.openVayTien(0);
    }//GEN-LAST:event_btnVayTienActionPerformed

    private void mniDangXuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniDangXuatActionPerformed
        // TODO add your handling code here:
        this.dangXuat();
    }//GEN-LAST:event_mniDangXuatActionPerformed

    private void mniKetThucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniKetThucActionPerformed
        // TODO add your handling code here:
        this.ketThuc();
    }//GEN-LAST:event_mniKetThucActionPerformed

    private void mniDoiMatKhauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniDoiMatKhauActionPerformed
        // TODO add your handling code here:
        this.openDoiMatKhau();
    }//GEN-LAST:event_mniDoiMatKhauActionPerformed

    private void mniGiaoDichActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniGiaoDichActionPerformed
        // TODO add your handling code here:
        this.openGiaoDich();
    }//GEN-LAST:event_mniGiaoDichActionPerformed

    private void mniTaiKhoanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniTaiKhoanActionPerformed
        // TODO add your handling code here:
        this.openTaiKhoan();
    }//GEN-LAST:event_mniTaiKhoanActionPerformed

    private void mniKhachHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniKhachHangActionPerformed
        // TODO add your handling code here:
        this.openKhachHang();
    }//GEN-LAST:event_mniKhachHangActionPerformed

    private void mniNhanVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniNhanVienActionPerformed
        // TODO add your handling code here:
        this.openNhanVien();
    }//GEN-LAST:event_mniNhanVienActionPerformed

    private void btnHuongDanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHuongDanActionPerformed
        // TODO add your handling code here:
        this.openHuongDan();
    }//GEN-LAST:event_btnHuongDanActionPerformed

    private void mniThongKeTaiKhoanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniThongKeTaiKhoanActionPerformed
        // TODO add your handling code here:
        this.openThongKe(2);
    }//GEN-LAST:event_mniThongKeTaiKhoanActionPerformed

    private void mniThongKeKhachHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniThongKeKhachHangActionPerformed
        // TODO add your handling code here:
        this.openThongKe(0);
    }//GEN-LAST:event_mniThongKeKhachHangActionPerformed

    private void mniThongKeGiaoDichActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniThongKeGiaoDichActionPerformed
        // TODO add your handling code here:
        this.openThongKe(1);
    }//GEN-LAST:event_mniThongKeGiaoDichActionPerformed

    private void mniThongKeVayTienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniThongKeVayTienActionPerformed
        // TODO add your handling code here:
        this.openThongKe(3);
    }//GEN-LAST:event_mniThongKeVayTienActionPerformed

    private void mniGioiThieuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniGioiThieuActionPerformed
        // TODO add your handling code here:
        this.openGioiThieu();
    }//GEN-LAST:event_mniGioiThieuActionPerformed

    private void mniHuongDanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniHuongDanActionPerformed
        // TODO add your handling code here:
        this.openHuongDan();
    }//GEN-LAST:event_mniHuongDanActionPerformed

    private void mniVayTienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniVayTienActionPerformed
        // TODO add your handling code here:
        this.openVayTien(0);
    }//GEN-LAST:event_mniVayTienActionPerformed

    private void btnTraGopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTraGopActionPerformed
        // TODO add your handling code here:
        this.openVayTien(1);
    }//GEN-LAST:event_btnTraGopActionPerformed

    private void mniDangNhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniDangNhapActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mniDangNhapActionPerformed

    private void mniTraGopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniTraGopActionPerformed
        // TODO add your handling code here:
        this.openVayTien(1);
    }//GEN-LAST:event_mniTraGopActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>


        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDangXuat;
    private javax.swing.JButton btnGiaoDich;
    private javax.swing.JButton btnHuongDan;
    private javax.swing.JButton btnKetThuc;
    private javax.swing.JButton btnTraGop;
    private javax.swing.JButton btnVayTien;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JLabel lblDongHo;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSoDu;
    private javax.swing.JLabel lblTrangThai;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem mniDangNhap;
    private javax.swing.JMenuItem mniDangXuat;
    private javax.swing.JMenuItem mniDoiMatKhau;
    private javax.swing.JMenuItem mniGiaoDich;
    private javax.swing.JMenuItem mniGioiThieu;
    private javax.swing.JMenuItem mniHuongDan;
    private javax.swing.JMenuItem mniKetThuc;
    private javax.swing.JMenuItem mniKhachHang;
    private javax.swing.JMenuItem mniNhanVien;
    private javax.swing.JMenuItem mniTaiKhoan;
    private javax.swing.JMenuItem mniThongKeGiaoDich;
    private javax.swing.JMenuItem mniThongKeKhachHang;
    private javax.swing.JMenuItem mniThongKeTaiKhoan;
    private javax.swing.JMenuItem mniThongKeVayTien;
    private javax.swing.JMenuItem mniTraGop;
    private javax.swing.JMenuItem mniVayTien;
    private javax.swing.JMenu mnuHeThong;
    private javax.swing.JMenu mnuQuanLy;
    private javax.swing.JMenu mnuThongKe;
    private javax.swing.JMenu mnuTroGiup;
    private javax.swing.JPanel pnlTrangThai;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables

    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.edusys.ui;

import com.edusys.utils.Auth;
import com.edusys.utils.XJdbc;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class GiaoDichJDialog extends javax.swing.JDialog {

    private DefaultTableModel tableModel;
    private int soLanThatBai = 0;

    public GiaoDichJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
        loadLichSuGiaoDich();
    }

    private void init() {
        this.setLocationRelativeTo(null);
        tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setRowCount(0);

        loadLoaiGiaoDich();
        loadTaiKhoanVaoCboTK(); // Tải danh sách tài khoản vào cboTK

        // Cố định không cho chỉnh sửa txtSoDu và txtSoThe
        txtSoDu.setEditable(false);
        txtSoDienThoai.setEditable(false);
        txtSoThe.setEditable(false);

        if (Auth.isLogin() && Auth.isCustomer()) {
            // Không cố định cboTK nữa, cho phép khách hàng chọn tài khoản
            if (cboTK.getItemCount() > 0) {
                cboTK.setSelectedIndex(0); // Chọn tài khoản đầu tiên mặc định
                String maTaiKhoan = cboTK.getSelectedItem().toString();
                updateSoDuAndSoThe(maTaiKhoan); // Cập nhật số dư và số thẻ cho tài khoản mặc định
                txtTenNguoiNhan.setText(getCurrentHoTen());
                txtTenNguoiNhan.setEditable(false);
            } else {
                cboTK.setSelectedIndex(-1);
                txtSoDu.setText("");
                txtSoThe.setText("");
                JOptionPane.showMessageDialog(this, "Khách hàng chưa có tài khoản nào!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        } else if (Auth.isEmployee()) {
            if (cboTK.getItemCount() > 0) {
                cboTK.setSelectedIndex(0); // Chọn tài khoản đầu tiên mặc định
                String maTaiKhoan = cboTK.getSelectedItem().toString();
                updateSoDuAndSoThe(maTaiKhoan);
            } else {
                cboTK.setSelectedIndex(-1);
                txtSoDu.setText("");
                txtSoThe.setText("");
            }
        }

        // Thêm sự kiện khi thay đổi tài khoản trong cboTK
        cboTK.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String selectedMaTaiKhoan = cboTK.getSelectedItem() != null ? cboTK.getSelectedItem().toString() : null;
                if (selectedMaTaiKhoan != null) {
                    updateSoDuAndSoThe(selectedMaTaiKhoan);
                } else {
                    txtSoDu.setText("");
                    txtSoThe.setText("");
                }
            }
        });

        txtMaNguoiNhan.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateTenNguoiNhan(txtMaNguoiNhan.getText().trim());
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateTenNguoiNhan(txtMaNguoiNhan.getText().trim());
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateTenNguoiNhan(txtMaNguoiNhan.getText().trim());
            }
        });

        cboPhanLoai.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                updateFormFields();
            }
        });

        // Thêm sự kiện click chuột vào bảng jTable1 (chỉ áp dụng cho nhân viên)
        jTable1.getColumnModel().getColumn(2).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
        @Override
        public void setValue(Object value) {
            if (value instanceof Double) {
                java.text.DecimalFormat df = new java.text.DecimalFormat("#,###.0");
                setText(df.format(value));
            } else {
                setText(value != null ? value.toString() : "");
            }
        }
    });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        if (Auth.isEmployee()) {
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow >= 0) {
                String maTaiKhoan = jTable1.getValueAt(selectedRow, 0).toString(); // Cột "Số tài khoản"
                showTenKhachHang(maTaiKhoan); // Hiển thị tên khách hàng
            }
        }
    }
});
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(120);
        updateFormFields();
    }

    // Phương thức để hiển thị tên khách hàng dựa trên mã tài khoản
    private void showTenKhachHang(String maTaiKhoan) {
        try {
            // Truy vấn để lấy MaKhachHang từ bảng TAI_KHOAN
            String sqlTaiKhoan = "SELECT MaKhachHang FROM TAI_KHOAN WHERE MaTaiKhoan = ?";
            ResultSet rsTaiKhoan = XJdbc.query(sqlTaiKhoan, maTaiKhoan);
            String maKhachHang = null;
            if (rsTaiKhoan.next()) {
                maKhachHang = rsTaiKhoan.getString("MaKhachHang");
            } else {
                JOptionPane.showMessageDialog(this, "Tài khoản không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                rsTaiKhoan.close();
                return;
            }
            rsTaiKhoan.close();

            // Truy vấn để lấy HoTen từ bảng KHACH_HANG
            String sqlKhachHang = "SELECT HoTen FROM KHACH_HANG WHERE MaKhachHang = ?";
            ResultSet rsKhachHang = XJdbc.query(sqlKhachHang, maKhachHang);
            if (rsKhachHang.next()) {
                String hoTen = rsKhachHang.getString("HoTen");
                JOptionPane.showMessageDialog(this, "Chủ tài khoản " + maTaiKhoan + ": " + hoTen, "Thông tin", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin khách hàng cho tài khoản này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            rsKhachHang.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tra cứu thông tin khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTaiKhoanVaoCboTK() {
        try {
            String sql = "SELECT MaTaiKhoan FROM TAI_KHOAN";
            if (Auth.isCustomer()) {
                sql += " WHERE MaKhachHang = ?";
            }
            ResultSet rs = Auth.isCustomer() ? XJdbc.query(sql, Auth.userKhachHang.getMaKhachHang()) : XJdbc.query(sql);
            cboTK.removeAllItems();
            while (rs.next()) {
                cboTK.addItem(rs.getString("MaTaiKhoan"));
            }
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách tài khoản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSoDuAndSoThe(String maTaiKhoan) {
        try {
            // Lấy số dư từ bảng TAI_KHOAN
            String sqlSoDu = "SELECT SoDu FROM TAI_KHOAN WHERE MaTaiKhoan = ?";
            ResultSet rsSoDu = XJdbc.query(sqlSoDu, maTaiKhoan);
            if (rsSoDu.next()) {
                txtSoDu.setText(String.valueOf(rsSoDu.getDouble("SoDu")));
            } else {
                txtSoDu.setText("");
            }
            rsSoDu.close();

            // Lấy số thẻ từ bảng THE_NGAN_HANG
            String sqlSoThe = "SELECT SoThe FROM THE_NGAN_HANG WHERE MaTaiKhoan = ?";
            ResultSet rsSoThe = XJdbc.query(sqlSoThe, maTaiKhoan);
            if (rsSoThe.next()) {
                txtSoThe.setText(rsSoThe.getString("SoThe"));
            } else {
                txtSoThe.setText("Chưa liên kết thẻ");
            }
            rsSoThe.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy số dư hoặc số thẻ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtSoDu.setText("");
            txtSoThe.setText("");
        }
    }

    private void updateFormFields() {
        String loaiGiaoDich = cboPhanLoai.getSelectedItem() != null ? cboPhanLoai.getSelectedItem().toString() : "Rút tiền";

        lblNguoiNhan.setVisible(false);
        txtMaNguoiNhan.setVisible(false);
        lblTenNH.setVisible(false);
        txtTenNguoiNhan.setVisible(true);
        lblNoiDung.setVisible(false);
        txtNoiDung.setVisible(false);
        jLabel2.setVisible(false); // Ẩn nhãn "Số điện thoại"
        txtSoDienThoai.setVisible(false); // Ẩn trường số điện thoại

        lblTK.setVisible(true);
        cboTK.setVisible(true);
        lblSoDu.setVisible(true);
        txtSoDu.setVisible(true);
        jLabel1.setVisible(true);
        txtSoThe.setVisible(true);
        lblSoTienChuyen.setVisible(true);
        txtSoTienChuyen.setVisible(true);

        if (loaiGiaoDich.equals("Nạp tiền") || loaiGiaoDich.equals("Rút tiền")) {
            if (Auth.isCustomer()) {
                txtTenNguoiNhan.setText(getCurrentHoTen());
                txtTenNguoiNhan.setEditable(false);
            }
        } else if (loaiGiaoDich.equals("Chuyển khoản")) {
            lblNguoiNhan.setVisible(true);
            txtMaNguoiNhan.setVisible(true);
            lblTenNH.setVisible(true);
            txtTenNguoiNhan.setEditable(true);
            jLabel2.setVisible(true); // Hiển thị nhãn "Số điện thoại"
            txtSoDienThoai.setVisible(true); // Hiển thị trường số điện thoại
            lblNoiDung.setVisible(true);
            txtNoiDung.setVisible(true);
        }
    }

    private String getCurrentMaTaiKhoan() {
        return cboTK.getSelectedItem() != null ? cboTK.getSelectedItem().toString() : null;
    }

    private String getCurrentHoTen() {
        if (Auth.isEmployee()) {
            return Auth.userNhanVien.getHoTen();
        } else if (Auth.isCustomer()) {
            return Auth.userKhachHang.getHoTen();
        }
        return "Không xác định";
    }

    private void loadLichSuGiaoDich() {
    try {
        String sql;
        if (Auth.isEmployee()) {
            sql = "SELECT gd.MaTaiKhoan, ct.TenNguoiNhan, gd.SoTien, gd.MoTa, gd.LoaiGiaoDich, kh.HoTen " +
                  "FROM GIAO_DICH gd " +
                  "LEFT JOIN CHI_TIET_GIAO_DICH ct ON gd.MaGiaoDich = ct.MaGiaoDich " +
                  "JOIN TAI_KHOAN tk ON gd.MaTaiKhoan = tk.MaTaiKhoan " +
                  "JOIN KHACH_HANG kh ON tk.MaKhachHang = kh.MaKhachHang";
        } else {
            sql = "SELECT gd.MaTaiKhoan, ct.TenNguoiNhan, gd.SoTien, gd.MoTa, gd.LoaiGiaoDich, kh.HoTen " +
                  "FROM GIAO_DICH gd " +
                  "LEFT JOIN CHI_TIET_GIAO_DICH ct ON gd.MaGiaoDich = ct.MaGiaoDich " +
                  "JOIN TAI_KHOAN tk ON gd.MaTaiKhoan = tk.MaTaiKhoan " +
                  "JOIN KHACH_HANG kh ON tk.MaKhachHang = kh.MaKhachHang " +
                  "WHERE gd.MaTaiKhoan = ?";
        }
        ResultSet rs = Auth.isEmployee() ? XJdbc.query(sql) : XJdbc.query(sql, getCurrentMaTaiKhoan());
        tableModel.setRowCount(0);
        while (rs.next()) {
            String loaiGiaoDich = rs.getString("LoaiGiaoDich");
            String tenNguoiNhan = "";
            if (loaiGiaoDich.equals("Chuyển khoản")) {
                tenNguoiNhan = rs.getString("TenNguoiNhan") != null ? rs.getString("TenNguoiNhan") : "";
            } else {
                tenNguoiNhan = rs.getString("HoTen"); // Hiển thị tên của chủ tài khoản
            }
            Object[] row = {
                rs.getString("MaTaiKhoan"),
                tenNguoiNhan,
                rs.getDouble("SoTien"),
                rs.getString("MoTa"),
                loaiGiaoDich
            };
            tableModel.addRow(row);
        }
        rs.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi tải lịch sử giao dịch: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}

    private String generateMaGiaoDich() throws SQLException {
        String sql = "SELECT MAX(MaGiaoDich) FROM GIAO_DICH";
        ResultSet rs = XJdbc.query(sql);
        String maxMaGiaoDich = null;
        if (rs.next()) {
            maxMaGiaoDich = rs.getString(1);
        }
        rs.close();
        if (maxMaGiaoDich == null) {
            return "GD001";
        }
        int number = Integer.parseInt(maxMaGiaoDich.substring(2)) + 1;
        return String.format("GD%03d", number);
    }

    private void loadLoaiGiaoDich() {
        cboPhanLoai.removeAllItems();
        if (Auth.isEmployee()) {
            // Nhân viên được phép thực hiện tất cả các loại giao dịch
            cboPhanLoai.addItem("Nạp tiền");
            cboPhanLoai.addItem("Rút tiền");
            cboPhanLoai.addItem("Chuyển khoản");
        } else if (Auth.isCustomer()) {
            // Khách hàng chỉ được phép "Rút tiền" và "Chuyển khoản"
            cboPhanLoai.addItem("Rút tiền");
            cboPhanLoai.addItem("Chuyển khoản");
        }
        cboPhanLoai.setSelectedIndex(0);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        lblTK = new javax.swing.JLabel();
        lblTenNH = new javax.swing.JLabel();
        txtTenNguoiNhan = new javax.swing.JTextField();
        lblSoTienChuyen = new javax.swing.JLabel();
        txtSoTienChuyen = new javax.swing.JTextField();
        lblNoiDung = new javax.swing.JLabel();
        txtNoiDung = new javax.swing.JTextField();
        lblPhanLoai = new javax.swing.JLabel();
        btnChuyenTien = new javax.swing.JButton();
        cboPhanLoai = new javax.swing.JComboBox<>();
        lblNguoiNhan = new javax.swing.JLabel();
        txtMaNguoiNhan = new javax.swing.JTextField();
        cboTK = new javax.swing.JComboBox<>();
        lblSoDu = new javax.swing.JLabel();
        txtSoDu = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtSoThe = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtSoDienThoai = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản Lý Giao Dịch");

        lblTK.setText("Tài khoản ngân hàng");

        lblTenNH.setText("Tên người nhận");

        lblSoTienChuyen.setText("Số tiền giao dịch");

        lblNoiDung.setText("Nội dung giao dịch");

        lblPhanLoai.setText("Phân loại giao dịch");

        btnChuyenTien.setBackground(new java.awt.Color(153, 255, 153));
        btnChuyenTien.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnChuyenTien.setForeground(new java.awt.Color(255, 255, 255));
        btnChuyenTien.setText("Giao dịch");
        btnChuyenTien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChuyenTienActionPerformed(evt);
            }
        });

        lblNguoiNhan.setText("Mã người nhận");

        lblSoDu.setText("Số dư");

        jLabel1.setText("Thẻ ngân hàng");

        txtSoThe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSoTheActionPerformed(evt);
            }
        });

        jLabel2.setText("Số điện thoại");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTenNguoiNhan)
                    .addComponent(txtSoTienChuyen)
                    .addComponent(txtNoiDung)
                    .addComponent(txtMaNguoiNhan, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnChuyenTien, javax.swing.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE)
                    .addComponent(txtSoDu)
                    .addComponent(txtSoThe)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTenNH)
                            .addComponent(lblSoTienChuyen)
                            .addComponent(lblNoiDung)
                            .addComponent(lblPhanLoai)
                            .addComponent(cboPhanLoai, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(cboTK, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblTK, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(lblSoDu, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblNguoiNhan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel2))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtSoDienThoai))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTK)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboTK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSoDu)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSoDu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSoThe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblNguoiNhan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaNguoiNhan, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTenNH)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTenNguoiNhan, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSoTienChuyen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSoTienChuyen, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNoiDung)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNoiDung, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPhanLoai)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboPhanLoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnChuyenTien, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        jTabbedPane1.addTab("Giao dịch", jPanel1);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Số tài khoản", "Tên người nhận", "Số tiền giao dịch", "Nội dung giao dịch", "Phân loại"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 734, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Lịch sử", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnChuyenTienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChuyenTienActionPerformed
        // TODO add your handling code here:
    String maTaiKhoanNguoiGui = getCurrentMaTaiKhoan();
    String tenNguoiNhan = txtTenNguoiNhan.getText().trim();
    String soTienStr = txtSoTienChuyen.getText().trim();
    String noiDung = txtNoiDung.getText().trim();
    String soDienThoai = txtSoDienThoai.getText().trim(); // Lấy số điện thoại

    Object selectedItem = cboPhanLoai.getSelectedItem();
    if (selectedItem == null) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn loại giao dịch!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        return;
    }
    String loaiGiaoDich = selectedItem.toString();

    // Kiểm tra quyền thực hiện "Nạp tiền" - chỉ nhân viên được phép
    if (loaiGiaoDich.equals("Nạp tiền") && Auth.isCustomer()) {
        JOptionPane.showMessageDialog(this, "Khách hàng không được phép thực hiện nạp tiền!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (!loaiGiaoDich.equals("Nạp tiền") && !loaiGiaoDich.equals("Rút tiền") && !loaiGiaoDich.equals("Chuyển khoản")) {
        JOptionPane.showMessageDialog(this, "Loại giao dịch không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String inputNguoiNhan = txtMaNguoiNhan.getText().trim();

    if (maTaiKhoanNguoiGui == null || maTaiKhoanNguoiGui.isEmpty() || soTienStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin bắt buộc!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (loaiGiaoDich.equals("Chuyển khoản") && (inputNguoiNhan.isEmpty() || soDienThoai.isEmpty())) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập mã người nhận và kiểm tra số điện thoại!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    double soTien;
    try {
        soTien = Double.parseDouble(soTienStr);
        if (soTien <= 0) {
            JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    double soDuNguoiGui = 0;
    try {
        String checkTaiKhoanSql = "SELECT SoDu FROM TAI_KHOAN WHERE MaTaiKhoan = ?";
        ResultSet rs = XJdbc.query(checkTaiKhoanSql, maTaiKhoanNguoiGui);
        if (!rs.next()) {
            JOptionPane.showMessageDialog(this, "Tài khoản người gửi không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        soDuNguoiGui = rs.getDouble("SoDu");
        rs.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi kiểm tra tài khoản người gửi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if ((loaiGiaoDich.equals("Rút tiền") || loaiGiaoDich.equals("Chuyển khoản")) && soDuNguoiGui < soTien) {
        soLanThatBai++;
        if (soLanThatBai >= 3) {
            JOptionPane.showMessageDialog(this, "Bạn đã vượt quá 3 lần giao dịch không đúng luật. Chương trình sẽ tự động đóng lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } else {
            JOptionPane.showMessageDialog(this, "Số dư không đủ để thực hiện giao dịch! Số lần thất bại: " + soLanThatBai, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return;
    }

    String maTaiKhoanNguoiNhan = null;
    String maKhachHangNguoiNhan = null;
    if (loaiGiaoDich.equals("Chuyển khoản")) {
        try {
            if (inputNguoiNhan.startsWith("TK")) {
                maTaiKhoanNguoiNhan = inputNguoiNhan;
                String checkNguoiNhanSql = "SELECT kh.MaKhachHang, kh.SoDienThoai " +
                                          "FROM TAI_KHOAN tk " +
                                          "JOIN KHACH_HANG kh ON tk.MaKhachHang = kh.MaKhachHang " +
                                          "WHERE tk.MaTaiKhoan = ?";
                ResultSet rs = XJdbc.query(checkNguoiNhanSql, maTaiKhoanNguoiNhan);
                if (rs.next()) {
                    maKhachHangNguoiNhan = rs.getString("MaKhachHang");
                    String soDienThoaiDB = rs.getString("SoDienThoai");
                    if (!soDienThoai.equals(soDienThoaiDB)) {
                        JOptionPane.showMessageDialog(this, "Số điện thoại không khớp với tài khoản người nhận!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Tài khoản người nhận không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    
return;
                }
                rs.close();
            } else {
                maKhachHangNguoiNhan = inputNguoiNhan;
                String checkNguoiNhanSql = "SELECT tk.MaTaiKhoan, kh.SoDienThoai " +
                                          "FROM KHACH_HANG kh " +
                                          "LEFT JOIN TAI_KHOAN tk ON kh.MaKhachHang = tk.MaKhachHang " +
                                          "WHERE kh.MaKhachHang = ?";
                ResultSet rs = XJdbc.query(checkNguoiNhanSql, maKhachHangNguoiNhan);
                if (rs.next()) {
                    maTaiKhoanNguoiNhan = rs.getString("MaTaiKhoan");
                    String soDienThoaiDB = rs.getString("SoDienThoai");
                    if (!soDienThoai.equals(soDienThoaiDB)) {
                        JOptionPane.showMessageDialog(this, "Số điện thoại không khớp với khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (maTaiKhoanNguoiNhan == null) {
                        String checkKHSql = "SELECT COUNT(*) FROM KHACH_HANG WHERE MaKhachHang = ?";
                        ResultSet rsKH = XJdbc.query(checkKHSql, maKhachHangNguoiNhan);
                        rsKH.next();
                        if (rsKH.getInt(1) == 0) {
                            JOptionPane.showMessageDialog(this, "Mã khách hàng người nhận không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            rsKH.close();
                            return;
                        }
                        rsKH.close();

                        int confirm = JOptionPane.showConfirmDialog(this,
                            "Khách hàng này chưa có tài khoản. Bạn có muốn tạo tài khoản mới không?",
                            "Thông báo", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            TaiKhoanJDialog taiKhoanDialog = new TaiKhoanJDialog(null, true);
                            taiKhoanDialog.setMaKhachHang(maKhachHangNguoiNhan);
                            taiKhoanDialog.setVisible(true);
                            rs = XJdbc.query(checkNguoiNhanSql, maKhachHangNguoiNhan);
                            if (rs.next()) {
                                maTaiKhoanNguoiNhan = rs.getString("MaTaiKhoan");
                            } else {
                                JOptionPane.showMessageDialog(this, "Vui lòng tạo tài khoản trước khi chuyển tiền!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Mã khách hàng người nhận không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                rs.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tra cứu tài khoản người nhận: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    try {
        String maGiaoDich = generateMaGiaoDich();

        String sqlGiaoDich = "INSERT INTO GIAO_DICH (MaGiaoDich, LoaiGiaoDich, SoTien, NgayGiaoDich, MoTa, MaTaiKhoan) " +
                             "VALUES (?, ?, ?, GETDATE(), ?, ?)";
        XJdbc.update(sqlGiaoDich, maGiaoDich, loaiGiaoDich, soTien, noiDung, maTaiKhoanNguoiGui);

        if (loaiGiaoDich.equals("Chuyển khoản")) {
            String maChiTiet = "CTGD" + maGiaoDich.substring(2);
            String sqlChiTiet = "INSERT INTO CHI_TIET_GIAO_DICH (MaChiTiet, MaGiaoDich, TaiKhoanNguoiGui, TenNguoiGui, TaiKhoanNguoiNhan, TenNguoiNhan, SoTien, TrangThai) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            String tenNguoiGui = getCurrentHoTen();
            XJdbc.update(sqlChiTiet, maChiTiet, maGiaoDich, maTaiKhoanNguoiGui, tenNguoiGui, maTaiKhoanNguoiNhan, tenNguoiNhan, soTien, "Thành công");

            String updateSoDuNguoiNhanSql = "UPDATE TAI_KHOAN SET SoDu = SoDu + ? WHERE MaTaiKhoan = ?";
            XJdbc.update(updateSoDuNguoiNhanSql, soTien, maTaiKhoanNguoiNhan);
        }

        String updateSoDuSql = "UPDATE TAI_KHOAN SET SoDu = SoDu " +
                               (loaiGiaoDich.equals("Nạp tiền") ? "+" : "-") + " ? WHERE MaTaiKhoan = ?";
        XJdbc.update(updateSoDuSql, soTien, maTaiKhoanNguoiGui);

        soLanThatBai = 0;
        JOptionPane.showMessageDialog(this, "Giao dịch thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        clearForm();
        loadLichSuGiaoDich();
        updateSoDuAndSoThe(maTaiKhoanNguoiGui); // Cập nhật lại số dư sau giao dịch
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi thực hiện giao dịch: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnChuyenTienActionPerformed

    private void txtSoTheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSoTheActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSoTheActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GiaoDichJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            GiaoDichJDialog dialog = new GiaoDichJDialog(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChuyenTien;
    private javax.swing.JComboBox<String> cboPhanLoai;
    private javax.swing.JComboBox<String> cboTK;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblNguoiNhan;
    private javax.swing.JLabel lblNoiDung;
    private javax.swing.JLabel lblPhanLoai;
    private javax.swing.JLabel lblSoDu;
    private javax.swing.JLabel lblSoTienChuyen;
    private javax.swing.JLabel lblTK;
    private javax.swing.JLabel lblTenNH;
    private javax.swing.JTextField txtMaNguoiNhan;
    private javax.swing.JTextField txtNoiDung;
    private javax.swing.JTextField txtSoDienThoai;
    private javax.swing.JTextField txtSoDu;
    private javax.swing.JTextField txtSoThe;
    private javax.swing.JTextField txtSoTienChuyen;
    private javax.swing.JTextField txtTenNguoiNhan;
    // End of variables declaration//GEN-END:variables
  private void clearForm() {
    if (Auth.isCustomer()) {
        String maTaiKhoan = getCurrentMaTaiKhoan();
        if (maTaiKhoan != null) {
            cboTK.setSelectedItem(maTaiKhoan);
            updateSoDuAndSoThe(maTaiKhoan);
        } else {
            cboTK.setSelectedIndex(-1);
            txtSoDu.setText("");
            txtSoThe.setText("");
        }
        txtTenNguoiNhan.setText(getCurrentHoTen());
    } else {
        cboTK.setSelectedIndex(-1);
        txtSoDu.setText("");
        txtSoThe.setText("");
        txtTenNguoiNhan.setText("");
    }
    txtSoTienChuyen.setText("");
    txtNoiDung.setText("");
    txtMaNguoiNhan.setText("");
    txtSoDienThoai.setText(""); // Thêm dòng này để xóa số điện thoại
    if (cboPhanLoai.getItemCount() > 0) {
        cboPhanLoai.setSelectedIndex(0);
    }
    updateFormFields();
}

    private void updateTenNguoiNhan(String input) {
    if (input == null || input.trim().isEmpty()) {
        txtTenNguoiNhan.setText("");
        txtSoDienThoai.setText("");
        return;
    }
    try {
        String sql;
        ResultSet rs;
        if (input.startsWith("TK")) {
            sql = "SELECT kh.HoTen, kh.SoDienThoai " +
                  "FROM KHACH_HANG kh " +
                  "JOIN TAI_KHOAN tk ON kh.MaKhachHang = tk.MaKhachHang " +
                  "WHERE tk.MaTaiKhoan = ?";
            rs = XJdbc.query(sql, input);
        } else {
            sql = "SELECT HoTen, SoDienThoai " +
                  "FROM KHACH_HANG WHERE MaKhachHang = ?";
            rs = XJdbc.query(sql, input);
        }
        if (rs.next()) {
            String tenNguoiNhan = rs.getString("HoTen");
            String soDienThoai = rs.getString("SoDienThoai");
            txtTenNguoiNhan.setText(tenNguoiNhan);
            txtSoDienThoai.setText(soDienThoai);
        } else {
            txtTenNguoiNhan.setText("");
            txtSoDienThoai.setText("");
        }
        rs.close();
    } catch (SQLException e) {
        txtTenNguoiNhan.setText("");
        txtSoDienThoai.setText("");
        JOptionPane.showMessageDialog(this, "Lỗi khi tra cứu thông tin người nhận: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
}

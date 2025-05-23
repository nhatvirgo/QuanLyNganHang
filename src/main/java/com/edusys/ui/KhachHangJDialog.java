/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.edusys.ui;

import com.edusys.utils.Auth;
import com.edusys.utils.XJdbc;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class KhachHangJDialog extends javax.swing.JDialog {

    private DefaultTableModel tblModel;
    private int currentRow = -1;
    private SimpleDateFormat sdfInput = new SimpleDateFormat("dd/MM/yyyy"); // Định dạng nhập từ người dùng
    private SimpleDateFormat sdfOutput = new SimpleDateFormat("yyyy-MM-dd"); // Định dạng cho SQL Server

    public KhachHangJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
        tblModel = (DefaultTableModel) tblThongKeKhachHang.getModel();
        loadDataToTable();
        tblThongKeKhachHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblThongKeKhachHangMouseClicked(evt);
            }
        });
    }

    private void tblThongKeKhachHangMouseClicked(java.awt.event.MouseEvent evt) {
        int selectedRow = tblThongKeKhachHang.getSelectedRow();
        if (selectedRow >= 0) {
            currentRow = selectedRow;
            fillFormFromSelectedRow();
        }
    }

    private void init() {
    this.setLocationRelativeTo(null);
    tblModel = (DefaultTableModel) tblThongKeKhachHang.getModel();
    tblModel.setRowCount(0);

    // Áp dụng DocumentFilter cho txtSoCCCD (giới hạn 12 số)
    ((javax.swing.text.AbstractDocument) txtSoCCCD.getDocument()).setDocumentFilter(new NumberLengthFilter(12));

    // Áp dụng DocumentFilter cho txtSDT (giới hạn 10 số)
    ((javax.swing.text.AbstractDocument) txtSDT.getDocument()).setDocumentFilter(new NumberLengthFilter(10));

    if (Auth.isLogin()) {
        if (Auth.isCustomer()) {
            // Ẩn các nút không cần thiết
            btnNew.setVisible(false);
            btnDelete.setVisible(false);
            btnReset.setVisible(false);
            btnBack.setVisible(false);
            btnFirst.setVisible(false);
            btnLast.setVisible(false);
            btnNext.setVisible(false);
            btnEdit.setVisible(true);

            // Điền thông tin khách hàng hiện tại vào form
            txtMaKH.setText(Auth.userKhachHang.getMaKhachHang());
            txtHoTen.setText(Auth.userKhachHang.getHoTen());
            txtNgaySinh.setText(sdfInput.format(Auth.userKhachHang.getNgaySinh()));
            if (Auth.userKhachHang.isGioiTinh()) {
                rdoNam.setSelected(true);
            } else {
                rdoNu.setSelected(true);
            }
            txtSoCCCD.setText(Auth.userKhachHang.getSoCCCD());
            txtDiaChi.setText(Auth.userKhachHang.getDiaChi());
            txtSDT.setText(Auth.userKhachHang.getSoDienThoai());
            txtEmail.setText(Auth.userKhachHang.getEmail());
            txtMatKhau.setText(Auth.userKhachHang.getMatKhau());
            txtXacNhanMatKhau.setText(Auth.userKhachHang.getMatKhau());

            // Khóa các trường không cho sửa
            txtMaKH.setEditable(false);
            txtNgaySinh.setEditable(false);
            rdoNam.setEnabled(false);
            rdoNu.setEnabled(false);
            txtSoCCCD.setEditable(false);

            // Ẩn tab "Danh sách" (tùy chọn)
            jTabbedPane1.removeTabAt(1); // Xóa tab "DANH SÁCH"
        } else if (Auth.isEmployee()) {
            // Hiển thị đầy đủ chức năng cho nhân viên
            btnNew.setVisible(true);
            btnDelete.setVisible(true);
            btnEdit.setVisible(true);
            btnReset.setVisible(true);
            btnBack.setVisible(true);
            btnFirst.setVisible(true);
            btnLast.setVisible(true);
            btnNext.setVisible(true);
        }
    }

    loadDataToTable();
}

    private void loadDataToTable() {
    try {
        String sql;
        if (Auth.isCustomer()) {
            sql = "SELECT MaKhachHang, HoTen, NgaySinh, GioiTinh, SoCCCD, DiaChi, SoDienThoai, Email, MatKhau, MaNhanVien, NgayTao " +
                  "FROM KHACH_HANG WHERE MaKhachHang = ?";
            ResultSet rs = XJdbc.query(sql, Auth.userKhachHang.getMaKhachHang());
            tblModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                    rs.getString("MaKhachHang"),
                    rs.getString("HoTen"),
                    sdfInput.format(rs.getDate("NgaySinh")),
                    rs.getBoolean("GioiTinh") ? "Nam" : "Nữ",
                    rs.getString("SoCCCD"),
                    rs.getString("DiaChi"),
                    rs.getString("SoDienThoai"),
                    rs.getString("Email"),
                    rs.getString("MatKhau"),
                    rs.getString("MaNhanVien"),
                    sdfInput.format(rs.getDate("NgayTao"))
                };
                tblModel.addRow(row);
            }
            rs.close();
        } else if (Auth.isEmployee()) {
            sql = "SELECT MaKhachHang, HoTen, NgaySinh, GioiTinh, SoCCCD, DiaChi, SoDienThoai, Email, MatKhau, MaNhanVien, NgayTao " +
                  "FROM KHACH_HANG";
            ResultSet rs = XJdbc.query(sql);
            tblModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                    rs.getString("MaKhachHang"),
                    rs.getString("HoTen"),
                    sdfInput.format(rs.getDate("NgaySinh")),
                    rs.getBoolean("GioiTinh") ? "Nam" : "Nữ",
                    rs.getString("SoCCCD"),
                    rs.getString("DiaChi"),
                    rs.getString("SoDienThoai"),
                    rs.getString("Email"),
                    rs.getString("MatKhau"),
                    rs.getString("MaNhanVien"),
                    sdfInput.format(rs.getDate("NgayTao"))
                };
                tblModel.addRow(row);
            }
            rs.close();
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}

    private String generateMaKH() throws SQLException {
        String sql = "SELECT MAX(MaKhachHang) FROM KHACH_HANG";
        ResultSet rs = XJdbc.query(sql);
        String maxMaKH = null;
        if (rs.next()) {
            maxMaKH = rs.getString(1);
        }
        rs.close();
        if (maxMaKH == null) {
            return "KH001";
        }
        int number = Integer.parseInt(maxMaKH.substring(2)) + 1;
        return String.format("KH%03d", number);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblHoTen = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtMaKH = new javax.swing.JTextField();
        txtHoTen = new javax.swing.JTextField();
        txtSoCCCD = new javax.swing.JTextField();
        txtSDT = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        lblCCCD = new javax.swing.JLabel();
        txtNgaySinh = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        rdoNam = new javax.swing.JRadioButton();
        rdoNu = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtMatKhau = new javax.swing.JTextField();
        txtXacNhanMatKhau = new javax.swing.JTextField();
        btnNew = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        btnFirst = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        lblHinh = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtDiaChi = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblThongKeKhachHang = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản Lý Khách Hàng");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel1.setText("Quản lý khách hàng");

        jLabel2.setText("Mã khách hàng:");

        lblHoTen.setText("Họ tên:");

        jLabel4.setText("Số CCCD:");

        jLabel5.setText("Số điện thoại:");

        jLabel6.setText("Email:");

        txtMaKH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaKHActionPerformed(evt);
            }
        });

        txtSoCCCD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSoCCCDActionPerformed(evt);
            }
        });

        lblCCCD.setText("Ngày sinh:");

        jLabel3.setText("Giới tính:");

        buttonGroup1.add(rdoNam);
        rdoNam.setText("Nam");

        buttonGroup1.add(rdoNu);
        rdoNu.setText("Nữ");

        jLabel8.setText("Mật khẩu:");

        jLabel9.setText("Xác nhận mật khẩu:");

        btnNew.setText("Thêm");
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });

        btnDelete.setText("Xóa");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnEdit.setText("Sửa");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnReset.setText("Mới");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        btnBack.setText("| <");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        btnFirst.setText("<<<");
        btnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });

        btnLast.setText(">>>");
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });

        btnNext.setText("> |");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        lblHinh.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel10.setText("Hình ảnh");

        jLabel7.setText("Địa chỉ:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(62, 62, 62)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCCCD)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(lblHoTen)
                                    .addComponent(jLabel5))
                                .addGap(30, 30, 30)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(rdoNam)
                                        .addGap(18, 18, 18)
                                        .addComponent(rdoNu))
                                    .addComponent(txtSDT, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                                    .addComponent(txtSoCCCD)
                                    .addComponent(txtNgaySinh)
                                    .addComponent(txtHoTen)
                                    .addComponent(txtMaKH)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtXacNhanMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(lblHinh, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(22, 22, 22))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(66, 66, 66))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLast, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(lblHinh, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtMaKH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblHoTen)
                            .addComponent(txtHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCCCD)
                            .addComponent(txtNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(rdoNam)
                            .addComponent(rdoNu))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtSoCCCD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txtXacNhanMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(13, 13, 13)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnNew)
                            .addComponent(btnDelete)
                            .addComponent(btnEdit)
                            .addComponent(btnReset)
                            .addComponent(btnBack)
                            .addComponent(btnFirst)
                            .addComponent(btnLast)
                            .addComponent(btnNext)))))
        );

        jTabbedPane1.addTab("THÔNG TIN", jPanel1);

        tblThongKeKhachHang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "MÃ KH", "HỌ TÊN", "NGÀY SINH", "GIỚI TÍNH", "SỐ CCCD", "ĐỊA CHỈ", "SĐT", "EMAIL", "MẬT KHẨU", "MÃ NV", "NGÀY TẠO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblThongKeKhachHang);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 591, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1)
                    .addContainerGap()))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 387, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("DANH SÁCH", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSoCCCDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSoCCCDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSoCCCDActionPerformed

    private void txtMaKHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaKHActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaKHActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        // TODO add your handling code here:
        String maKH = txtMaKH.getText().trim();
    String hoTen = txtHoTen.getText().trim();
    String ngaySinh = txtNgaySinh.getText().trim();
    Boolean gioiTinh = rdoNam.isSelected() ? true : rdoNu.isSelected() ? false : null;
    String soCCCD = txtSoCCCD.getText().trim();
    String diaChi = txtDiaChi.getText().trim();
    String soDienThoai = txtSDT.getText().trim();
    String email = txtEmail.getText().trim();
    String matKhau = txtMatKhau.getText().trim();
    String xacNhanMatKhau = txtXacNhanMatKhau.getText().trim();

    // Kiểm tra các trường bắt buộc
    if (maKH.isEmpty() || hoTen.isEmpty() || ngaySinh.isEmpty() || gioiTinh == null || soCCCD.isEmpty() || matKhau.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin bắt buộc!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Kiểm tra mật khẩu khớp
    if (!matKhau.equals(xacNhanMatKhau)) {
        JOptionPane.showMessageDialog(this, "Mật khẩu không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Kiểm tra định dạng ngày sinh
    String ngaySinhSQL;
    try {
        sdfInput.setLenient(false);
        java.util.Date date = sdfInput.parse(ngaySinh);
        ngaySinhSQL = sdfOutput.format(date);
    } catch (ParseException e) {
        JOptionPane.showMessageDialog(this, "Ngày sinh không đúng định dạng (dd/MM/yyyy)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        // Kiểm tra trùng lặp MaKH hoặc SoCCCD
        String checkSql = "SELECT COUNT(*) FROM KHACH_HANG WHERE MaKhachHang = ? OR SoCCCD = ?";
        ResultSet rs = XJdbc.query(checkSql, maKH, soCCCD);
        rs.next();
        if (rs.getInt(1) > 0) {
            JOptionPane.showMessageDialog(this, "Mã khách hàng hoặc số CCCD đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            rs.close();
            return;
        }
        rs.close();

        // Thêm khách hàng với NgayTao là thời gian hiện tại
        String sql = "INSERT INTO KHACH_HANG (MaKhachHang, HoTen, NgaySinh, GioiTinh, SoCCCD, DiaChi, SoDienThoai, Email, MatKhau, MaNhanVien, NgayTao) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String maNV = Auth.isEmployee() ? Auth.userNhanVien.getMaNhanVien() : "NV001";
        Date ngayTao = new Date(); // Lấy thời gian hiện tại
        XJdbc.update(sql, maKH, hoTen, ngaySinhSQL, gioiTinh, soCCCD, diaChi, soDienThoai, email, matKhau, maNV, ngayTao);

        JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        clearForm();
        loadDataToTable();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi thêm khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        int selectedRow = tblThongKeKhachHang.getSelectedRow();
    if (selectedRow < 0) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String maKH = txtMaKH.getText().trim();
    String hoTen = txtHoTen.getText().trim();
    String ngaySinh = txtNgaySinh.getText().trim();
    Boolean gioiTinh = rdoNam.isSelected() ? true : rdoNu.isSelected() ? false : null;
    String soCCCD = txtSoCCCD.getText().trim();
    String diaChi = txtDiaChi.getText().trim();
    String soDienThoai = txtSDT.getText().trim();
    String email = txtEmail.getText().trim();
    String matKhau = txtMatKhau.getText().trim();
    String xacNhanMatKhau = txtXacNhanMatKhau.getText().trim();

    // Kiểm tra các trường bắt buộc
    if (maKH.isEmpty() || hoTen.isEmpty() || ngaySinh.isEmpty() || gioiTinh == null || soCCCD.isEmpty() || matKhau.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin bắt buộc!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Kiểm tra mật khẩu khớp
    if (!matKhau.equals(xacNhanMatKhau)) {
        JOptionPane.showMessageDialog(this, "Mật khẩu không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (Auth.isCustomer() && !maKH.equals(Auth.userKhachHang.getMaKhachHang())) {
        JOptionPane.showMessageDialog(this, "Bạn chỉ có thể sửa thông tin của mình!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Kiểm tra định dạng ngày sinh
    String ngaySinhSQL;
    try {
        sdfInput.setLenient(false);
        java.util.Date date = sdfInput.parse(ngaySinh);
        ngaySinhSQL = sdfOutput.format(date);
    } catch (ParseException e) {
        JOptionPane.showMessageDialog(this, "Ngày sinh không đúng định dạng (dd/MM/yyyy)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        // Kiểm tra trùng lặp SoCCCD (trừ chính khách hàng đang sửa)
        String checkSql = "SELECT COUNT(*) FROM KHACH_HANG WHERE SoCCCD = ? AND MaKhachHang != ?";
        ResultSet rs = XJdbc.query(checkSql, soCCCD, maKH);
        rs.next();
        if (rs.getInt(1) > 0) {
            JOptionPane.showMessageDialog(this, "Số CCCD đã tồn tại cho khách hàng khác!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            rs.close();
            return;
        }
        rs.close();

        // Cập nhật khách hàng (NgayTao giữ nguyên, không thay đổi)
        String sql = "UPDATE KHACH_HANG SET HoTen = ?, NgaySinh = ?, GioiTinh = ?, SoCCCD = ?, DiaChi = ?, SoDienThoai = ?, Email = ?, MatKhau = ? " +
                     "WHERE MaKhachHang = ?";
        XJdbc.update(sql, hoTen, ngaySinhSQL, gioiTinh, soCCCD, diaChi, soDienThoai, email, matKhau, maKH);

        JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        loadDataToTable();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
        String maKH = txtMaKH.getText().trim();
    String hoTen = txtHoTen.getText().trim();
    String ngaySinh = txtNgaySinh.getText().trim();
    Boolean gioiTinh = rdoNam.isSelected() ? true : rdoNu.isSelected() ? false : null;
    String soCCCD = txtSoCCCD.getText().trim();
    String diaChi = txtDiaChi.getText().trim();
    String soDienThoai = txtSDT.getText().trim();
    String email = txtEmail.getText().trim();
    String matKhau = txtMatKhau.getText().trim();
    String xacNhanMatKhau = txtXacNhanMatKhau.getText().trim();

    // Kiểm tra các trường bắt buộc
    if (hoTen.isEmpty() || soDienThoai.isEmpty() || email.isEmpty() || matKhau.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin bắt buộc!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Kiểm tra mật khẩu khớp
    if (!matKhau.equals(xacNhanMatKhau)) {
        JOptionPane.showMessageDialog(this, "Mật khẩu không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Kiểm tra độ dài Số điện thoại (phải đủ 10 số)
    if (soDienThoai.length() != 10 || !soDienThoai.matches("\\d{10}")) {
        JOptionPane.showMessageDialog(this, "Số điện thoại phải là 10 chữ số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (Auth.isCustomer() && !maKH.equals(Auth.userKhachHang.getMaKhachHang())) {
        JOptionPane.showMessageDialog(this, "Bạn chỉ có thể sửa thông tin của mình!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        // Kiểm tra trùng lặp SoDienThoai (trừ chính khách hàng đang sửa)
        String checkSql = "SELECT COUNT(*) FROM KHACH_HANG WHERE SoDienThoai = ? AND MaKhachHang != ?";
        ResultSet rs = XJdbc.query(checkSql, soDienThoai, maKH);
        rs.next();
        if (rs.getInt(1) > 0) {
            JOptionPane.showMessageDialog(this, "Số điện thoại đã tồn tại cho khách hàng khác!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            rs.close();
            return;
        }
        rs.close();

        // Cập nhật thông tin khách hàng
        String sql = "UPDATE KHACH_HANG SET HoTen = ?, DiaChi = ?, SoDienThoai = ?, Email = ?, MatKhau = ? WHERE MaKhachHang = ?";
        XJdbc.update(sql, hoTen, diaChi, soDienThoai, email, matKhau, maKH);

        // Cập nhật Auth.userKhachHang nếu là khách hàng đang đăng nhập
        if (Auth.isCustomer()) {
            Auth.userKhachHang.setHoTen(hoTen);
            Auth.userKhachHang.setDiaChi(diaChi);
            Auth.userKhachHang.setSoDienThoai(soDienThoai);
            Auth.userKhachHang.setEmail(email);
            Auth.userKhachHang.setMatKhau(matKhau);
        }

        JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        loadDataToTable();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        // TODO add your handling code here:
        clearForm();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        if (tblThongKeKhachHang.getRowCount() > 0) {
            currentRow = 0;
            tblThongKeKhachHang.setRowSelectionInterval(currentRow, currentRow);
            fillFormFromSelectedRow();
        }
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        // TODO add your handling code here:
        int rowCount = tblThongKeKhachHang.getRowCount();
        if (rowCount > 0) {
            currentRow = rowCount - 1;
            tblThongKeKhachHang.setRowSelectionInterval(currentRow, currentRow);
            fillFormFromSelectedRow();
        }
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        // TODO add your handling code here:
        int rowCount = tblThongKeKhachHang.getRowCount();
        if (currentRow < rowCount - 1 && currentRow >= 0) {
            currentRow++;
            tblThongKeKhachHang.setRowSelectionInterval(currentRow, currentRow);
            fillFormFromSelectedRow();
        }
    }//GEN-LAST:event_btnLastActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
        if (currentRow > 0) {
            currentRow--;
            tblThongKeKhachHang.setRowSelectionInterval(currentRow, currentRow);
            fillFormFromSelectedRow();
        }
    }//GEN-LAST:event_btnNextActionPerformed

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
            java.util.logging.Logger.getLogger(KhachHangJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(KhachHangJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(KhachHangJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(KhachHangJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                KhachHangJDialog dialog = new KhachHangJDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnReset;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblCCCD;
    private javax.swing.JLabel lblHinh;
    private javax.swing.JLabel lblHoTen;
    private javax.swing.JRadioButton rdoNam;
    private javax.swing.JRadioButton rdoNu;
    private javax.swing.JTable tblThongKeKhachHang;
    private javax.swing.JTextField txtDiaChi;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtHoTen;
    private javax.swing.JTextField txtMaKH;
    private javax.swing.JTextField txtMatKhau;
    private javax.swing.JTextField txtNgaySinh;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtSoCCCD;
    private javax.swing.JTextField txtXacNhanMatKhau;
    // End of variables declaration//GEN-END:variables

    private void fillFormFromSelectedRow() {
        int selectedRow = tblThongKeKhachHang.getSelectedRow();
        if (selectedRow >= 0) {
            txtMaKH.setText(tblThongKeKhachHang.getValueAt(selectedRow, 0).toString());
            txtHoTen.setText(tblThongKeKhachHang.getValueAt(selectedRow, 1).toString());
            txtNgaySinh.setText(tblThongKeKhachHang.getValueAt(selectedRow, 2).toString());
            String gioiTinh = tblThongKeKhachHang.getValueAt(selectedRow, 3).toString();
            if (gioiTinh.equals("Nam")) rdoNam.setSelected(true);
            else if (gioiTinh.equals("Nữ")) rdoNu.setSelected(true);
            txtSoCCCD.setText(tblThongKeKhachHang.getValueAt(selectedRow, 4).toString());
            txtDiaChi.setText(tblThongKeKhachHang.getValueAt(selectedRow, 5).toString());
            txtSDT.setText(tblThongKeKhachHang.getValueAt(selectedRow, 6).toString());
            txtEmail.setText(tblThongKeKhachHang.getValueAt(selectedRow, 7).toString());
            txtMatKhau.setText(tblThongKeKhachHang.getValueAt(selectedRow, 8).toString());
            txtXacNhanMatKhau.setText(tblThongKeKhachHang.getValueAt(selectedRow, 8).toString());
        }
    }

    private void clearForm() {
        try {
            txtMaKH.setText(generateMaKH());
        } catch (SQLException e) {
            txtMaKH.setText("KH001");
        }
        txtHoTen.setText("");
        txtNgaySinh.setText("");
        buttonGroup1.clearSelection();
        txtSoCCCD.setText("");
        txtDiaChi.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        txtMatKhau.setText("");
        txtXacNhanMatKhau.setText("");
        tblThongKeKhachHang.clearSelection();
        currentRow = -1;
    }
    // DocumentFilter để giới hạn độ dài và chỉ cho phép nhập số
private class NumberLengthFilter extends DocumentFilter {
    private final int maxLength;

    public NumberLengthFilter(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string == null) return;

        // Chỉ cho phép nhập số
        if (!string.matches("\\d*")) return;

        // Kiểm tra độ dài sau khi thêm chuỗi mới
        int currentLength = fb.getDocument().getLength();
        int newLength = currentLength + string.length();
        if (newLength <= maxLength) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text == null) return;

        // Chỉ cho phép nhập số
        if (!text.matches("\\d*")) return;

        // Kiểm tra độ dài sau khi thay thế
        int currentLength = fb.getDocument().getLength();
        int newLength = currentLength - length + text.length();
        if (newLength <= maxLength) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}
}
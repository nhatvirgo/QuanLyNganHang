/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.edusys.ui;

import com.edusys.enity.TaiKhoan;
import com.edusys.utils.Auth;
import com.edusys.utils.XJdbc;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class TaiKhoanJDialog extends javax.swing.JDialog {

    private DefaultTableModel tableModel;
    private String maKhachHang;
    private int currentRow = -1;

    public TaiKhoanJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
        tblTaiKhoan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTaiKhoanMouseClicked(evt);
            }
        });
    }

    private void init() {
        this.setLocationRelativeTo(null);
        tableModel = (DefaultTableModel) tblTaiKhoan.getModel();
        tableModel.setRowCount(0);

        if (Auth.isLogin()) {
            if (Auth.isCustomer()) {
                btnDelete.setVisible(false);
                btnEdit.setVisible(false);
                btnNew.setVisible(true);
                btnBack.setVisible(false);
                btnFirst.setVisible(false);
                btnLast.setVisible(false);
                btnNext.setVisible(false);
                cboTrangThai.setVisible(false);
                jLabel6.setVisible(false);
                txtSoDu.setVisible(false);
                jLabel4.setVisible(false);
                txtMaKhachHang.setText(Auth.userKhachHang.getMaKhachHang());
                txtMaKhachHang.setEditable(false);
                txtSoTaiKhoan.setEditable(false);
                txtNgayMo.setText(new Date().toString());
                txtNgayMo.setEditable(false);
                txtHoTen.setText(Auth.userKhachHang.getHoTen());
                txtSdt.setText(Auth.userKhachHang.getSoDienThoai());
                txtHoTen.setEditable(false);
                txtSdt.setEditable(false);
            } else if (Auth.isEmployee()) {
                btnDelete.setVisible(true);
                btnEdit.setVisible(true);
                btnNew.setVisible(true);
                btnBack.setVisible(true);
                btnFirst.setVisible(true);
                btnLast.setVisible(true);
                btnNext.setVisible(true);
                cboTrangThai.setVisible(true);
                jLabel6.setVisible(true);
                txtSoDu.setVisible(true);
                jLabel4.setVisible(true);
                txtHoTen.setEditable(false); // Khóa trường họ tên
                txtSdt.setEditable(false);   // Khóa trường số điện thoại
            }
        }

        // Thêm sự kiện DocumentListener cho txtMaKhachHang (chỉ áp dụng cho nhân viên)
        if (Auth.isEmployee()) {
            txtMaKhachHang.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateKhachHangInfo();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateKhachHangInfo();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateKhachHangInfo();
                }
            });
        }

        fillTable();
    }

    private void updateKhachHangInfo() {
        String maKhachHang = txtMaKhachHang.getText().trim();
        if (maKhachHang.isEmpty()) {
            txtHoTen.setText("");
            txtSdt.setText("");
            return;
        }

        try {
            String sql = "SELECT HoTen, SoDienThoai FROM KHACH_HANG WHERE MaKhachHang = ?";
            ResultSet rs = XJdbc.query(sql, maKhachHang);
            if (rs.next()) {
                txtHoTen.setText(rs.getString("HoTen"));
                txtSdt.setText(rs.getString("SoDienThoai"));
            } else {
                txtHoTen.setText("");
                txtSdt.setText("");
            }
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tra cứu thông tin khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtHoTen.setText("");
            txtSdt.setText("");
        }
    }

    private void tblTaiKhoanMouseClicked(java.awt.event.MouseEvent evt) {
        fillFormFromSelectedRow();
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
        txtMaKhachHang.setText(maKhachHang);
        txtMaKhachHang.setEditable(false);
    }

    private void fillTable() {
        try {
            String sql = "SELECT tk.MaTaiKhoan, tk.SoTaiKhoan, tk.LoaiTaiKhoan, tk.SoDu, tk.TrangThai, tk.NgayMo, tk.MaKhachHang, kh.HoTen, kh.SoDienThoai " +
                         "FROM TAI_KHOAN tk " +
                         "LEFT JOIN KHACH_HANG kh ON tk.MaKhachHang = kh.MaKhachHang";
            if (Auth.isCustomer()) {
                sql += " WHERE tk.MaKhachHang = ?";
            }

            ResultSet rs = Auth.isCustomer() 
                ? XJdbc.query(sql, Auth.userKhachHang.getMaKhachHang())
                : XJdbc.query(sql);

            try {
                tableModel.setRowCount(0);
                while (rs.next()) {
                    Object[] row = {
                        rs.getString("MaTaiKhoan"),
                        rs.getString("SoTaiKhoan"),
                        rs.getString("LoaiTaiKhoan"),
                        rs.getDouble("SoDu"),
                        rs.getString("TrangThai"),
                        rs.getDate("NgayMo") != null ? rs.getDate("NgayMo").toString() : "",
                        rs.getString("MaKhachHang"),
                        rs.getString("HoTen"),
                        rs.getString("SoDienThoai")
                    };
                    tableModel.addRow(row);
                }
            } finally {
                rs.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách tài khoản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private TaiKhoan getForm() {
        TaiKhoan tk = new TaiKhoan();
        tk.setMaTaiKhoan(txtMaTaiKhoan.getText().trim());
        tk.setMaKhachHang(txtMaKhachHang.getText().trim());
        if (Auth.isEmployee()) {
            try {
                tk.setSoDu(Double.parseDouble(txtSoDu.getText().trim()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Số dư không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            tk.setTrangThai(cboTrangThai.getSelectedItem().toString());
        } else {
            tk.setSoDu(0.0);
            tk.setTrangThai("Hoạt động");
        }
        tk.setLoaiTaiKhoan(cboLoaiTK.getSelectedItem().toString());
        tk.setSoTaiKhoan(txtSoTaiKhoan.getText().trim());
        tk.setNgayMo(new Date());
        return tk;
    }

    private void setForm(TaiKhoan tk) {
        txtMaTaiKhoan.setText(tk.getMaTaiKhoan());
        txtMaKhachHang.setText(tk.getMaKhachHang());
        txtSoDu.setText(String.valueOf(tk.getSoDu()));
        cboTrangThai.setSelectedItem(tk.getTrangThai());
        cboLoaiTK.setSelectedItem(tk.getLoaiTaiKhoan());
        txtSoTaiKhoan.setText(tk.getSoTaiKhoan());
        txtNgayMo.setText(tk.getNgayMo() != null ? tk.getNgayMo().toString() : "");
    }

    private void clearForm() {
        try {
            txtMaTaiKhoan.setText(generateMaTaiKhoan());
            txtSoTaiKhoan.setText(generateUniqueSoTaiKhoan());
        } catch (SQLException e) {
            txtMaTaiKhoan.setText("TK001");
            txtSoTaiKhoan.setText("STK0001");
        }
        txtMaKhachHang.setText("");
        txtSoDu.setText("");
        txtNgayMo.setText(new Date().toString());
        txtHoTen.setText("");
        txtSdt.setText("");
        cboTrangThai.setSelectedItem("Hoạt động");
        cboLoaiTK.setSelectedIndex(0);
        if (Auth.isCustomer()) {
            txtMaKhachHang.setText(Auth.userKhachHang.getMaKhachHang());
            txtMaKhachHang.setEditable(false);
            txtSoTaiKhoan.setEditable(false);
            txtNgayMo.setEditable(false);
            txtHoTen.setText(Auth.userKhachHang.getHoTen());
            txtSdt.setText(Auth.userKhachHang.getSoDienThoai());
            txtHoTen.setEditable(false);
            txtSdt.setEditable(false);
        }
    }

    private String generateMaTaiKhoan() throws SQLException {
        String sql = "SELECT MAX(MaTaiKhoan) FROM TAI_KHOAN";
        ResultSet rs = XJdbc.query(sql);
        String maxMaTaiKhoan = null;
        if (rs.next()) {
            maxMaTaiKhoan = rs.getString(1);
        }
        rs.close();
        if (maxMaTaiKhoan == null) {
            return "TK001";
        }
        int number = Integer.parseInt(maxMaTaiKhoan.substring(2)) + 1;
        return String.format("TK%03d", number);
    }

    private String generateUniqueSoTaiKhoan() throws SQLException {
        String sql = "SELECT MAX(SoTaiKhoan) FROM TAI_KHOAN WHERE SoTaiKhoan LIKE 'STK[0-9][0-9][0-9][0-9]'";
        ResultSet rs = XJdbc.query(sql);
        String maxSoTaiKhoan = null;
        if (rs.next()) {
            maxSoTaiKhoan = rs.getString(1);
        }
        rs.close();
        if (maxSoTaiKhoan == null) {
            return "STK0001";
        }
        int number = Integer.parseInt(maxSoTaiKhoan.substring(3)) + 1;
        return String.format("STK%04d", number);
    }

    private String generateUniqueMaThe() throws SQLException {
        String sql = "SELECT MAX(MaThe) FROM THE_NGAN_HANG WHERE MaThe LIKE 'THE[0-9][0-9][0-9]'";
        ResultSet rs = XJdbc.query(sql);
        int maxNumber = 0;
        if (rs.next()) {
            String maxMaThe = rs.getString(1);
            if (maxMaThe != null) {
                maxNumber = Integer.parseInt(maxMaThe.substring(3));
            }
        }
        rs.close();
        return String.format("THE%03d", maxNumber + 1);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        tabs = new javax.swing.JTabbedPane();
        pnlThongTin = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtMaTaiKhoan = new javax.swing.JTextField();
        txtNgayMo = new javax.swing.JTextField();
        txtSoTaiKhoan = new javax.swing.JTextField();
        txtSoDu = new javax.swing.JTextField();
        txtMaKhachHang = new javax.swing.JTextField();
        btnNew = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        btnFirst = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        cboTrangThai = new javax.swing.JComboBox<>();
        cboLoaiTK = new javax.swing.JComboBox<>();
        lblHoTen = new javax.swing.JLabel();
        txtHoTen = new javax.swing.JTextField();
        lblSdt = new javax.swing.JLabel();
        txtSdt = new javax.swing.JTextField();
        pnlDanhSach = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTaiKhoan = new javax.swing.JTable();

        jScrollPane1.setViewportView(jEditorPane1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản Lý Tài Khoản");

        jLabel1.setText("Mã tài khoản:");

        jLabel2.setText("Số tài khoản:");

        jLabel3.setText("Loại tài khoản:");

        jLabel4.setText("Số dư:");

        jLabel5.setText("Ngày mở:");

        jLabel6.setText("Trạng thái:");

        jLabel7.setText("Mã khách hàng:");

        txtSoDu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSoDuActionPerformed(evt);
            }
        });

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

        cboTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hoạt động", "Đóng" }));

        cboLoaiTK.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Thanh toán", "Tiết kiệm" }));

        lblHoTen.setText("Họ và tên");

        txtHoTen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHoTenActionPerformed(evt);
            }
        });

        lblSdt.setText("Số điện thoại");

        javax.swing.GroupLayout pnlThongTinLayout = new javax.swing.GroupLayout(pnlThongTin);
        pnlThongTin.setLayout(pnlThongTinLayout);
        pnlThongTinLayout.setHorizontalGroup(
            pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThongTinLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlThongTinLayout.createSequentialGroup()
                        .addGroup(pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel7)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboLoaiTK, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(lblHoTen, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(lblSdt, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlThongTinLayout.createSequentialGroup()
                        .addGroup(pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtSdt, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtHoTen, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtMaKhachHang, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNgayMo, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSoDu, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSoTaiKhoan, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtMaTaiKhoan, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlThongTinLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLast, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlThongTinLayout.setVerticalGroup(
            pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThongTinLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaTaiKhoan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSoTaiKhoan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboLoaiTK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSoDu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addGap(4, 4, 4)
                .addComponent(txtNgayMo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblHoTen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSdt)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSdt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNew)
                    .addComponent(btnDelete)
                    .addComponent(btnEdit)
                    .addComponent(btnReset)
                    .addComponent(btnBack)
                    .addComponent(btnFirst)
                    .addComponent(btnLast)
                    .addComponent(btnNext))
                .addGap(17, 17, 17))
        );

        tabs.addTab("THÔNG TIN", pnlThongTin);

        tblTaiKhoan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "MÃ TK", "SỐ TK", "LOẠI TK", "SỐ DƯ", "TRẠNG THÁI", "NGÀY MỞ", "MÃ KH", "Họ và tên", "Số điện thoại"
            }
        ));
        jScrollPane2.setViewportView(tblTaiKhoan);

        javax.swing.GroupLayout pnlDanhSachLayout = new javax.swing.GroupLayout(pnlDanhSach);
        pnlDanhSach.setLayout(pnlDanhSachLayout);
        pnlDanhSachLayout.setHorizontalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDanhSachLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlDanhSachLayout.setVerticalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDanhSachLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(112, Short.MAX_VALUE))
        );

        tabs.addTab("DANH SÁCH", pnlDanhSach);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabs)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabs)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSoDuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSoDuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSoDuActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        // TODO add your handling code here:                                 
   try {
            TaiKhoan tk = getForm();
            if (tk == null) return;

            String maTaiKhoan = generateMaTaiKhoan();
            tk.setMaTaiKhoan(maTaiKhoan);
            txtMaTaiKhoan.setText(maTaiKhoan);

            String soTaiKhoan = generateUniqueSoTaiKhoan();
            tk.setSoTaiKhoan(soTaiKhoan);
            txtSoTaiKhoan.setText(soTaiKhoan);

            String checkKHSql = "SELECT HoTen, SoDienThoai FROM KHACH_HANG WHERE MaKhachHang = ?";
            ResultSet rsKH = XJdbc.query(checkKHSql, tk.getMaKhachHang());
            if (rsKH.next()) {
                txtHoTen.setText(rsKH.getString("HoTen"));
                txtSdt.setText(rsKH.getString("SoDienThoai"));
            } else {
                JOptionPane.showMessageDialog(this, "Mã khách hàng không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                rsKH.close();
                return;
            }
            rsKH.close();

            String sql = "INSERT INTO dbo.TAI_KHOAN (MaTaiKhoan, SoTaiKhoan, LoaiTaiKhoan, SoDu, TrangThai, NgayMo, MaKhachHang) VALUES (?, ?, ?, ?, ?, ?, ?)";
            XJdbc.update(sql, tk.getMaTaiKhoan(), tk.getSoTaiKhoan(), tk.getLoaiTaiKhoan(), tk.getSoDu(), tk.getTrangThai(), tk.getNgayMo(), tk.getMaKhachHang());

            String maThe = generateUniqueMaThe();
            String soThe = XJdbc.generateUniqueSoThe();
            Date currentDate = new Date();
            Date expiryDate = new Date(currentDate.getTime() + 5L * 365 * 24 * 60 * 60 * 1000);
            String insertTheSql = "INSERT INTO THE_NGAN_HANG (MaThe, SoThe, MaTaiKhoan, NgayPhatHanh, NgayHetHan) VALUES (?, ?, ?, ?, ?)";
            XJdbc.update(insertTheSql, maThe, soThe, tk.getMaTaiKhoan(), currentDate, expiryDate);

            JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công! Số thẻ: " + soThe, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            fillTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
       if (Auth.isCustomer()) {
        JOptionPane.showMessageDialog(this, "Bạn không có quyền xóa tài khoản!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try {
        TaiKhoan tk = getForm();
        if (tk == null) return;

        // Kiểm tra mã tài khoản có tồn tại không
        String checkTKSql = "SELECT TrangThai FROM TAI_KHOAN WHERE MaTaiKhoan = ?";
        ResultSet rsTK = XJdbc.query(checkTKSql, tk.getMaTaiKhoan());
        if (!rsTK.next()) {
            JOptionPane.showMessageDialog(this, "Mã tài khoản không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            rsTK.close();
            return;
        }

        // Kiểm tra xem tài khoản có đang tham gia giao dịch hay không
        String checkGiaoDichSql = "SELECT COUNT(*) FROM CHI_TIET_GIAO_DICH WHERE TaiKhoanNguoiGui = ? OR TaiKhoanNguoiNhan = ?";
        ResultSet rsGiaoDich = XJdbc.query(checkGiaoDichSql, tk.getMaTaiKhoan(), tk.getMaTaiKhoan());
        rsGiaoDich.next();
        int giaoDichCount = rsGiaoDich.getInt(1);
        rsGiaoDich.close();

        if (giaoDichCount > 0) {
            JOptionPane.showMessageDialog(this, "Tài khoản này đang thực hiện giao dịch nên không thể xóa được!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Xác nhận trước khi xóa
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa tài khoản " + tk.getMaTaiKhoan() + " không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Xóa các giao dịch trong GIAO_DICH
        String deleteGiaoDichSql = "DELETE FROM GIAO_DICH WHERE MaTaiKhoan = ?";
        XJdbc.update(deleteGiaoDichSql, tk.getMaTaiKhoan());

        // Xóa thông tin thẻ ngân hàng trong THE_NGAN_HANG
        String deleteTheSql = "DELETE FROM THE_NGAN_HANG WHERE MaTaiKhoan = ?";
        XJdbc.update(deleteTheSql, tk.getMaTaiKhoan());

        // Xóa tài khoản
        String deleteTaiKhoanSql = "DELETE FROM TAI_KHOAN WHERE MaTaiKhoan = ?";
        XJdbc.update(deleteTaiKhoanSql, tk.getMaTaiKhoan());

        JOptionPane.showMessageDialog(this, "Xóa tài khoản thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        clearForm();
        fillTable();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi xóa tài khoản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
        if (Auth.isCustomer()) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền sửa tài khoản!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            TaiKhoan tk = getForm();
            if (tk == null) return;

            String checkTKSql = "SELECT COUNT(*) FROM TAI_KHOAN WHERE MaTaiKhoan = ?";
            ResultSet rsTK = XJdbc.query(checkTKSql, tk.getMaTaiKhoan());
            rsTK.next();
            if (rsTK.getInt(1) == 0) {
                JOptionPane.showMessageDialog(this, "Mã tài khoản không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                rsTK.close();
                return;
            }
            rsTK.close();

            String checkKHSql = "SELECT COUNT(*) FROM KHACH_HANG WHERE MaKhachHang = ?";
            ResultSet rsKH = XJdbc.query(checkKHSql, tk.getMaKhachHang());
            rsKH.next();
            if (rsKH.getInt(1) == 0) {
                JOptionPane.showMessageDialog(this, "Mã khách hàng không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                rsKH.close();
                return;
            }
            rsKH.close();

            String sql = "UPDATE TAI_KHOAN SET SoTaiKhoan = ?, LoaiTaiKhoan = ?, SoDu = ?, TrangThai = ?, MaKhachHang = ? WHERE MaTaiKhoan = ?";
            XJdbc.update(sql, tk.getSoTaiKhoan(), tk.getLoaiTaiKhoan(), tk.getSoDu(), tk.getTrangThai(), tk.getMaKhachHang(), tk.getMaTaiKhoan());
            JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            fillTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật tài khoản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        // TODO add your handling code here:
        clearForm();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        if (currentRow > 0) {
            currentRow--;
            tblTaiKhoan.setRowSelectionInterval(currentRow, currentRow);
            fillFormFromSelectedRow();
        } 
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        // TODO add your handling code here:
        if (tblTaiKhoan.getRowCount() > 0) {
            currentRow = 0;
            tblTaiKhoan.setRowSelectionInterval(currentRow, currentRow);
            fillFormFromSelectedRow();
        }
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        // TODO add your handling code here:
        int rowCount = tblTaiKhoan.getRowCount();
        if (rowCount > 0) {
            currentRow = rowCount - 1;
            tblTaiKhoan.setRowSelectionInterval(currentRow, currentRow);
            fillFormFromSelectedRow();
        }
    }//GEN-LAST:event_btnLastActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
        int rowCount = tblTaiKhoan.getRowCount();
        if (currentRow < rowCount - 1 && currentRow >= 0) {
            currentRow++;
            tblTaiKhoan.setRowSelectionInterval(currentRow, currentRow);
            fillFormFromSelectedRow();
        }
    }//GEN-LAST:event_btnNextActionPerformed

    private void txtHoTenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHoTenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHoTenActionPerformed
    
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
            java.util.logging.Logger.getLogger(TaiKhoanJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TaiKhoanJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TaiKhoanJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TaiKhoanJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                TaiKhoanJDialog dialog = new TaiKhoanJDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox<String> cboLoaiTK;
    private javax.swing.JComboBox<String> cboTrangThai;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblHoTen;
    private javax.swing.JLabel lblSdt;
    private javax.swing.JPanel pnlDanhSach;
    private javax.swing.JPanel pnlThongTin;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblTaiKhoan;
    private javax.swing.JTextField txtHoTen;
    private javax.swing.JTextField txtMaKhachHang;
    private javax.swing.JTextField txtMaTaiKhoan;
    private javax.swing.JTextField txtNgayMo;
    private javax.swing.JTextField txtSdt;
    private javax.swing.JTextField txtSoDu;
    private javax.swing.JTextField txtSoTaiKhoan;
    // End of variables declaration//GEN-END:variables
    
private void fillFormFromSelectedRow() {
        int selectedRow = tblTaiKhoan.getSelectedRow();
        if (selectedRow >= 0) {
            currentRow = selectedRow;
            txtMaTaiKhoan.setText(tblTaiKhoan.getValueAt(selectedRow, 0) != null ? tblTaiKhoan.getValueAt(selectedRow, 0).toString() : "");
            txtSoTaiKhoan.setText(tblTaiKhoan.getValueAt(selectedRow, 1) != null ? tblTaiKhoan.getValueAt(selectedRow, 1).toString() : "");
            cboLoaiTK.setSelectedItem(tblTaiKhoan.getValueAt(selectedRow, 2) != null ? tblTaiKhoan.getValueAt(selectedRow, 2).toString() : "Thanh toán");
            txtSoDu.setText(tblTaiKhoan.getValueAt(selectedRow, 3) != null ? tblTaiKhoan.getValueAt(selectedRow, 3).toString() : "0");
            cboTrangThai.setSelectedItem(tblTaiKhoan.getValueAt(selectedRow, 4) != null ? tblTaiKhoan.getValueAt(selectedRow, 4).toString() : "Hoạt động");
            txtNgayMo.setText(tblTaiKhoan.getValueAt(selectedRow, 5) != null ? tblTaiKhoan.getValueAt(selectedRow, 5).toString() : "");
            txtMaKhachHang.setText(tblTaiKhoan.getValueAt(selectedRow, 6) != null ? tblTaiKhoan.getValueAt(selectedRow, 6).toString() : "");
            txtHoTen.setText(tblTaiKhoan.getValueAt(selectedRow, 7) != null ? tblTaiKhoan.getValueAt(selectedRow, 7).toString() : "");
            txtSdt.setText(tblTaiKhoan.getValueAt(selectedRow, 8) != null ? tblTaiKhoan.getValueAt(selectedRow, 8).toString() : "");
        }
    }
}

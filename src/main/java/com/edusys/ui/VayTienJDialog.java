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

/**
 *
 * @author nguye
 */
public class VayTienJDialog extends javax.swing.JDialog {

    private DefaultTableModel tblVayTienModel;
    private DefaultTableModel tblTraGopModel;

    public VayTienJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
        loadKyHanOptions();
        loadVayTienData();
        loadTraGopData();
        setMaKhachHangToForm();
        setLatestMaVayToForm();
        setLatestMaTraToForm();
        updateLaiSuat(); // Cập nhật lãi suất khi khởi tạo
        cboKyHan.addActionListener(evt -> updateLaiSuat()); // Cập nhật lãi suất khi thay đổi kỳ hạn
        tblVayTien.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        tblVayTienMouseClicked(evt);
    }
});

jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        jTable1MouseClicked(evt);
    }
});
    }
    
    private void tblVayTienMouseClicked(java.awt.event.MouseEvent evt) {
    int selectedRow = tblVayTien.getSelectedRow();
    if (selectedRow >= 0) {
        txtMaVay.setText(tblVayTien.getValueAt(selectedRow, 0).toString());
        txtMaVay2.setText(tblVayTien.getValueAt(selectedRow, 0).toString()); // Đồng bộ mã vay ở tab trả góp
        txtMaKH.setText(tblVayTien.getValueAt(selectedRow, 1).toString());
        txtSotien.setText(tblVayTien.getValueAt(selectedRow, 2).toString());
        txtLaiSuat.setText(tblVayTien.getValueAt(selectedRow, 3).toString());
        cboKyHan.setSelectedItem(tblVayTien.getValueAt(selectedRow, 4).toString());
    }
}

private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
    int selectedRow = jTable1.getSelectedRow();
    if (selectedRow >= 0) {
        txtMaTra.setText(jTable1.getValueAt(selectedRow, 0).toString());
        txtMaVay2.setText(jTable1.getValueAt(selectedRow, 1).toString());
        txtSoTienTra.setText(jTable1.getValueAt(selectedRow, 2).toString());
    }
}

    private void init() {
        this.setLocationRelativeTo(null);
        tblVayTienModel = (DefaultTableModel) tblVayTien.getModel();
        tblTraGopModel = (DefaultTableModel) jTable1.getModel();
        tblVayTienModel.setRowCount(0);
        tblTraGopModel.setRowCount(0);
    }

    private void setMaKhachHangToForm() {
        if (Auth.isCustomer()) {
            txtMaKH.setText(Auth.userKhachHang.getMaKhachHang());
        } else if (Auth.isEmployee()) {
            txtMaKH.setText("Nhân viên");
        }
    }

    private void setLatestMaVayToForm() {
        if (Auth.isCustomer()) {
            try {
                String sql = "SELECT TOP 1 MaVay FROM VAY_TIEN WHERE MaKhachHang = ? ORDER BY NgayVay DESC";
                ResultSet rs = XJdbc.query(sql, Auth.userKhachHang.getMaKhachHang());
                if (rs.next()) {
                    txtMaVay.setText(rs.getString("MaVay"));
                    txtMaVay2.setText(rs.getString("MaVay"));
                } else {
                    txtMaVay.setText("Chưa có khoản vay");
                    txtMaVay2.setText("Chưa có khoản vay");
                }
                rs.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lấy mã vay: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setLatestMaTraToForm() {
        if (Auth.isCustomer()) {
            try {
                String sql = "SELECT TOP 1 tg.MaTraGop " +
                             "FROM TRA_GOP tg " +
                             "JOIN VAY_TIEN vt ON tg.MaVay = vt.MaVay " +
                             "WHERE vt.MaKhachHang = ? " +
                             "ORDER BY tg.NgayTra DESC";
                ResultSet rs = XJdbc.query(sql, Auth.userKhachHang.getMaKhachHang());
                if (rs.next()) {
                    txtMaTra.setText(rs.getString("MaTraGop"));
                } else {
                    txtMaTra.setText("Chưa có trả góp");
                }
                rs.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lấy mã trả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadKyHanOptions() {
        cboKyHan.addItem("6");
        cboKyHan.addItem("12");
        cboKyHan.addItem("18");
        cboKyHan.addItem("24");
        cboKyHan.addItem("30");
        cboKyHan.addItem("36");
        cboKyHan.addItem("48");
    }

    private void updateLaiSuat() {
        int kyHan = Integer.parseInt(cboKyHan.getSelectedItem().toString());
        double laiSuat;

        if (kyHan == 6) {
            laiSuat = 3.0; // 3% cho 6 tháng
        } else if (kyHan == 48) {
            laiSuat = 3.0 * 2.5; // Gấp 2.5 lần cho 48 tháng
        } else {
            laiSuat = 3.0;
            int steps = (kyHan - 6) / 6; // Số lần tăng lãi suất (mỗi 6 tháng tăng 1 lần)
            for (int i = 0; i < steps; i++) {
                laiSuat *= 1.5; // Tăng gấp 1.5 mỗi bước
            }
        }
        txtLaiSuat.setText(String.format("%.2f", laiSuat)); // Hiển thị lãi suất với 2 chữ số thập phân
    }

    private void loadVayTienData() {
        try {
            String sql;
            if (Auth.isCustomer()) {
                sql = "SELECT MaVay, MaKhachHang, SoTienVay, LaiSuat, ThoiGianVay, NgayVay, TrangThai " +
                      "FROM VAY_TIEN WHERE MaKhachHang = ?";
                ResultSet rs = XJdbc.query(sql, Auth.userKhachHang.getMaKhachHang());
                tblVayTienModel.setRowCount(0);
                while (rs.next()) {
                    Object[] row = {
                        rs.getString("MaVay"),
                        rs.getString("MaKhachHang"),
                        rs.getDouble("SoTienVay"),
                        rs.getDouble("LaiSuat"),
                        rs.getInt("ThoiGianVay"),
                        rs.getDate("NgayVay"),
                        rs.getString("TrangThai")
                    };
                    tblVayTienModel.addRow(row);
                }
                rs.close();
            } else if (Auth.isEmployee()) {
                sql = "SELECT MaVay, MaKhachHang, SoTienVay, LaiSuat, ThoiGianVay, NgayVay, TrangThai FROM VAY_TIEN";
                ResultSet rs = XJdbc.query(sql);
                tblVayTienModel.setRowCount(0);
                while (rs.next()) {
                    Object[] row = {
                        rs.getString("MaVay"),
                        rs.getString("MaKhachHang"),
                        rs.getDouble("SoTienVay"),
                        rs.getDouble("LaiSuat"),
                        rs.getInt("ThoiGianVay"),
                        rs.getDate("NgayVay"),
                        rs.getString("TrangThai")
                    };
                    tblVayTienModel.addRow(row);
                }
                rs.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách khoản vay: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTraGopData() {
        try {
            String sql;
            if (Auth.isCustomer()) {
                sql = "SELECT tg.MaTraGop, tg.MaVay, tg.SoTienTra, tg.NgayTra, tg.TrangThai " +
                      "FROM TRA_GOP tg " +
                      "JOIN VAY_TIEN vt ON tg.MaVay = vt.MaVay " +
                      "WHERE vt.MaKhachHang = ?";
                ResultSet rs = XJdbc.query(sql, Auth.userKhachHang.getMaKhachHang());
                tblTraGopModel.setRowCount(0);
                while (rs.next()) {
                    Object[] row = {
                        rs.getString("MaTraGop"),
                        rs.getString("MaVay"),
                        rs.getDouble("SoTienTra"),
                        rs.getDate("NgayTra"),
                        rs.getString("TrangThai")
                    };
                    tblTraGopModel.addRow(row);
                }
                rs.close();
            } else if (Auth.isEmployee()) {
                sql = "SELECT MaTraGop, MaVay, SoTienTra, NgayTra, TrangThai FROM TRA_GOP";
                ResultSet rs = XJdbc.query(sql);
                tblTraGopModel.setRowCount(0);
                while (rs.next()) {
                    Object[] row = {
                        rs.getString("MaTraGop"),
                        rs.getString("MaVay"),
                        rs.getDouble("SoTienTra"),
                        rs.getDate("NgayTra"),
                        rs.getString("TrangThai")
                    };
                    tblTraGopModel.addRow(row);
                }
                rs.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách trả góp: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateMaVay() throws SQLException {
        String sql = "SELECT MAX(MaVay) FROM VAY_TIEN";
        ResultSet rs = XJdbc.query(sql);
        String maxMaVay = null;
        if (rs.next()) {
            maxMaVay = rs.getString(1);
        }
        rs.close();
        if (maxMaVay == null) {
            return "VAY001";
        }
        int number = Integer.parseInt(maxMaVay.substring(3)) + 1;
        return String.format("VAY%03d", number);
    }

    private String generateMaTraGop() throws SQLException {
        String sql = "SELECT MAX(MaTraGop) FROM TRA_GOP";
        ResultSet rs = XJdbc.query(sql);
        String maxMaTraGop = null;
        if (rs.next()) {
            maxMaTraGop = rs.getString(1);
        }
        rs.close();
        if (maxMaTraGop == null) {
            return "TG001";
        }
        int number = Integer.parseInt(maxMaTraGop.substring(2)) + 1;
        return String.format("TG%03d", number);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rdoGioiTinh = new javax.swing.ButtonGroup();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtDiachi = new javax.swing.JTextArea();
        jTextField5 = new javax.swing.JTextField();
        tabs = new javax.swing.JTabbedPane();
        PnlVayTien = new javax.swing.JPanel();
        lblMaVay = new javax.swing.JLabel();
        lblMaKH = new javax.swing.JLabel();
        btnAccept = new javax.swing.JButton();
        lblSotien = new javax.swing.JLabel();
        txtSotien = new javax.swing.JTextField();
        lblLaiSuat = new javax.swing.JLabel();
        btnHuy = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVayTien = new javax.swing.JTable();
        lblKyHan = new javax.swing.JLabel();
        cboKyHan = new javax.swing.JComboBox<>();
        txtLaiSuat = new javax.swing.JLabel();
        txtMaKH = new javax.swing.JLabel();
        txtMaVay = new javax.swing.JLabel();
        PnlTraGop = new javax.swing.JPanel();
        lblMatra = new javax.swing.JLabel();
        lblMaVay1 = new javax.swing.JLabel();
        lblSoTienTra = new javax.swing.JLabel();
        txtSoTienTra = new javax.swing.JTextField();
        btnXacNhan = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        txtMaTra = new javax.swing.JLabel();
        txtMaVay2 = new javax.swing.JLabel();

        txtDiachi.setColumns(20);
        txtDiachi.setRows(5);
        jScrollPane3.setViewportView(txtDiachi);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản Lý Khoản Vay & Trả Góp");

        lblMaVay.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMaVay.setText("Mã vay:");

        lblMaKH.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMaKH.setText("Mã khách hàng:");

        btnAccept.setText("Xác nhận");
        btnAccept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcceptActionPerformed(evt);
            }
        });

        lblSotien.setText("Số tiền cần vay:");

        lblLaiSuat.setText("Lãi suất:");

        btnHuy.setText("Huỷ bỏ");
        btnHuy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHuyActionPerformed(evt);
            }
        });

        tblVayTien.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã vay", "Mã KH", "Số tiền vay", "Lãi suất", "Kỳ hạn", "Ngày vay", "Trạng thái"
            }
        ));
        jScrollPane1.setViewportView(tblVayTien);

        lblKyHan.setText("Kỳ hạn:");

        txtLaiSuat.setText("0.0");

        txtMaKH.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtMaKH.setText("0");

        txtMaVay.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtMaVay.setText("0");

        javax.swing.GroupLayout PnlVayTienLayout = new javax.swing.GroupLayout(PnlVayTien);
        PnlVayTien.setLayout(PnlVayTienLayout);
        PnlVayTienLayout.setHorizontalGroup(
            PnlVayTienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PnlVayTienLayout.createSequentialGroup()
                .addGroup(PnlVayTienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PnlVayTienLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(PnlVayTienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PnlVayTienLayout.createSequentialGroup()
                                .addGroup(PnlVayTienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblSotien)
                                    .addComponent(lblKyHan)
                                    .addComponent(lblLaiSuat))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(PnlVayTienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtLaiSuat, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(PnlVayTienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtSotien)
                                        .addComponent(cboKyHan, 0, 350, Short.MAX_VALUE))))
                            .addGroup(PnlVayTienLayout.createSequentialGroup()
                                .addComponent(lblMaKH, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMaKH, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(PnlVayTienLayout.createSequentialGroup()
                                .addComponent(lblMaVay)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMaVay, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 82, Short.MAX_VALUE))
                    .addGroup(PnlVayTienLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE))
                    .addGroup(PnlVayTienLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAccept)
                        .addGap(58, 58, 58)
                        .addComponent(btnHuy, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(144, 144, 144)))
                .addContainerGap())
        );
        PnlVayTienLayout.setVerticalGroup(
            PnlVayTienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PnlVayTienLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(PnlVayTienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMaVay, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMaVay, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PnlVayTienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMaKH)
                    .addComponent(txtMaKH, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PnlVayTienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSotien)
                    .addComponent(txtSotien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PnlVayTienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLaiSuat)
                    .addComponent(txtLaiSuat))
                .addGap(12, 12, 12)
                .addGroup(PnlVayTienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblKyHan)
                    .addComponent(cboKyHan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PnlVayTienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAccept, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHuy, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
        );

        tabs.addTab("VAY TIỀN", PnlVayTien);

        lblMatra.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMatra.setText("Mã trả:");

        lblMaVay1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMaVay1.setText("Mã vay:");

        lblSoTienTra.setText("Số tiền trả:");

        btnXacNhan.setText("Xác nhận");
        btnXacNhan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXacNhanActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Mã trả", "Mã vay", "Số tiền trả", "Ngày trả", "Trạng thái"
            }
        ));
        jScrollPane2.setViewportView(jTable1);

        txtMaTra.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtMaTra.setText("0");

        txtMaVay2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtMaVay2.setText("0");

        javax.swing.GroupLayout PnlTraGopLayout = new javax.swing.GroupLayout(PnlTraGop);
        PnlTraGop.setLayout(PnlTraGopLayout);
        PnlTraGopLayout.setHorizontalGroup(
            PnlTraGopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PnlTraGopLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(PnlTraGopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PnlTraGopLayout.createSequentialGroup()
                        .addComponent(lblSoTienTra, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSoTienTra, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnXacNhan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(PnlTraGopLayout.createSequentialGroup()
                        .addGroup(PnlTraGopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(PnlTraGopLayout.createSequentialGroup()
                                .addComponent(lblMaVay1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMaVay2, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(PnlTraGopLayout.createSequentialGroup()
                                .addComponent(lblMatra)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMaTra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap(350, Short.MAX_VALUE))))
            .addGroup(PnlTraGopLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2))
        );
        PnlTraGopLayout.setVerticalGroup(
            PnlTraGopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PnlTraGopLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(PnlTraGopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMatra)
                    .addComponent(txtMaTra))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PnlTraGopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMaVay1)
                    .addComponent(txtMaVay2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PnlTraGopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSoTienTra)
                    .addComponent(txtSoTienTra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXacNhan))
                .addGap(48, 48, 48)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(164, Short.MAX_VALUE))
        );

        tabs.addTab("TRẢ GÓP", PnlTraGop);

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

    private void btnHuyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHuyActionPerformed
        // TODO add your handling code here:
       clearVayTienForm();
    }//GEN-LAST:event_btnHuyActionPerformed

    private void btnAcceptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcceptActionPerformed
        // TODO add your handling code here:                                       
    String maKH = txtMaKH.getText().trim();
    String soTienStr = txtSotien.getText().trim();
    String kyHan = cboKyHan.getSelectedItem().toString();
    String laiSuatStr = txtLaiSuat.getText().trim();

    if (maKH.equals("0") || soTienStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (Auth.isCustomer() && !maKH.equals(Auth.userKhachHang.getMaKhachHang())) {
        JOptionPane.showMessageDialog(this, "Bạn chỉ có thể vay cho tài khoản của mình!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    double soTienVay, laiSuat;
    try {
        soTienVay = Double.parseDouble(soTienStr);
        laiSuat = Double.parseDouble(laiSuatStr); // Lãi suất đã được tính tự động
        if (soTienVay <= 0) {
            JOptionPane.showMessageDialog(this, "Số tiền vay phải lớn hơn 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Số tiền vay không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        // Kiểm tra khách hàng tồn tại
        String checkKhachHangSql = "SELECT COUNT(*) FROM KHACH_HANG WHERE MaKhachHang = ?";
        ResultSet rs = XJdbc.query(checkKhachHangSql, maKH);
        rs.next();
        if (rs.getInt(1) == 0) {
            JOptionPane.showMessageDialog(this, "Khách hàng không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            rs.close();
            return;
        }
        rs.close();

        // Lấy MaTaiKhoan của khách hàng (chọn tài khoản đầu tiên nếu có nhiều)
        String maTaiKhoan = null;
        String sqlGetTaiKhoan = "SELECT TOP 1 MaTaiKhoan FROM TAI_KHOAN WHERE MaKhachHang = ?";
        rs = XJdbc.query(sqlGetTaiKhoan, maKH);
        if (rs.next()) {
            maTaiKhoan = rs.getString("MaTaiKhoan");
        } else {
            JOptionPane.showMessageDialog(this, "Khách hàng chưa có tài khoản! Vui lòng tạo tài khoản trước.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            rs.close();
            return;
        }
        rs.close();

        // Thêm khoản vay vào bảng VAY_TIEN
        String maVay = generateMaVay();
        txtMaVay.setText(maVay);
        txtMaVay2.setText(maVay); // Cập nhật mã vay vào tab "TRẢ GÓP"
        String sqlVayTien = "INSERT INTO VAY_TIEN (MaVay, SoTienVay, LaiSuat, ThoiGianVay, NgayVay, TrangThai, MaKhachHang) " +
                            "VALUES (?, ?, ?, ?, GETDATE(), ?, ?)";
        XJdbc.update(sqlVayTien, maVay, soTienVay, laiSuat, Integer.parseInt(kyHan), "Đang trả", maKH);

        // Cập nhật số dư trong bảng TAI_KHOAN
        String sqlUpdateSoDu = "UPDATE TAI_KHOAN SET SoDu = SoDu + ? WHERE MaTaiKhoan = ?";
        XJdbc.update(sqlUpdateSoDu, soTienVay, maTaiKhoan);

        JOptionPane.showMessageDialog(this, "Thêm khoản vay thành công! Số tiền đã được cộng vào tài khoản.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        clearVayTienForm();
        loadVayTienData();
        setLatestMaVayToForm(); // Cập nhật lại mã vay sau khi thêm
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi thêm khoản vay: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnAcceptActionPerformed

    private void btnXacNhanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXacNhanActionPerformed
        // TODO add your handling code here:
    String maVay = txtMaVay2.getText().trim();
    String soTienTraStr = txtSoTienTra.getText().trim();

    if (maVay.equals("0") || maVay.equals("Chưa có khoản vay") || soTienTraStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    double soTienTra;
    try {
        soTienTra = Double.parseDouble(soTienTraStr);
        if (soTienTra <= 0) {
            JOptionPane.showMessageDialog(this, "Số tiền trả phải lớn hơn 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Số tiền trả không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        // Kiểm tra mã vay tồn tại
        String checkVaySql;
        ResultSet rs;
        String maKH = Auth.isCustomer() ? Auth.userKhachHang.getMaKhachHang() : null;
        if (Auth.isCustomer()) {
            checkVaySql = "SELECT COUNT(*) FROM VAY_TIEN WHERE MaVay = ? AND MaKhachHang = ?";
            rs = XJdbc.query(checkVaySql, maVay, maKH);
        } else {
            checkVaySql = "SELECT COUNT(*) FROM VAY_TIEN WHERE MaVay = ?";
            rs = XJdbc.query(checkVaySql, maVay);
        }
        rs.next();
        if (rs.getInt(1) == 0) {
            JOptionPane.showMessageDialog(this, "Mã vay không tồn tại hoặc không thuộc về bạn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            rs.close();
            return;
        }
        rs.close();

        // Lấy MaTaiKhoan của khách hàng (chọn tài khoản đầu tiên nếu có nhiều)
        String maTaiKhoan = null;
        String sqlGetTaiKhoan = "SELECT TOP 1 MaTaiKhoan FROM TAI_KHOAN WHERE MaKhachHang = ?";
        rs = XJdbc.query(sqlGetTaiKhoan, maKH);
        if (rs.next()) {
            maTaiKhoan = rs.getString("MaTaiKhoan");
        } else {
            JOptionPane.showMessageDialog(this, "Bạn chưa có tài khoản! Vui lòng tạo tài khoản trước.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            rs.close();
            return;
        }
        rs.close();

        // Kiểm tra số dư tài khoản
        double soDuHienTai = 0;
        String sqlCheckSoDu = "SELECT SoDu FROM TAI_KHOAN WHERE MaTaiKhoan = ?";
        rs = XJdbc.query(sqlCheckSoDu, maTaiKhoan);
        if (rs.next()) {
            soDuHienTai = rs.getDouble("SoDu");
        }
        rs.close();
        if (soDuHienTai < soTienTra) {
            JOptionPane.showMessageDialog(this, "Số dư không đủ để thực hiện trả góp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Thêm bản ghi vào bảng TRA_GOP
        String maTraGop = generateMaTraGop();
        txtMaTra.setText(maTraGop);
        String sqlTraGop = "INSERT INTO TRA_GOP (MaTraGop, SoTienTra, NgayTra, TrangThai, MaVay) " +
                           "VALUES (?, ?, GETDATE(), ?, ?)";
        XJdbc.update(sqlTraGop, maTraGop, soTienTra, "Đã thanh toán", maVay);

        // Cập nhật số dư trong bảng TAI_KHOAN
        String sqlUpdateSoDu = "UPDATE TAI_KHOAN SET SoDu = SoDu - ? WHERE MaTaiKhoan = ?";
        XJdbc.update(sqlUpdateSoDu, soTienTra, maTaiKhoan);

        // Kiểm tra và cập nhật trạng thái khoản vay
        String sqlCheckVay = "SELECT SoTienVay, (SELECT SUM(SoTienTra) FROM TRA_GOP WHERE MaVay = ?) AS TongTra " +
                             "FROM VAY_TIEN WHERE MaVay = ?";
        rs = XJdbc.query(sqlCheckVay, maVay, maVay);
        if (rs.next()) {
            double soTienVay = rs.getDouble("SoTienVay");
            double tongTra = rs.getDouble("TongTra");
            if (tongTra >= soTienVay) {
                XJdbc.update("UPDATE VAY_TIEN SET TrangThai = ? WHERE MaVay = ?", "Hoàn thành", maVay);
            }
        }
        rs.close();

        JOptionPane.showMessageDialog(this, "Trả góp thành công! Số dư đã được cập nhật.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        clearTraGopForm();
        loadTraGopData();
        loadVayTienData();
        setLatestMaTraToForm(); // Cập nhật lại mã trả sau khi thêm
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi thêm trả góp: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnXacNhanActionPerformed

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
            java.util.logging.Logger.getLogger(VayTienJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VayTienJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VayTienJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VayTienJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                VayTienJDialog dialog = new VayTienJDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JPanel PnlTraGop;
    private javax.swing.JPanel PnlVayTien;
    private javax.swing.JButton btnAccept;
    private javax.swing.JButton btnHuy;
    private javax.swing.JButton btnXacNhan;
    private javax.swing.JComboBox<String> cboKyHan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JLabel lblKyHan;
    private javax.swing.JLabel lblLaiSuat;
    private javax.swing.JLabel lblMaKH;
    private javax.swing.JLabel lblMaVay;
    private javax.swing.JLabel lblMaVay1;
    private javax.swing.JLabel lblMatra;
    private javax.swing.JLabel lblSoTienTra;
    private javax.swing.JLabel lblSotien;
    private javax.swing.ButtonGroup rdoGioiTinh;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblVayTien;
    private javax.swing.JTextArea txtDiachi;
    private javax.swing.JLabel txtLaiSuat;
    private javax.swing.JLabel txtMaKH;
    private javax.swing.JLabel txtMaTra;
    private javax.swing.JLabel txtMaVay;
    private javax.swing.JLabel txtMaVay2;
    private javax.swing.JTextField txtSoTienTra;
    private javax.swing.JTextField txtSotien;
    // End of variables declaration//GEN-END:variables

    private void clearVayTienForm() {
        txtMaVay.setText("Chưa có khoản vay");
        if (Auth.isCustomer()) {
            txtMaKH.setText(Auth.userKhachHang.getMaKhachHang());
        } else {
            txtMaKH.setText("0");
        }
        txtSotien.setText("");
        txtLaiSuat.setText("0.0");
        cboKyHan.setSelectedIndex(0);
        updateLaiSuat(); // Cập nhật lại lãi suất khi xóa form
    }

    private void clearTraGopForm() {
        txtMaTra.setText("Chưa có trả góp");
        txtMaVay2.setText(txtMaVay.getText()); // Giữ nguyên mã vay hiện tại
        txtSoTienTra.setText("");
    }
    
    
    public void selectTab(int index){
        tabs.setSelectedIndex(index);
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
}

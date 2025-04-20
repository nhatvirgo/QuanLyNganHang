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
import java.time.LocalDate; // Để lấy ngày hiện tại
import java.time.format.DateTimeFormatter; // Để định dạng ngày

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
        loadKyHanTraOptions(); // Load các giá trị mặc định vào cbotra
        loadVayTienData();
        loadTraGopData();
        setMaKhachHangToForm();
        setLatestMaVayToForm();
        setLatestMaTraToForm();
        updateLaiSuat(); // Cập nhật lãi suất khi khởi tạo

        // Kiểm tra tài khoản của khách hàng
        if (Auth.isCustomer()) {
            try {
                String maKH = Auth.userKhachHang.getMaKhachHang();
                String sqlCheckTaiKhoan = "SELECT COUNT(*) FROM TAI_KHOAN WHERE MaKhachHang = ?";
                ResultSet rs = XJdbc.query(sqlCheckTaiKhoan, maKH);
                rs.next();
                if (rs.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(this, "Bạn chưa có tài khoản! Vui lòng tạo tài khoản trước khi sử dụng chức năng trả góp.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    tabs.setEnabledAt(1, false); // Vô hiệu hóa tab "TRẢ GÓP"
                }
                rs.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi kiểm tra tài khoản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }

        cboKyHan.addActionListener(evt -> {
            updateLaiSuat(); // Cập nhật lãi suất
            updateTraGopFields(); // Cập nhật số tiền trả
        });
        cbotra.addActionListener(evt -> updateTraGopFields()); // Thêm ActionListener cho cbotra
        txtSotien.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateTraGopFields();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateTraGopFields();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateTraGopFields();
            }
        });
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

        updateTraGopFields(); // Gọi lần đầu để cập nhật txtSoTienTra
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
            cbotra.setSelectedItem(tblVayTien.getValueAt(selectedRow, 7).toString()); // Hiển thị Hình thức trả
            updateTraGopFields(); // Cập nhật số tiền trả khi chọn dòng
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

        if (Auth.isCustomer()) {
            btnAccept.setText("Gửi yêu cầu"); // Đổi tên nút cho khách hàng
            cbotra.setEnabled(true); // Cho phép khách hàng chọn cách trả
            txtSoTienTra.setEditable(true); // Cho phép khách hàng chỉnh sửa số tiền trả (mặc định)
        } else if (Auth.isEmployee()) {
            btnAccept.setText("Xác nhận"); // Giữ tên nút cho nhân viên
            cbotra.setEnabled(false); // Vô hiệu hóa cbotra cho nhân viên
            txtSoTienTra.setEditable(false); // Vô hiệu hóa chỉnh sửa số tiền trả cho nhân viên
        }
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

    private void loadKyHanTraOptions() {
        cbotra.removeAllItems(); // Xóa các mục hiện tại
        cbotra.addItem("Trả mỗi tháng");
        cbotra.addItem("Trả hết tháng");
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
                sql = "SELECT MaVay, MaKhachHang, SoTienVay, LaiSuat, ThoiGianVay, NgayVay, TrangThai, HinhThucTra " +
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
                        rs.getString("TrangThai"),
                        rs.getString("HinhThucTra") != null ? rs.getString("HinhThucTra") : "Chưa xác định" // Sử dụng HinhThucTra
                    };
                    tblVayTienModel.addRow(row);
                }
                rs.close();
            } else if (Auth.isEmployee()) {
                sql = "SELECT MaVay, MaKhachHang, SoTienVay, LaiSuat, ThoiGianVay, NgayVay, TrangThai, HinhThucTra FROM VAY_TIEN";
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
                        rs.getString("TrangThai"),
                        rs.getString("HinhThucTra") != null ? rs.getString("HinhThucTra") : "Chưa xác định" // Sử dụng HinhThucTra
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
        lblTra = new javax.swing.JLabel();
        cbotra = new javax.swing.JComboBox<>();
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
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã vay", "Mã KH", "Số tiền vay", "Lãi suất", "Kỳ hạn", "Ngày vay", "Trạng thái", "Kỳ hạn trả"
            }
        ));
        jScrollPane1.setViewportView(tblVayTien);

        lblKyHan.setText("Kỳ hạn:");

        txtLaiSuat.setText("0.0");

        txtMaKH.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtMaKH.setText("0");

        txtMaVay.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtMaVay.setText("0");

        lblTra.setText("Kỳ trả theo:");

        cbotra.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Trả mỗi tháng", "Trả hết tháng", " " }));

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
                                    .addComponent(lblLaiSuat)
                                    .addComponent(lblTra))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(PnlVayTienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtLaiSuat, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtSotien)
                                    .addComponent(cboKyHan, 0, 350, Short.MAX_VALUE)
                                    .addComponent(cbotra, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PnlVayTienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTra)
                    .addComponent(cbotra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
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
                .addContainerGap(188, Short.MAX_VALUE))
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
    if (Auth.isEmployee()) {
            confirmVayTien();
        } else {
            // Logic cho khách hàng tạo khoản vay
            String maKH = txtMaKH.getText().trim();
            String soTienStr = txtSotien.getText().trim();
            String kyHan = cboKyHan.getSelectedItem().toString();
            String laiSuatStr = txtLaiSuat.getText().trim();
            String hinhThucTra = cbotra.getSelectedItem().toString(); // Sử dụng HinhThucTra

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
                laiSuat = Double.parseDouble(laiSuatStr);
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

                // Kiểm tra số lần vay trong ngày
                String ngayHienTai = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String sqlCheckSoLanVay = "SELECT COUNT(*) FROM VAY_TIEN WHERE MaKhachHang = ? AND CAST(NgayVay AS DATE) = ?";
                rs = XJdbc.query(sqlCheckSoLanVay, maKH, ngayHienTai);
                rs.next();
                int soLanVay = rs.getInt(1);
                rs.close();

                if (soLanVay >= 5) {
                    JOptionPane.showMessageDialog(this, "Bạn đã đạt giới hạn 5 lần vay trong ngày! Vui lòng thử lại vào ngày mai.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Thêm khoản vay với trạng thái "Đang xử lý"
                String maVay = generateMaVay();
                txtMaVay.setText(maVay);
                txtMaVay2.setText(maVay);
                String sqlVayTien = "INSERT INTO VAY_TIEN (MaVay, SoTienVay, LaiSuat, ThoiGianVay, NgayVay, TrangThai, MaKhachHang, HinhThucTra) " +
                                    "VALUES (?, ?, ?, ?, GETDATE(), ?, ?, ?)"; // Sử dụng HinhThucTra
                XJdbc.update(sqlVayTien, maVay, soTienVay, laiSuat, Integer.parseInt(kyHan), "Đang xử lý", maKH, hinhThucTra);

                JOptionPane.showMessageDialog(this, "Yêu cầu vay tiền đã được gửi. Vui lòng chờ nhân viên xác nhận!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                clearVayTienForm();
                loadVayTienData();
                setLatestMaVayToForm();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi gửi yêu cầu vay: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
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

            // Lấy MaTaiKhoan của khách hàng
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

            // Kiểm tra và cập nhật trạng thái khoản vay, đồng thời kiểm tra trả dư
            String sqlCheckVay = "SELECT SoTienVay, (SELECT SUM(SoTienTra) FROM TRA_GOP WHERE MaVay = ?) AS TongTra " +
                                 "FROM VAY_TIEN WHERE MaVay = ?";
            rs = XJdbc.query(sqlCheckVay, maVay, maVay);
            if (rs.next()) {
                double soTienVay = rs.getDouble("SoTienVay");
                double tongTra = rs.getDouble("TongTra");

                // Kiểm tra trả dư
                if (tongTra > soTienVay) {
                    double soTienDu = tongTra - soTienVay;
                    JOptionPane.showMessageDialog(this, "Bạn đã trả dư: " + String.format("%.2f", soTienDu) + " VNĐ.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                }

                // Cập nhật trạng thái khoản vay nếu đã trả đủ
                if (tongTra >= soTienVay) {
                    XJdbc.update("UPDATE VAY_TIEN SET TrangThai = ? WHERE MaVay = ?", "Hoàn thành", maVay);
                }
            }
            rs.close();

            JOptionPane.showMessageDialog(this, "Trả góp thành công! Số dư đã được cập nhật.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            clearTraGopForm();
            loadTraGopData();
            loadVayTienData();
            setLatestMaTraToForm();
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
    private javax.swing.JComboBox<String> cbotra;
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
    private javax.swing.JLabel lblTra;
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
        cbotra.setSelectedIndex(0); // Đặt lại cbotra về giá trị đầu tiên
        updateLaiSuat(); // Cập nhật lại lãi suất khi xóa form
        updateTraGopFields(); // Cập nhật lại số tiền trả góp
    }

    private void clearTraGopForm() {
        txtMaTra.setText("Chưa có trả góp");
        txtMaVay2.setText(txtMaVay.getText()); // Giữ nguyên mã vay hiện tại
        txtSoTienTra.setText("");
    }
    
    public void selectTab(int index) {
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

    private void confirmVayTien() {
        if (!Auth.isEmployee()) {
            JOptionPane.showMessageDialog(this, "Chỉ nhân viên mới có thể xác nhận khoản vay!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = tblVayTien.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khoản vay để xác nhận!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maVay = tblVayTien.getValueAt(selectedRow, 0).toString();
        String trangThai = tblVayTien.getValueAt(selectedRow, 6).toString();

        if (!trangThai.equals("Đang xử lý")) {
            JOptionPane.showMessageDialog(this, "Khoản vay này đã được xử lý hoặc hoàn thành!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Lấy thông tin khoản vay
            String sqlGetVay = "SELECT MaKhachHang, SoTienVay FROM VAY_TIEN WHERE MaVay = ?";
            ResultSet rs = XJdbc.query(sqlGetVay, maVay);
            String maKH = null;
            double soTienVay = 0;
            if (rs.next()) {
                maKH = rs.getString("MaKhachHang");
                soTienVay = rs.getDouble("SoTienVay");
            }
            rs.close();

            // Lấy MaTaiKhoan của khách hàng
            String maTaiKhoan = null;
            String sqlGetTaiKhoan = "SELECT TOP 1 MaTaiKhoan FROM TAI_KHOAN WHERE MaKhachHang = ?";
            rs = XJdbc.query(sqlGetTaiKhoan, maKH);
            if (rs.next()) {
                maTaiKhoan = rs.getString("MaTaiKhoan");
            } else {
                JOptionPane.showMessageDialog(this, "Khách hàng chưa có tài khoản!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                rs.close();
                return;
            }
            rs.close();

            // Cập nhật trạng thái khoản vay thành "Đang trả"
            String sqlUpdateVay = "UPDATE VAY_TIEN SET TrangThai = ? WHERE MaVay = ?";
            XJdbc.update(sqlUpdateVay, "Đang trả", maVay);

            // Cộng số tiền vay vào tài khoản
            String sqlUpdateSoDu = "UPDATE TAI_KHOAN SET SoDu = SoDu + ? WHERE MaTaiKhoan = ?";
            XJdbc.update(sqlUpdateSoDu, soTienVay, maTaiKhoan);

            JOptionPane.showMessageDialog(this, "Xác nhận khoản vay thành công! Số tiền đã được cộng vào tài khoản.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadVayTienData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xác nhận khoản vay: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTraGopFields() {
        String selectedTraOption = cbotra.getSelectedItem() != null ? cbotra.getSelectedItem().toString() : "";
        String soTienStr = txtSotien.getText().trim();
        String laiSuatStr = txtLaiSuat.getText().trim();
        String kyHanStr = cboKyHan.getSelectedItem() != null ? cboKyHan.getSelectedItem().toString() : "";

        // Kiểm tra dữ liệu đầu vào
        if (soTienStr.isEmpty() || laiSuatStr.isEmpty() || kyHanStr.isEmpty() || selectedTraOption.isEmpty()) {
            txtSoTienTra.setText("");
            txtSoTienTra.setEditable(true);
            return;
        }

        double soTienVay, laiSuat;
        int kyHan;
        try {
            soTienVay = Double.parseDouble(soTienStr);
            laiSuat = Double.parseDouble(laiSuatStr);
            kyHan = Integer.parseInt(kyHanStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtSoTienTra.setText("");
            txtSoTienTra.setEditable(true);
            return;
        }

        if (selectedTraOption.equals("Trả mỗi tháng")) {
            // Tính số tiền trả mỗi tháng (gốc + lãi chia đều cho số tháng)
            double laiSuatThang = laiSuat / 100 / 12; // Lãi suất hàng tháng
            double tongLai = soTienVay * laiSuatThang * kyHan; // Tổng lãi
            double tongTienPhaiTra = soTienVay + tongLai; // Tổng tiền phải trả
            double soTienTraMoiThang = tongTienPhaiTra / kyHan; // Số tiền trả mỗi tháng

            txtSoTienTra.setText(String.format("%.2f", soTienTraMoiThang));
            txtSoTienTra.setEditable(true); // Cho phép chỉnh sửa
        } else if (selectedTraOption.equals("Trả hết tháng")) {
            // Tính tổng số tiền phải trả (gốc + lãi)
            double laiSuatThang = laiSuat / 100 / 12; // Lãi suất hàng tháng
            double tongLai = soTienVay * laiSuatThang * kyHan; // Tổng lãi
            double tongTienPhaiTra = soTienVay + tongLai; // Tổng tiền phải trả

            txtSoTienTra.setText(String.format("%.2f", tongTienPhaiTra));
            txtSoTienTra.setEditable(false); // Không cho phép chỉnh sửa
        }
    }
}
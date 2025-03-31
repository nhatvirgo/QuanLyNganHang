/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.edusys.ui;

import com.edusys.enity.GiaoDich;
import com.edusys.utils.Auth;
import com.edusys.utils.XJdbc;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class GiaoDichJDialog extends javax.swing.JDialog {

    private DefaultTableModel tableModel;
    private int soLanThatBai = 0; // Biến để đếm số lần giao dịch thất bại (lưu trong bộ nhớ)

    public GiaoDichJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
        loadLichSuGiaoDich();
    }

    private void init() {
    this.setLocationRelativeTo(null);
    tableModel = (DefaultTableModel) jTable1.getModel();
    tableModel.setRowCount(0); // Xóa dữ liệu mặc định

    // Tải dữ liệu loại giao dịch từ cơ sở dữ liệu
    loadLoaiGiaoDich();

    // Nếu là khách hàng, tự động điền MaTaiKhoan (TKxxx) và không cho chỉnh sửa
    if (Auth.isLogin() && Auth.isCustomer()) {
        String maTaiKhoan = getCurrentMaTaiKhoan();
        if (maTaiKhoan != null) {
            txtTK.setText(maTaiKhoan); // Hiển thị TKxxx
            txtTK.setEditable(false);
        } else {
            txtTK.setText(""); // Nếu không tìm thấy tài khoản, để trống
        }
    }

    // Thêm DocumentListener cho txtMaNguoiNhan
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
}

    private String getCurrentMaTaiKhoan() {
    if (Auth.isEmployee()) {
        return txtTK.getText().trim(); // Nhân viên nhập trực tiếp MaTaiKhoan (TKxxx)
    } else if (Auth.isCustomer()) {
        try {
            String sql = "SELECT MaTaiKhoan FROM TAI_KHOAN WHERE MaKhachHang = ?";
            ResultSet rs = XJdbc.query(sql, Auth.userKhachHang.getMaKhachHang()); // MaKhachHang là KHxxx
            if (rs.next()) {
                return rs.getString("MaTaiKhoan"); // Trả về TKxxx
            }
            rs.close();
            JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản liên kết với khách hàng này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy mã tài khoản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    return null;
}

    private String getCurrentHoTen() {
        if (Auth.isEmployee()) {
            return Auth.userNhanVien.getHoTen(); // Tên nhân viên
        } else if (Auth.isCustomer()) {
            return Auth.userKhachHang.getHoTen(); // Tên khách hàng
        }
        return "Không xác định";
    }

    private void loadLichSuGiaoDich() {
        try {
            String sql;
            if (Auth.isEmployee()) {
                // Nhân viên có thể xem tất cả giao dịch
                sql = "SELECT gd.MaTaiKhoan, ct.TenNguoiNhan, gd.SoTien, gd.MoTa, gd.LoaiGiaoDich " +
                      "FROM GIAO_DICH gd " +
                      "LEFT JOIN CHI_TIET_GIAO_DICH ct ON gd.MaGiaoDich = ct.MaGiaoDich";
            } else {
                // Khách hàng chỉ xem giao dịch của chính họ
                sql = "SELECT gd.MaTaiKhoan, ct.TenNguoiNhan, gd.SoTien, gd.MoTa, gd.LoaiGiaoDich " +
                      "FROM GIAO_DICH gd " +
                      "LEFT JOIN CHI_TIET_GIAO_DICH ct ON gd.MaGiaoDich = ct.MaGiaoDich " +
                      "WHERE gd.MaTaiKhoan = ?";
            }
            ResultSet rs = Auth.isEmployee() ? XJdbc.query(sql) : XJdbc.query(sql, getCurrentMaTaiKhoan());
            tableModel.setRowCount(0); // Xóa dữ liệu cũ
            while (rs.next()) {
                Object[] row = {
                    rs.getString("MaTaiKhoan"),
                    rs.getString("TenNguoiNhan"),
                    rs.getDouble("SoTien"),
                    rs.getString("MoTa"),
                    rs.getString("LoaiGiaoDich")
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
    cboPhanLoai.removeAllItems(); // Xóa các mục cũ
    cboPhanLoai.addItem("Nạp tiền");
    cboPhanLoai.addItem("Rút tiền");
    cboPhanLoai.addItem("Chuyển khoản");
    cboPhanLoai.setSelectedIndex(0); // Chọn mục đầu tiên làm mặc định
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
        txtTK = new javax.swing.JTextField();
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
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản Lý Giao Dịch");

        lblTK.setText("Tài khoản ngân hàng");

        lblTenNH.setText("Tên người nhận");

        lblSoTienChuyen.setText("Số tiền chuyển");

        lblNoiDung.setText("Nội dung giao dịch");

        lblPhanLoai.setText("Phân loại giao dịch");

        btnChuyenTien.setBackground(new java.awt.Color(153, 255, 153));
        btnChuyenTien.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnChuyenTien.setForeground(new java.awt.Color(255, 255, 255));
        btnChuyenTien.setText("Chuyển tiền");
        btnChuyenTien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChuyenTienActionPerformed(evt);
            }
        });

        lblNguoiNhan.setText("Mã người nhận");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTK)
                    .addComponent(txtTenNguoiNhan)
                    .addComponent(txtSoTienChuyen)
                    .addComponent(txtNoiDung)
                    .addComponent(btnChuyenTien, javax.swing.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
                    .addComponent(txtMaNguoiNhan, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTK)
                            .addComponent(lblTenNH)
                            .addComponent(lblSoTienChuyen)
                            .addComponent(lblNoiDung)
                            .addComponent(lblPhanLoai)
                            .addComponent(cboPhanLoai, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblNguoiNhan))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTK)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTK, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNguoiNhan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaNguoiNhan, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTenNH)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTenNguoiNhan, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addGap(34, 34, 34)
                .addComponent(btnChuyenTien, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
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
                "Số tài khoản", "Tên người nhận", "Số tiền chuyển", "Nội dung chuyển", "Phân loại"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
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
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnChuyenTienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChuyenTienActionPerformed
        // TODO add your handling code here:
    String maTaiKhoanNguoiGui = getCurrentMaTaiKhoan(); // TKxxx
    String tenNguoiNhan = txtTenNguoiNhan.getText().trim();
    String soTienStr = txtSoTienChuyen.getText().trim();
    String noiDung = txtNoiDung.getText().trim();
    
    // Kiểm tra và lấy giá trị từ cboPhanLoai
    Object selectedItem = cboPhanLoai.getSelectedItem();
    if (selectedItem == null) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn loại giao dịch!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        return;
    }
    String loaiGiaoDich = selectedItem.toString();
    
    // Kiểm tra giá trị loaiGiaoDich
    if (!loaiGiaoDich.equals("Nạp tiền") && !loaiGiaoDich.equals("Rút tiền") && !loaiGiaoDich.equals("Chuyển khoản")) {
        JOptionPane.showMessageDialog(this, "Loại giao dịch không hợp lệ! Chỉ chấp nhận: Nạp tiền, Rút tiền, Chuyển khoản.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    String inputNguoiNhan = txtMaNguoiNhan.getText().trim(); // TKxxx hoặc KHxxx

    // Kiểm tra dữ liệu đầu vào cơ bản
    if (maTaiKhoanNguoiGui == null || maTaiKhoanNguoiGui.isEmpty() || soTienStr.isEmpty() || noiDung.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Nếu là Chuyển khoản, kiểm tra mã người nhận
    if (loaiGiaoDich.equals("Chuyển khoản") && inputNguoiNhan.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập mã tài khoản hoặc mã khách hàng người nhận!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
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

    // Kiểm tra tài khoản người gửi
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

    // Kiểm tra số dư nếu là Rút tiền hoặc Chuyển khoản
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

    // Nếu là Chuyển khoản, xử lý mã người nhận
    String maTaiKhoanNguoiNhan = null;
    String maKhachHangNguoiNhan = null;
    if (loaiGiaoDich.equals("Chuyển khoản")) {
        try {
            if (inputNguoiNhan.startsWith("TK")) { // Nếu nhập MaTaiKhoan (TKxxx)
                maTaiKhoanNguoiNhan = inputNguoiNhan;
                String checkNguoiNhanSql = "SELECT MaKhachHang FROM TAI_KHOAN WHERE MaTaiKhoan = ?";
                ResultSet rs = XJdbc.query(checkNguoiNhanSql, maTaiKhoanNguoiNhan);
                if (rs.next()) {
                    maKhachHangNguoiNhan = rs.getString("MaKhachHang");
                } else {
                    JOptionPane.showMessageDialog(this, "Tài khoản người nhận không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                rs.close();
            } else { // Nếu nhập MaKhachHang (KHxxx)
                maKhachHangNguoiNhan = inputNguoiNhan;
                String checkNguoiNhanSql = "SELECT MaTaiKhoan FROM TAI_KHOAN WHERE MaKhachHang = ?";
                ResultSet rs = XJdbc.query(checkNguoiNhanSql, maKhachHangNguoiNhan);
                if (rs.next()) {
                    maTaiKhoanNguoiNhan = rs.getString("MaTaiKhoan"); // Có tài khoản liên kết
                } else {
                    // Kiểm tra xem MaKhachHang có tồn tại không
                    String checkKHSql = "SELECT COUNT(*) FROM KHACH_HANG WHERE MaKhachHang = ?";
                    ResultSet rsKH = XJdbc.query(checkKHSql, maKhachHangNguoiNhan);
                    rsKH.next();
                    if (rsKH.getInt(1) == 0) {
                        JOptionPane.showMessageDialog(this, "Mã khách hàng người nhận không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        rsKH.close();
                        return;
                    }
                    rsKH.close();

                    // Không có tài khoản, mở TaiKhoanJDialog
                    int confirm = JOptionPane.showConfirmDialog(this, 
                        "Khách hàng này chưa có tài khoản. Bạn có muốn tạo tài khoản mới không?", 
                        "Thông báo", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        TaiKhoanJDialog taiKhoanDialog = new TaiKhoanJDialog(null, true);
                        taiKhoanDialog.setMaKhachHang(maKhachHangNguoiNhan); // Đặt MaKhachHang
                        taiKhoanDialog.setVisible(true);
                        // Kiểm tra lại sau khi tạo
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
                rs.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tra cứu tài khoản người nhận: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    // Thực hiện giao dịch
    try {
        String maGiaoDich = generateMaGiaoDich();

        // Thêm vào bảng GIAO_DICH
        String sqlGiaoDich = "INSERT INTO GIAO_DICH (MaGiaoDich, LoaiGiaoDich, SoTien, NgayGiaoDich, MoTa, MaTaiKhoan) " +
                             "VALUES (?, ?, ?, GETDATE(), ?, ?)";
        XJdbc.update(sqlGiaoDich, maGiaoDich, loaiGiaoDich, soTien, noiDung, maTaiKhoanNguoiGui);

        // Nếu là Chuyển khoản, thêm vào CHI_TIET_GIAO_DICH và cập nhật số dư người nhận
        if (loaiGiaoDich.equals("Chuyển khoản")) {
            String maChiTiet = "CTGD" + maGiaoDich.substring(2);
            String sqlChiTiet = "INSERT INTO CHI_TIET_GIAO_DICH (MaChiTiet, MaGiaoDich, TaiKhoanNguoiGui, TenNguoiGui, TaiKhoanNguoiNhan, TenNguoiNhan, SoTien, TrangThai) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            String tenNguoiGui = getCurrentHoTen();
            XJdbc.update(sqlChiTiet, maChiTiet, maGiaoDich, maTaiKhoanNguoiGui, tenNguoiGui, maTaiKhoanNguoiNhan, tenNguoiNhan, soTien, "Thành công");

            // Cập nhật số dư người nhận
            String updateSoDuNguoiNhanSql = "UPDATE TAI_KHOAN SET SoDu = SoDu + ? WHERE MaTaiKhoan = ?";
            XJdbc.update(updateSoDuNguoiNhanSql, soTien, maTaiKhoanNguoiNhan);
        }

        // Cập nhật số dư tài khoản người gửi
        String updateSoDuSql = "UPDATE TAI_KHOAN SET SoDu = SoDu " +
                               (loaiGiaoDich.equals("Nạp tiền") ? "+" : "-") + " ? WHERE MaTaiKhoan = ?";
        XJdbc.update(updateSoDuSql, soTien, maTaiKhoanNguoiGui);

        soLanThatBai = 0;
        JOptionPane.showMessageDialog(this, "Giao dịch thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        clearForm();
        loadLichSuGiaoDich();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi thực hiện giao dịch: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnChuyenTienActionPerformed

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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblNguoiNhan;
    private javax.swing.JLabel lblNoiDung;
    private javax.swing.JLabel lblPhanLoai;
    private javax.swing.JLabel lblSoTienChuyen;
    private javax.swing.JLabel lblTK;
    private javax.swing.JLabel lblTenNH;
    private javax.swing.JTextField txtMaNguoiNhan;
    private javax.swing.JTextField txtNoiDung;
    private javax.swing.JTextField txtSoTienChuyen;
    private javax.swing.JTextField txtTK;
    private javax.swing.JTextField txtTenNguoiNhan;
    // End of variables declaration//GEN-END:variables
  private void clearForm() {
    if (Auth.isCustomer()) {
        String maTaiKhoan = getCurrentMaTaiKhoan();
        txtTK.setText(maTaiKhoan != null ? maTaiKhoan : "");
    } else {
        txtTK.setText("");
    }
    txtTenNguoiNhan.setText("");
    txtSoTienChuyen.setText("");
    txtNoiDung.setText("");
    txtMaNguoiNhan.setText(""); // Làm mới txtMaNguoiNhan
    if (cboPhanLoai.getItemCount() > 0) {
        cboPhanLoai.setSelectedIndex(0); // Đặt về mục đầu tiên nếu có
    }
}
  private void updateTenNguoiNhan(String input) {
    if (input == null || input.trim().isEmpty()) {
        txtTenNguoiNhan.setText(""); // Xóa tên nếu mã trống
        return;
    }
    try {
        String sql;
        ResultSet rs;
        if (input.startsWith("TK")) { // Nếu nhập MaTaiKhoan (TKxxx)
            sql = "SELECT kh.HoTen FROM KHACH_HANG kh " +
                  "JOIN TAI_KHOAN tk ON kh.MaKhachHang = tk.MaKhachHang " +
                  "WHERE tk.MaTaiKhoan = ?";
            rs = XJdbc.query(sql, input);
        } else { // Nếu nhập MaKhachHang (KHxxx)
            sql = "SELECT HoTen FROM KHACH_HANG WHERE MaKhachHang = ?";
            rs = XJdbc.query(sql, input);
        }
        if (rs.next()) {
            String tenNguoiNhan = rs.getString("HoTen");
            txtTenNguoiNhan.setText(tenNguoiNhan); // Cập nhật tên người nhận
        } else {
            txtTenNguoiNhan.setText(""); // Xóa tên nếu không tìm thấy
        }
        rs.close();
    } catch (SQLException e) {
        txtTenNguoiNhan.setText(""); // Xóa tên nếu có lỗi
    }
}
}

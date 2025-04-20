package com.edusys.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class XJdbc {
    private static String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static String dburl = "jdbc:sqlserver://localhost:1433;databaseName=QUANLYNGANHANG;trustServerCertificate=true;";
    private static String username = "sa";
    private static String password = "12345678";

    static {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static PreparedStatement getStmt(String sql, Object... args) throws SQLException {
    Connection connection = DriverManager.getConnection(dburl, username, password);
    System.out.println("Connected to database: " + connection.getCatalog());
    System.out.println("Executing SQL: " + sql);
    PreparedStatement pstmt = null;
    if (sql.trim().startsWith("{")) {
        pstmt = connection.prepareCall(sql);
    } else {
        pstmt = connection.prepareStatement(sql);
    }
    for (int i = 0; i < args.length; i++) {
        pstmt.setObject(i + 1, args[i]);
    }
    return pstmt;
}

    public static void update(String sql, Object... args) {
        try {
            PreparedStatement stmt = XJdbc.getStmt(sql, args);
            try {
                stmt.executeUpdate();
            } finally {
                stmt.getConnection().close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultSet query(String sql, Object... args) {
        try {
            PreparedStatement stmt = XJdbc.getStmt(sql, args);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Object value(String sql, Object... args) {
        try {
            ResultSet rs = XJdbc.query(sql, args);
            if (rs.next()) {
                return rs.getObject(0);
            }
            rs.getStatement().getConnection().close();
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Phương thức sinh SoThe không trùng lặp
    public static String generateUniqueSoThe() {
        Random random = new Random();
        String soThe;
        boolean isUnique;
        final String BANK_PREFIX = "9704"; // Mã ngân hàng
        final int CARD_LENGTH = 16;

        do {
            StringBuilder sb = new StringBuilder(BANK_PREFIX);
            for (int i = 0; i < CARD_LENGTH - BANK_PREFIX.length(); i++) {
                sb.append(random.nextInt(10));
            }
            soThe = sb.toString();

            try {
                String sql = "SELECT COUNT(*) FROM THE_NGAN_HANG WHERE SoThe = ?";
                ResultSet rs = XJdbc.query(sql, soThe);
                rs.next();
                isUnique = rs.getInt(1) == 0;
                rs.close();
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi kiểm tra SoThe: " + e.getMessage());
            }
        } while (!isUnique);

        return soThe;
    }
}
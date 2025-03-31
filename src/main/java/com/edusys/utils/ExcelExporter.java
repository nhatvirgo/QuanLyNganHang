package com.edusys.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.JTable;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelExporter {

    public static void exportToExcel(JTable table, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook(); // Tạo workbook mới (định dạng .xlsx)
        Sheet sheet = workbook.createSheet("Sheet1"); // Tạo sheet mới

        // Tạo hàng tiêu đề
        Row headerRow = sheet.createRow(0);
        for (int col = 0; col < table.getColumnCount(); col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(table.getColumnName(col));
            // Tùy chỉnh style (nếu muốn)
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cell.setCellStyle(headerStyle);
        }

        // Điền dữ liệu từ JTable vào sheet
        for (int row = 0; row < table.getRowCount(); row++) {
            Row dataRow = sheet.createRow(row + 1); // +1 để chừa hàng tiêu đề
            for (int col = 0; col < table.getColumnCount(); col++) {
                Cell cell = dataRow.createCell(col);
                Object value = table.getValueAt(row, col);
                if (value != null) {
                    cell.setCellValue(value.toString()); // Chuyển đổi dữ liệu sang chuỗi
                } else {
                    cell.setCellValue(""); // Nếu dữ liệu null, để trống
                }
            }
        }

        // Tự động điều chỉnh kích thước cột
        for (int col = 0; col < table.getColumnCount(); col++) {
            sheet.autoSizeColumn(col);
        }

        // Ghi workbook ra file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
            MsgBox.alert(null, "Xuất file Excel thành công tại: " + filePath);
        } catch (IOException e) {
            MsgBox.alert(null, "Lỗi khi xuất file Excel: " + e.getMessage());
        } finally {
            workbook.close();
        }
    }
}
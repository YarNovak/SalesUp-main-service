package io.proj3ct.SpringDemoBot.model;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;

@Component
@RequiredArgsConstructor
public class ExelExporter {

    @Autowired
    private final DataSource dataSource;



    public File createExcelFromAllTables() throws Exception {
        Workbook workbook = new XSSFWorkbook();

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");

                Sheet sheet = workbook.createSheet(tableName);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
                ResultSetMetaData rsmd = rs.getMetaData();

                Row header = sheet.createRow(0);
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    header.createCell(i - 1).setCellValue(rsmd.getColumnName(i));
                }

                int rowIdx = 1;
                while (rs.next()) {
                    Row row = sheet.createRow(rowIdx++);
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        row.createCell(i - 1).setCellValue(String.valueOf(rs.getObject(i)));
                    }
                }
            }
        }

        File file = new File("DatabaseBackup.xlsx");
        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        }
        workbook.close();
        return file;
    }

}

package io.proj3ct.SpringDemoBot.dopclasses.Exel;


import io.proj3ct.SpringDemoBot.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    public byte[] exportToExcel(List<Orders> orders, List<Vapecompony_katalog> products, List<User> users) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {

            // 1️⃣ Замовлення
            Sheet ordersSheet = workbook.createSheet("Orders");
            createOrdersSheet(ordersSheet, orders);

            // 2️⃣ Каталог
            Sheet catalogSheet = workbook.createSheet("Catalog");
            createCatalogSheet(catalogSheet, products);

            // 3️⃣ Користувачі
            Sheet usersSheet = workbook.createSheet("Users");
            createUsersSheet(usersSheet, users);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    // ==========================================================
    //  ORDERS SHEET
    // ==========================================================
    private void createOrdersSheet(Sheet sheet, List<Orders> orders) {
        Workbook workbook = sheet.getWorkbook();
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle cellStyle = createCellStyle(workbook);

        String[] headers = {
                "Order ID", "Created At", "Customer", "Payment Type",
                "Paid", "Delivery", "Currency", "Status",
                "Product Name", "Quantity", "Price", "Menu", "Total per Item"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        double grandTotal = 0; // Підсумок по всіх замовленнях

        for (Orders order : orders) {
            double orderTotal = 0;

            for (FinalItem item : order.getFinalItems()) {
                Row row = sheet.createRow(rowIdx++);

                createStyledCell(row, 0, order.getId(), cellStyle);
                createStyledCell(row, 1, order.getCreatedAt(), cellStyle);
                createStyledCell(row, 2, order.getUser() != null ? order.getUser().getUserName() : "", cellStyle);
                createStyledCell(row, 3, order.getCash_card(), cellStyle);
                createStyledCell(row, 4, order.isPaid() ? "Yes" : "No", cellStyle);
                createStyledCell(row, 5, order.getDelivery(), cellStyle);
                createStyledCell(row, 6, order.getCurrency(), cellStyle);
                createStyledCell(row, 7, order.getStatus(), cellStyle);

                createStyledCell(row, 8, item.getName(), cellStyle);
                createStyledCell(row, 9, item.getQuantity(), cellStyle);
                createStyledCell(row, 10, item.getCena() != null ? item.getCena() : 0, cellStyle);

                Vapecompony_katalog product = vapecomponyKatalogRepository.findByIdAndBot_Id(item.getVid(), item.getBot().getId()).orElse(null);

              //  String kak = product.getVapecompony().getName();

              //  String menuName = (item.getBot() != null ? item.getBot().getName() : "N/A");
                String menuName = "N/A";

                if (item.getMenu()!=null) {

                    menuName = item.getMenu();
                }

                if((product != null) && (product.getVapecompony()!= null)) {
                    menuName = (product.getVapecompony().getName() != null ? product.getVapecompony().getName() : "N/A");
                }
                //else if(!(item.getMenu().isEmpty())) menuName = item.getMenu();

                createStyledCell(row, 11, menuName, cellStyle);

                double itemTotal = (item.getQuantity() != 0 && item.getCena() != null)
                        ? item.getQuantity() * item.getCena()
                        : 0;
                createStyledCell(row, 12, itemTotal, cellStyle);

                orderTotal += itemTotal;
            }

            // Рядок TOTAL for order
            Row totalRow = sheet.createRow(rowIdx++);
            Cell totalLabel = totalRow.createCell(0);
            totalLabel.setCellValue("TOTAL for order " + order.getId());
            totalLabel.setCellStyle(headerStyle);

            Cell totalValue = totalRow.createCell(12);
            totalValue.setCellValue(orderTotal);
            totalValue.setCellStyle(headerStyle);

            grandTotal += orderTotal;
        }

        // GRAND TOTAL в кінці
        Row grandRow = sheet.createRow(rowIdx++);
        Cell grandLabel = grandRow.createCell(0);
        grandLabel.setCellValue("GRAND TOTAL");
        grandLabel.setCellStyle(headerStyle);

        Cell grandValue = grandRow.createCell(12);
        grandValue.setCellValue(grandTotal);
        grandValue.setCellStyle(headerStyle);

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ==========================================================
    //  CATALOG SHEET
    // ==========================================================
    private void createCatalogSheet(Sheet sheet, List<Vapecompony_katalog> products) {
        Workbook workbook = sheet.getWorkbook();
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle cellStyle = createCellStyle(workbook);

        String[] headers = {"Product ID", "Name", "Menu", "Price", "Quantity", "Description"};

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (Vapecompony_katalog product : products) {
            Row row = sheet.createRow(rowIdx++);
            createStyledCell(row, 0, product.getId(), cellStyle);
            createStyledCell(row, 1, product.getName(), cellStyle);
            createStyledCell(row, 2, product.getVapecompony() != null ? product.getVapecompony().getName() : "", cellStyle);
            createStyledCell(row, 3, product.getCena() != null ? product.getCena() : 0, cellStyle);
            createStyledCell(row, 4, product.getKilkist() != null ? product.getKilkist() : 0, cellStyle);
            createStyledCell(row, 5, product.getDescription() != null ? product.getDescription() : "", cellStyle);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ==========================================================
    //  USERS SHEET
    // ==========================================================
    private void createUsersSheet(Sheet sheet, List<User> users) {
        Workbook workbook = sheet.getWorkbook();
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle cellStyle = createCellStyle(workbook);

        String[] headers = {"Chat ID", "First Name", "Last Name", "Username", "Registered At", "Blocked"};

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (User user : users) {
            Row row = sheet.createRow(rowIdx++);
            createStyledCell(row, 0, user.getChatId(), cellStyle);
            createStyledCell(row, 1, user.getFirstName(), cellStyle);
            createStyledCell(row, 2, user.getLastName(), cellStyle);
            createStyledCell(row, 3, user.getUserName(), cellStyle);
            createStyledCell(row, 4, user.getRegisteredAt(), cellStyle);
            createStyledCell(row, 5, user.getBlocked() != null && user.getBlocked() == 1 ? "Yes" : "No", cellStyle);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ==========================================================
    //  HELPER STYLES
    // ==========================================================
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        return headerStyle;
    }

    private CellStyle createCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        return cellStyle;
    }

    private void createStyledCell(Row row, int column, Object value, CellStyle style) {
        Workbook workbook = row.getSheet().getWorkbook();
        CreationHelper creationHelper = workbook.getCreationHelper();

        Cell cell = row.createCell(column);

        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());

        } else if (value instanceof java.util.Date) {
            cell.setCellValue((java.util.Date) value);
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.cloneStyleFrom(style);
            dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd.MM.yyyy HH:mm"));
            cell.setCellStyle(dateStyle);

        } else if (value instanceof java.sql.Timestamp) {
            java.util.Date date = new java.util.Date(((java.sql.Timestamp) value).getTime());
            cell.setCellValue(date);
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.cloneStyleFrom(style);
            dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd.MM.yyyy HH:mm"));
            cell.setCellStyle(dateStyle);

        } else if (value instanceof java.time.LocalDateTime) {
            java.util.Date date = java.sql.Timestamp.valueOf((java.time.LocalDateTime) value);
            cell.setCellValue(date);
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.cloneStyleFrom(style);
            dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd.MM.yyyy HH:mm"));
            cell.setCellStyle(dateStyle);

        } else if (value instanceof java.time.LocalDate) {
            java.util.Date date = java.sql.Date.valueOf((java.time.LocalDate) value);
            cell.setCellValue(date);
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.cloneStyleFrom(style);
            dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd.MM.yyyy"));
            cell.setCellStyle(dateStyle);

        } else {
            cell.setCellValue(value.toString());
            cell.setCellStyle(style);
        }
    }

}

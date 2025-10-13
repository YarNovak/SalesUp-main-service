package io.proj3ct.SpringDemoBot.model;

import javassist.Modifier;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExcelExporterViaCrudRepositories {

    @Autowired
    private final CartItemRepository cartRepo;
    @Autowired
    private final OrdersRepository ordersRepo;
    @Autowired
    private final FinalItemRepository finalRepo;
    @Autowired
    private final UserRepository userRepo;
    @Autowired
    private final VapecomponyKatalogRepository vapeRepo;

    public ExcelExporterViaCrudRepositories(CartItemRepository cartRepo, OrdersRepository ordersRepo, FinalItemRepository finalRepo, UserRepository userRepo, VapecomponyKatalogRepository vapeRepo) {
        this.cartRepo = cartRepo;
        this.ordersRepo = ordersRepo;
        this.finalRepo = finalRepo;
        this.userRepo = userRepo;
        this.vapeRepo = vapeRepo;
    }

    public File generateExcel() throws Exception {
        Workbook workbook = new XSSFWorkbook();

        writeSheet(workbook, "cart_items",  cartRepo.findAll());
        writeSheet(workbook, "orders", ordersRepo.findAll());
        writeSheet(workbook, "final_items", finalRepo.findAll());
        writeSheet(workbook, "users", userRepo.findAll());
        writeSheet(workbook, "vapecompony_katalog",  vapeRepo.findAll());


        File file = new File("DatabaseBackup.xlsx");
        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        }
        workbook.close();
        return file;
    }

    private String aggregateCollection(List<?> collection) {
        return "aaa";
    }



    private String aggregateCollection(Set<?> collection) {
        return collection.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }


    private <T> void writeSheet(Workbook workbook, String name, Iterable<T> dataIterable) {
        List<T> data = new ArrayList<>();
        dataIterable.forEach(data::add);

        Sheet sheet = workbook.createSheet(name);
        if (data.isEmpty()) return;

        T first = data.get(0);
        Field[] allFields = first.getClass().getDeclaredFields();

        List<Field> validFields = Arrays.stream(allFields)
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .filter(f -> !Modifier.isTransient(f.getModifiers()))
                .filter(f -> !f.isSynthetic())
                .collect(Collectors.toList());

        Row header = sheet.createRow(0);
        for (int i = 0; i < validFields.size(); i++) {
            Field field = validFields.get(i);
            field.setAccessible(true);
            header.createCell(i).setCellValue(field.getName());
        }

        for (int rowIdx = 0; rowIdx < data.size(); rowIdx++) {
            Row row = sheet.createRow(rowIdx + 1);
            T obj = data.get(rowIdx);

            for (int colIdx = 0; colIdx < validFields.size(); colIdx++) {
                Field field = validFields.get(colIdx);
                field.setAccessible(true);
                Cell cell = row.createCell(colIdx);
                try {
                    Object value = field.get(obj);
                    if (value == null) {
                        cell.setCellValue("");
                    } else if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else if (value instanceof Boolean) {
                        cell.setCellValue((Boolean) value);
                    } else if (value instanceof LocalDate) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        cell.setCellValue(((LocalDate) value).format(formatter));
                    }
                    else if(value instanceof String ) {
                        cell.setCellValue((String) value);
                    }
                    else if (value instanceof LocalDateTime) {
                        // Для LocalDateTime (формат 'yyyy-MM-dd HH:mm:ss')
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        cell.setCellValue(((LocalDateTime) value).format(formatter));
                    } else if (value instanceof Date) {
                        // Для старої java.util.Date
                        cell.setCellValue((Date) value);
                    } else if (value instanceof UUID) {
                        // Для UUID
                        cell.setCellValue(value.toString());
                    } else if (value instanceof BigDecimal) {
                        // Для BigDecimal
                        cell.setCellValue(((BigDecimal) value).doubleValue());
                    } else if (value instanceof Date) {
                        // Для SQL Date (java.sql.Date)
                        cell.setCellValue(((Date) value).toString());
                    }
                   else  if (value instanceof List) {
                        // Якщо поле є колекцією (наприклад, @OneToMany, @ManyToMany)
                        String aggregatedValues = aggregateCollection((List<?>) value);
                        row.createCell(colIdx).setCellValue(aggregatedValues);
                        //cell.setCellValue(((Number)1111111111).doubleValue());


                    }
                    else {
                        cell.setCellValue(((Number)123123).doubleValue());

                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    cell.setCellValue("ERROR");
                }
            }
        }
    }
}

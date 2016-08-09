package com.gwservices.inspections.util;

import org.apache.poi.ss.usermodel.*;
import org.jetbrains.annotations.*;

/**
 * Created by vmansoori on 8/6/2016.
 */
public class WorksheetWrapper {

    private Sheet sheet;

    WorksheetWrapper(@NotNull Workbook workbook, @NotNull String name) {
        Sheet sheet = workbook.getSheet(name);
        this.sheet = sheet == null ? workbook.createSheet(name) : sheet;
    }

    public WorksheetWrapper appendRow(String... values) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);

        for (int i = 0; i < values.length; i++) {
            row.createCell(i).setCellValue(values[i]);
        }

        return this;
    }

    public WorksheetWrapper addHeader(String... headers) {

        sheet.shiftRows(sheet.getFirstRowNum(), sheet.getLastRowNum(), 1);
        System.out.println("First row number " + sheet.getFirstRowNum());
        Row row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            row.createCell(i).setCellValue(headers[i]);
        }
        return this;
    }
}

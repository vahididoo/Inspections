package com.gwservices.inspections.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;

/**
 * Created by vmansoori on 7/27/2016.
 */
public class WorkbookWrapper {

    private Workbook workbook;

    public WorkbookWrapper() {
    }

    public static void main(String[] args) {
        WorkbookWrapper workbookWrapper = new WorkbookWrapper();

        String path = "C:\\tmp\\inspections\\inspectionResult.xlsx";
        String sheetName = "Raw Data 4";
        workbookWrapper.loadOrCreateWorkbook(path).createSheet(sheetName).addHeader(sheetName, "File Name", "File " +
                "Path");
        for (int i = 0; i < 100; i++) {
//            workbookWrapper.appendRow(sheetName, "Value 1", "Value 2", "Value 3");
        }
    }

    public WorkbookWrapper formatData(String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
//        sheet.getRow(0).setRowStyle(new XSSFCellStyle());
        return this;
    }

    private WorkbookWrapper loadOrCreateWorkbook(String path) {
        try {
            this.loadWorkbook(path);
        } catch (Exception e) {
            try {
                this.createWorkbook().write(path);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return this;
    }

    public WorkbookWrapper loadWorkbook(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist at " + path);
        }
        FileInputStream stream = new FileInputStream(file);
        workbook = new XSSFWorkbook(stream);
        return this;
    }

    public WorkbookWrapper createWorkbook() throws IOException {
        workbook = new XSSFWorkbook();
        return this;
    }

    public WorkbookWrapper withWorkbook(Workbook workbook) {
        this.workbook = workbook;
        return this;
    }

    public WorksheetWrapper createSheet(String name) {
        return new WorksheetWrapper(this.workbook, name);
    }

    public void write(String pathname) {
        FileOutputStream outputStream = null;

        try {

            outputStream = new FileOutputStream(new File(pathname));
            workbook.write(outputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

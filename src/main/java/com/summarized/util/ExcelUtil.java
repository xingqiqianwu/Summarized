package com.summarized.util;


import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * 不建议使用，内存占用极其严重
 * @author chenjing03
 * Created on 2018-08-09
 */
@Deprecated
public class ExcelUtil {
    public enum ExcelFileType{
        XLS,
        XLSX
    }

    /**
     * 创建excel文件，支持xls，xlsx
     *
     * @param absPath  绝对路径
     * @param fileName 文件名称，带后缀
     * @param data     Excel数据
     * @return WorkBook 对象，不用可以忽略
     * @throws IOException ioException
     */
    public static byte[] createXls(String absPath, String fileName, List<List<String>> data) throws IOException {
        FileOutputStream fileOutputStream = null;
        DataInputStream dataInputStream = null;
        FileInputStream fileInputStream = null;
        try {
            if (fileName == null || fileName.equals("")) {
                throw new RuntimeException(" createXls error ,fileName is null or empty ");
            }
            String filePath = absPath + fileName;
            File file = new File(filePath);
            if (file.exists()) {
                throw new RuntimeException(filePath + "  is exist ");
            }
            ExcelFileType excelFileType;
            if (fileName.endsWith(".xls")) {
                excelFileType = ExcelFileType.XLS;
            } else if (fileName.endsWith(".xlsx")) {
                excelFileType = ExcelFileType.XLSX;
            } else {
                throw new RuntimeException("当前文件不是excel文件");
            }
            // 创建工作薄
            Workbook workbook = getWorkbook(excelFileType,data);
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);

            // read byte[] from file
            fileInputStream = new FileInputStream(file);
            dataInputStream = new DataInputStream(fileInputStream);
            byte[] bufferFileOut = new byte[(int) file.length()];
            dataInputStream.readFully(bufferFileOut);
            return bufferFileOut;
        }finally {
            if(fileOutputStream != null){
                fileOutputStream.close();
            }
            if(fileInputStream != null){
                fileInputStream.close();
            }
            if(dataInputStream != null){
                dataInputStream.close();
            }
        }
    }

    /**
     * 构建workbook并填充数据
     * @param excelFileType excel 文件类型
     * @param data 数据
     * @return workbook
     */
    private static Workbook getWorkbook(ExcelFileType excelFileType, List<List<String>> data){
        Workbook workbook;
        if (excelFileType == ExcelFileType.XLS) {
            workbook = new HSSFWorkbook();
        } else if (excelFileType == ExcelFileType.XLSX) {
            workbook = new XSSFWorkbook();
        } else {
            throw new RuntimeException(" ExcelFileType is null");
        }
        // 创建工作表
        Sheet sheet = workbook.createSheet("sheet1");
        if (data != null) {
            for (int row = 0; row < data.size(); row++) {
                Row rows = sheet.createRow(row);
                for (int col = 0; col < data.get(row).size(); col++) {
                    // 向工作表中添加数据
                    rows.createCell(col).setCellValue(String.valueOf(data.get(row).get(col)));
                }
            }
        }
        return workbook;
    }

    /**
     * 获取Excel文件流，但是不会创建Excel文件
     *
     * @param excelFileType  绝对路径
     * @param data     Excel数据
     * @return WorkBook 对象，不用可以忽略
     * @throws IOException ioException
     */
    public static byte[] getExcelFileBytes(ExcelFileType excelFileType, List<List<String>> data) throws IOException {
        ByteArrayOutputStream byteArrayBuffer = null;
        try {
            // 创建工作薄，xlsx的workbook的write方法只能调用一次。
            Workbook workbook = getWorkbook(excelFileType,data);
            byteArrayBuffer = new ByteArrayOutputStream();
            workbook.write(byteArrayBuffer);
            return byteArrayBuffer.toByteArray();
        }finally {
            if(byteArrayBuffer != null){
                byteArrayBuffer.close();
            }
        }
    }


}

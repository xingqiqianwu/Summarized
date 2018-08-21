package com.summarized.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenjing03
 * Created on 2018-08-10
 */
public class CsvUtil {

    public static byte[] createCsvFileAndGetBytes(List<List<String>> data, String outPutPath,
                                                  String fileName) throws IOException {
        File csvFile = createCsvFile(data, outPutPath, fileName);
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(csvFile));
        byte[] bufferFileOut = new byte[(int) csvFile.length()];
        dataInputStream.readFully(bufferFileOut);
        return bufferFileOut;
    }

    /**
     * 生成为CVS文件
     *
     * @param data       源数据
     * @param outPutPath 文件路径
     * @param fileName   文件名称
     * @return file
     */
    @SuppressWarnings("rawtypes")
    public static File createCsvFile(List<List<String>> data, String outPutPath,
                                     String fileName) throws IOException {
        File csvFile;
        BufferedWriter csvFileOutputStream = null;
        try {
            File file = new File(outPutPath);
            if (!file.exists()) {
               boolean createPathResult = file.mkdir();
               if(!createPathResult){
                   throw new RuntimeException(" create path fail , path = "+outPutPath);
               }
            }
            if(!fileName.endsWith(".csv")){
                throw new RuntimeException(" CSV fileName must end with .csv ");
            }
            //定义文件名格式并创建
            String filePath = outPutPath + fileName;
            csvFile = new File(filePath);
            // UTF-8使正确读取分隔符","
            FileOutputStream fos = new FileOutputStream(csvFile);
            fos.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            // 写入文件头部
            BufferedWriter finalCsvFileOutputStream = csvFileOutputStream;
            for (List<String> o : data) {
                if (o != null) {
                    StringBuilder sb = new StringBuilder();
                    o.forEach(i -> {
                        if (i != null) {
                            sb.append("\"").append(i).append("\"");
                        }
                        sb.append(",");
                    });
                    sb.setLength(sb.length() - 1);
                    finalCsvFileOutputStream.write(sb.toString());
                    finalCsvFileOutputStream.newLine();
                }
            }
            fos.flush();
        } finally {
            if (csvFileOutputStream != null) {
                csvFileOutputStream.close();
            }
        }
        return csvFile;
    }

    /**
     * 将数据转化为csv文件流
     *
     * @param data       源数据
     * @return file
     */
    @SuppressWarnings("rawtypes")
    public static byte[] getCsvFileBytes(List<List<String>> data) throws IOException {
        BufferedWriter csvFileOutputStream = null;
        ByteArrayOutputStream fos = null;
        try {
            fos = new ByteArrayOutputStream();
            // 解决excel打开乱码问题
            fos.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            // 写入文件头部
            BufferedWriter finalCsvFileOutputStream = csvFileOutputStream;
            data.stream().filter(o -> o != null).forEach(o -> {
                StringBuilder sb = new StringBuilder();
                o.forEach(i -> {
                    if (i != null) {
                        sb.append("\"").append(i).append("\"");
                    }
                    sb.append(",");
                });
                sb.setLength(sb.length() - 1);
                try {
                    finalCsvFileOutputStream.write(sb.toString());
                    finalCsvFileOutputStream.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            finalCsvFileOutputStream.flush();
            //fos.flush();
            return fos.toByteArray();
        } finally {
            if (csvFileOutputStream != null) {
                csvFileOutputStream.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        List<List<String>> data = new ArrayList<>();
        for (int row = 0; row < 100; row++) {
            List<String> rowDataList = new ArrayList<>();
            for (int col = 0; col < 30; col++) {
                // 向工作表中添加数据
                rowDataList.add("汉字002");
            }
            data.add(rowDataList);
        }
        createCsvFile(data, "", "test");

    }

}

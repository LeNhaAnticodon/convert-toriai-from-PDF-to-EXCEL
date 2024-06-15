package com.example.convert_toriai_from_pdf_to_excel.convert;
import com.example.convert_toriai_from_pdf_to_excel.model.CsvFile;
import com.opencsv.CSVWriter;
import javafx.collections.ObservableList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class ReadPDFToExcel {

    private static String shortNouKi = "";
    private static String kouJiMe = "";
    private static String kyakuSakiMei = "";
    private static int size1;
    private static int size2;
    private static int size3 = 0;
    private static String koSyuNumMark = "3";
    private static String kirirosu = "";

    private static String fileChlName = "";

    private static String pdfPath = "";

    private static String xlsxExcelPath = "";
    private static String csvExcelDirPath = "";
    private static String chlDirPath = "";
    private static int rowToriAiNum;

    private static String kouSyu;

    private static String kouSyuName;
    public static String fileName;

    public static void convertPDFToExcel(String filePDFPath, String fileChlDirPath, ObservableList<CsvFile> csvFileNames) throws FileNotFoundException, TimeoutException {
        csvFileNames.clear();

        pdfPath = filePDFPath;
//        csvExcelDirPath = fileCSVDirPath;

        chlDirPath = fileChlDirPath;

        String[] kakuKouSyu = getFullToriaiText();
        getHeaderData(kakuKouSyu[0]);

        List<String> kakuKouSyuList = new LinkedList<>(Arrays.asList(kakuKouSyu));
        int kakuKouSyuListSize = kakuKouSyuList.size();

        for (int i = 1; i < kakuKouSyuListSize; i++) {
            String KouSyuName = extractValue(kakuKouSyuList.get(i), "法:", "梱包");

            for (int j = i + 1; j < kakuKouSyuListSize; j++) {
                String KouSyuNameAfter = extractValue(kakuKouSyuList.get(j), "法:", "梱包");
                if (KouSyuName.equals(KouSyuNameAfter)) {
                    kakuKouSyuList.set(i, kakuKouSyuList.get(i).concat(kakuKouSyuList.get(j)));
                    kakuKouSyuList.remove(j);
                    j--;
                    kakuKouSyuListSize--;
                }
            }

            /*if (i > 1) {
                String KouSyuNameBefore = extractValue(kakuKouSyuList.get(i - 1), "法:", "梱包");

                if (KouSyuName.equals(KouSyuNameBefore)) {
                    kakuKouSyuList.set(i - 1, kakuKouSyuList.get(i - 1).concat(kakuKouSyuList.get(i)));
                    kakuKouSyuList.remove(i);
                    i--;
                    kakuKouSyuListSize--;
                }
            }*/
        }

        for (int i = 1; i < kakuKouSyuList.size(); i++) {
            String[] kakuKakou = kakuKouSyuList.get(i).split("加工No:");

            getKouSyu(kakuKakou);
            Map<Map<StringBuilder, Integer>, Map<StringBuilder, Integer>> kaKouPairs = getToriaiData(kakuKakou);

            if (kaKouPairs != null) {
//                writeDataToExcel(kaKouPairs, i - 1, csvFileNames);
//                writeDataToCSV(kaKouPairs, i - 1, csvFileNames);
                writeDataToChl(kaKouPairs, i - 1, csvFileNames);
            }
        }

    }

    private static String[] getFullToriaiText() {
        String[] kakuKouSyu = new String[0];
        try(PDDocument document = PDDocument.load(new File(pdfPath))) {
            if (!document.isEncrypted()) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String toriaiText = pdfStripper.getText(document);

                kakuKouSyu = toriaiText.split("材寸");

//                System.out.println(toriaiText);

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return kakuKouSyu;
    }

    private static void getHeaderData(String header) {

        String nouKi = extractValue(header, "期[", "]");
        String[] nouKiArr = nouKi.split("/");
        shortNouKi = nouKiArr[1] + "/" + nouKiArr[2];

        kouJiMe = extractValue(header, "考[", "]");
        kyakuSakiMei = extractValue(header, "客先名[", "]");
        fileChlName = extractValue(header, "工事名[", "]");

        System.out.println(shortNouKi + " : " + kouJiMe + " : " + kyakuSakiMei);
    }

    private static void getKouSyu(String[] kakuKakou) {

        kouSyu = extractValue(kakuKakou[0].split("\n")[0], "法:", "梱包");
        String[] kouSyuNameAndSize = kouSyu.split("-");
        kouSyuName = kouSyuNameAndSize[0].trim();

        switch (kouSyuName) {
            case "K":
                koSyuNumMark = "3";
                break;
            case "L":
                koSyuNumMark = "4";
                break;
            case "FB":
                koSyuNumMark = "5";
                break;
            case "[":
                koSyuNumMark = "6";
                break;
            case "C":
                koSyuNumMark = "7";
                break;
            case "H":
                koSyuNumMark = "8";
                break;
            case "CA":
                koSyuNumMark = "9";
                break;
        }

        String[] koSyuSizeArr = kouSyuNameAndSize[1].split("x");

        size1 = 0;
        size2 = 0;
        size3 = 0;

        if (koSyuSizeArr.length == 3) {
            size1 = convertStringToIntAndMul(koSyuSizeArr[1], 10);
            size2 = convertStringToIntAndMul(koSyuSizeArr[0], 10);
            size3 = convertStringToIntAndMul(koSyuSizeArr[2], 10);
        } else if (koSyuSizeArr.length == 4) {
            size1 = convertStringToIntAndMul(koSyuSizeArr[1], 10);
            size2 = convertStringToIntAndMul(koSyuSizeArr[0], 10);
            size3 = convertStringToIntAndMul(koSyuSizeArr[3], 10);
        } else {
            size1 = convertStringToIntAndMul(koSyuSizeArr[1], 10);
            size2 = convertStringToIntAndMul(koSyuSizeArr[0], 10);
        }
    }

    private static Map<Map<StringBuilder, Integer>, Map<StringBuilder, Integer>> getToriaiData(String[] kakuKakou) throws TimeoutException {
        rowToriAiNum = 0;

        Map<Map<StringBuilder, Integer>, Map<StringBuilder, Integer>> kaKouPairs = new LinkedHashMap<>();

        if (kakuKakou == null) {
            return kaKouPairs;
        }
        for (int i = 1; i < kakuKakou.length; i++) {

            if (i == 1) {
                kirirosu = extractValue(kakuKakou[i], "切りﾛｽ設定:", "mm");
            }

            String kaKouText = kakuKakou[i];

            Map<StringBuilder, Integer> kouZaiChouPairs = new LinkedHashMap<>();
            Map<StringBuilder, Integer> meiSyouPairs = new LinkedHashMap<>();

            String[] kaKouLines = kaKouText.split("\n");

            for (String line : kaKouLines) {
                if (line.contains("鋼材長:") && line.contains("本数:")) {
                    String kouZaiChou = extractValue(line, "鋼材長:", "mm");
                    String honSuu = extractValue(line, "本数:", " ").split(" ")[0];
                    kouZaiChouPairs.put(new StringBuilder().append(convertStringToIntAndMul(kouZaiChou, 1)), convertStringToIntAndMul(honSuu, 1));
                }

                if (line.contains("名称")) {
                    String meiSyouLength = extractValue(line, "名称", "mm x").trim();
                    String[] meiSyouLengths = meiSyouLength.split(" ");
                    String length = meiSyouLengths[meiSyouLengths.length - 1];

                    String meiSyouHonSuu = extractValue(line, "mm x", "本").trim();
                    meiSyouPairs.put(new StringBuilder().append(convertStringToIntAndMul(length, 100)), convertStringToIntAndMul(meiSyouHonSuu, 1));
                }
            }

            kaKouPairs.put(kouZaiChouPairs, meiSyouPairs);
        }

        kaKouPairs.forEach((kouZaiChouPairs, meiSyouPairs) -> {
            kouZaiChouPairs.forEach((key, value) -> System.out.println("\n" + key.toString() + " : " + value));
            meiSyouPairs.forEach((key, value) -> System.out.println(key.toString() + " : " + value));
        });

        for (Map.Entry<Map<StringBuilder, Integer>, Map<StringBuilder, Integer>> e : kaKouPairs.entrySet()) {

            Map<StringBuilder, Integer> kouZaiChouPairs = e.getKey();
            Map<StringBuilder, Integer> meiSyouPairs = e.getValue();
            int kouZaiNum = 1;
            for (Map.Entry<StringBuilder, Integer> entry : kouZaiChouPairs.entrySet()) {
                kouZaiNum = entry.getValue();
            }

            rowToriAiNum += kouZaiNum * meiSyouPairs.size();
        }

        if (rowToriAiNum > 99) {
            rowToriAiNum = 99;
            System.out.println("vượt quá 99 hàng");
            throw new TimeoutException();
        }

        System.out.println(rowToriAiNum);
        System.out.println("\n" + kirirosu);

        return kaKouPairs;
    }

    private static void writeDataToExcel(Map<Map<StringBuilder, Integer>, Map<StringBuilder, Integer>> kaKouPairs, int timePlus, ObservableList<CsvFile> csvFileNames) throws FileNotFoundException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        // Ghi thời gian hiện tại vào ô A1
        Row row1 = sheet.createRow(0);
        Cell cellA1 = row1.createCell(0);

        // Ghi thời gian hiện tại vào dòng đầu tiên
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
//        SimpleDateFormat sdfSecond = new SimpleDateFormat("yyMMddHHmmss");

        // Tăng thời gian lên timePlus phút
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.MINUTE, timePlus);

        // Lấy thời gian sau khi tăng
        Date newDate = calendar.getTime();

        String newTime = sdf.format(newDate);

        cellA1.setCellValue(newTime);

        // Ghi size1, size2, size3, 1 vào ô A2, B2, C2, D2
        Row row2 = sheet.createRow(1);
        row2.createCell(0).setCellValue(size1);
        row2.createCell(1).setCellValue(size2);
        row2.createCell(2).setCellValue(size3);
        row2.createCell(3).setCellValue(1);

        // Ghi koSyuNumMark, 1, rowToriAiNum, 1 vào ô A3, B3, C3, D3
        Row row3 = sheet.createRow(2);
        row3.createCell(0).setCellValue(koSyuNumMark);
        row3.createCell(1).setCellValue(1);
        row3.createCell(2).setCellValue(rowToriAiNum);
        row3.createCell(3).setCellValue(1);

        int rowIndex = 3;

        // Ghi dữ liệu từ KA_KOU_PAIRS vào các ô
        for (Map.Entry<Map<StringBuilder, Integer>, Map<StringBuilder, Integer>> entry : kaKouPairs.entrySet()) {
            if (rowIndex >= 102) break;

            Map<StringBuilder, Integer> kouZaiChouPairs = entry.getKey();
            Map<StringBuilder, Integer> meiSyouPairs = entry.getValue();

            String keyTemp = "";
            int valueTemp = 0;

            // Ghi dữ liệu từ mapkey vào ô C4
            for (Map.Entry<StringBuilder, Integer> kouZaiEntry : kouZaiChouPairs.entrySet()) {

                keyTemp = String.valueOf(kouZaiEntry.getKey());
                valueTemp = kouZaiEntry.getValue();
            }

            // Ghi dữ liệu từ mapvalue vào ô A4, B4 và các hàng tiếp theo
            for (int i = 0; i < valueTemp; i++) {
                int j = 0;
                for (Map.Entry<StringBuilder, Integer> meiSyouEntry : meiSyouPairs.entrySet()) {
                    if (rowIndex >= 102) break;

                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(String.valueOf(meiSyouEntry.getKey()));
                    row.createCell(1).setCellValue(meiSyouEntry.getValue());
                    j++;
                }
                sheet.getRow(rowIndex - j).createCell(2).setCellValue(keyTemp);
            }
        }

        // nếu không có hàng sản phẩm nào thì sẽ chưa tạo hàng 4, 5, 6, 7, 8 và rowIndex vẫn là 3
        // cần tạo thêm 4 hàng này để ghi các thông tin kouJiMe, kyakuSakiMei, shortNouKi, kirirosu, fileName bên dưới
        for (int i = 0; i < 5; i++) {
            if (rowIndex <= i + 3) {
                sheet.createRow(i + 3);
            }
        }

        // Ghi kouJiMe, kyakuSakiMei, shortNouKi, kirirosu, fileName + kouSyu vào ô D4, D5, D6, D7
        sheet.getRow(3).createCell(3).setCellValue(kouJiMe);
        sheet.getRow(4).createCell(3).setCellValue(kyakuSakiMei);
        sheet.getRow(5).createCell(3).setCellValue(shortNouKi);
        sheet.getRow(6).createCell(3).setCellValue(kirirosu);
        sheet.getRow(7).createCell(3).setCellValue(fileChlName + " " + kouSyu);

        // Ghi giá trị 0 vào các ô A99, B99, C99, D99
        Row lastRow = sheet.createRow(rowIndex);
        lastRow.createCell(0).setCellValue(0);
        lastRow.createCell(1).setCellValue(0);
        lastRow.createCell(2).setCellValue(0);
        lastRow.createCell(3).setCellValue(0);

        String[] linkarr = pdfPath.split("\\\\");
        fileName = linkarr[linkarr.length - 1].split("\\.")[0] + " " + kouSyu + ".csv";
//        String fileNameAndTime = linkarr[linkarr.length - 1].split("\\.")[0] + "(" + sdfSecond.format(currentDate) + ")--" + kouSyu + ".csv";
        String excelPath = csvExcelDirPath + "\\" + fileName;

        csvFileNames.add(new CsvFile(fileName, kouSyuName));

        try (FileOutputStream fileOut = new FileOutputStream(excelPath)) {
            workbook.write(fileOut);
            workbook.close();
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                System.out.println("File đang được mở bởi người dùng khác");
                throw new FileNotFoundException();
            }
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        // Tạo đối tượng File đại diện cho file cần xóa
        File file = new File(excelPath);

        // Kiểm tra nếu file tồn tại và xóa nó
        // vì nếu file đang được mở thì không thể ghi đè nhưng do file là readonly nên có thể xóa dù đang mở
        // xóa xong file thì có thể ghi lại file mới mà không bị lỗi không thể ghi đè
        if (file.exists()) {
            if (file.delete()) {
//                System.out.println("File đã được xóa thành công.");
            } else {
//                System.out.println("Xóa file thất bại.");
            }
        }

        // Đặt quyền chỉ đọc cho file
        File readOnly = new File(excelPath);
        if (readOnly.exists()) {
            boolean result = readOnly.setReadOnly();
            if (result) {
                System.out.println("File is set to read-only.");
            } else {
                System.out.println("Failed to set file to read-only.");
            }
        } else {
            System.out.println("File does not exist.");
        }
    }

    private static void writeDataToCSV(Map<Map<StringBuilder, Integer>, Map<StringBuilder, Integer>> kaKouPairs, int timePlus, ObservableList<CsvFile> csvFileNames) throws FileNotFoundException {

        // Ghi thời gian hiện tại vào dòng đầu tiên
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
//        // Tạo thêm fomat có thêm giây
//        SimpleDateFormat sdfSecond = new SimpleDateFormat("yyMMddHHmmss");

        // Tăng thời gian lên timePlus phút
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.MINUTE, timePlus);

        // Lấy thời gian sau khi tăng
        Date newDate = calendar.getTime();

        String newTime = sdf.format(newDate);

        String[] linkarr = pdfPath.split("\\\\");
        fileName = linkarr[linkarr.length - 1].split("\\.")[0] + " " + kouSyu + ".csv";
//        // tạo tên file có gắn thêm thời gian để không trùng với file trước đó
//        String fileNameAndTime = linkarr[linkarr.length - 1].split("\\.")[0] + "(" + sdfSecond.format(currentDate) + ")--" + kouSyu + ".csv";
        String csvPath = csvExcelDirPath + "\\" + fileName;
        System.out.println("dir path: " + csvExcelDirPath);
        System.out.println("filename: " + fileName);

        csvFileNames.add(new CsvFile(fileName, kouSyuName));

        // Tạo đối tượng File đại diện cho file cần xóa
        File file = new File(csvPath);

        // Kiểm tra nếu file tồn tại và xóa nó
        // vì nếu file đang được mở thì không thể ghi đè nhưng do file là readonly nên có thể xóa dù đang mở
        // xóa xong file thì có thể ghi lại file mới mà không bị lỗi không thể ghi đè
        if (file.exists()) {
            if (file.delete()) {
//                System.out.println("File đã được xóa thành công.");
            } else {
//                System.out.println("Xóa file thất bại.");
            }
        } else {
//            System.out.println("File không tồn tại.");
        }

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(csvPath), Charset.forName("MS932")))) {



            writer.writeNext(new String[]{newTime});

            // Ghi size1, size2, size3, 1 vào dòng tiếp theo
            writer.writeNext(new String[]{String.valueOf(size1), String.valueOf(size2), String.valueOf(size3), "1"});

            // Ghi koSyuNumMark, 1, rowToriAiNum, 1 vào dòng tiếp theo
            writer.writeNext(new String[]{koSyuNumMark, "1", String.valueOf(rowToriAiNum), "1"});

            List<String[]> toriaiDatas = new LinkedList<>();

            int rowIndex = 3;

            // Ghi dữ liệu từ KA_KOU_PAIRS vào các ô
            for (Map.Entry<Map<StringBuilder, Integer>, Map<StringBuilder, Integer>> entry : kaKouPairs.entrySet()) {
                if (rowIndex >= 102) break;

                Map<StringBuilder, Integer> kouZaiChouPairs = entry.getKey();
                Map<StringBuilder, Integer> meiSyouPairs = entry.getValue();

                String keyTemp = "";
                int valueTemp = 0;

                // Ghi dữ liệu từ mapkey vào ô C4
                for (Map.Entry<StringBuilder, Integer> kouZaiEntry : kouZaiChouPairs.entrySet()) {

                    keyTemp = String.valueOf(kouZaiEntry.getKey());
                    valueTemp = kouZaiEntry.getValue();
                }

                // Ghi dữ liệu từ mapvalue vào ô A4, B4 và các hàng tiếp theo
                for (int i = 0; i < valueTemp; i++) {
                    int j = 0;
                    for (Map.Entry<StringBuilder, Integer> meiSyouEntry : meiSyouPairs.entrySet()) {
                        if (rowIndex >= 102) break;

                        String[] line = new String[4];
                        rowIndex++;
                        line[0] = String.valueOf(meiSyouEntry.getKey());
                        line[1] = meiSyouEntry.getValue().toString();

                        toriaiDatas.add(line);
                        j++;
                    }
                    toriaiDatas.get(toriaiDatas.size() - j)[2] = keyTemp;
                }
            }

            // nếu không có hàng sản phẩm nào thì sẽ chưa tạo hàng 4, 5, 6, 7, 8 và rowIndex vẫn là 3
            // cần tạo thêm 4 hàng này để ghi các thông tin kouJiMe, kyakuSakiMei, shortNouKi, kirirosu, fileName bên dưới
            for (int i = 0; i < 5; i++) {
                if (rowIndex <= i + 3) {
                    toriaiDatas.add(new String[4]);
                }
            }

            // Ghi kouJiMe, kyakuSakiMei, shortNouKi, kirirosu, fileName + " " + kouSyu vào ô D4, D5, D6, D7
            toriaiDatas.get(0)[3] = kouJiMe;
            toriaiDatas.get(1)[3] = kyakuSakiMei;
            toriaiDatas.get(2)[3] = shortNouKi;
            toriaiDatas.get(3)[3] = kirirosu;
            toriaiDatas.get(4)[3] = fileChlName + " " + kouSyu;

            writer.writeAll(toriaiDatas);

            // Ghi giá trị 0 vào các ô A99, B99, C99, D99
            writer.writeNext(new String[]{"0", "0", "0", "0"});



        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                System.out.println("File đang được mở bởi người dùng khác");
                throw new FileNotFoundException();
            }
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        // Đặt quyền chỉ đọc cho file
        File readOnly = new File(csvPath);
        if (readOnly.exists()) {
            boolean result = readOnly.setReadOnly();
            if (result) {
//                System.out.println("File is set to read-only.");
            } else {
//                System.out.println("Failed to set file to read-only.");
            }
        } else {
//            System.out.println("File does not exist.");
        }

    }

    private static void writeDataToChl(Map<Map<StringBuilder, Integer>, Map<StringBuilder, Integer>> kaKouPairs, int timePlus, ObservableList<CsvFile> csvFileNames) throws FileNotFoundException {

        // Ghi thời gian hiện tại vào dòng đầu tiên
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
//        // Tạo thêm fomat có thêm giây
//        SimpleDateFormat sdfSecond = new SimpleDateFormat("yyMMddHHmmss");

        // Tăng thời gian lên timePlus phút
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.MINUTE, timePlus);

        // Lấy thời gian sau khi tăng
        Date newDate = calendar.getTime();

        String newTime = sdf.format(newDate);

        // lấy tên file chl trong tiêu đề gắn thêm tên vật liệu + .sysc2
        fileName = fileChlName + " " + kouSyu + ".sysc2";

//        // tạo tên file có gắn thêm thời gian để không trùng với file trước đó
//        String fileNameAndTime = linkarr[linkarr.length - 1].split("\\.")[0] + "(" + sdfSecond.format(currentDate) + ")--" + kouSyu + ".csv";

        String chlPath = chlDirPath + "\\" + fileName;
        System.out.println("dir path: " + csvExcelDirPath);
        System.out.println("filename: " + fileName);

        // thêm file vào list hiển thị
        csvFileNames.add(new CsvFile(fileName, kouSyuName));

        // Tạo đối tượng File đại diện cho file cần xóa
        File file = new File(chlPath);

        // Kiểm tra nếu file tồn tại và xóa nó
        // vì nếu file đang được mở thì không thể ghi đè nhưng do file là readonly nên có thể xóa dù đang mở
        // xóa xong file thì có thể ghi lại file mới mà không bị lỗi không thể ghi đè
        if (file.exists()) {
            if (file.delete()) {
//                System.out.println("File đã được xóa thành công.");
            } else {
//                System.out.println("Xóa file thất bại.");
            }
        } else {
//            System.out.println("File không tồn tại.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chlPath, Charset.forName("MS932")))) {

            writer.write(newTime + ",,,"); writer.newLine();


            // Ghi size1, size2, size3, 1 vào dòng tiếp theo
            writer.write(size1 + "," + size2 + "," + size3 + "," + "1"); writer.newLine();

            // Ghi koSyuNumMark, 1, 99, 1 vào dòng tiếp theo, rowToriAiNum sẽ được sử dụng sau khi ước tính ghi đến hàng 102
            writer.write(koSyuNumMark + "," + "1" + "," + "99" + "," + "1"); writer.newLine();

            List<String[]> toriaiDatas = new LinkedList<>();

            int rowIndex = 3;

            // Ghi dữ liệu từ KA_KOU_PAIRS vào các ô
            // kaKouPairs là map chứa key cũng là map chỉ có 1 cặp có key là chiều dài bozai, value là số lượng bozai
            // còn value của kaKouPairs cũng là map chứa các cặp key là chiều dài sản phẩm, value là số lượng sản phẩm
            for (Map.Entry<Map<StringBuilder, Integer>, Map<StringBuilder, Integer>> entry : kaKouPairs.entrySet()) {
                if (rowIndex >= 102) break;

                Map<StringBuilder, Integer> kouZaiChouPairs = entry.getKey();
                Map<StringBuilder, Integer> meiSyouPairs = entry.getValue();

                // chiều dài bozai
                String keyTemp = "";
                // số lượng bozai
                int valueTemp = 0;

                // Ghi dữ liệu bozai từ mapkey vào ô C4 kouZaiChouPairs
                for (Map.Entry<StringBuilder, Integer> kouZaiEntry : kouZaiChouPairs.entrySet()) {
                    keyTemp = String.valueOf(kouZaiEntry.getKey());
                    valueTemp = kouZaiEntry.getValue();
                }

                // Ghi dữ liệu từ mapvalue vào ô A4, B4 và các hàng tiếp theo
                for (int i = 0; i < valueTemp; i++) {
                    int j = 0; // đếm số hàng đã ghi
                    // lặp qua map sản phẩm, tính chiều dài map bằng j
                    for (Map.Entry<StringBuilder, Integer> meiSyouEntry : meiSyouPairs.entrySet()) {
                        if (rowIndex >= 102) break;

                        String[] line = new String[3];
                        rowIndex++;
                        line[0] = String.valueOf(meiSyouEntry.getKey());
                        line[1] = meiSyouEntry.getValue().toString();
                        // ghi vào phần tử thứ 3 của mảng giá trị rỗng để tránh giá trị null
                        line[2] = "";

                        toriaiDatas.add(line);
                        j++;
                    }
                    // ghi vào cột 3 ([2]) chiều dài bozai khi ghi xong 1 lượt sản phẩm + số lượng
                    // tính vị trí của nó bằng cách lấy size của list kaKouPairs - chiều dài map sản phẩm
                    toriaiDatas.get(toriaiDatas.size() - j)[2] = keyTemp;
                }
            }

/*
            // nếu không có hàng sản phẩm nào thì sẽ chưa tạo hàng 4, 5, 6, 7, 8 và rowIndex vẫn là 3
            // cần tạo thêm 4 hàng này để ghi các thông tin kouJiMe, kyakuSakiMei, shortNouKi, kirirosu, fileName bên dưới
            // không cần tạo nữa vì ghi file sysc2 sẽ ghi xuống cuối
            for (int i = 0; i < 5; i++) {
                if (rowIndex <= i + 3) {
                    toriaiDatas.add(new String[4]);
                }
            }
*/

/*
            // Ghi kouJiMe, kyakuSakiMei, shortNouKi, kirirosu, fileName + " " + kouSyu vào ô D4, D5, D6, D7
            // không cần tạo nữa vì ghi file sysc2 sẽ ghi xuống cuối
            toriaiDatas.get(0)[3] = kouJiMe;
            toriaiDatas.get(1)[3] = kyakuSakiMei;
            toriaiDatas.get(2)[3] = shortNouKi;
            toriaiDatas.get(3)[3] = kirirosu;
            toriaiDatas.get(4)[3] = fileChlName + " " + kouSyu;
*/
            // lặp qua list chứa các dòng toriaiDatas
            for (String[] line : toriaiDatas) {
                // mỗi dòng là 1 mảng nên lặp qua mảng ghi các phần tử vào dòng phân tách nhau bởi dấu (,)
                for (String length : line) {
                    writer.write(length + ",");
                }
                writer.newLine();
            }

            // ghi nốt các dòng còn lại không có sản phẩn ",,," để đủ 99 sản phẩm
            for (int i = toriaiDatas.size(); i < 99; i++) {
                writer.write(",,,"); writer.newLine();
            }


            // Ghi giá trị 0 vào dòng tiếp theo là dòng 103
            writer.write("0,0,0,0");  writer.newLine();
            // ghi 20 vaf kirirosu vào dòng tiếp
            writer.write("20.0," + kirirosu + ",,");  writer.newLine();
            // ghi các tên và ngày vào dòng tiếp
            writer.write(kouJiMe + "," +  kyakuSakiMei + "," +  shortNouKi + ",");  writer.newLine();
            // dòng tiếp theo là ghi 備考１、備考２ theo định dạng 備考１,備考２,, nhưng không có nên không cần chỉ ghi (,,,)
            writer.write(",,,");  writer.newLine();
            // ghi dấu hiệu nhận biết kết thúc
            writer.write("END,,,");  writer.newLine();


        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                System.out.println("File đang được mở bởi người dùng khác");
                throw new FileNotFoundException();
            }
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        // Đặt quyền chỉ đọc cho file
        File readOnly = new File(chlPath);
        if (readOnly.exists()) {
            boolean result = readOnly.setReadOnly();
            if (result) {
//                System.out.println("File is set to read-only.");
            } else {
//                System.out.println("Failed to set file to read-only.");
            }
        } else {
//            System.out.println("File does not exist.");
        }

    }

    private static String extractValue(String text, String startDelimiter, String endDelimiter) {
        int startIndex = text.indexOf(startDelimiter) + startDelimiter.length();
        int endIndex = text.indexOf(endDelimiter, startIndex);
        return text.substring(startIndex, endIndex).trim();
    }


    private static int convertStringToIntAndMul(String textNum, int multiplier) {
        Double num = null;
        try {
            num = Double.parseDouble(textNum);
        } catch (NumberFormatException e) {
            System.out.println("Lỗi chuyển đổi chuỗi không phải số thực sang số");
            System.out.println(textNum);

        }
        if (num != null) {
            return (int) (num * multiplier);
        }
        return 0;
    }
}

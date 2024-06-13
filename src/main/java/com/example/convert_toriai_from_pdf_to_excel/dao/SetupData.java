package com.example.convert_toriai_from_pdf_to_excel.dao;

import com.example.convert_toriai_from_pdf_to_excel.model.CsvFile;
import com.example.convert_toriai_from_pdf_to_excel.model.Setup;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetupData {
    private static SetupData instance;
    private static final String FILE_SETUP_NAME = "setup_data.dat";
    private final ObservableList<CsvFile> csvFiles = FXCollections.observableArrayList();
    private final Map<String, String> languageMap = new HashMap<>();
    private final Setup setup = new Setup();
    private final Path path = Paths.get(FILE_SETUP_NAME);

    private final ObservableList<Object> controls = FXCollections.observableArrayList();

    private SetupData() {
        languageMap.put("Chọn file cần chuyển", "Select_the_file_to_transfer");
        languageMap.put("Chọn thư mục lưu file", "Select_the_folder_to_save_the_file");
        languageMap.put("THỰC HIỆN CHUYỂN FILE", "IMPLEMENT_FILE_TRANSFER");
        languageMap.put("Mở thư mục chứa", "Open_containing_folder");
        languageMap.put("Danh sách file đã chuyển sang excel.csv", "List_of_files_has_been_converted_to_excel_csv");
        languageMap.put("Trợ giúp", "Help");
        languageMap.put("Chỉnh Sửa", "Edit");
        languageMap.put("Tệp", "File");
        languageMap.put("Xác nhận địa chỉ file PDF", "Confirm_the_PDF_address_file");
        languageMap.put("Địa chỉ của file PDF chưa được xác nhận", "The_address_of_the_PDF_file_has_not_been_confirmed");
        languageMap.put("Hãy chọn file PDF để tiếp tục!", "Please_select_the_PDF_file_to_continue");
        languageMap.put("Xác nhận thư mục chứa các file Excel", "Confirm_the_folder_containing_Excel_files");
        languageMap.put("Địa chỉ thư mục chứa các file Excel chưa được xác nhận", "Folder_address_containing_unconfirmed_Excel_files");
        languageMap.put("Hãy chọn thư mục chứa để tiếp tục!", "Please_select_the_containing_folder_to_continue");

        languageMap.put("Thông tin hoạt động chuyển file", "Information_on_file_transfer_activities");
        languageMap.put("Đã chuyển xong file PDF sang các file EXCEL", "Finished_converting_PDF_files_to_EXCEL_files");
        languageMap.put("Bạn có muốn mở thư mục chứa các file EXCEL và\ntự động copy địa chỉ không?", "Do_you_want_to_open_a_folder_containing_EXCEL_files_and_automatically_copy_the_address");
        languageMap.put("Thông báo lỗi chuyển file", "File_transfer_error_message");
        languageMap.put("Nội dung file PDF không phải là tính toán vật liệu hoặc file không được phép truy cập", "The_PDF_file_content_is_not_a_material_calculation_or_the_file_is_not_authorized_to_be_accessed");
        languageMap.put("Bạn có muốn chọn file khác và thực hiện lại không?", "Do_you_want_to_select_another_file_and_do_it_again");

        languageMap.put("CHUYỂN ĐỔI FILE PDF TÍNH TOÁN VẬT LIỆU SANG EXCEL CHL", "Convert_material_calculation_PDF_files_to_Excel_CHL");



        languageMap.put("転送するファイルを選択します", "Select_the_file_to_transfer");
        languageMap.put("ファイルを保存するフォルダーを選択します", "Select_the_folder_to_save_the_file");
        languageMap.put("ファイル転送を実行します", "IMPLEMENT_FILE_TRANSFER");
        languageMap.put("含まれているフォルダーを開きます", "Open_containing_folder");
        languageMap.put("ファイルのリストがEXCEL.csvに変換されました", "List_of_files_has_been_converted_to_excel_csv");
        languageMap.put("ヘルプ", "Help");
        languageMap.put("編集", "Edit");
        languageMap.put("ファイル", "File");
        languageMap.put("PDFファイルの場所を確認", "Confirm_the_PDF_address_file");
        languageMap.put("PDFファイルのアドレスは未確認です", "The_address_of_the_PDF_file_has_not_been_confirmed");
        languageMap.put("続行するには PDF ファイルを選択してください。", "Please_select_the_PDF_file_to_continue");
        languageMap.put("Excelファイルが入っているフォルダを確認", "Confirm_the_folder_containing_Excel_files");
        languageMap.put("未確認のExcelファイルが入っているフォルダアドレス", "Folder_address_containing_unconfirmed_Excel_files");
        languageMap.put("続行するには、含まれているフォルダーを選択してください。", "Please_select_the_containing_folder_to_continue");

        languageMap.put("ファイル転送アクティビティに関する情報", "Information_on_file_transfer_activities");
        languageMap.put("PDFファイルからEXCELファイルへの変換が完了しました", "Finished_converting_PDF_files_to_EXCEL_files");
        languageMap.put("EXCEL ファイルを含むフォルダーを開いて、アドレスを自動的にコピーしますか?", "Do_you_want_to_open_a_folder_containing_EXCEL_files_and_automatically_copy_the_address");
        languageMap.put("ファイル転送エラーメッセージ", "File_transfer_error_message");
        languageMap.put("PDFファイルの内容が材料計算ではないか、ファイルへのアクセスが許可されていません", "The_PDF_file_content_is_not_a_material_calculation_or_the_file_is_not_authorized_to_be_accessed");
        languageMap.put("別のファイルを選択してやり直しますか?", "Do_you_want_to_select_another_file_and_do_it_again");

        languageMap.put("材料計算書PDFファイルをExcelCHLに変換", "Convert_material_calculation_PDF_files_to_Excel_CHL");



        languageMap.put("Select the file to transfer", "Select_the_file_to_transfer");
        languageMap.put("Select the folder to save the file", "Select_the_folder_to_save_the_file");
        languageMap.put("IMPLEMENT FILE TRANSFER", "IMPLEMENT_FILE_TRANSFER");
        languageMap.put("Open containing folder", "Open_containing_folder");
        languageMap.put("List of files has been converted to excel.csv", "List_of_files_has_been_converted_to_excel_csv");
        languageMap.put("Help", "Help");
        languageMap.put("Edit", "Edit");
        languageMap.put("File", "File");
        languageMap.put("Confirm the PDF address file", "Confirm_the_PDF_address_file");
        languageMap.put("The address of the PDF file has not been confirmed", "The_address_of_the_PDF_file_has_not_been_confirmed");
        languageMap.put("Please select the PDF file to continue!", "Please_select_the_PDF_file_to_continue");
        languageMap.put("Confirm the folder containing Excel files", "Confirm_the_folder_containing_Excel_files");
        languageMap.put("Folder address containing unconfirmed Excel files", "Folder_address_containing_unconfirmed_Excel_files");
        languageMap.put("Please select the containing folder to continue!", "Please_select_the_containing_folder_to_continue");

        languageMap.put("Information on file transfer activities", "Information_on_file_transfer_activities");
        languageMap.put("Finished converting PDF files to EXCEL files", "Finished_converting_PDF_files_to_EXCEL_files");
        languageMap.put("Do you want to open a folder containing EXCEL files and automatically copy the address?", "Do_you_want_to_open_a_folder_containing_EXCEL_files_and_automatically_copy_the_address");
        languageMap.put("File transfer error message", "File_transfer_error_message");
        languageMap.put("The PDF file content is not a material calculation or the file is not authorized to be accessed", "The_PDF_file_content_is_not_a_material_calculation_or_the_file_is_not_authorized_to_be_accessed");
        languageMap.put("Do you want to select another file and do it again?", "Do_you_want_to_select_another_file_and_do_it_again");

        languageMap.put("CONVERT MATERIAL CALCULATION PDF FILES TO EXCEL CHL", "Convert_material_calculation_PDF_files_to_Excel_CHL");
    }

    public static SetupData getInstance() {
        if (instance == null) {
            synchronized (SetupData.class) {
                instance = new SetupData();
            }
        }
        return instance;
    }

    public ObservableList<Object> getControls() {
        return controls;
    }

    public Setup getSetup() {
        return setup;
    }

    public void setLinkPdfFile(String linkPdfFile){
        setup.setLinkPdfFile(linkPdfFile);
        try {
            saveSetup();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setLinkSaveCvsFileDir(String SaveCvsFileDir) {
        setup.setLinkSaveCvsFileDir(SaveCvsFileDir);
        try {
            saveSetup();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setLang(String lang) throws IOException {
        setup.setLang(lang);
        saveSetup();
    }

    public ObservableList<CsvFile> getCsvFiles() {
        return csvFiles;
    }

    public Map<String, String> getLanguageMap() {
        return languageMap;
    }

    public void loadSetup() throws IOException {
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(setup.getLinkPdfFile() + setup.getLinkSaveCvsFileDir() + setup.getLang());
            return;
        }

        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(Files.newInputStream(path)))) {
            boolean eof = false;
            while(!eof) {
                try {

                    String linkPdfFile = dis.readUTF();
                    String linkSaveCvsFileDir = dis.readUTF();
                    String lang = dis.readUTF();

                    setup.setLinkPdfFile(linkPdfFile);
                    System.out.println(":" + setup.getLinkPdfFile());

                    setup.setLinkSaveCvsFileDir(linkSaveCvsFileDir);
                    System.out.println(":" + setup.getLinkSaveCvsFileDir());

                    setup.setLang(lang);
                    System.out.println(":" + setup.getLang());

                } catch(EOFException e) {
                    eof = true;
                }
            }
        }
    }

    public void saveSetup() throws IOException {
//        if (Files.notExists(path)) {
//            Files.createFile(path);
//        }

        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(path)))) {
                dos.writeUTF(setup.getLinkPdfFile());
                dos.writeUTF(setup.getLinkSaveCvsFileDir());
                dos.writeUTF(setup.getLang());
        }
    }


}

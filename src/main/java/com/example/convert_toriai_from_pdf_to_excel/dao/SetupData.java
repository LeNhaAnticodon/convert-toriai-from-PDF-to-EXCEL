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
    private static final String FILE_SETUP_NAME = "setup data.dat";
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

        languageMap.put("転送するファイルを選択します", "Select_the_file_to_transfer");
        languageMap.put("ファイルを保存するフォルダーを選択します", "Select_the_folder_to_save_the_file");
        languageMap.put("ファイル転送を実行します", "IMPLEMENT_FILE_TRANSFER");
        languageMap.put("含まれているフォルダーを開きます", "Open_containing_folder");
        languageMap.put("ファイルのリストがEXCEL.csvに変換されました", "List_of_files_has_been_converted_to_excel_csv");
        languageMap.put("ヘルプ", "Help");
        languageMap.put("編集", "Edit");
        languageMap.put("ファイル", "File");

        languageMap.put("Select the file to transfer", "Select_the_file_to_transfer");
        languageMap.put("Select the folder to save the file", "Select_the_folder_to_save_the_file");
        languageMap.put("IMPLEMENT FILE TRANSFER", "IMPLEMENT_FILE_TRANSFER");
        languageMap.put("Open containing folder", "Open_containing_folder");
        languageMap.put("List of files has been converted to excel.csv", "List_of_files_has_been_converted_to_excel_csv");
        languageMap.put("Help", "Help");
        languageMap.put("Edit", "Edit");
        languageMap.put("File", "File");
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

    public void setLinkPdfFile(String linkPdfFile) throws IOException {
        setup.setLinkPdfFile(linkPdfFile);
        saveSetup();
    }

    public void setLinkSaveCvsFileDir(String SaveCvsFileDir) throws IOException {
        setup.setLinkSaveCvsFileDir(SaveCvsFileDir);
        saveSetup();
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

    public void loadSetup(){
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

                    setup.setLinkPdfFile(dis.readUTF());
                    setup.setLinkSaveCvsFileDir(dis.readUTF());
                    setup.setLang(dis.readUTF());

                    System.out.println(":" + setup.getLinkPdfFile());
                    System.out.println(":" + setup.getLinkSaveCvsFileDir());
                    System.out.println(":" + setup.getLang());
                } catch(EOFException e) {
                    eof = true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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

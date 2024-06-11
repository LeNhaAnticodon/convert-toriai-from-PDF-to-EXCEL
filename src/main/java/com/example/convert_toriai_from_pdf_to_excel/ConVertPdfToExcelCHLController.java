package com.example.convert_toriai_from_pdf_to_excel;

import com.example.convert_toriai_from_pdf_to_excel.dao.SetupData;
import com.example.convert_toriai_from_pdf_to_excel.model.CsvFile;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ConVertPdfToExcelCHLController implements Initializable {
    @FXML
    public TextField linkPdfFile;
    @FXML
    public Button getPdfFileBtn;
    @FXML
    public TextField linkCvsDir;
    @FXML
    public Button setSaveCsvFileDirBtn;
    @FXML
    public ListView<CsvFile> csvFIleList;
    @FXML
    public Button convertFileBtn;
    @FXML
    public Button openDirCsvBtn;
    @FXML
    public RadioButton setLangNihongoBtn;
    @FXML
    public ToggleGroup languages;
    @FXML
    public RadioButton setLangVietNamBtn;
    @FXML
    public RadioButton setLangEnglishBtn;
    @FXML
    public Menu menuHelp;
    @FXML
    public Menu menuEdit;
    @FXML
    public Menu menuFile;
    @FXML
    public Label listCsvFileTitle;
    @FXML
    public MenuBar menuBar;

    private Map<String, String> languageMap;

    private ResourceBundle bundle;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        csvFIleList.setItems(SetupData.getInstance().getCsvFiles());

        languageMap = SetupData.getInstance().getLanguageMap();

        // Load the resource bundle
        bundle = ResourceBundle.getBundle("languagesMap");

        setLangVietNamBtn.setUserData("vi");
        setLangEnglishBtn.setUserData("en");
        setLangNihongoBtn.setUserData("ja");

        setLangVietNamBtn.setSelected(true);
        SetupData.getInstance().getSetup().setLang(setLangVietNamBtn.getUserData().toString());

        System.out.println(setLangVietNamBtn.getUserData().toString());

//        menuHelp.setText("ヘルプ");

        languages.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                String selectedLang = (String) newValue.getUserData();
                updateLangInBackground(selectedLang, getPdfFileBtn, setSaveCsvFileDirBtn, convertFileBtn, openDirCsvBtn, listCsvFileTitle, menuBar);
            }
        });
    }

    @FXML
    public void getPdfFile(ActionEvent actionEvent) {

    }

    @FXML
    public void setSaveCsvFileDir(ActionEvent actionEvent) {

    }

    @FXML
    public void convertFile(ActionEvent actionEvent) {
    }

    @FXML
    public void openDirCsv(ActionEvent actionEvent) {
    }

    @FXML
    public void setLangNihongo(ActionEvent actionEvent) {
    }

    @FXML
    public void setLangVietNam(ActionEvent actionEvent) {
    }

    @FXML
    public void setLangEnglish(ActionEvent actionEvent) {
    }

    private void updateLangInBackground(String lang, Object... controls) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> updateLang(lang, controls));
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void updateLang(String lang, Object... controls) {
        for (Object control : controls) {
            if (control instanceof Labeled labeledControl) {
                String currentText = labeledControl.getText();
                String key = languageMap.get(currentText);
                if (key != null) {
                    String newText = bundle.getString(key + "." + lang);
                    labeledControl.setText(newText);
                }
            } else if (control instanceof MenuBar menuBar) {
                for (Menu menu : menuBar.getMenus()) {
                    String currentText = menu.getText();
                    String key = languageMap.get(currentText);
                    if (key != null) {
                        String newText = bundle.getString(key + "." + lang);
                        menu.setText(newText);
                    }
                    for (MenuItem menuItem : menu.getItems()) {
                        currentText = menuItem.getText();
                        key = languageMap.get(currentText);
                        if (key != null) {
                            String newText = bundle.getString(key + "." + lang);
                            menuItem.setText(newText);
                        }
                    }
                }
            }
        }
    }


}
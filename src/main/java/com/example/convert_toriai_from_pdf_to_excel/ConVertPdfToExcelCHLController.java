package com.example.convert_toriai_from_pdf_to_excel;

import com.example.convert_toriai_from_pdf_to_excel.dao.SetupData;
import com.example.convert_toriai_from_pdf_to_excel.model.CsvFile;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
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

    private ObservableList<Object> controls;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        csvFIleList.setItems(SetupData.getInstance().getCsvFiles());

        languageMap = SetupData.getInstance().getLanguageMap();
        controls = SetupData.getInstance().getControls();
        controls.addAll(getPdfFileBtn, setSaveCsvFileDirBtn, convertFileBtn, openDirCsvBtn, listCsvFileTitle, menuBar);

        // Load the resource bundle
        bundle = ResourceBundle.getBundle("languagesMap");

        setLangVietNamBtn.setUserData("vi");
        setLangEnglishBtn.setUserData("en");
        setLangNihongoBtn.setUserData("ja");

        String lang = SetupData.getInstance().getSetup().getLang();
        if (lang.isBlank() || (!lang.equals("vi") && !lang.equals("en") && !lang.equals("ja"))) {
            languages.selectToggle(setLangVietNamBtn);
        } else {
            if (lang.equals("vi")) {
                languages.selectToggle(setLangVietNamBtn);
            }

            if (lang.equals("en")) {
                languages.selectToggle(setLangEnglishBtn);
            }

            if (lang.equals("ja")) {
                languages.selectToggle(setLangNihongoBtn);
            }
        }

        updateLangInBackground(languages.getSelectedToggle(), controls);
        setlang();

        languages.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                updateLangInBackground(newValue, controls);
                setlang();
            }
        });

        linkCvsDir.setText(SetupData.getInstance().getSetup().getLinkSaveCvsFileDir());
    }

    private void setlang() {
        try {
            SetupData.getInstance().setLang(languages.getSelectedToggle().getUserData().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void getPdfFile(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));

        String oldLinkPdfFile = SetupData.getInstance().getSetup().getLinkPdfFile();
        if (!oldLinkPdfFile.isBlank()) {
            String[] oldLinkPdfFileArr = oldLinkPdfFile.split("\\\\");
            String linkPdfFileDir = "";
            for (int i = 0; i < oldLinkPdfFileArr.length - 1; i++) {
                linkPdfFileDir = linkPdfFileDir.concat(oldLinkPdfFileArr[i]).concat("\\");
            }
            fileChooser.setInitialDirectory(new File(linkPdfFileDir));
        }

        File file = null;
        while (true) {
            try {
                file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("địa chỉ khởi đầu không hợp lệ");
                fileChooser.setInitialDirectory(null);
            }
        }

        if (file != null) {
            String link = file.getAbsolutePath();
            linkPdfFile.setText(link);
            SetupData.getInstance().setLinkPdfFile(link);

            if (linkCvsDir.getText().isBlank()) {
                String[] csvDirArr = link.split("\\\\");
                String csvDir = "";
                for (int i = 0; i < csvDirArr.length - 1; i++) {
                    csvDir = csvDir.concat(csvDirArr[i]).concat("\\");
                }
                linkCvsDir.setText(csvDir);
            }
        }

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

    private void updateLangInBackground(Toggle langBtn, ObservableList<Object> controls) {
        String lang = langBtn.getUserData().toString();
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

    private void updateLang(String lang, ObservableList<Object> controls) {
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
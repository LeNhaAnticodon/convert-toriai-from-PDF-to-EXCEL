package com.example.convert_toriai_from_pdf_to_excel;

import com.example.convert_toriai_from_pdf_to_excel.convert.ReadPDFToExcel;
import com.example.convert_toriai_from_pdf_to_excel.dao.SetupData;
import com.example.convert_toriai_from_pdf_to_excel.model.CsvFile;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
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
    @FXML
    public Label copyLinkStatusLabel;
    @FXML
    public Button copyLinkBtn;

    private Map<String, String> languageMap;

    private ResourceBundle bundle;

    private ObservableList<Object> controls;

    public ObservableList<Object> getControls() {
        return controls;
    }

    private final Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

    private static final String CONFIRM_PDF_FILE_LINK_TITLE = "Xác nhận địa chỉ file PDF";
    private static final String CONFIRM_PDF_FILE_LINK_HEADER = "Địa chỉ của file PDF chưa được xác nhận";
    private static final String CONFIRM_PDF_FILE_LINK_CONTENT = "Hãy chọn file PDF để tiếp tục!";

    private static final String CONFIRM_CSV_FILE_DIR_TITLE = "Xác nhận thư mục chứa các file Excel";
    private static final String CONFIRM_CSV_FILE_DIR_HEADER = "Địa chỉ thư mục chứa các file Excel chưa được xác nhận";
    private static final String CONFIRM_CSV_FILE_DIR_CONTENT = "Hãy chọn thư mục chứa để tiếp tục!";

    private static final String CONFIRM_CONVERT_COMPLETE_TITLE = "Thông tin hoạt động chuyển file";
    private static final String CONFIRM_CONVERT_COMPLETE_HEADER = "Đã chuyển xong file PDF sang các file EXCEL";
    private static final String CONFIRM_CONVERT_COMPLETE_CONTENT = "Bạn có muốn mở thư mục chứa các file EXCEL và\ntự động copy địa chỉ không?";

    private static final String ERROR_CONVERT_TITLE = "Thông báo lỗi chuyển file";
    private static final String ERROR_CONVERT_HEADER = "Nội dung file PDF không phải là tính toán vật liệu hoặc file không được phép truy cập";
    private static final String ERROR_CONVERT_CONTENT = "Bạn có muốn chọn file khác và thực hiện lại không?";

    private static final String ERROR_OPEN_CSV_DIR_TITLE = "Lỗi mở thư mục";
    private static final String ERROR_COPY_CSV_DIR_TITLE = "Lỗi copy địa chỉ thư mục";
    private static final String ERROR_CSV_DIR_HEADER = "Thư mục chứa các file EXCEL có địa chỉ không đúng hoặc chưa được chọn!";
    private static final String ERROR_COPY_CSV_DIR_CONTENT = "Không thể copy địa chỉ thư mục chứa các file EXCEL";


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("link csv " + SetupData.getInstance().getSetup().getLinkSaveCvsFileDir());
        System.out.println("old link file " + SetupData.getInstance().getSetup().getLinkPdfFile());
        System.out.println("old" + SetupData.getInstance().getSetup().getLinkPdfFile());

        csvFIleList.setItems(SetupData.getInstance().getCsvFiles());
        setupCellCsvFIleList();

        languageMap = SetupData.getInstance().getLanguageMap();
        controls = SetupData.getInstance().getControls();
        controls.addAll(getPdfFileBtn, setSaveCsvFileDirBtn, convertFileBtn, openDirCsvBtn, listCsvFileTitle, menuBar, copyLinkStatusLabel, copyLinkBtn);

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

        linkPdfFile.textProperty().addListener((observableValue, oldValue, newValue) -> {
            setBorderColorTF(linkPdfFile);
        });

        linkCvsDir.textProperty().addListener((observableValue, oldValue, newValue) -> {
            setBorderColorTF(linkCvsDir);
        });

        File fileCsvDiv = new File(SetupData.getInstance().getSetup().getLinkSaveCvsFileDir());

        if (fileCsvDiv.isDirectory()) {
            linkCvsDir.setText(SetupData.getInstance().getSetup().getLinkSaveCvsFileDir());
        }
    }

    private void setBorderColorTF(TextField textField) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    textField.setStyle("-fx-border-color: #FFA000; -fx-border-width: 2; -fx-border-radius: 5");
                    // Tạo Timeline để ẩn Label sau 3 giây
                    Timeline timeline = new Timeline(new KeyFrame(
                            Duration.seconds(3),
                            event -> {
                                textField.setStyle("-fx-border-color:  none");
//                                textField.setStyle("-fx-border-width:  none");
//                                textField.setStyle("-fx-border-radius:  none");
                            } // Ẩn Label sau 3 giây
                    ));
                    // Chạy Timeline một lần
                    timeline.setCycleCount(1);
                    timeline.play();

                });
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void setlang() {
        try {
            SetupData.getInstance().setLang(languages.getSelectedToggle().getUserData().toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public File getPdfFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));

        String oldLinkPdfFile = SetupData.getInstance().getSetup().getLinkPdfFile();
        System.out.println("old" + oldLinkPdfFile);
        if (!oldLinkPdfFile.isBlank()) {
            String[] oldLinkPdfFileArr = oldLinkPdfFile.split("\\\\");
            String linkPdfFileDir = "";
            for (int i = 0; i < oldLinkPdfFileArr.length - 1; i++) {
                linkPdfFileDir = linkPdfFileDir.concat(oldLinkPdfFileArr[i]).concat("\\");
            }

            File file = new File(linkPdfFileDir);
            if (file.isDirectory()) {
                fileChooser.setInitialDirectory(file);
            }
        }

        File file;
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
                SetupData.getInstance().setLinkSaveCvsFileDir(csvDir);
            }
        } else {
            System.out.println("không chọn file");
        }

        return file;
    }

    @FXML
    public File setSaveCsvFileDir() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(menuBar.getScene().getWindow());

        if (dir != null) {
            String link = dir.getAbsolutePath();
            linkCvsDir.setText(link);
            SetupData.getInstance().setLinkSaveCvsFileDir(link);
        } else {
            System.out.println("không chọn thư mục");
        }
        return dir;
    }

    @FXML
    public void convertFile(ActionEvent actionEvent) {
        String pdfFilePath;
        String csvFileDirPath;

        // yêu cầu chọn địa chỉ file và thư mục khi 2 địa chỉ này chưa được chọn
        // nếu chọn xong thì phải chuyển dữ liệu thành công thì mới thoát được vòng lặp
        while (true) {
            pdfFilePath = linkPdfFile.getText();
            csvFileDirPath = linkCvsDir.getText();

            File pdfFile = new File(pdfFilePath);
            File csvFileDir = new File(csvFileDirPath);

            boolean isFilePDF = pdfFile.isFile();
            boolean isDir = csvFileDir.isDirectory();

            if (!isFilePDF) {
                confirmAlert.setTitle(CONFIRM_PDF_FILE_LINK_TITLE);
                confirmAlert.setHeaderText(CONFIRM_PDF_FILE_LINK_HEADER);
                confirmAlert.setContentText(CONFIRM_PDF_FILE_LINK_CONTENT);
                updateLangAlert(confirmAlert);
                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    File fileSelected = getPdfFile();

                    if (fileSelected == null) {
                        return;
                    }

                    /* sau khi đã chọn xong file pdf thì gán luôn file do hàm chọn trả về để hàm lấy file để chuyển bên dưới
                     hoạt động đúng mà không phải thêm 1 vòng lặp nữa tính lại file */
                    pdfFile = fileSelected;

                    if (!pdfFile.isFile()) {
                        continue;
                    }
                } else {
                    return;
                }
            }

            if (!isDir) {
                /* nếu địa chỉ file pdf đã xác nhận thì nó sẽ tự động lấy địa chỉ thư mục chứa file pdf đó nhập vào
                 linkCvsDir, mà trước đó đã xác nhận chưa chọn thư mục chứa file đã chuyển nên cần xóa text của
                 linkCvsDir đi để người dùng xác nhận lại, tránh hiển thị địa chỉ mặc định trên ỏ linkCvsDir làm khó hiểu */
                linkCvsDir.setText("");
                SetupData.getInstance().setLinkSaveCvsFileDir("");
                confirmAlert.setTitle(CONFIRM_CSV_FILE_DIR_TITLE);
                confirmAlert.setHeaderText(CONFIRM_CSV_FILE_DIR_HEADER);
                confirmAlert.setContentText(CONFIRM_CSV_FILE_DIR_CONTENT);
                updateLangAlert(confirmAlert);
                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    File dirSelected = setSaveCsvFileDir();

                    if (dirSelected == null) {
                        return;
                    }

                    csvFileDir = dirSelected;

                    if (!csvFileDir.isDirectory()) {
                        continue;
                    }
                } else {
                    return;
                }
            }

            if (pdfFile.isFile() && csvFileDir.isDirectory()) {
                System.out.println("đã chọn xong 2 địa chỉ");
                System.out.println(pdfFile.getAbsolutePath());
                System.out.println(csvFileDir.getAbsolutePath());
            }

            try {
                ReadPDFToExcel.convertPDFToExcel(pdfFile.getAbsolutePath(), csvFileDir.getAbsolutePath(), SetupData.getInstance().getCsvFiles());
                confirmAlert.setTitle(CONFIRM_CONVERT_COMPLETE_TITLE);
                confirmAlert.setHeaderText(CONFIRM_CONVERT_COMPLETE_HEADER);
                confirmAlert.setContentText(CONFIRM_CONVERT_COMPLETE_CONTENT);
                updateLangAlert(confirmAlert);

                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    copyContentToClipBoard(csvFileDir.getAbsolutePath());
                    Desktop.getDesktop().open(csvFileDir);
                }

                return;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                confirmAlert.getButtonTypes().clear();
                confirmAlert.setAlertType(Alert.AlertType.ERROR);
                confirmAlert.getButtonTypes().add(ButtonType.CANCEL);
                confirmAlert.getButtonTypes().add(ButtonType.OK);

                confirmAlert.setTitle(ERROR_CONVERT_TITLE);
                confirmAlert.setHeaderText(ERROR_CONVERT_HEADER);
                confirmAlert.setContentText(ERROR_CONVERT_CONTENT);
                updateLangAlert(confirmAlert);
                Optional<ButtonType> result = confirmAlert.showAndWait();

                confirmAlert.setAlertType(Alert.AlertType.CONFIRMATION);

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    File fileSelected2 = getPdfFile();

                    if (fileSelected2 == null) {
                        return;
                    }
                } else {
                    return;
                }
            }
        }

    }

    private void updateLangAlert(Alert alert) {
        updateLangInBackground(languages.getSelectedToggle(), FXCollections.observableArrayList(alert));
    }

    @FXML
    public void openDirCsv(ActionEvent actionEvent) {
        File csvFileDir = new File(linkCvsDir.getText());
        if (csvFileDir.isDirectory()) {
            try {
                Desktop.getDesktop().open(csvFileDir);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                confirmAlert.setAlertType(Alert.AlertType.ERROR);
                confirmAlert.setTitle(ERROR_OPEN_CSV_DIR_TITLE);
                confirmAlert.setHeaderText(e.getMessage());
                confirmAlert.setContentText("");
                updateLangAlert(confirmAlert);
                confirmAlert.showAndWait();
                confirmAlert.setAlertType(Alert.AlertType.CONFIRMATION);
            }
        } else {
            confirmAlert.setAlertType(Alert.AlertType.ERROR);
            confirmAlert.setTitle(ERROR_OPEN_CSV_DIR_TITLE);
            confirmAlert.setHeaderText(ERROR_CSV_DIR_HEADER);
            confirmAlert.setContentText("");
            updateLangAlert(confirmAlert);
            confirmAlert.showAndWait();
            confirmAlert.setAlertType(Alert.AlertType.CONFIRMATION);
        }

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
            } else if (control instanceof Alert alert) {
                String title = alert.getTitle();
                String header = alert.getHeaderText();
                String content = alert.getContentText();

                String keyTitle = languageMap.get(title);
                String keyHeader = languageMap.get(header);
                String keyContent = languageMap.get(content);

                if (keyTitle != null) {
                    alert.setTitle(bundle.getString(keyTitle + "." + lang));
                }

                if (keyHeader != null) {
                    alert.setHeaderText(bundle.getString(keyHeader + "." + lang));
                }

                if (keyContent != null) {
                    alert.setContentText(bundle.getString(keyContent + "." + lang));
                }
            } else if (control instanceof Stage stage) {
                String title = stage.getTitle();
                String keyTitle = languageMap.get(title);
                if (keyTitle != null) {
                    stage.setTitle(bundle.getString(keyTitle + "." + lang));
                }
            }
        }
    }

    private void copyContentToClipBoard(String content) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        clipboard.setContent(clipboardContent);
    }

    public void copyLinkCsvDir(ActionEvent actionEvent) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> copylinkCsvFolder());
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

    }

    private void copylinkCsvFolder() {

        File csvFileDir = new File(linkCvsDir.getText());
        if (csvFileDir.isDirectory()) {
            copyContentToClipBoard(csvFileDir.getAbsolutePath());

            copyLinkStatusLabel.setVisible(true);
            // Tạo Timeline để ẩn Label sau 3 giây
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(3),
                    event -> copyLinkStatusLabel.setVisible(false) // Ẩn Label sau 3 giây
            ));
            // Chạy Timeline một lần
            timeline.setCycleCount(1);
            timeline.play();
        } else {
            confirmAlert.setAlertType(Alert.AlertType.ERROR);
            confirmAlert.setTitle(ERROR_COPY_CSV_DIR_TITLE);
            confirmAlert.setHeaderText(ERROR_CSV_DIR_HEADER);
            confirmAlert.setContentText(ERROR_COPY_CSV_DIR_CONTENT);
            updateLangAlert(confirmAlert);
            confirmAlert.showAndWait();
            confirmAlert.setAlertType(Alert.AlertType.CONFIRMATION);
        }

    }

    private void setupCellCsvFIleList() {
        /*gọi hàm setCellFactory để cài đặt lại các thuộc tính của ListView
            tham số là 1 FunctionalInterface Callback, ta sẽ tạo lớp ẩn danh của
            Interface này để Override method call của nó
            cần xác định các thuộc tính để Callback truyền vào cho method call bằng generics với
            2 thuộc tính lần này là ListView<CsvFile> và ListCell<CsvFile>*/
        csvFIleList.setCellFactory(new Callback<ListView<CsvFile>, ListCell<CsvFile>>() {
            @Override
            public ListCell<CsvFile> call(ListView<CsvFile> CsvFileListView) {

                 /*các ListCell là các phần tử con hay các hàng của list nó extends Labeled
                 nên có thể định dạng cho nó giống Labeled như màu sắc
                 ListCell không phải Interface nhưng ta vẫn tạo lớp ẩn danh kế thừa lớp này và
                 Override method updateItem của nó*/
                ListCell<CsvFile> cell = new ListCell<CsvFile>() {
                    @Override
                    protected void updateItem(CsvFile CsvFile, boolean empty) {
                        //vẫn giữ lại các cài đặt của lớp cha, chỉ cần sửa một vài giá trị
                        super.updateItem(CsvFile, empty);

                        if (CsvFile != null && !empty) {
                            Label label = new Label(CsvFile.getName());
                            label.setMaxWidth(Double.MAX_VALUE);
                            label.setAlignment(Pos.BASELINE_CENTER);
                            label.setStyle("-fx-font-weight: bold; -fx-background-color: #DCEDC8; -fx-padding: 3 3 3 3");
                            label.setTextFill(Color.BLUE);
                            setGraphic(label);
                        } else {
                            setGraphic(null);
                        }
                    }
                };

                //trả về lớp ẩn danh kế thừa cell trên vừa Override lại các updateItem của nó
                return cell;
            }
        });

    }

    public void closeApp(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    public void openAbout(ActionEvent actionEvent) throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(menuBar.getScene().getWindow());// lấy window đang chạy

        dialog.setTitle("Add new Todo Item");

        // thêm 2 nút ok, cancel vào dialog
        dialog.setResizable(true);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ConVertPdfToExcelCHLController.class.getResource("About.fxml"));// thêm ui fxml

        try {
            dialog.getDialogPane().setContent(loader.load());// liên kết ui fxml vào dialog
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
        }
        Optional<ButtonType> result = dialog.showAndWait();

//        // nếu là nút ok thì thêm item nhập từ dialog vào listview
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            DialogController controller = loader.getController();// lấy controller của ui fxml
//            TodoItem newItem = controller.processResult();// nhận TodoItem từ hàm của controller trả về, hàm này đã thêm item vòa list liên kết với listview
//            todoListView.getSelectionModel().select(newItem);//cho list view chọn item trên
//        } else {
//            System.out.println("cancel");
//        }
//        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("About.fxml"));
//        Parent root = (Parent)loader.load();
//        Stage stage = new Stage();
//        stage.initOwner(menuBar.getScene().getWindow());
//        stage.setScene(new Scene(root));
//        stage.setTitle("Update a Contact");
//        stage.show();


    }
}
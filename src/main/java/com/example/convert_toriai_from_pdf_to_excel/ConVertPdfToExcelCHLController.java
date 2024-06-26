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
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import javafx.scene.image.Image;

import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;

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

    private static final String CONFIRM_CHL_FILE_DIR_TITLE = "Xác nhận thư mục chứa các file CHL";
    private static final String CONFIRM_CHL_FILE_DIR_HEADER = "Địa chỉ thư mục chứa các file CHL chưa được xác nhận";
    private static final String CONFIRM_CHL_FILE_DIR_CONTENT = "Hãy chọn thư mục chứa để tiếp tục!";

    private static final String CONFIRM_CONVERT_COMPLETE_TITLE = "Thông tin hoạt động chuyển file";
    private static final String CONFIRM_CONVERT_COMPLETE_HEADER = "Đã chuyển xong file PDF sang các file CHL";
    private static final String CONFIRM_CONVERT_COMPLETE_CONTENT = "Bạn có muốn mở thư mục chứa các file CHL và\ntự động copy địa chỉ không?";

    private static final String ERROR_CONVERT_TITLE = "Thông báo lỗi chuyển file";
    private static final String ERROR_CONVERT_HEADER = "Nội dung file PDF không phải là tính toán vật liệu hoặc file không được phép truy cập";
    private static final String ERROR_CONVERT_CONTENT = "Bạn có muốn chọn file khác và thực hiện lại không?";

    private static final String ERROR_OPEN_CHL_DIR_TITLE = "Lỗi mở thư mục";
    private static final String ERROR_COPY_CHL_DIR_TITLE = "Lỗi copy địa chỉ thư mục";
    private static final String ERROR_CHL_DIR_HEADER = "Thư mục chứa các file CHL có địa chỉ không đúng hoặc chưa được chọn!";
    private static final String ERROR_COPY_CHL_DIR_CONTENT = "Không thể copy địa chỉ thư mục chứa các file CHL";


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("link csv " + SetupData.getInstance().getSetup().getLinkSaveCvsFileDir());
        System.out.println("old link file " + SetupData.getInstance().getSetup().getLinkPdfFile());
        System.out.println("old" + SetupData.getInstance().getSetup().getLinkPdfFile());

        csvFIleList.setItems(SetupData.getInstance().getChlFiles());
        setupCellChlFIleList();

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

        if (file != null && file.isFile()) {
            String link = file.getAbsolutePath();
            // nếu địa chỉ link của file được chọn khác với địa chỉ cũ đang được chọn thì xóa danh sách list các file csv
            if (!link.equals(SetupData.getInstance().getSetup().getLinkPdfFile())) {
                SetupData.getInstance().getChlFiles().clear();
            }
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
    public File setSaveChlFileDir() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String oldDir = SetupData.getInstance().getSetup().getLinkSaveCvsFileDir();
        File oldFileDir = new File(oldDir);
        if (oldFileDir.isDirectory()) {
            directoryChooser.setInitialDirectory(oldFileDir);
        }

        File dir = directoryChooser.showDialog(menuBar.getScene().getWindow());

        if (dir != null && dir.isDirectory()) {

            String link = dir.getAbsolutePath();

            // nếu địa chỉ link của thư mục được chọn khác với địa chỉ cũ đang được chọn thì xóa danh sách list các file csv
            if (!link.equals(SetupData.getInstance().getSetup().getLinkSaveCvsFileDir())) {
                SetupData.getInstance().getChlFiles().clear();
            }

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
        String chlFileDirPath;

        // yêu cầu chọn địa chỉ file và thư mục khi 2 địa chỉ này chưa được chọn
        // nếu chọn xong thì phải chuyển dữ liệu thành công thì mới thoát được vòng lặp
        while (true) {
            pdfFilePath = linkPdfFile.getText();
            chlFileDirPath = linkCvsDir.getText();

            File pdfFile = new File(pdfFilePath);
            File chlFileDir = new File(chlFileDirPath);

            boolean isFilePDF = pdfFile.isFile();
            boolean isDir = chlFileDir.isDirectory();

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
                confirmAlert.setTitle(CONFIRM_CHL_FILE_DIR_TITLE);
                confirmAlert.setHeaderText(CONFIRM_CHL_FILE_DIR_HEADER);
                confirmAlert.setContentText(CONFIRM_CHL_FILE_DIR_CONTENT);
                updateLangAlert(confirmAlert);
                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    File dirSelected = setSaveChlFileDir();

                    if (dirSelected == null) {
                        return;
                    }

                    chlFileDir = dirSelected;

                    if (!chlFileDir.isDirectory()) {
                        continue;
                    }
                } else {
                    return;
                }
            }

            if (pdfFile.isFile() && chlFileDir.isDirectory()) {
                System.out.println("đã chọn xong 2 địa chỉ");
                System.out.println(pdfFile.getAbsolutePath());
                System.out.println(chlFileDir.getAbsolutePath());
            }

            try {
                ReadPDFToExcel.convertPDFToExcel(pdfFile.getAbsolutePath(), chlFileDir.getAbsolutePath(), SetupData.getInstance().getChlFiles());
                confirmAlert.setTitle(CONFIRM_CONVERT_COMPLETE_TITLE);
                confirmAlert.setHeaderText(CONFIRM_CONVERT_COMPLETE_HEADER);
                confirmAlert.setContentText(CONFIRM_CONVERT_COMPLETE_CONTENT);

                updateLangAlert(confirmAlert);

                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    copyContentToClipBoard(chlFileDir.getAbsolutePath());
                    Desktop.getDesktop().open(chlFileDir);
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

                // nếu là sự kiện không ghi được file chl do file trùng tên với file sắp tạo đang được mở
                // th in ra cảnh báo
                if (e instanceof FileNotFoundException) {
                    confirmAlert.getButtonTypes().clear();
                    confirmAlert.getButtonTypes().add(ButtonType.OK);
                    confirmAlert.setHeaderText("Tên file CHL đang tạo: (\"" + ReadPDFToExcel.fileName +  "\") trùng tên với 1 file CHL khác đang được mở nên không thể ghi đè");
                    confirmAlert.setContentText("Hãy đóng file CHL đang mở để tiếp tục!");
                    System.out.println("File đang được mở bởi người dùng khác");
                    updateLangAlert(confirmAlert);
                    confirmAlert.showAndWait();

                    confirmAlert.setAlertType(Alert.AlertType.CONFIRMATION);
                    confirmAlert.getButtonTypes().add(ButtonType.CANCEL);

                    return;
                }
                if (e instanceof TimeoutException) {
                    confirmAlert.getButtonTypes().clear();
                    confirmAlert.getButtonTypes().add(ButtonType.OK);
                    confirmAlert.setHeaderText("File CHL đang tạo: (\"" + ReadPDFToExcel.fileName +  "\") có số dòng sản phẩm cần ghi lớn hơn 99 nên không thể ghi");
                    confirmAlert.setContentText("Hãy chỉnh sửa lại dữ liệu vật liệu đang chuyển để tiếp tục!");
                    System.out.println("Vật liệu có số dòng lớn hơn 99");
                    updateLangAlert(confirmAlert);
                    confirmAlert.showAndWait();

                    confirmAlert.setAlertType(Alert.AlertType.CONFIRMATION);
                    confirmAlert.getButtonTypes().add(ButtonType.CANCEL);

                    return;
                }



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
    public void openDirChl(ActionEvent actionEvent) {
        File chlFileDir = new File(linkCvsDir.getText());
        if (chlFileDir.isDirectory()) {
            try {
                Desktop.getDesktop().open(chlFileDir);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                confirmAlert.setAlertType(Alert.AlertType.ERROR);
                confirmAlert.setTitle(ERROR_OPEN_CHL_DIR_TITLE);
                confirmAlert.setHeaderText(e.getMessage());
                confirmAlert.setContentText("");
                updateLangAlert(confirmAlert);
                confirmAlert.showAndWait();
                confirmAlert.setAlertType(Alert.AlertType.CONFIRMATION);
            }
        } else {
            confirmAlert.setAlertType(Alert.AlertType.ERROR);
            confirmAlert.setTitle(ERROR_OPEN_CHL_DIR_TITLE);
            confirmAlert.setHeaderText(ERROR_CHL_DIR_HEADER);
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

    public void updateLangInBackground(Toggle langBtn, ObservableList<Object> controls) {
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
            } else if (control instanceof MenuBar menuBar1) {
                for (Menu menu : menuBar1.getMenus()) {
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

                String fileName = "";
                // nếu header có .sysc2 tức là trong tên có tên file đang tạo bị lỗi hoặc file có số dòng lớn hơn 99
                // ở sự kiện tên file sắp tạo trùng tên với file đang mở
                // tách tên file ra ghi vào fileName
                // chỉ lấy phần cố định thêm "" vào giữa gán cho header
                // phần cố định sẽ có trong map languageMap và lấy được keyHeader trong languageMap
                // từ keyHeader lấy được ngôn ngữ đang dùng trong bundle
                // phần tách tiếp ngôn ngữ chia 2 nửa tại điểm " rồi thêm " + fileName + " vào giữa để hiển thị hoàn chỉnh theo ngôn ngữ này
                if (header.contains(".sysc2")) {
                    String[] headerarr = header.split("\"");
                    fileName = headerarr[1];
                    header = headerarr[0] + "\"\"" + headerarr[2];
                }

                String keyTitle = languageMap.get(title);
                String keyHeader = languageMap.get(header);
                String keyContent = languageMap.get(content);


                if (keyTitle != null) {
                    alert.setTitle(bundle.getString(keyTitle + "." + lang));
                }

                if (keyHeader != null) {
                    if (fileName.isBlank()) {
                        alert.setHeaderText(bundle.getString(keyHeader + "." + lang));
                    } else {
                        String[] headerArr = bundle.getString(keyHeader + "." + lang).split("\"");
                        alert.setHeaderText(headerArr[0] + "\"" + fileName + "\"" + headerArr[2]);

                    }
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
            } else if (control instanceof Dialog dialog) {
                String title = dialog.getTitle();
                String keyTitle = languageMap.get(title);
                if (keyTitle != null) {
                    dialog.setTitle(bundle.getString(keyTitle + "." + lang));
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

    public void copyLinkChlDir(ActionEvent actionEvent) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> copylinkChlFolder());
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

    }

    private void copylinkChlFolder() {

        File chlFileDir = new File(linkCvsDir.getText());
        if (chlFileDir.isDirectory()) {
            copyContentToClipBoard(chlFileDir.getAbsolutePath());

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
            confirmAlert.setTitle(ERROR_COPY_CHL_DIR_TITLE);
            confirmAlert.setHeaderText(ERROR_CHL_DIR_HEADER);
            confirmAlert.setContentText(ERROR_COPY_CHL_DIR_CONTENT);
            updateLangAlert(confirmAlert);
            confirmAlert.showAndWait();
            confirmAlert.setAlertType(Alert.AlertType.CONFIRMATION);
        }

    }

    private void setupCellChlFIleList() {
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
                    protected void updateItem(CsvFile csvFile, boolean empty) {
                        //vẫn giữ lại các cài đặt của lớp cha, chỉ cần sửa một vài giá trị
                        super.updateItem(csvFile, empty);

                        if (csvFile != null && !empty) {
                            Label label = new Label(csvFile.getName());
//                            label.setMaxWidth(Double.MAX_VALUE);
                            label.setAlignment(Pos.CENTER);
//                            label.setStyle("-fx-font-weight: bold; -fx-background-color: #DCEDC8; -fx-padding: 3 3 3 3");
                            label.setTextFill(Color.BLUE);

                            HBox hBox = new HBox();
                            hBox.setAlignment(Pos.CENTER);
                            hBox.setMaxWidth(Double.MAX_VALUE);
                            hBox.setStyle("-fx-font-weight: bold; -fx-background-color: #DCEDC8; -fx-padding: 3 3 3 3");

                            Class<ConVertPdfToExcelCHLController> clazz = ConVertPdfToExcelCHLController.class;
                            InputStream input = clazz.getResourceAsStream("/com/example/convert_toriai_from_pdf_to_excel/ICON/ok.png");

                            String koSyuName = csvFile.getKouSyuName();
                            if (koSyuName.equalsIgnoreCase("[")) {
                                input = clazz.getResourceAsStream("/com/example/convert_toriai_from_pdf_to_excel/ICON/U.png");
                            } else if (koSyuName.equalsIgnoreCase("C")) {
                                input = clazz.getResourceAsStream("/com/example/convert_toriai_from_pdf_to_excel/ICON/C.png");
                            } else if (koSyuName.equalsIgnoreCase("K")) {
                                input = clazz.getResourceAsStream("/com/example/convert_toriai_from_pdf_to_excel/ICON/P.png");
                            } else if (koSyuName.equalsIgnoreCase("L")) {
                                input = clazz.getResourceAsStream("/com/example/convert_toriai_from_pdf_to_excel/ICON/L.png");
                            } else if (koSyuName.equalsIgnoreCase("H")) {
                                input = clazz.getResourceAsStream("/com/example/convert_toriai_from_pdf_to_excel/ICON/H.png");
                            } else if (koSyuName.equalsIgnoreCase("FB")) {
                                input = clazz.getResourceAsStream("/com/example/convert_toriai_from_pdf_to_excel/ICON/FB.png");
                            } else if (koSyuName.equalsIgnoreCase("CA")) {
                                input = clazz.getResourceAsStream("/com/example/convert_toriai_from_pdf_to_excel/ICON/CA.png");
                            }

                            Image image;

                            try {
                                assert input != null;
                                image = new Image(input);
                                ImageView imageView = new ImageView(image);
                                imageView.setFitWidth(25);
                                imageView.setFitHeight(25);

                                hBox.getChildren().add(label);
                                hBox.getChildren().add(imageView);
                                hBox.setSpacing(10);
                                setGraphic(hBox);
                            } catch (NullPointerException e) {
                                System.out.println(e.getMessage());
                            }

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

    public void openAbout(ActionEvent actionEvent) {
        Dialog<Object> dialog = new Dialog<>();
        dialog.initOwner(menuBar.getScene().getWindow());// lấy window đang chạy

        dialog.setTitle("About");

        dialog.setResizable(true);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ConVertPdfToExcelCHLController.class.getResource("/com/example/convert_toriai_from_pdf_to_excel/about.fxml"));// thêm ui fxml

        try {
            dialog.getDialogPane().setContent(loader.load());// liên kết ui fxml vào dialog
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
        }

        AboutController controller = loader.getController();
        controller.init(this, dialog);

        dialog.show();

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
package com.example.convert_toriai_from_pdf_to_excel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

public class AboutController {
    @FXML
    public Button okBtn;
    @FXML
    public Label introduce;
    @FXML
    public Label introduceContent;
    @FXML
    public Label using;
    @FXML
    public Label usingContent;
    @FXML
    public Label creator;

    private ConVertPdfToExcelCHLController conVertPdfToExcelCHLController;

    private Dialog<Object> dialog;

    public void okAbout(ActionEvent actionEvent) {
        dialog.setResult(Boolean.TRUE);
        dialog.close();
    }

    public void init(ConVertPdfToExcelCHLController conVertPdfToExcelCHLController, Dialog<Object> dialog) {
        this.dialog = dialog;

        ObservableList<Object> controls = FXCollections.observableArrayList(okBtn, introduce, introduceContent, using, usingContent, creator);

        conVertPdfToExcelCHLController.updateLangInBackground(conVertPdfToExcelCHLController.languages.getSelectedToggle(), controls);
    }
}

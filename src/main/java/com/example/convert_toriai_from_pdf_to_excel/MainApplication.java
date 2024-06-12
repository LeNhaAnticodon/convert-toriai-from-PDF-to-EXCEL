package com.example.convert_toriai_from_pdf_to_excel;

import com.example.convert_toriai_from_pdf_to_excel.dao.SetupData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("convertPdfToExcelCHL.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Convert PDF To Excel CHL!");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
        try {
            SetupData.getInstance().loadSetup();
        } catch (IOException e) {
            System.out.println("không đọc được file");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
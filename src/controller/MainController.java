package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private void moQuanLySinhVien() {
        openWindow("/view/SinhVienView.fxml", "Quản lý Sinh viên");
    }

    @FXML
    private void moQuanLyMonHoc() {
        openWindow("/view/MonHocView.fxml", "Quản lý Môn học");
    }

    @FXML
    private void moBangDiem() {
        openWindow("/view/DiemView.fxml", "Nhập/Sửa điểm");
    }

    @FXML
    private void moThongKe() {
        openWindow("/view/ThongKeView.fxml", "Báo cáo Kết quả học tập");
    }

    @FXML
    private void thoatChuongTrinh(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Helper method để mở một cửa sổ FXML mới.
     * @param fxmlFile Đường dẫn đến tệp FXML.
     * @param title Tiêu đề của cửa sổ.
     */
    private void openWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(loader.load()));
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Lỗi khi tải tệp FXML: " + fxmlFile);
            e.printStackTrace();
        }
    }
}


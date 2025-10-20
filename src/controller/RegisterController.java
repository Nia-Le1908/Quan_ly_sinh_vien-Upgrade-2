package controller;

import java.io.IOException;
import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

public class RegisterController {

    @FXML private TextField tfUsername;
    @FXML private PasswordField pfPassword;
    @FXML private PasswordField pfConfirmPassword;
    @FXML private ComboBox<String> cbRole;
    @FXML private TextField tfMaSV;
    @FXML private Label lblStatus;

    @FXML
    public void initialize() {
        cbRole.getItems().addAll("GIANGVIEN", "SINHVIEN");
        // Lắng nghe sự thay đổi của ComboBox để ẩn/hiện ô nhập Mã SV
        cbRole.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isStudent = "SINHVIEN".equals(newVal);
            tfMaSV.setVisible(isStudent);
            tfMaSV.setManaged(isStudent);
        });
    }

    @FXML
    void handleRegister(ActionEvent event) {
        String username = tfUsername.getText();
        String password = pfPassword.getText();
        String confirmPassword = pfConfirmPassword.getText();
        String role = cbRole.getValue();
        String maSV = tfMaSV.getText();

        // Kiểm tra dữ liệu đầu vào
        if (username.isEmpty() || password.isEmpty() || role == null) {
            showError("Vui lòng nhập đầy đủ thông tin.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showError("Mật khẩu xác nhận không khớp.");
            return;
        }
        if ("SINHVIEN".equals(role) && maSV.isEmpty()) {
            showError("Vui lòng nhập Mã số sinh viên.");
            return;
        }

        User newUser = new User(username, password, role);
        String result = UserDAO.registerUser(newUser, "SINHVIEN".equals(role) ? maSV : null);
        
        if ("SUCCESS".equals(result)) {
            showSuccess("Đăng ký thành công! Quay lại để đăng nhập.");
        } else {
            showError(result); // Hiển thị thông báo lỗi cụ thể từ DAO
        }
    }

    private void showError(String message) {
        lblStatus.setStyle("-fx-text-fill: red;");
        lblStatus.setText(message);
    }

    private void showSuccess(String message) {
        lblStatus.setStyle("-fx-text-fill: green;");
        lblStatus.setText(message);
    }

    @FXML
    void switchToLogin(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Đăng nhập");
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
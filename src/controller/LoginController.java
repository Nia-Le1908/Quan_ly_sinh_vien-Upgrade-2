package controller;

import java.io.IOException;
import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

public class LoginController {

    @FXML private TextField tfUsername;
    @FXML private PasswordField pfPassword;
    @FXML private Label lblStatus;

    @FXML
    void handleLogin(ActionEvent event) {
        String username = tfUsername.getText();
        String password = pfPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        User user = UserDAO.authenticateUser(username, password);

        if (user != null) {
            // Đăng nhập thành công, chuyển màn hình dựa trên vai trò
            if ("GIANGVIEN".equals(user.getRole())) {
                openWindow("/view/MainView.fxml", "Bảng điều khiển Giảng viên", event);
            } else {
                // Là sinh viên, cần lấy maSV và mở màn hình thống kê
                String maSV = UserDAO.getMaSVByUsername(username);
                openPersonalizedStudentView(maSV, event);
            }
        } else {
            lblStatus.setText("Tên đăng nhập hoặc mật khẩu không đúng.");
        }
    }

    @FXML
    void switchToRegister(ActionEvent event) {
        openWindow("/view/RegisterView.fxml", "Đăng ký tài khoản", event);
    }
    
    private void openWindow(String fxmlPath, String title, ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openPersonalizedStudentView(String maSV, ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ThongKeView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Tra cứu điểm cá nhân");
            stage.setScene(new Scene(loader.load()));
            
            // Truyền maSV cho ThongKeController
            ThongKeController controller = loader.getController();
            controller.initDataForStudent(maSV);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
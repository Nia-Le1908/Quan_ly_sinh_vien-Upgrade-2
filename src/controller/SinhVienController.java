package controller;

import java.time.LocalDate;
import dao.SinhVienDAO;
import dao.UserDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.SinhVien;

public class SinhVienController {

    @FXML private TextField tfMaSV, tfHoTen, tfQueQuan, tfKhoa, tfLop, tfTimKiem;
    @FXML private DatePicker dpNgaySinh;
    @FXML private ComboBox<String> cbGioiTinh;
    @FXML private TableView<SinhVien> tableSV;
    @FXML private TableColumn<SinhVien, String> colMaSV, colHoTen, colGioiTinh, colQueQuan, colKhoa, colLop;
    @FXML private TableColumn<SinhVien, LocalDate> colNgaySinh;
    @FXML private TableColumn<SinhVien, Double> colDiemTichLuy;
    @FXML private Label lblStatus;

    private ObservableList<SinhVien> dsSV;

    @FXML
    public void initialize() {
        System.out.println("✅ SinhVienController initialized");
        cbGioiTinh.getItems().addAll("Nam", "Nữ");
        colMaSV.setCellValueFactory(data -> data.getValue().maSVProperty());
        colHoTen.setCellValueFactory(data -> data.getValue().hoTenProperty());
        colNgaySinh.setCellValueFactory(data -> data.getValue().ngaySinhProperty());
        colGioiTinh.setCellValueFactory(data -> data.getValue().gioiTinhProperty());
        colQueQuan.setCellValueFactory(data -> data.getValue().queQuanProperty());
        colKhoa.setCellValueFactory(data -> data.getValue().khoaProperty());
        colLop.setCellValueFactory(data -> data.getValue().lopProperty());
        colDiemTichLuy.setCellValueFactory(data -> data.getValue().diemTichLuyProperty().asObject());

        // Listener để tự động điền form khi chọn sinh viên
        tableSV.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showSinhVienDetails(newValue));

        loadData();
    }

    private void loadData() {
        dsSV = SinhVienDAO.getAllSinhVien();
        tableSV.setItems(dsSV);
    }

    @FXML
    private void themSinhVien() {
        if(tfMaSV.getText().isEmpty() || tfHoTen.getText().isEmpty()) {
            showError("Mã SV và Họ tên không được để trống.");
            return;
        }

        SinhVien sv = new SinhVien(
                tfMaSV.getText(),
                tfHoTen.getText(),
                dpNgaySinh.getValue(),
                cbGioiTinh.getValue(),
                tfQueQuan.getText(),
                tfKhoa.getText(),
                tfLop.getText()
        );
        if (SinhVienDAO.insert(sv)) {
            loadData();
            clearForm();
            showSuccess("Thêm sinh viên thành công.");
        } else {
            showError("Thêm sinh viên thất bại (có thể trùng Mã SV).");
        }
    }

    @FXML
    private void suaSinhVien() {
        SinhVien sv = tableSV.getSelectionModel().getSelectedItem();
        if (sv != null) {
             if(tfHoTen.getText().isEmpty()) {
                showError("Họ tên không được để trống.");
                return;
            }
            sv.setHoTen(tfHoTen.getText());
            sv.setNgaySinh(dpNgaySinh.getValue());
            sv.setGioiTinh(cbGioiTinh.getValue());
            sv.setQueQuan(tfQueQuan.getText());
            sv.setKhoa(tfKhoa.getText());
            sv.setLop(tfLop.getText());

            if (SinhVienDAO.update(sv)) {
                loadData();
                showSuccess("Cập nhật thành công.");
            } else {
                 showError("Cập nhật thất bại.");
            }
        } else {
            showError("Vui lòng chọn một sinh viên để sửa.");
        }
    }

    @FXML
    private void xoaSinhVien() {
        SinhVien sv = tableSV.getSelectionModel().getSelectedItem();
        if (sv != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText("Bạn có chắc chắn muốn xóa sinh viên " + sv.getHoTen() + "?");
            alert.setContentText("Hành động này sẽ xóa cả tài khoản và toàn bộ điểm của sinh viên này (nếu có).");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (SinhVienDAO.delete(sv.getMaSV())) {
                        loadData();
                        showSuccess("Xóa sinh viên thành công.");
                    } else {
                        showError("Xóa sinh viên thất bại.");
                    }
                }
            });
        } else {
            showError("Vui lòng chọn một sinh viên để xóa.");
        }
    }

    @FXML
    private void handleResetPassword() {
        SinhVien selectedSV = tableSV.getSelectionModel().getSelectedItem();
        if (selectedSV == null) {
            showError("Vui lòng chọn một sinh viên để đặt lại mật khẩu.");
            return;
        }

        String username = SinhVienDAO.getUsernameByMaSV(selectedSV.getMaSV());
        if (username == null || username.isEmpty()) {
            showError("Sinh viên này chưa có tài khoản để đặt lại mật khẩu.");
            return;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Đặt lại mật khẩu cho tài khoản '" + username + "'?");
        alert.setContentText("Mật khẩu sẽ được đặt lại về giá trị mặc định là '123456'.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (UserDAO.resetPassword(username)) {
                    showSuccess("Mật khẩu đã được đặt lại thành công.");
                } else {
                    showError("Đặt lại mật khẩu thất bại.");
                }
            }
        });
    }

    @FXML
    private void timKiemSinhVien() {
        String keyword = tfTimKiem.getText();
        dsSV = SinhVienDAO.search(keyword);
        tableSV.setItems(dsSV);
    }

    private void clearForm() {
        tableSV.getSelectionModel().clearSelection(); // Bỏ chọn dòng
        tfMaSV.clear();
        tfHoTen.clear();
        dpNgaySinh.setValue(null);
        cbGioiTinh.setValue(null);
        tfQueQuan.clear();
        tfKhoa.clear();
        tfLop.clear();
        tfMaSV.setDisable(false);
    }
    
    private void showSinhVienDetails(SinhVien sv) {
        if (sv != null) {
            tfMaSV.setText(sv.getMaSV());
            tfHoTen.setText(sv.getHoTen());
            dpNgaySinh.setValue(sv.getNgaySinh());
            cbGioiTinh.setValue(sv.getGioiTinh());
            tfQueQuan.setText(sv.getQueQuan());
            tfKhoa.setText(sv.getKhoa());
            tfLop.setText(sv.getLop());
            tfMaSV.setDisable(true); // Không cho sửa mã SV
        } else {
            clearForm();
        }
    }
    
    private void showError(String message) {
        lblStatus.setText("Lỗi: " + message);
        lblStatus.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(String message) {
        lblStatus.setText("Thành công: " + message);
        lblStatus.setStyle("-fx-text-fill: green;");
    }
}
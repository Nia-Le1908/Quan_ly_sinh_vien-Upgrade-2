package controller;

import dao.DiemDAO;
import dao.MonHocDAO;
import dao.SinhVienDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import model.BangDiem;
import model.DiemChiTiet;
import model.MonHoc;

import java.util.List;

public class DiemController {

    @FXML private ComboBox<String> cbLop;
    @FXML private ComboBox<MonHoc> cbMonHoc;
    @FXML private Button btnXemDiem, btnCapNhat;
    @FXML private TableView<DiemChiTiet> tableDiem;
    @FXML private TableColumn<DiemChiTiet, String> colMaSV, colHoTen;
    @FXML private TableColumn<DiemChiTiet, Float> colDiemQT, colDiemThi, colDiemTB;
    @FXML private TextField tfMaSV, tfHoTen, tfDiemQT, tfDiemThi;
    @FXML private Label lblStatus;

    @FXML
    public void initialize() {
        // Nạp danh sách lớp và môn học vào các ComboBox.
        cbLop.setItems(FXCollections.observableArrayList(SinhVienDAO.getAllLop()));
        cbMonHoc.setItems(FXCollections.observableArrayList(MonHocDAO.getAllMonHoc()));

        // Thiết lập cách hiển thị cho ComboBox Môn học để hiển thị cả mã và tên.
        cbMonHoc.setConverter(new StringConverter<MonHoc>() {
            @Override
            public String toString(MonHoc object) {
                return object == null ? "" : object.getMaMon() + " - " + object.getTenMon();
            }

            @Override
            public MonHoc fromString(String string) {
                return null; // Không cần thiết cho trường hợp này
            }
        });

        // Cấu hình các cột cho bảng
        colMaSV.setCellValueFactory(new PropertyValueFactory<>("maSV"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colDiemQT.setCellValueFactory(new PropertyValueFactory<>("diemQT"));
        colDiemThi.setCellValueFactory(new PropertyValueFactory<>("diemThi"));
        colDiemTB.setCellValueFactory(new PropertyValueFactory<>("diemTB"));

        // Listener để tự động điền thông tin khi chọn sinh viên trong bảng
        tableDiem.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateFields(newSelection);
                }
            }
        );
    }

    /**
     * Xử lý sự kiện nhấn nút "Xem điểm".
     */
    @FXML
    private void locDiem() {
        String lop = cbLop.getValue();
        MonHoc monHoc = cbMonHoc.getValue();

        if (lop == null || monHoc == null) {
            showStatus("Lỗi: Vui lòng chọn đầy đủ lớp và môn học.", true);
            tableDiem.setItems(FXCollections.observableArrayList()); // Xóa dữ liệu cũ
            return;
        }

        List<DiemChiTiet> dsDiem = DiemDAO.getDiemByLopAndMon(lop, monHoc.getMaMon());
        tableDiem.setItems(FXCollections.observableArrayList(dsDiem));

        if (dsDiem.isEmpty()) {
            showStatus("Không tìm thấy sinh viên nào trong lớp đã chọn.", true);
        } else {
            showStatus("Đã tải " + dsDiem.size() + " sinh viên. Vui lòng chọn sinh viên để nhập điểm.", false);
        }
        clearFields();
    }

    /**
     * Xử lý sự kiện nhấn nút "Cập nhật".
     */
    @FXML
    private void capNhatDiem() {
        DiemChiTiet selected = tableDiem.getSelectionModel().getSelectedItem();
        MonHoc selectedMonHoc = cbMonHoc.getValue();

        if (selected == null || selectedMonHoc == null) {
            showStatus("Lỗi: Vui lòng chọn sinh viên và môn học trước khi cập nhật.", true);
            return;
        }

        try {
            float diemQT = Float.parseFloat(tfDiemQT.getText());
            float diemThi = Float.parseFloat(tfDiemThi.getText());
            
            if (diemQT < 0 || diemQT > 10 || diemThi < 0 || diemThi > 10) {
                 showStatus("Lỗi: Điểm phải nằm trong khoảng từ 0 đến 10.", true);
                 return;
            }

            BangDiem diemMoi = new BangDiem(selected.getMaSV(), selectedMonHoc.getMaMon(), diemQT, diemThi, 0);
            if (DiemDAO.upsertDiem(diemMoi)) {
                showStatus("Cập nhật điểm thành công!", false);
                locDiem(); // Tải lại bảng điểm để xem kết quả
            } else {
                showStatus("Lỗi: Cập nhật điểm thất bại.", true);
            }

        } catch (NumberFormatException e) {
            showStatus("Lỗi: Vui lòng nhập điểm là một con số hợp lệ.", true);
        }
    }

    /**
     * Tự động điền thông tin vào các ô nhập liệu.
     */
    private void populateFields(DiemChiTiet diem) {
        tfMaSV.setText(diem.getMaSV());
        tfHoTen.setText(diem.getHoTen());
        tfDiemQT.setText(String.valueOf(diem.getDiemQT()));
        tfDiemThi.setText(String.valueOf(diem.getDiemThi()));
    }

    /**
     * Xóa trắng các ô nhập liệu.
     */
    private void clearFields() {
        tfMaSV.clear();
        tfHoTen.clear();
        tfDiemQT.clear();
        tfDiemThi.clear();
        tableDiem.getSelectionModel().clearSelection();
    }
    
    /**
     * Hiển thị thông báo trạng thái.
     */
    private void showStatus(String message, boolean isError) {
        lblStatus.setText(message);
        lblStatus.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }
}
package controller;

import dao.MonHocDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.MonHoc;

public class MonHocController {

    @FXML private TableView<MonHoc> tableMonHoc;
    @FXML private TableColumn<MonHoc, String> colMaMon, colTenMon, colLoaiMon, colMonTienQuyet;
    @FXML private TableColumn<MonHoc, Integer> colSoTinChi, colHocKy;
    @FXML private TextField tfMaMon, tfTenMon, tfSoTinChi, tfHocKy, tfMonTienQuyet, tfTimKiem;
    @FXML private ComboBox<String> cbLoaiMon;
    @FXML private Label lblStatus; // Thêm Label để hiển thị thông báo

    private ObservableList<MonHoc> list;

    @FXML
    public void initialize() {
        cbLoaiMon.setItems(FXCollections.observableArrayList("Bắt buộc", "Tự chọn"));
        colMaMon.setCellValueFactory(data -> data.getValue().maMonProperty());
        colTenMon.setCellValueFactory(data -> data.getValue().tenMonProperty());
        colSoTinChi.setCellValueFactory(data -> data.getValue().soTinChiProperty().asObject());
        colHocKy.setCellValueFactory(data -> data.getValue().hocKyProperty().asObject());
        colLoaiMon.setCellValueFactory(data -> data.getValue().loaiMonProperty());
        colMonTienQuyet.setCellValueFactory(data -> data.getValue().monTienQuyetProperty());

        // Thêm listener để tự động điền thông tin khi chọn một dòng
        tableMonHoc.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateFields(newSelection);
                }
            }
        );

        loadData();
    }
    
    /**
     * Tải dữ liệu môn học từ CSDL và hiển thị lên TableView.
     */
    private void loadData() {
        list = FXCollections.observableArrayList(MonHocDAO.getAllMonHoc());
        tableMonHoc.setItems(list);
    }

    /**
     * Xử lý sự kiện thêm môn học mới.
     */
    @FXML
    private void themMonHoc() {
        // --- B1: Kiểm tra dữ liệu đầu vào ---
        if (!validateInput()) {
            return; // Dừng lại nếu dữ liệu không hợp lệ
        }
        
        // --- B2: Kiểm tra mã môn học đã tồn tại chưa ---
        String maMon = tfMaMon.getText().trim();
        if (list.stream().anyMatch(m -> m.getMaMon().equalsIgnoreCase(maMon))) {
            showStatus("Lỗi: Mã môn học đã tồn tại!", true);
            return;
        }

        // --- B3: Thêm vào CSDL ---
        try {
            MonHoc m = new MonHoc(
                    maMon,
                    tfTenMon.getText().trim(),
                    Integer.parseInt(tfSoTinChi.getText().trim()),
                    cbLoaiMon.getValue(),
                    tfMonTienQuyet.getText().trim(),
                    Integer.parseInt(tfHocKy.getText().trim())
            );
            MonHocDAO.insert(m);
            loadData(); // Tải lại dữ liệu
            clearFields(); // Xóa trắng các ô nhập
            showStatus("Thêm môn học thành công!", false);
        } catch (NumberFormatException e) {
            showStatus("Lỗi: Số tín chỉ và học kỳ phải là số nguyên.", true);
        }
    }

    /**
     * Xử lý sự kiện sửa thông tin môn học.
     */
    @FXML
    private void suaMonHoc() {
        MonHoc selected = tableMonHoc.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus("Lỗi: Vui lòng chọn một môn học để sửa.", true);
            return;
        }

        if (!validateInput()) {
            return;
        }
        
        try {
            selected.setTenMon(tfTenMon.getText().trim());
            selected.setSoTinChi(Integer.parseInt(tfSoTinChi.getText().trim()));
            selected.setLoaiMon(cbLoaiMon.getValue());
            selected.setMonTienQuyet(tfMonTienQuyet.getText().trim());
            selected.setHocKy(Integer.parseInt(tfHocKy.getText().trim()));

            MonHocDAO.update(selected);
            loadData();
            clearFields();
            showStatus("Cập nhật môn học thành công!", false);
        } catch (NumberFormatException e) {
             showStatus("Lỗi: Số tín chỉ và học kỳ phải là số nguyên.", true);
        }
    }

    /**
     * Xử lý sự kiện xóa môn học.
     */
    @FXML
    private void xoaMonHoc() {
        MonHoc selected = tableMonHoc.getSelectionModel().getSelectedItem();
        if (selected != null) {
            MonHocDAO.delete(selected.getMaMon());
            loadData();
            clearFields();
            showStatus("Xóa môn học thành công!", false);
        } else {
            showStatus("Lỗi: Vui lòng chọn một môn học để xóa.", true);
        }
    }

    /**
     * Xử lý sự kiện tìm kiếm môn học.
     */
    @FXML
    private void timKiemMonHoc() {
        String keyword = tfTimKiem.getText();
        list = FXCollections.observableArrayList(MonHocDAO.search(keyword));
        tableMonHoc.setItems(list);
    }
    
    /**
     * Tự động điền thông tin của môn học được chọn vào các ô nhập liệu.
     * @param monHoc Môn học được chọn từ bảng.
     */
    private void populateFields(MonHoc monHoc) {
        tfMaMon.setText(monHoc.getMaMon());
        tfTenMon.setText(monHoc.getTenMon());
        tfSoTinChi.setText(String.valueOf(monHoc.getSoTinChi()));
        tfHocKy.setText(String.valueOf(monHoc.getHocKy()));
        cbLoaiMon.setValue(monHoc.getLoaiMon());
        tfMonTienQuyet.setText(monHoc.getMonTienQuyet());
        tfMaMon.setDisable(true); // Không cho phép sửa mã môn (khóa chính)
        lblStatus.setText("");
    }

    /**
     * Xóa trắng các ô nhập liệu và bỏ chọn trên bảng.
     */
    @FXML
    private void clearFields() {
        tfMaMon.clear();
        tfTenMon.clear();
        tfSoTinChi.clear();
        tfHocKy.clear();
        cbLoaiMon.setValue(null);
        tfMonTienQuyet.clear();
        tfTimKiem.clear();
        tfMaMon.setDisable(false); // Cho phép nhập lại mã môn
        lblStatus.setText("");
        tableMonHoc.getSelectionModel().clearSelection();
    }
    
    /**
     * Hiển thị thông báo trạng thái cho người dùng.
     * @param message Nội dung thông báo.
     * @param isError true nếu là thông báo lỗi (màu đỏ), false nếu là thông báo thành công (màu xanh).
     */
    private void showStatus(String message, boolean isError) {
        lblStatus.setText(message);
        if (isError) {
            lblStatus.setStyle("-fx-text-fill: red;");
        } else {
            lblStatus.setStyle("-fx-text-fill: green;");
        }
    }
    
    /**
     * Kiểm tra các ô nhập liệu có hợp lệ không.
     * @return true nếu hợp lệ, false nếu không hợp lệ.
     */
    private boolean validateInput() {
        String maMon = tfMaMon.getText();
        String tenMon = tfTenMon.getText();
        String soTinChiText = tfSoTinChi.getText();
        String hocKyText = tfHocKy.getText();

        if (maMon.trim().isEmpty() || tenMon.trim().isEmpty() || soTinChiText.trim().isEmpty() || hocKyText.trim().isEmpty() || cbLoaiMon.getValue() == null) {
            showStatus("Lỗi: Vui lòng nhập đầy đủ thông tin bắt buộc.", true);
            return false;
        }
        return true;
    }
}
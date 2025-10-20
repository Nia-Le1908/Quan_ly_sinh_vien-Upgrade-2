package controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dao.SinhVienDAO;
import dao.ThongKeDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.DiemChiTiet;
import model.SinhVien;

public class ThongKeController {

    @FXML private ComboBox<SinhVien> cbSinhVien;
    @FXML private TabPane tabPane; // Sử dụng TabPane để chứa các kỳ học
    @FXML private HBox selectionHBox;

    @FXML
    public void initialize() {
        cbSinhVien.setItems(SinhVienDAO.getAllSinhVien());
    }
    
    /**
     * Khởi tạo dữ liệu cho giao diện xem điểm của sinh viên.
     * @param maSV Mã sinh viên
     */
    public void initDataForStudent(String maSV) {
        selectionHBox.setVisible(false); // Ẩn phần chọn sinh viên
        // Tạo một đối tượng SinhVien tạm để chọn trong ComboBox
        SinhVien sv = SinhVienDAO.getAllSinhVien().stream()
                        .filter(s -> s.getMaSV().equals(maSV))
                        .findFirst().orElse(null);
        if (sv != null) {
            cbSinhVien.setValue(sv);
            xemThongKe();
        }
    }

    @FXML
    private void xemThongKe() {
        SinhVien selectedSV = cbSinhVien.getValue();
        if (selectedSV == null) {
            return;
        }

        List<DiemChiTiet> dsDiem = ThongKeDAO.getDiemChiTiet(selectedSV.getMaSV());
        taoGiaoDienBaoCao(dsDiem);
    }
    
    /**
     * Tự động tạo giao diện báo cáo điểm theo từng Tab học kỳ.
     * @param dsDiem Danh sách điểm của sinh viên
     */
    private void taoGiaoDienBaoCao(List<DiemChiTiet> dsDiem) {
        tabPane.getTabs().clear();

        if (dsDiem.isEmpty()) {
            Tab emptyTab = new Tab("Thông báo");
            emptyTab.setClosable(false);
            emptyTab.setContent(new Label("Sinh viên này chưa có điểm."));
            tabPane.getTabs().add(emptyTab);
            return;
        }

        Map<Integer, List<DiemChiTiet>> diemTheoKy = dsDiem.stream()
            .collect(Collectors.groupingBy(DiemChiTiet::getHocKy));

        double tongDiemTichLuyHe4 = 0;
        int tongTinChiTichLuy = 0;
        int tongTinChiDaHoc = 0;

        // Sắp xếp các kỳ và tạo Tab cho mỗi kỳ
        List<Integer> sortedKeys = diemTheoKy.keySet().stream().sorted().collect(Collectors.toList());

        for (int hocKy : sortedKeys) {
            List<DiemChiTiet> diemKyNay = diemTheoKy.get(hocKy);
            
            // --- Tính toán cho kỳ hiện tại ---
            double tongDiemHe4KyNay = 0;
            int tongTinChiKyNay = 0;
            
            for (DiemChiTiet diem : diemKyNay) {
                tongTinChiKyNay += diem.getSoTinChi();
                if (diem.getDiemTB() >= 4.0) { // Chỉ tính môn qua
                    tongDiemHe4KyNay += ThongKeDAO.convertToScale4(diem.getDiemTB()) * diem.getSoTinChi();
                }
            }

            double gpaKyNay = (tongTinChiKyNay == 0) ? 0.0 : tongDiemHe4KyNay / tongTinChiKyNay;
            
            // --- Cập nhật điểm tích lũy ---
            tongDiemTichLuyHe4 += tongDiemHe4KyNay;
            tongTinChiTichLuy += tongTinChiKyNay;
            tongTinChiDaHoc += tongTinChiKyNay;

            // --- Tạo giao diện cho Tab ---
            Tab tab = new Tab("Học kỳ " + hocKy);
            VBox tabContent = new VBox(15);
            tabContent.setPadding(new Insets(15));

            TableView<DiemChiTiet> tableView = taoBangDiem(); // Tạo bảng mới với cột mới
            tableView.setItems(FXCollections.observableArrayList(diemKyNay));

            Label lblGpa = new Label(String.format("Điểm trung bình học kỳ (Hệ 4): %.2f", gpaKyNay));
            lblGpa.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            tabContent.getChildren().addAll(tableView, lblGpa);
            tab.setContent(tabContent);
            tabPane.getTabs().add(tab);
        }
        
        // --- Tạo Tab Tổng kết ---
        Tab summaryTab = new Tab("Tổng kết");
        VBox summaryContent = new VBox(10);
        summaryContent.setPadding(new Insets(20));
        
        double gpaTichLuyToanKhoa = (tongTinChiTichLuy == 0) ? 0.0 : tongDiemTichLuyHe4 / tongTinChiTichLuy;

        Label title = new Label("KẾT QUẢ HỌC TẬP TOÀN KHÓA");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label lblTongTinChiDaHoc = new Label("Tổng số tín chỉ đã đăng ký: " + tongTinChiDaHoc);
        Label lblTongTinChiTichLuy = new Label("Tổng số tín chỉ tích lũy: " + tongTinChiTichLuy);
        Label lblGpaTichLuy = new Label(String.format("Điểm trung bình tích lũy (Hệ 4): %.2f", gpaTichLuyToanKhoa));
        
        // Thêm định dạng cho các label tổng kết
        lblTongTinChiDaHoc.setStyle("-fx-font-size: 14px;");
        lblTongTinChiTichLuy.setStyle("-fx-font-size: 14px;");
        lblGpaTichLuy.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        summaryContent.getChildren().addAll(title, lblTongTinChiDaHoc, lblTongTinChiTichLuy, lblGpaTichLuy);
        summaryTab.setContent(summaryContent);
        tabPane.getTabs().add(summaryTab);
    }
    
    /**
     * Tạo một TableView mới với các cột được cấu hình đầy đủ.
     * @return Một đối tượng TableView mới.
     */
    @SuppressWarnings("unchecked")
    private TableView<DiemChiTiet> taoBangDiem() {
        TableView<DiemChiTiet> tableView = new TableView<>();

        // Tạo các cột mới mỗi lần gọi hàm
        TableColumn<DiemChiTiet, String> colMaMon = new TableColumn<>("Mã Môn");
        TableColumn<DiemChiTiet, String> colTenMon = new TableColumn<>("Tên Môn Học");
        TableColumn<DiemChiTiet, Integer> colSoTinChi = new TableColumn<>("Số TC");
        TableColumn<DiemChiTiet, Float> colDiemQT = new TableColumn<>("Điểm QT");
        TableColumn<DiemChiTiet, Float> colDiemThi = new TableColumn<>("Điểm Thi");
        TableColumn<DiemChiTiet, Float> colDiemTB = new TableColumn<>("Điểm TB (10)");
        TableColumn<DiemChiTiet, String> colDiemChu = new TableColumn<>("Điểm Chữ");

        // Liên kết dữ liệu với các cột mới
        colMaMon.setCellValueFactory(new PropertyValueFactory<>("maMon"));
        colTenMon.setCellValueFactory(new PropertyValueFactory<>("tenMon"));
        colSoTinChi.setCellValueFactory(new PropertyValueFactory<>("soTinChi"));
        colDiemQT.setCellValueFactory(new PropertyValueFactory<>("diemQT"));
        colDiemThi.setCellValueFactory(new PropertyValueFactory<>("diemThi"));
        colDiemTB.setCellValueFactory(new PropertyValueFactory<>("diemTB"));
        colDiemChu.setCellValueFactory(new PropertyValueFactory<>("diemChu"));
        
        // Thiết lập độ rộng cột
        colTenMon.setPrefWidth(250);
        colMaMon.setPrefWidth(100);
        
        tableView.getColumns().addAll(colMaMon, colTenMon, colSoTinChi, colDiemQT, colDiemThi, colDiemTB, colDiemChu);
        return tableView;
    }
}

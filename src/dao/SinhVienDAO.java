package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.SinhVien;
import util.DBConnection;

public class SinhVienDAO {

    // ✅ Lấy toàn bộ danh sách sinh viên
    public static ObservableList<SinhVien> getAllSinhVien() {
        System.out.println("📡 Đang tải dữ liệu sinh viên từ MySQL...");
        ObservableList<SinhVien> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM sinhvien ORDER BY maSV";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                SinhVien sv = new SinhVien(
                        rs.getString("maSV"),
                        rs.getString("hoTen"),
                        rs.getDate("ngaySinh") != null ? rs.getDate("ngaySinh").toLocalDate() : null,
                        rs.getString("gioiTinh"),
                        rs.getString("queQuan"),
                        rs.getString("khoa"),
                        rs.getString("lop")
                );
                // Lấy điểm tích lũy
                double diemTichLuy = ThongKeDAO.getDiemTichLuy(sv.getMaSV());
                sv.setDiemTichLuy(diemTichLuy);
                list.add(sv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Thêm sinh viên
    public static boolean insert(SinhVien sv) {
        String sql = "INSERT INTO sinhvien (maSV, hoTen, ngaySinh, gioiTinh, queQuan, khoa, lop) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sv.getMaSV());
            ps.setString(2, sv.getHoTen());
            if (sv.getNgaySinh() != null) {
                ps.setDate(3, java.sql.Date.valueOf(sv.getNgaySinh()));
            } else {
                ps.setNull(3, Types.DATE);
            }
            ps.setString(4, sv.getGioiTinh());
            ps.setString(5, sv.getQueQuan());
            ps.setString(6, sv.getKhoa());
            ps.setString(7, sv.getLop());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Cập nhật sinh viên
    public static boolean update(SinhVien sv) {
        String sql = "UPDATE sinhvien SET hoTen=?, ngaySinh=?, gioiTinh=?, queQuan=?, khoa=?, lop=? WHERE maSV=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sv.getHoTen());
            if (sv.getNgaySinh() != null) {
                ps.setDate(2, java.sql.Date.valueOf(sv.getNgaySinh()));
            } else {
                ps.setNull(2, Types.DATE);
            }
            ps.setString(3, sv.getGioiTinh());
            ps.setString(4, sv.getQueQuan());
            ps.setString(5, sv.getKhoa());
            ps.setString(6, sv.getLop());
            ps.setString(7, sv.getMaSV());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Xóa sinh viên
    public static boolean delete(String maSV) {
        String sql = "DELETE FROM sinhvien WHERE maSV=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSV);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Tìm kiếm sinh viên
    public static ObservableList<SinhVien> search(String keyword) {
        ObservableList<SinhVien> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM sinhvien WHERE maSV LIKE ? OR hoTen LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SinhVien sv = new SinhVien(
                        rs.getString("maSV"),
                        rs.getString("hoTen"),
                        rs.getDate("ngaySinh") != null ? rs.getDate("ngaySinh").toLocalDate() : null,
                        rs.getString("gioiTinh"),
                        rs.getString("queQuan"),
                        rs.getString("khoa"),
                        rs.getString("lop")
                );
                double diemTichLuy = ThongKeDAO.getDiemTichLuy(sv.getMaSV());
                sv.setDiemTichLuy(diemTichLuy);
                list.add(sv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Lấy danh sách tất cả các lớp
    public static ObservableList<String> getAllLop() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT DISTINCT lop FROM sinhvien ORDER BY lop";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("lop"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy tên đăng nhập (username) dựa vào mã sinh viên.
     * @param maSV Mã sinh viên
     * @return Tên đăng nhập, hoặc null nếu không tìm thấy.
     */
    public static String getUsernameByMaSV(String maSV) {
        String sql = "SELECT username FROM sinhvien WHERE maSV = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maSV);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Không tìm thấy hoặc có lỗi
    }
}

package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.BangDiem;
import model.DiemChiTiet;
import util.DBConnection;

public class DiemDAO {

    /**
     * Lấy danh sách tất cả sinh viên thuộc một lớp và điểm của họ cho một môn học cụ thể.
     * Nếu sinh viên chưa có điểm, các giá trị điểm sẽ là 0.
     * @param lop Lớp cần xem điểm.
     * @param maMon Mã môn học cần xem điểm.
     * @return Danh sách chi tiết điểm.
     */
    public static List<DiemChiTiet> getDiemByLopAndMon(String lop, String maMon) {
        List<DiemChiTiet> list = new ArrayList<>();
        // Sửa lỗi: Sử dụng câu lệnh SQL chính xác với CROSS JOIN và LEFT JOIN
        // để đảm bảo tất cả sinh viên trong lớp đều được liệt kê.
        String sql = "SELECT sv.maSV, sv.hoTen, mh.maMon, mh.tenMon, mh.soTinChi, mh.hocKy, " +
                     "bd.diemQT, bd.diemThi, bd.diemTB " +
                     "FROM sinhvien sv " +
                     "CROSS JOIN monhoc mh " +
                     "LEFT JOIN bangdiem bd ON sv.maSV = bd.maSV AND mh.maMon = bd.maMon " +
                     "WHERE sv.lop = ? AND mh.maMon = ? " +
                     "ORDER BY sv.maSV";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lop);
            ps.setString(2, maMon);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new DiemChiTiet(
                        rs.getString("maSV"),
                        rs.getString("hoTen"),
                        rs.getString("maMon"),
                        rs.getString("tenMon"),
                        rs.getInt("soTinChi"),
                        rs.getInt("hocKy"),
                        rs.getFloat("diemQT"),
                        rs.getFloat("diemThi"),
                        rs.getFloat("diemTB")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Thêm mới hoặc cập nhật điểm cho một sinh viên.
     * Sử dụng câu lệnh INSERT ... ON DUPLICATE KEY UPDATE của MySQL.
     * @param diem Đối tượng BangDiem chứa thông tin điểm cần lưu.
     * @return true nếu thành công, false nếu thất bại.
     */
    public static boolean upsertDiem(BangDiem diem) {
        String sql = "INSERT INTO bangdiem (maSV, maMon, diemQT, diemThi) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE diemQT = VALUES(diemQT), diemThi = VALUES(diemThi)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, diem.getMaSV());
            ps.setString(2, diem.getMaMon());
            ps.setFloat(3, diem.getDiemQT());
            ps.setFloat(4, diem.getDiemThi());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
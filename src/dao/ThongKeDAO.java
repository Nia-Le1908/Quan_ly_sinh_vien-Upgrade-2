package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.DiemChiTiet;
import util.DBConnection;

public class ThongKeDAO {
    
    /**
     * Lấy danh sách điểm chi tiết của một sinh viên.
     * @param maSV Mã sinh viên
     * @return Danh sách điểm chi tiết.
     */
    public static List<DiemChiTiet> getDiemChiTiet(String maSV) {
        List<DiemChiTiet> list = new ArrayList<>();
        String sql = "SELECT sv.maSV, sv.hoTen, mh.maMon, mh.tenMon, mh.soTinChi, mh.hocKy, bd.diemQT, bd.diemThi, bd.diemTB " +
                     "FROM sinhvien sv " +
                     "JOIN bangdiem bd ON sv.maSV = bd.maSV " +
                     "JOIN monhoc mh ON bd.maMon = mh.maMon " +
                     "WHERE sv.maSV = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maSV);
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
     * Tính điểm tích lũy hệ 4 của một sinh viên.
     * @param maSV Mã sinh viên
     * @return Điểm tích lũy.
     */
    public static double getDiemTichLuy(String maSV) {
        double tongDiemNhanTinChi = 0;
        int tongTinChi = 0;
        String sql = "SELECT bd.diemTB, mh.soTinChi " +
                     "FROM bangdiem bd " +
                     "JOIN monhoc mh ON bd.maMon = mh.maMon " +
                     "WHERE bd.maSV = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSV);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                double diemTB = rs.getDouble("diemTB");
                int soTinChi = rs.getInt("soTinChi");

                // Chỉ tính các môn có điểm >= 4.0 (môn qua)
                if (diemTB >= 4.0) {
                    tongDiemNhanTinChi += convertToScale4(diemTB) * soTinChi;
                    tongTinChi += soTinChi;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (tongTinChi == 0) {
            return 0.0;
        }

        // Làm tròn đến 2 chữ số thập phân
        double diemTichLuy = tongDiemNhanTinChi / tongTinChi;
        return Math.round(diemTichLuy * 100.0) / 100.0;
    }

    /**
     * Chuyển đổi điểm hệ 10 sang điểm hệ 4.
     * @param diemHe10 Điểm hệ 10
     * @return Điểm hệ 4.
     */
    public static double convertToScale4(double diemHe10) {
        if (diemHe10 >= 8.5) return 4.0;
        if (diemHe10 >= 7.0) return 3.0;
        if (diemHe10 >= 5.5) return 2.0;
        if (diemHe10 >= 4.0) return 1.0;
        return 0.0;
    }
}
package dao;

import model.MonHoc;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonHocDAO {

    public static List<MonHoc> getAllMonHoc() {
        List<MonHoc> list = new ArrayList<>();
        String sql = "SELECT * FROM monhoc ORDER BY hocKy, tenMon";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new MonHoc(
                        rs.getString("maMon"),
                        rs.getString("tenMon"),
                        rs.getInt("soTinChi"),
                        rs.getString("loaiMon"),
                        rs.getString("monTienQuyet"),
                        rs.getInt("hocKy")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void insert(MonHoc m) {
        String sql = "INSERT INTO monhoc (maMon, tenMon, soTinChi, loaiMon, monTienQuyet, hocKy) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getMaMon());
            ps.setString(2, m.getTenMon());
            ps.setInt(3, m.getSoTinChi());
            ps.setString(4, m.getLoaiMon());
            ps.setString(5, m.getMonTienQuyet());
            ps.setInt(6, m.getHocKy());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void update(MonHoc m) {
        String sql = "UPDATE monhoc SET tenMon=?, soTinChi=?, loaiMon=?, monTienQuyet=?, hocKy=? WHERE maMon=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getTenMon());
            ps.setInt(2, m.getSoTinChi());
            ps.setString(3, m.getLoaiMon());
            ps.setString(4, m.getMonTienQuyet());
            ps.setInt(5, m.getHocKy());
            ps.setString(6, m.getMaMon());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delete(String maMon) {
        String sql = "DELETE FROM monhoc WHERE maMon=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maMon);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<MonHoc> search(String keyword) {
        List<MonHoc> list = new ArrayList<>();
        String sql = "SELECT * FROM monhoc WHERE maMon LIKE ? OR tenMon LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new MonHoc(
                        rs.getString("maMon"),
                        rs.getString("tenMon"),
                        rs.getInt("soTinChi"),
                        rs.getString("loaiMon"),
                        rs.getString("monTienQuyet"),
                        rs.getInt("hocKy")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
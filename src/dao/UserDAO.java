package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;
import util.DBConnection;
import util.PasswordUtil;

public class UserDAO {

    /**
     * Xác thực người dùng.
     * @param username Tên đăng nhập.
     * @param password Mật khẩu.
     * @return Trả về đối tượng User nếu thành công, null nếu thất bại.
     */
    public static User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password_hash");
                if (PasswordUtil.checkPassword(password, hashedPassword)) {
                    // Mật khẩu khớp, trả về thông tin người dùng
                    return new User(username, null, rs.getString("role"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Xác thực thất bại
    }

    /**
     * Đăng ký người dùng mới.
     * @param user Đối tượng người dùng chứa thông tin đăng ký.
     * @param maSV Mã sinh viên (chỉ dành cho vai trò SINHVIEN, có thể là null).
     * @return Chuỗi "SUCCESS" nếu thành công, hoặc một thông báo lỗi cụ thể nếu thất bại.
     */
    public static String registerUser(User user, String maSV) {
        // Tách biệt logic cho 2 vai trò
        if ("SINHVIEN".equals(user.getRole())) {
            return registerStudent(user, maSV);
        } else {
            return registerLecturer(user);
        }
    }

    private static String registerLecturer(User user) {
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getUsername());
            ps.setString(2, PasswordUtil.hashPassword(user.getPassword()));
            ps.setString(3, user.getRole());
            
            ps.executeUpdate();
            return "SUCCESS";
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Mã lỗi cho Duplicate entry
                return "Tên đăng nhập đã tồn tại.";
            }
            e.printStackTrace();
            return "Đã xảy ra lỗi cơ sở dữ liệu.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Đã xảy ra lỗi không xác định.";
        }
    }

    private static String registerStudent(User user, String maSV) {
        String checkStudentSql = "SELECT username FROM sinhvien WHERE maSV = ?";
        String insertUserSql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
        String updateUserSql = "UPDATE sinhvien SET username = ? WHERE maSV = ?";
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu một transaction

            // 1. Kiểm tra xem sinh viên có tồn tại và đã có tài khoản chưa
            try (PreparedStatement psCheck = conn.prepareStatement(checkStudentSql)) {
                psCheck.setString(1, maSV);
                ResultSet rs = psCheck.executeQuery();
                if (!rs.next()) {
                    return "Mã sinh viên không tồn tại.";
                }
                if (rs.getString("username") != null) {
                    return "Sinh viên này đã có tài khoản.";
                }
            }
            
            // 2. Thêm tài khoản vào bảng users
            try (PreparedStatement psInsert = conn.prepareStatement(insertUserSql)) {
                psInsert.setString(1, user.getUsername());
                psInsert.setString(2, PasswordUtil.hashPassword(user.getPassword()));
                psInsert.setString(3, user.getRole());
                psInsert.executeUpdate();
            }

            // 3. Cập nhật bảng sinhvien để liên kết với tài khoản mới
            try (PreparedStatement psUpdate = conn.prepareStatement(updateUserSql)) {
                psUpdate.setString(1, user.getUsername());
                psUpdate.setString(2, maSV);
                psUpdate.executeUpdate();
            }
            
            conn.commit(); // Hoàn tất transaction
            return "SUCCESS";

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Mã lỗi cho Duplicate entry (cho username)
                return "Tên đăng nhập đã tồn tại.";
            }
             if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return "Đã xảy ra lỗi cơ sở dữ liệu khi đăng ký.";
        } 
        catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return "Đã xảy ra lỗi không xác định.";
        } finally {
             if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

     /**
     * Lấy mã sinh viên từ username.
     * @param username Tên đăng nhập của sinh viên.
     * @return Mã sinh viên.
     */
    public static String getMaSVByUsername(String username) {
        String sql = "SELECT maSV FROM sinhvien WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("maSV");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Đặt lại mật khẩu của người dùng về một giá trị mặc định.
     * @param username Tên đăng nhập của tài khoản cần đặt lại mật khẩu.
     * @return true nếu thành công, false nếu thất bại.
     */
    public static boolean resetPassword(String username) {
        // Mật khẩu mặc định mới sẽ là "123456"
        String defaultPassword = "123456"; 
        String hashedPassword = PasswordUtil.hashPassword(defaultPassword);

        String sql = "UPDATE users SET password_hash = ? WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, hashedPassword);
            ps.setString(2, username);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
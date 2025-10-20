package model;

// Lớp này đại diện cho một người dùng trong hệ thống
public class User {
    private String username;
    private String password; // Mật khẩu gốc, chỉ dùng khi đăng ký
    private String role; // GIANGVIEN hoặc SINHVIEN

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
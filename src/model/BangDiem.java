package model;

public class BangDiem {
    private String maSV;
    private String maMon;
    private float diemQT;
    private float diemThi;
    private float diemTB;

    public BangDiem(String maSV, String maMon, float diemQT, float diemThi, float diemTB) {
        this.maSV = maSV;
        this.maMon = maMon;
        this.diemQT = diemQT;
        this.diemThi = diemThi;
        this.diemTB = diemTB;
    }

    // Getters
    public String getMaSV() { return maSV; }
    public String getMaMon() { return maMon; }
    public float getDiemQT() { return diemQT; }
    public float getDiemThi() { return diemThi; }
    public float getDiemTB() { return diemTB; }

    // Setters - Cần thiết cho chức năng sửa điểm
    public void setMaSV(String maSV) { this.maSV = maSV; }
    public void setMaMon(String maMon) { this.maMon = maMon; }
    public void setDiemQT(float diemQT) { this.diemQT = diemQT; }
    public void setDiemThi(float diemThi) { this.diemThi = diemThi; }
    public void setDiemTB(float diemTB) { this.diemTB = diemTB; }
}
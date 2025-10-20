package model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DiemChiTiet {
    private final StringProperty maSV;
    private final StringProperty hoTen;
    private final StringProperty maMon;
    private final StringProperty tenMon;
    private final IntegerProperty soTinChi;
    private final IntegerProperty hocKy;
    private final FloatProperty diemQT;
    private final FloatProperty diemThi;
    private final FloatProperty diemTB;
    private final ReadOnlyStringWrapper diemChu; // Thuộc tính điểm chữ

    public DiemChiTiet(String maSV, String hoTen, String maMon, String tenMon, int soTinChi, int hocKy, float diemQT, float diemThi, float diemTB) {
        this.maSV = new SimpleStringProperty(maSV);
        this.hoTen = new SimpleStringProperty(hoTen);
        this.maMon = new SimpleStringProperty(maMon);
        this.tenMon = new SimpleStringProperty(tenMon);
        this.soTinChi = new SimpleIntegerProperty(soTinChi);
        this.hocKy = new SimpleIntegerProperty(hocKy);
        this.diemQT = new SimpleFloatProperty(diemQT);
        this.diemThi = new SimpleFloatProperty(diemThi);
        this.diemTB = new SimpleFloatProperty(diemTB);
        
        // Khởi tạo và tính toán điểm chữ
        this.diemChu = new ReadOnlyStringWrapper(convertToLetterGrade(diemTB));
    }

    // ===== Getters =====
    public String getMaSV() { return maSV.get(); }
    public String getHoTen() { return hoTen.get(); }
    public String getMaMon() { return maMon.get(); }
    public String getTenMon() { return tenMon.get(); }
    public int getSoTinChi() { return soTinChi.get(); }
    public int getHocKy() { return hocKy.get(); }
    public float getDiemQT() { return diemQT.get(); }
    public float getDiemThi() { return diemThi.get(); }
    public float getDiemTB() { return diemTB.get(); }
    public String getDiemChu() { return diemChu.get(); }

    // ===== Property Accessors =====
    public StringProperty maSVProperty() { return maSV; }
    public StringProperty hoTenProperty() { return hoTen; }
    public StringProperty maMonProperty() { return maMon; }
    public StringProperty tenMonProperty() { return tenMon; }
    public IntegerProperty soTinChiProperty() { return soTinChi; }
    public IntegerProperty hocKyProperty() { return hocKy; }
    public FloatProperty diemQTProperty() { return diemQT; }
    public FloatProperty diemThiProperty() { return diemThi; }
    public FloatProperty diemTBProperty() { return diemTB; }
    public ReadOnlyStringProperty diemChuProperty() { return diemChu.getReadOnlyProperty(); }

    /**
     * Chuyển đổi điểm hệ 10 sang điểm chữ.
     * @param diemHe10 Điểm hệ 10
     * @return Điểm chữ (A, B, C, D, F).
     */
    private String convertToLetterGrade(double diemHe10) {
        if (diemHe10 >= 8.5) return "A";
        if (diemHe10 >= 7.0) return "B";
        if (diemHe10 >= 5.5) return "C";
        if (diemHe10 >= 4.0) return "D";
        return "F";
    }
}
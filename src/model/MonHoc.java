package model;

import javafx.beans.property.*;

public class MonHoc {
    private final StringProperty maMon;
    private final StringProperty tenMon;
    private final IntegerProperty soTinChi;
    private final StringProperty loaiMon;
    private final StringProperty monTienQuyet;
    private final IntegerProperty hocKy;

    public MonHoc(String maMon, String tenMon, int soTinChi, String loaiMon, String monTienQuyet, int hocKy) {
        this.maMon = new SimpleStringProperty(maMon);
        this.tenMon = new SimpleStringProperty(tenMon);
        this.soTinChi = new SimpleIntegerProperty(soTinChi);
        this.loaiMon = new SimpleStringProperty(loaiMon);
        this.monTienQuyet = new SimpleStringProperty(monTienQuyet);
        this.hocKy = new SimpleIntegerProperty(hocKy);
    }

    public String getMaMon() { return maMon.get(); }
    public StringProperty maMonProperty() { return maMon; }

    public String getTenMon() { return tenMon.get(); }
    public void setTenMon(String value) { tenMon.set(value); }
    public StringProperty tenMonProperty() { return tenMon; }

    public int getSoTinChi() { return soTinChi.get(); }
    public void setSoTinChi(int value) { soTinChi.set(value); }
    public IntegerProperty soTinChiProperty() { return soTinChi; }

    public String getLoaiMon() { return loaiMon.get(); }
    public void setLoaiMon(String value) { loaiMon.set(value); }
    public StringProperty loaiMonProperty() { return loaiMon; }

    public String getMonTienQuyet() { return monTienQuyet.get(); }
    public void setMonTienQuyet(String value) { monTienQuyet.set(value); }
    public StringProperty monTienQuyetProperty() { return monTienQuyet; }

    public int getHocKy() { return hocKy.get(); }
    public void setHocKy(int value) { hocKy.set(value); }
    public IntegerProperty hocKyProperty() { return hocKy; }
}
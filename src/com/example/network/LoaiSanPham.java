package com.example.network;

public class LoaiSanPham  {
	String TenLoaiSp;
	String HinhAnhLoaiSp;
	public LoaiSanPham(){
		
	}
	public LoaiSanPham(String tenLoaiSp, String hinhAnhLoaiSp) {
		super();
		TenLoaiSp = tenLoaiSp;
		HinhAnhLoaiSp = hinhAnhLoaiSp;
	}
	public String getTenLoaiSp() {
		return TenLoaiSp;
	}
	public void setTenLoaiSp(String tenLoaiSp) {
		TenLoaiSp = tenLoaiSp;
	}
	public String getHinhAnhLoaiSp() {
		return HinhAnhLoaiSp;
	}
	public void setHinhAnhLoaiSp(String hinhAnhLoaiSp) {
		HinhAnhLoaiSp = hinhAnhLoaiSp;
	}
}


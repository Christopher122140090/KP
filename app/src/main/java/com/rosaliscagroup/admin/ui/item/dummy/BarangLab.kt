package com.rosaliscagroup.admin.ui.item.dummy

data class BarangLab(
    val id: String = "", // id unik untuk setiap barang
    val nama: String = "",
    val kategori: String = "",
    val kondisi: String = "",
    val labtekId: String = "",
    val pengelolaId: String = "",
    val status: String = "",
    val tanggalMasuk: String = "",
    val ownerUid: String = "", // UID pemilik barang
    val createdAt: String = "" // waktu pembuatan data
)

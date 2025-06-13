package com.hadiyarajesh.composetemplate.ui.barang

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.hadiyarajesh.composetemplate.ui.barang.dummy.BarangLab
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object BarangRepository {
    private val db = FirebaseDatabase.getInstance().reference.child("barang")

    suspend fun tambahBarang(barang: BarangLab) {
        val newRef = db.push()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val barangWithId = barang.copy(
            id = newRef.key ?: "",
            ownerUid = uid ?: ""
        )
        newRef.setValue(barangWithId).await()
    }

    suspend fun hapusBarang(barang: BarangLab) {
        if (barang.id.isNotEmpty()) {
            db.child(barang.id).removeValue().await()
        }
    }

    suspend fun updateBarang(barang: BarangLab) {
        if (barang.id.isNotEmpty()) {
            db.child(barang.id).setValue(barang).await()
        }
    }

    fun listenBarangList(): Flow<List<BarangLab>> = callbackFlow {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("BarangRepository", "listenBarangList() uid: $uid")
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        // UID admin hardcoded sesuai rules
        val adminUid = "ImE5OKfoyiR8dAG0GxNgWTxDUJ12"
        var barangListener: ValueEventListener? = null
        var barangQuery: Query? = null

        if (uid == adminUid) {
            // Admin: ambil semua barang
            barangQuery = db
        } else {
            // User biasa: hanya barang miliknya
            barangQuery = db.orderByChild("ownerUid").equalTo(uid)
        }

        barangListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(BarangLab::class.java) }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("BarangRepository", "Firebase error: ${error.message} (uid: $uid)")
                trySend(emptyList())
            }
        }
        barangQuery.addValueEventListener(barangListener)

        awaitClose {
            barangListener?.let { barangQuery?.removeEventListener(it) }
        }
    }
}

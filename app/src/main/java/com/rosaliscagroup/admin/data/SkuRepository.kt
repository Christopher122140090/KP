package com.rosaliscagroup.admin.data

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

object SkuRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserId(): String = auth.currentUser?.uid ?: UUID.randomUUID().toString()

    suspend fun generateAndReserveSku(category: String): String? {
        val prefix = when (category) {
            "Generator" -> "GNR"
            "Alat Berat" -> "ALT"
            "Alat Personel" -> "PRS"
            "Alat Tambahan" -> "TMB"
            else -> "OTH"
        }
        // Ambil semua tiket dan item yang sudah ada untuk prefix ini
        val tickets = db.collection("sku_tickets")
            .whereGreaterThanOrEqualTo("sku", "$prefix-001")
            .whereLessThanOrEqualTo("sku", "$prefix-999")
            .get().await().documents.mapNotNull { it.id }
        val items = db.collection("items")
            .whereGreaterThanOrEqualTo("sku", "$prefix-001")
            .whereLessThanOrEqualTo("sku", "$prefix-999")
            .get().await().documents.mapNotNull { it.id }
        val used = (tickets + items).toSet()
        for (i in 1..999) {
            val number = i.toString().padStart(3, '0')
            val sku = "$prefix-$number"
            if (!used.contains(sku)) {
                // Double check: reserve with Firestore transaction to avoid race condition
                val ticketRef = db.collection("sku_tickets").document(sku)
                val itemRef = db.collection("items").document(sku)
                val ticketResult = db.runTransaction { transaction ->
                    val ticketSnap = transaction.get(ticketRef)
                    val itemSnap = transaction.get(itemRef)
                    if (!ticketSnap.exists() && !itemSnap.exists()) {
                        val ticket = mapOf(
                            "sku" to sku,
                            "userId" to getUserId(),
                            "timestamp" to Timestamp.now()
                        )
                        transaction.set(ticketRef, ticket)
                        sku
                    } else {
                        null
                    }
                }.await()
                if (ticketResult != null) return ticketResult
            }
        }
        return null // Semua SKU habis
    }

    suspend fun confirmSku(sku: String, itemData: Map<String, Any>) {
        db.collection("items").document(sku).set(itemData).await()
        db.collection("sku_tickets").document(sku).delete().await()
    }

    suspend fun releaseSku(sku: String) {
        db.collection("sku_tickets").document(sku).delete().await()
    }
}

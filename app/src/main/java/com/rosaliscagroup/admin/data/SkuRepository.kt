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

    // Helper untuk mengambil 3 huruf konsonan pertama dari kategori (huruf besar)
    private fun getCategoryCode(category: String): String {
        val upper = category.uppercase().replace(Regex("[^A-Z]"), "")
        val consonants = upper.filter { it !in "AIUEO" }
        return when {
            consonants.length >= 3 -> consonants.take(3)
            upper.length >= 3 -> upper.take(3)
            else -> upper.padEnd(3, 'X')
        }
    }

    // Helper untuk mengambil kode kategori unik dari daftar kategori yang sudah ada
    suspend fun getUniqueCategoryCode(category: String): String {
        val upper = category.uppercase().replace(Regex("[^A-Z]"), "")
        val consonants = upper.filter { it !in "AIUEO" }
        val baseCode = when {
            consonants.length >= 3 -> consonants.take(3)
            upper.length >= 3 -> upper.take(3)
            else -> upper.padEnd(3, 'X')
        }
        // Ambil semua kode kategori yang sudah ada di Firestore
        val categories = db.collection("categories").get().await()
        val usedCodes = categories.documents.mapNotNull { doc ->
            val name = doc.getString("name") ?: return@mapNotNull null
            val up = name.uppercase().replace(Regex("[^A-Z]"), "")
            val cons = up.filter { it !in "AIUEO" }
            when {
                cons.length >= 3 -> cons.take(3)
                up.length >= 3 -> up.take(3)
                else -> up.padEnd(3, 'X')
            }
        }.toSet()
        if (baseCode !in usedCodes) return baseCode
        // Jika sudah ada, coba tambah vokal pertama setelah konsonan
        val allChars = upper.toList()
        val code4 = buildString {
            var consCount = 0
            for (c in allChars) {
                if (c !in "AIUEO" && consCount < 3) {
                    append(c)
                    consCount++
                } else if (c in "AIUEO" && consCount == 3) {
                    append(c)
                    break
                }
            }
        }.padEnd(4, 'X')
        if (code4 !in usedCodes) return code4
        // Jika masih bentrok, tambahkan angka urut
        for (i in 1..99) {
            val code = baseCode + i
            if (code !in usedCodes) return code
        }
        // Fallback
        return baseCode + UUID.randomUUID().toString().take(2)
    }

    suspend fun generateAndReserveSku(category: String): String? {
        val prefix = getUniqueCategoryCode(category)
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

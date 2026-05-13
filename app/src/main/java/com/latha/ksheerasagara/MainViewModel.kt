package com.latha.ksheerasagara

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.database.*

class MainViewModel : ViewModel() {
    private val db = FirebaseDatabase.getInstance().getReference("transactions")
    val allTransactions = mutableStateListOf<Transaction>()

    init { listenToDatabase() }

    private fun listenToDatabase() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allTransactions.clear()
                val list = snapshot.children.mapNotNull { it.getValue(Transaction::class.java) }
                allTransactions.addAll(list.sortedByDescending { it.date })
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun addIncome(liters: Double, fat: Double, rate: Double, cowId: String) {
        val id = db.push().key ?: return
        val entry = Transaction(
            id = id,
            type = "INCOME",
            category = "Milk",
            amount = liters * rate,
            liters = liters,
            fatPercentage = fat,
            cowId = cowId,
            date = System.currentTimeMillis() // FIX: Explicitly capture current time here
        )
        db.child(id).setValue(entry)
    }

    fun addExpense(category: String, amount: Double, cowId: String) {
        val id = db.push().key ?: return
        val entry = Transaction(
            id = id,
            type = "EXPENSE",
            category = category,
            amount = amount,
            cowId = cowId,
            date = System.currentTimeMillis() // FIX: Explicitly capture current time here
        )
        db.child(id).setValue(entry)
    }
}
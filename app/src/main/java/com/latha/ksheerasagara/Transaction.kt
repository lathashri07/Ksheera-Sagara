package com.latha.ksheerasagara

data class Transaction(
    val id: String = "",
    val type: String = "", // INCOME or EXPENSE
    val category: String = "", // Milk, Fodder, Medical, Labor
    val amount: Double = 0.0,
    val liters: Double = 0.0,
    val fatPercentage: Double = 0.0,
    val cowId: String = "Cow 1",
    val date: Long = System.currentTimeMillis() // Fix: Capture current time
)
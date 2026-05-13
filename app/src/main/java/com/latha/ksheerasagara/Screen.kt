package com.latha.ksheerasagara

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object IncomeLog : Screen("income_log")
    object ExpenseLog : Screen("expense_log")
    object ProfitReport : Screen("profit_report")
    object CowAnalysis : Screen("cow_analysis")
    object Analytics : Screen("analytics")
    object Summary : Screen("summary")
}

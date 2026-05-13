package com.latha.ksheerasagara

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.latha.ksheerasagara.ui.theme.KsheeraSagaraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KsheeraSagaraTheme {
                val navController = rememberNavController()
                // Simple ViewModel initialization without the Factory/Room
                val mainViewModel: MainViewModel = viewModel()

                NavHost(navController = navController, startDestination = Screen.Login.route) {
                    composable(Screen.Login.route) {
                        LoginScreen { navController.navigate(Screen.Dashboard.route) }
                    }
                    composable(Screen.Dashboard.route) {
                        DashboardHub(navController)
                    }
                    composable(Screen.IncomeLog.route) { IncomeLogScreen(mainViewModel) }
                    composable(Screen.ExpenseLog.route) { ExpenseLogScreen(mainViewModel) }
                    composable(Screen.ProfitReport.route) { ProfitReportScreen(mainViewModel) }
                    composable(Screen.CowAnalysis.route) { CowAnalysisScreen(mainViewModel) }
                    composable(Screen.Analytics.route) { AnalyticsScreen(mainViewModel) }
                    composable(Screen.Summary.route) { MonthlySummaryScreen(mainViewModel) }
                }
            }
        }
    }
}
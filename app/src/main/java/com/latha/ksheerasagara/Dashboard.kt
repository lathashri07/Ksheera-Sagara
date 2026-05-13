package com.latha.ksheerasagara

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Keep ONLY this Color import
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import java.io.FileOutputStream

// Central list of 10 cows
val cowList = listOf("Cow 1", "Cow 2", "Cow 3", "Cow 4", "Cow 5", "Cow 6", "Cow 7", "Cow 8", "Cow 9", "Cow 10")

// Helper class for Analytics
data class CowResult(val id: String, val profit: Double, val liters: Double)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardHub(navController: NavController) {
    Scaffold(
        // Set the background color here
        containerColor = Color(0xFFF1F8E9),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ksheera-Sagara Hub", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF2E7D32) // Nature Green Header
                )
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = Color(0xFF2E7D32)) {
                IconButton(
                    onClick = { navController.navigate(Screen.Analytics.route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PieChart, contentDescription = "Analytics", tint = Color.White)
                        Text(" View Detailed Expense Analytics", color = Color.White)
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text(
                "Welcome, Farmer!",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF1B5E20), // Darker green for text
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                HubCard("Financial Summary", "📄 Generate PDF", Color(0xFFDCEDC8), Modifier.fillMaxWidth()) {
                    navController.navigate(Screen.Summary.route)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    HubCard("Income Log", "₹ Milk Slips", Color(0xFFC8E6C9), Modifier.weight(1f)) {
                        navController.navigate(Screen.IncomeLog.route)
                    }
                    HubCard("Expense Log", "💸 Costs", Color(0xFFFFCCBC), Modifier.weight(1f)) {
                        navController.navigate(Screen.ExpenseLog.route)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    HubCard("Profit Dashboard", "📈 Health", Color(0xFFB2DFDB), Modifier.weight(1f)) {
                        navController.navigate(Screen.ProfitReport.route)
                    }
                    HubCard("Cow Analysis", "🐄 Performance", Color(0xFFFFF9C4), Modifier.weight(1f)) {
                        navController.navigate(Screen.CowAnalysis.route)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HubCard(title: String, subtitle: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.height(150.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeLogScreen(viewModel: MainViewModel) {
    Surface(color = Color(0xFFF1F8E9), modifier = Modifier.fillMaxSize()) { // Nature Green Background
        var liters by remember { mutableStateOf("") }
        var fat by remember { mutableStateOf("") }
        var selectedCow by remember { mutableStateOf(cowList[0]) }
        val context = LocalContext.current

        Column(Modifier.padding(16.dp)) {
            Text("Log Milk Income", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF1B5E20))

            Text("Select Cow:", modifier = Modifier.padding(top = 8.dp), color = Color(0xFF1B5E20))
            LazyRow(Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(cowList) { cow ->
                    FilterChip(
                        selected = selectedCow == cow,
                        onClick = { selectedCow = cow },
                        label = { Text(cow) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2E7D32),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            OutlinedTextField(
                value = liters,
                onValueChange = { liters = it },
                label = { Text("Liters") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2E7D32))
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = fat,
                onValueChange = { fat = it },
                label = { Text("Fat %") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2E7D32))
            )

            Button(
                onClick = {
                    if (liters.isNotEmpty()) {
                        viewModel.addIncome(liters.toDoubleOrNull() ?: 0.0, fat.toDoubleOrNull() ?: 0.0, 40.0, selectedCow)
                        Toast.makeText(context, "Saved for $selectedCow!", Toast.LENGTH_SHORT).show()
                        liters = ""; fat = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)) // Unified Button Color
            ) { Text("Save Milk Slip") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseLogScreen(viewModel: MainViewModel) {
    Surface(color = Color(0xFFF1F8E9), modifier = Modifier.fillMaxSize()) {
        var amount by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Fodder") }
        var selectedCow by remember { mutableStateOf(cowList[0]) }
        val context = LocalContext.current

        Column(Modifier.padding(16.dp)) {
            Text("Log Farm Expense", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF1B5E20))

            Text("Associate with Cow:", modifier = Modifier.padding(top = 8.dp), color = Color(0xFF1B5E20))
            LazyRow(Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(cowList) { cow ->
                    FilterChip(
                        selected = selectedCow == cow,
                        onClick = { selectedCow = cow },
                        label = { Text(cow) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF2E7D32), selectedLabelColor = Color.White)
                    )
                }
            }

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2E7D32))
            )

            Text("Category:", modifier = Modifier.padding(top = 16.dp), color = Color(0xFF1B5E20))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Fodder", "Medical", "Labor").forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF2E7D32), selectedLabelColor = Color.White)
                    )
                }
            }

            Button(
                onClick = {
                    if (amount.isNotEmpty()) {
                        viewModel.addExpense(category, amount.toDoubleOrNull() ?: 0.0, selectedCow)
                        Toast.makeText(context, "Expense Saved for $selectedCow!", Toast.LENGTH_SHORT).show()
                        amount = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) { Text("Save Expense") }
        }
    }
}

@Composable
fun ProfitReportScreen(viewModel: MainViewModel) {
    Surface(color = Color(0xFFF1F8E9), modifier = Modifier.fillMaxSize()) {
        val transactions = viewModel.allTransactions
        val netProfit = transactions.filter { it.type == "INCOME" }.sumOf { it.amount } -
                transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }

        val cowProfits = transactions.groupBy { it.cowId }.mapValues { (_, t) ->
            t.filter { it.type == "INCOME" }.sumOf { it.amount } - t.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        }
        val bestCow = cowProfits.maxByOrNull { it.value }

        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Financial Health Status", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF1B5E20))
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier.size(150.dp).background(if (netProfit >= 0) Color(0xFF2E7D32) else Color.Red, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(if (netProfit >= 0) "HEALTHY" else "LOSS", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Text("Total Net: ₹${"%.2f".format(netProfit)}", Modifier.padding(top = 16.dp), color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)

            bestCow?.let {
                Card(
                    Modifier.fillMaxWidth().padding(top = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⭐ Highest Profit Cow", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        Text("${it.key}: ₹${"%.2f".format(it.value)}", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF1B5E20))
                    }
                }
            }
        }
    }
}
@Composable
fun AnalyticsScreen(viewModel: MainViewModel) {
    Surface(color = Color(0xFFF1F8E9), modifier = Modifier.fillMaxSize()) {
        val transactions = viewModel.allTransactions
        val fodder = transactions.filter { it.category == "Fodder" }.sumOf { it.amount }.toFloat()
        val medical = transactions.filter { it.category == "Medical" }.sumOf { it.amount }.toFloat()
        val labor = transactions.filter { it.category == "Labor" }.sumOf { it.amount }.toFloat()
        val totalExp = fodder + medical + labor

        Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Expense Breakdown", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF1B5E20))
            Spacer(Modifier.height(32.dp))
            Box(Modifier.size(200.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    if (totalExp > 0) {
                        val sweepFodder = (fodder / totalExp) * 360f
                        val sweepMed = (medical / totalExp) * 360f
                        val sweepLabor = (labor / totalExp) * 360f
                        drawArc(Color(0xFF4CAF50), -90f, sweepFodder, true)
                        drawArc(Color(0xFFF44336), -90f + sweepFodder, sweepMed, true)
                        drawArc(Color(0xFF2196F3), -90f + sweepFodder + sweepMed, sweepLabor, true)
                    } else { drawCircle(Color.LightGray) }
                }
            }
            Spacer(Modifier.height(32.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    ExpenseLabel("Fodder", fodder, Color(0xFF4CAF50))
                    ExpenseLabel("Medical", medical, Color(0xFFF44336))
                    ExpenseLabel("Labor", labor, Color(0xFF2196F3))
                }
            }
        }
    }
}

@Composable
fun ExpenseLabel(label: String, amount: Float, color: Color) {
    Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(16.dp).background(color, CircleShape))
        Text(" $label: ", fontWeight = FontWeight.Bold)
        Text("₹${"%.2f".format(amount)}")
    }
}

@Composable
fun CowAnalysisScreen(viewModel: MainViewModel) {
    Surface(color = Color(0xFFF1F8E9), modifier = Modifier.fillMaxSize()) {
        val transactions = viewModel.allTransactions
        val cowStats = cowList.map { cowId ->
            val t = transactions.filter { it.cowId == cowId }
            val profit = t.filter { it.type == "INCOME" }.sumOf { it.amount } - t.filter { it.type == "EXPENSE" }.sumOf { it.amount }
            val liters = t.filter { it.type == "INCOME" }.sumOf { it.liters }
            CowResult(cowId, profit, liters)
        }.filter { it.profit != 0.0 || it.liters != 0.0 }

        LazyColumn(Modifier.padding(16.dp)) {
            item { Text("Cow Performance Ranking", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF1B5E20)) }
            items(cowStats.sortedByDescending { it.profit }) { cow ->
                Card(
                    Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(cow.id, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color(0xFF1B5E20))
                            Text("Total Produced: ${cow.liters} Liters")
                        }
                        Text("₹${"%.2f".format(cow.profit)}", color = if (cow.profit >= 0) Color(0xFF2E7D32) else Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

fun generateMonthlyPdf(context: Context, transactions: List<Transaction>) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val jobName = "KsheeraSagara_Summary_${System.currentTimeMillis()}"

    printManager.print(jobName, object : PrintDocumentAdapter() {
        override fun onLayout(oldAttributes: PrintAttributes?, newAttributes: PrintAttributes,
                              cancellationSignal: CancellationSignal?, callback: LayoutResultCallback, extras: Bundle?) {
            val pdi = PrintDocumentInfo.Builder(jobName).setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build()
            callback.onLayoutFinished(pdi, true)
        }

        override fun onWrite(pages: Array<out PageRange>?, destination: ParcelFileDescriptor?,
                             cancellationSignal: CancellationSignal?, callback: WriteResultCallback) {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()

            // Date Formatter
            val sdf = java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault())

            // Header
            paint.color = android.graphics.Color.BLACK
            paint.textSize = 24f
            paint.isFakeBoldText = true
            canvas.drawText("Ksheera-Sagara Monthly Report", 50f, 50f, paint)

            paint.textSize = 14f
            paint.isFakeBoldText = false
            canvas.drawText("Generated on: ${java.util.Date()}", 50f, 80f, paint)

            // Table Header - Updated with Date Column
            var yPos = 130f
            paint.isFakeBoldText = true
            canvas.drawText("Date", 50f, yPos, paint)        // New Column
            canvas.drawText("Category", 150f, yPos, paint)
            canvas.drawText("Cow", 300f, yPos, paint)
            canvas.drawText("Amount", 450f, yPos, paint)

            // Data Rows
            paint.isFakeBoldText = false
            transactions.take(25).forEach { item ->
                yPos += 25f

                // Format the timestamp to "10 May"
                val dateString = sdf.format(java.util.Date(item.date))

                canvas.drawText(dateString, 50f, yPos, paint)      // New Column Data
                canvas.drawText(item.category, 150f, yPos, paint)
                canvas.drawText(item.cowId, 300f, yPos, paint)
                canvas.drawText("₹${item.amount}", 450f, yPos, paint)
            }

            pdfDocument.finishPage(page)
            pdfDocument.writeTo(FileOutputStream(destination?.fileDescriptor))
            pdfDocument.close()
            callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        }
    }, null)
}

@Composable
fun MonthlySummaryScreen(viewModel: MainViewModel) {
    Surface(color = Color(0xFFF1F8E9), modifier = Modifier.fillMaxSize()) { // Nature Green Background
        val transactions = viewModel.allTransactions
        val context = LocalContext.current

        val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }

        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Financial Summary", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF1B5E20))

            Card(
                Modifier.fillMaxWidth().padding(vertical = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text("Total Monthly Income: ₹$totalIncome", color = Color(0xFF2E7D32))
                    Text("Total Monthly Expense: ₹$totalExpense", color = Color.Red)
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Text("Net Savings: ₹${totalIncome - totalExpense}", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                }
            }

            Button(
                onClick = { generateMonthlyPdf(context, transactions) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Download & Share PDF Report")
            }
        }
    }
}
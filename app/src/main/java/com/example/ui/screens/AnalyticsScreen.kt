package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.MasterSartodiData
import com.example.data.MonthlyFeederStat
import com.example.data.MonthlyLossStat
import com.example.data.MonthlyRecordDoc
import com.example.ui.components.KpiMetricCard
import com.example.ui.components.ModuleHeaderBar
import com.example.ui.theme.CautionAmber
import com.example.ui.theme.EnergizedGreen
import com.example.ui.theme.GridBluePrimary
import com.example.ui.theme.GridCyanAccent
import com.example.ui.theme.HighVoltageRed

@Composable
fun AnalyticsScreen(
    lossHistory: List<MonthlyLossStat>,
    getMonthlyFeeders: (Int, Int) -> Pair<List<MonthlyFeederStat>, String>,
    editModeEnabled: Boolean,
    onSaveMonthlyRecord: (MonthlyRecordDoc) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    var selectedYear by remember { mutableIntStateOf(2026) }
    var selectedMonthIdx by remember { mutableIntStateOf(9) } // October
    var showAddDialog by remember { mutableStateOf(false) }

    val (feeders, drivePdfId) = remember(selectedYear, selectedMonthIdx, lossHistory) {
        getMonthlyFeeders(selectedYear, selectedMonthIdx)
    }
    val monthLoss = remember(selectedYear, selectedMonthIdx, lossHistory) {
        lossHistory.firstOrNull { it.year == selectedYear && it.monthIdx == selectedMonthIdx }
    }
    val yearLosses = remember(selectedYear, lossHistory) {
        lossHistory.filter { it.year == selectedYear }
    }
    val totalMonthTripping = feeders.sumOf { it.totalTripping }
    val totalMonthMinutes = feeders.sumOf { it.durationMin }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ModuleHeaderBar(
                title = "२. २०२४–२०२६ ॲनालिटिक्स व PDF",
                subtitle = "३४ महिने फिडर कामगिरी, लॉस चार्ट व Drive PDF",
                onBack = onBack,
                trailing = if (editModeEnabled) {
                    {
                        OutlinedButton(
                            onClick = { showAddDialog = true },
                            modifier = Modifier.testTag("add_monthly_data_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("डेटा जोडा")
                        }
                    }
                } else null
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(2024, 2025, 2026).forEach { yr ->
                    FilterChip(
                        selected = selectedYear == yr,
                        onClick = { selectedYear = yr },
                        label = { Text("$yr", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("analytics_year_$yr")
                    )
                }
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(MasterSartodiData.MONTHS.indices.toList()) { mIdx ->
                    FilterChip(
                        selected = selectedMonthIdx == mIdx,
                        onClick = { selectedMonthIdx = mIdx },
                        label = { Text(MasterSartodiData.MONTHS[mIdx].take(3)) },
                        modifier = Modifier.testTag("analytics_month_$mIdx")
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiMetricCard(
                    label = "33kV इनकमर",
                    value = monthLoss?.let { "${MasterSartodiData.fmtIndian(it.incomerKwh)} kWh" } ?: "N/A",
                    sub = "${MasterSartodiData.MONTHS[selectedMonthIdx]} $selectedYear",
                    accentColor = GridBluePrimary,
                    modifier = Modifier.weight(1f)
                )
                KpiMetricCard(
                    label = "उपकेंद्र लॉस %",
                    value = monthLoss?.let { "${it.lossPct} %" } ?: "N/A",
                    sub = "फरक: ${MasterSartodiData.fmtIndian(monthLoss?.diffKwh)} kWh",
                    accentColor = if ((monthLoss?.lossPct ?: 0.0) > 1.5) HighVoltageRed else EnergizedGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiMetricCard(
                    label = "एकूण ट्रिपिंग",
                    value = "$totalMonthTripping",
                    sub = "सर्व फिडर्स मिळून",
                    accentColor = CautionAmber,
                    modifier = Modifier.weight(1f)
                )
                KpiMetricCard(
                    label = "एकूण कालावधी",
                    value = "$totalMonthMinutes मि.",
                    sub = "${totalMonthMinutes / 60} तास ${totalMonthMinutes % 60} मि.",
                    accentColor = GridCyanAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (drivePdfId.isNotBlank()) {
            item {
                Button(
                    onClick = {
                        val url = if (drivePdfId.startsWith("http")) drivePdfId
                        else "https://drive.google.com/file/d/$drivePdfId/view"
                        runCatching {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_drive_pdf_btn")
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${MasterSartodiData.MONTHS[selectedMonthIdx]} $selectedYear मूळ Google Drive PDF पहा")
                }
            }
        }

        if (yearLosses.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "$selectedYear मासिक 33kV इनकमर (kWh) बार चार्ट",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        val maxKwh = (yearLosses.maxOfOrNull { it.incomerKwh } ?: 1.0).coerceAtLeast(1.0)
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        ) {
                            val count = yearLosses.size.coerceAtLeast(1)
                            val slotW = size.width / count
                            val barW = (slotW * 0.62f).coerceAtLeast(8f)
                            yearLosses.forEachIndexed { idx, stat ->
                                val ratio = (stat.incomerKwh / maxKwh).toFloat().coerceIn(0.05f, 1f)
                                val barH = size.height * ratio
                                val left = idx * slotW + (slotW - barW) / 2f
                                val color = if (stat.monthIdx == selectedMonthIdx) CautionAmber else GridBluePrimary
                                drawRoundRect(
                                    color = color,
                                    topLeft = Offset(left, size.height - barH),
                                    size = Size(barW, barH),
                                    cornerRadius = CornerRadius(6f, 6f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            yearLosses.forEach { st ->
                                Text(
                                    text = st.month.take(1),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (st.monthIdx == selectedMonthIdx) CautionAmber
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "फिडरनिहाय कामगिरी (${MasterSartodiData.MONTHS[selectedMonthIdx]} $selectedYear)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        if (feeders.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "या महिन्याचा फिडरनिहाय डेटा उपलब्ध नाही.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(feeders) { f ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = f.feeder,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Total: ${f.totalTripping} (${f.durationMin} मि.)",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (f.totalTripping > 20) HighVoltageRed else GridBluePrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "TP: ${f.tp} • HT: ${f.ht} • SD: ${f.sd} • BD: ${f.bd} • LS: ${f.ls}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Load (A): Max ${f.maxLoad} / Min ${f.minLoad} / Avg ${f.avgLoad}" +
                                if (f.maxVolt > 0) "  |  Volt (kV): ${f.maxVolt} / ${f.minVolt}" else "",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "उपकेंद्र मासिक लॉस इतिहास (${lossHistory.size} महिने)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        items(lossHistory.reversed()) { l ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${l.month} ${l.year}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "IN: ${MasterSartodiData.fmtIndian(l.incomerKwh)} | OUT: ${MasterSartodiData.fmtIndian(l.outgoingKwh)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${l.lossPct} %",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (l.lossPct > 1.5) HighVoltageRed else EnergizedGreen
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddMonthlyRecordDialog(
            initialYear = selectedYear,
            initialMonthIdx = selectedMonthIdx,
            existingLoss = monthLoss,
            existingPdfId = drivePdfId,
            onDismiss = { showAddDialog = false },
            onSave = { doc ->
                onSaveMonthlyRecord(doc)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddMonthlyRecordDialog(
    initialYear: Int,
    initialMonthIdx: Int,
    existingLoss: MonthlyLossStat?,
    existingPdfId: String,
    onDismiss: () -> Unit,
    onSave: (MonthlyRecordDoc) -> Unit
) {
    var yearStr by remember { mutableStateOf(initialYear.toString()) }
    var monthIdx by remember { mutableIntStateOf(initialMonthIdx) }
    var incStr by remember { mutableStateOf(existingLoss?.incomerKwh?.toLong()?.toString() ?: "") }
    var outStr by remember { mutableStateOf(existingLoss?.outgoingKwh?.toLong()?.toString() ?: "") }
    var pdfId by remember { mutableStateOf(existingPdfId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("मासिक लॉस व PDF डेटा जोडा/एडिट करा") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = yearStr,
                    onValueChange = { yearStr = it },
                    label = { Text("वर्ष (उदा. 2026)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Text(
                    text = "महिना: ${MasterSartodiData.MONTHS[monthIdx]}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(MasterSartodiData.MONTHS.indices.toList()) { idx ->
                        FilterChip(
                            selected = monthIdx == idx,
                            onClick = { monthIdx = idx },
                            label = { Text(MasterSartodiData.MONTHS[idx].take(3)) }
                        )
                    }
                }
                HorizontalDivider()
                OutlinedTextField(
                    value = incStr,
                    onValueChange = { incStr = it },
                    label = { Text("33kV इनकमर (kWh)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = outStr,
                    onValueChange = { outStr = it },
                    label = { Text("11kV आउटगोइंग (kWh)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = pdfId,
                    onValueChange = { pdfId = it },
                    label = { Text("Google Drive PDF ID किंवा URL") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val yr = yearStr.toLongOrNull() ?: initialYear.toLong()
                    val inc = incStr.toDoubleOrNull() ?: 0.0
                    val out = outStr.toDoubleOrNull() ?: 0.0
                    val diff = inc - out
                    val pct = if (inc > 0.0) ((diff / inc) * 100.0) else 0.0
                    onSave(
                        MonthlyRecordDoc(
                            year = yr,
                            monthIdx = monthIdx.toLong(),
                            monthName = MasterSartodiData.MONTHS[monthIdx],
                            incomerKwh = inc,
                            outgoingKwh = out,
                            diffKwh = diff,
                            lossPct = kotlin.math.round(pct * 1000.0) / 1000.0,
                            drivePdfId = pdfId.trim()
                        )
                    )
                },
                modifier = Modifier.testTag("save_monthly_record_btn")
            ) {
                Text("सेव्ह करा")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("रद्द करा") }
        }
    )
}

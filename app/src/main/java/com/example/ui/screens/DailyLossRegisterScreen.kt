package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.DailyLossDayDoc
import com.example.data.MasterSartodiData
import com.example.data.ReportHtmlBuilder
import com.example.data.SubstationDoc
import com.example.ui.components.DateStepSelector
import com.example.ui.components.ModuleHeaderBar
import com.example.ui.components.ReadOnlyNoticeBanner
import com.example.ui.components.shiftYearMonth
import com.example.ui.theme.CautionAmber
import com.example.ui.theme.EnergizedGreen
import com.example.ui.theme.GridBluePrimary
import com.example.ui.theme.HighVoltageRed
import java.util.Calendar

@Composable
fun DailyLossRegisterScreen(
    substation: SubstationDoc,
    allDailyLosses: Map<String, DailyLossDayDoc>,
    todayIso: String,
    currentYearMonth: String,
    editModeEnabled: Boolean,
    onToggleEditMode: () -> Unit,
    onSaveDay: (String, Map<String, Double>, Map<String, Double>) -> Unit,
    onSaveMf: (Map<String, Double>) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    var tabIndex by remember { mutableIntStateOf(0) }
    var selectedDate by remember { mutableStateOf(todayIso) }
    var selectedMonth by remember { mutableStateOf(currentYearMonth) }
    var showMfDialog by remember { mutableStateOf(false) }

    val finalInputs = remember { mutableStateMapOf<String, String>() }
    val initInputs = remember { mutableStateMapOf<String, String>() }

    LaunchedEffect(selectedDate, allDailyLosses) {
        finalInputs.clear()
        initInputs.clear()
        val doc = allDailyLosses[selectedDate]
        MasterSartodiData.DAILY_LOSS_METERS.forEach { m ->
            finalInputs[m.id] = doc?.finalReadings?.get(m.id)?.toString() ?: ""
            initInputs[m.id] = doc?.initialOverrides?.get(m.id)?.toString() ?: ""
        }
    }

    val liveDays = remember(selectedDate, allDailyLosses, finalInputs.toMap(), initInputs.toMap()) {
        val m = allDailyLosses.toMutableMap()
        val fMap = finalInputs.mapNotNull { (k, v) -> v.toDoubleOrNull()?.let { k to it } }.toMap()
        val iMap = initInputs.mapNotNull { (k, v) -> v.toDoubleOrNull()?.let { k to it } }.toMap()
        m[selectedDate] = DailyLossDayDoc(date = selectedDate, finalReadings = fMap, initialOverrides = iMap)
        m
    }
    val calc = remember(selectedDate, liveDays, substation.lossMf) {
        MasterSartodiData.calculateDailyLoss(selectedDate, liveDays, substation.lossMf)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            ModuleHeaderBar(
                title = "१०. दैनिक लॉसेस रजिस्टर (Loss Calc)",
                subtitle = "ऑटो Initial carryover, M.F. नुसार Consumption व 9 लॉस %",
                onBack = onBack,
                trailing = {
                    Row {
                        IconButton(
                            onClick = { showMfDialog = true },
                            modifier = Modifier.size(48.dp).testTag("mf_settings_btn")
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "M.F. सेटिंग्ज")
                        }
                        IconButton(
                            onClick = {
                                val html = ReportHtmlBuilder.buildDailyLossHtml(substation, selectedDate, calc)
                                ReportHtmlBuilder.printHtmlReport(context, "DailyLoss_$selectedDate", html, landscape = true)
                            },
                            modifier = Modifier.size(48.dp).testTag("print_daily_loss_btn")
                        ) {
                            Icon(Icons.Default.Print, contentDescription = "प्रिंट / PDF")
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TabRow(selectedTabIndex = tabIndex) {
                Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("दैनिक नोंद") })
                Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("मासिक रजिस्टर") })
            }
        }

        if (tabIndex == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    DateStepSelector(dateIso = selectedDate, onDateChange = { selectedDate = it })
                }
                if (!editModeEnabled) {
                    item { ReadOnlyNoticeBanner(onEnableEdit = onToggleEditMode) }
                }

                items(calc.rows, key = { it.meter.id }) { row ->
                    val mId = row.meter.id
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(row.meter.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "MF: ${row.mf.toInt()}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = GridBluePrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = initInputs[mId].orEmpty(),
                                    onValueChange = { if (editModeEnabled) initInputs[mId] = it },
                                    label = {
                                        Text(
                                            if (row.initialVal != null && !row.manualInitial) "Auto: ${row.initialVal}"
                                            else "Initial @00:00"
                                        )
                                    },
                                    enabled = editModeEnabled,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = finalInputs[mId].orEmpty(),
                                    onValueChange = { if (editModeEnabled) finalInputs[mId] = it },
                                    label = { Text("Final @24:00") },
                                    enabled = editModeEnabled,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).testTag("loss_final_$mId")
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Diff: ${row.diff?.let { "%.2f".format(it) } ?: "—"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Consumption: ${MasterSartodiData.fmtIndian(row.consumption)} kWh",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = EnergizedGreen,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                if (editModeEnabled) {
                    item {
                        Button(
                            onClick = {
                                val fMap = finalInputs.mapNotNull { (k, v) -> v.toDoubleOrNull()?.let { k to it } }.toMap()
                                val iMap = initInputs.mapNotNull { (k, v) -> v.toDoubleOrNull()?.let { k to it } }.toMap()
                                onSaveDay(selectedDate, fMap, iMap)
                            },
                            modifier = Modifier.fillMaxWidth().testTag("save_daily_loss_btn")
                        ) {
                            Text("$selectedDate दैनिक लॉस रीडिंग क्लाउडवर सेव्ह करा")
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("दैनिक लॉस % विश्लेषण (९ तुलना)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            HorizontalDivider()
                            calc.lossRows.forEach { lr ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(lr.label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                    Text(
                                        text = "${MasterSartodiData.fmtIndian(lr.diffKwh)} kWh",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = lr.lossPct?.let { "${"%.3f".format(it)} %" } ?: "—",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if ((lr.lossPct ?: 0.0) > 2.0) HighVoltageRed else EnergizedGreen
                                    )
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            SummaryLine("T/F-1 Total O/G (Saphala+DFCC+SST)", calc.tf1Out)
                            SummaryLine("T/F-2 Total O/G (Pargaon+Navghar)", calc.tf2Out)
                            SummaryLine("TOTAL H.V. (T/F-1 + T/F-2)", calc.totalHv)
                            SummaryLine("TOTAL L.V. (T/F-1 + T/F-2)", calc.totalLv)
                            SummaryLine("TOTAL O/G FEEDERS", calc.totalOg)
                        }
                    }
                }
            }
        } else {
            MonthlyLossRegisterTab(
                yearMonth = selectedMonth,
                allDailyLosses = allDailyLosses,
                mfOverrides = substation.lossMf,
                onChangeMonth = { selectedMonth = it },
                onSelectDay = { dk ->
                    selectedDate = dk
                    tabIndex = 0
                }
            )
        }
    }

    if (showMfDialog) {
        MfSettingsDialog(
            currentMf = substation.lossMf,
            editModeEnabled = editModeEnabled,
            onDismiss = { showMfDialog = false },
            onSave = {
                onSaveMf(it)
                showMfDialog = false
            }
        )
    }
}

@Composable
private fun SummaryLine(label: String, value: Double?) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        Text(
            text = "${MasterSartodiData.fmtIndian(value)} kWh",
            style = MaterialTheme.typography.labelMedium,
            color = GridBluePrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MonthlyLossRegisterTab(
    yearMonth: String,
    allDailyLosses: Map<String, DailyLossDayDoc>,
    mfOverrides: Map<String, Double>,
    onChangeMonth: (String) -> Unit,
    onSelectDay: (String) -> Unit
) {
    val parts = yearMonth.split("-")
    val yr = parts.getOrNull(0)?.toIntOrNull() ?: 2026
    val mon = parts.getOrNull(1)?.toIntOrNull() ?: 1
    val daysInMonth = Calendar.getInstance().apply { set(yr, mon - 1, 1) }.getActualMaximum(Calendar.DAY_OF_MONTH)
    val monthDates = (1..daysInMonth).map { d -> "$yr-${mon.toString().padStart(2, '0')}-${d.toString().padStart(2, '0')}" }
    val filledDates = monthDates.filter { it in allDailyLosses }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onChangeMonth(shiftYearMonth(yearMonth, -1)) }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "मागील महिना")
                }
                Text(
                    text = "${MasterSartodiData.MONTHS.getOrElse(mon - 1) { "" }} $yr (${filledDates.size} दिवस नोंद)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { onChangeMonth(shiftYearMonth(yearMonth, 1)) }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "पुढील महिना")
                }
            }
        }
        if (filledDates.isEmpty()) {
            item { EmptyRegisterHint("या महिन्यात अद्याप दैनिक लॉस नोंदी नाहीत.") }
        } else {
            items(filledDates) { dk ->
                val calc = MasterSartodiData.calculateDailyLoss(dk, allDailyLosses, mfOverrides)
                val incVsOg = calc.lossRows.firstOrNull { it.label == "INC. VS O/G FDR" }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(MasterSartodiData.formatDmy(dk), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(
                                text = "INC: ${MasterSartodiData.fmtIndian(calc.consumptionMap["inc"])} | O/G: ${MasterSartodiData.fmtIndian(calc.totalOg)} kWh",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = incVsOg?.lossPct?.let { "${"%.2f".format(it)} %" } ?: "—",
                                style = MaterialTheme.typography.titleSmall,
                                color = CautionAmber,
                                fontWeight = FontWeight.ExtraBold
                            )
                            TextButton(onClick = { onSelectDay(dk) }) { Text("पहा") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MfSettingsDialog(
    currentMf: Map<String, Double>,
    editModeEnabled: Boolean,
    onDismiss: () -> Unit,
    onSave: (Map<String, Double>) -> Unit
) {
    val map = remember {
        mutableStateMapOf<String, String>().apply {
            MasterSartodiData.DAILY_LOSS_METERS.forEach { m ->
                put(m.id, (currentMf[m.id] ?: m.defaultMf).toInt().toString())
            }
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("M.F. (Multiplying Factor) सेटिंग्ज") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MasterSartodiData.DAILY_LOSS_METERS.forEach { m ->
                    OutlinedTextField(
                        value = map[m.id].orEmpty(),
                        onValueChange = { if (editModeEnabled) map[m.id] = it },
                        label = { Text("${m.shortName} (Default ${m.defaultMf.toInt()})") },
                        enabled = editModeEnabled,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            if (editModeEnabled) {
                Button(onClick = {
                    val out = map.mapNotNull { (k, v) -> v.toDoubleOrNull()?.takeIf { it > 0 }?.let { k to it } }.toMap()
                    onSave(out)
                }) { Text("सेव्ह करा") }
            } else {
                TextButton(onClick = onDismiss) { Text("बंद करा") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("रद्द करा") } }
    )
}

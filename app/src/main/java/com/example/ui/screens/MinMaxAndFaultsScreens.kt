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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import com.example.data.FaultEntry
import com.example.data.MasterSartodiData
import com.example.data.MinMaxDayDoc
import com.example.data.ReportHtmlBuilder
import com.example.data.SubstationDoc
import com.example.data.TripEntry
import com.example.ui.components.DateStepSelector
import com.example.ui.components.DeleteEntryButton
import com.example.ui.components.DropdownSelectorField
import com.example.ui.components.KpiMetricCard
import com.example.ui.components.ModuleHeaderBar
import com.example.ui.components.ReadOnlyNoticeBanner
import com.example.ui.components.shiftYearMonth
import com.example.ui.theme.CautionAmber
import com.example.ui.theme.EnergizedGreen
import com.example.ui.theme.GridBluePrimary
import com.example.ui.theme.HighVoltageRed

// ==================== MODULE 11: MIN-MAX & MONTHLY REPORTS PDF ====================
@Composable
fun MinMaxReportScreen(
    substation: SubstationDoc,
    allMinMax: Map<String, MinMaxDayDoc>,
    allDailyLosses: Map<String, DailyLossDayDoc>,
    trips: List<TripEntry>,
    faults: List<FaultEntry>,
    todayIso: String,
    currentYearMonth: String,
    editModeEnabled: Boolean,
    onToggleEditMode: () -> Unit,
    onSaveMinMaxDay: (String, Map<String, Map<String, String>>) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    var tabIndex by remember { mutableIntStateOf(0) }
    var selectedDate by remember { mutableStateOf(todayIso) }
    var reportMonth by remember { mutableStateOf(currentYearMonth) }

    val mxVals = remember { mutableStateMapOf<String, String>() }
    val mxTimes = remember { mutableStateMapOf<String, String>() }
    val mnVals = remember { mutableStateMapOf<String, String>() }
    val mnTimes = remember { mutableStateMapOf<String, String>() }

    LaunchedEffect(selectedDate, allMinMax) {
        mxVals.clear(); mxTimes.clear(); mnVals.clear(); mnTimes.clear()
        val day = allMinMax[selectedDate]?.entries.orEmpty()
        MasterSartodiData.MINMAX_ITEMS.forEach { item ->
            val m = day[item.id].orEmpty()
            mxVals[item.id] = m["mx"].orEmpty()
            mxTimes[item.id] = m["mxT"].orEmpty()
            mnVals[item.id] = m["mn"].orEmpty()
            mnTimes[item.id] = m["mnT"].orEmpty()
        }
    }

    val selectedSections = remember {
        mutableStateMapOf(
            "minmax" to true,
            "msedcl" to true,
            "intr" to true,
            "lossm" to true,
            "faults" to true
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            ModuleHeaderBar(
                title = "११. Min-Max नोंद व मासिक रिपोर्ट PDF",
                subtitle = "दैनिक Max/Min वेळेसह + सर्व मासिक रिपोर्ट PDF पॅक",
                onBack = onBack
            )
            Spacer(modifier = Modifier.height(8.dp))
            TabRow(selectedTabIndex = tabIndex) {
                Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("दैनिक Min-Max") })
                Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("मासिक PDF पॅक") })
            }
        }

        if (tabIndex == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { DateStepSelector(dateIso = selectedDate, onDateChange = { selectedDate = it }) }
                if (!editModeEnabled) {
                    item { ReadOnlyNoticeBanner(onEnableEdit = onToggleEditMode) }
                }

                items(MasterSartodiData.MINMAX_ITEMS, key = { it.id }) { item ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("${item.name} (${item.unit})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedTextField(
                                    value = mxVals[item.id].orEmpty(),
                                    onValueChange = { if (editModeEnabled) mxVals[item.id] = it },
                                    label = { Text("Max (${item.unit})") },
                                    enabled = editModeEnabled,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = mxTimes[item.id].orEmpty(),
                                    onValueChange = { if (editModeEnabled) mxTimes[item.id] = it },
                                    label = { Text("Max वेळ") },
                                    enabled = editModeEnabled,
                                    singleLine = true,
                                    modifier = Modifier.weight(0.9f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedTextField(
                                    value = mnVals[item.id].orEmpty(),
                                    onValueChange = { if (editModeEnabled) mnVals[item.id] = it },
                                    label = { Text("Min (${item.unit})") },
                                    enabled = editModeEnabled,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = mnTimes[item.id].orEmpty(),
                                    onValueChange = { if (editModeEnabled) mnTimes[item.id] = it },
                                    label = { Text("Min वेळ") },
                                    enabled = editModeEnabled,
                                    singleLine = true,
                                    modifier = Modifier.weight(0.9f)
                                )
                            }
                        }
                    }
                }

                if (editModeEnabled) {
                    item {
                        Button(
                            onClick = {
                                val out = mutableMapOf<String, Map<String, String>>()
                                MasterSartodiData.MINMAX_ITEMS.forEach { it ->
                                    val mx = mxVals[it.id].orEmpty().trim()
                                    val mxt = mxTimes[it.id].orEmpty().trim()
                                    val mn = mnVals[it.id].orEmpty().trim()
                                    val mnt = mnTimes[it.id].orEmpty().trim()
                                    if (mx.isNotBlank() || mn.isNotBlank()) {
                                        out[it.id] = mapOf("mx" to mx, "mxT" to mxt, "mn" to mn, "mnT" to mnt)
                                    }
                                }
                                onSaveMinMaxDay(selectedDate, out)
                            },
                            modifier = Modifier.fillMaxWidth().testTag("save_minmax_btn")
                        ) {
                            Text("$selectedDate Min-Max नोंद सेव्ह करा")
                        }
                    }
                }
            }
        } else {
            val parts = reportMonth.split("-")
            val yr = parts.getOrNull(0)?.toIntOrNull() ?: 2026
            val mon = parts.getOrNull(1)?.toIntOrNull() ?: 1
            val stats = remember(reportMonth, allMinMax) {
                MasterSartodiData.computeMinMaxMonthStats(reportMonth, allMinMax)
            }
            val reportOptions = listOf(
                "minmax" to "१. Monthly Minimum-Maximum Report (तारीख व वेळेसह)",
                "msedcl" to "२. MSEDCL Monthly Interruption Report (BD/SD/TP/HT/LS/PL)",
                "intr" to "३. T/P-H/T-S/D-B/D-L/S Interruption Report",
                "lossm" to "४. Monthly Substation Loss Calculation Report",
                "faults" to "५. यंत्रसामग्री बिघाड / दुरुस्ती अहवाल"
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { reportMonth = shiftYearMonth(reportMonth, -1) }) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "मागील महिना")
                        }
                        Text(
                            text = "${MasterSartodiData.MONTHS.getOrElse(mon - 1) { "" }} $yr",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        IconButton(onClick = { reportMonth = shiftYearMonth(reportMonth, 1) }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "पुढील महिना")
                        }
                    }
                }
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("PDF मध्ये समाविष्ट करायचे रिपोर्ट्स निवडा:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            reportOptions.forEach { (key, label) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = selectedSections[key] == true,
                                        onCheckedChange = { selectedSections[key] = it }
                                    )
                                    Text(label, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    val chosen = selectedSections.filterValues { it }.keys
                                    if (chosen.isEmpty()) return@Button
                                    val html = ReportHtmlBuilder.buildSelectedMonthlyPackHtml(
                                        sub = substation,
                                        yearMonth = reportMonth,
                                        selectedSections = chosen,
                                        allMinMax = allMinMax,
                                        allDailyLosses = allDailyLosses,
                                        mfOverrides = substation.lossMf,
                                        trips = trips,
                                        faults = faults
                                    )
                                    ReportHtmlBuilder.printHtmlReport(context, "Sartodi_ReportPack_$reportMonth", html, landscape = true)
                                },
                                modifier = Modifier.fillMaxWidth().testTag("print_report_pack_btn")
                            ) {
                                Icon(Icons.Default.Print, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("निवडलेले रिपोर्ट्स PDF प्रिंट / सेव्ह करा")
                            }
                        }
                    }
                }
                item {
                    Text("मासिक Min-Max सारांश (Preview)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                items(MasterSartodiData.MINMAX_ITEMS, key = { it.id }) { item ->
                    val s = stats[item.id]
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text(
                                text = "Max: ${s?.maxVal ?: "—"} | Min: ${s?.minVal ?: "—"} ${item.unit}",
                                style = MaterialTheme.typography.labelSmall,
                                color = GridBluePrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==================== MODULE 12: EQUIPMENT FAULT / REPAIR ====================
@Composable
fun FaultsRegisterScreen(
    substation: SubstationDoc,
    faults: List<FaultEntry>,
    todayIso: String,
    currentYearMonth: String,
    editModeEnabled: Boolean,
    onToggleEditMode: () -> Unit,
    onAddFault: (FaultEntry) -> Unit,
    onUpdateFaultStatus: (String, String, String) -> Unit,
    onDeleteFault: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    var date by remember { mutableStateOf(todayIso) }
    var equip by remember { mutableStateOf(MasterSartodiData.FAULT_EQUIP_OPTIONS.first()) }
    var bay by remember { mutableStateOf("") }
    var faultDesc by remember { mutableStateOf("") }
    var actionTaken by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(MasterSartodiData.FAULT_STATUS_OPTIONS.first()) }
    var oldSn by remember { mutableStateOf("") }
    var newSn by remember { mutableStateOf("") }
    var refNo by remember { mutableStateOf("") }

    val openCount = faults.count { it.status == "बिघाड (Open)" }
    val inProgCount = faults.count { it.status == "दुरुस्ती सुरू" }
    val closedCount = faults.size - openCount - inProgCount

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            ModuleHeaderBar(
                title = "१२. यंत्रसामग्री बिघाड / दुरुस्ती रजिस्टर",
                subtitle = "बिघाड नोंद, जुना → नवीन S/N व दुरुस्ती ट्रॅकिंग",
                onBack = onBack,
                trailing = {
                    IconButton(
                        onClick = {
                            val html = ReportHtmlBuilder.buildSelectedMonthlyPackHtml(
                                sub = substation,
                                yearMonth = currentYearMonth,
                                selectedSections = setOf("faults"),
                                allMinMax = emptyMap(),
                                allDailyLosses = emptyMap(),
                                mfOverrides = emptyMap(),
                                trips = emptyList(),
                                faults = faults
                            )
                            ReportHtmlBuilder.printHtmlReport(context, "Sartodi_Faults_$currentYearMonth", html, landscape = true)
                        },
                        modifier = Modifier.testTag("print_faults_pdf_btn")
                    ) {
                        Icon(Icons.Default.Print, contentDescription = "बिघाड अहवाल PDF")
                    }
                }
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiMetricCard("बिघाड (Open)", "$openCount", "तात्काळ दुरुस्ती", HighVoltageRed, Modifier.weight(1f))
                KpiMetricCard("सुरू / पूर्ण", "$inProgCount / $closedCount", "एकूण: ${faults.size}", EnergizedGreen, Modifier.weight(1f))
            }
        }
        if (!editModeEnabled) {
            item { ReadOnlyNoticeBanner(onEnableEdit = onToggleEditMode) }
        } else {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("नवीन यंत्रसामग्री बिघाड नोंद", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("बिघाड तारीख") }, singleLine = true, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = bay, onValueChange = { bay = it }, label = { Text("बे / फिडर") }, singleLine = true, modifier = Modifier.weight(1f))
                        }
                        DropdownSelectorField(
                            label = "यंत्रसामग्री निवडा",
                            selectedValue = equip,
                            options = MasterSartodiData.FAULT_EQUIP_OPTIONS,
                            onSelect = { equip = it },
                            testTag = "fault_equip_dd"
                        )
                        OutlinedTextField(value = faultDesc, onValueChange = { faultDesc = it }, label = { Text("बिघाडाचा तपशील *") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = actionTaken, onValueChange = { actionTaken = it }, label = { Text("केलेली कार्यवाही") }, modifier = Modifier.fillMaxWidth())
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = oldSn, onValueChange = { oldSn = it }, label = { Text("जुना S/N") }, singleLine = true, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = newSn, onValueChange = { newSn = it }, label = { Text("नवीन S/N") }, singleLine = true, modifier = Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DropdownSelectorField(
                                label = "स्थिती",
                                selectedValue = status,
                                options = MasterSartodiData.FAULT_STATUS_OPTIONS,
                                onSelect = { status = it },
                                modifier = Modifier.weight(1f),
                                testTag = "fault_status_dd"
                            )
                            OutlinedTextField(value = refNo, onValueChange = { refNo = it }, label = { Text("संदर्भ क्र.") }, singleLine = true, modifier = Modifier.weight(0.9f))
                        }
                        Button(
                            onClick = {
                                if (faultDesc.isBlank()) return@Button
                                onAddFault(
                                    FaultEntry(
                                        date = date.trim(),
                                        equip = equip,
                                        bay = bay.trim(),
                                        faultDesc = faultDesc.trim(),
                                        actionTaken = actionTaken.trim(),
                                        status = status,
                                        restoredDate = if (status == "दुरुस्त झाले" || status == "बदलले (Replaced)") todayIso else "",
                                        oldSn = oldSn.trim(),
                                        newSn = newSn.trim(),
                                        refNo = refNo.trim()
                                    )
                                )
                                faultDesc = ""; actionTaken = ""; oldSn = ""; newSn = ""; refNo = ""
                            },
                            modifier = Modifier.fillMaxWidth().testTag("save_fault_btn")
                        ) {
                            Text("बिघाड नोंद सेव्ह करा")
                        }
                    }
                }
            }
        }

        if (faults.isEmpty()) {
            item { EmptyRegisterHint("अद्याप कोणतीही बिघाड नोंद नाही.") }
        } else {
            items(faults, key = { it.id.ifBlank { it.date + it.equip } }) { f ->
                val isOpen = f.status == "बिघाड (Open)" || f.status == "दुरुस्ती सुरू"
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, if (isOpen) HighVoltageRed.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${MasterSartodiData.formatDmy(f.date)} • ${f.status}",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isOpen) HighVoltageRed else EnergizedGreen,
                                fontWeight = FontWeight.Bold
                            )
                            DeleteEntryButton(enabled = editModeEnabled, onClick = { onDeleteFault(f.id) })
                        }
                        Text("${f.equip} ${if (f.bay.isNotBlank()) "(${f.bay})" else ""}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(f.faultDesc, style = MaterialTheme.typography.bodySmall)
                        if (f.actionTaken.isNotBlank()) {
                            Text("कार्यवाही: ${f.actionTaken}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (f.oldSn.isNotBlank() || f.newSn.isNotBlank()) {
                            Text("S/N: ${f.oldSn.ifBlank { "-" }} → ${f.newSn.ifBlank { "-" }}", style = MaterialTheme.typography.labelSmall, color = GridBluePrimary)
                        }
                        if (editModeEnabled && isOpen) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                                OutlinedButton(
                                    onClick = { onUpdateFaultStatus(f.id, "दुरुस्त झाले", todayIso) }
                                ) {
                                    Text("दुरुस्त झाले (Close)", style = MaterialTheme.typography.labelSmall)
                                }
                                OutlinedButton(
                                    onClick = { onUpdateFaultStatus(f.id, "बदलले (Replaced)", todayIso) }
                                ) {
                                    Text("बदलले (Replaced)", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        } else if (f.restoredDate.isNotBlank()) {
                            Text(
                                text = "दुरुस्ती तारीख: ${MasterSartodiData.formatDmy(f.restoredDate)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = EnergizedGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

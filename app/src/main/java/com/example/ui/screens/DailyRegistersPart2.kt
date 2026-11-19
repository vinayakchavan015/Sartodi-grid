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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.BatteryEntry
import com.example.data.HandoverEntry
import com.example.data.IoEntry
import com.example.data.MasterSartodiData
import com.example.data.MonthlyFeederStat
import com.example.data.MonthlyLossStat
import com.example.data.NameplateDoc
import com.example.data.ReportHtmlBuilder
import com.example.data.SubstationDoc
import com.example.ui.components.DeleteEntryButton
import com.example.ui.components.DropdownSelectorField
import com.example.ui.components.ModuleHeaderBar
import com.example.ui.components.ReadOnlyNoticeBanner
import com.example.ui.theme.CautionAmber
import com.example.ui.theme.EnergizedGreen
import com.example.ui.theme.GridBluePrimary

// ==================== MODULE 7: SHIFT HANDOVER ====================
@Composable
fun HandoverRegisterScreen(
    handovers: List<HandoverEntry>,
    todayIso: String,
    editModeEnabled: Boolean,
    onToggleEditMode: () -> Unit,
    onAddHandover: (HandoverEntry) -> Unit,
    onDeleteHandover: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val shifts = listOf("सकाळ शिफ्ट (07:00 - 15:00)", "दुपार शिफ्ट (15:00 - 23:00)", "रात्र शिफ्ट (23:00 - 07:00)")
    var date by remember { mutableStateOf(todayIso) }
    var shift by remember { mutableStateOf(shifts.first()) }
    var relieved by remember { mutableStateOf("") }
    var relieving by remember { mutableStateOf("") }
    var runningStatus by remember { mutableStateOf("T/F-1, T/F-2 व सर्व 4 मुख्य फिडर्स चालू (Normal)") }
    var permits by remember { mutableStateOf("कोणताही LC प्रलंबित नाही") }
    var batteryVolt by remember { mutableStateOf("112.4 V DC") }
    var notes by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            ModuleHeaderBar(
                title = "७. शिफ्ट चार्ज हँडओव्हर रजिस्टर",
                subtitle = "कार्यभार हस्तांतरण, चालू फिडर्स व LC नोंदी (${handovers.size})",
                onBack = onBack
            )
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
                        OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("तारीख") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        DropdownSelectorField(label = "शिफ्ट", selectedValue = shift, options = shifts, onSelect = { shift = it }, testTag = "handover_shift_dd")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = relieved, onValueChange = { relieved = it }, label = { Text("कार्यभार देणारे") }, singleLine = true, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = relieving, onValueChange = { relieving = it }, label = { Text("कार्यभार घेणारे") }, singleLine = true, modifier = Modifier.weight(1f))
                        }
                        OutlinedTextField(value = runningStatus, onValueChange = { runningStatus = it }, label = { Text("चालू फिडर्स व ट्रान्सफॉर्मर स्थिती") }, modifier = Modifier.fillMaxWidth())
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = permits, onValueChange = { permits = it }, label = { Text("प्रलंबित LC / परमिट") }, singleLine = true, modifier = Modifier.weight(1.3f))
                            OutlinedTextField(value = batteryVolt, onValueChange = { batteryVolt = it }, label = { Text("110V DC") }, singleLine = true, modifier = Modifier.weight(0.7f))
                        }
                        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("विशेष सूचना / शेरा") }, modifier = Modifier.fillMaxWidth())
                        Button(
                            onClick = {
                                if (relieved.isBlank() || relieving.isBlank()) return@Button
                                onAddHandover(
                                    HandoverEntry(
                                        date = date.trim(),
                                        shift = shift,
                                        relieved = relieved.trim(),
                                        relieving = relieving.trim(),
                                        runningStatus = runningStatus.trim(),
                                        permits = permits.trim(),
                                        batteryVolt = batteryVolt.trim(),
                                        notes = notes.trim()
                                    )
                                )
                                relieved = ""
                                relieving = ""
                                notes = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_handover_btn")
                        ) {
                            Text("शिफ्ट हँडओव्हर सेव्ह करा")
                        }
                    }
                }
            }
        }
        if (handovers.isEmpty()) {
            item { EmptyRegisterHint("अद्याप शिफ्ट हँडओव्हर नोंद नाही.") }
        } else {
            items(handovers, key = { it.id.ifBlank { it.date + it.shift } }) { item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("${MasterSartodiData.formatDmy(item.date)} • ${item.shift}", style = MaterialTheme.typography.labelMedium, color = GridBluePrimary, fontWeight = FontWeight.Bold)
                            Text("${item.relieved} → ${item.relieving}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(item.runningStatus, style = MaterialTheme.typography.bodySmall)
                            Text("LC: ${item.permits} | DC: ${item.batteryVolt}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        DeleteEntryButton(enabled = editModeEnabled, onClick = { onDeleteHandover(item.id) })
                    }
                }
            }
        }
    }
}

// ==================== MODULE 8: BATTERY BANK ====================
@Composable
fun BatteryRegisterScreen(
    entries: List<BatteryEntry>,
    todayIso: String,
    editModeEnabled: Boolean,
    onToggleEditMode: () -> Unit,
    onAddBattery: (BatteryEntry) -> Unit,
    onDeleteBattery: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    var date by remember { mutableStateOf(todayIso) }
    var dcVolt by remember { mutableStateOf("112.5 V") }
    var charger by remember { mutableStateOf("Float 2.1 A (Normal)") }
    var cellVolt by remember { mutableStateOf("2.05 V / 1.210 Sp.Gr.") }
    var remark by remember { mutableStateOf("सर्व 55 Cells व चार्जर सुस्थितीत") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            ModuleHeaderBar(
                title = "८. 110V DC बॅटरी बँक तपासणी",
                subtitle = "55 Cells व्होल्टेज, Float/Boost चार्जर व Specific Gravity (${entries.size})",
                onBack = onBack
            )
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
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("तारीख") }, singleLine = true, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = dcVolt, onValueChange = { dcVolt = it }, label = { Text("DC व्होल्टेज") }, singleLine = true, modifier = Modifier.weight(1f))
                        }
                        OutlinedTextField(value = charger, onValueChange = { charger = it }, label = { Text("चार्जर स्थिती (Float/Boost & Amp)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = cellVolt, onValueChange = { cellVolt = it }, label = { Text("सरासरी सेल व्होल्टेज / Sp. Gravity") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = remark, onValueChange = { remark = it }, label = { Text("शेरा / डिस्टिल्ड वॉटर टॉप-अप") }, modifier = Modifier.fillMaxWidth())
                        Button(
                            onClick = {
                                onAddBattery(
                                    BatteryEntry(
                                        date = date.trim(),
                                        dcVolt = dcVolt.trim(),
                                        charger = charger.trim(),
                                        cellVolt = cellVolt.trim(),
                                        remark = remark.trim()
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_battery_btn")
                        ) {
                            Text("बॅटरी तपासणी सेव्ह करा")
                        }
                    }
                }
            }
        }
        if (entries.isEmpty()) {
            item { EmptyRegisterHint("अद्याप बॅटरी बँक तपासणी नोंद नाही.") }
        } else {
            items(entries, key = { it.id.ifBlank { it.date + it.dcVolt } }) { item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("${MasterSartodiData.formatDmy(item.date)} • ${item.dcVolt}", style = MaterialTheme.typography.titleSmall, color = EnergizedGreen, fontWeight = FontWeight.Bold)
                            Text("चार्जर: ${item.charger}", style = MaterialTheme.typography.bodySmall)
                            Text("Cell/Sp.Gr: ${item.cellVolt}", style = MaterialTheme.typography.bodySmall)
                            if (item.remark.isNotBlank()) {
                                Text(item.remark, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        DeleteEntryButton(enabled = editModeEnabled, onClick = { onDeleteBattery(item.id) })
                    }
                }
            }
        }
    }
}

// ==================== MODULE 9: INWARD/OUTWARD & A4 PDF ====================
@Composable
fun PdfAndIoScreen(
    substation: SubstationDoc,
    ioList: List<IoEntry>,
    auxMeters: List<NameplateDoc>,
    lossHistory: List<MonthlyLossStat>,
    getMonthlyFeeders: (Int, Int) -> Pair<List<MonthlyFeederStat>, String>,
    todayIso: String,
    editModeEnabled: Boolean,
    onToggleEditMode: () -> Unit,
    onAddIo: (IoEntry) -> Unit,
    onDeleteIo: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    var pdfYear by remember { mutableIntStateOf(2026) }
    var pdfMonthIdx by remember { mutableIntStateOf(9) }

    var date by remember { mutableStateOf(todayIso) }
    var ioType by remember { mutableStateOf("आवक (Inward)") }
    var refNo by remember { mutableStateOf("") }
    var party by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            ModuleHeaderBar(
                title = "९. आवक-जावक व मासिक PDF अहवाल",
                subtitle = "A4 प्रिंट-रेडी मासिक अहवाल + कार्यालयीन आवक-जावक (${ioList.size})",
                onBack = onBack
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("मासिक एकत्रित A4 PDF अहवाल तयार करा", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(2024, 2025, 2026).forEach { yr ->
                            FilterChip(selected = pdfYear == yr, onClick = { pdfYear = yr }, label = { Text("$yr") })
                        }
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(MasterSartodiData.MONTHS.indices.toList()) { idx ->
                            FilterChip(
                                selected = pdfMonthIdx == idx,
                                onClick = { pdfMonthIdx = idx },
                                label = { Text(MasterSartodiData.MONTHS[idx].take(3)) }
                            )
                        }
                    }
                    Button(
                        onClick = {
                            val (feeders, _) = getMonthlyFeeders(pdfYear, pdfMonthIdx)
                            val loss = lossHistory.firstOrNull { it.year == pdfYear && it.monthIdx == pdfMonthIdx }
                            val html = ReportHtmlBuilder.buildMonthlyA4ReportHtml(
                                sub = substation,
                                year = pdfYear,
                                monthIdx = pdfMonthIdx,
                                feeders = feeders,
                                loss = loss,
                                auxMeters = auxMeters
                            )
                            ReportHtmlBuilder.printHtmlReport(
                                context = context,
                                jobName = "Sartodi_Monthly_${MasterSartodiData.MONTHS[pdfMonthIdx]}_$pdfYear",
                                html = html,
                                landscape = true
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("print_monthly_a4_pdf_btn")
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${MasterSartodiData.MONTHS[pdfMonthIdx]} $pdfYear A4 PDF प्रिंट / सेव्ह करा")
                    }
                }
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
                        Text("आवक / जावक पत्र नोंद", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("तारीख") }, singleLine = true, modifier = Modifier.weight(1f))
                            DropdownSelectorField(
                                label = "प्रकार",
                                selectedValue = ioType,
                                options = listOf("आवक (Inward)", "जावक (Outward)"),
                                onSelect = { ioType = it },
                                modifier = Modifier.weight(1f),
                                testTag = "io_type_dd"
                            )
                        }
                        OutlinedTextField(value = refNo, onValueChange = { refNo = it }, label = { Text("पत्र क्रमांक / संदर्भ क्र.") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = party, onValueChange = { party = it }, label = { Text("कोणाकडून / कोणास (कार्यालय)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("पत्राचा विषय") }, modifier = Modifier.fillMaxWidth())
                        Button(
                            onClick = {
                                if (subject.isBlank()) return@Button
                                onAddIo(
                                    IoEntry(
                                        date = date.trim(),
                                        type = ioType,
                                        refNo = refNo.trim(),
                                        party = party.trim(),
                                        subject = subject.trim()
                                    )
                                )
                                refNo = ""
                                party = ""
                                subject = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_io_btn")
                        ) {
                            Text("आवक-जावक नोंद सेव्ह करा")
                        }
                    }
                }
            }
        }

        if (ioList.isEmpty()) {
            item { EmptyRegisterHint("अद्याप आवक-जावक नोंद नाही.") }
        } else {
            items(ioList, key = { it.id.ifBlank { it.date + it.refNo } }) { item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "${MasterSartodiData.formatDmy(item.date)} • ${item.type} • ${item.refNo}",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (item.type.contains("आवक")) EnergizedGreen else CautionAmber,
                                fontWeight = FontWeight.Bold
                            )
                            Text(item.subject, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(item.party, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        DeleteEntryButton(enabled = editModeEnabled, onClick = { onDeleteIo(item.id) })
                    }
                }
            }
        }
    }
}

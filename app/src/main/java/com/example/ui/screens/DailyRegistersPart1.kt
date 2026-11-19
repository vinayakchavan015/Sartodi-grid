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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.EnergyEntry
import com.example.data.LoadEntry
import com.example.data.MasterSartodiData
import com.example.data.TripEntry
import com.example.ui.components.DeleteEntryButton
import com.example.ui.components.DropdownSelectorField
import com.example.ui.components.KpiMetricCard
import com.example.ui.components.ModuleHeaderBar
import com.example.ui.components.ReadOnlyNoticeBanner
import com.example.ui.theme.CautionAmber
import com.example.ui.theme.EnergizedGreen
import com.example.ui.theme.GridBluePrimary
import com.example.ui.theme.HighVoltageRed

// ==================== MODULE 4: TRIPPING / SHUTDOWN ====================
@Composable
fun TrippingRegisterScreen(
    trips: List<TripEntry>,
    todayIso: String,
    editModeEnabled: Boolean,
    onToggleEditMode: () -> Unit,
    onAddTrip: (TripEntry) -> Unit,
    onDeleteTrip: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    var date by remember { mutableStateOf(todayIso) }
    var type by remember { mutableStateOf(MasterSartodiData.TRIP_TYPES.first()) }
    var feeder by remember { mutableStateOf(MasterSartodiData.TRIP_FEEDER_OPTIONS[5]) }
    var offTime by remember { mutableStateOf("10:00") }
    var onTime by remember { mutableStateOf("10:15") }
    var relay by remember { mutableStateOf("") }
    var remark by remember { mutableStateOf("") }

    val previewMin = MasterSartodiData.calcDurationMinutes(offTime, onTime)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            ModuleHeaderBar(
                title = "४. दैनंदिन ट्रिपिंग / शटडाउन रजिस्टर",
                subtitle = "TP, HT, SD, BD, LS, PL व कालावधी ऑटो-गणना (${trips.size} नोंदी)",
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
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("नवीन ट्रिपिंग / शटडाउन नोंद", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("तारीख (YYYY-MM-DD)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownSelectorField(
                            label = "प्रकार (TP/HT/SD/BD/LS/PL)",
                            selectedValue = type,
                            options = MasterSartodiData.TRIP_TYPES,
                            onSelect = { type = it },
                            testTag = "trip_type_dropdown"
                        )
                        DropdownSelectorField(
                            label = "फिडर / उपकरण",
                            selectedValue = feeder,
                            options = MasterSartodiData.TRIP_FEEDER_OPTIONS,
                            onSelect = { feeder = it },
                            testTag = "trip_feeder_dropdown"
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = offTime,
                                onValueChange = { offTime = it },
                                label = { Text("बंद वेळ (HH:MM)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = onTime,
                                onValueChange = { onTime = it },
                                label = { Text("चालू वेळ (HH:MM)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Text(
                            text = "कालावधी: ${MasterSartodiData.formatDurationMr(previewMin)} ($previewMin मि.)",
                            style = MaterialTheme.typography.labelMedium,
                            color = GridBluePrimary,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = relay,
                            onValueChange = { relay = it },
                            label = { Text("रिले इंडिकेशन / कारण (O/C, E/F, LC इ.)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = remark,
                            onValueChange = { remark = it },
                            label = { Text("शेरा / ऑपरेटर") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                if (date.isBlank()) return@Button
                                onAddTrip(
                                    TripEntry(
                                        date = date.trim(),
                                        type = type,
                                        feeder = feeder,
                                        offTime = offTime.trim(),
                                        onTime = onTime.trim(),
                                        duration = MasterSartodiData.formatDurationMr(previewMin),
                                        durationMin = previewMin,
                                        relay = relay.trim(),
                                        remark = remark.trim()
                                    )
                                )
                                relay = ""
                                remark = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_trip_btn")
                        ) {
                            Text("ट्रिपिंग नोंद सेव्ह करा")
                        }
                    }
                }
            }
        }

        if (trips.isEmpty()) {
            item { EmptyRegisterHint("अद्याप कोणतीही ट्रिपिंग / शटडाउन नोंद नाही.") }
        } else {
            items(trips, key = { it.id.ifBlank { it.date + it.offTime } }) { item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                text = "${MasterSartodiData.formatDmy(item.date)} • ${item.type.substringBefore(" -")}",
                                style = MaterialTheme.typography.labelMedium,
                                color = HighVoltageRed,
                                fontWeight = FontWeight.Bold
                            )
                            Text(item.feeder, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${item.offTime} → ${item.onTime} (${item.duration})",
                                style = MaterialTheme.typography.bodySmall,
                                color = GridBluePrimary
                            )
                            if (item.relay.isNotBlank() || item.remark.isNotBlank()) {
                                Text(
                                    text = listOf(item.relay, item.remark).filter { it.isNotBlank() }.joinToString(" | "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        DeleteEntryButton(enabled = editModeEnabled, onClick = { onDeleteTrip(item.id) })
                    }
                }
            }
        }
    }
}

// ==================== MODULE 5: ENERGY & LOSSES ====================
@Composable
fun EnergyRegisterScreen(
    entries: List<EnergyEntry>,
    todayIso: String,
    editModeEnabled: Boolean,
    onToggleEditMode: () -> Unit,
    onAddEnergy: (EnergyEntry) -> Unit,
    onDeleteEnergy: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    var date by remember { mutableStateOf(todayIso) }
    var meterIdx by remember { mutableIntStateOf(0) }
    var openStr by remember { mutableStateOf("") }
    var closeStr by remember { mutableStateOf("") }
    val selectedOption = MasterSartodiData.ENERGY_METER_OPTIONS[meterIdx]
    var mfStr by remember(meterIdx) { mutableStateOf(selectedOption.defaultMf.toInt().toString()) }

    val openVal = openStr.toDoubleOrNull() ?: 0.0
    val closeVal = closeStr.toDoubleOrNull() ?: 0.0
    val mfVal = mfStr.toDoubleOrNull() ?: selectedOption.defaultMf
    val calcUnits = ((closeVal - openVal) * mfVal).coerceAtLeast(0.0)

    val sumIn = entries.filter { it.category == "33kV_IN" }.sumOf { it.units }
    val sumLv = entries.filter { it.category == "11kV_LV" }.sumOf { it.units }
    val sumOut = entries.filter { it.category == "11kV_OUT" }.sumOf { it.units }
    val lossPct = if (sumIn > 0.0) ((sumIn - sumOut) / sumIn) * 100.0 else 0.0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            ModuleHeaderBar(
                title = "५. दैनिक / मासिक एनर्जी व लॉसेस",
                subtitle = "ऑटो MF (600, 900, 80, 60, 40) व थेट लॉस %",
                onBack = onBack
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiMetricCard(
                    label = "33kV IN",
                    value = "${MasterSartodiData.fmtIndian(sumIn)} kWh",
                    sub = "LV: ${MasterSartodiData.fmtIndian(sumLv)}",
                    accentColor = GridBluePrimary,
                    modifier = Modifier.weight(1f)
                )
                KpiMetricCard(
                    label = "11kV OUT / Loss",
                    value = "${"%.2f".format(lossPct)} %",
                    sub = "OUT: ${MasterSartodiData.fmtIndian(sumOut)} kWh",
                    accentColor = if (lossPct > 2.0) HighVoltageRed else EnergizedGreen,
                    modifier = Modifier.weight(1f)
                )
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
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("नवीन मीटर रीडिंग नोंद", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("तारीख (YYYY-MM-DD)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownSelectorField(
                            label = "SECURE मीटर निवडा",
                            selectedValue = selectedOption.label,
                            options = MasterSartodiData.ENERGY_METER_OPTIONS.map { it.label },
                            onSelect = { lbl ->
                                val idx = MasterSartodiData.ENERGY_METER_OPTIONS.indexOfFirst { it.label == lbl }
                                if (idx >= 0) meterIdx = idx
                            },
                            testTag = "energy_meter_dropdown"
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = openStr,
                                onValueChange = { openStr = it },
                                label = { Text("सुरुवातीचे (Open)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = closeStr,
                                onValueChange = { closeStr = it },
                                label = { Text("अखेरचे (Close)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = mfStr,
                                onValueChange = { mfStr = it },
                                label = { Text("M.F.") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier.weight(0.7f)
                            )
                        }
                        Text(
                            text = "युनिट्स (kWh): ${MasterSartodiData.fmtIndian(calcUnits, 2)}",
                            style = MaterialTheme.typography.titleSmall,
                            color = EnergizedGreen,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Button(
                            onClick = {
                                if (openStr.isBlank() || closeStr.isBlank()) return@Button
                                onAddEnergy(
                                    EnergyEntry(
                                        date = date.trim(),
                                        meter = selectedOption.label,
                                        category = selectedOption.category,
                                        openReading = openVal,
                                        closeReading = closeVal,
                                        mf = mfVal,
                                        units = calcUnits
                                    )
                                )
                                openStr = ""
                                closeStr = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_energy_btn")
                        ) {
                            Text("एनर्जी रीडिंग सेव्ह करा")
                        }
                    }
                }
            }
        }

        if (entries.isEmpty()) {
            item { EmptyRegisterHint("अद्याप एनर्जी मीटर रीडिंग नोंद नाही.") }
        } else {
            items(entries, key = { it.id.ifBlank { it.date + it.meter } }) { item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${MasterSartodiData.formatDmy(item.date)} • ${item.category}",
                                style = MaterialTheme.typography.labelSmall,
                                color = GridBluePrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(item.meter, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${item.openReading} → ${item.closeReading} × MF ${item.mf.toInt()} = ${MasterSartodiData.fmtIndian(item.units)} kWh",
                                style = MaterialTheme.typography.bodySmall,
                                color = EnergizedGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        DeleteEntryButton(enabled = editModeEnabled, onClick = { onDeleteEnergy(item.id) })
                    }
                }
            }
        }
    }
}

// ==================== MODULE 6: VOLTAGE & LOAD ====================
@Composable
fun LoadsRegisterScreen(
    loads: List<LoadEntry>,
    todayIso: String,
    editModeEnabled: Boolean,
    onToggleEditMode: () -> Unit,
    onAddLoad: (LoadEntry) -> Unit,
    onDeleteLoad: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    var date by remember { mutableStateOf(todayIso) }
    var timeLabel by remember { mutableStateOf("Peak 19:00") }
    var v33 by remember { mutableStateOf("33.4 / 31.2 kV") }
    var v11 by remember { mutableStateOf("11.6 / 10.5 kV") }
    var tfLoad by remember { mutableStateOf("T/F-1: 42A | T/F-2: 38A") }
    var fdr1 by remember { mutableStateOf("सफाळा: 120A | DFCCIL: 1A") }
    var fdr2 by remember { mutableStateOf("नवघर: 62A | पारगाव: 65A") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            ModuleHeaderBar(
                title = "६. व्होल्टेज व लोड Min/Max रजिस्टर",
                subtitle = "33kV/11kV बस व्होल्टेज, T/F व फिडर लोड नोंदी (${loads.size})",
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
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("तारीख") }, singleLine = true, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = timeLabel, onValueChange = { timeLabel = it }, label = { Text("वेळ / शिफ्ट") }, singleLine = true, modifier = Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = v33, onValueChange = { v33 = it }, label = { Text("33kV Max/Min") }, singleLine = true, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = v11, onValueChange = { v11 = it }, label = { Text("11kV Max/Min") }, singleLine = true, modifier = Modifier.weight(1f))
                        }
                        OutlinedTextField(value = tfLoad, onValueChange = { tfLoad = it }, label = { Text("T/F-1 व T/F-2 लोड (Amps)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = fdr1, onValueChange = { fdr1 = it }, label = { Text("सफाळा (205) व DFCCIL (204)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = fdr2, onValueChange = { fdr2 = it }, label = { Text("नवघर (203) व पारगाव (201)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        Button(
                            onClick = {
                                onAddLoad(
                                    LoadEntry(
                                        date = date.trim(),
                                        timeLabel = timeLabel.trim(),
                                        v33 = v33.trim(),
                                        v11 = v11.trim(),
                                        tfLoad = tfLoad.trim(),
                                        feederGroup1 = fdr1.trim(),
                                        feederGroup2 = fdr2.trim()
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_load_btn")
                        ) {
                            Text("लोड नोंद सेव्ह करा")
                        }
                    }
                }
            }
        }
        if (loads.isEmpty()) {
            item { EmptyRegisterHint("अद्याप लोड / व्होल्टेज नोंद नाही.") }
        } else {
            items(loads, key = { it.id.ifBlank { it.date + it.timeLabel } }) { item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "${MasterSartodiData.formatDmy(item.date)} • ${item.timeLabel}",
                                style = MaterialTheme.typography.labelMedium,
                                color = CautionAmber,
                                fontWeight = FontWeight.Bold
                            )
                            Text("33kV: ${item.v33} | 11kV: ${item.v11}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Text(item.tfLoad, style = MaterialTheme.typography.bodySmall)
                            Text("${item.feederGroup1} • ${item.feederGroup2}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        DeleteEntryButton(enabled = editModeEnabled, onClick = { onDeleteLoad(item.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyRegisterHint(text: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
        )
    }
}

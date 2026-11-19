package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.NameplateDoc
import com.example.ui.components.ModuleHeaderBar
import com.example.ui.theme.CautionAmber
import com.example.ui.theme.EnergizedGreen
import com.example.ui.theme.GridBluePrimary
import com.example.ui.theme.HighVoltageRed

private val EQUIPMENT_CATEGORIES = listOf(
    "transformers" to "ट्रान्सफॉर्मर्स",
    "cts" to "C.T. (R-Y-B)",
    "pts" to "P.T. (R-Y-B)",
    "vcbs" to "VCB ब्रेकर्स",
    "aux" to "SECURE मीटर्स व इतर"
)

@Composable
fun EquipmentScreen(
    nameplates: List<NameplateDoc>,
    editModeEnabled: Boolean,
    onOpenDetail: (NameplateDoc) -> Unit,
    onAddNew: (NameplateDoc) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    var selectedCategory by remember { mutableStateOf("transformers") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filtered = remember(nameplates, selectedCategory) {
        nameplates.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            ModuleHeaderBar(
                title = "३. यंत्रसामग्री व R-Y-B नेमप्लेट मास्टर",
                subtitle = "${nameplates.size} उपकरणे • SECURE मीटर्स, R-Y-B सिरीयल व रिले सेटिंग्ज",
                onBack = onBack,
                trailing = if (editModeEnabled) {
                    {
                        OutlinedButton(
                            onClick = { showAddDialog = true },
                            modifier = Modifier.testTag("add_equipment_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("जोडा")
                        }
                    }
                } else null
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(EQUIPMENT_CATEGORIES) { (key, label) ->
                    val count = nameplates.count { it.category == key }
                    FilterChip(
                        selected = selectedCategory == key,
                        onClick = { selectedCategory = key },
                        label = { Text("$label ($count)", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("eq_cat_$key")
                    )
                }
            }
        }

        items(filtered, key = { it.id.ifBlank { it.name } }) { item ->
            EquipmentCard(item = item, onClick = { onOpenDetail(item) })
        }
    }

    if (showAddDialog) {
        NameplateEditDialog(
            doc = NameplateDoc(category = selectedCategory),
            editModeEnabled = true,
            isNew = true,
            onDismiss = { showAddDialog = false },
            onSave = { doc ->
                onAddNew(doc)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun EquipmentCard(item: NameplateDoc, onClick: () -> Unit) {
    val isSpare = item.status.contains("Spare", ignoreCase = true)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("eq_item_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.bay,
                    style = MaterialTheme.typography.labelMedium,
                    color = GridBluePrimary,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = (if (isSpare) CautionAmber else EnergizedGreen).copy(alpha = 0.14f)
                ) {
                    Text(
                        text = item.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSpare) CautionAmber else EnergizedGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Make: ${item.make.ifBlank { "-" }}  •  S/N: ${item.serialNo.ifBlank { "-" }}  •  Year: ${item.year.ifBlank { "-" }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (item.category == "cts" || item.category == "pts") {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                PhaseLine("R-Phase", HighVoltageRed, item.specs["r_serial"] ?: "-", item.specs["r_met"] ?: "-")
                PhaseLine("Y-Phase", CautionAmber, item.specs["y_serial"] ?: "-", item.specs["y_met"] ?: "-")
                PhaseLine("B-Phase", GridBluePrimary, item.specs["b_serial"] ?: "-", item.specs["b_met"] ?: "-")
            } else {
                val summarySpec = item.specs["spec"]
                    ?: item.specs["capacity"]?.let { "$it | %Z: ${item.specs["impedance"] ?: "-"} | Oil: ${item.specs["oilQty"] ?: "-"}" }
                    ?: item.specs["ratedKv"]?.let { "$it / ${item.specs["ratedAmp"] ?: "-"} / ${item.specs["breakingKa"] ?: "-"}" }
                if (!summarySpec.isNullOrBlank()) {
                    Text(
                        text = summarySpec,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun PhaseLine(phase: String, color: androidx.compose.ui.graphics.Color, sn: String, cls: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$phase: $sn",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = cls,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun NameplateEditDialog(
    doc: NameplateDoc,
    editModeEnabled: Boolean,
    isNew: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (NameplateDoc) -> Unit
) {
    var editing by remember { mutableStateOf(isNew) }
    var name by remember(doc) { mutableStateOf(doc.name) }
    var bay by remember(doc) { mutableStateOf(doc.bay) }
    var status by remember(doc) { mutableStateOf(doc.status) }
    var make by remember(doc) { mutableStateOf(doc.make) }
    var serialNo by remember(doc) { mutableStateOf(doc.serialNo) }
    var year by remember(doc) { mutableStateOf(doc.year) }
    var specText by remember(doc) { mutableStateOf(doc.specs["spec"] ?: "") }
    var notes by remember(doc) { mutableStateOf(doc.specs["notes"] ?: "") }
    var rSn by remember(doc) { mutableStateOf(doc.specs["r_serial"] ?: "") }
    var ySn by remember(doc) { mutableStateOf(doc.specs["y_serial"] ?: "") }
    var bSn by remember(doc) { mutableStateOf(doc.specs["b_serial"] ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isNew) "नवीन उपकरण नेमप्लेट जोडा" else doc.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!editing) {
                    DetailRow("बे / विभाग", doc.bay)
                    DetailRow("स्थिती", doc.status)
                    DetailRow("कंपनी (Make)", doc.make)
                    DetailRow("सिरीयल नं.", doc.serialNo)
                    DetailRow("वर्ष", doc.year)
                    HorizontalDivider()
                    doc.specs.forEach { (k, v) ->
                        if (v.isNotBlank()) DetailRow(k, v)
                    }
                } else {
                    OutlinedTextField(value = bay, onValueChange = { bay = it }, label = { Text("बे / फिडर") }, singleLine = true)
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("उपकरणाचे नाव") }, singleLine = true)
                    OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("स्थिती (In Service / Spare)") }, singleLine = true)
                    OutlinedTextField(value = make, onValueChange = { make = it }, label = { Text("कंपनी (Make)") }, singleLine = true)
                    OutlinedTextField(value = serialNo, onValueChange = { serialNo = it }, label = { Text("सिरीयल नंबर") }, singleLine = true)
                    OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("वर्ष") }, singleLine = true)
                    if (doc.category == "cts" || doc.category == "pts") {
                        OutlinedTextField(value = rSn, onValueChange = { rSn = it }, label = { Text("R-Phase S/N") }, singleLine = true)
                        OutlinedTextField(value = ySn, onValueChange = { ySn = it }, label = { Text("Y-Phase S/N") }, singleLine = true)
                        OutlinedTextField(value = bSn, onValueChange = { bSn = it }, label = { Text("B-Phase S/N") }, singleLine = true)
                    } else {
                        OutlinedTextField(value = specText, onValueChange = { specText = it }, label = { Text("तांत्रिक तपशील (CT/MF/Relay)") })
                    }
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("टिप्पणी (Notes)") })
                }
            }
        },
        confirmButton = {
            if (editing) {
                Button(
                    onClick = {
                        val updatedSpecs = doc.specs.toMutableMap()
                        if (specText.isNotBlank()) updatedSpecs["spec"] = specText.trim()
                        if (notes.isNotBlank()) updatedSpecs["notes"] = notes.trim()
                        if (doc.category == "cts" || doc.category == "pts") {
                            updatedSpecs["r_serial"] = rSn.trim()
                            updatedSpecs["y_serial"] = ySn.trim()
                            updatedSpecs["b_serial"] = bSn.trim()
                        }
                        onSave(
                            doc.copy(
                                bay = bay.trim(),
                                name = name.trim().ifBlank { "Unnamed Equipment" },
                                status = status.trim().ifBlank { "In Service (ON)" },
                                make = make.trim(),
                                serialNo = serialNo.trim(),
                                year = year.trim(),
                                specs = updatedSpecs
                            )
                        )
                    },
                    modifier = Modifier.testTag("save_nameplate_btn")
                ) {
                    Text("सेव्ह करा")
                }
            } else if (editModeEnabled) {
                Button(
                    onClick = { editing = true },
                    modifier = Modifier.testTag("edit_nameplate_btn")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("एडिट करा")
                }
            } else {
                TextButton(onClick = onDismiss) { Text("बंद करा") }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("रद्द करा") }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value.ifBlank { "-" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

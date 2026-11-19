package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.MasterSartodiData
import com.example.data.MonthlyLossStat
import com.example.data.SubstationDoc
import com.example.data.SubstationModule
import com.example.ui.SubstationDataBundle
import com.example.ui.components.KpiMetricCard
import com.example.ui.theme.CautionAmber
import com.example.ui.theme.EnergizedGreen
import com.example.ui.theme.GridBlueDark
import com.example.ui.theme.GridBluePrimary
import com.example.ui.theme.GridCyanAccent
import com.example.ui.theme.HighVoltageRed

@Composable
fun HomeDashboardScreen(
    substation: SubstationDoc,
    bundle: SubstationDataBundle,
    lossHistory: List<MonthlyLossStat>,
    editModeEnabled: Boolean,
    onToggleEditMode: () -> Unit,
    onOpenModule: (SubstationModule) -> Unit
) {
    val latestLoss = lossHistory.lastOrNull()
    val openFaultCount = bundle.faults.count {
        it.status == "बिघाड (Open)" || it.status == "दुरुस्ती सुरू"
    }
    val modules = SubstationModule.entries.filter { it != SubstationModule.HOME }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            HeroBannerCard(
                substation = substation,
                editModeEnabled = editModeEnabled,
                onToggleEditMode = onToggleEditMode
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiMetricCard(
                    label = "33kV इनकमर (${latestLoss?.month?.take(3) ?: "SEP"} ${latestLoss?.year ?: 2026})",
                    value = latestLoss?.let { "${MasterSartodiData.fmtIndian(it.incomerKwh)} kWh" } ?: "—",
                    sub = "2×5 MVA Power T/F",
                    accentColor = GridBluePrimary,
                    modifier = Modifier.weight(1f)
                )
                KpiMetricCard(
                    label = "11kV आउटगोइंग",
                    value = latestLoss?.let { "${MasterSartodiData.fmtIndian(it.outgoingKwh)} kWh" } ?: "—",
                    sub = "4 कार्यरत + 2 स्पेअर बे",
                    accentColor = EnergizedGreen,
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
                    label = "उपकेंद्र लॉस %",
                    value = latestLoss?.let { "${it.lossPct} %" } ?: "—",
                    sub = "फरक: ${MasterSartodiData.fmtIndian(latestLoss?.diffKwh)} kWh",
                    accentColor = CautionAmber,
                    modifier = Modifier.weight(1f)
                )
                KpiMetricCard(
                    label = "नोंदी / प्रलंबित बिघाड",
                    value = "${bundle.trips.size} ट्रिप / $openFaultCount Open",
                    sub = "रिअल-टाइम फायरबेस सिंक",
                    accentColor = if (openFaultCount > 0) HighVoltageRed else GridCyanAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(
                text = "उपकेंद्र डिजिटल मॉड्यूल्स (१ ते १२)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 4.dp, start = 2.dp)
            )
        }

        val rows = modules.chunked(2)
        items(rows.size) { rowIdx ->
            val pair = rows[rowIdx]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                pair.forEach { mod ->
                    ModuleTileCard(
                        module = mod,
                        dynamicBadge = dynamicBadgeFor(mod, bundle, openFaultCount),
                        onClick = { onOpenModule(mod) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HeroBannerCard(
    substation: SubstationDoc,
    editModeEnabled: Boolean,
    onToggleEditMode: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(listOf(GridBlueDark, GridBluePrimary, GridCyanAccent))
            )
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = Color.White.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "क्लाउड सिंक",
                            tint = Color(0xFF6EE7B7),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Firebase रिअल-टाइम सिंक सुरू",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = if (editModeEnabled) Color(0xFFF59E0B) else Color.White.copy(alpha = 0.22f),
                    modifier = Modifier
                        .clickable { onToggleEditMode() }
                        .testTag("hero_mode_toggle")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (editModeEnabled) Icons.Default.Edit else Icons.Default.Visibility,
                            contentDescription = "मोड बदला",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (editModeEnabled) "ॲडमिन (Edit)" else "सदस्य व्यू",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = substation.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "${substation.division} • कोड: ${substation.code}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun ModuleTileCard(
    module: SubstationModule,
    dynamicBadge: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, accent) = moduleIconAndColor(module)
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("module_card_${module.name.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accent.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = module.titleMr,
                        tint = accent,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = accent.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = dynamicBadge,
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = "${module.numberLabel}. ${module.titleMr}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = module.subtitleMr,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun dynamicBadgeFor(
    mod: SubstationModule,
    bundle: SubstationDataBundle,
    openFaults: Int
): String = when (mod) {
    SubstationModule.TRIPPING -> "${bundle.trips.size} नोंदी"
    SubstationModule.ENERGY -> "${bundle.energy.size} नोंदी"
    SubstationModule.LOADS -> "${bundle.loads.size} नोंदी"
    SubstationModule.HANDOVER -> "${bundle.handovers.size} नोंदी"
    SubstationModule.BATTERY -> "${bundle.battery.size} नोंदी"
    SubstationModule.PDF_IO -> "${bundle.io.size} + PDF"
    SubstationModule.DAILY_LOSSES -> "${bundle.dailyLosses.size} दिवस"
    SubstationModule.MINMAX_REPORT -> "${bundle.minmax.size} दिवस"
    SubstationModule.FAULTS -> if (openFaults > 0) "$openFaults Open" else "${bundle.faults.size} नोंदी"
    else -> mod.badgeText
}

private fun moduleIconAndColor(mod: SubstationModule): Pair<ImageVector, Color> = when (mod) {
    SubstationModule.HOME -> Icons.Default.Bolt to GridBluePrimary
    SubstationModule.SLD -> Icons.Default.AccountTree to GridCyanAccent
    SubstationModule.ANALYTICS -> Icons.Default.Assessment to GridBluePrimary
    SubstationModule.EQUIPMENT -> Icons.Default.Memory to Color(0xFF7C3AED)
    SubstationModule.TRIPPING -> Icons.Default.WarningAmber to HighVoltageRed
    SubstationModule.ENERGY -> Icons.Default.Bolt to EnergizedGreen
    SubstationModule.LOADS -> Icons.Default.Speed to Color(0xFF0284C7)
    SubstationModule.HANDOVER -> Icons.Default.SwapHoriz to Color(0xFF0D9488)
    SubstationModule.BATTERY -> Icons.Default.BatteryChargingFull to CautionAmber
    SubstationModule.PDF_IO -> Icons.Default.Description to Color(0xFF4F46E5)
    SubstationModule.DAILY_LOSSES -> Icons.Default.Calculate to EnergizedGreen
    SubstationModule.MINMAX_REPORT -> Icons.Default.PictureAsPdf to Color(0xFF0891B2)
    SubstationModule.FAULTS -> Icons.Default.Build to HighVoltageRed
}

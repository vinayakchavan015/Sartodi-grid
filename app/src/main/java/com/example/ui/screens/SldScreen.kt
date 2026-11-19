package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.SubstationDoc
import com.example.ui.components.ModuleHeaderBar
import com.example.ui.components.ReadOnlyNoticeBanner
import com.example.ui.theme.CautionAmber
import com.example.ui.theme.EnergizedGreen
import com.example.ui.theme.GridBluePrimary
import com.example.ui.theme.GridCyanAccent
import com.example.ui.theme.HighVoltageRed

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SldScreen(
    substation: SubstationDoc,
    editModeEnabled: Boolean,
    onToggleEditMode: () -> Unit,
    onToggleSwitch: (String) -> Unit,
    onOpenEquipmentId: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val sw = substation.switches
    val goCoupler = sw["goCouplerClosed"] == true
    val pt33God = sw["pt33GodClosed"] != false
    val capLeft = sw["capLeftOn"] != false
    val capRight = sw["capRightOn"] != false
    val byp205206 = sw["byp_205_206"] == true
    val byp204205 = sw["byp_204_205"] == true
    val byp202203 = sw["byp_202_203"] == true
    val byp201202 = sw["byp_201_202"] == true

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ModuleHeaderBar(
                title = "१. सिंगल लाईन डायग्राम (SLD)",
                subtitle = "${substation.name} • स्विच व उपकरणे रिअल-टाइम सिंक",
                onBack = onBack
            )
        }

        if (!editModeEnabled) {
            item { ReadOnlyNoticeBanner(onEnableEdit = onToggleEditMode) }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "33/11 kV स्कीमॅटिक लेआउट (आडवे स्क्रोल करा)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "33kV लाल • 11kV निळा • बायपास केशरी",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        SldDiagramCanvas(
                            goCoupler = goCoupler,
                            pt33God = pt33God,
                            capLeft = capLeft,
                            capRight = capRight,
                            byp205206 = byp205206,
                            byp204205 = byp204205,
                            byp202203 = byp202203,
                            byp201202 = byp201202
                        )
                    }
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "स्विच नियंत्रण",
                            tint = GridBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "रिअल-टाइम G.O.D., कॅपॅसिटर व बायपास नियंत्रण",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SwitchChip("33kV Bus PT G.O.D.", pt33God, "sw_pt33") { onToggleSwitch("pt33GodClosed") }
                        SwitchChip("11kV Bus Coupler G.O.D.", goCoupler, "sw_coupler") { onToggleSwitch("goCouplerClosed") }
                        SwitchChip("Cap Bank-1 (Left)", capLeft, "sw_cap_left") { onToggleSwitch("capLeftOn") }
                        SwitchChip("Cap Bank-2 (Right)", capRight, "sw_cap_right") { onToggleSwitch("capRightOn") }
                        SwitchChip("Bypass 206 ↔ 205", byp205206, "sw_byp_205_206") { onToggleSwitch("byp_205_206") }
                        SwitchChip("Bypass 205 ↔ 204", byp204205, "sw_byp_204_205") { onToggleSwitch("byp_204_205") }
                        SwitchChip("Bypass 203 ↔ 202", byp202203, "sw_byp_202_203") { onToggleSwitch("byp_202_203") }
                        SwitchChip("Bypass 202 ↔ 201", byp201202, "sw_byp_201_202") { onToggleSwitch("byp_201_202") }
                    }
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = "नेमप्लेट पहा",
                            tint = GridCyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "उपकरण नेमप्लेट व मीटर माहिती (टॅप करा)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val quickLinks = listOf(
                        "tf_1" to "⚡ 5 MVA T/F-1",
                        "tf_2" to "⚡ 5 MVA T/F-2",
                        "tf_dtc" to "🔌 63 kVA DTC",
                        "ct_33main" to "33kV Main CT",
                        "pt_33bus" to "33kV Bus PT",
                        "vcb_33_1" to "33kV VCB T/F-1",
                        "vcb_33_2" to "33kV VCB T/F-2",
                        "m_33inc" to "33kV Incomer Meter",
                        "m_205" to "Bay 205 सफाळा",
                        "m_204" to "Bay 204 DFCCIL",
                        "m_203" to "Bay 203 नवघर",
                        "m_201" to "Bay 201 पारगाव",
                        "aux_bat" to "110V Battery Bank",
                        "aux_cap" to "11kV Capacitor Banks"
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickLinks.forEach { (id, label) ->
                            AssistChip(
                                onClick = { onOpenEquipmentId(id) },
                                label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                                modifier = Modifier.testTag("sld_eq_$id")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SwitchChip(
    label: String,
    isClosed: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isClosed,
        onClick = onClick,
        label = {
            Text(
                text = "$label: ${if (isClosed) "ON / CLOSED" else "OFF / OPEN"}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = EnergizedGreen.copy(alpha = 0.18f),
            selectedLabelColor = EnergizedGreen,
            containerColor = HighVoltageRed.copy(alpha = 0.12f),
            labelColor = HighVoltageRed
        ),
        modifier = Modifier.testTag(tag)
    )
}

@Composable
private fun SldDiagramCanvas(
    goCoupler: Boolean,
    pt33God: Boolean,
    capLeft: Boolean,
    capRight: Boolean,
    byp205206: Boolean,
    byp204205: Boolean,
    byp202203: Boolean,
    byp201202: Boolean
) {
    val line33 = Color(0xFFEF4444)
    val line11 = Color(0xFF38BDF8)
    val green = Color(0xFF34D399)
    val amber = Color(0xFFFBBF24)
    val muted = Color(0xFF64748B)
    val dash = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)

    Column(modifier = Modifier.width(680.dp)) {
        // Labels strip for clarity above Canvas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BadgeMini("33kV SPARE BAY", muted)
            BadgeMini("33kV सफाळे मेन इनकमर (MF 600)", line33)
            BadgeMini("33kV BUS PT: ${if (pt33God) "ON" else "OFF"}", if (pt33God) green else line33)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Canvas(
            modifier = Modifier
                .width(680.dp)
                .height(320.dp)
        ) {
            val w = size.width
            val h = size.height

            // 33kV Spare (dashed) and Main Incomer down to 33kV Main Bus
            val spareX = w * 0.16f
            val mainX = w * 0.50f
            val bus33Y = h * 0.22f
            drawLine(muted, Offset(spareX, 10f), Offset(spareX, bus33Y), strokeWidth = 4f, pathEffect = dash)
            drawLine(line33, Offset(mainX, 10f), Offset(mainX, bus33Y), strokeWidth = 6f)

            // 33kV Main Bus
            drawLine(line33, Offset(w * 0.08f, bus33Y), Offset(w * 0.92f, bus33Y), strokeWidth = 8f)

            // 33kV Bus Common PT with G.O.D.
            val ptX = w * 0.86f
            drawLine(if (pt33God) line33 else muted, Offset(ptX, bus33Y), Offset(ptX, bus33Y + 38f), strokeWidth = 4f)
            drawCircle(if (pt33God) green else line33, radius = 9f, center = Offset(ptX, bus33Y + 46f), style = Stroke(3f))

            // T/F-1 (Left) and T/F-2 (Right) drops
            val tf1X = w * 0.28f
            val tf2X = w * 0.72f
            val bus11Y = h * 0.62f

            // HV drop -> VCB box -> Transformer two-circle symbol -> LV drop
            listOf(tf1X, tf2X).forEach { tx ->
                drawLine(line33, Offset(tx, bus33Y), Offset(tx, h * 0.36f), strokeWidth = 5f)
                // 33kV VCB box
                drawRoundRect(
                    color = green,
                    topLeft = Offset(tx - 16f, bus33Y + 14f),
                    size = Size(32f, 20f),
                    cornerRadius = CornerRadius(4f, 4f),
                    style = Stroke(3f)
                )
                // Transformer dual coils
                drawCircle(line33, radius = 16f, center = Offset(tx, h * 0.42f), style = Stroke(4f))
                drawCircle(line11, radius = 16f, center = Offset(tx, h * 0.48f), style = Stroke(4f))
                drawLine(line11, Offset(tx, h * 0.53f), Offset(tx, bus11Y), strokeWidth = 5f)
                // 11kV LV VCB
                drawRoundRect(
                    color = line11,
                    topLeft = Offset(tx - 14f, bus11Y - 26f),
                    size = Size(28f, 18f),
                    cornerRadius = CornerRadius(4f, 4f),
                    style = Stroke(3f)
                )
            }

            // 11kV Bus Section-1 (Left) & Section-2 (Right) + Bus Coupler G.O.D.
            drawLine(line11, Offset(w * 0.06f, bus11Y), Offset(w * 0.46f, bus11Y), strokeWidth = 8f)
            drawLine(line11, Offset(w * 0.54f, bus11Y), Offset(w * 0.94f, bus11Y), strokeWidth = 8f)
            if (goCoupler) {
                drawLine(green, Offset(w * 0.46f, bus11Y), Offset(w * 0.54f, bus11Y), strokeWidth = 7f)
            } else {
                drawLine(line33, Offset(w * 0.46f, bus11Y), Offset(w * 0.53f, bus11Y - 14f), strokeWidth = 5f)
            }
            drawCircle(if (goCoupler) green else line33, radius = 6f, center = Offset(w * 0.46f, bus11Y))
            drawCircle(if (goCoupler) green else line33, radius = 6f, center = Offset(w * 0.54f, bus11Y))

            // 63 kVA DTC off Left 11kV Bus
            drawLine(amber, Offset(w * 0.09f, bus11Y), Offset(w * 0.09f, bus11Y - 40f), strokeWidth = 3f)
            drawCircle(amber, radius = 8f, center = Offset(w * 0.09f, bus11Y - 48f), style = Stroke(3f))

            // Capacitor Banks Left & Right
            drawLine(if (capLeft) green else muted, Offset(w * 0.06f, bus11Y), Offset(w * 0.03f, bus11Y), strokeWidth = 4f)
            drawLine(if (capRight) green else muted, Offset(w * 0.94f, bus11Y), Offset(w * 0.97f, bus11Y), strokeWidth = 4f)

            // 6 Outgoing Feeders: 206, 205, 204 | 203, 202, 201
            val bayXs = listOf(w * 0.13f, w * 0.27f, w * 0.41f, w * 0.59f, w * 0.73f, w * 0.87f)
            val isSpareBay = listOf(true, false, false, false, true, false)
            bayXs.forEachIndexed { idx, bx ->
                val col = if (isSpareBay[idx]) muted else line11
                drawLine(col, Offset(bx, bus11Y), Offset(bx, h - 18f), strokeWidth = 5f)
                drawRoundRect(
                    color = if (isSpareBay[idx]) muted else green,
                    topLeft = Offset(bx - 14f, bus11Y + 22f),
                    size = Size(28f, 20f),
                    cornerRadius = CornerRadius(4f, 4f),
                    style = Stroke(3f)
                )
            }

            // 4 Bypass G.O.D. bridges
            val bypY = h - 38f
            fun drawByp(x1: Float, x2: Float, closed: Boolean) {
                drawLine(
                    color = if (closed) amber else muted.copy(alpha = 0.45f),
                    start = Offset(x1, bypY),
                    end = Offset(x2, bypY),
                    strokeWidth = if (closed) 6f else 3f,
                    pathEffect = if (closed) null else dash
                )
            }
            drawByp(bayXs[0], bayXs[1], byp205206)
            drawByp(bayXs[1], bayXs[2], byp204205)
            drawByp(bayXs[3], bayXs[4], byp202203)
            drawByp(bayXs[4], bayXs[5], byp201202)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BadgeMini("206 SPARE", muted)
            BadgeMini("205 सफाळा", line11)
            BadgeMini("204 DFCCIL", line11)
            BadgeMini("Coupler: ${if (goCoupler) "ON" else "OFF"}", if (goCoupler) green else line33)
            BadgeMini("203 नवघर", line11)
            BadgeMini("202 SPARE", muted)
            BadgeMini("201 पारगाव", line11)
        }
    }
}

@Composable
private fun BadgeMini(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.6f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

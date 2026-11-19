package com.example.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

enum class SubstationModule(
    val numberLabel: String,
    val titleMr: String,
    val badgeText: String,
    val subtitleMr: String
) {
    HOME("0", "मुख्य डॅशबोर्ड", "Home", "सरतोडी उपकेंद्र डॅशबोर्ड"),
    SLD("१", "सिंगल लाईन डायग्राम (SLD)", "Interactive SLD", "33kV मेन व स्पेअर इनकमर, 2×5 MVA T/F, 63 kVA DTC, कॅपॅसिटर बँक व 4 बायपास G.O.D."),
    ANALYTICS("२", "२०२४–२०२६ ॲनालिटिक्स व PDF", "34 महिने (2024–26)", "जानेवारी २०२४ ते ऑक्टोबर २०२६ फिडरनिहाय TP, HT, SD, BD, LS, लोड, लॉस चार्ट व Drive PDF."),
    EQUIPMENT("३", "यंत्रसामग्री व R-Y-B नेमप्लेट मास्टर", "SECURE + R-Y-B", "ट्रान्सफॉर्मर (%Z, Oil), CT व PT (R-Y-B स्वतंत्र सिरीयल), SECURE मीटर्स व रिले सेटिंग्ज."),
    TRIPPING("४", "दैनंदिन ट्रिपिंग / शटडाउन रजिस्टर", "Daily Log", "फॉल्ट ट्रिपिंग (TP), हॅण्ड ट्रिप (HT), शटडाउन (SD/LC), ब्रेकडाउन (BD) व कालावधी गणना."),
    ENERGY("५", "दैनिक / मासिक एनर्जी व लॉसेस", "Auto MF & Loss %", "MF (600, 900, 80, 60, 40) नुसार युनिट्स, 2024–2026 लॉस तक्ता व मीटर रीडिंग नोंद."),
    LOADS("६", "व्होल्टेज व लोड Min/Max रजिस्टर", "Min / Max kV & A", "33kV व 11kV बस व्होल्टेज, T/F-1, T/F-2 व सर्व फिडर्सचा किमान-कमाल करंट (Amps) नोंद."),
    HANDOVER("७", "शिफ्ट चार्ज हँडओव्हर रजिस्टर", "Shift Log", "शिफ्ट बदलताना कार्यभार देणाऱ्या व घेणाऱ्या यंत्रचालकाची नोंद, चालू फिडर्स व प्रलंबित LC."),
    BATTERY("८", "बॅटरी बँक तपासणी रजिस्टर", "110V DC Bank", "110V DC बॅटरी बँक (55 Cells) व्होल्टेज, चार्जर करंट व Specific Gravity तपासणी."),
    PDF_IO("९", "आवक-जावक व मासिक PDF अहवाल", "आवक-जावक + A4 PDF", "कार्यालयीन आवक-जावक रजिस्टर व कोणत्याही महिन्याचा एकत्रित A4 प्रिंट-रेडी PDF अहवाल."),
    DAILY_LOSSES("१०", "दैनिक लॉसेस रजिस्टर (Loss Calc)", "Daily Loss Calc", "रोज Final रीडिंग टाका — Initial आपोआप, M.F. नुसार Consumption व T/F-wise लॉस %."),
    MINMAX_REPORT("११", "Min-Max नोंद व मासिक रिपोर्ट PDF", "Min-Max + PDF", "रोजची Min-Max नोंद (वेळेसह), Min-Max Report, MSEDCL Interruption व T/P-H/T-S/D-B/D-L/S PDF."),
    FAULTS("१२", "यंत्रसामग्री बिघाड / दुरुस्ती रजिस्टर", "बिघाड नोंद", "यंत्रसामग्री बिघाड तारीख, कार्यवाही, जुना/नवीन सिरीयल नं., दुरुस्ती तारीख व स्थिती.")
}

@Keep
@IgnoreExtraProperties
data class SubstationDoc(
    @DocumentId val id: String = "",
    val name: String = "33/11 kV सरतोडी उपकेंद्र (Sartodi Substation)",
    val code: String = "284619",
    val division: String = "सफाळा शाखा, सफाळा उपविभाग, पालघर विभाग (MSEDCL)",
    val switches: Map<String, Boolean> = defaultSwitches(),
    val lossMf: Map<String, Double> = emptyMap(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

fun defaultSwitches(): Map<String, Boolean> = mapOf(
    "goCouplerClosed" to false,
    "pt33GodClosed" to true,
    "capLeftOn" to true,
    "capRightOn" to true,
    "byp_205_206" to false,
    "byp_204_205" to false,
    "byp_202_203" to false,
    "byp_201_202" to false
)

@Keep
@IgnoreExtraProperties
data class TripEntry(
    @DocumentId val id: String = "",
    val date: String = "",
    val type: String = "",
    val feeder: String = "",
    val offTime: String = "",
    val onTime: String = "",
    val duration: String = "",
    val durationMin: Long = 0L,
    val relay: String = "",
    val remark: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class EnergyEntry(
    @DocumentId val id: String = "",
    val date: String = "",
    val meter: String = "",
    val category: String = "33kV_IN",
    val openReading: Double = 0.0,
    val closeReading: Double = 0.0,
    val mf: Double = 600.0,
    val units: Double = 0.0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class LoadEntry(
    @DocumentId val id: String = "",
    val date: String = "",
    val timeLabel: String = "",
    val v33: String = "",
    val v11: String = "",
    val tfLoad: String = "",
    val feederGroup1: String = "",
    val feederGroup2: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class HandoverEntry(
    @DocumentId val id: String = "",
    val date: String = "",
    val shift: String = "",
    val relieved: String = "",
    val relieving: String = "",
    val runningStatus: String = "",
    val permits: String = "",
    val batteryVolt: String = "",
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class BatteryEntry(
    @DocumentId val id: String = "",
    val date: String = "",
    val dcVolt: String = "",
    val charger: String = "",
    val cellVolt: String = "",
    val remark: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class IoEntry(
    @DocumentId val id: String = "",
    val date: String = "",
    val type: String = "",
    val refNo: String = "",
    val party: String = "",
    val subject: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class FaultEntry(
    @DocumentId val id: String = "",
    val date: String = "",
    val equip: String = "",
    val bay: String = "",
    val faultDesc: String = "",
    val actionTaken: String = "",
    val status: String = "बिघाड (Open)",
    val restoredDate: String = "",
    val oldSn: String = "",
    val newSn: String = "",
    val refNo: String = "",
    val remark: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class DailyLossDayDoc(
    @DocumentId val id: String = "",
    val date: String = "",
    val finalReadings: Map<String, Double> = emptyMap(),
    val initialOverrides: Map<String, Double> = emptyMap(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class MinMaxDayDoc(
    @DocumentId val id: String = "",
    val date: String = "",
    val entries: Map<String, Map<String, String>> = emptyMap(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class NameplateDoc(
    @DocumentId val id: String = "",
    val category: String = "transformers",
    val bay: String = "",
    val name: String = "",
    val status: String = "In Service (ON)",
    val make: String = "",
    val serialNo: String = "",
    val year: String = "",
    val specs: Map<String, String> = emptyMap(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class MonthlyRecordDoc(
    @DocumentId val id: String = "",
    val year: Long = 2026L,
    val monthIdx: Long = 0L,
    val monthName: String = "JANUARY",
    val incomerKwh: Double = 0.0,
    val outgoingKwh: Double = 0.0,
    val diffKwh: Double = 0.0,
    val lossPct: Double = 0.0,
    val drivePdfId: String = "",
    val feeders: List<Map<String, Any>> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

// Domain / Calculation models
data class MonthlyFeederStat(
    val year: Int,
    val monthIdx: Int,
    val month: String,
    val feederIdx: Int,
    val feeder: String,
    val tp: Int,
    val ht: Int,
    val sd: Int,
    val bd: Int,
    val ls: Int,
    val totalTripping: Int,
    val durationMin: Int,
    val maxLoad: Double,
    val minLoad: Double,
    val avgLoad: Double,
    val maxVolt: Double,
    val minVolt: Double,
    val drivePdfId: String
)

data class MonthlyLossStat(
    val year: Int,
    val monthIdx: Int,
    val month: String,
    val incomerKwh: Double,
    val outgoingKwh: Double,
    val diffKwh: Double,
    val lossPct: Double
)

data class DailyLossMeterDef(
    val id: String,
    val name: String,
    val shortName: String,
    val defaultMf: Double
)

data class DailyLossRowCalc(
    val meter: DailyLossMeterDef,
    val initialVal: Double?,
    val manualInitial: Boolean,
    val finalVal: Double?,
    val diff: Double?,
    val mf: Double,
    val consumption: Double?
)

data class LossPairRow(
    val label: String,
    val diffKwh: Double?,
    val lossPct: Double?
)

data class DailyLossSummary(
    val rows: List<DailyLossRowCalc>,
    val consumptionMap: Map<String, Double?>,
    val tf1Out: Double?,
    val tf2Out: Double?,
    val totalHv: Double?,
    val totalLv: Double?,
    val totalOg: Double?,
    val lossRows: List<LossPairRow>
)

data class MinMaxItemDef(
    val id: String,
    val name: String,
    val unit: String,
    val sectionHeader: String? = null,
    val cssClass: String = "c-ye"
)

data class MinMaxMonthStat(
    val maxVal: Double? = null,
    val maxDate: String = "",
    val maxTime: String = "",
    val minVal: Double? = null,
    val minDate: String = "",
    val minTime: String = ""
)

data class InterruptionCount(
    var count: Int = 0,
    var minutes: Int = 0
)

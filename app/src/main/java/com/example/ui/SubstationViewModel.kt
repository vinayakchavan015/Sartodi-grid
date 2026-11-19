package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BatteryEntry
import com.example.data.DailyLossDayDoc
import com.example.data.EnergyEntry
import com.example.data.FaultEntry
import com.example.data.HandoverEntry
import com.example.data.IoEntry
import com.example.data.LoadEntry
import com.example.data.MasterSartodiData
import com.example.data.MinMaxDayDoc
import com.example.data.MonthlyFeederStat
import com.example.data.MonthlyLossStat
import com.example.data.MonthlyRecordDoc
import com.example.data.NameplateDoc
import com.example.data.SubstationDoc
import com.example.data.SubstationModule
import com.example.data.SubstationRepository
import com.example.data.TripEntry
import com.example.data.UiState
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SubstationDataBundle(
    val trips: List<TripEntry> = emptyList(),
    val energy: List<EnergyEntry> = emptyList(),
    val loads: List<LoadEntry> = emptyList(),
    val handovers: List<HandoverEntry> = emptyList(),
    val battery: List<BatteryEntry> = emptyList(),
    val io: List<IoEntry> = emptyList(),
    val faults: List<FaultEntry> = emptyList(),
    val dailyLosses: Map<String, DailyLossDayDoc> = emptyMap(),
    val minmax: Map<String, MinMaxDayDoc> = emptyMap(),
    val nameplates: List<NameplateDoc> = MasterSartodiData.DEFAULT_NAMEPLATES,
    val monthlyRecords: List<MonthlyRecordDoc> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class SubstationViewModel(
    private val repository: SubstationRepository,
    private val currentUserId: String
) : ViewModel() {

    private val _currentModule = MutableStateFlow(SubstationModule.HOME)
    val currentModule: StateFlow<SubstationModule> = _currentModule.asStateFlow()

    private val _activeSubstationId = MutableStateFlow("sub_284619")
    val activeSubstationId: StateFlow<String> = _activeSubstationId.asStateFlow()

    private val _editModeEnabled = MutableStateFlow(true)
    val editModeEnabled: StateFlow<Boolean> = _editModeEnabled.asStateFlow()

    private val _snackbar = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val snackbar: SharedFlow<String> = _snackbar.asSharedFlow()

    private val _selectedEquipmentForModal = MutableStateFlow<NameplateDoc?>(null)
    val selectedEquipmentForModal: StateFlow<NameplateDoc?> = _selectedEquipmentForModal.asStateFlow()

    val substationsState: StateFlow<UiState<List<SubstationDoc>>> =
        repository.observeSubstations(currentUserId)
            .map<List<SubstationDoc>, UiState<List<SubstationDoc>>> { list ->
                if (list.isNotEmpty() && list.none { it.id == _activeSubstationId.value }) {
                    _activeSubstationId.value = list.first().id
                }
                UiState.Success(list)
            }
            .catch { error ->
                val fsErr = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (fsErr == null && error is CancellationException) throw error
                val msg = (fsErr ?: error).localizedMessage ?: "Failed to sync substations"
                emit(UiState.Error(msg))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    val activeSubstation: StateFlow<SubstationDoc> = combine(
        substationsState,
        activeSubstationId
    ) { state, activeId ->
        val list = (state as? UiState.Success)?.data.orEmpty()
        list.firstOrNull { it.id == activeId } ?: list.firstOrNull() ?: SubstationDoc(id = activeId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SubstationDoc(id = "sub_284619"))

    private val primaryRegistersFlow = activeSubstationId.flatMapLatest { subId ->
        if (subId.isBlank()) {
            flowOf(SubstationDataBundle())
        } else {
            combine(
                repository.observeTrips(currentUserId, subId).catch { emit(emptyList()) },
                repository.observeEnergy(currentUserId, subId).catch { emit(emptyList()) },
                repository.observeLoads(currentUserId, subId).catch { emit(emptyList()) },
                repository.observeHandovers(currentUserId, subId).catch { emit(emptyList()) },
                repository.observeBattery(currentUserId, subId).catch { emit(emptyList()) }
            ) { trips, energy, loads, handovers, battery ->
                SubstationDataBundle(
                    trips = trips.sortedByDescending { it.date + it.offTime },
                    energy = energy.sortedByDescending { it.date },
                    loads = loads.sortedByDescending { it.date },
                    handovers = handovers.sortedByDescending { it.date },
                    battery = battery.sortedByDescending { it.date }
                )
            }
        }
    }

    private val secondaryRegistersFlow = activeSubstationId.flatMapLatest { subId ->
        if (subId.isBlank()) {
            flowOf(SubstationDataBundle())
        } else {
            val partA = combine(
                repository.observeIo(currentUserId, subId).catch { emit(emptyList()) },
                repository.observeFaults(currentUserId, subId).catch { emit(emptyList()) },
                repository.observeDailyLosses(currentUserId, subId).catch { emit(emptyList()) }
            ) { io, faults, dailyLosses -> Triple(io, faults, dailyLosses) }

            val partB = combine(
                repository.observeMinMax(currentUserId, subId).catch { emit(emptyList()) },
                repository.observeNameplates(currentUserId, subId).catch { emit(emptyList()) },
                repository.observeMonthlyRecords(currentUserId, subId).catch { emit(emptyList()) }
            ) { minmax, nameplates, monthly -> Triple(minmax, nameplates, monthly) }

            combine(partA, partB) { (io, faults, dailyLosses), (minmax, nameplates, monthly) ->
                val mergedNameplates = mergeNameplates(nameplates)
                SubstationDataBundle(
                    io = io.sortedByDescending { it.date },
                    faults = faults.sortedByDescending { it.date },
                    dailyLosses = dailyLosses.associateBy { it.date.ifBlank { it.id } },
                    minmax = minmax.associateBy { it.date.ifBlank { it.id } },
                    nameplates = mergedNameplates,
                    monthlyRecords = monthly
                )
            }
        }
    }

    val dataBundle: StateFlow<SubstationDataBundle> = combine(
        primaryRegistersFlow,
        secondaryRegistersFlow
    ) { p, s ->
        p.copy(
            io = s.io,
            faults = s.faults,
            dailyLosses = s.dailyLosses,
            minmax = s.minmax,
            nameplates = s.nameplates,
            monthlyRecords = s.monthlyRecords
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SubstationDataBundle())

    init {
        viewModelScope.launch {
            repository.ensureDefaultSubstation().onFailure { err ->
                _snackbar.tryEmit("उपकेंद्र डेटा सिंक त्रुटी: ${err.localizedMessage ?: "Permission error"}")
            }
        }
    }

    private fun mergeNameplates(remote: List<NameplateDoc>): List<NameplateDoc> {
        val map = linkedMapOf<String, NameplateDoc>()
        MasterSartodiData.DEFAULT_NAMEPLATES.forEach { map[it.id] = it }
        remote.forEach { doc ->
            val key = doc.id.ifBlank { "${doc.category}_${doc.name}" }
            map[key] = doc
        }
        return map.values.toList()
    }

    fun mergedLossHistory(records: List<MonthlyRecordDoc>): List<MonthlyLossStat> {
        val map = linkedMapOf<String, MonthlyLossStat>()
        MasterSartodiData.LOSS_HISTORY.forEach { l -> map["${l.year}-${l.monthIdx}"] = l }
        records.forEach { rec ->
            if (rec.incomerKwh > 0.0 || rec.outgoingKwh > 0.0) {
                map["${rec.year}-${rec.monthIdx}"] = MonthlyLossStat(
                    year = rec.year.toInt(),
                    monthIdx = rec.monthIdx.toInt(),
                    month = rec.monthName,
                    incomerKwh = rec.incomerKwh,
                    outgoingKwh = rec.outgoingKwh,
                    diffKwh = rec.diffKwh,
                    lossPct = rec.lossPct
                )
            }
        }
        return map.values.sortedWith(compareBy<MonthlyLossStat> { it.year }.thenBy { it.monthIdx })
    }

    fun mergedMonthlyFeeders(
        year: Int,
        monthIdx: Int,
        records: List<MonthlyRecordDoc>
    ): Pair<List<MonthlyFeederStat>, String> {
        val custom = records.firstOrNull { it.year.toInt() == year && it.monthIdx.toInt() == monthIdx }
        if (custom != null && custom.feeders.isNotEmpty()) {
            val list = custom.feeders.mapIndexed { idx, m ->
                val mxL = (m["maxLoad"] as? Number)?.toDouble() ?: 0.0
                val mnL = (m["minLoad"] as? Number)?.toDouble() ?: 0.0
                val tp = (m["tp"] as? Number)?.toInt() ?: 0
                val ht = (m["ht"] as? Number)?.toInt() ?: 0
                val sd = (m["sd"] as? Number)?.toInt() ?: 0
                val bd = (m["bd"] as? Number)?.toInt() ?: 0
                val ls = (m["ls"] as? Number)?.toInt() ?: 0
                MonthlyFeederStat(
                    year = year,
                    monthIdx = monthIdx,
                    month = custom.monthName,
                    feederIdx = idx,
                    feeder = (m["feeder"] as? String) ?: MasterSartodiData.FEEDERS.getOrElse(idx) { "Feeder" },
                    tp = tp, ht = ht, sd = sd, bd = bd, ls = ls,
                    totalTripping = (m["totalTripping"] as? Number)?.toInt() ?: (tp + ht + sd + bd + ls),
                    durationMin = (m["durationMin"] as? Number)?.toInt() ?: 0,
                    maxLoad = mxL, minLoad = mnL,
                    avgLoad = kotlin.math.round(((mxL + mnL) / 2.0) * 10.0) / 10.0,
                    maxVolt = (m["maxVolt"] as? Number)?.toDouble() ?: 0.0,
                    minVolt = (m["minVolt"] as? Number)?.toDouble() ?: 0.0,
                    drivePdfId = custom.drivePdfId
                )
            }
            return list to custom.drivePdfId
        }
        val base = MasterSartodiData.MONTHLY_SUMMARY.filter { it.year == year && it.monthIdx == monthIdx }
        val pdfId = custom?.drivePdfId?.ifBlank { null }
            ?: base.firstOrNull { it.drivePdfId.isNotBlank() }?.drivePdfId.orEmpty()
        return base to pdfId
    }

    fun navigateTo(module: SubstationModule) {
        _currentModule.value = module
    }

    fun navigateHome() {
        _currentModule.value = SubstationModule.HOME
    }

    fun selectSubstation(subId: String) {
        _activeSubstationId.value = subId
    }

    fun toggleEditMode() {
        _editModeEnabled.value = !_editModeEnabled.value
        _snackbar.tryEmit(
            if (_editModeEnabled.value) "ॲडमिन (Edit) मोड सुरू" else "सदस्य (Read-Only) मोड सुरू"
        )
    }

    fun openEquipmentModal(doc: NameplateDoc?) {
        _selectedEquipmentForModal.value = doc
    }

    fun openEquipmentById(id: String) {
        val found = dataBundle.value.nameplates.firstOrNull { it.id == id }
        _selectedEquipmentForModal.value = found
    }

    private fun requireEdit(): Boolean {
        if (!_editModeEnabled.value) {
            _snackbar.tryEmit("सध्या सदस्य (Read-Only) मोड सुरू आहे. बदलासाठी ॲडमिन मोड सुरू करा.")
            return false
        }
        return true
    }

    fun createSubstation(name: String, code: String, division: String) {
        if (!requireEdit()) return
        viewModelScope.launch {
            repository.createSubstation(name, code, division)
                .onSuccess { newId ->
                    _activeSubstationId.value = newId
                    _snackbar.tryEmit("नवीन उपकेंद्र जोडले गेले")
                }
                .onFailure { _snackbar.tryEmit("त्रुटी: ${it.localizedMessage}") }
        }
    }

    fun toggleSwitch(key: String) {
        if (!requireEdit()) return
        val sub = activeSubstation.value
        val current = sub.switches.toMutableMap()
        val oldVal = current[key] ?: false
        current[key] = !oldVal
        viewModelScope.launch {
            repository.updateSwitches(sub.id, current)
                .onSuccess { _snackbar.tryEmit("स्विच स्थिती रिअल-टाइम सेव्ह झाली") }
                .onFailure { _snackbar.tryEmit("स्विच अपडेट त्रुटी: ${it.localizedMessage}") }
        }
    }

    fun saveLossMf(mfMap: Map<String, Double>) {
        if (!requireEdit()) return
        val subId = activeSubstation.value.id
        viewModelScope.launch {
            repository.updateLossMf(subId, mfMap)
                .onSuccess { _snackbar.tryEmit("M.F. सेटिंग्ज सेव्ह झाली") }
                .onFailure { _snackbar.tryEmit("M.F. सेव्ह त्रुटी: ${it.localizedMessage}") }
        }
    }

    fun addTrip(entry: TripEntry) {
        if (!requireEdit()) return
        viewModelScope.launch {
            repository.addTrip(activeSubstation.value.id, entry)
                .onSuccess { _snackbar.tryEmit("ट्रिपिंग नोंद सेव्ह झाली") }
                .onFailure { _snackbar.tryEmit("त्रुटी: ${it.localizedMessage}") }
        }
    }

    fun addEnergy(entry: EnergyEntry) {
        if (!requireEdit()) return
        viewModelScope.launch {
            repository.addEnergy(activeSubstation.value.id, entry)
                .onSuccess { _snackbar.tryEmit("मीटर रीडिंग सेव्ह झाले") }
                .onFailure { _snackbar.tryEmit("त्रुटी: ${it.localizedMessage}") }
        }
    }

    fun addLoad(entry: LoadEntry) {
        if (!requireEdit()) return
        viewModelScope.launch {
            repository.addLoad(activeSubstation.value.id, entry)
                .onSuccess { _snackbar.tryEmit("व्होल्टेज व लोड नोंद सेव्ह झाली") }
                .onFailure { _snackbar.tryEmit("त्रुटी: ${it.localizedMessage}") }
        }
    }

    fun addHandover(entry: HandoverEntry) {
        if (!requireEdit()) return
        viewModelScope.launch {
            repository.addHandover(activeSubstation.value.id, entry)
                .onSuccess { _snackbar.tryEmit("शिफ्ट हँडओव्हर सेव्ह झाले") }
                .onFailure { _snackbar.tryEmit("त्रुटी: ${it.localizedMessage}") }
        }
    }

    fun addBattery(entry: BatteryEntry) {
        if (!requireEdit()) return
        viewModelScope.launch {
            repository.addBattery(activeSubstation.value.id, entry)
                .onSuccess { _snackbar.tryEmit("बॅटरी बँक नोंद सेव्ह झाली") }
                .onFailure { _snackbar.tryEmit("त्रुटी: ${it.localizedMessage}") }
        }
    }

    fun addIo(entry: IoEntry) {
        if (!requireEdit()) return
        viewModelScope.launch {
            repository.addIo(activeSubstation.value.id, entry)
                .onSuccess { _snackbar.tryEmit("आवक-जावक नोंद सेव्ह झाली") }
                .onFailure { _snackbar.tryEmit("त्रुटी: ${it.localizedMessage}") }
        }
    }

    fun addFault(entry: FaultEntry) {
        if (!requireEdit()) return
        viewModelScope.launch {
            repository.addFault(activeSubstation.value.id, entry)
                .onSuccess { _snackbar.tryEmit("बिघाड नोंद सेव्ह झाली") }
                .onFailure { _snackbar.tryEmit("त्रुटी: ${it.localizedMessage}") }
        }
    }

    fun updateFaultStatus(faultId: String, status: String, restoredDate: String) {
        if (!requireEdit()) return
        viewModelScope.launch {
            repository.updateFaultStatus(activeSubstation.value.id, faultId, status, restoredDate)
                .onSuccess { _snackbar.tryEmit("बिघाड स्थिती अपडेट झाली") }
                .onFailure { _snackbar.tryEmit("त्रुटी: ${it.localizedMessage}") }
        }
    }

    fun saveDailyLossDay(
        dateKey: String,
        finalReadings: Map<String, Double>,
        initialOverrides: Map<String, Double>
    ) {
        if (!requireEdit()) return
        viewModelScope.launch {
            repository.saveDailyLossDay(activeSubstation.value.id, dateKey, finalReadings, initialOverrides)
                .onSuccess { _snackbar.tryEmit("$dateKey लॉस रीडिंग सेव्ह झाले") }
                .onFailure { _snackbar.tryEmit("त्रुटी: ${it.localizedMessage}") }
        }
    }

    fun saveMinMaxDay(dateKey: String, entries: Map<String, Map<String, String>>) {
        if (!requireEdit()) return
        viewModelScope.launch {
            repository.saveMinMaxDay(activeSubstation.value.id, dateKey, entries)
                .onSuccess { _snackbar.tryEmit("$dateKey Min-Max नोंद सेव्ह झाली") }
                .onFailure { _snackbar.tryEmit("त्रुटी: ${it.localizedMessage}") }
        }
    }

    fun saveNameplate(doc: NameplateDoc) {
        if (!requireEdit()) return
        viewModelScope.launch {
            repository.saveNameplate(activeSubstation.value.id, doc)
                .onSuccess {
                    _selectedEquipmentForModal.value = null
                    _snackbar.tryEmit("नेमप्लेट माहिती क्लाउडवर सेव्ह झाली")
                }
                .onFailure { _snackbar.tryEmit("त्रुटी: ${it.localizedMessage}") }
        }
    }

    fun saveMonthlyRecord(doc: MonthlyRecordDoc) {
        if (!requireEdit()) return
        viewModelScope.launch {
            repository.saveMonthlyRecord(activeSubstation.value.id, doc)
                .onSuccess { _snackbar.tryEmit("${doc.monthName} ${doc.year} डेटा सेव्ह झाला") }
                .onFailure { _snackbar.tryEmit("त्रुटी: ${it.localizedMessage}") }
        }
    }

    fun deleteItem(subCol: String, entryId: String) {
        if (!requireEdit()) return
        viewModelScope.launch {
            repository.deleteSubItem(activeSubstation.value.id, subCol, entryId)
                .onSuccess { _snackbar.tryEmit("नोंद हटवली गेली") }
                .onFailure { _snackbar.tryEmit("त्रुटी: ${it.localizedMessage}") }
        }
    }

    fun todayIso(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    fun currentYearMonth(): String = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
}

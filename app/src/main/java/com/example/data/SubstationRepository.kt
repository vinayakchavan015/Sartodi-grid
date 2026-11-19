package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class SubstationRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String =
        auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")

    private fun userSubstations(uid: String) =
        db.collection("users").document(uid).collection("substations")

    private fun subDoc(uid: String, subId: String) = userSubstations(uid).document(subId)

    private inline fun <reified T> observeSubCollection(
        uid: String,
        subId: String,
        subCol: String
    ): Flow<List<T>> = flow {
        val path = "users/$uid/substations/$subId/$subCol"
        emitAll(
            subDoc(uid, subId).collection(subCol)
                .snapshots()
                .map { snap ->
                    snap.toObjects(T::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    val toLog = fsErr ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeSubstations(uid: String): Flow<List<SubstationDoc>> = flow {
        val path = "users/$uid/substations"
        emitAll(
            userSubstations(uid)
                .snapshots()
                .map { snap ->
                    snap.toObjects(SubstationDoc::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    val toLog = fsErr ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeTrips(uid: String, subId: String): Flow<List<TripEntry>> =
        observeSubCollection(uid, subId, "trips")

    fun observeEnergy(uid: String, subId: String): Flow<List<EnergyEntry>> =
        observeSubCollection(uid, subId, "energy")

    fun observeLoads(uid: String, subId: String): Flow<List<LoadEntry>> =
        observeSubCollection(uid, subId, "loads")

    fun observeHandovers(uid: String, subId: String): Flow<List<HandoverEntry>> =
        observeSubCollection(uid, subId, "handovers")

    fun observeBattery(uid: String, subId: String): Flow<List<BatteryEntry>> =
        observeSubCollection(uid, subId, "battery")

    fun observeIo(uid: String, subId: String): Flow<List<IoEntry>> =
        observeSubCollection(uid, subId, "io")

    fun observeFaults(uid: String, subId: String): Flow<List<FaultEntry>> =
        observeSubCollection(uid, subId, "faults")

    fun observeDailyLosses(uid: String, subId: String): Flow<List<DailyLossDayDoc>> =
        observeSubCollection(uid, subId, "dailyLosses")

    fun observeMinMax(uid: String, subId: String): Flow<List<MinMaxDayDoc>> =
        observeSubCollection(uid, subId, "minmax")

    fun observeNameplates(uid: String, subId: String): Flow<List<NameplateDoc>> =
        observeSubCollection(uid, subId, "nameplates")

    fun observeMonthlyRecords(uid: String, subId: String): Flow<List<MonthlyRecordDoc>> =
        observeSubCollection(uid, subId, "monthlyRecords")

    // Ensure Sartodi 284619 default substation exists on first sign-in
    suspend fun ensureDefaultSubstation(): Result<String> {
        val uid = try { requireUserId() } catch (e: Exception) { return Result.failure(e) }
        val docId = "sub_284619"
        val path = "users/$uid/substations/$docId"
        return try {
            val existing = userSubstations(uid).get().await()
            if (existing.isEmpty) {
                val payload = mapOf(
                    "name" to "33/11 kV सरतोडी उपकेंद्र (Sartodi Substation)",
                    "code" to "284619",
                    "division" to "सफाळा शाखा, सफाळा उपविभाग, पालघर विभाग (MSEDCL)",
                    "switches" to defaultSwitches(),
                    "lossMf" to emptyMap<String, Double>(),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                subDoc(uid, docId).set(payload).await()
            }
            Result.success(docId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun createSubstation(name: String, code: String, division: String): Result<String> {
        val cleanCode = code.replace(Regex("[^a-zA-Z0-9_-]"), "").ifBlank { "custom" }
        val subId = "sub_${cleanCode}_${System.currentTimeMillis()}"
        var path = "users/unauthenticated/substations/$subId"
        return try {
            val uid = requireUserId()
            path = "users/$uid/substations/$subId"
            require(name.trim().length in 1..140) { "Substation name invalid" }
            require(code.trim().length in 1..40) { "Substation code invalid" }
            require(division.trim().length in 1..200) { "Division invalid" }
            val payload = mapOf(
                "name" to name.trim(),
                "code" to code.trim(),
                "division" to division.trim(),
                "switches" to defaultSwitches(),
                "lossMf" to emptyMap<String, Double>(),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            subDoc(uid, subId).set(payload).await()
            Result.success(subId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateSwitches(subId: String, switches: Map<String, Boolean>): Result<Unit> {
        var path = "users/unauthenticated/substations/$subId"
        return try {
            val uid = requireUserId()
            path = "users/$uid/substations/$subId"
            subDoc(uid, subId).update(
                mapOf("switches" to switches, "updatedAt" to FieldValue.serverTimestamp())
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateLossMf(subId: String, lossMf: Map<String, Double>): Result<Unit> {
        var path = "users/unauthenticated/substations/$subId"
        return try {
            val uid = requireUserId()
            path = "users/$uid/substations/$subId"
            subDoc(uid, subId).update(
                mapOf("lossMf" to lossMf, "updatedAt" to FieldValue.serverTimestamp())
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun addTrip(subId: String, entry: TripEntry): Result<Unit> =
        createSubItem(subId, "trips", "tr_${System.currentTimeMillis()}") {
            mapOf(
                "date" to entry.date.take(20),
                "type" to entry.type.take(80),
                "feeder" to entry.feeder.take(120),
                "offTime" to entry.offTime.take(20),
                "onTime" to entry.onTime.take(20),
                "duration" to entry.duration.take(60),
                "durationMin" to entry.durationMin.coerceIn(0L, 44640L),
                "relay" to entry.relay.take(200),
                "remark" to entry.remark.take(300),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        }

    suspend fun addEnergy(subId: String, entry: EnergyEntry): Result<Unit> =
        createSubItem(subId, "energy", "en_${System.currentTimeMillis()}") {
            mapOf(
                "date" to entry.date.take(20),
                "meter" to entry.meter.take(140),
                "category" to entry.category,
                "openReading" to entry.openReading,
                "closeReading" to entry.closeReading,
                "mf" to entry.mf.coerceAtLeast(0.001),
                "units" to entry.units,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        }

    suspend fun addLoad(subId: String, entry: LoadEntry): Result<Unit> =
        createSubItem(subId, "loads", "ld_${System.currentTimeMillis()}") {
            mapOf(
                "date" to entry.date.take(20),
                "timeLabel" to entry.timeLabel.take(60),
                "v33" to entry.v33.take(60),
                "v11" to entry.v11.take(60),
                "tfLoad" to entry.tfLoad.take(120),
                "feederGroup1" to entry.feederGroup1.take(120),
                "feederGroup2" to entry.feederGroup2.take(120),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        }

    suspend fun addHandover(subId: String, entry: HandoverEntry): Result<Unit> =
        createSubItem(subId, "handovers", "ho_${System.currentTimeMillis()}") {
            mapOf(
                "date" to entry.date.take(20),
                "shift" to entry.shift.take(100),
                "relieved" to entry.relieved.take(120),
                "relieving" to entry.relieving.take(120),
                "runningStatus" to entry.runningStatus.take(350),
                "permits" to entry.permits.take(250),
                "batteryVolt" to entry.batteryVolt.take(50),
                "notes" to entry.notes.take(350),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        }

    suspend fun addBattery(subId: String, entry: BatteryEntry): Result<Unit> =
        createSubItem(subId, "battery", "bt_${System.currentTimeMillis()}") {
            mapOf(
                "date" to entry.date.take(20),
                "dcVolt" to entry.dcVolt.take(50),
                "charger" to entry.charger.take(100),
                "cellVolt" to entry.cellVolt.take(120),
                "remark" to entry.remark.take(300),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        }

    suspend fun addIo(subId: String, entry: IoEntry): Result<Unit> =
        createSubItem(subId, "io", "io_${System.currentTimeMillis()}") {
            mapOf(
                "date" to entry.date.take(20),
                "type" to entry.type.take(50),
                "refNo" to entry.refNo.take(100),
                "party" to entry.party.take(160),
                "subject" to entry.subject.take(300),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        }

    suspend fun addFault(subId: String, entry: FaultEntry): Result<Unit> =
        createSubItem(subId, "faults", "fm_${System.currentTimeMillis()}") {
            mapOf(
                "date" to entry.date.take(20),
                "equip" to entry.equip.take(120),
                "bay" to entry.bay.take(120),
                "faultDesc" to entry.faultDesc.take(350),
                "actionTaken" to entry.actionTaken.take(350),
                "status" to entry.status.take(60),
                "restoredDate" to entry.restoredDate.take(20),
                "oldSn" to entry.oldSn.take(80),
                "newSn" to entry.newSn.take(80),
                "refNo" to entry.refNo.take(100),
                "remark" to entry.remark.take(350),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        }

    suspend fun updateFaultStatus(
        subId: String,
        faultId: String,
        status: String,
        restoredDate: String
    ): Result<Unit> {
        var path = "users/unauthenticated/substations/$subId/faults/$faultId"
        return try {
            val uid = requireUserId()
            path = "users/$uid/substations/$subId/faults/$faultId"
            subDoc(uid, subId).collection("faults").document(faultId).update(
                mapOf(
                    "status" to status.take(60),
                    "restoredDate" to restoredDate.take(20),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun saveDailyLossDay(
        subId: String,
        dateKey: String,
        finalReadings: Map<String, Double>,
        initialOverrides: Map<String, Double>
    ): Result<Unit> {
        var path = "users/unauthenticated/substations/$subId/dailyLosses/$dateKey"
        return try {
            val uid = requireUserId()
            path = "users/$uid/substations/$subId/dailyLosses/$dateKey"
            val docRef = subDoc(uid, subId).collection("dailyLosses").document(dateKey)
            if (finalReadings.isEmpty() && initialOverrides.isEmpty()) {
                docRef.delete().await()
                return Result.success(Unit)
            }
            val snap = docRef.get().await()
            if (snap.exists()) {
                docRef.update(
                    mapOf(
                        "finalReadings" to finalReadings,
                        "initialOverrides" to initialOverrides,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                docRef.set(
                    mapOf(
                        "date" to dateKey,
                        "finalReadings" to finalReadings,
                        "initialOverrides" to initialOverrides,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun saveMinMaxDay(
        subId: String,
        dateKey: String,
        entries: Map<String, Map<String, String>>
    ): Result<Unit> {
        var path = "users/unauthenticated/substations/$subId/minmax/$dateKey"
        return try {
            val uid = requireUserId()
            path = "users/$uid/substations/$subId/minmax/$dateKey"
            val docRef = subDoc(uid, subId).collection("minmax").document(dateKey)
            if (entries.isEmpty()) {
                docRef.delete().await()
                return Result.success(Unit)
            }
            val snap = docRef.get().await()
            if (snap.exists()) {
                docRef.update(
                    mapOf(
                        "entries" to entries,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                docRef.set(
                    mapOf(
                        "date" to dateKey,
                        "entries" to entries,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun saveNameplate(subId: String, doc: NameplateDoc): Result<Unit> {
        val docId = doc.id.ifBlank { "${doc.category}_${System.currentTimeMillis()}" }
            .replace(Regex("[^a-zA-Z0-9_-]"), "_")
        var path = "users/unauthenticated/substations/$subId/nameplates/$docId"
        return try {
            val uid = requireUserId()
            path = "users/$uid/substations/$subId/nameplates/$docId"
            val docRef = subDoc(uid, subId).collection("nameplates").document(docId)
            val snap = docRef.get().await()
            if (snap.exists()) {
                docRef.update(
                    mapOf(
                        "category" to doc.category,
                        "bay" to doc.bay.take(140),
                        "name" to doc.name.take(180),
                        "status" to doc.status.take(60),
                        "make" to doc.make.take(120),
                        "serialNo" to doc.serialNo.take(100),
                        "year" to doc.year.take(20),
                        "specs" to doc.specs,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                docRef.set(
                    mapOf(
                        "category" to doc.category,
                        "bay" to doc.bay.take(140),
                        "name" to doc.name.take(180),
                        "status" to doc.status.take(60),
                        "make" to doc.make.take(120),
                        "serialNo" to doc.serialNo.take(100),
                        "year" to doc.year.take(20),
                        "specs" to doc.specs,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun saveMonthlyRecord(subId: String, doc: MonthlyRecordDoc): Result<Unit> {
        val monthKey = "${doc.year}-${(doc.monthIdx + 1).toString().padStart(2, '0')}"
        var path = "users/unauthenticated/substations/$subId/monthlyRecords/$monthKey"
        return try {
            val uid = requireUserId()
            path = "users/$uid/substations/$subId/monthlyRecords/$monthKey"
            val docRef = subDoc(uid, subId).collection("monthlyRecords").document(monthKey)
            val snap = docRef.get().await()
            if (snap.exists()) {
                docRef.update(
                    mapOf(
                        "year" to doc.year,
                        "monthIdx" to doc.monthIdx,
                        "monthName" to doc.monthName,
                        "incomerKwh" to doc.incomerKwh,
                        "outgoingKwh" to doc.outgoingKwh,
                        "diffKwh" to doc.diffKwh,
                        "lossPct" to doc.lossPct,
                        "drivePdfId" to doc.drivePdfId,
                        "feeders" to doc.feeders,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                docRef.set(
                    mapOf(
                        "year" to doc.year,
                        "monthIdx" to doc.monthIdx,
                        "monthName" to doc.monthName,
                        "incomerKwh" to doc.incomerKwh,
                        "outgoingKwh" to doc.outgoingKwh,
                        "diffKwh" to doc.diffKwh,
                        "lossPct" to doc.lossPct,
                        "drivePdfId" to doc.drivePdfId,
                        "feeders" to doc.feeders,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteSubItem(subId: String, subCol: String, entryId: String): Result<Unit> {
        var path = "users/unauthenticated/substations/$subId/$subCol/$entryId"
        return try {
            val uid = requireUserId()
            path = "users/$uid/substations/$subId/$subCol/$entryId"
            subDoc(uid, subId).collection(subCol).document(entryId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    private suspend fun createSubItem(
        subId: String,
        subCol: String,
        docId: String,
        payloadBuilder: () -> Map<String, Any>
    ): Result<Unit> {
        var path = "users/unauthenticated/substations/$subId/$subCol/$docId"
        return try {
            val uid = requireUserId()
            path = "users/$uid/substations/$subId/$subCol/$docId"
            subDoc(uid, subId).collection(subCol).document(docId).set(payloadBuilder()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }
}

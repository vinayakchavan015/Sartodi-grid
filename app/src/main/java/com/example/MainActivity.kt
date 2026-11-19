package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.auth.authStateFlow
import com.example.auth.signOutUser
import com.example.data.SubstationModule
import com.example.data.SubstationRepository
import com.example.data.UiState
import com.example.ui.SubstationViewModel
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BatteryRegisterScreen
import com.example.ui.screens.DailyLossRegisterScreen
import com.example.ui.screens.EquipmentScreen
import com.example.ui.screens.EnergyRegisterScreen
import com.example.ui.screens.FaultsRegisterScreen
import com.example.ui.screens.HandoverRegisterScreen
import com.example.ui.screens.HomeDashboardScreen
import com.example.ui.screens.LoadsRegisterScreen
import com.example.ui.screens.MinMaxReportScreen
import com.example.ui.screens.NameplateEditDialog
import com.example.ui.screens.PdfAndIoScreen
import com.example.ui.screens.SldScreen
import com.example.ui.screens.TrippingRegisterScreen
import com.example.ui.theme.SartodiSubstationTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkTheme by rememberSaveable { mutableStateOf(false) }
            SartodiSubstationTheme(darkTheme = darkTheme) {
                AppContent(
                    darkTheme = darkTheme,
                    onToggleTheme = { darkTheme = !darkTheme }
                )
            }
        }
    }
}

@Composable
private fun AppContent(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val authFlow = remember { Firebase.auth.authStateFlow() }
    val user by authFlow.collectAsStateWithLifecycle(initialValue = Firebase.auth.currentUser)

    val currentUser = user
    if (currentUser == null) {
        AuthScreen()
    } else {
        key(currentUser.uid) {
            AuthenticatedSubstationApp(
                user = currentUser,
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthenticatedSubstationApp(
    user: FirebaseUser,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val vm: SubstationViewModel = viewModel(
        key = user.uid,
        factory = viewModelFactory {
            initializer {
                SubstationViewModel(
                    repository = SubstationRepository(appContext),
                    currentUserId = user.uid
                )
            }
        }
    )

    val substationsState by vm.substationsState.collectAsStateWithLifecycle()
    val activeSubstation by vm.activeSubstation.collectAsStateWithLifecycle()
    val currentModule by vm.currentModule.collectAsStateWithLifecycle()
    val editModeEnabled by vm.editModeEnabled.collectAsStateWithLifecycle()
    val bundle by vm.dataBundle.collectAsStateWithLifecycle()
    val selectedEquipment by vm.selectedEquipmentForModal.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(vm) {
        vm.snackbar.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    var showSubMenu by remember { mutableStateOf(false) }
    var showAddSubDialog by remember { mutableStateOf(false) }
    val allSubstations = (substationsState as? UiState.Success)?.data.orEmpty()
    val lossHistory = remember(bundle.monthlyRecords) { vm.mergedLossHistory(bundle.monthlyRecords) }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {
                    Box {
                        TextButton(
                            onClick = { showSubMenu = true },
                            modifier = Modifier.testTag("substation_switcher_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                Text(
                                    text = activeSubstation.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "कोड: ${activeSubstation.code} • ${user.email ?: "Signed In"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "उपकेंद्र निवडा"
                            )
                        }

                        DropdownMenu(
                            expanded = showSubMenu,
                            onDismissRequest = { showSubMenu = false }
                        ) {
                            allSubstations.forEach { sub ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${sub.name} (${sub.code})",
                                            fontWeight = if (sub.id == activeSubstation.id) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        vm.selectSubstation(sub.id)
                                        showSubMenu = false
                                    }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("नवीन उपकेंद्र जोडा", fontWeight = FontWeight.Bold)
                                    }
                                },
                                onClick = {
                                    showSubMenu = false
                                    showAddSubDialog = true
                                }
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier.size(48.dp).testTag("theme_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "थीम बदला"
                        )
                    }
                    IconButton(
                        onClick = { signOutUser(credentialManager, scope) },
                        modifier = Modifier.size(48.dp).testTag("sign_out_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "साइन आउट"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentModule) {
                SubstationModule.HOME -> HomeDashboardScreen(
                    substation = activeSubstation,
                    bundle = bundle,
                    lossHistory = lossHistory,
                    editModeEnabled = editModeEnabled,
                    onToggleEditMode = vm::toggleEditMode,
                    onOpenModule = vm::navigateTo
                )

                SubstationModule.SLD -> SldScreen(
                    substation = activeSubstation,
                    editModeEnabled = editModeEnabled,
                    onToggleEditMode = vm::toggleEditMode,
                    onToggleSwitch = vm::toggleSwitch,
                    onOpenEquipmentId = vm::openEquipmentById,
                    onBack = vm::navigateHome
                )

                SubstationModule.ANALYTICS -> AnalyticsScreen(
                    lossHistory = lossHistory,
                    getMonthlyFeeders = { yr, mIdx -> vm.mergedMonthlyFeeders(yr, mIdx, bundle.monthlyRecords) },
                    editModeEnabled = editModeEnabled,
                    onSaveMonthlyRecord = vm::saveMonthlyRecord,
                    onBack = vm::navigateHome
                )

                SubstationModule.EQUIPMENT -> EquipmentScreen(
                    nameplates = bundle.nameplates,
                    editModeEnabled = editModeEnabled,
                    onOpenDetail = vm::openEquipmentModal,
                    onAddNew = vm::saveNameplate,
                    onBack = vm::navigateHome
                )

                SubstationModule.TRIPPING -> TrippingRegisterScreen(
                    trips = bundle.trips,
                    todayIso = vm.todayIso(),
                    editModeEnabled = editModeEnabled,
                    onToggleEditMode = vm::toggleEditMode,
                    onAddTrip = vm::addTrip,
                    onDeleteTrip = { vm.deleteItem("trips", it) },
                    onBack = vm::navigateHome
                )

                SubstationModule.ENERGY -> EnergyRegisterScreen(
                    entries = bundle.energy,
                    todayIso = vm.todayIso(),
                    editModeEnabled = editModeEnabled,
                    onToggleEditMode = vm::toggleEditMode,
                    onAddEnergy = vm::addEnergy,
                    onDeleteEnergy = { vm.deleteItem("energy", it) },
                    onBack = vm::navigateHome
                )

                SubstationModule.LOADS -> LoadsRegisterScreen(
                    loads = bundle.loads,
                    todayIso = vm.todayIso(),
                    editModeEnabled = editModeEnabled,
                    onToggleEditMode = vm::toggleEditMode,
                    onAddLoad = vm::addLoad,
                    onDeleteLoad = { vm.deleteItem("loads", it) },
                    onBack = vm::navigateHome
                )

                SubstationModule.HANDOVER -> HandoverRegisterScreen(
                    handovers = bundle.handovers,
                    todayIso = vm.todayIso(),
                    editModeEnabled = editModeEnabled,
                    onToggleEditMode = vm::toggleEditMode,
                    onAddHandover = vm::addHandover,
                    onDeleteHandover = { vm.deleteItem("handovers", it) },
                    onBack = vm::navigateHome
                )

                SubstationModule.BATTERY -> BatteryRegisterScreen(
                    entries = bundle.battery,
                    todayIso = vm.todayIso(),
                    editModeEnabled = editModeEnabled,
                    onToggleEditMode = vm::toggleEditMode,
                    onAddBattery = vm::addBattery,
                    onDeleteBattery = { vm.deleteItem("battery", it) },
                    onBack = vm::navigateHome
                )

                SubstationModule.PDF_IO -> PdfAndIoScreen(
                    substation = activeSubstation,
                    ioList = bundle.io,
                    auxMeters = bundle.nameplates.filter { it.category == "aux" },
                    lossHistory = lossHistory,
                    getMonthlyFeeders = { yr, mIdx -> vm.mergedMonthlyFeeders(yr, mIdx, bundle.monthlyRecords) },
                    todayIso = vm.todayIso(),
                    editModeEnabled = editModeEnabled,
                    onToggleEditMode = vm::toggleEditMode,
                    onAddIo = vm::addIo,
                    onDeleteIo = { vm.deleteItem("io", it) },
                    onBack = vm::navigateHome
                )

                SubstationModule.DAILY_LOSSES -> DailyLossRegisterScreen(
                    substation = activeSubstation,
                    allDailyLosses = bundle.dailyLosses,
                    todayIso = vm.todayIso(),
                    currentYearMonth = vm.currentYearMonth(),
                    editModeEnabled = editModeEnabled,
                    onToggleEditMode = vm::toggleEditMode,
                    onSaveDay = vm::saveDailyLossDay,
                    onSaveMf = vm::saveLossMf,
                    onBack = vm::navigateHome
                )

                SubstationModule.MINMAX_REPORT -> MinMaxReportScreen(
                    substation = activeSubstation,
                    allMinMax = bundle.minmax,
                    allDailyLosses = bundle.dailyLosses,
                    trips = bundle.trips,
                    faults = bundle.faults,
                    todayIso = vm.todayIso(),
                    currentYearMonth = vm.currentYearMonth(),
                    editModeEnabled = editModeEnabled,
                    onToggleEditMode = vm::toggleEditMode,
                    onSaveMinMaxDay = vm::saveMinMaxDay,
                    onBack = vm::navigateHome
                )

                SubstationModule.FAULTS -> FaultsRegisterScreen(
                    substation = activeSubstation,
                    faults = bundle.faults,
                    todayIso = vm.todayIso(),
                    currentYearMonth = vm.currentYearMonth(),
                    editModeEnabled = editModeEnabled,
                    onToggleEditMode = vm::toggleEditMode,
                    onAddFault = vm::addFault,
                    onUpdateFaultStatus = vm::updateFaultStatus,
                    onDeleteFault = { vm.deleteItem("faults", it) },
                    onBack = vm::navigateHome
                )
            }
        }
    }

    selectedEquipment?.let { doc ->
        NameplateEditDialog(
            doc = doc,
            editModeEnabled = editModeEnabled,
            isNew = false,
            onDismiss = { vm.openEquipmentModal(null) },
            onSave = vm::saveNameplate
        )
    }

    if (showAddSubDialog) {
        AddSubstationDialog(
            onDismiss = { showAddSubDialog = false },
            onCreate = { name, code, division ->
                vm.createSubstation(name, code, division)
                showAddSubDialog = false
            }
        )
    }
}

@Composable
private fun AddSubstationDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var division by remember { mutableStateOf("पालघर विभाग (MSEDCL)") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("नवीन उपकेंद्र जोडा") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("उपकेंद्राचे नाव *") }, singleLine = true)
                OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("उपकेंद्र कोड *") }, singleLine = true)
                OutlinedTextField(value = division, onValueChange = { division = it }, label = { Text("शाखा / उपविभाग / विभाग") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && code.isNotBlank()) {
                        onCreate(name, code, division)
                    }
                },
                modifier = Modifier.testTag("create_substation_btn")
            ) {
                Text("तयार करा")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("रद्द करा") }
        }
    )
}

package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.example.ui.components.TrackLazyListScroll
import com.example.ui.components.TrackScrollState
import com.example.ui.components.fullScreenToggleOnTap
import com.example.ui.components.rememberDirectionalScrollConnection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.data.DailyExamination
import com.example.ui.MainViewModel
import com.example.ui.components.GothicHeader
import com.example.ui.components.IlluminatedDivider
import com.example.ui.theme.*
import com.example.util.AppLanguage
import com.example.util.AppTranslationHelper

data class TraditionalFault(
    val id: String,
    val description: String,
    val category: String, // "COMMANDMENT" or "CAPITAL_SIN"
    val groupName: String // e.g. "1st Commandment", "Pride"
)

val TRADITIONAL_FAULTS = listOf(
    // Commandments
    TraditionalFault("C1_NEGLECT_PRAYER", "Neglected daily prayers or spiritual duties", "COMMANDMENT", "1st Commandment"),
    TraditionalFault("C1_DOUBT_FAITH", "Deliberately doubted or endangered my faith", "COMMANDMENT", "1st Commandment"),
    TraditionalFault("C2_VAIN_NAME", "Used God's holy name in vain or swore rashly", "COMMANDMENT", "2nd Commandment"),
    TraditionalFault("C3_MISS_MASS", "Missed Mass on Sunday/Holy Day through own fault", "COMMANDMENT", "3rd Commandment"),
    TraditionalFault("C3_SERVILE_WORK", "Did unnecessary servile work on Sunday", "COMMANDMENT", "3rd Commandment"),
    TraditionalFault("C4_DISOBEY_AUTH", "Disobeyed, disrespected, or neglected parents/superiors", "COMMANDMENT", "4th Commandment"),
    TraditionalFault("C5_ANGER_REACTION", "Yielded to anger, impatience, or resentment", "COMMANDMENT", "5th Commandment"),
    TraditionalFault("C5_HARSH_WORDS", "Spoke unkind, harsh, or judging words", "COMMANDMENT", "5th Commandment"),
    TraditionalFault("C6_IMPURE_THOUGHTS", "Entertained impure thoughts or desires", "COMMANDMENT", "6th & 9th Commandments"),
    TraditionalFault("C6_GUARD_EYES", "Neglected to guard my eyes or senses (impurity)", "COMMANDMENT", "6th & 9th Commandments"),
    TraditionalFault("C7_THEFT_WASTE", "Took other's goods or wasted time/resources", "COMMANDMENT", "7th & 10th Commandments"),
    TraditionalFault("C7_ENVY_GOODS", "Coveted or envied other's material success", "COMMANDMENT", "7th & 10th Commandments"),
    TraditionalFault("C8_LYING", "Told lies, gossiped, or damaged reputations", "COMMANDMENT", "8th Commandment"),
    
    // Capital Sins
    TraditionalFault("S1_PRIDE", "Pride: Self-complacency, vanity, or seeking praise", "CAPITAL_SIN", "Pride"),
    TraditionalFault("S2_COVET", "Greed: Excessive attachment to things, selfishness", "CAPITAL_SIN", "Greed"),
    TraditionalFault("S3_LUST", "Lust: Impurity of mind or actions", "CAPITAL_SIN", "Lust"),
    TraditionalFault("S4_ANGER", "Anger: Bitterness, irritation, or desire for revenge", "CAPITAL_SIN", "Anger"),
    TraditionalFault("S5_GLUTTONY", "Gluttony: Excess in eating, drinking, or media", "CAPITAL_SIN", "Gluttony"),
    TraditionalFault("S6_ENVY", "Envy: Discontent/jealousy at another's blessings", "CAPITAL_SIN", "Envy"),
    TraditionalFault("S7_SLOTH", "Sloth: Laziness in daily labor or spiritual routine", "CAPITAL_SIN", "Sloth")
)

@Composable
fun ExaminationScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val today = remember { viewModel.getTodayLiturgicalInfo().dateString }
    val recentExams by viewModel.recentExaminations.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    var dutiesFulfilled by remember { mutableStateOf(false) }
    var prayerText by remember { mutableStateOf("") }
    var charityText by remember { mutableStateOf("") }
    var temptationText by remember { mutableStateOf("") }
    var resolutionText by remember { mutableStateOf("") }
    var gratitudeText by remember { mutableStateOf("") }

    var isSavedToday by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }

    val selectedFaultIds = remember { mutableStateListOf<String>() }

    val scrollState = rememberScrollState()
    val historyListState = remember { androidx.compose.foundation.lazy.LazyListState() }

    val coroutineScope = rememberCoroutineScope()
    var showExportPdfDialog by remember { mutableStateOf(false) }
    var pendingPdfPassword by remember { mutableStateOf<String?>(null) }

    val exportPdfLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val outputStream = context.contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        val resultMsg = viewModel.exportExaminationsToPdf(pendingPdfPassword, outputStream)
                        Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Could not open storage location.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "PDF Export Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                } finally {
                    pendingPdfPassword = null
                }
            }
        }
    }

    LaunchedEffect(showHistory) {
        viewModel.setReadingProgress(0f)
        viewModel.setBottomNavVisible(true)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setReadingProgress(0f)
            viewModel.setBottomNavVisible(true)
            viewModel.setTopHeaderVisible(true)
        }
    }

    if (showHistory) {
        TrackLazyListScroll(listState = historyListState, viewModel = viewModel)
    } else {
        TrackScrollState(scrollState = scrollState, viewModel = viewModel)
    }

    val isTopHeaderVisible by viewModel.isTopHeaderVisible.collectAsStateWithLifecycle()
    val directionalScrollConnection = rememberDirectionalScrollConnection(viewModel = viewModel)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCathedralBackground)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .widthIn(max = 600.dp)
            .padding(horizontal = 16.dp)
            .fullScreenToggleOnTap(viewModel)
            .nestedScroll(directionalScrollConnection)
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = isTopHeaderVisible,
            enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically(),
            exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkVertically()
        ) {
            Column {
                GothicHeader(
                    title = AppTranslationHelper.translate("EXAMINATION OF CONSCIENCE", appLanguage),
                    subtitle = AppTranslationHelper.translate("Nightly Self-Examination & Concrete Resolution", appLanguage),
                    badgeText = AppTranslationHelper.translate("Daily Examination", appLanguage)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { showHistory = !showHistory },
                        modifier = Modifier.testTag("toggle_history_btn").weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.History, contentDescription = null, tint = GoldBright)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = AppTranslationHelper.translate(if (showHistory) "Conduct" else "History Logs", appLanguage), color = GoldBright)
                    }

                    var showLedgerAuthDialog by remember { mutableStateOf(false) }
                    var isLedgerUnlocked by remember { mutableStateOf(false) }

                    Button(
                        onClick = { showLedgerAuthDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonBurgundy, contentColor = StaticGoldBright),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, GoldSacred),
                        modifier = Modifier.testTag("confession_prep_btn").weight(1.5f)
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = StaticGoldBright, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = AppTranslationHelper.translate("Confession Prep 🔒", appLanguage),
                            color = StaticGoldBright,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    if (showLedgerAuthDialog) {
                        ConfessionLedgerAuthDialog(
                            onDismiss = { showLedgerAuthDialog = false },
                            onUnlocked = {
                                showLedgerAuthDialog = false
                                isLedgerUnlocked = true
                            },
                            context = context
                        )
                    }

                    if (isLedgerUnlocked) {
                        ConfessionPrepLedgerDialog(
                            onDismiss = { isLedgerUnlocked = false },
                            recentExams = recentExams,
                            context = context,
                            onExportPdfRequested = {
                                showExportPdfDialog = true
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        if (showHistory) {
            if (recentExams.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recorded examinations yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMutedParchment
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Past Examination Logs (${recentExams.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                            color = GoldBright
                        )
                        OutlinedButton(
                            onClick = { showExportPdfDialog = true },
                            modifier = Modifier.testTag("export_pdf_history_btn"),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, GoldSacred),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = GoldBright, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export PDF 🔒", color = GoldBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    LazyColumn(
                        state = historyListState,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 120.dp)
                    ) {
                    items(recentExams, key = { it.date }) { exam ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
                            border = BorderStroke(1.dp, DarkCathedralCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Date: ${exam.date}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = GoldBright
                                    )
                                    Text(
                                        text = if (exam.dutiesFulfilled) "Duties Fulfilled" else "Partial",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (exam.dutiesFulfilled) GoldBright else CrimsonLight
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                if (exam.concreteResolution.isNotBlank()) {
                                    Text(
                                        text = "Resolution: ${exam.concreteResolution}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = ParchmentLight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Checkbox 1: Fulfilled Duties
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
                    border = BorderStroke(1.dp, GoldSacred)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = dutiesFulfilled,
                            onCheckedChange = { dutiesFulfilled = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = GoldBright,
                                checkmarkColor = Color.Black
                            ),
                            modifier = Modifier.testTag("duties_fulfilled_checkbox")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Did I faithfully fulfill my spiritual duties & state of life today?",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ParchmentLight
                            )
                            Text(
                                text = "Prayer, family duties, labor, and mortification.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedParchment
                            )
                        }
                    }
                }

                // Field 2: Faithfulness in Prayer
                OutlinedTextField(
                    value = prayerText,
                    onValueChange = { prayerText = it },
                    label = { Text("1. Faithfulness in Prayer & Recollection") },
                    placeholder = { Text("Was my prayer attentive or distracted? Did I say the Rosary?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldBright,
                        unfocusedBorderColor = DarkCathedralCardBorder
                    )
                )

                // Field 3: Practice of Charity & Patience
                OutlinedTextField(
                    value = charityText,
                    onValueChange = { charityText = it },
                    label = { Text("2. Charity, Patience, Humility & Speech") },
                    placeholder = { Text("Did I speak unkindly, judge others, or react in anger?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldBright,
                        unfocusedBorderColor = DarkCathedralCardBorder
                    )
                )

                // Field 4: Resistance to Temptation
                OutlinedTextField(
                    value = temptationText,
                    onValueChange = { temptationText = it },
                    label = { Text("3. Resistance to Temptation & Senses") },
                    placeholder = { Text("Did I guard my eyes, thoughts, and desires against sin?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldBright,
                        unfocusedBorderColor = DarkCathedralCardBorder
                    )
                )

                // Field 5: Concrete Resolution for Tomorrow (MANDATORY FOCUS)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CrimsonBurgundy.copy(alpha = 0.25f)),
                    border = BorderStroke(1.5.dp, GoldBright)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "☩ Concrete Resolution for Tomorrow",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = GoldBright
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Identify one practical virtue or weakness to tackle tomorrow:",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedParchment
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = resolutionText,
                            onValueChange = { resolutionText = it },
                            placeholder = { Text("e.g. 'I will avoid harsh words when tired and pray 1 decade for patience.'") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(95.dp)
                                .testTag("resolution_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldBright,
                                unfocusedBorderColor = DarkCathedralCardBorder
                            )
                        )
                    }
                }

                // Field 6: Gratitude
                OutlinedTextField(
                    value = gratitudeText,
                    onValueChange = { gratitudeText = it },
                    label = { Text("5. Gratitude & Offering to God") },
                    placeholder = { Text("Thank God for today's graces and protection.") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(85.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldBright,
                        unfocusedBorderColor = DarkCathedralCardBorder
                    )
                )

                // Checklist Card for Daily Faults
                var expandedFaults by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
                    border = BorderStroke(1.dp, GoldSacred)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedFaults = !expandedFaults },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Checklist,
                                    contentDescription = "Faults Checklist Icon",
                                    tint = GoldBright,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "☩ DAILY FAULTS CHECKLIST (${selectedFaultIds.size} Selected)",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = GoldBright
                                )
                            }
                            Text(
                                text = if (expandedFaults) "Hide ▲" else "Show ▼",
                                style = MaterialTheme.typography.labelMedium,
                                color = GoldLight
                            )
                        }

                        if (expandedFaults) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Check any shortcomings from today. These are compiled privately to build your confidential offline Confession Preparation Ledger.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedParchment
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            val groupedFaults = TRADITIONAL_FAULTS.groupBy { it.groupName }
                            groupedFaults.forEach { (group, faultsInGroup) ->
                                Text(
                                    text = AppTranslationHelper.translate(group, appLanguage).uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                                    color = GoldDark,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                faultsInGroup.forEach { fault ->
                                    val isChecked = selectedFaultIds.contains(fault.id)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                if (isChecked) selectedFaultIds.remove(fault.id) else selectedFaultIds.add(fault.id)
                                            }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { checked ->
                                                if (checked == true) {
                                                    if (!selectedFaultIds.contains(fault.id)) selectedFaultIds.add(fault.id)
                                                } else {
                                                    selectedFaultIds.remove(fault.id)
                                                }
                                            },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = GoldBright,
                                                checkmarkColor = Color.Black
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = AppTranslationHelper.translate(fault.description, appLanguage),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ParchmentLight
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        val faultsString = selectedFaultIds.joinToString(",")
                        viewModel.saveExamination(
                            dutiesFulfilled = dutiesFulfilled,
                            prayer = prayerText,
                            charity = charityText,
                            temptation = temptationText,
                            resolution = resolutionText,
                            gratitude = gratitudeText,
                            selectedFaults = faultsString
                        )
                        
                        val examContent = mapOf(
                            "Duties Fulfilled" to dutiesFulfilled.toString(),
                            "Faithfulness in Prayer" to prayerText,
                            "Charity, Patience, Humility & Speech" to charityText,
                            "Resistance to Temptation & Senses" to temptationText,
                            "Concrete Resolution" to resolutionText,
                            "Gratitude & Offering to God" to gratitudeText,
                            "Logged Shortcomings Count" to selectedFaultIds.size.toString()
                        )
                        com.example.util.PdfGenerator.generatePdf(
                            context, 
                            "Daily Examination", 
                            "Examination_${today.replace("/", "_")}", 
                            examContent
                        )
                        
                        // Empty the boxes and reset state for more entries
                        dutiesFulfilled = false
                        prayerText = ""
                        charityText = ""
                        temptationText = ""
                        resolutionText = ""
                        gratitudeText = ""
                        selectedFaultIds.clear()
                        isSavedToday = false
                        
                        Toast.makeText(context, "Daily Examination of Conscience saved successfully.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_examen_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSavedToday) "Examination Saved Today ☩" else "Save Daily Examination",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }

    if (showExportPdfDialog) {
        ExportExaminationPdfDialog(
            onDismiss = { showExportPdfDialog = false },
            onConfirmExport = { pwd ->
                pendingPdfPassword = pwd
                showExportPdfDialog = false
                val dateStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                exportPdfLauncher.launch("examination_record_$dateStr.pdf")
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfessionPrepLedgerDialog(
    onDismiss: () -> Unit,
    recentExams: List<DailyExamination>,
    context: android.content.Context,
    onExportPdfRequested: () -> Unit = {}
) {
    var selectedDaysRange by remember { mutableStateOf(14) }
    var showChangePwdDialog by remember { mutableStateOf(false) }

    val sdf = remember { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()) }
    
    val filteredExams = remember(recentExams, selectedDaysRange) {
        val cutoffTime = System.currentTimeMillis() - (selectedDaysRange * 24L * 60L * 60L * 1000L)
        recentExams.filter { exam ->
            try {
                val dateObj = sdf.parse(exam.date)
                dateObj != null && dateObj.time >= cutoffTime
            } catch (e: Exception) {
                exam.timestamp >= cutoffTime
            }
        }
    }

    val aggregatedFaults = remember(filteredExams) {
        val counts = mutableMapOf<String, Int>()
        filteredExams.forEach { exam ->
            if (exam.selectedFaults.isNotBlank()) {
                val ids = exam.selectedFaults.split(",")
                ids.forEach { id ->
                    if (id.isNotBlank()) {
                        counts[id] = (counts[id] ?: 0) + 1
                    }
                }
            }
        }
        counts
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DarkCathedralBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close Ledger", tint = GoldBright)
                    }
                    Text(
                        text = "† CONFESSION PREPARATION †",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = GoldBright
                    )
                    IconButton(
                        onClick = {
                            showChangePwdDialog = true
                        }
                    ) {
                        Icon(imageVector = Icons.Default.VpnKey, contentDescription = "Change Password", tint = GoldDark)
                    }
                }

                if (showChangePwdDialog) {
                    ConfessionLedgerAuthDialog(
                        initialMode = "CHANGE_PWD",
                        onDismiss = { showChangePwdDialog = false },
                        onUnlocked = {
                            showChangePwdDialog = false
                        },
                        context = context
                    )
                }

                IlluminatedDivider()
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CrimsonBurgundy.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Confession Prep Ledger 🔒",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldBright
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "This ledger aggregates your daily checked faults and resolutions over the selected period, categorized by the Ten Commandments and Seven Capital Sins. Use this list to prepare a sincere, humble, and thorough confession.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedParchment
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Time Period:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = ParchmentLight
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    
                    listOf(14, 30).forEach { days ->
                        val isSelected = selectedDaysRange == days
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) GoldSacred else DarkCathedralSurface,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedDaysRange = days }
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) GoldBright else DarkCathedralCardBorder,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Last $days Days",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Color.Black else GoldBright
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "AGGREGATED SHORTCOMINGS & FAULTS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                    color = GoldDark
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (aggregatedFaults.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No faults logged during the last $selectedDaysRange days.\nKeep conducting your evening examination faithfully!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMutedParchment,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    val commandmentsFaults = TRADITIONAL_FAULTS.filter { it.category == "COMMANDMENT" }
                    val capitalSinsFaults = TRADITIONAL_FAULTS.filter { it.category == "CAPITAL_SIN" }

                    Text(
                        text = "I. BY THE TEN COMMANDMENTS",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        color = GoldBright,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    val hasCommandmentFaults = commandmentsFaults.any { aggregatedFaults.containsKey(it.id) }
                    if (!hasCommandmentFaults) {
                        Text(
                            text = "• No commandment faults logged.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedParchment,
                            modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)
                        )
                    } else {
                        commandmentsFaults.forEach { fault ->
                            val count = aggregatedFaults[fault.id] ?: 0
                            if (count > 0) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp, top = 4.dp, bottom = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "• ${fault.description} (${fault.groupName})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ParchmentLight,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "Logged $count times",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldBright
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "II. BY THE SEVEN CAPITAL SINS",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        color = GoldBright,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    val hasCapitalSinFaults = capitalSinsFaults.any { aggregatedFaults.containsKey(it.id) }
                    if (!hasCapitalSinFaults) {
                        Text(
                            text = "• No capital sin faults logged.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedParchment,
                            modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)
                        )
                    } else {
                        capitalSinsFaults.forEach { fault ->
                            val count = aggregatedFaults[fault.id] ?: 0
                            if (count > 0) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp, top = 4.dp, bottom = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "• ${fault.description}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ParchmentLight,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "Logged $count times",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldBright
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = DarkCathedralCardBorder)
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "MY RECENT CONCRETE RESOLUTIONS:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                    color = GoldDark
                )
                Spacer(modifier = Modifier.height(8.dp))

                val resolutionsList = filteredExams.filter { it.concreteResolution.isNotBlank() }
                if (resolutionsList.isEmpty()) {
                    Text(
                        text = "No recorded resolutions in this period.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedParchment,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    resolutionsList.forEach { exam ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
                            border = BorderStroke(0.5.dp, DarkCathedralCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = exam.date,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldBright
                                    )
                                    if (exam.dutiesFulfilled) {
                                        Text(
                                            text = "Duties Fulfilled",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = GoldLight
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = exam.concreteResolution,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ParchmentLight
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = onExportPdfRequested,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("export_pdf_ledger_btn"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GoldSacred)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = GoldBright, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Export Encrypted PDF Record 📄🔒",
                        color = GoldBright,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("close_ledger_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Lock & Return to Conscience Screen",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}

@Composable
fun ConfessionLedgerAuthDialog(
    initialMode: String? = null,
    onDismiss: () -> Unit,
    onUnlocked: () -> Unit,
    context: Context
) {
    val sharedPref = remember { context.getSharedPreferences("confession_ledger_pref", Context.MODE_PRIVATE) }
    val savedPwd = remember { sharedPref.getString("ledger_pwd", "") ?: "" }
    val savedQuestion = remember { sharedPref.getString("ledger_security_q", "What is your Confirmation Name?") ?: "What is your Confirmation Name?" }
    val savedAnswer = remember { sharedPref.getString("ledger_security_a", "") ?: "" }

    val isFirstTime = savedPwd.isEmpty()

    // Modes: "INITIAL_SETUP", "UNLOCK_PWD", "UNLOCK_SECURITY", "CHANGE_PWD"
    var currentMode by remember { 
        mutableStateOf(
            initialMode ?: if (isFirstTime) "INITIAL_SETUP" else "UNLOCK_PWD"
        ) 
    }

    // Input States
    var passwordInput by remember { mutableStateOf("") }
    var passwordConfirmInput by remember { mutableStateOf("") }
    var oldPasswordInput by remember { mutableStateOf("") }
    
    // Security Question States
    var selectedQuestionIndex by remember { mutableStateOf(0) }
    var customQuestionInput by remember { mutableStateOf("") }
    var securityAnswerInput by remember { mutableStateOf("") }
    
    // Change Password Verification Method ("OLD_PWD" or "SECURITY_Q")
    var changeVerifyMethod by remember { mutableStateOf("OLD_PWD") }

    var authError by remember { mutableStateOf("") }

    val defaultQuestions = remember {
        listOf(
            "What is your Confirmation Name?",
            "What parish/church were you baptized or confirmed in?",
            "Who is your favorite patron saint?",
            "What town or city were you born in?",
            "Custom Question..."
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = when (currentMode) {
                    "INITIAL_SETUP" -> "Set Up Ledger Security 🔑"
                    "CHANGE_PWD" -> "Change Ledger Password 🔐"
                    "UNLOCK_SECURITY" -> "Security Verification 🛡️"
                    else -> "Unlock Conscience Ledger 🔒"
                },
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif),
                color = GoldBright
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                when (currentMode) {
                    "INITIAL_SETUP" -> {
                        Text(
                            text = "Establish a private password and personal security question to protect your confession preparation ledger. Created once and stored securely offline.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedParchment
                        )

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("New Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("ledger_pwd_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldBright,
                                unfocusedBorderColor = DarkCathedralCardBorder
                            )
                        )

                        OutlinedTextField(
                            value = passwordConfirmInput,
                            onValueChange = { passwordConfirmInput = it },
                            label = { Text("Confirm Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("ledger_pwd_confirm_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldBright,
                                unfocusedBorderColor = DarkCathedralCardBorder
                            )
                        )

                        HorizontalDivider(color = DarkCathedralCardBorder, modifier = Modifier.padding(vertical = 4.dp))

                        Text(
                            text = "Personal Security Question (For Password Changes)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldBright
                        )

                        var expandedQuestionDropdown by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { expandedQuestionDropdown = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, GoldSacred)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = defaultQuestions[selectedQuestionIndex],
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ParchmentLight,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = GoldBright)
                                }
                            }

                            DropdownMenu(
                                expanded = expandedQuestionDropdown,
                                onDismissRequest = { expandedQuestionDropdown = false },
                                modifier = Modifier.background(DarkCathedralSurface)
                            ) {
                                defaultQuestions.forEachIndexed { index, qText ->
                                    DropdownMenuItem(
                                        text = { Text(qText, color = ParchmentLight, style = MaterialTheme.typography.bodySmall) },
                                        onClick = {
                                            selectedQuestionIndex = index
                                            expandedQuestionDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        if (selectedQuestionIndex == defaultQuestions.lastIndex) {
                            OutlinedTextField(
                                value = customQuestionInput,
                                onValueChange = { customQuestionInput = it },
                                label = { Text("Your Custom Security Question") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldBright,
                                    unfocusedBorderColor = DarkCathedralCardBorder
                                )
                            )
                        }

                        OutlinedTextField(
                            value = securityAnswerInput,
                            onValueChange = { securityAnswerInput = it },
                            label = { Text("Your Security Answer") },
                            placeholder = { Text("Personal answer known only to you") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("ledger_security_a_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldBright,
                                unfocusedBorderColor = DarkCathedralCardBorder
                            )
                        )
                    }

                    "UNLOCK_PWD" -> {
                        Text(
                            text = "Enter your private password to unlock your confession prep ledger.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedParchment
                        )

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("ledger_pwd_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldBright,
                                unfocusedBorderColor = DarkCathedralCardBorder
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(
                                onClick = {
                                    authError = ""
                                    securityAnswerInput = ""
                                    currentMode = "UNLOCK_SECURITY"
                                }
                            ) {
                                Text("Forgot Password? 🛡️", style = MaterialTheme.typography.labelSmall, color = GoldBright)
                            }

                            TextButton(
                                onClick = {
                                    authError = ""
                                    oldPasswordInput = ""
                                    securityAnswerInput = ""
                                    passwordInput = ""
                                    passwordConfirmInput = ""
                                    currentMode = "CHANGE_PWD"
                                }
                            ) {
                                Text("Change Password ⚙️", style = MaterialTheme.typography.labelSmall, color = GoldLight)
                            }
                        }
                    }

                    "UNLOCK_SECURITY" -> {
                        Text(
                            text = "Answer your personal security question to unlock your ledger.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedParchment
                        )

                        Surface(
                            color = DarkCathedralSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "SECURITY QUESTION:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GoldDark
                                )
                                Text(
                                    text = savedQuestion,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = GoldBright
                                )
                            }
                        }

                        OutlinedTextField(
                            value = securityAnswerInput,
                            onValueChange = { securityAnswerInput = it },
                            label = { Text("Your Personal Security Answer") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("ledger_security_a_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldBright,
                                unfocusedBorderColor = DarkCathedralCardBorder
                            )
                        )

                        TextButton(
                            onClick = {
                                authError = ""
                                passwordInput = ""
                                currentMode = "UNLOCK_PWD"
                            }
                        ) {
                            Text("‹ Back to Password Unlock", style = MaterialTheme.typography.labelSmall, color = TextMutedParchment)
                        }
                    }

                    "CHANGE_PWD" -> {
                        Text(
                            text = "To change your password, verify your identity using either your old password or your personal security question.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedParchment
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = changeVerifyMethod == "OLD_PWD",
                                onClick = { changeVerifyMethod = "OLD_PWD"; authError = "" },
                                label = { Text("Use Old Password", style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldSacred,
                                    selectedLabelColor = Color.Black
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = changeVerifyMethod == "SECURITY_Q",
                                onClick = { changeVerifyMethod = "SECURITY_Q"; authError = "" },
                                label = { Text("Security Question", style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldSacred,
                                    selectedLabelColor = Color.Black
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (changeVerifyMethod == "OLD_PWD") {
                            OutlinedTextField(
                                value = oldPasswordInput,
                                onValueChange = { oldPasswordInput = it },
                                label = { Text("Current Old Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("ledger_old_pwd_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldBright,
                                    unfocusedBorderColor = DarkCathedralCardBorder
                                )
                            )
                        } else {
                            Surface(
                                color = DarkCathedralSurfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "SECURITY QUESTION:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldDark
                                    )
                                    Text(
                                        text = savedQuestion,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = GoldBright
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = securityAnswerInput,
                                onValueChange = { securityAnswerInput = it },
                                label = { Text("Your Personal Security Answer") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("ledger_security_a_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldBright,
                                    unfocusedBorderColor = DarkCathedralCardBorder
                                )
                            )
                        }

                        HorizontalDivider(color = DarkCathedralCardBorder, modifier = Modifier.padding(vertical = 4.dp))

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("New Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("ledger_new_pwd_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldBright,
                                unfocusedBorderColor = DarkCathedralCardBorder
                            )
                        )

                        OutlinedTextField(
                            value = passwordConfirmInput,
                            onValueChange = { passwordConfirmInput = it },
                            label = { Text("Confirm New Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("ledger_new_pwd_confirm_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldBright,
                                unfocusedBorderColor = DarkCathedralCardBorder
                            )
                        )

                        if (!isFirstTime) {
                            TextButton(
                                onClick = {
                                    authError = ""
                                    passwordInput = ""
                                    currentMode = "UNLOCK_PWD"
                                }
                            ) {
                                Text("‹ Back to Unlock", style = MaterialTheme.typography.labelSmall, color = TextMutedParchment)
                            }
                        }
                    }
                }

                if (authError.isNotEmpty()) {
                    Text(
                        text = authError,
                        style = MaterialTheme.typography.labelSmall,
                        color = CrimsonLight,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when (currentMode) {
                        "INITIAL_SETUP" -> {
                            val finalQuestion = if (selectedQuestionIndex == defaultQuestions.lastIndex) {
                                customQuestionInput.trim()
                            } else {
                                defaultQuestions[selectedQuestionIndex]
                            }

                            if (passwordInput.length < 4) {
                                authError = "Password must be at least 4 characters."
                            } else if (passwordInput != passwordConfirmInput) {
                                authError = "Passwords do not match."
                            } else if (finalQuestion.isEmpty()) {
                                authError = "Please select or write a security question."
                            } else if (securityAnswerInput.trim().isEmpty()) {
                                authError = "Please answer your personal security question."
                            } else {
                                sharedPref.edit()
                                    .putString("ledger_pwd", passwordInput)
                                    .putString("ledger_security_q", finalQuestion)
                                    .putString("ledger_security_a", securityAnswerInput.trim().lowercase())
                                    .apply()

                                Toast.makeText(context, "Security credentials saved.", Toast.LENGTH_SHORT).show()
                                onUnlocked()
                            }
                        }

                        "UNLOCK_PWD" -> {
                            if (passwordInput == savedPwd) {
                                onUnlocked()
                            } else {
                                authError = "Incorrect password. Try again or use security question."
                            }
                        }

                        "UNLOCK_SECURITY" -> {
                            val normalizedSavedAnswer = savedAnswer.trim().lowercase()
                            val normalizedUserAnswer = securityAnswerInput.trim().lowercase()

                            if (normalizedSavedAnswer.isNotEmpty() && normalizedUserAnswer == normalizedSavedAnswer) {
                                Toast.makeText(context, "Identity verified via security question.", Toast.LENGTH_SHORT).show()
                                onUnlocked()
                            } else {
                                authError = "Incorrect security answer. Please check spelling."
                            }
                        }

                        "CHANGE_PWD" -> {
                            var isVerified = false
                            if (changeVerifyMethod == "OLD_PWD") {
                                if (oldPasswordInput == savedPwd) {
                                    isVerified = true
                                } else {
                                    authError = "Incorrect current old password."
                                }
                            } else {
                                val normalizedSavedAnswer = savedAnswer.trim().lowercase()
                                val normalizedUserAnswer = securityAnswerInput.trim().lowercase()
                                if (normalizedSavedAnswer.isNotEmpty() && normalizedUserAnswer == normalizedSavedAnswer) {
                                    isVerified = true
                                } else {
                                    authError = "Incorrect security answer."
                                }
                            }

                            if (isVerified) {
                                if (passwordInput.length < 4) {
                                    authError = "New password must be at least 4 characters."
                                } else if (passwordInput != passwordConfirmInput) {
                                    authError = "New passwords do not match."
                                } else {
                                    sharedPref.edit().putString("ledger_pwd", passwordInput).apply()
                                    Toast.makeText(context, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                                    onUnlocked()
                                }
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black),
                modifier = Modifier.testTag("ledger_auth_confirm_btn")
            ) {
                Text(
                    text = when (currentMode) {
                        "INITIAL_SETUP" -> "Save Credentials & Unlock"
                        "CHANGE_PWD" -> "Update Password"
                        "UNLOCK_SECURITY" -> "Verify & Unlock"
                        else -> "Unlock"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = TextMutedParchment)
            }
        },
        containerColor = DarkCathedralSurface,
        tonalElevation = 6.dp
    )
}

@Composable
fun ExportExaminationPdfDialog(
    onDismiss: () -> Unit,
    onConfirmExport: (password: String?) -> Unit
) {
    var passwordInput by remember { mutableStateOf("") }
    var passwordConfirmInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Export Examinations to PDF",
                color = GoldBright,
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Export your examination entries to a password-protected PDF document for personal record-keeping, ensuring full data privacy.",
                    color = ParchmentLight,
                    fontSize = 12.sp
                )

                Surface(
                    color = DarkCathedralSurfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            "🔒 SECURE PDF ENCRYPTION",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = GoldBright
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Set a password to encrypt your PDF record (128-bit encryption). Leave blank if you prefer an unencrypted PDF.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedParchment
                        )
                    }
                }

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it; errorMessage = "" },
                    label = { Text("PDF Encryption Password (Optional)") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldBright,
                        unfocusedBorderColor = DarkCathedralCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("pdf_export_pwd_input")
                )

                if (passwordInput.isNotEmpty()) {
                    OutlinedTextField(
                        value = passwordConfirmInput,
                        onValueChange = { passwordConfirmInput = it; errorMessage = "" },
                        label = { Text("Confirm PDF Encryption Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldBright,
                            unfocusedBorderColor = DarkCathedralCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("pdf_export_confirm_pwd_input")
                    )
                }

                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = CrimsonLight, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (passwordInput.isNotEmpty()) {
                        if (passwordInput.length < 4) {
                            errorMessage = "Password must be at least 4 characters long."
                            return@Button
                        }
                        if (passwordInput != passwordConfirmInput) {
                            errorMessage = "Passwords do not match."
                            return@Button
                        }
                        onConfirmExport(passwordInput)
                    } else {
                        onConfirmExport(null)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black),
                modifier = Modifier.testTag("pdf_export_confirm_btn")
            ) {
                Text("Generate PDF", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMutedParchment)
            }
        },
        containerColor = DarkCathedralSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

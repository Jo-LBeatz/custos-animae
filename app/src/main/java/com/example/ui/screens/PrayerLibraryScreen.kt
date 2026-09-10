package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.PrayerEntity
import com.example.data.prayers.PrayerItem
import com.example.data.prayers.RosarySet
import com.example.data.prayers.TraditionalPrayerLibrary
import com.example.util.AppLanguage
import com.example.util.AppTranslationHelper
import com.example.util.PrayerMode
import com.example.util.PrayerPerspectiveFormatter
import com.example.util.getFormattedEnglishText
import com.example.ui.MainViewModel
import com.example.ui.components.GothicHeader
import com.example.ui.components.IlluminatedDivider
import com.example.ui.theme.*

import com.example.ui.components.TrackLazyListScroll
import com.example.ui.components.TrackScrollState
import com.example.ui.components.fullScreenToggleOnTap
import com.example.ui.components.rememberDirectionalScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll

@Composable
fun PrayerLibraryScreen(
    viewModel: MainViewModel
) {
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var showFullRosaryScreen by remember { mutableStateOf(false) }
    val isLatinMode by viewModel.isLatinMode.collectAsStateWithLifecycle()
    val manualIntention by viewModel.manualDevotionalIntention.collectAsStateWithLifecycle()

    val searchQuery by viewModel.prayerSearchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.prayerSearchResults.collectAsStateWithLifecycle()

    val searchListState = remember(searchQuery) { androidx.compose.foundation.lazy.LazyListState() }

    if (searchQuery.isNotBlank() && searchResults.isNotEmpty()) {
        TrackLazyListScroll(listState = searchListState, viewModel = viewModel)
    }

    LaunchedEffect(selectedCategoryIndex, searchQuery) {
        viewModel.setReadingProgress(0f)
        viewModel.setBottomNavVisible(!showFullRosaryScreen)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setReadingProgress(0f)
            viewModel.setBottomNavVisible(true)
            viewModel.setTopHeaderVisible(true)
        }
    }

    if (showFullRosaryScreen) {
        FuturisticRosaryScreen(
            isLatinInitial = isLatinMode,
            onExit = {
                showFullRosaryScreen = false
            },
            viewModel = viewModel
        )
        return
    }

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val isFemaleAccount = remember(userProfile) { userProfile?.gender?.equals("FEMALE", ignoreCase = true) == true }

    val isTopHeaderVisible by viewModel.isTopHeaderVisible.collectAsStateWithLifecycle()
    val bookmarkedPrayers by viewModel.bookmarkedPrayers.collectAsStateWithLifecycle()
    val bookmarkedIds = remember(bookmarkedPrayers) { bookmarkedPrayers.map { it.id }.toSet() }

    val categories = remember(bookmarkedPrayers.size, isFemaleAccount) {
        buildList {
            add("⭐ Favorites (${bookmarkedPrayers.size})")
            add("Creeds")
            add("Morning Prayers")
            if (!isFemaleAccount) {
                add("Prayer before Work")
            }
            add("Prayer before Study")
            add("Leonine Prayers")
            add("Night Prayers")
            add("Holy Rosary")
            add("Eucharistic")
            add("Protection & Saints")
            add("Angelus & Regina")
            add("Immaculate Heart")
            add("Sacred Heart")
        }
    }

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
                val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
                val activePrayerMode = PrayerMode.fromCode(userProfile?.prayerMode)

                GothicHeader(
                    title = "TRADITIONAL PRAYERS",
                    subtitle = "100% Offline Catholic Prayer Treasury",
                    badgeText = "${if (isLatinMode) "Latin" else "English"} • ${activePrayerMode.badgeText}"
                )

                // Local Room DB Prayer Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setPrayerSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("prayer_search_input"),
                    placeholder = { Text("Search prayers, litanies, Latin & English text...", color = TextMutedParchment, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = GoldBright) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setPrayerSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = GoldBright)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldSacred,
                        unfocusedBorderColor = DarkCathedralCardBorder,
                        focusedContainerColor = DarkCathedralSurface,
                        unfocusedContainerColor = DarkCathedralSurface,
                        focusedTextColor = ParchmentLight,
                        unfocusedTextColor = ParchmentLight
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (searchQuery.isBlank()) {
                    // Language Toggle, Perspective Mode Toggle & Category Tabs Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val safeTabIdx = selectedCategoryIndex.coerceIn(0, (categories.size - 1).coerceAtLeast(0))
                        ScrollableTabRow(
                            selectedTabIndex = safeTabIdx,
                            containerColor = DarkCathedralSurface,
                            contentColor = GoldBright,
                            edgePadding = 0.dp,
                            modifier = Modifier.weight(1f)
                        ) {
                            categories.forEachIndexed { index, cat ->
                                Tab(
                                    selected = safeTabIdx == index,
                                    onClick = { selectedCategoryIndex = index },
                                    text = {
                                        Text(
                                            text = cat,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = if (safeTabIdx == index) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = if (safeTabIdx == index) GoldBright else TextMutedParchment
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Perspective Mode Toggle Button (Individual -> Group -> Traditional)
                        FilterChip(
                            selected = activePrayerMode != PrayerMode.TRADITIONAL,
                            onClick = {
                                val nextMode = when (activePrayerMode) {
                                    PrayerMode.INDIVIDUAL -> PrayerMode.GROUP
                                    PrayerMode.GROUP -> PrayerMode.TRADITIONAL
                                    PrayerMode.TRADITIONAL -> PrayerMode.INDIVIDUAL
                                }
                                viewModel.updatePrayerMode(nextMode)
                            },
                            label = {
                                Text(
                                    text = when (activePrayerMode) {
                                        PrayerMode.INDIVIDUAL -> "Solo"
                                        PrayerMode.GROUP -> "Group"
                                        PrayerMode.TRADITIONAL -> "Trad"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activePrayerMode == PrayerMode.INDIVIDUAL) GoldBright else ParchmentLight
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (activePrayerMode) {
                                        PrayerMode.INDIVIDUAL -> Icons.Default.Person
                                        PrayerMode.GROUP -> Icons.Default.Groups
                                        PrayerMode.TRADITIONAL -> Icons.AutoMirrored.Filled.MenuBook
                                    },
                                    contentDescription = "Toggle Prayer Mode",
                                    tint = GoldBright,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = DarkCathedralSurfaceVariant,
                                selectedContainerColor = DarkCathedralSurface
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = DarkCathedralCardBorder,
                                selectedBorderColor = GoldSacred,
                                enabled = true,
                                selected = activePrayerMode != PrayerMode.TRADITIONAL
                            ),
                            modifier = Modifier.testTag("prayer_mode_toggle_btn")
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = { viewModel.toggleLatinMode() },
                            modifier = Modifier
                                .background(DarkCathedralSurfaceVariant, CircleShape)
                                .border(1.dp, GoldSacred, CircleShape)
                                .testTag("toggle_latin_mode_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Toggle Latin/English",
                                tint = GoldBright
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Offline Prayer Search Results (${searchResults.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                            color = GoldBright
                        )
                        TextButton(onClick = { viewModel.setPrayerSearchQuery("") }) {
                            Text("Clear Search", color = TextMutedParchment, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        val safeCategoryIndex = selectedCategoryIndex.coerceIn(0, (categories.size - 1).coerceAtLeast(0))
        val activeCategoryName = categories.getOrElse(safeCategoryIndex) { "" }

        val filteredSearchResults = remember(searchResults, isFemaleAccount) {
            if (isFemaleAccount) {
                searchResults.filterNot { it.id == "prayer_before_work" || it.id == "st_joseph_before_work" || it.title.contains("Prayer before work", ignoreCase = true) }
            } else searchResults
        }

        if (searchQuery.isBlank()) {
            when {
                activeCategoryName.startsWith("⭐ Favorites") -> {
                    val filteredBookmarks = if (isFemaleAccount) {
                        bookmarkedPrayers.filterNot { it.id == "prayer_before_work" || it.id == "st_joseph_before_work" || it.title.contains("Prayer before work", ignoreCase = true) }
                    } else bookmarkedPrayers
                    BookmarkedPrayersList(bookmarkedPrayers = filteredBookmarks, isLatin = isLatinMode, viewModel = viewModel, manualIntention = manualIntention)
                }
                activeCategoryName == "Creeds" -> PrayerCategoryList(prayers = TraditionalPrayerLibrary.traditionalCreeds, isLatin = isLatinMode, viewModel = viewModel, manualIntention = manualIntention, onManualIntentionChange = { viewModel.updateManualDevotionalIntention(it) }, bookmarkedIds = bookmarkedIds)
                activeCategoryName == "Morning Prayers" -> {
                    val filteredMorning = if (isFemaleAccount) {
                        TraditionalPrayerLibrary.morningPrayers.filterNot { it.id == "prayer_before_work" || it.id == "st_joseph_before_work" || it.title.contains("Prayer before work", ignoreCase = true) }
                    } else TraditionalPrayerLibrary.morningPrayers
                    PrayerCategoryList(prayers = filteredMorning, isLatin = isLatinMode, viewModel = viewModel, manualIntention = manualIntention, onManualIntentionChange = { viewModel.updateManualDevotionalIntention(it) }, bookmarkedIds = bookmarkedIds)
                }
                activeCategoryName == "Prayer before Work" -> PrayerCategoryList(prayers = TraditionalPrayerLibrary.prayersBeforeWork, isLatin = isLatinMode, viewModel = viewModel, manualIntention = manualIntention, onManualIntentionChange = { viewModel.updateManualDevotionalIntention(it) }, bookmarkedIds = bookmarkedIds)
                activeCategoryName == "Prayer before Study" -> PrayerCategoryList(prayers = TraditionalPrayerLibrary.prayersBeforeStudy, isLatin = isLatinMode, viewModel = viewModel, manualIntention = manualIntention, onManualIntentionChange = { viewModel.updateManualDevotionalIntention(it) }, bookmarkedIds = bookmarkedIds)
                activeCategoryName == "Leonine Prayers" -> PrayerCategoryList(prayers = TraditionalPrayerLibrary.leoninePrayers, isLatin = isLatinMode, viewModel = viewModel, manualIntention = manualIntention, onManualIntentionChange = { viewModel.updateManualDevotionalIntention(it) }, bookmarkedIds = bookmarkedIds)
                activeCategoryName == "Night Prayers" -> PrayerCategoryList(prayers = TraditionalPrayerLibrary.nightPrayers, isLatin = isLatinMode, viewModel = viewModel, manualIntention = manualIntention, onManualIntentionChange = { viewModel.updateManualDevotionalIntention(it) }, bookmarkedIds = bookmarkedIds)
                activeCategoryName == "Holy Rosary" -> InteractiveRosaryViewer(isLatin = isLatinMode, viewModel = viewModel, onLaunchFullRosary = { showFullRosaryScreen = true })
                activeCategoryName == "Eucharistic" -> PrayerCategoryList(prayers = TraditionalPrayerLibrary.eucharisticDevotions, isLatin = isLatinMode, viewModel = viewModel, manualIntention = manualIntention, onManualIntentionChange = { viewModel.updateManualDevotionalIntention(it) }, bookmarkedIds = bookmarkedIds)
                activeCategoryName == "Protection & Saints" -> {
                    val filteredProtection = if (isFemaleAccount) {
                        TraditionalPrayerLibrary.protectionAndSaints.filterNot { it.id == "prayer_before_work" || it.id == "st_joseph_before_work" || it.title.contains("Prayer before work", ignoreCase = true) }
                    } else TraditionalPrayerLibrary.protectionAndSaints
                    PrayerCategoryList(prayers = filteredProtection, isLatin = isLatinMode, viewModel = viewModel, manualIntention = manualIntention, onManualIntentionChange = { viewModel.updateManualDevotionalIntention(it) }, bookmarkedIds = bookmarkedIds)
                }
                activeCategoryName == "Angelus & Regina" -> PrayerCategoryList(prayers = TraditionalPrayerLibrary.angelusAndRegina, isLatin = isLatinMode, viewModel = viewModel, manualIntention = manualIntention, onManualIntentionChange = { viewModel.updateManualDevotionalIntention(it) }, bookmarkedIds = bookmarkedIds)
                activeCategoryName == "Immaculate Heart" -> PrayerCategoryList(prayers = TraditionalPrayerLibrary.devotionsImmaculateHeart, isLatin = isLatinMode, viewModel = viewModel, manualIntention = manualIntention, onManualIntentionChange = { viewModel.updateManualDevotionalIntention(it) }, bookmarkedIds = bookmarkedIds)
                activeCategoryName == "Sacred Heart" -> PrayerCategoryList(prayers = TraditionalPrayerLibrary.devotionsSacredHeart, isLatin = isLatinMode, viewModel = viewModel, manualIntention = manualIntention, onManualIntentionChange = { viewModel.updateManualDevotionalIntention(it) }, bookmarkedIds = bookmarkedIds)
            }
        } else {
            // Room DB Search Results View
            if (filteredSearchResults.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface)
                ) {
                    Text(
                        text = "No prayers found matching '$searchQuery' in offline library.",
                        color = TextMutedParchment,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                LazyColumn(
                    state = searchListState,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(filteredSearchResults, key = { it.id }) { prayerEntity ->
                        PrayerEntityCard(prayer = prayerEntity, isLatin = isLatinMode, viewModel = viewModel, manualIntention = manualIntention, bookmarkedIds = bookmarkedIds)
                    }
                }
            }
        }
    }
}

@Composable
fun BookmarkedPrayersList(
    bookmarkedPrayers: List<PrayerEntity>,
    isLatin: Boolean,
    viewModel: MainViewModel,
    manualIntention: String
) {
    if (bookmarkedPrayers.isEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.BookmarkBorder,
                    contentDescription = null,
                    tint = GoldBright,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No Bookmarked Prayers Yet",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldBright
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap the bookmark icon (🔖) on any traditional prayer, litany, or devotion in the library to save it to your personal offline favorites.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMutedParchment,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        val listState = remember(bookmarkedPrayers) { androidx.compose.foundation.lazy.LazyListState() }
        TrackLazyListScroll(listState = listState, viewModel = viewModel)

        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            items(bookmarkedPrayers, key = { it.id }) { prayer ->
                PrayerEntityCard(
                    prayer = prayer,
                    isLatin = isLatin,
                    viewModel = viewModel,
                    manualIntention = manualIntention,
                    bookmarkedIds = bookmarkedPrayers.map { it.id }.toSet()
                )
            }
        }
    }
}

@Composable
fun PrayerEntityCard(
    prayer: PrayerEntity,
    isLatin: Boolean,
    viewModel: MainViewModel,
    manualIntention: String = "",
    bookmarkedIds: Set<String> = emptySet()
) {
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val translatedTitle = if (!isLatin) AppTranslationHelper.translate(prayer.title, appLanguage) else prayer.title
    val translatedCategory = if (!isLatin) AppTranslationHelper.translate(prayer.category, appLanguage) else prayer.category
    val translatedSubtitle = if (!isLatin && prayer.subtitle.isNotBlank()) AppTranslationHelper.translate(prayer.subtitle, appLanguage) else prayer.subtitle
    val isBookmarked = prayer.isBookmarked || bookmarkedIds.contains(prayer.id)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("prayer_search_card_${prayer.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
        border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.7f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = translatedTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldBright
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = CrimsonBurgundy.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.5f))
                    ) {
                        com.example.ui.components.ResponsiveText(
                            text = translatedCategory,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldLight,
                            maxLines = 2,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.togglePrayerBookmark(prayer.id, isBookmarked) },
                    modifier = Modifier.testTag("bookmark_entity_btn_${prayer.id}")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isBookmarked) "Remove Bookmark" else "Bookmark Prayer",
                        tint = if (isBookmarked) GoldBright else TextMutedParchment
                    )
                }
            }

            if (translatedSubtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = translatedSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedParchment
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            IlluminatedDivider()
            Spacer(modifier = Modifier.height(10.dp))

            val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
            val activePrayerMode = PrayerMode.fromCode(userProfile?.prayerMode)

            val basePrayerText = if (isLatin && prayer.latinText.isNotBlank()) {
                prayer.latinText
            } else {
                PrayerPerspectiveFormatter.format(prayer.englishText, activePrayerMode)
            }

            val prayerText = if (!isLatin) {
                AppTranslationHelper.translatePrayerText(prayer.id, basePrayerText, appLanguage)
            } else {
                basePrayerText
            }

            val formattedAnnotatedString = remember(prayerText, manualIntention) {
                val placeholders = listOf("(here make your request)", "(specify petition)", "(mention petition)")
                androidx.compose.ui.text.buildAnnotatedString {
                    var cursor = 0
                    val len = prayerText.length
                    while (cursor < len) {
                        var foundPlaceholder: String? = null
                        for (p in placeholders) {
                            if (prayerText.startsWith(p, cursor, ignoreCase = true)) {
                                foundPlaceholder = p
                                break
                            }
                        }
                        if (foundPlaceholder != null) {
                            if (manualIntention.isNotBlank()) {
                                withStyle(style = androidx.compose.ui.text.SpanStyle(color = GoldBright, fontWeight = FontWeight.Bold)) {
                                    append(" [ $manualIntention ] ")
                                }
                            } else {
                                withStyle(style = androidx.compose.ui.text.SpanStyle(color = GoldLight.copy(alpha = 0.8f), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)) {
                                    append(prayerText.substring(cursor, cursor + foundPlaceholder.length))
                                }
                            }
                            cursor += foundPlaceholder.length
                        } else {
                            append(prayerText[cursor].toString())
                            cursor++
                        }
                    }
                }
            }

            Text(
                text = formattedAnnotatedString,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Serif,
                    lineHeight = 22.sp
                ),
                color = ParchmentLight
            )

            // Optional Pronunciation Controller
            val textToPronounce = prayerText
            Spacer(modifier = Modifier.height(12.dp))
            com.example.ui.components.LatinAudioPlayerController(
                viewModel = viewModel,
                prayerId = "entity_${prayer.id}",
                prayerText = textToPronounce,
                isLatin = isLatin
            )
        }
    }
}


@Composable
fun PrayerCategoryList(
    prayers: List<PrayerItem>,
    isLatin: Boolean,
    viewModel: MainViewModel,
    manualIntention: String,
    onManualIntentionChange: (String) -> Unit,
    bookmarkedIds: Set<String> = emptySet()
) {
    val subsections = remember(prayers) {
        val list = prayers.map { it.subsection }.filter { it.isNotBlank() }.distinct()
        if (list.isNotEmpty()) listOf("All") + list else emptyList()
    }
    var selectedSubsection by remember(prayers) { mutableStateOf("All") }

    val filteredPrayers = remember(prayers, selectedSubsection) {
        if (selectedSubsection == "All" || selectedSubsection.isBlank()) {
            prayers
        } else {
            prayers.filter { it.subsection == selectedSubsection }
        }
    }

    val listState = remember(filteredPrayers) { androidx.compose.foundation.lazy.LazyListState() }
    TrackLazyListScroll(listState = listState, viewModel = viewModel)

    val hasIntercessoryPrayers = remember(prayers) {
        prayers.any {
            it.englishText.contains("(here make your request)", ignoreCase = true) ||
            it.englishText.contains("(specify petition)", ignoreCase = true) ||
            it.englishText.contains("(mention petition)", ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (subsections.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subsections.forEach { sub ->
                    val isSelected = selectedSubsection == sub
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSubsection = sub },
                        label = {
                            Text(
                                text = sub,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = FontFamily.Serif
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldSacred,
                            selectedLabelColor = DarkCathedralBackground,
                            containerColor = DarkCathedralSurfaceVariant,
                            labelColor = TextMutedParchment
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) GoldBright else DarkCathedralCardBorder
                        )
                    )
                }
            }
        }

        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            if (hasIntercessoryPrayers) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("intention_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCathedralSurfaceVariant),
                        border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "MANUAL DEVOTIONAL INTENTION",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif
                                ),
                                color = GoldBright
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Enter your custom petition. It will dynamically embed into placeholders (e.g. '(specify petition)', '(here make your request)') within the intercessory prayers below for focused meditation.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedParchment
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = manualIntention,
                                onValueChange = onManualIntentionChange,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("manual_intention_input"),
                                placeholder = { Text("e.g. For the health and recovery of my family...", color = TextMutedParchment, fontSize = 13.sp) },
                                trailingIcon = {
                                    if (manualIntention.isNotEmpty()) {
                                        IconButton(onClick = { onManualIntentionChange("") }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = GoldBright)
                                        }
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldSacred,
                                    unfocusedBorderColor = DarkCathedralCardBorder,
                                    focusedContainerColor = DarkCathedralBackground,
                                    unfocusedContainerColor = DarkCathedralBackground,
                                    focusedTextColor = ParchmentLight,
                                    unfocusedTextColor = ParchmentLight
                                ),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            items(filteredPrayers, key = { it.id }) { prayer ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("prayer_card_${prayer.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
                    border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.7f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = prayer.title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = GoldBright,
                                modifier = Modifier.weight(1f)
                            )

                            val isBookmarked = bookmarkedIds.contains(prayer.id)
                            IconButton(
                                onClick = { viewModel.togglePrayerBookmark(prayer.id, isBookmarked) },
                                modifier = Modifier.testTag("bookmark_btn_${prayer.id}")
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = if (isBookmarked) "Remove Bookmark" else "Bookmark Prayer",
                                    tint = if (isBookmarked) GoldBright else TextMutedParchment
                                )
                            }
                        }

                        if (prayer.subsection.isNotBlank() && selectedSubsection == "All") {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = CrimsonBurgundy.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(0.5.dp, GoldSacred.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = prayer.subsection,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldLight,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (prayer.subtitle.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = prayer.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedParchment
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        IlluminatedDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
                        val activePrayerMode = PrayerMode.fromCode(userProfile?.prayerMode)

                        val textToShow = if (isLatin && prayer.latinText.isNotBlank()) prayer.latinText else prayer.getFormattedEnglishText(activePrayerMode)
                        val formattedAnnotatedString = remember(textToShow, manualIntention) {
                            val placeholders = listOf("(here make your request)", "(specify petition)", "(mention petition)")
                            androidx.compose.ui.text.buildAnnotatedString {
                                var cursor = 0
                                val len = textToShow.length
                                while (cursor < len) {
                                    var foundPlaceholder: String? = null
                                    for (p in placeholders) {
                                        if (textToShow.startsWith(p, cursor, ignoreCase = true)) {
                                            foundPlaceholder = p
                                            break
                                        }
                                    }
                                    if (foundPlaceholder != null) {
                                        if (manualIntention.isNotBlank()) {
                                            withStyle(style = androidx.compose.ui.text.SpanStyle(color = GoldBright, fontWeight = FontWeight.Bold)) {
                                                append(" [ $manualIntention ] ")
                                            }
                                        } else {
                                            withStyle(style = androidx.compose.ui.text.SpanStyle(color = GoldLight.copy(alpha = 0.8f), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)) {
                                                append(textToShow.substring(cursor, cursor + foundPlaceholder.length))
                                            }
                                        }
                                        cursor += foundPlaceholder.length
                                    } else {
                                        append(textToShow[cursor].toString())
                                        cursor++
                                    }
                                }
                            }
                        }

                        Text(
                            text = formattedAnnotatedString,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            ),
                            color = ParchmentLight
                        )

                        // Optional Pronunciation Controller
                        val textToPronounce = if (isLatin && prayer.latinText.isNotBlank()) prayer.latinText else prayer.englishText
                        Spacer(modifier = Modifier.height(12.dp))
                        com.example.ui.components.LatinAudioPlayerController(
                            viewModel = viewModel,
                            prayerId = "item_${prayer.id}",
                            prayerText = textToPronounce,
                            isLatin = isLatin
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveRosaryViewer(
    isLatin: Boolean,
    viewModel: MainViewModel,
    onLaunchFullRosary: () -> Unit = {}
) {
    var rosarySection by remember { mutableStateOf(1) } // 0: Opening, 1: Decades, 2: Concluding
    var selectedSetIndex by remember { mutableStateOf(0) }
    var currentMysteryIndex by remember { mutableStateOf(0) }
    var beadCounter by remember { mutableStateOf(0) } // 0-10 beads

    val currentSet = TraditionalPrayerLibrary.rosarySets[selectedSetIndex]
    val currentMystery = currentSet.mysteries[currentMysteryIndex]

    val scrollState = rememberScrollState()
    TrackScrollState(scrollState = scrollState, viewModel = viewModel)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp)
    ) {
        // PROMINENT HERO BUTTON FOR FULL-SCREEN FUTURISTIC HOLY ROSARY
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLaunchFullRosary() }
                .testTag("launch_full_rosary_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurfaceVariant),
            border = BorderStroke(2.dp, GoldBright)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = CrimsonBurgundy,
                        shape = CircleShape,
                        border = BorderStroke(1.dp, GoldBright),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Full Screen Rosary",
                                tint = GoldBright,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "HOLY ROSARY MODE",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = GoldBright
                        )
                        Text(
                            text = "Full-Screen Cyber-Gothic Animations • Complete Non-Truncated Prayers",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedParchment
                        )
                    }
                }

                Button(
                    onClick = onLaunchFullRosary,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("launch_full_rosary_btn")
                ) {
                    Text("Launch ›", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section Selector: Opening -> Decades -> Concluding
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val sections = listOf("1. Opening", "2. Decades", "3. Concluding")
            sections.forEachIndexed { idx, secTitle ->
                FilterChip(
                    selected = rosarySection == idx,
                    onClick = { rosarySection = idx },
                    label = { com.example.ui.components.ResponsiveText(secTitle, fontSize = 12.sp, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldSacred,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (rosarySection) {
            0 -> {
                // Opening Prayers (Full Text)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
                    border = BorderStroke(1.5.dp, GoldBright)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isLatin) "Initium Rosarii • Opening Prayers" else "Opening Prayers of the Holy Rosary",
                            style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                            color = GoldBright
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // 1. Sign of the Cross
                        Text(text = "1. Sign of the Cross", style = MaterialTheme.typography.titleSmall, color = TextGoldHighlight)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isLatin) "☩ In nomine Patris, et Filii, et Spiritus Sancti. Amen."
                            else "☩ In the name of the Father, and of the Son, and of the Holy Ghost. Amen.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
                            color = ParchmentLight
                        )
                        com.example.ui.components.LatinAudioPlayerController(
                            viewModel = viewModel,
                            prayerId = "rosary_open_cross",
                            prayerText = if (isLatin) "In nomine Patris, et Filii, et Spiritus Sancti. Amen." else "In the name of the Father, and of the Son, and of the Holy Ghost. Amen.",
                            isLatin = isLatin,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // 2. Apostles' Creed
                        Text(text = "2. The Apostles' Creed (Credo)", style = MaterialTheme.typography.titleSmall, color = TextGoldHighlight)
                        Spacer(modifier = Modifier.height(4.dp))
                        val creedText = if (isLatin) "Credo in Deum Patrem omnipotentem, Creatorem caeli et terrae. Et in Iesum Christum, Filium eius unicum, Dominum nostrum: qui conceptus est de Spiritu Sancto, natus ex Maria Virgine, passus sub Pontio Pilato, crucifixus, mortuus, et sepultus: descendit ad inferos; tertia die resurrexit a mortuis; ascendit ad caelos; sedet ad dexteram Dei Patris omnipotentis: inde venturus est iudicare vivos et mortuos. Credo in Spiritum Sanctum, sanctam Ecclesiam catholicam, Sanctorum communionem, remissionem peccatorum, carnis resurrectionem, vitam aeternam. Amen."
                        else "I believe in God, the Father Almighty, Creator of heaven and earth. And in Jesus Christ, His only Son, our Lord: who was conceived by the Holy Ghost, born of the Virgin Mary, suffered under Pontius Pilate, was crucified, died, and was buried: He descended into hell; the third day He rose again from the dead; He ascended into heaven, and sitteth on the right hand of God the Father Almighty; from thence He shall come to judge the living and the dead. I believe in the Holy Ghost, the holy Catholic Church, the communion of Saints, the forgiveness of sins, the resurrection of the body, and life everlasting. Amen."
                        Text(
                            text = creedText,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
                            color = ParchmentLight
                        )
                        com.example.ui.components.LatinAudioPlayerController(
                            viewModel = viewModel,
                            prayerId = "rosary_open_creed",
                            prayerText = creedText,
                            isLatin = isLatin,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // 3. Our Father
                        Text(text = "3. Our Father (Pater Noster)", style = MaterialTheme.typography.titleSmall, color = TextGoldHighlight)
                        Spacer(modifier = Modifier.height(4.dp))
                        val paterText = if (isLatin) "Pater noster, qui es in caelis: sanctificetur nomen tuum: adveniat regnum tuum: fiat voluntas tua, sicut in caelo, et in terra. Panem nostrum cotidianum da nobis hodie: et dimitte nobis debita nostra, sicut et nos dimittimus debitoribus nostris: et ne nos inducas in tentationem: sed libera nos a malo. Amen."
                        else "Our Father, who art in heaven, hallowed be Thy name; Thy kingdom come; Thy will be done on earth as it is in heaven. Give us this day our daily bread; and forgive us our trespasses as we forgive those who trespass against us; and lead us not into temptation, but deliver us from evil. Amen."
                        Text(
                            text = paterText,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
                            color = ParchmentLight
                        )
                        com.example.ui.components.LatinAudioPlayerController(
                            viewModel = viewModel,
                            prayerId = "rosary_open_pater",
                            prayerText = paterText,
                            isLatin = isLatin,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // 4. Three Hail Marys for Faith, Hope, Charity
                        Text(text = "4. Three Hail Marys (Ave Maria) — For Faith, Hope, and Charity", style = MaterialTheme.typography.titleSmall, color = TextGoldHighlight)
                        Spacer(modifier = Modifier.height(4.dp))
                        val aveText = if (isLatin) "Ave Maria, gratia plena, Dominus tecum. Benedicta tu in mulieribus, et benedictus fructus ventris tui, Iesus. Sancta Maria, Mater Dei, ora pro nobis peccatoribus, nunc et in hora mortis nostrae. Amen."
                        else "Hail Mary, full of grace, the Lord is with thee. Blessed art thou among women, and blessed is the fruit of thy womb, Jesus. Holy Mary, Mother of God, pray for us sinners, now and at the hour of our death. Amen."
                        Text(
                            text = aveText,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
                            color = ParchmentLight
                        )
                        com.example.ui.components.LatinAudioPlayerController(
                            viewModel = viewModel,
                            prayerId = "rosary_open_ave",
                            prayerText = aveText,
                            isLatin = isLatin,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // 5. Glory Be
                        Text(text = "5. Glory Be (Gloria Patri)", style = MaterialTheme.typography.titleSmall, color = TextGoldHighlight)
                        Spacer(modifier = Modifier.height(4.dp))
                        val gloriaText = if (isLatin) "Gloria Patri, et Filio, et Spiritui Sancto. Sicut erat in principio, et nunc, et semper, et in saecula saeculorum. Amen."
                        else "Glory be to the Father, and to the Son, and to the Holy Ghost. As it was in the beginning, is now, and ever shall be, world without end. Amen."
                        Text(
                            text = gloriaText,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
                            color = ParchmentLight
                        )
                        com.example.ui.components.LatinAudioPlayerController(
                            viewModel = viewModel,
                            prayerId = "rosary_open_gloria",
                            prayerText = gloriaText,
                            isLatin = isLatin,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // 6. Fatima Prayer
                        Text(text = "6. Fatima Prayer (O mi Iesu)", style = MaterialTheme.typography.titleSmall, color = TextGoldHighlight)
                        Spacer(modifier = Modifier.height(4.dp))
                        val fatimaText = if (isLatin) "O mi Iesu, dimitte nobis debita nostra, libera nos ab igne inferni, duc in caelum omnes, praesertim eos, qui maxima indigent misericordia tua."
                        else "O My Jesus, forgive us our sins, save us from the fires of hell, lead all souls to Heaven, especially those most in need of Thy mercy."
                        Text(
                            text = fatimaText,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
                            color = TextGoldHighlight
                        )
                        com.example.ui.components.LatinAudioPlayerController(
                            viewModel = viewModel,
                            prayerId = "rosary_open_fatima",
                            prayerText = fatimaText,
                            isLatin = isLatin,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { rosarySection = 1 },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Begin Decades ›", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            1 -> {
                // Set selector (Joyful, Sorrowful, Glorious)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TraditionalPrayerLibrary.rosarySets.forEachIndexed { index, set ->
                        val isSelected = selectedSetIndex == index
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedSetIndex = index
                                currentMysteryIndex = 0
                                beadCounter = 0
                            },
                            label = { com.example.ui.components.ResponsiveText(set.category, fontSize = 11.sp, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldSacred,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Traditional Days: ${currentSet.days}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedParchment
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Mystery Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
                    border = BorderStroke(1.5.dp, GoldBright)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Decade ${currentMystery.number} of 5",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = GoldBright
                            )
                            Text(
                                text = "Fruit: ${currentMystery.fruit}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGoldHighlight
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isLatin && currentMystery.latinTitle.isNotBlank()) currentMystery.latinTitle else currentMystery.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ParchmentLight
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        IlluminatedDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Mystery Reflection:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedParchment
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentMystery.meditation,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
                            color = ParchmentLight
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Interactive Rosary Bead Counter
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurfaceVariant),
                            border = BorderStroke(1.dp, GoldDark)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Hail Mary Bead Counter: $beadCounter / 10",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = GoldBright
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // 10 Visual Rosary Beads
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    (1..10).forEach { beadNum ->
                                        val isCounted = beadNum <= beadCounter
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .background(
                                                    if (isCounted) GoldBright else DarkCathedralBackground,
                                                    CircleShape
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isCounted) GoldSacred else TextMutedParchment,
                                                    CircleShape
                                                )
                                                .clickable { beadCounter = beadNum },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$beadNum",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isCounted) Color.Black else TextMutedParchment
                                            )
                                        }
                                    }
                                }

                                val currentBeadText = if (beadCounter == 0) {
                                    if (isLatin) "Pater noster, qui es in caelis: sanctificetur nomen tuum: adveniat regnum tuum: fiat voluntas tua, sicut in caelo, et in terra. Panem nostrum cotidianum da nobis hodie: et dimitte nobis debita nostra, sicut et nos dimittimus debitoribus nostris: et ne nos inducas in tentationem: sed libera nos a malo. Amen."
                                    else "Our Father, who art in heaven, hallowed be Thy name; Thy kingdom come; Thy will be done on earth as it is in heaven. Give us this day our daily bread; and forgive us our trespasses as we forgive those who trespass against us; and lead us not into temptation, but deliver us from evil. Amen."
                                } else {
                                    if (isLatin) "Ave Maria, gratia plena, Dominus tecum. Benedicta tu in mulieribus, et benedictus fructus ventris tui, Iesus. Sancta Maria, Mater Dei, ora pro nobis peccatoribus, nunc et in hora mortis nostrae. Amen."
                                    else "Hail Mary, full of grace, the Lord is with thee. Blessed art thou among women, and blessed is the fruit of thy womb, Jesus. Holy Mary, Mother of God, pray for us sinners, now and at the hour of our death. Amen."
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                com.example.ui.components.LatinAudioPlayerController(
                                    viewModel = viewModel,
                                    prayerId = "decade_bead_${beadCounter}",
                                    prayerText = currentBeadText,
                                    isLatin = isLatin,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { if (beadCounter > 0) beadCounter-- },
                                        modifier = Modifier.testTag("decrement_bead_btn")
                                    ) {
                                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Minus Bead", tint = GoldBright)
                                    }

                                    Button(
                                        onClick = {
                                            if (beadCounter < 10) {
                                                beadCounter++
                                            } else if (currentMysteryIndex < 4) {
                                                currentMysteryIndex++
                                                beadCounter = 0
                                            } else {
                                                rosarySection = 2 // Move to Concluding Prayers
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black),
                                        modifier = Modifier.testTag("add_bead_btn")
                                    ) {
                                        Text(if (beadCounter < 10) "+ Hail Mary Bead" else (if (currentMysteryIndex < 4) "Next Decade ☩" else "Go to Concluding Prayers"), fontWeight = FontWeight.Bold)
                                    }

                                    IconButton(
                                        onClick = { beadCounter = 0 },
                                        modifier = Modifier.testTag("reset_beads_btn")
                                    ) {
                                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset Beads", tint = TextMutedParchment)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Previous / Next Decade Navigation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (currentMysteryIndex > 0) {
                                        currentMysteryIndex--
                                        beadCounter = 0
                                    }
                                },
                                enabled = currentMysteryIndex > 0,
                                border = BorderStroke(1.dp, GoldSacred)
                            ) {
                                Text("‹ Previous Decade", color = GoldBright)
                            }

                            Button(
                                onClick = {
                                    if (currentMysteryIndex < 4) {
                                        currentMysteryIndex++
                                        beadCounter = 0
                                    } else {
                                        rosarySection = 2
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black)
                            ) {
                                Text(if (currentMysteryIndex < 4) "Next Decade ›" else "Concluding Prayers ›", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            2 -> {
                // Concluding Prayers (Full Text)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
                    border = BorderStroke(1.5.dp, GoldBright)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isLatin) "Conclusio Rosarii • Concluding Prayers" else "Concluding Prayers of the Holy Rosary",
                            style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                            color = GoldBright
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // 1. Salve Regina
                        Text(text = "1. Salve Regina (Hail Holy Queen)", style = MaterialTheme.typography.titleSmall, color = TextGoldHighlight)
                        Spacer(modifier = Modifier.height(4.dp))
                        val salveText = if (isLatin) "Salve, Regina, Mater misericordiae, vita, dulcedo, et spes nostra, salve. Ad te clamamus, exsules filii Evae. Ad te suspiramus, gementes et flentes in hac lacrimarum valle. Eia ergo, advocata nostra, illos tuos misericordes oculos ad nos converte. Et Iesum, benedictum fructum ventris tui, nobis post hoc exsilium ostende. O clemens, o pia, o dulcis Virgo Maria."
                        else "Hail, Holy Queen, Mother of Mercy, our life, our sweetness, and our hope. To thee do we cry, poor banished children of Eve. To thee do we send up our sighs, mourning and weeping in this valley of tears. Turn then, most gracious advocate, thine eyes of mercy toward us, and after this our exile, show unto us the blessed fruit of thy womb, Jesus. O clement, O loving, O sweet Virgin Mary."
                        Text(
                            text = salveText,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
                            color = ParchmentLight
                        )
                        com.example.ui.components.LatinAudioPlayerController(
                            viewModel = viewModel,
                            prayerId = "rosary_close_salve",
                            prayerText = salveText,
                            isLatin = isLatin,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isLatin) "V. Ora pro nobis, Sancta Dei Genetrix.\nR. Ut digni efficiamur promissionibus Christi."
                            else "V. Pray for us, O Holy Mother of God.\nR. That we may be made worthy of the promises of Christ.",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                            color = TextGoldHighlight
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // 2. Final Rosary Prayer
                        Text(text = "2. Final Rosary Prayer", style = MaterialTheme.typography.titleSmall, color = TextGoldHighlight)
                        Spacer(modifier = Modifier.height(4.dp))
                        val finalRosaryPrayerText = if (isLatin) "Deus, cuius Unigenitus per vitam, mortem et resurrectionem suam nobis salutis aeternae praemia comparavit, concede, quaesumus: ut haec mysteria sacratissimo beatae Mariae Virginis Rosario recolentes, et imitemur quod continent, et quod promunt assequamur. Per eumdem Christum Dominum nostrum. Amen."
                        else "O God, whose only begotten Son, by His life, death, and resurrection, has purchased for us the rewards of eternal life, grant, we beseech Thee, that meditating upon these mysteries of the Most Holy Rosary of the Blessed Virgin Mary, we may imitate what they contain and obtain what they promise, through the same Christ our Lord. Amen."
                        Text(
                            text = finalRosaryPrayerText,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
                            color = ParchmentLight
                        )
                        com.example.ui.components.LatinAudioPlayerController(
                            viewModel = viewModel,
                            prayerId = "rosary_close_final",
                            prayerText = finalRosaryPrayerText,
                            isLatin = isLatin,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // 3. St. Michael Prayer
                        Text(text = "3. Prayer to St. Michael the Archangel", style = MaterialTheme.typography.titleSmall, color = TextGoldHighlight)
                        Spacer(modifier = Modifier.height(4.dp))
                        val michaelText = if (isLatin) "Sancte Michael Archangele, defende nos in praelio; contra nequitiam et insidias diaboli esto praesidium. Imperet illi Deus, supplices deprecamur: tuque, Princeps militiae caelestis, Satanam aliosque spiritus malignos, qui ad perditionem animarum pervagantur in mundo, divina virtute, in infernum detrude. Amen."
                        else "St. Michael the Archangel, defend us in battle. Be our protection against the wickedness and snares of the devil. May God rebuke him, we humbly pray; and do thou, O Prince of the Heavenly Host, by the power of God, thrust into hell Satan and all the evil spirits who wander through the world seeking the ruin of souls. Amen."
                        Text(
                            text = michaelText,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
                            color = ParchmentLight
                        )
                        com.example.ui.components.LatinAudioPlayerController(
                            viewModel = viewModel,
                            prayerId = "rosary_close_michael",
                            prayerText = michaelText,
                            isLatin = isLatin,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // 4. Final Sign of the Cross
                        Text(text = "4. Final Sign of the Cross", style = MaterialTheme.typography.titleSmall, color = TextGoldHighlight)
                        Spacer(modifier = Modifier.height(4.dp))
                        val closeCrossText = if (isLatin) "☩ In nomine Patris, et Filii, et Spiritus Sancti. Amen." else "☩ In the name of the Father, and of the Son, and of the Holy Ghost. Amen."
                        Text(
                            text = closeCrossText,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                            color = GoldBright
                        )
                        com.example.ui.components.LatinAudioPlayerController(
                            viewModel = viewModel,
                            prayerId = "rosary_close_cross",
                            prayerText = if (isLatin) "In nomine Patris, et Filii, et Spiritus Sancti. Amen." else "In the name of the Father, and of the Son, and of the Holy Ghost. Amen.",
                            isLatin = isLatin,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { rosarySection = 0; currentMysteryIndex = 0; beadCounter = 0 },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Restart Rosary ☩", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

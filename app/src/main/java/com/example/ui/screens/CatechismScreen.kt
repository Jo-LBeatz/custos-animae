package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CatechismArticle
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CatechismScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val searchResults by viewModel.catechismSearchResults.collectAsState()
    val searchQuery by viewModel.catechismSearchQuery.collectAsState()
    val selectedSection by viewModel.selectedCatechismSection.collectAsState()

    val aiQuestionInput by viewModel.aiQuestionInput.collectAsState()
    val aiAnswerResult by viewModel.aiAnswerResult.collectAsState()
    val matchedArticlesForAi by viewModel.matchedArticlesForAi.collectAsState()
    val isAiSearching by viewModel.isAiSearching.collectAsState()

    var userQuestionText by remember { mutableStateOf("") }
    val uriHandler = LocalUriHandler.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val highlightQuery = remember(searchQuery, userQuestionText, aiQuestionInput) {
        when {
            searchQuery.isNotBlank() -> searchQuery
            aiQuestionInput.isNotBlank() -> aiQuestionInput
            userQuestionText.isNotBlank() -> userQuestionText
            else -> ""
        }
    }

    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 1 }
    }

    val categories = listOf(
        "ALL",
        "Faith & Creed",
        "Hope & Prayer",
        "Charity & Commandments",
        "Sacraments & Mass",
        "Grace, Virtues & Sins",
        "Church & Salvation"
    )

    val sampleQuestions = listOf(
        "Is Water Baptism necessary for salvation?",
        "What is Transubstantiation?",
        "Can anyone be saved outside the Catholic Church?",
        "When does the Pope speak infallibly?",
        "What is the Holy Sacrifice of the Mass?"
    )

    val darkBackground = DarkCathedralBackground
    val cardBackground = DarkCathedralSurface
    val goldBright = GoldBright
    val goldAccent = GoldSacred
    val crimsonAccent = CrimsonBurgundy
    val parchmentText = ParchmentLight
    val mutedText = TextMutedParchment

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(darkBackground)
            .testTag("catechism_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 600.dp)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp)
        ) {
            // HEADER & SOURCE BANNER
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Catechism Icon",
                            tint = goldBright,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Catholic Catechism",
                            color = goldBright,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    }

                    Text(
                        text = "100% Offline Dogmatic Catechism & AI Questions",
                        color = mutedText,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    // Source Link Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(crimsonAccent.copy(alpha = 0.3f))
                            .border(1.dp, crimsonAccent, RoundedCornerShape(20.dp))
                            .clickable {
                                uriHandler.openUri("https://vaticancatholic.com/catechism/")
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Source Link",
                            tint = goldBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "vaticancatholic.com/catechism/",
                            color = goldBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // OFFLINE AI QUESTION SEARCH CARD
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(goldAccent)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .testTag("ai_question_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "AI Search",
                                tint = goldBright,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ask Catechism Question (Offline AI)",
                                color = goldBright,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                        }

                        Text(
                            text = "Ask any question about Catholic faith, dogma, or sacraments. Answers are searched and cited directly from local Catechism storage.",
                            color = mutedText,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        // Input field
                        OutlinedTextField(
                            value = userQuestionText,
                            onValueChange = { userQuestionText = it },
                            placeholder = {
                                Text(
                                    "Type your question here (e.g. Is water baptism required?)....",
                                    color = mutedText.copy(alpha = 0.7f),
                                    fontSize = 13.sp
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = goldBright,
                                unfocusedBorderColor = goldAccent.copy(alpha = 0.5f),
                                focusedTextColor = parchmentText,
                                unfocusedTextColor = parchmentText,
                                cursorColor = goldBright,
                                focusedContainerColor = DarkCathedralSurfaceVariant,
                                unfocusedContainerColor = DarkCathedralSurfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("question_text_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Sample Question Chips
                        Text(
                            text = "Suggested Questions:",
                            color = goldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            sampleQuestions.forEach { sample ->
                                Surface(
                                    color = DarkCathedralSurfaceVariant,
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, goldAccent.copy(alpha = 0.4f)),
                                    modifier = Modifier.clickable {
                                        userQuestionText = sample
                                        viewModel.askCatechismQuestion(sample)
                                    }
                                ) {
                                    Text(
                                        text = sample,
                                        color = parchmentText,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Search Button
                        Button(
                            onClick = {
                                if (userQuestionText.isNotBlank()) {
                                    viewModel.askCatechismQuestion(userQuestionText)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = crimsonAccent,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = userQuestionText.isNotBlank() && !isAiSearching,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ask_question_button")
                        ) {
                            if (isAiSearching) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Searching Catechism Database...")
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Search Catechism Answer", fontWeight = FontWeight.Bold)
                            }
                        }

                        // DISPLAY AI ANSWER RESULT CARD IF AVAILABLE
                        if (aiAnswerResult != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkCathedralSurfaceVariant),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(goldBright)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ai_answer_result_card")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Dogmatic Answer Summary",
                                            color = goldBright,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )

                                        IconButton(
                                            onClick = { viewModel.setAiQuestionInput("") },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Clear Answer",
                                                tint = mutedText
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = buildHighlightedText(
                                            text = aiAnswerResult ?: "",
                                            query = highlightQuery,
                                            highlightColor = goldBright,
                                            highlightBgColor = goldAccent.copy(alpha = 0.35f)
                                        ),
                                        color = parchmentText,
                                        fontSize = 13.sp,
                                        lineHeight = 19.sp,
                                        fontFamily = FontFamily.Serif
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // CATEGORY FILTER TABS
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Browse Catechism Articles by Section",
                        color = goldBright,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("catechism_category_tabs")
                    ) {
                        items(categories) { category ->
                            val isSelected = selectedSection.equals(category, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setSelectedCatechismSection(category) },
                                label = {
                                    Text(
                                        text = category,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSecondary else parchmentText,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = goldBright,
                                    containerColor = DarkCathedralSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = DarkCathedralCardBorder,
                                    selectedBorderColor = goldBright
                                )
                            )
                        }
                    }

                    // KEYWORD SEARCH INPUT FIELD
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setCatechismSearchQuery(it) },
                        placeholder = {
                            Text(
                                "Filter articles by title or text...",
                                color = mutedText.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = goldAccent
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.setCatechismSearchQuery("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = mutedText
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = goldBright,
                            unfocusedBorderColor = goldAccent.copy(alpha = 0.4f),
                            focusedTextColor = parchmentText,
                            unfocusedTextColor = parchmentText,
                            cursorColor = goldBright,
                            focusedContainerColor = cardBackground,
                            unfocusedContainerColor = cardBackground
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .testTag("catechism_keyword_filter")
                    )
                }
            }

            // ARTICLE COUNT SUMMARY
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Articles Found: ${searchResults.size}",
                        color = goldAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (highlightQuery.isNotBlank()) {
                        Surface(
                            color = GoldDark.copy(alpha = 0.25f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, goldBright.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Highlighted",
                                    tint = goldBright,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Matched Terms Highlighted",
                                    color = goldBright,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // CATECHISM ARTICLE LIST
            if (searchResults.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Catechism articles found matching '$searchQuery'. Try adjusting your search query or section filter.",
                            color = mutedText,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(
                    items = searchResults,
                    key = { it.id }
                ) { article ->
                    CatechismArticleCard(
                        article = article,
                        highlightQuery = highlightQuery,
                        onToggleBookmark = { viewModel.toggleCatechismBookmark(article) },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }

        // SCROLL TO TOP FAB
        AnimatedVisibility(
            visible = showScrollToTop,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                containerColor = crimsonAccent,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("scroll_to_top_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Scroll to top"
                )
            }
        }
    }
}

@Composable
fun CatechismArticleCard(
    article: CatechismArticle,
    highlightQuery: String = "",
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }
    val cardBackground = DarkCathedralSurface
    val goldBright = GoldBright
    val goldAccent = GoldSacred
    val crimsonTag = CrimsonBurgundy
    val parchmentText = ParchmentLight
    val mutedText = TextMutedParchment

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(goldAccent.copy(alpha = 0.5f))),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .testTag("catechism_article_${article.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Section Tag & Article Number
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = crimsonTag,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = article.articleNumber,
                            color = goldBright,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = article.section,
                        color = goldAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Bookmark Icon Button
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (article.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (article.isBookmarked) goldBright else mutedText
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Article Title
            Text(
                text = buildHighlightedText(
                    text = article.title,
                    query = highlightQuery,
                    highlightColor = goldBright,
                    highlightBgColor = goldAccent.copy(alpha = 0.35f)
                ),
                color = goldBright,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Question
            Text(
                text = buildHighlightedText(
                    text = "Q: ${article.question}",
                    query = highlightQuery,
                    highlightColor = goldBright,
                    highlightBgColor = goldAccent.copy(alpha = 0.35f)
                ),
                color = parchmentText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Serif
            )

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                // Answer Text
                Text(
                    text = buildHighlightedText(
                        text = article.answerText,
                        query = highlightQuery,
                        highlightColor = goldBright,
                        highlightBgColor = goldAccent.copy(alpha = 0.35f)
                    ),
                    color = parchmentText,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    fontFamily = FontFamily.Serif
                )

                if (article.citations.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = DarkCathedralSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Citation",
                                tint = goldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = buildHighlightedText(
                                    text = "Ref: ${article.citations}",
                                    query = highlightQuery,
                                    highlightColor = goldBright,
                                    highlightBgColor = goldAccent.copy(alpha = 0.35f)
                                ),
                                color = goldAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Builds an AnnotatedString highlighting terms from [query] found within [text].
 */
fun buildHighlightedText(
    text: String,
    query: String,
    highlightColor: Color = GoldBright,
    highlightBgColor: Color = GoldSacred.copy(alpha = 0.35f)
): AnnotatedString {
    if (query.isBlank() || text.isBlank()) {
        return AnnotatedString(text)
    }

    val terms = query.trim()
        .split(Regex("[^a-zA-Z0-9]+"))
        .filter { it.length >= 2 }
        .distinct()

    if (terms.isEmpty()) {
        return AnnotatedString(text)
    }

    val builder = AnnotatedString.Builder(text)
    val lowerText = text.lowercase()

    for (term in terms) {
        val lowerTerm = term.lowercase()
        var startIndex = 0
        while (startIndex < text.length) {
            val index = lowerText.indexOf(lowerTerm, startIndex)
            if (index == -1) break
            builder.addStyle(
                style = SpanStyle(
                    color = highlightColor,
                    background = highlightBgColor,
                    fontWeight = FontWeight.Bold
                ),
                start = index,
                end = index + term.length
            )
            startIndex = index + term.length
        }
    }

    return builder.toAnnotatedString()
}

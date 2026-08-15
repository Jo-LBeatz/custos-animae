package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import com.example.ui.components.ScrollableClockField
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun OnboardingGenderScreen(
    onSelectGender: (String, String, String, String, Boolean) -> Unit
) {
    var selectedGender by remember { mutableStateOf<String?>(null) }
    var showWelcomeDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }

    var morningTime by remember { mutableStateOf("07:00") }
    var eveningTime by remember { mutableStateOf("21:00") }
    var saintTime by remember { mutableStateOf("07:30") }
    var notificationsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCathedralBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .widthIn(max = 640.dp)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Hero Image
        Image(
            painter = painterResource(id = R.drawable.img_gothic_banner),
            contentDescription = "Gothic Cathedral Interior",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.5.dp, GoldSacred, RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "CUSTOS ANIMAE",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            ),
            color = GoldBright,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Guardian of the Soul • Traditional Catholic Assistant",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMutedParchment,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Powered & Developed by RegulaVitae",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = GoldBright,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Welcome to your personal journey of spiritual discipline and true faith. To tailor your suggested rule of life, please select your path:",
            style = MaterialTheme.typography.bodyMedium,
            color = ParchmentLight,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Male Option
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("select_male_card")
                .clip(RoundedCornerShape(16.dp))
                .border(
                    BorderStroke(
                        if (selectedGender == "MALE") 2.dp else 1.dp,
                        if (selectedGender == "MALE") GoldBright else DarkCathedralCardBorder
                    ),
                    RoundedCornerShape(16.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedGender == "MALE") DarkCathedralSurfaceVariant else DarkCathedralSurface
            ),
            onClick = { selectedGender = "MALE" }
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Male,
                        contentDescription = null,
                        tint = GoldBright,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Rule of St. Benedict (Male)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldBright
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Emphasizes Prayer (Ora), Humility, Holy Order, Sacred Labor (Labora), Silence, Fasting, and the continual search for God.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedParchment
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Female Option
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("select_female_card")
                .clip(RoundedCornerShape(16.dp))
                .border(
                    BorderStroke(
                        if (selectedGender == "FEMALE") 2.dp else 1.dp,
                        if (selectedGender == "FEMALE") GoldBright else DarkCathedralCardBorder
                    ),
                    RoundedCornerShape(16.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedGender == "FEMALE") DarkCathedralSurfaceVariant else DarkCathedralSurface
            ),
            onClick = { selectedGender = "FEMALE" }
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Female,
                        contentDescription = null,
                        tint = GoldBright,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Rule of Donatus (Female)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldBright
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Emphasizes Prayer, Deep Humility, Hidden Charity, Devotion to Our Lady, Sacred Discipline, and Growth in Holiness.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedParchment
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                if (selectedGender != null) {
                    showWelcomeDialog = true
                }
            },
            enabled = selectedGender != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("confirm_onboarding_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = GoldSacred,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Inaugurate Spiritual Rule",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }

    // 1. Welcome Dialog based on Man or Woman choice
    if (showWelcomeDialog) {
        val welcomeTitle = "Welcome"
        val welcomeMessage = "Use CUSTOS ANIMAE to help you become spiritually disciplined into the traditional Catholic faith. If not Catholic, please convert to the Catholic faith. Visit www.vaticancatholic.com for more information on how to convert to the true faith"

        AlertDialog(
            onDismissRequest = {},
            containerColor = DarkCathedralSurface,
            title = {
                Text(
                    text = welcomeTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldBright
                )
            },
            text = {
                Text(
                    text = welcomeMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ParchmentLight
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showWelcomeDialog = false
                        showNotificationDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black)
                ) {
                    Text("Continue to Notification Setup", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 2. Notification Queries Pop-up Box
    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = DarkCathedralSurface,
            title = {
                Text(
                    text = "Notification Queries & Schedule",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldBright
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Please configure your preferred times for daily monastic notifications and reminders:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedParchment
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ScrollableClockField(
                        value = saintTime,
                        onValueChange = { saintTime = it },
                        label = "Saint of the Day Time (HH:mm)",
                        dialogTitle = "Select Saint Notification Time"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ScrollableClockField(
                        value = morningTime,
                        onValueChange = { morningTime = it },
                        label = "Morning Prayer Time (HH:mm)",
                        dialogTitle = "Select Morning Prayer Time"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ScrollableClockField(
                        value = eveningTime,
                        onValueChange = { eveningTime = it },
                        label = "Evening Prayer / Examen Time (HH:mm)",
                        dialogTitle = "Select Evening Prayer Time"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showNotificationDialog = false
                        selectedGender?.let { gender ->
                            onSelectGender(gender, morningTime, eveningTime, saintTime, notificationsEnabled)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black)
                ) {
                    Text("Save & Begin Life of Faith ☩", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

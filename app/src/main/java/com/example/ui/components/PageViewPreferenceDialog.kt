package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PageViewMode
import com.example.ui.theme.*

@Composable
fun PageViewPreferenceDialog(
    currentMode: PageViewMode,
    onModeSelected: (PageViewMode) -> Unit,
    onDismiss: (Boolean) -> Unit
) {
    var selectedMode by remember { mutableStateOf(currentMode) }
    var dontAskOnLaunch by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss(dontAskOnLaunch) },
        containerColor = DarkCathedralSurface,
        titleContentColor = GoldBright,
        textContentColor = ParchmentLight,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = GoldBright,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Page Viewing Preference",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    color = GoldBright
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Would you like pages to be viewed with pure content visibility (without top and bottom elements) or with navigation elements visible across all pages?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMutedParchment
                )

                // Option 1: Pure Content (Without Elements)
                Surface(
                    onClick = { selectedMode = PageViewMode.PURE_CONTENT },
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedMode == PageViewMode.PURE_CONTENT) GoldSacred.copy(alpha = 0.25f) else DarkCathedralBackground,
                    border = BorderStroke(1.dp, if (selectedMode == PageViewMode.PURE_CONTENT) GoldBright else GoldDark.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RadioButton(
                            selected = (selectedMode == PageViewMode.PURE_CONTENT),
                            onClick = { selectedMode = PageViewMode.PURE_CONTENT },
                            colors = RadioButtonDefaults.colors(selectedColor = GoldBright, unselectedColor = TextMutedParchment)
                        )
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = if (selectedMode == PageViewMode.PURE_CONTENT) GoldBright else TextMutedParchment,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Pure Visibility (Without Elements)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = GoldBright
                            )
                            Text(
                                text = "Hides top headers and bottom navigation bar completely for 100% full, unobstructed content visibility on ALL pages.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedParchment
                            )
                        }
                    }
                }

                // Option 2: Standard With Bars
                Surface(
                    onClick = { selectedMode = PageViewMode.STANDARD_WITH_BARS },
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedMode == PageViewMode.STANDARD_WITH_BARS) GoldSacred.copy(alpha = 0.25f) else DarkCathedralBackground,
                    border = BorderStroke(1.dp, if (selectedMode == PageViewMode.STANDARD_WITH_BARS) GoldBright else GoldDark.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RadioButton(
                            selected = (selectedMode == PageViewMode.STANDARD_WITH_BARS),
                            onClick = { selectedMode = PageViewMode.STANDARD_WITH_BARS },
                            colors = RadioButtonDefaults.colors(selectedColor = GoldBright, unselectedColor = TextMutedParchment)
                        )
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = if (selectedMode == PageViewMode.STANDARD_WITH_BARS) GoldBright else TextMutedParchment,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Standard View (With Elements)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = GoldBright
                            )
                            Text(
                                text = "Keeps top header titles and bottom navigation bar visible on all pages.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedParchment
                            )
                        }
                    }
                }

                // Option 3: Dynamic Auto-Hide
                Surface(
                    onClick = { selectedMode = PageViewMode.DYNAMIC_AUTO_HIDE },
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedMode == PageViewMode.DYNAMIC_AUTO_HIDE) GoldSacred.copy(alpha = 0.25f) else DarkCathedralBackground,
                    border = BorderStroke(1.dp, if (selectedMode == PageViewMode.DYNAMIC_AUTO_HIDE) GoldBright else GoldDark.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RadioButton(
                            selected = (selectedMode == PageViewMode.DYNAMIC_AUTO_HIDE),
                            onClick = { selectedMode = PageViewMode.DYNAMIC_AUTO_HIDE },
                            colors = RadioButtonDefaults.colors(selectedColor = GoldBright, unselectedColor = TextMutedParchment)
                        )
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = if (selectedMode == PageViewMode.DYNAMIC_AUTO_HIDE) GoldBright else TextMutedParchment,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Dynamic Auto-Hide",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = GoldBright
                            )
                            Text(
                                text = "Fades out top & bottom bars when scrolling down, restores them when scrolling up or tapping.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedParchment
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Checkbox(
                        checked = dontAskOnLaunch,
                        onCheckedChange = { dontAskOnLaunch = it },
                        colors = CheckboxDefaults.colors(checkedColor = GoldBright, checkmarkColor = DarkCathedralBackground)
                    )
                    Text(
                        text = "Remember my choice (do not ask on app launch)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedParchment
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onModeSelected(selectedMode)
                    onDismiss(dontAskOnLaunch)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonBurgundy, contentColor = StaticGoldBright)
            ) {
                Text("Apply Preference To All Pages", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss(dontAskOnLaunch) },
                colors = ButtonDefaults.textButtonColors(contentColor = TextMutedParchment)
            ) {
                Text("Dismiss")
            }
        }
    )
}

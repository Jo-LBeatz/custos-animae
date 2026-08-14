package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.prayers.RosaryMystery
import com.example.data.prayers.TraditionalPrayerLibrary
import com.example.ui.MainViewModel
import com.example.ui.components.IlluminatedDivider
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FuturisticRosaryScreen(
    isLatinInitial: Boolean = false,
    onExit: () -> Unit,
    viewModel: MainViewModel
) {
    // Hide top/bottom global bars while in full-screen Rosary
    LaunchedEffect(Unit) {
        viewModel.setBottomNavVisible(false)
        viewModel.setTopHeaderVisible(false)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setBottomNavVisible(true)
            viewModel.setTopHeaderVisible(true)
            viewModel.stopLatinAudio()
        }
    }

    var isLatin by remember { mutableStateOf(isLatinInitial) }
    var activeTab by remember { mutableStateOf(0) } // 0: Opening, 1: Decades, 2: Concluding

    // Auto mystery calculation based on day of week
    val cal = Calendar.getInstance()
    val initialSetIndex = when (cal.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY, Calendar.WEDNESDAY, Calendar.SATURDAY -> 2 // Glorious
        Calendar.MONDAY, Calendar.THURSDAY -> 0 // Joyful
        Calendar.TUESDAY, Calendar.FRIDAY -> 1 // Sorrowful
        else -> 0
    }

    var selectedSetIndex by remember { mutableStateOf(initialSetIndex) }
    var currentMysteryIndex by remember { mutableStateOf(0) } // 0-4
    var currentStepInDecade by remember { mutableStateOf(0) } // 0: Pater Noster, 1..10: Ave Maria, 11: Gloria Patri, 12: Fatima Prayer
    var currentOpeningStep by remember { mutableStateOf(0) } // 0-5 (opening prayers)
    var currentConcludingStep by remember { mutableStateOf(0) } // 0-3 (concluding prayers)
    var isAutoPlay by remember { mutableStateOf(false) }

    val currentSet = TraditionalPrayerLibrary.rosarySets.getOrElse(selectedSetIndex) { TraditionalPrayerLibrary.rosarySets[0] }
    val currentMystery = currentSet.mysteries.getOrElse(currentMysteryIndex) { currentSet.mysteries[0] }

    // Listen to when Latin audio completes and automatically advance to next prayer
    LaunchedEffect(isAutoPlay) {
        viewModel.onLatinAudioCompleted.collect { completedId ->
            if (isAutoPlay) {
                // Determine the expected prayerId of the currently active step
                val currentExpectedId = when (activeTab) {
                    0 -> when (currentOpeningStep) {
                        0 -> "futuristic_open_cross"
                        1 -> "futuristic_open_creed"
                        2 -> "futuristic_open_pater"
                        3 -> "futuristic_open_ave"
                        4 -> "futuristic_open_gloria"
                        else -> "futuristic_open_fatima"
                    }
                    1 -> "futuristic_bead_${currentStepInDecade}"
                    else -> when (currentConcludingStep) {
                        0 -> "futuristic_close_salve"
                        1 -> "futuristic_close_versicle"
                        2 -> "futuristic_close_concluding"
                        else -> "futuristic_close_michael"
                    }
                }
                
                if (completedId == currentExpectedId) {
                    // Audio finished, advance to the next step
                    if (activeTab == 0) {
                        if (currentOpeningStep < 5) {
                            currentOpeningStep++
                        } else {
                            activeTab = 1
                            currentMysteryIndex = 0
                            currentStepInDecade = 0
                        }
                    } else if (activeTab == 1) {
                        if (currentStepInDecade < 12) {
                            currentStepInDecade++
                        } else if (currentMysteryIndex < 4) {
                            currentMysteryIndex++
                            currentStepInDecade = 0
                        } else {
                            activeTab = 2
                            currentConcludingStep = 0
                        }
                    } else if (activeTab == 2) {
                        if (currentConcludingStep < 3) {
                            currentConcludingStep++
                        } else {
                            isAutoPlay = false
                        }
                    }
                }
            }
        }
    }

    // Trigger audio playback for the active step if autoplay is active
    LaunchedEffect(isAutoPlay, activeTab, currentOpeningStep, currentMysteryIndex, currentStepInDecade, currentConcludingStep) {
        if (isAutoPlay) {
            // Get the text to speak based on the current state
            val (prayerId, textToSpeak) = when (activeTab) {
                0 -> {
                    val id = when (currentOpeningStep) {
                        0 -> "futuristic_open_cross"
                        1 -> "futuristic_open_creed"
                        2 -> "futuristic_open_pater"
                        3 -> "futuristic_open_ave"
                        4 -> "futuristic_open_gloria"
                        else -> "futuristic_open_fatima"
                    }
                    val text = when (currentOpeningStep) {
                        0 -> if (isLatin) "In nomine Patris, et Filii, et Spiritus Sancti. Amen."
                             else "In the name of the Father, and of the Son, and of the Holy Ghost. Amen."
                        1 -> if (isLatin) """Credo in Deum Patrem omnipotentem, Creatorem caeli et terrae. Et in Iesum Christum, Filium eius unicum, Dominum nostrum: qui conceptus est de Spiritu Sancto, natus ex Maria Virgine, passus sub Pontio Pilato, crucifixus, mortuus, et sepultus: descendit ad inferos; tertia die resurrexit a mortuis; ascendit ad caelos; sedet ad dexteram Dei Patris omnipotentis: inde venturus est iudicare vivos et mortuos. Credo in Spiritum Sanctum, sanctam Ecclesiam catholicam, Sanctorum communionem, remissionem peccatorum, carnis resurrectionem, vitam aeternam. Amen."""
                             else """I believe in God, the Father Almighty, Creator of heaven and earth. And in Jesus Christ, His only Son, our Lord: who was conceived by the Holy Ghost, born of the Virgin Mary, suffered under Pontius Pilate, was crucified, died, and was buried: He descended into hell; the third day He rose again from the dead; He ascended into heaven, and sitteth on the right hand of God the Father Almighty; from thence He shall come to judge the living and the dead. I believe in the Holy Ghost, the holy Catholic Church, the communion of Saints, the forgiveness of sins, the resurrection of the body, and life everlasting. Amen."""
                        2 -> if (isLatin) "Pater noster, qui es in caelis: sanctificetur nomen tuum: adveniat regnum tuum: fiat voluntas tua, sicut in caelo, et in terra. Panem nostrum cotidianum da nobis hodie: et dimitte nobis debita nostra, sicut et nos dimittimus debitoribus nostris: et ne nos inducas in tentationem: sed libera nos a malo. Amen."
                             else "Our Father, who art in heaven, hallowed be Thy name; Thy kingdom come; Thy will be done on earth as it is in heaven. Give us this day our daily bread; and forgive us our trespasses as we forgive those who trespass against us; and lead us not into temptation, but deliver us from evil. Amen."
                        3 -> if (isLatin) "Ave Maria, gratia plena, Dominus tecum. Benedicta tu in mulieribus, et benedictus fructus ventris tui, Iesus. Sancta Maria, Mater Dei, ora pro nobis peccatoribus, nunc et in hora mortis nostrae. Amen."
                             else "Hail Mary, full of grace, the Lord is with thee. Blessed art thou among women, and blessed is the fruit of thy womb, Jesus. Holy Mary, Mother of God, pray for us sinners, now and at the hour of our death. Amen."
                        4 -> if (isLatin) "Gloria Patri, et Filio, et Spiritui Sancto. Sicut erat in principio, et nunc, et semper, et in saecula saeculorum. Amen."
                             else "Glory be to the Father, and to the Son, and to the Holy Ghost. As it was in the beginning, is now, and ever shall be, world without end. Amen."
                        else -> if (isLatin) "O mi Iesu, dimitte nobis debita nostra, libera nos ab igne inferni, duc in caelum omnes, praesertim eos, qui maxima indigent misericordia tua."
                             else "O My Jesus, forgive us our sins, save us from the fires of hell, lead all souls to Heaven, especially those most in need of Thy mercy."
                    }
                    Pair(id, text)
                }
                1 -> {
                    val id = "futuristic_bead_${currentStepInDecade}"
                    val text = when (currentStepInDecade) {
                        0 -> if (isLatin) "Pater noster, qui es in caelis: sanctificetur nomen tuum: adveniat regnum tuum: fiat voluntas tua, sicut in caelo, et in terra. Panem nostrum cotidianum da nobis hodie: et dimitte nobis debita nostra, sicut et nos dimittimus debitoribus nostris: et ne nos inducas in tentationem: sed libera nos a malo. Amen."
                             else "Our Father, who art in heaven, hallowed be Thy name; Thy kingdom come; Thy will be done on earth as it is in heaven. Give us this day our daily bread; and forgive us our trespasses as we forgive those who trespass against us; and lead us not into temptation, but deliver us from evil. Amen."
                        in 1..10 -> if (isLatin) "Ave Maria, gratia plena, Dominus tecum. Benedicta tu in mulieribus, et benedictus fructus ventris tui, Iesus. Sancta Maria, Mater Dei, ora pro nobis peccatoribus, nunc et in hora mortis nostrae. Amen."
                                    else "Hail Mary, full of grace, the Lord is with thee. Blessed art thou among women, and blessed is the fruit of thy womb, Jesus. Holy Mary, Mother of God, pray for us sinners, now and at the hour of our death. Amen."
                        11 -> if (isLatin) "Gloria Patri, et Filio, et Spiritui Sancto. Sicut erat in principio, et nunc, et semper, et in saecula saeculorum. Amen."
                              else "Glory be to the Father, and to the Son, and to the Holy Ghost. As it was in the beginning, is now, and ever shall be, world without end. Amen."
                        else -> if (isLatin) "O mi Iesu, dimitte nobis debita nostra, libera nos ab igne inferni, duc in caelum omnes, praesertim eos, qui maxima indigent misericordia tua."
                             else "O My Jesus, forgive us our sins, save us from the fires of hell, lead all souls to Heaven, especially those most in need of Thy mercy."
                    }
                    Pair(id, text)
                }
                else -> {
                    val id = when (currentConcludingStep) {
                        0 -> "futuristic_close_salve"
                        1 -> "futuristic_close_versicle"
                        2 -> "futuristic_close_concluding"
                        else -> "futuristic_close_michael"
                    }
                    val text = when (currentConcludingStep) {
                        0 -> if (isLatin) """Salve, Regina, Mater misericordiae, vita, dulcedo, et spes nostra, salve. Ad te clamamus exsules filii Hevae, ad te suspiramus gementes et flentes in hac lacrimarum valle. Eia ergo, advocata nostra, illos tuos misericordes oculos ad nos converte; et Iesum, benedictum fructum ventris tui, nobis post hoc exsilium ostende. O clemens, o pia, o dulcis Virgo Maria."""
                             else """Hail, Holy Queen, Mother of Mercy, our life, our sweetness, and our hope. To thee do we cry, poor banished children of Eve; to thee do we send up our sighs, mourning and weeping in this valley of tears. Turn then, most gracious advocate, thine eyes of mercy toward us; and after this our exile, show unto us the blessed fruit of thy womb, Jesus. O clement, O loving, O sweet Virgin Mary."""
                        1 -> if (isLatin) """Ora pro nobis, Sancta Dei Genetrix. Ut digni efficiamur promissionibus Christi."""
                             else """Pray for us, O Holy Mother of God. That we may be made worthy of the promises of Christ."""
                        2 -> if (isLatin) """Oremus. Deus, cuius Unigenitus per vitam, mortem et resurrectionem suam nobis salutis aeternae praemia comparavit: concede, quaesumus; ut haec mysteria sanctissimo Beatae Mariae Virginis Rosario recolentes, et imitemur quod continent, et quod promittunt assequamur. Per eundem Christum Dominum nostrum. Amen."""
                             else """O God, whose only-begotten Son, by His life, death, and resurrection, has purchased for us the rewards of eternal life, grant, we beseech Thee, that meditating upon these mysteries of the Most Holy Rosary of the Blessed Virgin Mary, we may imitate what they contain and obtain what they promise, through the same Christ our Lord. Amen."""
                        else -> if (isLatin) """Sancte Michael Archangele, defende nos in praelio; contra nequitiam et insidias diaboli esto praesidium. Imperet illi Deus, supplices deprecamur: tuque, Princeps militiae caelestis, Satanam aliosque spiritus malignos, qui ad perditionem animarum pervagantur in mundo, divina virtute in infernum detrude. Amen. In nomine Patris, et Filii, et Spiritus Sancti. Amen."""
                             else """St. Michael the Archangel, defend us in battle; be our protection against the wickedness and snares of the devil. May God rebuke him, we humbly pray; and do thou, O Prince of the heavenly host, by the power of God, thrust into hell Satan and all the evil spirits who prowl about the world seeking the ruin of souls. Amen. In the name of the Father, and of the Son, and of the Holy Ghost. Amen."""
                    }
                    Pair(id, text)
                }
            }

            val cleanText = textToSpeak.replace("☩", "").trim()
            viewModel.playLatinAudio(
                id = prayerId,
                text = cleanText,
                speed = viewModel.latinAudioState.value.speed,
                useEcclesiastical = viewModel.latinAudioState.value.useEcclesiastical,
                isLatin = isLatin
            )
        } else {
            // Stop TTS if autoplay is toggled off
            viewModel.stopLatinAudio()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "futuristic_bg")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )
    val rotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate_angle"
    )

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
    ) {
        // Advanced Cyber-Gothic Particle Background Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Cosmic Radial Gradient
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E1B4B).copy(alpha = 0.5f * pulseGlow),
                        Color(0xFF0F172A).copy(alpha = 0.8f),
                        Color(0xFF070B14)
                    ),
                    center = Offset(w / 2f, h / 3f),
                    radius = w * 1.2f
                )
            )

            // Luminous Sacred Halo Rings
            val center = Offset(w / 2f, h * 0.28f)
            val ringRadius = w * 0.35f
            drawCircle(
                color = GoldBright.copy(alpha = 0.12f * pulseGlow),
                center = center,
                radius = ringRadius,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = CrimsonBurgundy.copy(alpha = 0.2f * pulseGlow),
                center = center,
                radius = ringRadius * 1.25f,
                style = Stroke(width = 1.dp.toPx())
            )

            // Floating Luminous Particles
            val particleCount = 18
            for (i in 0 until particleCount) {
                val angle = Math.toRadians((i * (360.0 / particleCount) + rotateAngle).toDouble())
                val radiusOffset = ringRadius * (0.8f + (i % 3) * 0.25f)
                val px = (center.x + radiusOffset * cos(angle)).toFloat()
                val py = (center.y + radiusOffset * sin(angle)).toFloat()
                val pAlpha = (0.2f + 0.6f * ((i % 5) / 5f)) * pulseGlow
                drawCircle(
                    color = if (i % 2 == 0) GoldBright.copy(alpha = pAlpha) else Color(0xFF38BDF8).copy(alpha = pAlpha),
                    center = Offset(px, py),
                    radius = (2.dp + (i % 3).dp).toPx()
                )
            }
        }

        // Main Screen Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .widthIn(max = 640.dp)
        ) {
            // TOP BAR WITH LEFT EXIT BUTTON
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // TOP LEFT EXIT BUTTON (MANDATORY REQUIREMENT)
                Button(
                    onClick = onExit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkCathedralSurface.copy(alpha = 0.9f),
                        contentColor = GoldBright
                    ),
                    border = BorderStroke(1.5.dp, GoldBright),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("exit_rosary_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Exit Rosary",
                        tint = GoldBright,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Exit Rosary",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        color = GoldBright
                    )
                }

                // Center Title & Active Mystery Set Badge
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "HOLY ROSARY",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = GoldBright
                    )
                    Text(
                        text = if (isLatin) "ROSARIUM SANCTISSIMUM" else currentSet.category,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = TextGoldHighlight
                    )
                }

                // Top Right Controls: Language & AutoPlay
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Language Toggle
                    Surface(
                        onClick = { isLatin = !isLatin },
                        shape = CircleShape,
                        color = DarkCathedralSurface.copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, GoldSacred),
                        modifier = Modifier.testTag("rosary_language_toggle")
                    ) {
                        Text(
                            text = if (isLatin) "LAT" else "ENG",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = GoldBright,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    // Auto-Play Toggle
                    IconButton(
                        onClick = { isAutoPlay = !isAutoPlay },
                        modifier = Modifier
                            .background(
                                if (isAutoPlay) CrimsonBurgundy else DarkCathedralSurface.copy(alpha = 0.85f),
                                CircleShape
                            )
                            .border(1.dp, if (isAutoPlay) GoldBright else DarkCathedralCardBorder, CircleShape)
                            .testTag("rosary_auto_play_btn")
                    ) {
                        Icon(
                            imageVector = if (isAutoPlay) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Auto Play",
                            tint = GoldBright,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Section Tabs: Opening -> Decades -> Concluding
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val sections = listOf("1. Opening Prayers", "2. Decades (1-5)", "3. Concluding")
                sections.forEachIndexed { index, label ->
                    val isSelected = activeTab == index
                    Surface(
                        onClick = { activeTab = index },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) CrimsonBurgundy else DarkCathedralSurface.copy(alpha = 0.7f),
                        border = BorderStroke(1.dp, if (isSelected) GoldBright else DarkCathedralCardBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center
                            ),
                            color = if (isSelected) GoldBright else TextMutedParchment,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Scrollable Content Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(bottom = 20.dp)
                ) {
                    when (activeTab) {
                        0 -> OpeningPrayersFullView(
                            isLatin = isLatin,
                            viewModel = viewModel,
                            currentOpeningStep = currentOpeningStep,
                            onStartDecades = {
                                activeTab = 1
                                currentMysteryIndex = 0
                                currentStepInDecade = 0
                            }
                        )

                        1 -> DecadesInteractiveView(
                            isLatin = isLatin,
                            viewModel = viewModel,
                            selectedSetIndex = selectedSetIndex,
                            onSetSelected = {
                                selectedSetIndex = it
                                currentMysteryIndex = 0
                                currentStepInDecade = 0
                            },
                            currentMysteryIndex = currentMysteryIndex,
                            onMysterySelected = {
                                currentMysteryIndex = it
                                currentStepInDecade = 0
                            },
                            currentStepInDecade = currentStepInDecade,
                            onStepSelected = { currentStepInDecade = it },
                            pulseGlow = pulseGlow,
                            rotateAngle = rotateAngle
                        )

                        2 -> ConcludingPrayersFullView(
                            isLatin = isLatin,
                            viewModel = viewModel,
                            currentConcludingStep = currentConcludingStep
                        )
                    }
                }
            }

            // BOTTOM NAVIGATION CONTROL BAR (For step-by-step Rosary advancement)
            Surface(
                color = DarkCathedralSurface.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(GoldBright, CrimsonBurgundy, GoldBright))),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Button
                    OutlinedButton(
                        onClick = {
                            if (activeTab == 0) {
                                if (currentOpeningStep > 0) {
                                    currentOpeningStep--
                                }
                            } else if (activeTab == 1) {
                                if (currentStepInDecade > 0) {
                                    currentStepInDecade--
                                } else if (currentMysteryIndex > 0) {
                                    currentMysteryIndex--
                                    currentStepInDecade = 12
                                } else {
                                    activeTab = 0
                                    currentOpeningStep = 5
                                }
                            } else if (activeTab == 2) {
                                if (currentConcludingStep > 0) {
                                    currentConcludingStep--
                                } else {
                                    activeTab = 1
                                    currentMysteryIndex = 4
                                    currentStepInDecade = 12
                                }
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldBright),
                        border = BorderStroke(1.dp, GoldSacred),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("rosary_prev_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = "Previous")
                        Text("Prev", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Active Step Label
                    val stepLabel = when (activeTab) {
                        0 -> when (currentOpeningStep) {
                            0 -> "Sign of the Cross"
                            1 -> "The Apostles' Creed"
                            2 -> "Pater Noster"
                            3 -> "3 Hail Marys"
                            4 -> "Glory Be"
                            else -> "Fatima Prayer"
                        }
                        1 -> when (currentStepInDecade) {
                            0 -> "Pater Noster (Our Father)"
                            in 1..10 -> "Ave Maria $currentStepInDecade / 10"
                            11 -> "Gloria Patri (Glory Be)"
                            else -> "Fatima Prayer"
                        }
                        else -> when (currentConcludingStep) {
                            0 -> "Salve Regina"
                            1 -> "Versicle & Response"
                            2 -> "Concluding Prayer"
                            else -> "St. Michael Prayer"
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Decade ${currentMysteryIndex + 1} of 5",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedParchment
                        )
                        Text(
                            text = stepLabel,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldBright
                        )
                    }

                    // Next Button
                    Button(
                        onClick = {
                            if (activeTab == 0) {
                                if (currentOpeningStep < 5) {
                                    currentOpeningStep++
                                } else {
                                    activeTab = 1
                                    currentMysteryIndex = 0
                                    currentStepInDecade = 0
                                }
                            } else if (activeTab == 1) {
                                if (currentStepInDecade < 12) {
                                    currentStepInDecade++
                                } else if (currentMysteryIndex < 4) {
                                    currentMysteryIndex++
                                    currentStepInDecade = 0
                                } else {
                                    activeTab = 2
                                    currentConcludingStep = 0
                                }
                            } else if (activeTab == 2) {
                                if (currentConcludingStep < 3) {
                                    currentConcludingStep++
                                } else {
                                    isAutoPlay = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("rosary_next_btn")
                    ) {
                        Text("Next ›", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 1. OPENING PRAYERS FULL VIEW (NON-TRUNCATED)
// -----------------------------------------------------------------------------
@Composable
private fun OpeningPrayersFullView(
    isLatin: Boolean,
    viewModel: MainViewModel,
    currentOpeningStep: Int,
    onStartDecades: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface.copy(alpha = 0.9f)),
        border = BorderStroke(1.5.dp, GoldBright)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = if (isLatin) "INITIUM ROSARII • OPENING PRAYERS" else "Opening Prayers of the Holy Rosary",
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                color = GoldBright
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Sign of the Cross
            PrayerSectionCard(
                number = "1",
                title = "Sign of the Cross (Signum Crucis)",
                text = if (isLatin) "☩ In nomine Patris, et Filii, et Spiritus Sancti. Amen."
                else "☩ In the name of the Father, and of the Son, and of the Holy Ghost. Amen.",
                viewModel = viewModel,
                prayerId = "futuristic_open_cross",
                isActive = (currentOpeningStep == 0)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Apostles' Creed
            PrayerSectionCard(
                number = "2",
                title = "The Apostles' Creed (Symbolum Apostolorum)",
                text = if (isLatin) """Credo in Deum Patrem omnipotentem, Creatorem caeli et terrae. Et in Iesum Christum, Filium eius unicum, Dominum nostrum: qui conceptus est de Spiritu Sancto, natus ex Maria Virgine, passus sub Pontio Pilato, crucifixus, mortuus, et sepultus: descendit ad inferos; tertia die resurrexit a mortuis; ascendit ad caelos; sedet ad dexteram Dei Patris omnipotentis: inde venturus est iudicare vivos et mortuos.

Credo in Spiritum Sanctum, sanctam Ecclesiam catholicam, Sanctorum communionem, remissionem peccatorum, carnis resurrectionem, vitam aeternam. Amen."""
                else """I believe in God, the Father Almighty, Creator of heaven and earth. And in Jesus Christ, His only Son, our Lord: who was conceived by the Holy Ghost, born of the Virgin Mary, suffered under Pontius Pilate, was crucified, died, and was buried: He descended into hell; the third day He rose again from the dead; He ascended into heaven, and sitteth on the right hand of God the Father Almighty; from thence He shall come to judge the living and the dead.

I believe in the Holy Ghost, the holy Catholic Church, the communion of Saints, the forgiveness of sins, the resurrection of the body, and life everlasting. Amen.""",
                viewModel = viewModel,
                prayerId = "futuristic_open_creed",
                isActive = (currentOpeningStep == 1)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Our Father
            PrayerSectionCard(
                number = "3",
                title = "Our Father (Pater Noster)",
                text = if (isLatin) "Pater noster, qui es in caelis: sanctificetur nomen tuum: adveniat regnum tuum: fiat voluntas tua, sicut in caelo, et in terra. Panem nostrum cotidianum da nobis hodie: et dimitte nobis debita nostra, sicut et nos dimittimus debitoribus nostris: et ne nos inducas in tentationem: sed libera nos a malo. Amen."
                else "Our Father, who art in heaven, hallowed be Thy name; Thy kingdom come; Thy will be done on earth as it is in heaven. Give us this day our daily bread; and forgive us our trespasses as we forgive those who trespass against us; and lead us not into temptation, but deliver us from evil. Amen.",
                viewModel = viewModel,
                prayerId = "futuristic_open_pater",
                isActive = (currentOpeningStep == 2)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Three Hail Marys
            PrayerSectionCard(
                number = "4",
                title = "3 Hail Marys (Ave Maria) — For Faith, Hope & Charity",
                text = if (isLatin) "Ave Maria, gratia plena, Dominus tecum. Benedicta tu in mulieribus, et benedictus fructus ventris tui, Iesus. Sancta Maria, Mater Dei, ora pro nobis peccatoribus, nunc et in hora mortis nostrae. Amen."
                else "Hail Mary, full of grace, the Lord is with thee. Blessed art thou among women, and blessed is the fruit of thy womb, Jesus. Holy Mary, Mother of God, pray for us sinners, now and at the hour of our death. Amen.",
                viewModel = viewModel,
                prayerId = "futuristic_open_ave",
                isActive = (currentOpeningStep == 3)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Glory Be
            PrayerSectionCard(
                number = "5",
                title = "Glory Be (Gloria Patri)",
                text = if (isLatin) "Gloria Patri, et Filio, et Spiritui Sancto. Sicut erat in principio, et nunc, et semper, et in saecula saeculorum. Amen."
                else "Glory be to the Father, and to the Son, and to the Holy Ghost. As it was in the beginning, is now, and ever shall be, world without end. Amen.",
                viewModel = viewModel,
                prayerId = "futuristic_open_gloria",
                isActive = (currentOpeningStep == 4)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 6. Fatima Prayer
            PrayerSectionCard(
                number = "6",
                title = "Fatima Prayer (O Mi Iesu)",
                text = if (isLatin) "O mi Iesu, dimitte nobis debita nostra, libera nos ab igne inferni, duc in caelum omnes, praesertim eos, qui maxima indigent misericordia tua."
                else "O My Jesus, forgive us our sins, save us from the fires of hell, lead all souls to Heaven, especially those most in need of Thy mercy.",
                viewModel = viewModel,
                prayerId = "futuristic_open_fatima",
                isActive = (currentOpeningStep == 5)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onStartDecades,
                colors = ButtonDefaults.buttonColors(containerColor = GoldSacred, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Begin 1st Decade ›", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 2. DECADES INTERACTIVE VIEW WITH ANIMATED ROSARY BEADS
// -----------------------------------------------------------------------------
@Composable
private fun DecadesInteractiveView(
    isLatin: Boolean,
    viewModel: MainViewModel,
    selectedSetIndex: Int,
    onSetSelected: (Int) -> Unit,
    currentMysteryIndex: Int,
    onMysterySelected: (Int) -> Unit,
    currentStepInDecade: Int,
    onStepSelected: (Int) -> Unit,
    pulseGlow: Float,
    rotateAngle: Float
) {
    val currentSet = TraditionalPrayerLibrary.rosarySets.getOrElse(selectedSetIndex) { TraditionalPrayerLibrary.rosarySets[0] }
    val currentMystery = currentSet.mysteries.getOrElse(currentMysteryIndex) { currentSet.mysteries[0] }

    Column {
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
                    onClick = { onSetSelected(index) },
                    label = { Text(set.category, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldSacred,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mystery Selector Bar (1 to 5)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (i in 0..4) {
                val isSel = currentMysteryIndex == i
                Surface(
                    onClick = { onMysterySelected(i) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSel) CrimsonBurgundy else DarkCathedralSurface,
                    border = BorderStroke(1.dp, if (isSel) GoldBright else DarkCathedralCardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Decade ${i + 1}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isSel) GoldBright else TextMutedParchment,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ANIMATED ROSARY BEAD RING CANVAS
        AnimatedRosaryBeadRing(
            activeStep = currentStepInDecade,
            onBeadClicked = onStepSelected,
            pulseGlow = pulseGlow,
            rotateAngle = rotateAngle
        )

        Spacer(modifier = Modifier.height(14.dp))

        // MYSTERY HEADER & MEDITATION CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface.copy(alpha = 0.9f)),
            border = BorderStroke(1.5.dp, GoldBright)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = CrimsonBurgundy.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, GoldBright)
                    ) {
                        Text(
                            text = "DECADE ${currentMystery.number} OF 5",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = GoldBright
                        )
                    }

                    Text(
                        text = "Fruit: ${currentMystery.fruit}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGoldHighlight
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isLatin && currentMystery.latinTitle.isNotBlank()) currentMystery.latinTitle else currentMystery.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                    color = GoldBright
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = currentMystery.meditation,
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif, lineHeight = 22.sp),
                    color = ParchmentLight
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ACTIVE PRAYER CARD (NON-TRUNCATED)
        val prayerCardTitle: String
        val prayerCardBody: String

        when (currentStepInDecade) {
            0 -> {
                prayerCardTitle = "Pater Noster • Our Father"
                prayerCardBody = if (isLatin) "Pater noster, qui es in caelis: sanctificetur nomen tuum: adveniat regnum tuum: fiat voluntas tua, sicut in caelo, et in terra. Panem nostrum cotidianum da nobis hodie: et dimitte nobis debita nostra, sicut et nos dimittimus debitoribus nostris: et ne nos inducas in tentationem: sed libera nos a malo. Amen."
                else "Our Father, who art in heaven, hallowed be Thy name; Thy kingdom come; Thy will be done on earth as it is in heaven. Give us this day our daily bread; and forgive us our trespasses as we forgive those who trespass against us; and lead us not into temptation, but deliver us from evil. Amen."
            }
            in 1..10 -> {
                prayerCardTitle = "Ave Maria • Hail Mary (Bead $currentStepInDecade / 10)"
                prayerCardBody = if (isLatin) "Ave Maria, gratia plena, Dominus tecum. Benedicta tu in mulieribus, et benedictus fructus ventris tui, Iesus. Sancta Maria, Mater Dei, ora pro nobis peccatoribus, nunc et in hora mortis nostrae. Amen."
                else "Hail Mary, full of grace, the Lord is with thee. Blessed art thou among women, and blessed is the fruit of thy womb, Jesus. Holy Mary, Mother of God, pray for us sinners, now and at the hour of our death. Amen."
            }
            11 -> {
                prayerCardTitle = "Gloria Patri • Glory Be"
                prayerCardBody = if (isLatin) "Gloria Patri, et Filio, et Spiritui Sancto. Sicut erat in principio, et nunc, et semper, et in saecula saeculorum. Amen."
                else "Glory be to the Father, and to the Son, and to the Holy Ghost. As it was in the beginning, is now, and ever shall be, world without end. Amen."
            }
            else -> {
                prayerCardTitle = "O Mi Iesu • Fatima Prayer"
                prayerCardBody = if (isLatin) "O mi Iesu, dimitte nobis debita nostra, libera nos ab igne inferni, duc in caelum omnes, praesertim eos, qui maxima indigent misericordia tua."
                else "O My Jesus, forgive us our sins, save us from the fires of hell, lead all souls to Heaven, especially those most in need of Thy mercy."
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
            border = BorderStroke(1.dp, GoldSacred)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = prayerCardTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                    color = GoldBright
                )

                Spacer(modifier = Modifier.height(8.dp))
                IlluminatedDivider()
                Spacer(modifier = Modifier.height(8.dp))

                // COMPLETE NON-TRUNCATED PRAYER TEXT
                Text(
                    text = prayerCardBody,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    ),
                    color = ParchmentLight,
                    softWrap = true,
                    maxLines = Int.MAX_VALUE
                )

                Spacer(modifier = Modifier.height(10.dp))
                com.example.ui.components.LatinAudioPlayerController(
                    viewModel = viewModel,
                    prayerId = "futuristic_bead_${currentStepInDecade}",
                    prayerText = prayerCardBody,
                    isLatin = isLatin,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// ANIMATED ROSARY BEAD RING CANVAS (10 HAIL MARY BEADS + OUR FATHER + GLORIA)
// -----------------------------------------------------------------------------
@Composable
private fun AnimatedRosaryBeadRing(
    activeStep: Int,
    onBeadClicked: (Int) -> Unit,
    pulseGlow: Float,
    rotateAngle: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1322)),
        border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.6f))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        // Click on canvas advances step
                        onBeadClicked((activeStep + 1) % 13)
                    }
            ) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val rx = size.width * 0.38f
                val ry = size.height * 0.32f

                // Draw wire connecting beads
                drawOval(
                    color = GoldSacred.copy(alpha = 0.4f),
                    topLeft = Offset(cx - rx, cy - ry),
                    size = androidx.compose.ui.geometry.Size(rx * 2f, ry * 2f),
                    style = Stroke(width = 2.dp.toPx())
                )

                // 12 Nodes: 0: Pater Noster, 1..10: Ave Maria 1-10, 11: Gloria/Fatima
                val totalNodes = 12
                for (i in 0 until totalNodes) {
                    val angle = Math.toRadians((i * (360.0 / totalNodes) - 90.0).toDouble())
                    val bx = (cx + rx * cos(angle)).toFloat()
                    val by = (cy + ry * sin(angle)).toFloat()

                    val isActive = i == activeStep || (i == 11 && activeStep >= 11)
                    val isPast = i < activeStep

                    val beadColor = when {
                        isActive -> GoldBright
                        isPast -> Color(0xFF38BDF8)
                        i == 0 -> CrimsonBurgundy
                        else -> DarkCathedralCardBorder
                    }

                    val beadRadius = if (isActive) (10.dp.toPx() * (1f + 0.15f * pulseGlow)) else 6.dp.toPx()

                    // Glow aura if active
                    if (isActive) {
                        drawCircle(
                            color = GoldBright.copy(alpha = 0.4f * pulseGlow),
                            center = Offset(bx, by),
                            radius = beadRadius * 2f
                        )
                    }

                    drawCircle(
                        color = beadColor,
                        center = Offset(bx, by),
                        radius = beadRadius
                    )
                }
            }

            // Center Badge text
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = when (activeStep) {
                        0 -> "PATER NOSTER"
                        in 1..10 -> "AVE MARIA $activeStep/10"
                        11 -> "GLORIA PATRI"
                        else -> "FATIMA PRAYER"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = GoldBright
                )
                Text(
                    text = "Tap Ring to Advance",
                    fontSize = 9.sp,
                    color = TextMutedParchment
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 3. CONCLUDING PRAYERS FULL VIEW (NON-TRUNCATED)
// -----------------------------------------------------------------------------
@Composable
private fun ConcludingPrayersFullView(
    isLatin: Boolean,
    viewModel: MainViewModel,
    currentConcludingStep: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface.copy(alpha = 0.9f)),
        border = BorderStroke(1.5.dp, GoldBright)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = if (isLatin) "ORATIONES CONCLUSIVAE • CONCLUDING PRAYERS" else "Concluding Prayers of the Holy Rosary",
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                color = GoldBright
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Hail Holy Queen
            PrayerSectionCard(
                number = "1",
                title = "Hail, Holy Queen (Salve Regina)",
                text = if (isLatin) """Salve, Regina, Mater misericordiae, vita, dulcedo, et spes nostra, salve. Ad te clamamus exsules filii Hevae, ad te suspiramus gementes et flentes in hac lacrimarum valle.

Eia ergo, advocata nostra, illos tuos misericordes oculos ad nos converte; et Iesum, benedictum fructum ventris tui, nobis post hoc exsilium ostende. O clemens, o pia, o dulcis Virgo Maria."""
                else """Hail, Holy Queen, Mother of Mercy, our life, our sweetness, and our hope. To thee do we cry, poor banished children of Eve; to thee do we send up our sighs, mourning and weeping in this valley of tears.

Turn then, most gracious advocate, thine eyes of mercy toward us; and after this our exile, show unto us the blessed fruit of thy womb, Jesus. O clement, O loving, O sweet Virgin Mary.""",
                viewModel = viewModel,
                prayerId = "futuristic_close_salve",
                isActive = (currentConcludingStep == 0)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Versicle & Response
            PrayerSectionCard(
                number = "2",
                title = "Versicle & Response",
                text = if (isLatin) """V. Ora pro nobis, Sancta Dei Genetrix.
R. Ut digni efficiamur promissionibus Christi."""
                else """V. Pray for us, O Holy Mother of God.
R. That we may be made worthy of the promises of Christ.""",
                viewModel = viewModel,
                prayerId = "futuristic_close_versicle",
                isActive = (currentConcludingStep == 1)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Concluding Rosary Prayer
            PrayerSectionCard(
                number = "3",
                title = "Concluding Rosary Prayer (O Deus, Cuius Unigenitus)",
                text = if (isLatin) """Oremus. Deus, cuius Unigenitus per vitam, mortem et resurrectionem suam nobis salutis aeternae praemia comparavit: concede, quaesumus; ut haec mysteria sanctissimo Beatae Mariae Virginis Rosario recolentes, et imitemur quod continent, et quod promittunt assequamur. Per eundem Christum Dominum nostrum. Amen."""
                else """O God, whose only-begotten Son, by His life, death, and resurrection, has purchased for us the rewards of eternal life, grant, we beseech Thee, that meditating upon these mysteries of the Most Holy Rosary of the Blessed Virgin Mary, we may imitate what they contain and obtain what they promise, through the same Christ our Lord. Amen.""",
                viewModel = viewModel,
                prayerId = "futuristic_close_concluding",
                isActive = (currentConcludingStep == 2)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4. St. Michael Prayer & Final Blessing
            PrayerSectionCard(
                number = "4",
                title = "St. Michael Archangel & Final Sign of the Cross",
                text = if (isLatin) """Sancte Michael Archangele, defende nos in praelio; contra nequitiam et insidias diaboli esto praesidium. Imperet illi Deus, supplices deprecamur: tuque, Princeps militiae caelestis, Satanam aliosque spiritus malignos, qui ad perditionem animarum pervagantur in mundo, divina virtute in infernum detrude. Amen.

☩ In nomine Patris, et Filii, et Spiritus Sancti. Amen."""
                else """St. Michael the Archangel, defend us in battle; be our protection against the wickedness and snares of the devil. May God rebuke him, we humbly pray; and do thou, O Prince of the heavenly host, by the power of God, thrust into hell Satan and all the evil spirits who prowl about the world seeking the ruin of souls. Amen.

☩ In the name of the Father, and of the Son, and of the Holy Ghost. Amen.""",
                viewModel = viewModel,
                prayerId = "futuristic_close_michael",
                isActive = (currentConcludingStep == 3)
            )
        }
    }
}

@Composable
private fun PrayerSectionCard(
    number: String,
    title: String,
    text: String,
    viewModel: MainViewModel? = null,
    prayerId: String = "",
    isActive: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isActive) CrimsonBurgundy.copy(alpha = 0.25f) else DarkCathedralSurface,
        border = BorderStroke(if (isActive) 2.dp else 1.dp, if (isActive) GoldBright else DarkCathedralCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = if (isActive) GoldBright else CrimsonBurgundy,
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = number, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isActive) Color.Black else GoldBright)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isActive) "$title (Active)" else title,
                    style = MaterialTheme.typography.titleSmall.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                    color = if (isActive) GoldBright else TextMutedParchment
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                ),
                color = ParchmentLight,
                softWrap = true,
                maxLines = Int.MAX_VALUE
            )

            if (viewModel != null && prayerId.isNotEmpty()) {
                val cleanText = text.replace("☩", "").trim()
                Spacer(modifier = Modifier.height(10.dp))
                com.example.ui.components.LatinAudioPlayerController(
                    viewModel = viewModel,
                    prayerId = prayerId,
                    prayerText = cleanText,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

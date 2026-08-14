package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.CrimsonBurgundy
import com.example.ui.theme.DarkCathedralBackground
import com.example.ui.theme.DarkCathedralSurface
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldSacred
import com.example.ui.theme.ParchmentLight
import com.example.ui.theme.TextMutedParchment
import kotlinx.coroutines.launch

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.TrackLazyListScroll
import com.example.ui.components.fullScreenToggleOnTap
import com.example.ui.components.rememberDirectionalScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll

@Composable
fun DeceptionScreen(
    viewModel: MainViewModel
) {
    var selectedSectionIndex by remember { mutableIntStateOf(0) }
    val sectionTitles = listOf(
        "Great Apostasy",
        "Dogmatic Fact",
        "False Religions",
        "Invalid Mass & Rites",
        "No Salvation Outside",
        "Religious Liberty",
        "Channels & Resources",
        "Comparison Matrix"
    )

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    TrackLazyListScroll(listState = listState, viewModel = viewModel)

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setReadingProgress(0f)
            viewModel.setBottomNavVisible(true)
            viewModel.setTopHeaderVisible(true)
        }
    }

    val isTopHeaderVisible by viewModel.isTopHeaderVisible.collectAsStateWithLifecycle()
    val directionalScrollConnection = rememberDirectionalScrollConnection(viewModel = viewModel)

    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 1 }
    }

    val isBottomNavVisible by viewModel.isBottomNavVisible.collectAsStateWithLifecycle()
    val dynamicFabBottomPadding by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isBottomNavVisible) 80.dp else 20.dp,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "deception_fab_bottom_padding"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCathedralBackground)
            .fullScreenToggleOnTap(viewModel)
            .nestedScroll(directionalScrollConnection),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 600.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("deception_screen_lazy_column"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header Title Card
            item {
                AnimatedVisibility(
                    visible = isTopHeaderVisible,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    val context = LocalContext.current
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
                        border = BorderStroke(1.5.dp, CrimsonBurgundy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .testTag("deception_header_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = CrimsonBurgundy,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "THE DECEPTION OF VATICAN II",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                ),
                                color = GoldBright
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Comprehensive Apologetics & Dogmatic Refutation of the Vatican II Counter-Church, Modernist Antipopes, Invalid Sacramental Rites, & Total Apostasy",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMutedParchment,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = GoldSacred.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "SONGS EXPOSING VATICAN II",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = GoldBright
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Song 1
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/tjGnf_NMPQg?si=mqSfd1N40Dxz930q"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonBurgundy),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("song_link_button_1")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartDisplay,
                                    contentDescription = null,
                                    tint = GoldBright,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Song 1: Vatican II Exposing Anthem",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = GoldBright
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    tint = GoldBright,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            // Song 2
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/zaL9y4CR3xk?si=YmkU1L2fFzD31gtA"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkCathedralBackground),
                                border = BorderStroke(1.dp, GoldSacred),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("song_link_button_2")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartDisplay,
                                    contentDescription = null,
                                    tint = GoldBright,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Song 2: Vatican 2! What the Heck Are You",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = GoldBright
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    tint = GoldBright,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Scrollable Navigation Tabs for Sub-topics
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedSectionIndex,
                    containerColor = DarkCathedralSurface,
                    contentColor = GoldBright,
                    edgePadding = 8.dp,
                    indicator = { tabPositions ->
                        if (selectedSectionIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedSectionIndex]),
                                color = CrimsonBurgundy
                            )
                        }
                    },
                    divider = { HorizontalDivider(color = GoldSacred.copy(alpha = 0.3f)) }
                ) {
                    sectionTitles.forEachIndexed { index, title ->
                        val isSelected = selectedSectionIndex == index
                        Tab(
                            selected = isSelected,
                            onClick = {
                                selectedSectionIndex = index
                                coroutineScope.launch {
                                    // Scroll smoothly to section content block (item index 2)
                                    listState.animateScrollToItem(2)
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    ),
                                    color = if (isSelected) GoldBright else TextMutedParchment,
                                    maxLines = 1
                                )
                            },
                            modifier = Modifier.testTag("deception_tab_$index")
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Dynamic Section Content based on selected tab
            item {
                when (selectedSectionIndex) {
                    0 -> GreatApostasySection()
                    1 -> DogmaticFactSection()
                    2 -> FalseReligionsSection()
                    3 -> InvalidMassAndRitesSection()
                    4 -> NoSalvationOutsideSection()
                    5 -> ReligiousLibertySection()
                    6 -> ChannelsAndResourcesSection()
                    7 -> ComparisonMatrixSection()
                }
            }

            item {
                Spacer(modifier = Modifier.height(120.dp))
            }
        }

        // Floating Action Button for Scroll to Top
        AnimatedVisibility(
            visible = showScrollToTop,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = dynamicFabBottomPadding)
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                containerColor = CrimsonBurgundy,
                contentColor = GoldBright,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Scroll to top"
                )
            }
        }
    }
}

/**
 * Section 1: The Great Apostasy Foretold in Scripture & Prophecy
 */
@Composable
fun GreatApostasySection() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        DeceptionCard(
            title = "1. Holy Scripture: The Great Falling Away & The Son of Perdition",
            icon = Icons.Default.Church,
            content = """
                The Second Vatican Council (1962–1965), convened by John XXIII and concluded by Paul VI, was not a Catholic Council, but an apostate revolution that erected a non-Catholic Counter-Church—the Vatican II Sect—upon the ruins of Catholic tradition.

                HOLY SCRIPTURE FORETOLD THIS EXACT SPIRITUAL CATASTROPHE:
                • 2 Thessalonians 2:3–4: 'Let no man deceive you by any means, for unless there come a revolt [apostasy] first, and the man of sin be revealed, the son of perdition, Who opposeth and is lifted up above all that is called God or that is worshipped, so that he sitteth in the temple of God, shewing himself as if he were God.'
                • Apocalypse 17:4–5: The Whore of Babylon sitting upon the seven hills of Rome, clad in purple and scarlet, holding a golden cup full of abominations and filthiness of her fornication.
                • Matthew 24:15: 'When therefore you shall see the abomination of desolation, which was spoken of by Daniel the prophet, standing in the holy place...'
                • Matthew 24:24: 'For there shall arise false Christs and false prophets, and shall show great signs and wonders, insomuch as to deceive (if possible) even the elect.'
            """.trimIndent()
        )

        DeceptionCard(
            title = "2. Marian Apparitions & Catholic Prophecies",
            icon = Icons.Default.Shield,
            content = """
                OUR LADY OF LA SALETTE (1846 - Approved Catholic Apparition):
                'Rome will lose the Faith and become the seat of the Anti-Christ... The Church will be in eclipse, the world will be in dismay.'

                POPE LEO XIII'S VISION & EXORCISM PRAYER (1884):
                Pope Leo XIII composed the original St. Michael Exorcism Prayer after receiving a divine vision of Satan attempting to destroy the Church:
                'In the Holy Place itself, where has been established the See of the most holy Peter and the Chair of Truth for the light of the world, they have raised the throne of their abominable impiety, with the iniquitous design that when the Pastor has been struck, the sheep may be scattered.'

                THE THIRD SECRET OF FATIMA (1917):
                As explained by faithful Catholic theologians, the true Third Secret of Fatima warns of a massive apostasy originating at the very top of the Church hierarchy in Rome, resulting in counterfeit rites and loss of dogma.
            """.trimIndent()
        )
    }
}

/**
 * Section 2: The Dogmatic Fact: Manifest Heretics Cannot Hold Office
 */
@Composable
fun DogmaticFactSection() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        DeceptionCard(
            title = "1. Infallible Papal Bull: 'Cum Ex Apostolatus Officio' (Pope Paul IV, 1559)",
            icon = Icons.Default.Gavel,
            content = """
                Pope Paul IV solemnly defined ex-cathedra in his Papal Bull 'Cum Ex Apostolatus Officio' (Feb. 15, 1559):
                
                • If anyone before his election as Bishop, Cardinal, or Pope had deviated from the Catholic Faith or fallen into any heresy, his election is NULL, VOID, AND WORTHLESS.
                • Such an election confers ZERO authority, jurisdiction, or administrative power upon the claimant.
                • The faithful have the right and obligation to reject such heretical claimants as invalid usurpers and antipopes.
                
                This solemn papal decree proves beyond doubt that a public heretic cannot validly become or remain Pope.
            """.trimIndent()
        )

        DeceptionCard(
            title = "2. Unanimous Teaching of Doctors of the Church & Canon Law",
            icon = Icons.Default.Book,
            content = """
                ST. ROBERT BELLARMINE (Doctor of the Church, 'De Romano Pontifice', Book II, Ch. 30):
                'A pope who is a manifest heretic automatically ceases to be pope and head, just as he ceases automatically to be a Christian and a member of the Church. Wherefore, he can be judged and punished by the Church. This is the teaching of all the ancient Fathers who teach that manifest heretics immediately lose all jurisdiction.'

                ST. ANTONINUS, ST. FRANCIS DE SALES, ST. ALPHONSUS LIGUORI:
                All unanimously affirm that if a Pope were to become a manifest heretic, he would ipso facto fall from the pontificate without needing any declaration.

                1917 CODE OF CANON LAW (Canon 188.4):
                'Any office becomes vacant upon the fact and without any declaration by tacit resignation accepted by the law itself if anyone publicly defects from the Catholic faith.'

                CONCLUSION (SEDEVACANTISM):
                John XXIII, Paul VI, John Paul I, John Paul II, Benedict XVI, and Francis were/are non-Catholic antipopes who automatically lost or never possessed papal jurisdiction due to public manifest heresy.
            """.trimIndent()
        )
    }
}

/**
 * Section 3: Blasphemous Heresy on False Religions
 */
@Composable
fun FalseReligionsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        DeceptionCard(
            title = "1. Nostra Aetate: Blasphemy on Islam & Paganism",
            icon = Icons.Default.Warning,
            content = """
                HERETICAL STATEMENT OF VATICAN II (Nostra Aetate §3):
                'The Church regards with esteem also the Muslims. They adore the one God, living and subsisting in Himself; merciful and all-powerful, the Creator of heaven and earth...'
                
                ☩ INFALLIBLE CATHOLIC DOGMA (Council of Florence, 'Cantate Domino' 1442):
                'The Holy Roman Church firmly believes, professes, and preaches that all those who are outside the Catholic Church, not only pagans but also Jews or heretics and schismatics, cannot share in eternal life; but that they will go into the everlasting fire which was prepared for the devil and his angels, unless before death they are joined to Her.'
                
                REFUTATION:
                To claim that Muslims worship the True God is explicit blasphemy and heresy. Muslims reject the Holy Trinity and the Incarnation of Jesus Christ.
                1 John 2:23 declares: 'Whosoever denieth the Son, the same hath not the Father.'
                Psalm 95:5 declares: 'For all the gods of the Gentiles are devils.'
            """.trimIndent()
        )

        DeceptionCard(
            title = "2. Assisi World Day of Prayer & Pachamama Idolatry",
            icon = Icons.Default.Church,
            content = """
                THE ASSISI SACRILEGES (John Paul II - 1986, 2002; Benedict XVI - 2011):
                Antipope John Paul II gathered animists, witch doctors, fire-worshippers, and Buddhists at Assisi. He placed a statue of Buddha upon the sacred Catholic tabernacle/altar of San Pietro and allowed pagan idol worship in consecrated Catholic sanctuaries.

                THE PACHAMAMA SACRILEGE IN ST. PETER'S (Francis - 2019):
                Antipope Francis bowed down before the Amazonian pagan fertility idol 'Pachamama' in the Vatican Gardens and placed the idol in front of the tomb of St. Peter.

                THE ABU DHABI DECLARATION (Francis - Feb 2019):
                Francis signed a joint declaration stating: 'The pluralism and the diversity of religions, color, sex, race and language are willed by God in His wisdom.'
                This claims that God directly wills false idol-worshipping religions, which is apostasy.
            """.trimIndent()
        )
    }
}

/**
 * Section 4: The Invalidity of the Novus Ordo Mass & Sacramental Rites
 */
@Composable
fun InvalidMassAndRitesSection() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        DeceptionCard(
            title = "1. The Invalidity of the Novus Ordo Missae (1969)",
            icon = Icons.Default.Church,
            content = """
                The New Mass (Novus Ordo Missae), promulgated by Antipope Paul VI in 1969, was engineered with six Protestant ministers (including Canon Jasper and George Lindbeck) to eliminate the Catholic dogma of the Holy Sacrifice.

                ESSENTIAL DEFECTS RENDERING THE NEW MASS INVALID & AN ABOMINATION:
                1. Mutilation of the Consecration Form: In the vernacular translations, 'pro multis' ('for many') was changed to 'for all', corrupting the words instituted by Christ and preaching universal salvation.
                2. Elimination of Sacrificial Offertory: Replaced with a Jewish/Lutheran meal prayer thanking God for bread and wine.
                3. Desecration of the Altar: The holy altar was replaced with a Protestant table facing the people ('versus populum').
                4. The Ottaviani Intervention (1969): Cardinals Ottaviani and Bacci testified to Paul VI that the Novus Ordo represents 'a striking departure from the Catholic theology of the Mass as formulated in Session 22 of the Council of Trent.'
            """.trimIndent()
        )

        DeceptionCard(
            title = "2. Invalidity of the 1968 New Rite of Episcopal Consecration",
            icon = Icons.Default.Gavel,
            content = """
                In 1968, Antipope Paul VI invalidly altered the essential sacramental form for the consecration of bishops.

                POPE PIUS XII'S INFALLIBLE DECREE ('Sacramentum Ordinis' 1947):
                Pius XII defined the exact essential words required for valid episcopal consecration:
                'Comple in sacerdote tuo ministerii tui summam...' ('Fulfill in Thy priest the sum of Thy ministry...')

                PAUL VI'S INVALID MUTILATION (1968):
                Paul VI removed these required words and substituted a vague phrase ('Spiritum principalem' - governing Spirit) taken from a non-sacramental Syrian installation prayer that does not signify the grace of the episcopate or the fullness of the priesthood.

                CATASTROPHIC CONSEQUENCE:
                Bishops consecrated in the 1968 New Rite are INVALID BISHOPS. Consequently, priests ordained by these invalid bishops are INVALID PRIESTS who cannot absolve sins or consecrate the Holy Eucharist.
            """.trimIndent()
        )
    }
}

/**
 * Section 5: Salvation of Non-Catholics & Extra Ecclesiam Nulla Salus
 */
@Composable
fun NoSalvationOutsideSection() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        DeceptionCard(
            title = "1. The Three Infallible Ex-Cathedra Definitions",
            icon = Icons.Default.Book,
            content = """
                THE DOGMA OF SALVATION IS DEFINED THREE TIMES EX-CATHEDRA:

                1. POPE INNOCENT III (Fourth Lateran Council, 1215):
                'There is indeed one universal Church of the faithful, outside of which nobody at all is saved, in which Jesus Christ is Himself both priest and sacrifice.'

                2. POPE BONIFACE VIII (Unam Sanctam, 1302):
                'We declare, say, define, and pronounce that it is absolutely necessary for the salvation of every human creature to be subject to the Roman Pontiff.'

                3. POPE EUGENE IV (Council of Florence, 'Cantate Domino' 1442):
                'The Holy Roman Church firmly believes, professes, and preaches that all those who are outside the Catholic Church, not only pagans but also Jews or heretics and schismatics, cannot share in eternal life; but that they will go into the everlasting fire which was prepared for the devil and his angels, unless before death they are joined to Her.'
            """.trimIndent()
        )

        DeceptionCard(
            title = "2. Refutation of Modern Indifferentism & Vatican II Heresy",
            icon = Icons.Default.Shield,
            content = """
                HERESY OF VATICAN II (Lumen Gentium §16 & Gaudium et Spes §22):
                Vatican II claims that non-Christians, atheists, and unbaptized persons can achieve eternal salvation by following their conscience.

                CATHOLIC TRUTH:
                Jesus Christ solemnly declared in John 3:5: 'Unless a man be born again of water and the Holy Ghost, he cannot enter into the kingdom of God.'
                Mark 16:16 declares: 'He that believeth and is baptized shall be saved; but he that believeth not shall be condemned.'

                No one can be saved without the Catholic Faith and Water Baptism. Claims of 'universal salvation' or salvation in false sects are soul-destroying lies.
            """.trimIndent()
        )
    }
}

/**
 * Section 6: Religious Liberty & Ecumenism Refuted
 */
@Composable
fun ReligiousLibertySection() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        DeceptionCard(
            title = "1. Dignitatis Humanae: Condemned Heresy on Religious Freedom",
            icon = Icons.Default.Gavel,
            content = """
                HERETICAL STATEMENT OF VATICAN II (Dignitatis Humanae §2):
                'This Vatican Council declares that the human person has a right to religious freedom. This freedom means that all men are to be immune from coercion... in matters religious, no one is to be forced to act in a manner contrary to his own beliefs, whether privately or publicly.'

                ☩ INFALLIBLE CATHOLIC DOGMA (Pope Pius IX, 'Quanta Cura' & 'Syllabus of Errors' 1864):
                Condemned Proposition #77: 'In the present day it is no longer expedient that the Catholic religion should be held as the only religion of the State, to the exclusion of all other forms of worship.'
                Condemned Proposition #78: 'Hence it has been wisely decided by law, in some Catholic countries, that persons coming to reside therein shall enjoy the public exercise of their own peculiar worship.'

                Pope Pius IX explicitly condemned religious liberty for false worship as a 'delirium' and an 'insanity' that destroys immortal souls.
            """.trimIndent()
        )

        DeceptionCard(
            title = "2. Unitatis Redintegratio: Heretical Ecumenism",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            content = """
                HERETICAL STATEMENT OF VATICAN II (Unitatis Redintegratio §3 & Lumen Gentium §8):
                'The Church of Christ subsists in the Catholic Church...' and 'Separated Churches and Communities... have been by no means deprived of significance and importance in the mystery of salvation.'

                ☩ INFALLIBLE CATHOLIC DOGMA (Pope Pius XI, 'Mortalium Animos' 1928):
                'It is clear that the Apostolic See can by no means take part in these assemblies, nor is it in any way lawful for Catholics to give to such enterprises their encouragement or support; if they did so, they would be giving countenance to a false Christianity, quite alien to the one Church of Christ.'

                REFUTATION:
                To state that non-Catholic heretical sects are 'means of salvation' is a direct denial of Extra Ecclesiam Nulla Salus.
            """.trimIndent()
        )
    }
}

/**
 * Section 7: Key Channels & Apologetics Resources
 */
@Composable
fun ChannelsAndResourcesSection() {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Channel 1: Most Holy Family Monastery / Dimond Brothers
        ChannelCard(
            title = "Most Holy Family Monastery (@vaticancatholic-dimond)",
            subtitle = "vaticancatholic.com • endtimes.video",
            description = """
                Led by Brother Michael Dimond and Brother Peter Dimond, Most Holy Family Monastery provides the world's most exhaustive dogmatic Catholic apologetics exposing the Vatican II Counter-Church.

                CORE APOLOGETICS & EXPOSÉS:
                • Infallible Papal Decrees: Proves through Pope Paul IV's 'Cum Ex Apostolatus Officio' and Canon 188.4 that heretics automatically lose all ecclesiastical authority.
                • Heresies of post-1958 Antipopes: Documents hundreds of explicit heresies uttered by John XXIII, Paul VI, John Paul II, Benedict XVI, and Francis.
                • The Assisi Pagan Sacrileges & Pachamama: Comprehensive video documentation of pagan worship on Catholic altars.
                • Invalidity of New Rites: Detailed theological proof that the 1968 New Rite of Bishops is 100% invalid.
            """.trimIndent(),
            webUrl = "https://vaticancatholic.com/",
            ytUrl = "https://www.youtube.com/@vaticancatholic-dimond"
        )

        // Channel 2: Extra Ecclesiam Nulla Salus 33
        ChannelCard(
            title = "Extra Ecclesiam Nulla Salus 33 (@extraecclesiamnullasalus33)",
            subtitle = "Uncompromising Defense of Dogma",
            description = """
                Focuses exclusively on the absolute, literal necessity of the Catholic Faith and Water Baptism for salvation, exposing modern indifferentism and universal salvation heresies.

                KEY PROOFS & DEFINITIONS:
                • Detailed breakdowns of the 3 Ex-Cathedra Papal Definitions (1215, 1302, 1442).
                • Exposure of Vatican II's false teaching on salvation for non-Catholics.
                • Scriptural and Patristic refutations of modern ecumenical compromises.
            """.trimIndent(),
            webUrl = "https://endtimes.video/",
            ytUrl = "https://www.youtube.com/@extraecclesiamnullasalus33"
        )

        // Channel 3: Shameless Papist (Dimond Brothers)
        ChannelCard(
            title = "Shameless Papist (@ShamelessPapist)",
            subtitle = "Dogmatic Catholic Apologetics by the Dimond Brothers",
            description = """
                Channel created and published by the Dimond Brothers (Most Holy Family Monastery) delivering sharp, decisive Catholic video apologetics refuting the Vatican II apostasy, antipopes, invalid rites, and Protestant/Eastern Orthodox errors.

                FEATURED PRODUCTIONS:
                • 'Vatican II Exposed': Step-by-step textual breakdown of conciliar heresies.
                • Refutation of Conciliar Apologists: Demonstrating how attempts to reconcile Vatican II with Catholic dogma fail to preserve Church Indefectibility.
                • The Invalidity of the Novus Ordo Mass & 1968 Ordination Rite.
            """.trimIndent(),
            webUrl = "https://vaticancatholic.com/",
            ytUrl = "https://www.youtube.com/@ShamelessPapist"
        )
    }
}

@Composable
fun ChannelCard(
    title: String,
    subtitle: String,
    description: String,
    webUrl: String,
    ytUrl: String
) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
        border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SmartDisplay,
                    contentDescription = null,
                    tint = CrimsonBurgundy,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldBright
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMutedParchment
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = GoldSacred.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                color = ParchmentLight
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ytUrl))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonBurgundy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = GoldBright
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("YouTube", style = MaterialTheme.typography.labelMedium, color = GoldBright)
                }

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCathedralBackground),
                    border = BorderStroke(1.dp, GoldSacred),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = GoldBright
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Website", style = MaterialTheme.typography.labelMedium, color = GoldBright)
                }
            }
        }
    }
}

/**
 * Section 8: Comparison Matrix Card
 */
@Composable
fun ComparisonMatrixSection() {
    val items = listOf(
        MatrixItem(
            topic = "Salvation of Non-Catholics",
            preVatican2 = "OUTSIDE THE CHURCH THERE IS ABSOLUTELY NO SALVATION (Infallible Dogma of Florence & Unam Sanctam).",
            vatican2 = "False Religions are means of salvation; non-Christians attain eternal life (Lumen Gentium §16, Nostra Aetate).",
            analysis = "DIRECT HERESY & CONTRADICTION of Defined Ex-Cathedra Dogma."
        ),
        MatrixItem(
            topic = "Religious Liberty for False Worship",
            preVatican2 = "Error has no rights. Civil states MUST suppress public false religions & heresy (Pius IX, Quanta Cura).",
            vatican2 = "All men have a 'right' to civil immunity in practicing false worship publicly (Dignitatis Humanae §2).",
            analysis = "EXPLICITLY CONDEMNED AS HERESY by Pope Pius IX."
        ),
        MatrixItem(
            topic = "Ecumenism & Interfaith Worship",
            preVatican2 = "STRICTLY FORBIDDEN under pain of mortal sin and apostasy (Pius XI, Mortalium Animos).",
            vatican2 = "Encouraged and praised; common prayer with heretics & pagans promoted (Unitatis Redintegratio).",
            analysis = "PAGAN SACRILEGE & APOSTASY (e.g. Assisi 1986, Pachamama 2019)."
        ),
        MatrixItem(
            topic = "The Holy Sacrifice of the Mass",
            preVatican2 = "True Propitiatory Sacrifice offered on an altar by a valid priest for the living and dead (Trent, Quo Primum).",
            vatican2 = "Protestantized memorial meal around a table, invalid consecration form in vernacular (Novus Ordo 1969).",
            analysis = "INVALID NO-SACRIFICE SERVICE; AN ABOMINATION."
        ),
        MatrixItem(
            topic = "Validation of Heretical Clerics",
            preVatican2 = "Manifest heretics automatically lose all office and jurisdiction (Cum Ex Apostolatus Officio, Canon 188.4).",
            vatican2 = "Heretics retain office and exercise supreme authority over the universal church.",
            analysis = "IMPOSSIBLE UNDER CATHOLIC DOGMA (Sedevacantism)."
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "INFALLIBLE CATHOLIC DOGMA VS. VATICAN II SECT HERESIES",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            ),
            color = GoldBright
        )

        items.forEach { item ->
            MatrixRowCard(item = item)
        }
    }
}

data class MatrixItem(
    val topic: String,
    val preVatican2: String,
    val vatican2: String,
    val analysis: String
)

@Composable
fun MatrixRowCard(item: MatrixItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
        border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("matrix_row_${item.topic.lowercase().replace(" ", "_")}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.topic,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldBright
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = GoldSacred
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Pre-1958: ${item.preVatican2}",
                style = MaterialTheme.typography.bodySmall,
                color = ParchmentLight
            )

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HorizontalDivider(color = GoldSacred.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 6.dp))
                    Text(
                        text = "Vatican II: ${item.vatican2}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CrimsonBurgundy
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color(0xFF2A151A),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, CrimsonBurgundy.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "Critical Note: ${item.analysis}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = GoldBright,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeceptionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCathedralSurface),
        border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GoldBright,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldBright
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = GoldSacred.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                color = ParchmentLight
            )
        }
    }
}

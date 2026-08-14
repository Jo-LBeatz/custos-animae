package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.IlluminatedDivider
import com.example.ui.theme.*

@Composable
fun TraditionalFastingGuideView(
    isLightTheme: Boolean,
    cardBg: Color,
    cardBorder: Color,
    goldAccent: Color,
    textNormal: Color,
    textMuted: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isLightTheme) Color(0xFFFAF6EE) else Color(0xFF1B1917)
            ),
            border = BorderStroke(1.5.dp, GoldSacred)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "TRADITIONAL FASTING & ABSTINENCE",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = goldAccent
                )
                Text(
                    text = "1954 Roman Rite Discipline & Apostolic Observance",
                    style = MaterialTheme.typography.labelMedium,
                    color = textMuted
                )
                Spacer(modifier = Modifier.height(10.dp))
                IlluminatedDivider()
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "The Roman Catholic Church prior to the disruptions of Vatican II maintained a strict, venerable discipline of bodily mortification, rooted in the Apostles and codified under the 1917 Code of Canon Law (Canons 1250–1254).",
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                    color = textNormal
                )
            }
        }

        // Section 1: Apostolic Foundation - The Didache
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "I. The Apostolic Foundation: The Didache",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = goldAccent
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Teaching of the Twelve Apostles (c. 70–100 A.D.)",
                    style = MaterialTheme.typography.labelSmall,
                    color = textMuted
                )

                Spacer(modifier = Modifier.height(10.dp))
                IlluminatedDivider()
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLightTheme) Color(0xFFF4EDE2) else Color(0xFF24201A)
                    ),
                    border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Didache, Chapter 8:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = goldAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "“Let not your fasts be with the hypocrites; for they fast on the second and fifth days of the week [Mondays and Thursdays]; but fast on the fourth day [Wednesday] and the Preparation Day [Friday].”",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 20.sp
                            ),
                            color = textNormal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                FastingBulletPoint(
                    title = "Wednesday (Feria Quarta):",
                    desc = "Observed by the Apostles in mournful memory of the betrayal of Our Lord Jesus Christ by Judas Iscariot for thirty pieces of silver (Spy Wednesday).",
                    goldAccent = goldAccent,
                    textNormal = textNormal
                )

                Spacer(modifier = Modifier.height(8.dp))

                FastingBulletPoint(
                    title = "Friday (Feria Sexta / Parasceve):",
                    desc = "Observed in perpetual memory and sorrow for the bitter Passion, Agony, and Crucifixion of Jesus Christ upon Mount Calvary.",
                    goldAccent = goldAccent,
                    textNormal = textNormal
                )
            }
        }

        // Section 2: 1917 Canon Law Definitions (1954 Rubrics)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "II. Pre-Vatican II Definitions (Canons 1250–1254)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = goldAccent
                )
                Spacer(modifier = Modifier.height(10.dp))
                IlluminatedDivider()
                Spacer(modifier = Modifier.height(10.dp))

                // Law of Abstinence
                RuleDefinitionCard(
                    ruleTitle = "The Law of Abstinence (Lex Abstinentiae — Canon 1250)",
                    whoBinds = "Binds all baptized Catholics from completion of 7th year of age until death.",
                    whatItRequires = "Forbids the eating of flesh meat and soup, gravy, or broth made from meat. Eggs, dairy products (milk, butter, cheese — lacticinia), and seasonings made from animal fats are permitted.",
                    isLightTheme = isLightTheme,
                    goldAccent = goldAccent,
                    textNormal = textNormal,
                    textMuted = textMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Law of Fasting
                RuleDefinitionCard(
                    ruleTitle = "The Law of Fasting (Lex Ieiunii — Canon 1251)",
                    whoBinds = "Binds all baptized Catholics from completion of 21st year until beginning of 60th year (ages 21–59).",
                    whatItRequires = "Allows only ONE full meal a day (the principal meal). Two small meatless collations (frustulum) are permitted to maintain strength, which together must strictly not equal another full meal. Eating solid food between meals is forbidden; liquids (water, coffee, tea, fruit juices) do not break the fast.",
                    isLightTheme = isLightTheme,
                    goldAccent = goldAccent,
                    textNormal = textNormal,
                    textMuted = textMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Complete vs Partial Abstinence
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLightTheme) Color(0xFFFAF8F5) else Color(0xFF1E1B18)
                    ),
                    border = BorderStroke(1.dp, cardBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Complete vs. Partial Abstinence",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = goldAccent
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• Complete Abstinence: Flesh meat is prohibited at all meals without exception.\n• Partial Abstinence: Flesh meat is permitted once a day at the principal meal only (strictly prohibited at the two collations).",
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                            color = textNormal
                        )
                    }
                }
            }
        }

        // Section 3: Comprehensive Schedule of Days (1954 Calendar)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "III. Complete Schedule of Traditional Fast Days (1954)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = goldAccent
                )
                Spacer(modifier = Modifier.height(10.dp))
                IlluminatedDivider()
                Spacer(modifier = Modifier.height(10.dp))

                DayCategoryBadge(
                    categoryTitle = "A. Days of Complete Fast & Complete Abstinence",
                    badgeColor = CrimsonBurgundy,
                    details = listOf(
                        "Ash Wednesday (Caput Ieiunii)",
                        "All Fridays and Saturdays of Lent (until Holy Saturday noon)",
                        "The Four Quattuor Tempora (Ember Days):",
                        "  • Lenten Ember Days (Wed, Fri, Sat after 1st Sunday of Lent)",
                        "  • Whitsun Ember Days (Wed, Fri, Sat of Pentecost Octave)",
                        "  • September Ember Days (Wed, Fri, Sat after Holy Cross, Sept 14)",
                        "  • Advent Ember Days (Wed, Fri, Sat after St. Lucy, Dec 13)",
                        "The Four Traditional Fast-Bound Vigils:",
                        "  • Vigil of Christmas (December 24)",
                        "  • Vigil of Pentecost (Whitsun Eve)",
                        "  • Vigil of the Assumption of the BVM (August 14)",
                        "  • Vigil of All Saints (October 31)"
                    ),
                    goldAccent = goldAccent,
                    textNormal = textNormal
                )

                Spacer(modifier = Modifier.height(14.dp))

                DayCategoryBadge(
                    categoryTitle = "B. Days of Fast & Partial Abstinence",
                    badgeColor = Color(0xFF6B4226),
                    details = listOf(
                        "All Mondays, Tuesdays, Wednesdays, and Thursdays of Lent (meat permitted once at the principal meal only)."
                    ),
                    goldAccent = goldAccent,
                    textNormal = textNormal
                )

                Spacer(modifier = Modifier.height(14.dp))

                DayCategoryBadge(
                    categoryTitle = "C. Days of Pure Abstinence (No Quantity Fast)",
                    badgeColor = Color(0xFF2C3E50),
                    details = listOf(
                        "Every Friday of the entire year (unless a Holy Day of Obligation outside of Lent occurs, per Canon 1252.4)."
                    ),
                    goldAccent = goldAccent,
                    textNormal = textNormal
                )
            }
        }

        // Section 4: Eucharistic Fast (Pope Pius XII)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "IV. The Eucharistic Fast (Pope Pius XII)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = goldAccent
                )
                Text(
                    text = "Apostolic Constitution Christus Dominus (1953) & Sacram Communionem (1957)",
                    style = MaterialTheme.typography.labelSmall,
                    color = textMuted
                )
                Spacer(modifier = Modifier.height(10.dp))
                IlluminatedDivider()
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "1. Natural Water and True Medicine: Never break the Eucharistic fast under any circumstance.\n" +
                           "2. Solid Food & Alcoholic Beverages: Minimum 3 hours prior to the reception of Holy Communion.\n" +
                           "3. Non-Alcoholic Beverages: Minimum 1 hour prior to Holy Communion.\n" +
                           "4. Traditional Immemorial Fast: Fasting from midnight remains the most venerable, praiseworthy, and recommended custom for morning Holy Mass.",
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                    color = textNormal
                )
            }
        }

        // Section 5: The Three Spiritual Ends of Fasting
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "V. The Three Ends of Holy Fasting",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = goldAccent
                )
                Text(
                    text = "St. Thomas Aquinas, Summa Theologiae (II-II, q. 147, art. 1)",
                    style = MaterialTheme.typography.labelSmall,
                    color = textMuted
                )
                Spacer(modifier = Modifier.height(10.dp))
                IlluminatedDivider()
                Spacer(modifier = Modifier.height(10.dp))

                FastingBulletPoint(
                    title = "1. Mortification of the Flesh:",
                    desc = "Ad concupiscentiae carnis refrenationem — To subdue the passions, bridle concupiscence, and preserve bodily chastity.",
                    goldAccent = goldAccent,
                    textNormal = textNormal
                )

                Spacer(modifier = Modifier.height(8.dp))

                FastingBulletPoint(
                    title = "2. Elevation of the Soul:",
                    desc = "Ad mentem facilius erigendam — To lift the intellect and will freely to divine contemplation, prayer, and celestial realities.",
                    goldAccent = goldAccent,
                    textNormal = textNormal
                )

                Spacer(modifier = Modifier.height(8.dp))

                FastingBulletPoint(
                    title = "3. Satisfaction for Sins:",
                    desc = "Ad satisfactionem pro peccatis — To render temporal satisfaction and atonement to God's offended justice for past sins.",
                    goldAccent = goldAccent,
                    textNormal = textNormal
                )
            }
        }
    }
}

@Composable
private fun FastingBulletPoint(
    title: String,
    desc: String,
    goldAccent: Color,
    textNormal: Color
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = goldAccent
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
            color = textNormal
        )
    }
}

@Composable
private fun RuleDefinitionCard(
    ruleTitle: String,
    whoBinds: String,
    whatItRequires: String,
    isLightTheme: Boolean,
    goldAccent: Color,
    textNormal: Color,
    textMuted: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLightTheme) Color(0xFFF9F6F0) else Color(0xFF1E1D22)
        ),
        border = BorderStroke(1.dp, GoldSacred.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = ruleTitle,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = goldAccent
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "• Who is bound: $whoBinds",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = textMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "• Requirements: $whatItRequires",
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = textNormal
            )
        }
    }
}

@Composable
private fun DayCategoryBadge(
    categoryTitle: String,
    badgeColor: Color,
    details: List<String>,
    goldAccent: Color,
    textNormal: Color
) {
    Column {
        Surface(
            color = badgeColor,
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = categoryTitle,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = GoldBright,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        details.forEach { detail ->
            Text(
                text = if (detail.startsWith("  •")) detail else "• $detail",
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = textNormal,
                modifier = Modifier.padding(vertical = 1.dp)
            )
        }
    }
}

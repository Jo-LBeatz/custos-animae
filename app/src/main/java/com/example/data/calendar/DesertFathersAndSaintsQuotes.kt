package com.example.data.calendar

import java.util.Calendar

data class TraditionalWisdomQuote(
    val id: String,
    val quoteText: String,
    val saintOrFatherName: String,
    val era: String, // e.g. "Desert Father (4th Century)", "Pre-Vatican II Doctor of the Church (18th c.)"
    val sourceWork: String, // e.g. "Sayings of the Desert Fathers (Apophthegmata Patrum)"
    val ruleApplication: String, // Practical application for the Rule of Life
    val disciplineCategory: String // e.g. "Silence & Custody of Tongue", "Fasting & Mortification", "Prayer & Vigilance"
)

object DesertFathersAndSaintsQuotes {

    val quotesList = listOf(
        TraditionalWisdomQuote(
            id = "df_anthony_1",
            quoteText = "A time is coming when men will go mad, and when they see someone who is not mad, they will attack him, saying: 'You are mad; you are not like us.'",
            saintOrFatherName = "St. Anthony the Great",
            era = "Desert Father (c. 251–356)",
            sourceWork = "Sayings of the Desert Fathers (Apophthegmata Patrum)",
            ruleApplication = "Stand firm in your daily Rule of Life regardless of modern worldly pressures. Perseverance in hidden virtue protects the soul.",
            disciplineCategory = "Spiritual Vigilance & Uncompromising Faith"
        ),
        TraditionalWisdomQuote(
            id = "df_arsenius_1",
            quoteText = "Many times I have repented of having spoken, but never of having kept silence.",
            saintOrFatherName = "St. Arsenius the Great",
            era = "Desert Father (c. 354–449)",
            sourceWork = "Apophthegmata Patrum",
            ruleApplication = "Practice custody of the tongue today. Avoid idle gossip, unnecessary complaints, and uncharitable speech.",
            disciplineCategory = "Silence & Custody of the Tongue"
        ),
        TraditionalWisdomQuote(
            id = "df_poemen_1",
            quoteText = "If you are silent, you will find rest wherever you live.",
            saintOrFatherName = "St. Poemen the Great",
            era = "Desert Father (5th Century)",
            sourceWork = "Sayings of the Desert Fathers",
            ruleApplication = "Cultivate an interior cloister in your mind. Maintain peace during interruptions and noise.",
            disciplineCategory = "Interior Silence & Peace"
        ),
        TraditionalWisdomQuote(
            id = "df_cassian_1",
            quoteText = "The end of our profession is the kingdom of God, but our immediate aim or goal is purity of heart, without which no one can see the Lord.",
            saintOrFatherName = "St. John Cassian",
            era = "Monastic Father & Doctor (c. 360–435)",
            sourceWork = "Conferences of the Desert Fathers (Collationes, Conf. I)",
            ruleApplication = "Purify your intentions before every prayer and duty. Do nothing for vain praise or temporal reward.",
            disciplineCategory = "Purity of Heart & Intention"
        ),
        TraditionalWisdomQuote(
            id = "df_macarius_1",
            quoteText = "This is the greatest work of a monk: to take the blame for his own faults before God and to expect temptation until his last breath.",
            saintOrFatherName = "St. Macarius the Great",
            era = "Desert Father of Scetis (c. 300–391)",
            sourceWork = "Spiritual Homilies of St. Macarius",
            ruleApplication = "Accept personal responsibility without excusing your faults. Use your daily Examination of Conscience to repent quickly.",
            disciplineCategory = "Humility & Self-Examination"
        ),
        TraditionalWisdomQuote(
            id = "df_moses_1",
            quoteText = "Go, sit in your cell, and your cell will teach you everything.",
            saintOrFatherName = "St. Moses the Black",
            era = "Desert Father (c. 330–405)",
            sourceWork = "Apophthegmata Patrum",
            ruleApplication = "Respect your designated times of prayer and study. Do not flee difficult tasks or seek restless distractions.",
            disciplineCategory = "Monastic Stability & Focus"
        ),
        TraditionalWisdomQuote(
            id = "st_ephrem_1",
            quoteText = "O Lord and Master of my life, take from me the spirit of sloth, despair, lust of power, and idle talk. Give rather the spirit of chastity, humility, patience, and love to Thy servant.",
            saintOrFatherName = "St. Ephrem the Syrian",
            era = "Father & Doctor of the Church (c. 306–373)",
            sourceWork = "Lenten Prayer of St. Ephrem",
            ruleApplication = "Offer a prostration or knee-genuflection during your morning prayers while making this act of humility.",
            disciplineCategory = "Daily Penance & Prayer"
        ),
        TraditionalWisdomQuote(
            id = "st_benedict_1",
            quoteText = "Idleness is the enemy of the soul; and therefore the brethren ought to be employed in manual labor at certain hours, and in devout reading at others.",
            saintOrFatherName = "St. Benedict of Nursia",
            era = "Father of Western Monasticism (c. 480–548)",
            sourceWork = "Rule of St. Benedict (Regula Benedicti, Ch. 48)",
            ruleApplication = "Organize your schedule strictly between work, sacred reading (Lectio Divina), and prayer. Eliminate wasted time.",
            disciplineCategory = "Sanctification of Time & Labor"
        ),
        TraditionalWisdomQuote(
            id = "st_chrysostom_1",
            quoteText = "Fasting of the body is food for the soul. As corporeal food fattens the body, so fasting strengthens the soul and gives it a joyful flight.",
            saintOrFatherName = "St. John Chrysostom",
            era = "Father & Doctor of the Church (c. 347–407)",
            sourceWork = "Homilies on Genesis (Homilia VIII)",
            ruleApplication = "Observe abstinence or moderate mortification at meals today as an offering for holy purity.",
            disciplineCategory = "Fasting & Bodily Mortification"
        ),
        TraditionalWisdomQuote(
            id = "st_jerome_1",
            quoteText = "Ignorance of the Scriptures is ignorance of Christ. Begin now to be what you will be hereafter.",
            saintOrFatherName = "St. Jerome",
            era = "Father & Doctor of the Church (c. 347–420)",
            sourceWork = "Commentary on Isaiah & Epistolae",
            ruleApplication = "Dedicating at least 10 minutes to reading the Holy Gospels or Douay-Rheims Scriptures daily.",
            disciplineCategory = "Lectio Divina & Sacred Study"
        ),
        TraditionalWisdomQuote(
            id = "st_alphonsus_1",
            quoteText = "He who prays is certainly saved; he who does not pray is certainly damned. All the saints became saints by prayer.",
            saintOrFatherName = "St. Alphonsus Liguori",
            era = "Doctor of the Church (1696–1787)",
            sourceWork = "The Great Means of Salvation and of Perfection",
            ruleApplication = "Never omit your daily morning and evening prayers. Prayer is the indispensable anchor of the Rule of Life.",
            disciplineCategory = "Persevering Prayer"
        ),
        TraditionalWisdomQuote(
            id = "st_john_cross_1",
            quoteText = "In the evening of life, we will be judged on love alone. Restrain your tongue and your thoughts, and keep your heart directed towards God.",
            saintOrFatherName = "St. John of the Cross",
            era = "Doctor of the Church (1542–1591)",
            sourceWork = "Sayings of Light and Love (Dichos de Luz y Amor)",
            ruleApplication = "Examine your thoughts before acting. Direct all affections and sufferings toward Our Lord Jesus Christ.",
            disciplineCategory = "Custody of the Heart & Love"
        ),
        TraditionalWisdomQuote(
            id = "st_teresa_1",
            quoteText = "Let nothing disturb you, let nothing frighten you. All things are passing away: God never changes. Patience obtains all things.",
            saintOrFatherName = "St. Teresa of Avila",
            era = "Doctor of the Church (1515–1582)",
            sourceWork = "The Bookmark Prayer of St. Teresa",
            ruleApplication = "When anxieties arise during the day, make an act of trust in Divine Providence and return to your duty.",
            disciplineCategory = "Patience & Trust in God"
        ),
        TraditionalWisdomQuote(
            id = "st_francis_sales_1",
            quoteText = "Have patience with all things, but first of all with yourself. Do not lose courage in considering your own imperfections, but instantly set about remedying them.",
            saintOrFatherName = "St. Francis de Sales",
            era = "Doctor of the Church (1567–1622)",
            sourceWork = "Introduction to the Devout Life (Philothea)",
            ruleApplication = "If you fail in a resolution today, do not fall into despair. Make an act of contrition and quietly resume your Rule.",
            disciplineCategory = "Gentle Perseverance & Contrition"
        ),
        TraditionalWisdomQuote(
            id = "st_ignatius_1",
            quoteText = "Few people understand what God would accomplish in them if they were to abandon themselves entirely into His hands and allow His grace to mold them.",
            saintOrFatherName = "St. Ignatius of Loyola",
            era = "Founder of the Society of Jesus (1491–1556)",
            sourceWork = "Spiritual Exercises (Exercitia Spiritualia)",
            ruleApplication = "Perform your noon and evening Examination of Conscience (Examen) to discern God's promptings in your soul.",
            disciplineCategory = "Abandonment to Divine Providence"
        ),
        TraditionalWisdomQuote(
            id = "st_aquinas_1",
            quoteText = "To one who has faith, no explanation is necessary. To one without faith, no explanation is possible. Love follows knowledge.",
            saintOrFatherName = "St. Thomas Aquinas",
            era = "Angelic Doctor of the Church (1225–1274)",
            sourceWork = "Summa Theologiae (Secunda Secundae)",
            ruleApplication = "Spend time studying traditional Catholic catechism and theology to deepen your love for the Lord.",
            disciplineCategory = "Intellectual & Spiritual Formation"
        ),
        TraditionalWisdomQuote(
            id = "st_catherine_siena_1",
            quoteText = "Build a cell inside your mind, from which you can never flee. Nothing great was ever done without much enduring.",
            saintOrFatherName = "St. Catherine of Siena",
            era = "Doctor of the Church (1347–1380)",
            sourceWork = "The Dialogue of Divine Providence",
            ruleApplication = "When in crowded or busy environments, retreat mentally into the interior cell of your heart to speak with God.",
            disciplineCategory = "Interior Cell & Fortitude"
        ),
        TraditionalWisdomQuote(
            id = "st_vianney_1",
            quoteText = "If we really understood the Mass, we would die of joy. Prayer is to the spiritual life what the breathing of air is to the body.",
            saintOrFatherName = "St. Jean-Marie Vianney (Curé of Ars)",
            era = "Patron of Parish Priests (1786–1859)",
            sourceWork = "Sermons of the Curé of Ars",
            ruleApplication = "Assist at the Holy Sacrifice of the Mass with profound reverence and make a Spiritual Communion daily.",
            disciplineCategory = "Holy Sacrifice of the Mass & Prayer"
        ),
        TraditionalWisdomQuote(
            id = "st_montfort_1",
            quoteText = "To die to oneself daily and to live only for God through Mary—this is the secret of the saints.",
            saintOrFatherName = "St. Louis-Marie Grignion de Montfort",
            era = "Apostle of the Holy Rosary (1673–1716)",
            sourceWork = "True Devotion to the Blessed Virgin Mary",
            ruleApplication = "Recite 5 decades of the Holy Rosary daily and offer all your actions through the Immaculate Heart of Mary.",
            disciplineCategory = "Marian Consecration & Self-Denial"
        ),
        TraditionalWisdomQuote(
            id = "st_bernard_1",
            quoteText = "Notice that the vessel does not pour out what it receives until it is full. Be eager to emulate the channel, but do not be a canal that lets water flow straight through.",
            saintOrFatherName = "St. Bernard of Clairvaux",
            era = "Mellifluous Doctor of the Church (1090–1153)",
            sourceWork = "Sermons on the Song of Songs (Sermo 18)",
            ruleApplication = "Ensure your own spiritual life is nourished through prayer before attempting to instruct or correct others.",
            disciplineCategory = "Interior Abundance & Discretion"
        ),
        TraditionalWisdomQuote(
            id = "st_athanasius_1",
            quoteText = "You will not see the Devil if your cell is kept closed by prayer and your heart guarded by watchfulness.",
            saintOrFatherName = "St. Athanasius the Great",
            era = "Father & Doctor of the Church (c. 296–373)",
            sourceWork = "Life of St. Anthony the Great (Vita Antonii)",
            ruleApplication = "Guard your senses against immoral images, media, and vain entertainment that weaken spiritual resolve.",
            disciplineCategory = "Custody of the Senses"
        ),
        TraditionalWisdomQuote(
            id = "st_therese_1",
            quoteText = "Holiness consists simply in doing God's will, and being just what God wants us to be in the small duties of each moment.",
            saintOrFatherName = "St. Thérèse of Lisieux",
            era = "Doctor of the Church (1873–1897)",
            sourceWork = "Story of a Soul (L'Histoire d'une Âme)",
            ruleApplication = "Fulfill the smallest routine duty of your Rule with extraordinary love and total precision.",
            disciplineCategory = "Fidelity to Small Daily Duties"
        ),
        TraditionalWisdomQuote(
            id = "st_bellarmine_1",
            quoteText = "If you are wise, understand that you have been created for the glory of God and your own eternal salvation.",
            saintOrFatherName = "St. Robert Bellarmine",
            era = "Doctor of the Church (1542–1621)",
            sourceWork = "The Mind's Ascent to God (De Ascensione Mentis in Deum)",
            ruleApplication = "Begin your day by offering your first thoughts, actions, and sufferings for God's greater glory.",
            disciplineCategory = "Ultimate Purpose & First Motion"
        ),
        TraditionalWisdomQuote(
            id = "st_pachomius_1",
            quoteText = "He who seeks the Lord with a pure heart and keeps the rule given to him shall never stumble, for the angels guard his footsteps.",
            saintOrFatherName = "St. Pachomius the Great",
            era = "Founder of Cenobitic Monasticism (c. 292–348)",
            sourceWork = "Monastic Instructions & Catechesis",
            ruleApplication = "Refuse to make arbitrary exceptions to your spiritual rule when feeling slothful or tired.",
            disciplineCategory = "Strict Observance of the Rule"
        ),
        TraditionalWisdomQuote(
            id = "st_mary_egypt_1",
            quoteText = "God is my witness that I fled into the wilderness to slay my passions, and grace alone sustained my soul in solitude.",
            saintOrFatherName = "St. Mary of Egypt",
            era = "Desert Penitent (c. 344–421)",
            sourceWork = "Life of St. Mary of Egypt by St. Sophronius",
            ruleApplication = "Embrace penance gladly as the antidote to sinful attachments and bodily cravings.",
            disciplineCategory = "Penance & Conversion of Heart"
        ),
        TraditionalWisdomQuote(
            id = "st_gregory_great_1",
            quoteText = "The holy man is not one who never falls, but one who rises immediately every time he stumbles.",
            saintOrFatherName = "St. Gregory the Great",
            era = "Pope & Doctor of the Church (c. 540–604)",
            sourceWork = "Moralia in Job",
            ruleApplication = "If you miss a scheduled prayer or duty, do not wallow in discouragement. Reset instantly with humility.",
            disciplineCategory = "Prompt Recovery & Humility"
        ),
        TraditionalWisdomQuote(
            id = "st_alcantara_1",
            quoteText = "If you wish to reform the world, begin with yourself; otherwise your labor will be in vain.",
            saintOrFatherName = "St. Peter of Alcantara",
            era = "Franciscan Reformer & Mystic (1499–1562)",
            sourceWork = "Treatise on Prayer and Meditation",
            ruleApplication = "Focus your spiritual energy on mortifying your own flaws rather than criticizing those around you.",
            disciplineCategory = "Self-Reformation First"
        ),
        TraditionalWisdomQuote(
            id = "st_rose_lima_1",
            quoteText = "Apart from the Cross, there is no other ladder by which we may get to heaven.",
            saintOrFatherName = "St. Rose of Lima",
            era = "Dominican Tertiary & Penitent (1586–1617)",
            sourceWork = "Maxims and Sayings of St. Rose",
            ruleApplication = "Accept daily contradictions, inconveniences, and bodily pains as precious splinters of Christ's Cross.",
            disciplineCategory = "Love of the Holy Cross"
        ),
        TraditionalWisdomQuote(
            id = "st_damascus_1",
            quoteText = "Prayer is the raising of one's mind and heart to God or the requesting of good things from God.",
            saintOrFatherName = "St. John of Damascus",
            era = "Father & Doctor of the Church (c. 676–749)",
            sourceWork = "An Exact Exposition of the Orthodox Faith",
            ruleApplication = "Frequently pause throughout the day to elevate your thoughts in brief ejaculatory prayers to God.",
            disciplineCategory = "Continuous Elevation to God"
        ),
        TraditionalWisdomQuote(
            id = "st_agatha_1",
            quoteText = "My mind is established in Christ; no earthly threat can shatter my faith or alter my devotion.",
            saintOrFatherName = "St. Agatha of Sicily",
            era = "Early Christian Virgin & Martyr (c. 231–251)",
            sourceWork = "Acts of St. Agatha & Roman Breviary",
            ruleApplication = "Keep your mind fixed on the eternal crown during times of earthly trial or temptation.",
            disciplineCategory = "Unshakable Faith"
        ),
        TraditionalWisdomQuote(
            id = "st_augustine_1",
            quoteText = "Thou hast made us for Thyself, O Lord, and our heart is restless until it rest in Thee.",
            saintOrFatherName = "St. Augustine of Hippo",
            era = "Father & Doctor of the Church (354–430)",
            sourceWork = "Confessions (Confessiones, Lib I)",
            ruleApplication = "Turn away from temporal pleasures and seek your ultimate joy and rest in Our Lord alone.",
            disciplineCategory = "Resting in God Alone"
        )
    )

    fun getDailyQuoteForDate(dayOffset: Int = 0): TraditionalWisdomQuote {
        val cal = Calendar.getInstance()
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR) + dayOffset
        val index = (dayOfYear % quotesList.size + quotesList.size) % quotesList.size
        return quotesList[index]
    }

    fun getQuoteById(id: String): TraditionalWisdomQuote? {
        return quotesList.find { it.id == id }
    }
}

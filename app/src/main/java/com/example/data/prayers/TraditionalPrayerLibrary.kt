package com.example.data.prayers

data class PrayerItem(
    val id: String,
    val title: String,
    val category: String,
    val subtitle: String = "",
    val englishText: String,
    val latinText: String = "",
    val notes: String = "",
    val subsection: String = ""
)

data class RosaryMystery(
    val number: Int,
    val title: String,
    val fruit: String,
    val meditation: String,
    val latinTitle: String = ""
)

data class RosarySet(
    val category: String, // Joyful, Sorrowful, Glorious
    val days: String,
    val mysteries: List<RosaryMystery>
)

object TraditionalPrayerLibrary {

    val morningPrayers = listOf(
        PrayerItem(
            id = "m_sign_of_cross",
            title = "Morning Adoration & Trinity Invocation",
            category = "Morning Prayers",
            subtitle = "Opening the day in the Name of the Most Holy Trinity",
            englishText = """In the name of the Father, and of the Son, and of the Holy Ghost. Amen.

Blessed be the holy, undivided, and Most Sacred Trinity, now and forever, world without end. Amen.

O Lord God Almighty, Who hast safely brought us to the beginning of this day: defend us in the same by Thy power; that during this day we may fall into no sin, but that all our words, thoughts, and works may be directed to the doing of Thy holy law. Through Our Lord Jesus Christ. Amen.""",
            latinText = """In nomine Patris, et Filii, et Spiritus Sancti. Amen.

Benedicta sit sancta et individua Trinitas, nunc et semper, et in saecula saeculorum. Amen.

Domine Deus omnipotens, qui ad principium huius diei nos pervenire fecisti: tua nos hodie salva virtute; ut ad nullum declinemus peccatum, sed semper ad tuam iustitiam faciendam nostra procedant eloquia, dirigantur cogitationes et opera. Per Dominum nostrum Iesum Christum. Amen."""
        ),
        PrayerItem(
            id = "m_offering",
            title = "Morning Offering to the Sacred Heart",
            category = "Morning Prayers",
            subtitle = "Consecrating all prayers, works, joys, and sufferings of the day",
            englishText = """O Jesus, through the Immaculate Heart of Mary, I offer Thee my prayers, works, joys, and sufferings of this day, for all the intentions of Thy Sacred Heart, in union with the Holy Sacrifice of the Mass throughout the world, in reparation for my sins, for the intentions of all my relatives and friends, and in particular for the salvation of souls. Amen.""",
            latinText = """O Iesu, per Immaculatum Cor Mariae, offero Tibi orationes, opera, gaudia et passiones huius diei, pro omnibus intentionibus Sacratissimi Cordis Tui, in unione cum Sancto Missae Sacrificio ubique terrarum celebrato, in reparationem peccatorum meorum, et pro intentionibus Sanctorum Pontificum. Amen."""
        ),
        PrayerItem(
            id = "m_pater_ave_creed",
            title = "Daily Morning Foundation (Pater, Ave, Credo)",
            category = "Morning Prayers",
            subtitle = "Our Father, Hail Mary, and Apostles' Creed",
            englishText = """OUR FATHER, Who art in heaven, hallowed be Thy name; Thy kingdom come; Thy will be done on earth as it is in heaven. Give us this day our daily bread; and forgive us our trespasses as we forgive those who trespass against us; and lead us not into temptation, but deliver us from evil. Amen.

HAIL MARY, full of grace, the Lord is with thee; blessed art thou among women, and blessed is the fruit of thy womb, Jesus. Holy Mary, Mother of God, pray for us sinners, now and at the hour of our death. Amen.

I BELIEVE in God, the Father Almighty, Creator of heaven and earth; and in Jesus Christ, His only Son, my Lord: Who was conceived by the Holy Ghost, born of the Virgin Mary, suffered under Pontius Pilate, was crucified, died, and was buried. He descended into hell; the third day He arose again from the dead; He ascended into heaven, sitteth at the right hand of God the Father Almighty; from thence He shall come to judge the living and the dead. I believe in the Holy Ghost, the Holy Catholic Church, the communion of saints, the forgiveness of sins, the resurrection of the body, and life everlasting. Amen.""",
            latinText = """PATER NOSTER, qui es in caelis, sanctificetur nomen tuum. Adveniat regnum tuum. Fiat voluntas tua, sicut in caelo et in terra. Panem nostrum cotidianum da nobis hodie, et dimitte nobis debita nostra sicut et nos dimittimus debitoribus nostris. Et ne nos inducas in tentationem, sed libera nos a malo. Amen.

AVE MARIA, gratia plena, Dominus tecum. Benedicta tu in mulieribus, et benedictus fructus ventris tui, Iesus. Sancta Maria, Mater Dei, ora pro nobis peccatoribus, nunc et in hora mortis nostrae. Amen.

CREDO in Deum Patrem omnipotentem, Creatorem caeli et terrae. Et in Iesum Christum, Filium eius unicum, Dominum nostrum: qui conceptus est de Spiritu Sancto, natus ex Maria Virgine, passus sub Pontio Pilato, crucifixus, mortuus, et sepultus: descendit ad inferos; tertia die resurrexit a mortuis; ascendit ad caelos, sedet ad dexteram Dei Patris omnipotentis: inde venturus est iudicare vivos et mortuos. Credo in Spiritum Sanctum, sanctam Ecclesiam catholicam, Sanctorum communionem, remissionem peccatorum, carnis resurrectionem, vitam aeternam. Amen."""
        ),
        PrayerItem(
            id = "act_faith",
            title = "Act of Faith (Actus Fidei)",
            category = "Morning Prayers",
            subtitle = "Firmly professing all revealed truths of the Catholic Church",
            englishText = """O my God, I firmly believe that Thou art one God in three Divine Persons, Father, Son, and Holy Ghost. I believe that Thy Divine Son became man, and died for our sins, and that He will come to judge the living and the dead. I believe these and all the truths which the Holy Catholic Church teaches, because Thou hast revealed them, who canst neither deceive nor be deceived. Amen.""",
            latinText = """Domine Deus, firma fide credo et confiteor omnia et singula quae sancta Ecclesia Catholica credenda proponit, quia Tu, Deus, ea omnia revelasti, qui es aeterna veritas et sapientia quae nec falli nec fallere potest. In hac fide vivere et mori statuo. Amen."""
        ),
        PrayerItem(
            id = "act_hope",
            title = "Act of Hope (Actus Spei)",
            category = "Morning Prayers",
            subtitle = "Trusting in God's promises of pardon, grace, and eternal life",
            englishText = """O my God, relying on Thy almighty power and infinite mercy and promises, I hope to obtain pardon of my sins, the help of Thy grace, and life everlasting, through the merits of Jesus Christ, my Lord and Redeemer. Amen.""",
            latinText = """Domine Deus, spero per gratiam tuam remissionem omnium peccatorum, et post hanc vitam aeternam felicitatem me esse consecuturum: quia Tu promisisti, qui es praepotens, fidelissimus et misericors. In hac spe vivere et mori statuo. Amen."""
        ),
        PrayerItem(
            id = "act_charity",
            title = "Act of Charity (Actus Caritatis)",
            category = "Morning Prayers",
            subtitle = "Loving God above all things and neighbor for His sake",
            englishText = """O my God, I love Thee above all things, with my whole heart and soul, because Thou art all-good and worthy of all love. I love my neighbor as myself for the love of Thee. I forgive all who have injured me, and ask pardon of all whom I have injured. Amen.""",
            latinText = """Domine Deus, amo Te super omnia et propter Te proximum meum sicut me ipsum, quia Tu es summum, infinitum, et perfectissimum bonum, omni dilectione dignum. In hac dilectione vivere et mori statuo. Amen."""
        ),
        PrayerItem(
            id = "guardian_angel",
            title = "Prayer to Guardian Angel (Angele Dei)",
            category = "Morning Prayers",
            subtitle = "Petitioning guidance and protection for the day",
            englishText = """Angel of God, my guardian dear, to whom God's love commits me here, ever this day be at my side, to light and guard, to rule and guide. Amen.""",
            latinText = """Angele Dei, qui custos es mei, me, tibi commissum pietate superna, illumina, custodi, rege et guberna. Amen."""
        ),
        PrayerItem(
            id = "m_st_joseph",
            title = "Morning Prayer to Saint Joseph",
            category = "Morning Prayers",
            subtitle = "Patron of the Universal Church and Protector of Families",
            englishText = """O Holy Joseph, guardian of the Virgin Mother of God and faithful defender of the Holy Family, extend thy protection over us this day. Obtain for us purity of heart, diligence in our duties, and final perseverance in the grace of God. Amen.""",
            latinText = """O Sancte Ioseph, custos Virginis Matris Dei et fidelis defensor Sanctae Familiae, extende super nos hodie protectionem tuam. Impetra nobis puritam cordis, diligentiam in officiis nostris, et finalem in gratia Dei perseverantiam. Amen."""
        ),
        PrayerItem(
            id = "m_st_michael",
            title = "Prayer to St. Michael the Archangel",
            category = "Morning Prayers",
            subtitle = "Invocation for daily spiritual protection and warfare",
            englishText = """St. Michael the Archangel, defend us in battle. Be our protection against the wickedness and snares of the devil. May God rebuke him, we humbly pray, and do thou, O Prince of the Heavenly Host, by the power of God, cast into hell Satan and all the evil spirits who prowl about the world seeking the ruin of souls. Amen.""",
            latinText = """Sancte Michael Archangele, defende nos in praelio; contra nequitiam et insidias diaboli esto praesidium. Imperet illi Deus, supplices deprecamur: tuque, Princeps militiae caelestis, Satanam aliosque spiritus malignos, qui ad perditionem animarum pervagantur in mundo, divina virtute, in infernum detrude. Amen."""
        ),
        PrayerItem(
            id = "prayer_before_work",
            title = "Prayer before work",
            category = "Morning Prayers",
            subtitle = "To St. Joseph the Worker (Pope St. Pius X, 1906)",
            notes = "An indulgenced prayer to St. Joseph, Patron of Workers, asking for grace to work in a spirit of penance, gratitude, order, and devotion.",
            englishText = """Glorious St. Joseph, model of all who are devoted to labor, obtain for me the grace to work in the spirit of penance in expiation of my many sins; to work conscientiously by placing the love of duty above my inclinations; to gratefully and joyfully deem it an honor to employ and to develop by labor the gifts I have received from God; to work methodically, peacefully, in moderation and patience, without ever shrinking from hardship and difficulties; to work above all with purity of intention and detachment from self, having unceasingly before my eyes death and the account I have to render of time lost, talents unused, good omitted, and vain complacency in success, so fatal to the work of God.

All for Jesus, all through Mary, all in imitation of thee, O Patriarch St. Joseph! This shall be my motto in life and in death. Amen.""",
            latinText = """Gloriose Sancte Ioseph, exemplar omnium qui labori sunt addicti, impetra mihi gratiam laborandi in spiritu paenitentiae pro expiatione multorum peccatorum meorum; laborandi conscientia ductum, anteponendo officii amorem propriis inclinationibus; laborandi cum gratitudine et gaudio, honoris loco ducentem adhibere et evolvere per laborem dona a Deo accepta; laborandi cum ordine, pace, moderatione et patience, nunquam succumbentem fatigationi vel difficultatibus; laborandi praesertim cum puritate intentionis et cum abnegatione sui ipsius, semper prae oculis habentem mortem et rationem reddendam de tempore amisso, de talentis inusitatis, de bono omisso, de vana complacentia in successu, tam noxia operi Dei.

Omnia pro Iesu, omnia per Mariam, omnia ad imitationem tuam, O Patriarcha Sancte Ioseph! Hoc erit synthema meum in vita et in morte. Amen."""
        ),
        PrayerItem(
            id = "prayer_before_study",
            title = "Prayer before study",
            category = "Morning Prayers",
            subtitle = "By St. Thomas Aquinas (Doctor Angelicus)",
            notes = "Traditional prayer composed by St. Thomas Aquinas before studying, preaching, or writing, asking for clarity of understanding and subtlety of interpretation.",
            englishText = """Creator of all things, true Source of light and wisdom, lofty Origin of all being, graciously let a ray of Your brilliance penetrate into the darkness of my understanding and take from me the double darkness in which I have been born, an obscurity of both sin and ignorance.

Give me a sharp sense of understanding, a retentive memory, and the ability to grasp things correctly and fundamentally. Grant me the favor of being exact in my explanations, and the ability to express myself with thoroughness and charm.

Point out the beginning, direct the progress, and help in the completion; through Christ Our Lord. Amen.""",
            latinText = """Creator ineffabilis, qui de thesauris sapientiae tuae tres Angelorum hierarchias designasti, et eas super caelum empyreum miro ordine collocasti, et universi partes elegantissime distribuisti:

Tu, inquam, qui verus fons luminis et sapientiae diceris, et supereminens principium, infundere digneris super intellectus mei tenebras tuae radium claritatis, duplices, in quibus natus sum, a me removens tenebras, peccatum scilicet et ignorantiam.

Tu, qui linguas infantium facis disertas, linguae meae erudias et in labiis meis gratiam tuae benedictionis infundas. Da mihi intelligendi acumen, retinendi capacitatem, interpretandi methodum et facilitatem, discendi subtilitatem, loquendi gratiam uberem.

Ingressum instruas, progressum dirigas, egressum compleas: Tu, qui es verus Deus et homo, qui vivis et regnas in saecula saeculorum. Amen."""
        )
    )

    val leoninePrayers = listOf(
        PrayerItem(
            id = "leonine_prayers_complete",
            title = "The Complete Leonine Prayers (Preces Leoninae)",
            category = "Leonine Prayers",
            subtitle = "Prescribed by Pope Leo XIII after Low Mass for the Church & Conversion of Sinners",
            englishText = """In the name of the Father, and of the Son, and of the Holy Ghost. Amen.

[1. Three Hail Marys]
Hail Mary, full of grace, the Lord is with thee. Blessed art thou among women, and blessed is the fruit of thy womb, Jesus. Holy Mary, Mother of God, pray for us sinners, now and at the hour of our death. Amen. (Recited 3 times)

[2. Hail, Holy Queen]
Hail, Holy Queen, Mother of Mercy, our life, our sweetness and our hope. To thee do we cry, poor banished children of Eve; to thee do we send up our sighs, mourning and weeping in this valley of tears. Turn then, most gracious advocate, thine eyes of mercy toward us; and after this our exile, show unto us the blessed fruit of thy womb, Jesus. O clement, O loving, O sweet Virgin Mary.

V. Pray for us, O holy Mother of God.
R. That we may be made worthy of the promises of Christ.

[3. Collect for the Church & Sinners]
Let us pray.
O God, our refuge and our strength, look down with favor upon Thy people who cry to Thee; and through the intercession of the Glorious and Immaculate Virgin Mary, Mother of God, of Saint Joseph her Spouse, of Thy Blessed Apostles Peter and Paul, and of all the Saints, mercifully and graciously hear the prayers which we pour forth for the conversion of sinners, and for the freedom and exultation of Holy Mother the Church. Through the same Christ Our Lord. Amen.

[4. Prayer to Saint Michael the Archangel]
Saint Michael the Archangel, defend us in battle; be our protection against the malice and snares of the devil. May God rebuke him, we humbly pray; and do thou, O Prince of the heavenly host, by the divine power, thrust into hell Satan and all the evil spirits who prowl about the world seeking the ruin of souls. Amen.

[5. Invocation to the Sacred Heart]
V. Most Sacred Heart of Jesus.
R. Have mercy on us. (Recited 3 times)""",
            latinText = """In nomine Patris, et Filii, et Spiritus Sancti. Amen.

[1. Tres Ave Mariae]
Ave Maria, gratia plena, Dominus tecum. Benedicta tu in mulieribus, et benedictus fructus ventris tui, Iesus. Sancta Maria, Mater Dei, ora pro nobis peccatoribus, nunc et in hora mortis nostrae. Amen. (Ter dicatur)

[2. Salve Regina]
Salve, Regina, Mater misericordiae, vita, dulcedo, et spes nostra, salve. Ad te clamamus, exsules filii Evae. Ad te suspiramus, gementes et flentes in hac lacrimarum valle. Eia, ergo, advocata nostra, illos tuos misericordes oculos ad nos converte; et Iesum, benedictum fructum ventris tui, nobis post hoc exsilium ostende. O clemens, O pia, O dulcis Virgo Maria.

V. Ora pro nobis, sancta Dei Genitrice.
R. Ut digni efficiamur promissionibus Christi.

[3. Oratio pro Ecclesia et Peccatoribus]
Oremus.
Deus, refugium nostrum et virtus, populum ad te clamantem propitius respice; et intercedente gloriosa et immaculata Virgine Dei Genitrice Maria, cum beato Ioseph eius Sponso, ac beatis Apostolis tuis Petro et Paulo, et omnibus Sanctis, quas pro conversione peccatorum, pro libertate et exaltatione sanctae Matris Ecclesiae, preces effundimus, misericors et benignus exaudi. Per eundem Christum Dominum nostrum. Amen.

[4. Oratio ad Sanctum Michael]
Sancte Michael Archangele, defende nos in proelio; contra nequitiam et insidias diaboli esto praesidium. Imperet illi Deus, supplices deprecamur: tuque, Princeps militiae caelestis, Satanam aliosque spiritus malignos, qui ad perditionem animarum pervagantur in mundo, divina virtute, in infernum detrude. Amen.

[5. Invocatio ad Cor Iesu]
V. Cor Iesu Sacratissimum.
R. Miserere nobis. (Ter dicatur)"""
        )
    )

    val nightPrayers = listOf(
        PrayerItem(
            id = "n_examination",
            title = "Evening Thanksgiving & Examination of Conscience",
            category = "Night Prayers",
            subtitle = "Reflecting on the graces and shortcomings of the day",
            englishText = """In the name of the Father, and of the Son, and of the Holy Ghost. Amen.

O my God, I thank Thee with all my heart for all the benefits Thou hast bestowed upon me, especially for creating me, preserving me during this day, and redeeming me through Thy Beloved Son.

Grant me Thy divine light to see clearly the sins I have committed this day in thought, word, deed, or omission.

(Pause for a quiet examination of conscience...)

O Lord, forgive me my failings, purify my soul, and grant me true sorrow for having offended Thy infinite goodness. Amen.""",
            latinText = """In nomine Patris, et Filii, et Spiritus Sancti. Amen.

Gratias tibi ago, Domine Deus meus, ex toto corde meo, pro omnibus beneficiis tuis, praesertim quod me creasti, conservasti me hac die, et per Filium tuum me redemisti.

Da mihi lumen divinum ut clare videam peccata quae hac die commisi cogitatione, verbo, opere et omissione.

O Domine, dimitte mihi defectus meos, purifica animam meam, et da mihi veram contritionem quia offendi infinitam bonitatem tuam. Amen."""
        ),
        PrayerItem(
            id = "act_contrition",
            title = "Act of Contrition (Actus Contritionis)",
            category = "Night Prayers",
            subtitle = "Traditional formula of sincere sorrow and firm resolution",
            englishText = """O my God, I am heartily sorry for having offended Thee, and I detest all my sins because of Thy just punishments, but most of all because they offend Thee, my God, who art all-good and deserving of all my love. I firmly resolve, with the help of Thy grace, to sin no more and to avoid the near occasions of sin. Amen.""",
            latinText = """Deus meus, ex toto corde me poenitet omnium meorum peccatorum, eaque detestor, quia peccando, non solum poenas a Te iuste statutas promeritus sum, sed praesertim quia offendi Te, summum bonum, ac dignum qui super omnia ameris. Ideo firmiter propono, adiuvante gratia Tua, de cetero me non peccaturum peccandique occasiones proximas fugiturum. Amen."""
        ),
        PrayerItem(
            id = "anima_christi",
            title = "Anima Christi (Soul of Christ)",
            category = "Night Prayers",
            subtitle = "Traditional devotion of Eucharistic and Passion reflection",
            englishText = """Soul of Christ, sanctify me.
Body of Christ, save me.
Blood of Christ, inebriate me.
Water from the side of Christ, wash me.
Passion of Christ, strengthen me.
O good Jesus, hear me.
Within Thy wounds hide me.
Suffer me not to be separated from Thee.
From the malignant enemy defend me.
In the hour of my death call me,
and bid me come unto Thee,
that with Thy saints I may praise Thee
for ever and ever. Amen.""",
            latinText = """Anima Christi, sanctifica me.
Corpus Christi, salva me.
Sanguis Christi, inebria me.
Aqua lateris Christi, lava me.
Passio Christi, conforta me.
O bone Iesu, exaudi me.
Intra tua vulnera absconde me.
Ne permittas me separari a te.
Ab hoste maligno defende me.
In hora mortis meae voca me.
Et iube me venire ad te,
ut cum Sanctis tuis laudem te
in saecula saeculorum. Amen."""
        ),
        PrayerItem(
            id = "te_lucis",
            title = "Te Lucis Ante Terminum",
            category = "Night Prayers",
            subtitle = "Traditional Compline Night Hymn",
            englishText = """To Thee before the close of day, Creator of the world, we pray, That, with Thy wonted favor, Thou Wouldst be our Guard and Keeper now.

From all ill dreams defend our eyes, From nightly fears and fantasies; Tread under foot our ghostly foe, That no pollution we may know.

O Father, that we ask be done, Through Jesus Christ, Thine only Son, Who, with the Holy Ghost and Thee, Doth live and reign eternally. Amen.""",
            latinText = """Te lucis ante terminum, Rerum Creator, poscimus, Ut pro tua clementia Sis praesul et custodia.

Procul recedant somnia, Et noctium phantasmata; Hostemque nostrum comprime, Ne polluantur corpora.

Praesta, Pater piissime, Patrique compar Unice, Cum Spiritu Paraclito Regnans per omne saeculum. Amen."""
        ),
        PrayerItem(
            id = "n_visita_dwelling",
            title = "Compline Collect for Home & Night Protection",
            category = "Night Prayers",
            subtitle = "Traditional prayer of the Church (Visita, Quaesumus, Domine)",
            englishText = """Visit, we beseech Thee, O Lord, this dwelling, and drive far from it all snares of the enemy; let Thy holy Angels dwell herein to preserve us in peace; and let Thy blessing be always upon us. Through Our Lord Jesus Christ, Thy Son, Who liveth and reigneth with Thee in the unity of the Holy Ghost, God, world without end. Amen.""",
            latinText = """Visita, quaesumus, Domine, habitationem istam, et omnes insidias inimici ab ea longe repelle: Angeli tui sancti habitent in ea, qui nos in pace custodiant; et benedictio tua sit super nos semper. Per Dominum nostrum Iesum Christum Filium tuum, qui tecum vivit et regnat in unitate Spiritus Sancti, Deus, per omnia saecula saeculorum. Amen."""
        ),
        PrayerItem(
            id = "n_in_manus",
            title = "Into Thy Hands (In Manus Tuas)",
            category = "Night Prayers",
            subtitle = "Nightly commendation of spirit to Christ",
            englishText = """Into Thy hands, O Lord, I commend my spirit. Thou hast redeemed us, O Lord, God of truth.

May the Almighty and merciful Lord, Father, Son, and Holy Ghost, bless us and keep us. May the divine assistance remain always with us, and may the souls of the faithful departed, through the mercy of God, rest in peace. Amen.""",
            latinText = """In manus tuas, Domine, commendo spiritum meum. Redemisti nos, Domine, Deus veritatis.

Benedicat et custodiat nos omnipotens et misericors Dominus, Pater, et Filius, et Spiritus Sanctus. Divinum auxilium maneat semper nobiscum, et fidelium animae per misericordiam Dei requiescant in pace. Amen."""
        ),
        PrayerItem(
            id = "n_sub_tuum",
            title = "Night Entrustment to Mary (Sub Tuum Praesidium)",
            category = "Night Prayers",
            subtitle = "The oldest known Marian prayer (3rd Century)",
            englishText = """We fly to thy patronage, O holy Mother of God; despise not our petitions in our necessities, but deliver us always from all dangers, O glorious and blessed Virgin. Amen.""",
            latinText = """Sub tuum praesidium confugimus, Sancta Dei Genetrix; nostras deprecationes ne despicias in necessitatibus, sed a periculis omnibus libera nos semper, Virgo gloriosa et benedicta. Amen."""
        ),
        PrayerItem(
            id = "n_guardian_angel",
            title = "Night Prayer to Guardian Angel",
            category = "Night Prayers",
            subtitle = "Commending sleep and body to heavenly protection",
            englishText = """O Holy Angel, watching over my poor soul and life, abandon me not, a sinner, nor depart from me. Give no place to the evil spirit to overpower me. Shield me during the hours of darkness and present my prayers to the Almighty God. Amen.""",
            latinText = """Sancte Angele, custos animae et corporis mei, ne derelinquas me peccatorem. Ne des locum spiritui maligno ut mihi dominetur. Protege me in horis nocturnis et praesenta orationes meas coram Altissimo Deo. Amen."""
        ),
        PrayerItem(
            id = "n_st_michael",
            title = "Prayer to St. Michael the Archangel",
            category = "Night Prayers",
            subtitle = "Nightly protection against the snares of the enemy during sleep",
            englishText = """St. Michael the Archangel, defend us in battle. Be our protection against the wickedness and snares of the devil. May God rebuke him, we humbly pray, and do thou, O Prince of the Heavenly Host, by the power of God, cast into hell Satan and all the evil spirits who prowl about the world seeking the ruin of souls. Amen.""",
            latinText = """Sancte Michael Archangele, defende nos in praelio; contra nequitiam et insidias diaboli esto praesidium. Imperet illi Deus, supplices deprecamur: tuque, Princeps militiae caelestis, Satanam aliosque spiritus malignos, qui ad perditionem animarum pervagantur in mundo, divina virtute, in infernum detrude. Amen."""
        )
    )

    val angelusAndRegina = listOf(
        PrayerItem(
            id = "angelus",
            title = "The Angelus",
            category = "Angelus & Regina Caeli",
            subtitle = "Recited at 6 AM, 12 PM, and 6 PM outside Paschaltide",
            englishText = """V. The Angel of the Lord declared unto Mary.
R. And she conceived of the Holy Ghost.
Hail Mary, full of grace, the Lord is with thee. Blessed art thou among women, and blessed is the fruit of thy womb, Jesus. Holy Mary, Mother of God, pray for us sinners, now and at the hour of our death. Amen.

V. Behold the handmaid of the Lord.
R. Be it done unto me according to thy word.
Hail Mary, full of grace, the Lord is with thee. Blessed art thou among women, and blessed is the fruit of thy womb, Jesus. Holy Mary, Mother of God, pray for us sinners, now and at the hour of our death. Amen.

V. And the Word was made flesh.
R. And dwelt among us.
Hail Mary, full of grace, the Lord is with thee. Blessed art thou among women, and blessed is the fruit of thy womb, Jesus. Holy Mary, Mother of God, pray for us sinners, now and at the hour of our death. Amen.

V. Pray for us, O Holy Mother of God.
R. That we may be made worthy of the promises of Christ.

Let us pray: Pour forth, we beseech Thee, O Lord, Thy grace into our hearts; that we, to whom the Incarnation of Christ, Thy Son, was made known by the message of an Angel, may by His Passion and Cross be brought to the glory of His Resurrection. Through the same Christ our Lord. Amen.""",
            latinText = """V. Angelus Domini nuntiavit Mariae.
R. Et concepit de Spiritu Sancto.
Ave Maria, gratia plena, Dominus tecum. Benedicta tu in mulieribus, et benedictus fructus ventris tui, Iesus. Sancta Maria, Mater Dei, ora pro nobis peccatoribus, nunc et in hora mortis nostrae. Amen.

V. Ecce ancilla Domini.
R. Fiat mihi secundum verbum tuum.
Ave Maria, gratia plena, Dominus tecum. Benedicta tu in mulieribus, et benedictus fructus ventris tui, Iesus. Sancta Maria, Mater Dei, ora pro nobis peccatoribus, nunc et in hora mortis nostrae. Amen.

V. Et Verbum caro factum est.
R. Et habitavit in nobis.
Ave Maria, gratia plena, Dominus tecum. Benedicta tu in mulieribus, et benedictus fructus ventris tui, Iesus. Sancta Maria, Mater Dei, ora pro nobis peccatoribus, nunc et in hora mortis nostrae. Amen.

V. Ora pro nobis, Sancta Dei Genetrix.
R. Ut digni efficiamur promissionibus Christi.

Oremus: Gratiam tuam, quaesumus Domine, mentibus nostris infunde: ut qui, Angelo nuntiante, Christi Filii tui Incarnationem cognovimus, per Passionem eius et Crucem ad Resurrectionis gloriam perducamur. Per eundem Christum Dominum nostrum. Amen."""
        ),
        PrayerItem(
            id = "regina_caeli",
            title = "Regina Caeli",
            category = "Angelus & Regina Caeli",
            subtitle = "Recited during Paschaltide (Easter to Pentecost)",
            englishText = """V. Queen of Heaven, rejoice, alleluia.
R. For He whom thou didst merit to bear, alleluia.
V. Has risen, as He said, alleluia.
R. Pray for us to God, alleluia.

V. Rejoice and be glad, O Virgin Mary, alleluia.
R. For the Lord has truly risen, alleluia.

Let us pray: O God, who gave joy to the world through the resurrection of Thy Son, Our Lord Jesus Christ, grant we beseech Thee, that through His Mother, the Virgin Mary, we may obtain the joys of everlasting life. Through the same Christ our Lord. Amen.""",
            latinText = """V. Regina caeli, laetare, alleluia.
R. Quia quem meruisti portare, alleluia.
V. Resurrexit, sicut dixit, alleluia.
R. Ora pro nobis Deum, alleluia.

V. Gaude et laetare, Virgo Maria, alleluia.
R. Quia surrexit Dominus vere, alleluia.

Oremus: Deus, qui per resurrectionem Filii tui, Domini nostri Iesu Christi, mundum laetificare dignatus es: praesta, quaesumus; ut per eius Genetricem Virginem Mariam, perpetuae capiamus gaudia vitae. Per eundem Christum Dominum nostrum. Amen."""
        )
    )

    val eucharisticDevotions = listOf(
        PrayerItem(
            id = "adoro_te_devote",
            title = "Adoro Te Devote",
            category = "Eucharistic Devotions",
            subtitle = "Hymn of St. Thomas Aquinas before the Blessed Sacrament",
            englishText = """Godhead here in hiding, whom I do adore,
Masked by these bare shadows, shape and nothing more,
See, Lord, at Thy service low lies here a heart
Lost, all lost in wonder at the God thou art.

Seeing, touching, tasting are in thee deceived;
How says trusty hearing? that shall be believed;
What God's Son has told me, take for truth I do;
Truth Himself speaks truly or there's nothing true.""",
            latinText = """Adoro te devote, latens Deitas,
Quae sub his figuris vere latibitas:
Tibi se cor meum totum subjicit,
Quia te contemplans totum deficit.

Visus, tactus, gustus in te fallitur,
Sed auditu solo tuto creditur.
Credo quidquid dixit Dei Filius:
Nil hoc verbo veritatis verius."""
        ),
        PrayerItem(
            id = "en_ego_suave",
            title = "En Ego, O Bone et Dulcissime Iesu",
            category = "Eucharistic Devotions",
            subtitle = "Prayer Before a Crucifix for Plenary Indulgence",
            englishText = """O good and sweetest Jesus, cast me upon my knees in Thy sight, and with the most fervent desire of my soul, I pray and beseech Thee to impress upon my heart lively sentiments of faith, hope, and charity, with true contrition for my sins and a firm purpose of amendment; while with deep affection and grief of soul I ponder within myself and mentally contemplate Thy five most precious wounds: having before my eyes that which David the prophet long ago spoke in Thy regard concerning Thee: "They have pierced My hands and My feet; they have numbered all My bones." """,
            latinText = """En ego, o bone et dulcissime Iesu, ante conspectum tuum genibus provolutus, maxima animi fervore precor ac obsecro, ut tuos in mentem nostram infundas sensus fidei, spei et caritatis, veramque peccatorum meorum paenitentiam, et voluntatem firmissimam eos emendandi; dum accepi et fletu animi ea quinque vulnera tecum mente revolvo ac contueor, o bone Iesu, ante oculos habens illud quod in te ipse vaticinabatur David propheta: Foderunt manus meas et pedes meos, dinumeraverunt omnia ossa mea."""
        )
    )

    val protectionAndSaints = listOf(
        PrayerItem(
            id = "st_michael_prayer",
            title = "Prayer to St. Michael the Archangel",
            category = "Protection & Saints",
            subtitle = "Sancte Michael Archangele",
            englishText = """St. Michael the Archangel, defend us in battle. Be our protection against the wickedness and snares of the devil. May God rebuke him, we humbly pray, and do thou, O Prince of the Heavenly Host, by the power of God, thrust into hell Satan and all the evil spirits who prowl about the world seeking the ruin of souls. Hear my humble prayer, O glorious Archangel, and obtain for me this request: (here make your request). Amen.""",
            latinText = """Sancte Michael Archangele, defende nos in praelio; contra nequitiam et insidias diaboli esto praesidium. Imperet illi Deus, supplices deprecamur: tuque, Princeps militiae caelestis, Satanam aliosque spiritus malignos, qui ad perditionem animarum pervagantur in mundo, divina virtute, in infernum detrude. Amen."""
        ),
        PrayerItem(
            id = "memorare_prayer",
            title = "The Memorare",
            category = "Protection & Saints",
            subtitle = "Prayer of St. Bernard of Clairvaux",
            englishText = """Remember, O most gracious Virgin Mary, that never was it known that anyone who fled to thy protection, implored thy help, or sought thine intercession was left unaided. Inspired by this confidence, I fly unto thee, O Virgin of virgins, my Mother; to thee do I come, before thee I stand, sinful and sorrowful. O Mother of the Word Incarnate, despise not my petitions, but in thy mercy hear and answer me, particularly in this my urgent necessity: (here make your request). Amen.""",
            latinText = """Memorare, o piissima Virgo Maria, a saeculo non esse auditum quemquam ad tua currentem praesidia, tua implorantem auxilia, tua petentem suffragia, esse derelictum. Ego tali animatus confidentia, ad te, Virgo Virginum, Mater, curro, ad te venio, coram te gemens peccator assisto. Noli, Mater Verbi, verba mea despicere; sed audi propitia et exaudi. Amen."""
        ),
        PrayerItem(
            id = "st_benedict_prayer",
            title = "Prayer to Saint Benedict for Protection",
            category = "Protection & Saints",
            subtitle = "Oratio ad Sanctum Benedictum",
            englishText = """O glorious Saint Benedict, sublime model of all virtues, pure vessel of God's grace! Behold me, humbly prostrate before thee. I implore thy loving heart to pray for me before the throne of God. To thee I have recourse in all the dangers which daily surround me. Shield me against my enemies, inspire me to imitate thee in all things. May thy blessing be with me always, so that I may flee from sin and avoid the occasions of it.

Graciously obtain for me from God those favors and graces which I so greatly need, in the trials, miseries and afflictions of life. Thy heart was always so full of love, compassion, and mercy towards those who were afflicted or troubled in any way. Thou didst never dismiss without consolation and assistance any one who had recourse to thee. I therefore invoke thy powerful intercession, in the confident hope that thou wilt hear my prayers and obtain for me the special grace and favor I earnestly implore: (here make your request), if it be for the greater glory of God and the welfare of my soul.

Help me, O great Saint Benedict, to live and die as a faithful child of God, to be ever submissive to His holy will, and to attain the eternal happiness of heaven. Amen.""",
            latinText = """Crux sacra sit mihi lux! Non draco sit mihi dux! Vade retro Satana! Nunquam suade mihi vana! Sunt mala quae libas. Ipse venena bibas!

(The Medal of Saint Benedict Exorcism: May the holy Cross be my light! Let not the dragon be my guide! Depart, Satan! Never suggest to me thy vanities! Evil are the things thou profferest. Drink thou thine own poison!)"""
        ),
        PrayerItem(
            id = "st_joseph_before_work",
            title = "Prayer Before Work (To St. Joseph the Worker)",
            category = "Protection & Saints",
            subtitle = "Composed by Pope St. Pius X (1906) for Sanctification of Daily Labor",
            notes = "An indulgenced prayer to St. Joseph, Patron of Workers, asking for grace to work in a spirit of penance, gratitude, order, and devotion.",
            englishText = """Glorious St. Joseph, model of all who are devoted to labor, obtain for me the grace to work in the spirit of penance in expiation of my many sins; to work conscientiously by placing the love of duty above my inclinations; to gratefully and joyfully deem it an honor to employ and to develop by labor the gifts I have received from God; to work methodically, peacefully, in moderation and patience, without ever shrinking from hardship and difficulties; to work above all with purity of intention and detachment from self, having unceasingly before my eyes death and the account I have to render of time lost, talents unused, good omitted, and vain complacency in success, so fatal to the work of God.

All for Jesus, all through Mary, all in imitation of thee, O Patriarch St. Joseph! This shall be my motto in life and in death. Amen.""",
            latinText = """Gloriose Sancte Ioseph, exemplar omnium qui labori sunt addicti, impetra mihi gratiam laborandi in spiritu paenitentiae pro expiatione multorum peccatorum meorum; laborandi conscientia ductum, anteponendo officii amorem propriis inclinationibus; laborandi cum gratitudine et gaudio, honoris loco ducentem adhibere et evolvere per laborem dona a Deo accepta; laborandi cum ordine, pace, moderatione et patientia, nunquam succumbentem fatigationi vel difficultatibus; laborandi praesertim cum puritate intentionis et cum abnegatione sui ipsius, semper prae oculis habentem mortem et rationem reddendam de tempore amisso, de talentis inusitatis, de bono omisso, de vana complacentia in successu, tam noxia operi Dei.

Omnia pro Iesu, omnia per Mariam, omnia ad imitationem tuam, O Patriarcha Sancte Ioseph! Hoc erit synthema meum in vita et in morte. Amen."""
        ),
        PrayerItem(
            id = "st_thomas_aquinas_before_study",
            title = "Prayer Before Study (By St. Thomas Aquinas)",
            category = "Protection & Saints",
            subtitle = "Oratio Ante Studium (Doctor Angelicus)",
            notes = "Traditional prayer composed by St. Thomas Aquinas before studying, preaching, or writing, asking for clarity of understanding and subtlety of interpretation.",
            englishText = """Creator of all things, true Source of light and wisdom, lofty Origin of all being, graciously let a ray of Your brilliance penetrate into the darkness of my understanding and take from me the double darkness in which I have been born, an obscurity of both sin and ignorance.

Give me a sharp sense of understanding, a retentive memory, and the ability to grasp things correctly and fundamentally. Grant me the favor of being exact in my explanations, and the ability to express myself with thoroughness and charm.

Point out the beginning, direct the progress, and help in the completion; through Christ Our Lord. Amen.""",
            latinText = """Creator ineffabilis, qui de thesauris sapientiae tuae tres Angelorum hierarchias designasti, et eas super caelum empyreum miro ordine collocasti, et universi partes elegantissime distribuisti:

Tu, inquam, qui verus fons luminis et sapientiae diceris, et supereminens principium, infundere digneris super intellectus mei tenebras tuae radium claritatis, duplices, in quibus natus sum, a me removens tenebras, peccatum scilicet et ignorantiam.

Tu, qui linguas infantium facis disertas, linguae meae erudias et in labiis meis gratiam tuae benedictionis infundas. Da mihi intelligendi acumen, retinendi capacitatem, interpretandi methodum et facilitatem, discendi subtilitatem, loquendi gratiam uberem.

Ingressum instruas, progressum dirigas, egressum compleas: Tu, qui es verus Deus et homo, qui vivis et regnas in saecula saeculorum. Amen."""
        ),
        PrayerItem(
            id = "st_joseph_ancient",
            title = "Ancient Prayer to Saint Joseph",
            category = "Protection & Saints",
            subtitle = "Over 1900 Years Old Protection Prayer",
            englishText = """O Saint Joseph, whose protection is so great, so strong, so prompt before the throne of God, I place in thee all my interests and desires.

O Saint Joseph, do assist me by thy powerful intercession, and obtain for me from thy Divine Son all spiritual blessings, through Jesus Christ, our Lord, so that, having engaged here below thy heavenly power, I may offer my thanksgiving and homage to the most loving of Fathers.

O Saint Joseph, I never weary of contemplating thee, and Jesus asleep in thine arms; I dare not approach while He reposes near thy heart. Press Him in my name and kiss His fine head for me and ask Him to return the kiss when I draw my dying breath. Saint Joseph, patron of departing souls, pray for me, and graciously obtain this my special request: (here make your request). Amen."""
        ),
        PrayerItem(
            id = "st_jude_prayer",
            title = "Prayer to Saint Jude Thaddeus",
            category = "Protection & Saints",
            subtitle = "Patron of Hopeless Cases",
            englishText = """Most holy Apostle, Saint Jude, faithful servant and friend of Jesus, the name of the traitor who delivered thy beloved Master into the hands of His enemies hath caused thee to be forgotten by many, but the Church honors and invokes thee universally, as the patron of hopeless cases, of things almost despaired of.

Pray for me, I am so helpless and alone. Make use, I implore thee, of that particular privilege given to thee, to bring visible and speedy help where help is almost despaired of. Come to my assistance in this great need, that I may receive the consolation and help of Heaven in all my necessities, tribulations, and sufferings, particularly (here make your request), and that I may praise God with thee and all the elect forever.

I promise, O blessed Saint Jude, to be ever mindful of this great favor, to always honor thee as my special and powerful patron, and to gratefully encourage devotion to thee. Amen."""
        ),
        PrayerItem(
            id = "st_anthony_prayer",
            title = "Unfailing Prayer to Saint Anthony",
            category = "Protection & Saints",
            subtitle = "The Wonder-Worker of Padua",
            englishText = """O holy Saint Anthony, gentlest of Saints, thy love for God and charity for His creatures made thee worthy, when on earth, to possess miraculous powers. Miracles waited on thy word, which thou wert ever ready to speak for those in trouble or anxiety. Encouraged by this thought, I implore thee to obtain for me (here make your request). The answer to my prayer may require a miracle; even so, thou art the Saint of Miracles.

O gentle and loving Saint Anthony, whose heart was ever full of human sympathy, whisper my petition into the ears of the Sweet Infant Jesus, who loved to be folded in thine arms, and the gratitude of my heart will ever be thine. Amen."""
        ),
        PrayerItem(
            id = "st_therese_prayer",
            title = "Prayer to Saint Thérèse of Lisieux",
            category = "Protection & Saints",
            subtitle = "Greatest Saint of Modern Times",
            englishText = """O Glorious St. Therese, whom Almighty God has raised up to aid and counsel mankind, I implore your miraculous intercession. So powerful are you in obtaining from God favors and graces that Holy Mother Church has called you 'the greatest saint of modern times.' Now I fervently beseech you to answer my petition: (specify petition) And to carry out your promises of spending heaven doing good upon earth, and of ever letting fall from heaven a shower of roses. Henceforth, dear little flower, I will fulfill your plea to be made known everywhere, and I will never cease to lead others to Jesus through you. Amen."""
        ),
        PrayerItem(
            id = "st_philomena_prayer",
            title = "Prayer to Saint Philomena",
            category = "Protection & Saints",
            subtitle = "Illustrious Virgin and Martyr",
            englishText = """O faithful virgin and glorious martyr, Saint Philomena, who works so many miracles on behalf of the poor and sorrowing, have pity on me. Thou knowest the multitude and diversity of my griefs. Behold me at thy feet, full of misery, but full of hope.

I entreat thy charity, O great Saint! Graciously hear me and obtain from God a favorable answer to the request which I now humbly lay before thee (here make your request).

I firmly believe that through thy merits, thy scorn of the world, thy love for Jesus Christ, and thy heroic death, thou wilt obtain for me what I ask, and that through thy powerful intercession before the throne of God, I may find comfort in my afflictions and strength to do His holy will. Saint Philomena, powerful with God, pray for us. Amen."""
        ),
        PrayerItem(
            id = "st_pius_x_prayer",
            title = "Prayer to Saint Pius X",
            category = "Protection & Saints",
            subtitle = "Pope of the Blessed Sacrament & Restorer of All Things in Christ (Canonized 1954)",
            englishText = """O Saint Pius X, glorious Pope of the Eucharist, model of humility and fortitude, who didst labor so zealously to restore all things in Christ, protect the Holy Church and all her faithful. Obtain for us a true love for the Holy Mass, devotion to the Blessed Sacrament, and unwavering fidelity to the traditional Catholic Faith. Teach us to preserve Christian modesty, to seek holiness in all our duties, and to defend the sacred traditions handed down by our fathers. Humbly do I beg thee to intercede for my special necessity: (here make your request). Pray for us, that we may be made worthy of the promises of Christ. Amen."""
        ),
        PrayerItem(
            id = "st_anne_prayer",
            title = "Prayer to Saint Anne",
            category = "Protection & Saints",
            subtitle = "Mother of the Blessed Virgin Mary",
            englishText = """O glorious Saint Anne, filled with compassion for those who invoke thee and with love for those who suffer, heavily laden with the weight of my troubles, I cast myself at thy feet and humbly beg of thee to take the present affair which I recommend to thee under thy special protection: (here make your request). Vouchsafe to recommend it to thy daughter, the Blessed Virgin Mary, and lay it before the throne of Jesus, so that He may bring it to a happy issue. Cease not to intercede for us until my request is granted. Above all, obtain for us the grace of one day beholding our God face to face, and with thee and Mary and all the saints, praising Him through all eternity. Amen."""
        ),
        PrayerItem(
            id = "st_augustine_prayer",
            title = "Prayer to Saint Augustine of Hippo",
            category = "Protection & Saints",
            subtitle = "Doctor of Grace & Spiritual Discipline",
            englishText = """O glorious Doctor of the Church, Saint Augustine, who didst wonderfully declare the deep mysteries of God's grace and love, I fly to thy powerful intercession. Obtain for us a true spirit of penance, a deep love of Sacred Scripture, and unwavering fidelity to the traditional Catholic Faith. Humbly do I beg thee to present my earnest request to the throne of the Almighty: (here make your request). Pray for us, O great Saint, that we may be made worthy of the promises of Christ. Amen."""
        ),
        PrayerItem(
            id = "st_thomas_aquinas_prayer",
            title = "Prayer to Saint Thomas Aquinas",
            category = "Protection & Saints",
            subtitle = "Doctor Angelicus (For Wisdom & Purity)",
            englishText = """O glorious Doctor, Saint Thomas, patron of students and Catholic schools, who didst shine with the light of wisdom and the purity of an angel! Guide my mind in the pursuit of truth, guard my heart against all sinful errors, and intercede for me before the throne of the Savior. I earnestly implore thee to obtain for me the grace I now seek: (here make your request). Help me to know God more clearly and love Him more fervently. Amen."""
        ),
        PrayerItem(
            id = "st_francis_prayer",
            title = "Prayer to Saint Francis of Assisi",
            category = "Protection & Saints",
            subtitle = "The Seraphic Patriarch of the Poor",
            englishText = """O glorious Saint Francis, who didst bear in thy flesh the sacred stigmata of our crucified King, I humbly have recourse to thy powerful prayers. Obtain for me from our Lord Jesus Christ a deep spirit of humility, holy poverty, and a burning charity for souls. Look down with pity upon my present trial, and intercede that my petition may be granted: (here make your request). Help me to follow Christ's footsteps faithfully until death. Amen."""
        ),
        PrayerItem(
            id = "st_ignatius_prayer",
            title = "Prayer to Saint Ignatius of Loyola",
            category = "Protection & Saints",
            subtitle = "Founder of the Society of Jesus",
            englishText = """O glorious Saint Ignatius, founder of the Society of Jesus, who didst live solely for the Greater Glory of God, I beg thy powerful intercession. Obtain for me a generous heart to serve God, courage to fight against spiritual enemies, and the grace of final perseverance. Look with favor upon my present necessity, and present my petition before the Divine Majesty: (here make your request). Amen."""
        ),
        PrayerItem(
            id = "st_monica_prayer",
            title = "Prayer to Saint Monica",
            category = "Protection & Saints",
            subtitle = "Patroness of Mothers & Wayward Children",
            englishText = """Exemplary Mother of the Great Augustine, thou didst perseveringly pursue thy wayward son not with threats but with prayerful cries to heaven. Intercede, O holy Monica, for all mothers of our day, that they may learn to draw their children to God through meekness, piety, and patient prayer. Obtain also for us the grace of a holy sorrow for our past sins, so that we may live in constant penitence and union with God, and present to the throne of God our particular petition: (here make your request). Amen."""
        ),
        PrayerItem(
            id = "st_catherine_prayer",
            title = "Prayer to Saint Catherine of Siena",
            category = "Protection & Saints",
            subtitle = "Doctor of the Church & Virgin",
            englishText = """O glorious Saint Catherine, virgin and Doctor of the Church, who didst receive from God the gift of a most high wisdom and a burning love for the salvation of souls, pray for us. Teach us to love Jesus with that same pure and sacrificial love which filled thy heart. Guide us to defend the Holy Church and her sacred traditions with courage and meekness. Look with maternal pity upon my present sorrow, and obtain for me this grace which I humbly request: (here make your request). Amen."""
        ),
        PrayerItem(
            id = "st_patrick_prayer",
            title = "Prayer to Saint Patrick",
            category = "Protection & Saints",
            subtitle = "Apostle of Ireland",
            englishText = """O glorious Apostle of Ireland, Saint Patrick, who didst plant the Standard of the Cross in a pagan land and didst chase away the darkness of error! We fly to thy protection and implore thy powerful intercession. Preserve in us the light of the true traditional Catholic Faith, obtain for us a strong and lively hope, and graciously present our special petition to the Lord: (here make your request). Amen."""
        ),
        PrayerItem(
            id = "st_john_vianney_prayer",
            title = "Prayer to Saint John Vianney",
            category = "Protection & Saints",
            subtitle = "The Curé of Ars (Patron of Priests)",
            englishText = """O holy Curé of Ars, Saint John Marie Vianney, who didst make thy life a constant sacrifice for the conversion of sinners and the sanctification of souls, pray for us. Grant us a portion of that extraordinary spirit of prayer, penance, and love for the Holy Eucharist which consumed thy earthly life. Help us to make sincere and holy confessions, and present our present trials and petitions to the Sacred Heart of Jesus: (here make your request). Amen."""
        ),
        PrayerItem(
            id = "st_gemma_prayer",
            title = "Prayer to Saint Gemma Galgani",
            category = "Protection & Saints",
            subtitle = "The Passion Flower of Lucca",
            englishText = """O glorious Saint Gemma, pure virgin and victim of divine love, who didst share in the Passion of our Lord Jesus Christ, look down with love upon me from thy place of heavenly glory. Obtain for me a tender devotion to the Blessed Sacrament and the grace to bear my daily cross with patience. Intercede for me in my current necessity, and present my prayer to the Savior: (here make your request). Amen."""
        ),
        PrayerItem(
            id = "st_dominic_prayer",
            title = "Prayer to Saint Dominic",
            category = "Protection & Saints",
            subtitle = "Founder of the Order of Preachers & Defender of the Rosary",
            englishText = """O glorious Saint Dominic, who didst receive the Holy Rosary from the hands of the Blessed Virgin Mary to preach it to the world for the salvation of souls, pray for us. Inspire us to love the Rosary, to meditate devoutly on its mysteries, and to walk in the path of truth and holiness. Help us to be defenders of the sacred traditions, and obtain for us this special favor which we humbly beg: (here make your request). Amen."""
        ),
        PrayerItem(
            id = "st_francis_de_sales_prayer",
            title = "Prayer to Saint Francis de Sales",
            category = "Protection & Saints",
            subtitle = "Doctor of Spiritual Direction",
            englishText = """O glorious Saint Francis de Sales, Doctor of Christian Gentleness, who didst convert thousands back to the true Fold through the sweetness of thy love! Direct my heart in the path of holy peace, obtain for me a spirit of meekness under trials, and intercede for me in this my urgent petition: (here make your request). Teach me to seek only the holy will of God. Amen."""
        )
    )

    val devotionsImmaculateHeart = listOf(
        PrayerItem(
            id = "litany_immaculate_heart",
            title = "Litany of the Immaculate Heart of Mary",
            category = "Immaculate Heart Devotions",
            subtitle = "Approved Traditional Litany for Private & Public Devotion",
            subsection = "Litanies & Consecrations",
            englishText = """Lord, have mercy on us. Christ, have mercy on us.
Lord, have mercy on us. Christ, hear us. Christ, graciously hear us.
God the Father of Heaven, have mercy on us.
God the Son, Redeemer of the world, have mercy on us.
God the Holy Ghost, have mercy on us.
Holy Trinity, one God, have mercy on us.

Heart of Mary, pray for us.
Heart of Mary, according to the Heart of God, pray for us.
Heart of Mary, united to the Heart of Jesus, pray for us.
Heart of Mary, organ of the Holy Ghost, pray for us.
Heart of Mary, sanctuary of the Adorable Trinity, pray for us.
Heart of Mary, tabernacle of the Word Incarnate, pray for us.
Heart of Mary, preserved from original sin, pray for us.
Heart of Mary, richest treasury of God's grace, pray for us.
Heart of Mary, blessed among all hearts, pray for us.
Heart of Mary, throne of glory, pray for us.
Heart of Mary, abyss of humility, pray for us.
Heart of Mary, holocaust of divine love, pray for us.
Heart of Mary, fastened to the Cross with Jesus Christ, pray for us.
Heart of Mary, solace of the afflicted, pray for us.
Heart of Mary, refuge of sinners, pray for us.
Heart of Mary, hope of the agonizing, pray for us.
Heart of Mary, seat of mercy, pray for us.

Lamb of God, who takest away the sins of the world, spare us, O Lord.
Lamb of God, who takest away the sins of the world, graciously hear us, O Lord.
Lamb of God, who takest away the sins of the world, have mercy on us.

V. Pray for us, O Holy Mother of God.
R. That we may be made worthy of the promises of Christ.

Let us pray: O God of all goodness, who didst fill the holy and immaculate Heart of Mary with the same sentiments of mercy and tenderness for us with which the Heart of Jesus Christ, Thy Son and her Son, was always filled: grant unto all who honor this virginal Heart that, by its merits and intercession, they may remain forever in conformity with the Hearts of Jesus and Mary. Through Christ our Lord. Amen.""",
            latinText = """Kyrie, eleison. Christe, eleison. Kyrie, eleison.
Christe, audi nos. Christe, exaudi nos.
Pater de caelis, Deus, miserere nobis.
Fili, Redemptor mundi, Deus, miserere nobis.
Spiritus Sancte, Deus, miserere nobis.
Sancta Trinitas, unus Deus, miserere nobis.

Cor Mariae, ora pro nobis.
Cor Mariae, Corde Dei conformatum, ora pro nobis.
Cor Mariae, Corde Iesu unitum, ora pro nobis.
Cor Mariae, organum Spiritus Sancti, ora pro nobis.
Cor Mariae, sanctuarium Sanctissimae Trinitatis, ora pro nobis.
Cor Mariae, tabernaculum Verbi Incarnati, ora pro nobis.
Cor Mariae, sine labe originali conceptum, ora pro nobis.
Cor Mariae, gratiae thesaurus plenissimus, ora pro nobis.
Cor Mariae, benedictum inter omnia corda, ora pro nobis.
Cor Mariae, thronus gloriae, ora pro nobis.
Cor Mariae, abyssus humilitatis, ora pro nobis.
Cor Mariae, holocaustum divini amoris, ora pro nobis.
Cor Mariae, Cruci cum Christo confixum, ora pro nobis.
Cor Mariae, solatium afflictorum, ora pro nobis.
Cor Mariae, refugium peccatorum, ora pro nobis.
Cor Mariae, spes agonizantium, ora pro nobis.
Cor Mariae, sedes misericordiae, ora pro nobis.

Agnus Dei, qui tollis peccata mundi, parce nobis, Domine.
Agnus Dei, qui tollis peccata mundi, exaudi nos, Domine.
Agnus Dei, qui tollis peccata mundi, miserere nobis.

V. Ora pro nobis, Sancta Dei Genetrix.
R. Ut digni efficiamur promissionibus Christi.

Oremus: Deus totius bonitatis, qui beatis et immaculatum Cor Mariae eisdem misericordiae ac pietatis erga nos sensibus replevisti, quibus Iesu Christi Filii tui Cor redundavit: da omnibus hoc Cor virginale colentibus, ut eius meritis et intercessione, cum Cordibus Iesu et Mariae perpetuo conformentur. Per Christum Dominum nostrum. Amen."""
        ),
        PrayerItem(
            id = "consecration_mary",
            title = "Act of Consecration to the Immaculate Heart of Mary",
            category = "Immaculate Heart Devotions",
            subtitle = "Solemn Act of Consecration of the World (Pope Pius XII, October 31, 1942)",
            subsection = "Litanies & Consecrations",
            englishText = """Queen of the Holy Rosary, Help of Christians, Refuge of the human race, Conqueress of all the battles of God! To thee, to thy Immaculate Heart, we, in this hour of humanity's dire need, consecrate and surrender ourselves, in union not only with the Holy Church—the Mystical Body of thy Son, Jesus—which suffers in so many of its members, but also with the entire world.

To thee and to thy Immaculate Heart we entrust, consecrate, and offer not only the Holy Church, but also our homes, our families, and our fatherland. Give peace to the nations and grant that the Church of God may enjoy liberty and quiet to spread throughout the world the Kingdom of Christ, the Kingdom of truth and life, the Kingdom of holiness and grace, the Kingdom of justice, love, and peace. Amen.""",
            latinText = """Regina sanctissimi Rosarii, Auxilium christianorum, Refugium generis humani, Victrix in omnibus Dei proeliis! Ad pedes tuos provoluti, tibi ac tuo Immaculato Cordi, hac tragica humani generis hora, nos, quae tua sunt, quae nostra sunt, devovemus, consecramus, ac tradimus, non tantum cum Sancta Ecclesia, mystico Filii tui Corpore, quae in multis membris dolet et patitur, sed etiam cum universo mundo.

Tibi et Immaculato Cordi tuo committimus, consecramus, ac tradimus nosmetipsos, familias nostras, domos nostras, patriam nostram. Da pacem populis et concede ut Ecclesia Dei libertate ac tranquillitate fruatur ad Christi Regnum, veritatis et vitae, sanctitatis et gratiae, iustitiae, amoris et pacis, ubique terrarum dilatandum. Amen."""
        ),
        PrayerItem(
            id = "personal_consecration_immaculate_heart",
            title = "Act of Personal Consecration to the Immaculate Heart of Mary",
            category = "Immaculate Heart Devotions",
            subtitle = "Traditional Consecration of St. John Eudes",
            subsection = "Litanies & Consecrations",
            englishText = """O Heart of Mary, Mother of God and my Mother, Heart most pure, most holy and most amiable, I surrender and consecrate myself entirely to thee. I dedicate to thee my body and my soul, my thoughts, desires, words, and actions, and all that I possess.

O Mother most loving, take me under thy special protection; guard me from all occasions of sin; obtain for me the grace of true conversion and perfect love of God. Form my heart after the model of thine own, that I may live only for Jesus and for thee, and that at the hour of my death thou mayest present my soul to Jesus, thy Divine Son. Amen.""",
            latinText = """O Cor Mariae, Mater Dei et Mater mea, Cor purissimum, sanctissimum atque amabilissimum, totum me tibi trado et consecro. Tibi dedico corpus et animam meam, cogitationes, desideria, verba et opera mea, omniaque bona mea.

O Mater amantissima, suscipe me sub singulari tutela tua; custodi me ab omnibus peccati occasionibus; impetra mihi gratiam verae conversionis et perfectae caritatis. Conforma cor meum ad exemplar Cordis tui, ut solum pro Iesu et pro te vivam, et in hora mortis meae animam meam Iesu Divino Filio tuo praesentes. Amen."""
        ),
        PrayerItem(
            id = "act_reparation_immaculate_heart",
            title = "Act of Reparation to the Immaculate Heart of Mary",
            category = "Immaculate Heart Devotions",
            subtitle = "Prescribed for First Saturdays - Roman Raccolta 392",
            subsection = "Acts of Reparation & First Saturdays",
            englishText = """O Most Holy Virgin, and our beloved Mother, we listen with grief to the complaints of thy Immaculate Heart surrounded with the thorns which ungrateful men place therein at every moment by their blasphemies and ingratitude. Moved by the ardent desire of loving thee as our Mother and of promoting a true devotion to thy Immaculate Heart, we prostrate ourselves at thy feet to prove the sorrow we feel for the grievances that men cause thee, and to atone by our prayers and sacrifices for the offenses with which men return thy tender love.

Obtain for them and for us the pardon of so many sins. A word from thee will obtain grace and forgiveness for all. Hasten, O Lady, the conversion of sinners, that they may love Jesus and cease to offend the Lord, already so much offended. Turn thine eyes of mercy toward us, that we may love God with all our heart on earth and enjoy Him forever in Heaven. Amen.""",
            latinText = """O Virgo Sanctissima et Mater nostra dulcissima, maerore perculsi audimus gemitus tui Immaculati Cordis, spinis undique cincti quas ingrati homines suis blasphemiis et ingratitudinibus indesinenter infigunt. Magno flagrantes desiderio te ut Matrem diligendi veramque devotionem tuo Immaculato Cordi propagandi, ad pedes tuos provoluimur, testaturi dolorem quem sentimus ob iniurias tibi illatas, ac precibus et sacrificiis nostris expiaturi offensiones quibus homines tenerrimo amori tuo respondent. Amen."""
        ),
        PrayerItem(
            id = "five_first_saturdays",
            title = "The Five First Saturdays Devotion of Reparation",
            category = "Immaculate Heart Devotions",
            subtitle = "Given by Our Lady at Fatima & Pontevedra (1925)",
            subsection = "Acts of Reparation & First Saturdays",
            englishText = """Our Lady promised to assist at the hour of death with all the graces necessary for salvation to all who, on the First Saturday of 5 consecutive months:
1. Go to Confession (within 8 days before or after, with the intention of making reparation to the Immaculate Heart).
2. Receive Holy Communion worthily on the First Saturday in reparation.
3. Recite 5 decades of the Holy Rosary.
4. Keep Our Lady company for 15 minutes while meditating on one or more mysteries of the Rosary with the intention of making reparation to her Immaculate Heart.

The Five Blasphemies made against the Immaculate Heart to be repaired:
1. Blasphemies against her Immaculate Conception.
2. Blasphemies against her Perpetual Virginity.
3. Blasphemies against her Divine Maternity and refusing to accept her as the Mother of men.
4. Blasphemies of those who publicly seek to instill in the hearts of children indifference, contempt, or hatred toward this Immaculate Mother.
5. Direct insults against her sacred images.""",
            latinText = ""
        ),
        PrayerItem(
            id = "reparation_sorrowful_immaculate_heart",
            title = "Prayer of Reparation to the Sorrowful and Immaculate Heart of Mary",
            category = "Immaculate Heart Devotions",
            subtitle = "From the Roman Raccolta",
            subsection = "Acts of Reparation & First Saturdays",
            englishText = """O Sorrowful and Immaculate Heart of Mary, pierced with the sword of sorrow at the foot of the Cross, behold us humbly kneeling before thee. We desire to comfort thy afflicted Heart for the countless sins and outrages of mankind.

We offer thee our prayers, our penances, and our total devotion, uniting our sufferings with the bitter tears thou didst shed for our salvation. May our love console thee, O Mother of Sorrows, and obtain from Jesus the pardon of our offenses and peace for the Church and the whole world. Amen.""",
            latinText = """O Cor Dolorosum et Immaculatum Mariae, gladio doloris sub Cruce transfixum, en nos humiliter ante te provoluti. Desideramus afflictum Cor tuum consolari pro innumeris peccatis et iniuriis hominum.

Offerimus tibi preces nostras, paenitentias nostras et plenam devotionem nostram, unientes dolores nostros cum lacrimis quas pro nostra salute effudisti. Amor noster te consoletur, o Mater Dolorosa, et impetret a Iesu veniam delictorum nostrorum ac pacem Ecclesiae et universo mundo. Amen."""
        ),
        PrayerItem(
            id = "immaculate_heart_traditional_prayer",
            title = "Traditional Prayer to the Immaculate Heart of Mary",
            category = "Immaculate Heart Devotions",
            subtitle = "Roman Raccolta 390",
            subsection = "Classical Devotions",
            englishText = """O Heart of Mary, Mother of God and our Mother; Heart most worthy of love, in which the Adorable Trinity is well-pleased, and worthy of all the veneration and love of Angels and of men; Heart most like unto the Heart of Jesus, whereof thou art the most perfect copy; Heart full of goodness, which art so compassionate toward our miseries: do thou melt the coldness of our hearts and make them to be entirely turned to the Heart of our Divine Savior.

Infuse into them the love of thy virtues, inflame them with that divine fire wherewith thine own ever burns. Take under thy protection the Holy Church, defend her and be her sweet refuge against every assault of her enemies. Be thou our way to Jesus, and the channel whereby we may receive all the graces needful for our salvation. Amen.""",
            latinText = """Cor Mariae, Mater Dei et Mater nostra; Cor amabilissimum, in quo Sanctissima Trinitas sibi bene complacuit, omnibusque Angelorum et hominum laudibus dignissimum; Cor Iesu Cordi simillimum, cuius perfectissima es imago; Cor bonitate plenum et nostris miserationibus compatiens: liquefac cordium nostrorum duritiem, et fac ut ad Divini Salvatoris Cor penitus convertantur. Amen."""
        ),
        PrayerItem(
            id = "memorare_immaculate_heart",
            title = "Memorare to the Immaculate Heart of Mary",
            category = "Immaculate Heart Devotions",
            subtitle = "Roman Raccolta 395",
            subsection = "Classical Devotions",
            englishText = """Remember, O most pure and compassionate Virgin Mary, that it is a thing unheard of that anyone who had recourse to thy Immaculate Heart, implored its help, or sought its intercession was left forsaken. Inspired with this confidence, I come to thee, O Virgin of virgins, and to thy maternal Heart do I fly; before thee I stand, sinful and sorrowful. O Mother of the Incarnate Word, despise not my petitions, but in thy clemency hear and answer me. Amen.""",
            latinText = """Memorare, o purissima et clementissima Virgo Maria, a saeculo inauditum esse quemquam ad tuum Immaculatum Cor confugientem, tua implorantem auxilia, tua petentem suffragia, esse derelictum. Tali animatus confidentia, ad tuum Cor maternum curro, ad te venio, coram te gemens peccator assisto. Noli, Mater Verbi Incarnati, verba mea despicere, sed audi propitia et exaudi. Amen."""
        ),
        PrayerItem(
            id = "ave_cor_sanctissimum_john_eudes",
            title = "Salutation of St. John Eudes to the Immaculate Heart of Mary",
            category = "Immaculate Heart Devotions",
            subtitle = "Ave Cor Sanctissimum Mariae (St. John Eudes, 1601–1680)",
            subsection = "Classical Devotions",
            englishText = """Hail, Heart most holy! Hail, Heart most meek! Hail, Heart most humble! Hail, Heart most pure! Hail, Heart most devoted! Hail, Heart most wise! Hail, Heart most patient! Hail, Heart most obedient! Hail, Heart most vigilant! Hail, Heart most faithful! Hail, Heart most blessed! Hail, Heart most merciful! Hail, most loving Heart of Jesus and Mary!

We praise Thee, we bless Thee, we glorify Thee, we give Thee thanks; we love Thee with all our heart, with all our soul, and with all our strength. We offer Thee our heart, we give it to Thee, we consecrate it to Thee, we immolate it to Thee. Receive it and possess it wholly; purify it, enlighten it, sanctify it, that Thou mayest live and reign in it forever. Amen.""",
            latinText = """Ave Cor sanctissimum, ave Cor mitissimum, ave Cor humillimum, ave Cor purissimum, ave Cor devotissimum, ave Cor sapientissimum, ave Cor patientissimum, ave Cor oboedientissimum, ave Cor vigilantissimum, ave Cor fidelissimum, ave Cor beatissimum, ave Cor misericordissimum, ave Cor amantissimum Iesu et Mariae!

Te laudamus, te benedicimus, te glorificamus, tibi gratias agimus; te amamus ex toto corde nostro, ex tota anima nostra, et ex totis viribus nostris. Tibi cor nostrum offerimus, donamus, consecramus, immolamus. Accipe illud et posside totum; purifica illud, illumina illud, sanctifica illud, ut in eo vivas et regnes in saecula saeculorum. Amen."""
        ),
        PrayerItem(
            id = "prayer_claret_immaculate_heart",
            title = "Prayer of St. Anthony Mary Claret to the Immaculate Heart",
            category = "Immaculate Heart Devotions",
            subtitle = "For the Salvation and Conversion of Sinners",
            subsection = "Classical Devotions",
            englishText = """O Heart of Mary, furnace and instrument of love, enkindle me with the love of God and of my neighbor. Thou didst so ardently desire the salvation of souls that thou didst suffer martyrdom of heart at the foot of the Cross.

Obtain for me, O Mother, an ardent zeal for the salvation of my brethren, that I may spare no labor, fear no trial, and shrink from no sacrifice to win souls to Jesus Christ, and to lead them to the eternal joy of Heaven. Amen.""",
            latinText = """O Cor Mariae, fornax et instrumentum divini amoris, accende me amore Dei et proximi. Quae tanto animarum salutis ardore flagrabas, ut sub Cruce cordis martyrium perpessa sis:

Impetra mihi, o Mater, fervidum zelum pro salute fratrum meorum, ut nullum laborem detrectem, nullum periculum timeam, nullumque sacrificium reformidem ad animas Iesu Christo lucrandas et ad aeternam caeli beatitudinem perducendas. Amen."""
        ),
        PrayerItem(
            id = "sweet_heart_mary_aspiration",
            title = "Aspiration: Sweet Heart of Mary, Be My Salvation!",
            category = "Immaculate Heart Devotions",
            subtitle = "Pope Pius IX Indulgence (Raccolta 393)",
            subsection = "Invocations & Aspirations",
            englishText = """Sweet Heart of Mary, be my salvation!

(Latin: Dulcissimum Cor Mariae, esto salus mea!)""",
            latinText = """Dulcissimum Cor Mariae, esto salus mea!"""
        ),
        PrayerItem(
            id = "immaculate_heart_invocations",
            title = "Invocations to the Immaculate Heart of Mary",
            category = "Immaculate Heart Devotions",
            subtitle = "Traditional Roman Raccolta Invocations (Raccolta 391 & 394)",
            subsection = "Invocations & Aspirations",
            englishText = """V. Immaculate Heart of Mary, pray for us now and at the hour of our death.
R. Amen.

V. Heart of Mary, Mother of God, pray for us.
R. That we may be made worthy of the promises of Christ.

V. Queen of Peace and Mother of Mercy,
R. Pray for our families, protect the Church, and save our souls.""",
            latinText = """V. Cor Mariae Immaculatum, ora pro nobis nunc et in hora mortis nostrae.
R. Amen.

V. Cor Mariae, Mater Dei, ora pro nobis.
R. Ut digni efficiamur promissionibus Christi.

V. Regina pacis et Mater misericordiae,
R. Ora pro familiis nostris, custodi Ecclesiam, et salva animas nostras. Amen."""
        )
    )

    val devotionsSacredHeart = listOf(
        PrayerItem(
            id = "litany_sacred_heart",
            title = "Litany of the Sacred Heart of Jesus",
            category = "Sacred Heart Devotions",
            subtitle = "Approved by Pope Leo XIII (June 2, 1899) - Roman Raccolta 242",
            subsection = "Litanies & Consecrations",
            englishText = """Lord, have mercy on us. Christ, have mercy on us.
Lord, have mercy on us. Christ, hear us. Christ, graciously hear us.
God the Father of Heaven, have mercy on us.
God the Son, Redeemer of the world, have mercy on us.
God the Holy Ghost, have mercy on us.
Holy Trinity, One God, have mercy on us.

Heart of Jesus, Son of the Eternal Father, have mercy on us.
Heart of Jesus, formed by the Holy Ghost in the womb of the Virgin Mother, have mercy on us.
Heart of Jesus, substantially united to the Word of God, have mercy on us.
Heart of Jesus, of Infinite Majesty, have mercy on us.
Heart of Jesus, Sacred Temple of God, have mercy on us.
Heart of Jesus, Tabernacle of the Most High, have mercy on us.
Heart of Jesus, House of God and Gate of Heaven, have mercy on us.
Heart of Jesus, Burning Furnace of Charity, have mercy on us.
Heart of Jesus, Abode of Justice and Love, have mercy on us.
Heart of Jesus, full of Goodness and Love, have mercy on us.
Heart of Jesus, Abyss of all Virtues, have mercy on us.
Heart of Jesus, most worthy of all praise, have mercy on us.
Heart of Jesus, King and Center of all hearts, have mercy on us.
Heart of Jesus, in whom are all the treasures of wisdom and knowledge, have mercy on us.
Heart of Jesus, in whom dwelleth the fullness of the Divinity, have mercy on us.
Heart of Jesus, in whom the Father was well pleased, have mercy on us.
Heart of Jesus, of whose fullness we have all received, have mercy on us.
Heart of Jesus, desire of the everlasting hills, have mercy on us.
Heart of Jesus, patient and most merciful, have mercy on us.
Heart of Jesus, enriching all who invoke Thee, have mercy on us.
Heart of Jesus, Fountain of life and holiness, have mercy on us.
Heart of Jesus, Propitiation for our sins, have mercy on us.
Heart of Jesus, loaded down with opprobrium, have mercy on us.
Heart of Jesus, bruised for our offenses, have mercy on us.
Heart of Jesus, obedient unto death, have mercy on us.
Heart of Jesus, pierced with a lance, have mercy on us.
Heart of Jesus, Source of all consolation, have mercy on us.
Heart of Jesus, our Life and Resurrection, have mercy on us.
Heart of Jesus, our Peace and Reconciliation, have mercy on us.
Heart of Jesus, Victim for our sins, have mercy on us.
Heart of Jesus, Salvation of those who trust in Thee, have mercy on us.
Heart of Jesus, Hope of those who die in Thee, have mercy on us.
Heart of Jesus, Delight of all the Saints, have mercy on us.

Lamb of God, who takest away the sins of the world, spare us, O Lord.
Lamb of God, who takest away the sins of the world, graciously hear us, O Lord.
Lamb of God, who takest away the sins of the world, have mercy on us.

V. Jesus, meek and humble of Heart,
R. Make our hearts like unto Thine.

Let us pray: Almighty and everlasting God, look upon the Heart of Thy well-beloved Son and upon the praise and satisfaction which He offers unto Thee in the name of sinners; and do Thou, of Thy great goodness, grant forgiveness unto them that seek Thy mercy, in the name of the same Jesus Christ, Thy Son, who liveth and reigneth with Thee in the unity of the Holy Ghost, God, world without end. Amen.""",
            latinText = """Kyrie, eleison. Christe, eleison.
Kyrie, eleison. Christe, audi nos. Christe, exaudi nos.
Pater de caelis, Deus, miserere nobis.
Fili, Redemptor mundi, Deus, miserere nobis.
Spiritus Sancte, Deus, miserere nobis.
Sancta Trinitas, unus Deus, miserere nobis.

Cor Iesu, Filii Patris aeterni, miserere nobis.
Cor Iesu, in sinu Virginis Matris a Spiritu Sancto formatum, miserere nobis.
Cor Iesu, Verbo Dei substantialiter unitum, miserere nobis.
Cor Iesu, maiestatis infinitae, miserere nobis.
Cor Iesu, templum Dei sanctum, miserere nobis.
Cor Iesu, tabernaculum Altissimi, miserere nobis.
Cor Iesu, domus Dei et porta caeli, miserere nobis.
Cor Iesu, fornax ardens caritatis, miserere nobis.
Cor Iesu, iustitiae et amoris receptaculum, miserere nobis.
Cor Iesu, bonitate et amore plenum, miserere nobis.
Cor Iesu, virtutum omnium abyssus, miserere nobis.
Cor Iesu, omni laude dignissimum, miserere nobis.
Cor Iesu, rex et centrum omnium cordium, miserere nobis.
Cor Iesu, in quo sunt omnes thesauri sapientiae et scientiae, miserere nobis.
Cor Iesu, in quo habitat omnis plenitudo divinitatis, miserere nobis.
Cor Iesu, in quo Pater sibi bene complacuit, miserere nobis.
Cor Iesu, de cuius plenitudine omnes nos accepimus, miserere nobis.
Cor Iesu, desiderium collium aeternorum, miserere nobis.
Cor Iesu, patiens et multae misericordiae, miserere nobis.
Cor Iesu, dives in omnes qui invocant te, miserere nobis.
Cor Iesu, fons vitae et sanctitatis, miserere nobis.
Cor Iesu, propitiatio pro peccatis nostris, miserere nobis.
Cor Iesu, saturatum opprobriis, miserere nobis.
Cor Iesu, attritum propter scelera nostra, miserere nobis.
Cor Iesu, usque ad mortem oboediens factum, miserere nobis.
Cor Iesu, lancea perforatum, miserere nobis.
Cor Iesu, fons totius consolationis, miserere nobis.
Cor Iesu, vita et resurrectio nostra, miserere nobis.
Cor Iesu, pax et reconciliatio nostra, miserere nobis.
Cor Iesu, victima peccatorum, miserere nobis.
Cor Iesu, salus in te sperantium, miserere nobis.
Cor Iesu, spes in te morientium, miserere nobis.
Cor Iesu, deliciae Sanctorum omnium, miserere nobis.

Agnus Dei, qui tollis peccata mundi, parce nobis, Domine.
Agnus Dei, qui tollis peccata mundi, exaudi nos, Domine.
Agnus Dei, qui tollis peccata mundi, miserere nobis.

V. Iesu, mitis et humilis Corde,
R. Fac cor nostrum secundum Cor tuum.

Oremus: Omnipotens sempiterne Deus, respice in Cor dilectissimi Filii tui et in laudes et satisfactiones, quas in nomine peccatorum tibi persolvit; eisque misericordiam tuam petentibus, tu veniam concede placatus in nomine eiusdem Filii tui Iesu Christi: Qui tecum vivit et regnat in unitate Spiritus Sancti, Deus, per omnia saecula saeculorum. Amen."""
        ),
        PrayerItem(
            id = "act_consecration_sacred_heart_leo_xiii",
            title = "Act of Consecration of the Human Race to the Sacred Heart",
            category = "Sacred Heart Devotions",
            subtitle = "Pope Leo XIII (Annum Sacrum, 1899) - Roman Raccolta 271",
            subsection = "Litanies & Consecrations",
            englishText = """Most Sweet Jesus, Redeemer of the human race, look down upon us humbly prostrate before Thy altar. We are Thine, and Thine we wish to be; but to be more surely united to Thee, behold each one of us freely consecrates ourselves today to Thy Most Sacred Heart.

Many indeed have never known Thee; many, too, despising Thy precepts, have rejected Thee. Have mercy on them all, most merciful Jesus, and draw them to Thy Sacred Heart. Be Thou King, O Lord, not only of the faithful who have never forsaken Thee, but also of the prodigal children who have abandoned Thee; grant that they may quickly return to their Father's house, lest they die of wretchedness and hunger.

Be Thou King of those who are deceived by erroneous opinions, or whom discord keeps aloof, and call them back to the harbor of truth and unity of faith, so that soon there may be but one flock and one Shepherd.

Grant, O Lord, to Thy Church assurance of freedom and immunity from harm; give peace and order to all nations, and make the earth resound from pole to pole with one cry: Praise to the Divine Heart that wrought our salvation; to It be glory and honor forever. Amen.""",
            latinText = """Iesu dulcissime, Redemptor humani generis, respice nos ad altare tuum humillime provolutos. Tui sumus, tui esse volumus; quo autem perfici possit unio nostra tecum, en hodie Sacratissimo Cordi tuo se quisque nostrum libenter consecrat.

Multi quidem te nunquam cognovere; multi item, spreti mandatis tuis, te reiecere. Miserere utrorumque, benignissime Iesu, atque ad sacrum Cor tuum pertrahe universos. Rex esto, Domine, non solum fidelium qui nullo tempore a te discessere, sed etiam prodigorum filiorum qui te reliquere; fac hos in domum paternam cito redeant, ne inedia et miseria pereant.

Rex esto eorum, quos vel opinionum error decepit, vel discordia separavit, eosque ad portum veritatis atque ad fidei unitatem revoca, ut brevi fiat unum ovile et unus pastor.

Largire, Domine, Ecclesiae tuae securam cum incolumitate libertatem; largire cunctis gentibus tranquillitatem ordinis; perfice ut ab utroque terrae vertice una resonet vox: Sit laus Divino Cordi, per quod nobis parta salus; ipsi gloria et honor in saecula. Amen."""
        ),
        PrayerItem(
            id = "act_consecration_margaret_mary",
            title = "Personal Act of Consecration to the Sacred Heart",
            category = "Sacred Heart Devotions",
            subtitle = "Saint Margaret Mary Alacoque - Roman Raccolta 258",
            subsection = "Litanies & Consecrations",
            englishText = """I, (Name), give and consecrate to the Sacred Heart of our Lord Jesus Christ my person and my life, my actions, pains, and sufferings, so that I may be unwilling to make use of any part of my being other than to honor, love, and glorify Him.

This is my unchanging purpose: to be all His, and to do all things for His love, while renouncing with all my heart whatever could displease Him.

I therefore take Thee, O Sacred Heart, for the only object of my love, the guardian of my life, the assurance of my salvation, the remedy of my weakness and inconstancy, the atonement for all the faults of my life, and the secure refuge at the hour of my death.

Be then, O Heart of goodness, my justification before God Thy Father, and turn away from me the strokes of His righteous anger. O Heart of love, I put all my trust in Thee, for I fear all things from my own weakness and malice, but I hope for all things from Thy bounty.

Consume in me whatever can displease or resist Thee; let Thy pure love imprint Thee so deeply upon my heart that I may never forget Thee or be separated from Thee. I beseech Thee by all Thy goodness that my name may be written upon Thee, for I wish to make all my happiness and all my glory consist in living and dying in quality of Thy slave. Amen.""",
            latinText = """Ego (Nomen), do et consecro Sacratissimo Cordi Domini nostri Iesu Christi personam et vitam meam, actiones, aerumnas et passiones meas, ut nulla mea parte uti velim nisi ad ipsum honorandum, amandum et glorificandum. Hoc est meum irrevocabile propositum: totus ipsius esse, et omnia pro eius amore facere, ex toto corde renuntiando quidquid ipsi displicere possit. Amen."""
        ),
        PrayerItem(
            id = "act_consecration_family_sacred_heart",
            title = "Act of Consecration of the Family to the Sacred Heart",
            category = "Sacred Heart Devotions",
            subtitle = "Enthronement of the Sacred Heart in the Home (Benedict XV)",
            subsection = "Litanies & Consecrations",
            englishText = """O Sacred Heart of Jesus, Who didst make known to Saint Margaret Mary Thine ardent desire to reign over Christian families, behold us assembled here today to proclaim Thy absolute dominion over our home.

Henceforth we resolve to live according to Thy life, to cultivate in our midst the virtues for which Thou didst promise peace on earth, and to banish from our home the spirit of the world which Thou didst curse.

Reign over our intellects by the simplicity of our faith; reign over our hearts by Thine unreserved love, wherewith they shall burn for Thee and whose flame we will nourish by the frequent reception of Thy Divine Eucharist.

Deign, O Divine Heart, to preside over our gatherings, to bless our enterprises, both spiritual and temporal, to dispel our cares, to sanctify our joys, and to assuage our sufferings. If ever one of us should have the misfortune to displease Thee, remind him, O Heart of Jesus, that Thou art good and merciful to the penitent sinner. Amen.""",
            latinText = """Sacratissimum Cor Iesu, quod sanctae Margaritae Mariae ardenti studio patefecisti desiderium tuum regnandi in christianis familiis, en nos hodie congregati ad proclamandum absolutum dominium tuum super domum nostram.

Ex hoc tempore proponimus vivere secundum vitam tuam, virtutes in nostro medio fovere quibus pacem in terris promisisti, et a domo nostra spiritum saeculi quem condemnasti procul expellere.

Regna super mentes nostras per fidei simplicitatem; regna super corda nostra per amorem tuum integrum, quo pro te flagrent et cuius flammam frequenti sanctissimae Eucharistiae sumptione alamus. Amen."""
        ),
        PrayerItem(
            id = "act_reparation_sacred_heart_pius_xi",
            title = "Act of Reparation to the Sacred Heart (Miserentissimus Redemptor)",
            category = "Sacred Heart Devotions",
            subtitle = "Pope Pius XI (1928) - Roman Raccolta 256",
            subsection = "Acts of Reparation & First Fridays",
            englishText = """O sweet Jesus, whose overflowing charity for men is requited by so much forgetfulness, negligence, and contempt, behold us prostrate before Thee, eager to repair by a special act of homage the cruel indifference and outrages to which Thy loving Heart is everywhere subjected.

Mindful, alas! that we ourselves have had a share in such great indignities, which we now deplore from the depths of our hearts, we humbly ask Thy pardon and declare our readiness to atone by voluntary expiation not only for our own personal offenses, but also for the sins of those who, straying far from the path of salvation, refuse in their obstinate infidelity to follow Thee, their Shepherd and Leader, or, renouncing their baptismal vows, have cast off the sweet yoke of Thy law.

We are now resolved to expiate each and every deplorable outrage committed against Thee: we are determined to make amends for the manifold offenses against Christian modesty in unbecoming dress and behavior, for all the foul seductions laid to ensnare the feet of the innocent, for the frequent violations of Sundays and holidays, and the shocking blasphemies uttered against Thee and Thy Saints.

Accept, then, O most loving Jesus, through the intercession of the Blessed Virgin Mary, the Mother of Reparation, this willing act of expiation; and by the great gift of final perseverance, keep us faithful unto death in our duty and the allegiance we owe to Thee, so that we may all one day come to that happy home where Thou, with the Father and the Holy Ghost, livest and reignest, God, world without end. Amen.""",
            latinText = """Iesu dulcissime, cuius effusa in homines caritas tanta oblivione, negligentia, contemptione rependitur, en nos, ante altaria tua provoluti, conspicuo honore compescare gestimus nefariam hominum socordiam et iniurias, quibus amantissimum Cor tuum undique afficitur.

Attamen, memores tantae et ipsi nos indignitatis non expertes fuisse, cuius alto dolore nunc sauciamur, tuam imprimis misericordiam nobis imploramus, parati voluntaria expiatione compensare non modo commissa a nobis scelera, sed eorum etiam qui, a salutis tramite longe devii, te pastorem ducemque sequi detrectant, in sua perfidia contumaces, vel, baptismi promissa conculcantes, suavissimum tuae legis iugum excussere.

Accipe, quaesumus, benignissime Iesu, beata Virgine Maria Reparatrice intercedente, voluntarium huius expiationis obsequium, nosque in officio tibique servitio fideles usque ad mortem serva magno illo perseverantiae dono, quo tandem omnes ad illam perveniamus patriam, ubi tu cum Patre et Spiritu Sancto vivis et regnas Deus in saecula saeculorum. Amen."""
        ),
        PrayerItem(
            id = "nine_first_fridays",
            title = "The Nine First Fridays Devotion & 12 Promises",
            category = "Sacred Heart Devotions",
            subtitle = "Revealed by Our Lord to St. Margaret Mary Alacoque",
            subsection = "Acts of Reparation & First Fridays",
            englishText = """Our Lord made 12 specific promises to those who practice devotion to His Sacred Heart:
1. I will give them all the graces necessary in their state of life.
2. I will establish peace in their homes.
3. I will comfort them in all their afflictions.
4. I will be their secure refuge in life, and above all in death.
5. I will bestow abundant blessings upon all their undertakings.
6. Sinners will find in My Heart the source and infinite ocean of mercy.
7. Lukewarm souls shall become fervent.
8. Fervent souls shall quickly mount to high perfection.
9. I will bless every place in which an image of My Sacred Heart is exposed and honored.
10. I will give to priests the gift of touching the most hardened hearts.
11. Those who promote this devotion shall have their names written in My Heart, never to be effaced.
12. The Great Promise: 'I promise thee in the excessive mercy of My Heart that My all-powerful love will grant to all those who receive Holy Communion on the First Friday in nine consecutive months the grace of final perseverance; they shall not die in My disgrace, nor without receiving their sacraments; My Divine Heart shall be their safe refuge in this last moment.'""",
            latinText = ""
        ),
        PrayerItem(
            id = "act_reparation_eucharistic_sacred_heart",
            title = "Act of Reparation to the Sacred Heart in the Holy Eucharist",
            category = "Sacred Heart Devotions",
            subtitle = "Roman Raccolta 247",
            subsection = "Acts of Reparation & First Fridays",
            englishText = """O Divine Savior, Who in the excess of Thy love hast chosen to dwell with us in the Sacrament of the Altar, behold us here prostrate to repair the many outrages and cold neglects to which Thy Sacred Heart is daily subjected in the Holy Eucharist.

Forgive, O Lord, our lukewarmness, our wandering thoughts, and our lack of reverence in Thy Holy Presence. We desire to love Thee for all who love Thee not, to adore Thee for all who adore Thee not, and to praise Thee for all who blaspheme Thy holy name. May Thy Sacred Heart in the Blessed Sacrament be praised, adored, and loved with grateful affection, at every moment, in all the tabernacles of the world, even unto the end of time. Amen.""",
            latinText = """O Salvator Divine, qui in excessu amoris tui in Sacramento Altaris nobiscum manere voluisti, en nos hic provoluti ad expiandas tot iniurias et socordias quibus Sacratissimum Cor tuum quotidie in Sanctissima Eucharistia afficitur.

Dimitte nobis, Domine, tepiditatem nostram et irreverentiam in Sanctissima Praesentia tua. Desideramus te amare pro omnibus qui te non amant, te adorare pro omnibus qui te non adorant, et te benedicere pro omnibus qui nomen sanctum tuum blasphemant. Sit laus, adoratio et amor grato animo Sacratissimo Cordi Iesu in Sanctissimo Sacramento, omni momento, in omnibus tabernaculis mundi, usque ad consummationem saeculi. Amen."""
        ),
        PrayerItem(
            id = "sacred_heart_padre_pio",
            title = "Efficacious Devotion to the Sacred Heart of Jesus",
            category = "Sacred Heart Devotions",
            subtitle = "Padre Pio's Daily Devotion of the Three Promises",
            subsection = "Classical Devotions",
            englishText = """I. O my Jesus, Who hast said: 'Truly I say to you, ask and you shall receive, seek and you shall find, knock and it shall be opened to you.' Behold, I knock, I seek, and I ask for the grace of (mention petition).
Our Father... Hail Mary... Glory be...
Sacred Heart of Jesus, I place all my trust in Thee.

II. O my Jesus, Who hast said: 'Truly I say to you, if you ask anything of the Father in My name, He will give it to you.' Behold, in Thy name, I ask the Father for the grace of (mention petition).
Our Father... Hail Mary... Glory be...
Sacred Heart of Jesus, I place all my trust in Thee.

III. O my Jesus, Who hast said: 'Truly I say to you, heaven and earth shall pass away, but My words shall not pass away.' Encouraged by Thine infallible words, I now ask for the grace of (mention petition).
Our Father... Hail Mary... Glory be...
Sacred Heart of Jesus, I place all my trust in Thee.

O Sacred Heart of Jesus, for whom it is impossible not to have compassion on the afflicted, have pity on us miserable sinners, and grant us the grace which we ask of Thee, through the Sorrowful and Immaculate Heart of Mary, Thy tender Mother and ours.
Hail, Holy Queen... St. Joseph, foster father of Jesus, pray for us.""",
            latinText = """I. O mi Iesu, qui dixisti: 'Petite et accipietis, quaerite et invenietis, pulsate et aperietur vobis.' En pulso, quaero, et peto gratiam...
Pater noster... Ave Maria... Gloria Patri...
Sacratissimum Cor Iesu, in te confido.

II. O mi Iesu, qui dixisti: 'Quidquid petieritis Patrem in nomine meo, dabit vobis.' En in nomine tuo a Patre peto gratiam...
Pater noster... Ave Maria... Gloria Patri...
Sacratissimum Cor Iesu, in te confido.

III. O mi Iesu, qui dixisti: 'Caelum et terra transibunt, verba autem mea non praeteribunt.' Verbis tuis fretus, peto gratiam...
Pater noster... Ave Maria... Gloria Patri...
Sacratissimum Cor Iesu, in te confido.

O Sacratissimum Cor Iesu, cui impossibile est non misereri afflictorum, miserere nostri miserorum peccatorum, et concede nobis gratiam quam a te petimus, per Dolorosum et Immaculatum Cor Mariae, Matris tuae et nostrae.
Salve Regina... Sancte Ioseph, ora pro nobis. Amen."""
        ),
        PrayerItem(
            id = "morning_offering_sacred_heart",
            title = "Daily Offering to the Sacred Heart of Jesus",
            category = "Sacred Heart Devotions",
            subtitle = "Apostleship of Prayer (1844, approved by Pius IX & Leo XIII)",
            subsection = "Classical Devotions",
            englishText = """O Jesus, through the Immaculate Heart of Mary, I offer Thee my prayers, works, joys, and sufferings of this day, for all the intentions of Thy Sacred Heart, in union with the Holy Sacrifice of the Mass throughout the world, in reparation for my sins, for the intentions of all our associates, and in particular for the intentions of obtaining a Sovereign Pontiff. Amen.

(Note: Modified by RegulaVitae for the relevance of our time.)""",
            latinText = """O Iesu, per Immaculatum Cor Mariae, offero tibi orationes, opera, gaudia et passiones huius diei, pro omnibus intentionibus Sacratissimi Cordis tui, in unione cum Sancto Missae Sacrificio ubique terrarum celebrato, in reparationem peccatorum meorum, et pro intentionibus ad impetrandum Summum Pontificem. Amen.""",
            notes = "Modified by RegulaVitae for the relevance of our time."
        ),
        PrayerItem(
            id = "prayer_st_gertrude_sacred_heart",
            title = "Prayer of St. Gertrude the Great to the Sacred Heart",
            category = "Sacred Heart Devotions",
            subtitle = "Living Fountain of Eternal Life (St. Gertrude, 1256–1302)",
            subsection = "Classical Devotions",
            englishText = """O Sacred Heart of Jesus, living and life-giving fountain of eternal life, infinite treasury of the Divinity, and burning furnace of divine love, Thou art my refuge and my sanctuary.

O my loving Savior, consume my heart with that burning fire with which Thine own is ever inflamed. Pour down upon my soul those graces which flow from Thy love. Let my heart be so united with Thine that our wills may be one, and that mine may in all things be conformed to Thine. Amen.""",
            latinText = """O Sacratissimum Cor Iesu, fons vivus et vivificans vitae aeternae, infinitus thesaurus Divinitatis, et fornax ardens divini amoris, tu es refugium meum et sanctuarium meum.

O Salvator amantissime, consume cor meum illo ardenti igne quo tuum indesinenter inflammatur. Effunde super animam meam gratias quae ex amore tuo manant. Sit cor meum ita tuo unitum, ut una sit voluntas nostra, et mea in omnibus tuae conformetur. Amen."""
        ),
        PrayerItem(
            id = "prayer_st_bonaventure_sacred_heart",
            title = "Prayer of St. Bonaventure to the Sacred Heart",
            category = "Sacred Heart Devotions",
            subtitle = "The Wound of Divine Love (St. Bonaventure, Doctor Seraphicus)",
            subsection = "Classical Devotions",
            englishText = """O most sweet Jesus, pierce the inmost depths of my soul with the most joyous and health-giving wound of Thy love, with true, serene, and most holy apostolic charity, that my soul may ever languish and melt with love and longing for Thee alone.

May my soul yearn for Thee and faint for Thy courts; may it long to be dissolved and to be with Thee. Grant that my heart may ever hunger after Thee, the Bread of Angels, the refreshment of holy souls, our daily and supersubstantial Bread. Amen.""",
            latinText = """Transfige, dulcissime Domine Iesu, medullas et viscera animae meae suavissimo ac saluberrimo amoris tui vulnere, vera serenaque et apostolica sanctissima caritate, ut languat et liquefiat anima mea solo semper amore et desiderio tui.

Te concupiscat et deficiat in atria tua, cupiat dissolvi et esse tecum. Da ut cor meum semper esuriat te, Panem Angelorum, refectionem animarum sanctarum, Panem nostrum quotidianum et supersubstantialem. Amen."""
        ),
        PrayerItem(
            id = "act_resignation_sickness_sacred_heart",
            title = "Act of Resignation & Offering in Sickness to the Sacred Heart",
            category = "Sacred Heart Devotions",
            subtitle = "Written by Saint Margaret Mary Alacoque",
            subsection = "Classical Devotions",
            englishText = """O Sacred Heart of Jesus, I accept from Thy hand this sickness and all the sufferings, bodily and spiritual, which it brings, in union with the bitter agony and sufferings of Thy Sacred Heart.

I offer them to Thee in expiation of my sins, for the relief of the souls in Purgatory, and for the sanctification of the Holy Catholic Church. Dispose of me, O Lord, in life and in death, according to Thy holy and adorable will. Amen.""",
            latinText = """O Sacratissimum Cor Iesu, accipio de manu tua hanc infirmitatem et omnes dolores corporales et spirituales quos affert, in unione cum amara agonia et passionibus Sacratissimi Cordis tui.

Offerro ea tibi in expiationem peccatorum meorum, pro levamine animarum in Purgatorio, et pro sanctificatione Sanctae Ecclesiae Catholicae. Dispone de me, Domine, in vita et in morte, secundum sanctam et adorabilem voluntatem tuam. Amen."""
        ),
        PrayerItem(
            id = "sacred_heart_aspiration",
            title = "Indulgenced Aspirations to the Sacred Heart of Jesus",
            category = "Sacred Heart Devotions",
            subtitle = "From the Roman Raccolta (Raccolta 251, 249, 267, 250)",
            subsection = "Invocations & Aspirations",
            englishText = """V. Sacred Heart of Jesus, I place my trust in Thee!
(Raccolta 251 - 300 Days Indulgence)

V. Jesus, meek and humble of Heart, make our hearts like unto Thine!
(Raccolta 249 - 300 Days Indulgence)

V. Eucharistic Heart of Jesus, have mercy on us!
(Raccolta 267 - 300 Days Indulgence)

V. May the Sacred Heart of Jesus be loved everywhere!
(Raccolta 250 - 100 Days Indulgence)""",
            latinText = """V. Sacratissimum Cor Iesu, in te confido!
(Raccolta 251)

V. Iesu, mitis et humilis Corde, fac cor nostrum secundum Cor tuum!
(Raccolta 249)

V. Cor Iesu Eucharisticum, miserere nobis!
(Raccolta 267)

V. Sit laus, honor et gloria Cordi Divino Iesu per infinita saeculorum saecula!
(Raccolta 250)"""
        )
    )

    val traditionalCreeds = listOf(
        PrayerItem(
            id = "creed_apostles",
            title = "The Apostles' Creed (Symbolum Apostolorum)",
            category = "Traditional Creeds",
            subtitle = "Ancient Baptismal Symbol of the Primitive Roman Church (c. 1st–2nd Century)",
            notes = "The foundational rule of faith handed down from the Apostles, recited at the beginning of the Holy Rosary and at Prime in the Divine Office.",
            englishText = """I BELIEVE in God, the Father Almighty, Creator of heaven and earth; and in Jesus Christ, His only Son, my Lord: Who was conceived by the Holy Ghost, born of the Virgin Mary, suffered under Pontius Pilate, was crucified, died, and was buried. He descended into hell; the third day He arose again from the dead; He ascended into heaven, sitteth at the right hand of God the Father Almighty; from thence He shall come to judge the living and the dead. I believe in the Holy Ghost, the Holy Catholic Church, the communion of saints, the forgiveness of sins, the resurrection of the body, and life everlasting. Amen.""",
            latinText = """CREDO in Deum Patrem omnipotentem, Creatorem caeli et terrae. Et in Iesum Christum, Filium eius unicum, Dominum nostrum: qui conceptus est de Spiritu Sancto, natus ex Maria Virgine, passus sub Pontio Pilato, crucifixus, mortuus, et sepultus: descendit ad inferos; tertia die resurrexit a mortuis; ascendit ad caelos, sedet ad dexteram Dei Patris omnipotentis: inde venturus est iudicare vivos et mortuos. Credo in Spiritum Sanctum, sanctam Ecclesiam catholicam, Sanctorum communionem, remissionem peccatorum, carnis resurrectionem, vitam aeternam. Amen."""
        ),
        PrayerItem(
            id = "creed_nicene",
            title = "Nicene-Constantinopolitan Creed (Symbolum Nicaenum)",
            category = "Traditional Creeds",
            subtitle = "Dogmatic Creed of the Holy Sacrifice of the Mass (Council of Nicaea 325 AD / Constantinople 381 AD)",
            notes = "Solemnly chanted or recited in the 1954 Missale Romanum on Sundays, Feasts of Our Lord, Our Lady, Apostles, and Doctors of the Church.",
            englishText = """I BELIEVE in one God, the Father Almighty, maker of heaven and earth, and of all things visible and invisible. And in one Lord Jesus Christ, the Only-begotten Son of God, born of the Father before all ages. God of God, Light of Light, true God of true God, begotten, not made, consubstantial with the Father: by Whom all things were made. Who for us men and for our salvation came down from heaven.

[GENUFLECT] AND WAS INCARNATE BY THE HOLY GHOST OF THE VIRGIN MARY, AND WAS MADE MAN.

He was crucified also for us under Pontius Pilate; suffered and was buried, and the third day He rose again, according to the Scriptures, and ascended into heaven, and sitteth at the right hand of the Father. And He shall come again with glory to judge the living and the dead: of Whose kingdom there shall be no end. And in the Holy Ghost, the Lord and Giver of life: Who proceedeth from the Father and the Son. Who together with the Father and the Son is adored and glorified: Who spoke by the prophets. And one, holy, catholic, and apostolic Church. I confess one baptism for the remission of sins. And I look for the resurrection of the dead, and the life of the world to come. Amen.""",
            latinText = """CREDO in unum Deum, Patrem omnipotentem, factorem caeli et terrae, visibilium omnium et invisibilium. Et in unum Dominum Iesum Christum, Filium Dei Unigenitum, et ex Patre natum ante omnia saecula. Deum de Deo, lumen de lumine, Deum verum de Deo vero, genitum, non factum, consubstantialem Patri: per quem omnia facta sunt. Qui propter nos homines et propter nostram salutem descendit de caelis.

[HIC GENUFLECTITUR] ET INCARNATUS EST DE SPIRITU SANCTO EX MARIA VIRGINE, ET HOMO FACTUS EST.

Crucifixus etiam pro nobis sub Pontio Pilato; passus, et sepultus est, et resurrexit tertia die, secundum Scripturas, et ascendit in caelum, sedet ad dexteram Patris. Et iterum venturus est cum gloria, iudicare vivos et mortuos, cuius regni non erit finis. Et in Spiritum Sanctum, Dominum et vivificantem: qui ex Patre Filioque procedit. Qui cum Patre et Filio simul adoratur et conglorificatur: qui locutus est per prophetas. Et unam, sanctam, catholicam et apostolicam Ecclesiam. Confiteor unum baptisma in remissionem peccatorum. Et exspecto resurrectionem mortuorum, et vitam venturi saeculi. Amen."""
        ),
        PrayerItem(
            id = "creed_athanasian",
            title = "The Athanasian Creed (Symbolum Athanasianum / Quicumque Vult)",
            category = "Traditional Creeds",
            subtitle = "Dogmatic Profession on the Trinity & Incarnation (Recited at Prime in the 1954 Breviarium Romanum)",
            notes = "Attributed historically to St. Athanasius of Alexandria (c. 296–373 AD) and solemnly prescribed in the 1954 Roman Breviary for Trinity Sunday.",
            englishText = """WHOSOEVER will be saved, before all things it is necessary that he hold the Catholic Faith: Which Faith except every one do keep whole and undefiled, without doubt he shall perish everlastingly.

And the Catholic Faith is this: That we worship one God in Trinity, and Trinity in Unity; Neither confounding the Persons, nor dividing the Substance. For there is one Person of the Father, another of the Son, and another of the Holy Ghost. But the Godhead of the Father, of the Son, and of the Holy Ghost, is all one, the Glory equal, the Majesty co-eternal.

Such as the Father is, such is the Son, and such is the Holy Ghost. The Father uncreate, the Son uncreate, and the Holy Ghost uncreate. The Father incomprehensible, the Son incomprehensible, and the Holy Ghost incomprehensible. The Father eternal, the Son eternal, and the Holy Ghost eternal. And yet they are not three eternals, but one eternal. As also there are not three incomprehensibles, nor three uncreated, but one uncreated, and one incomprehensible. So likewise the Father is Almighty, the Son Almighty, and the Holy Ghost Almighty. And yet they are not three Almighties, but one Almighty.

So the Father is God, the Son is God, and the Holy Ghost is God. And yet they are not three Gods, but one God. So likewise the Father is Lord, the Son Lord, and the Holy Ghost Lord. And yet not three Lords, but one Lord. For like as we are compelled by the Christian verity to acknowledge every Person by Himself to be God and Lord; So are we forbidden by the Catholic Religion to say, There be three Gods, or three Lords.

The Father is made of none, neither created, nor begotten. The Son is of the Father alone, not made, nor created, but begotten. The Holy Ghost is of the Father and of the Son, neither made, nor created, nor begotten, but proceeding. So there is one Father, not three Fathers; one Son, not three Sons; one Holy Ghost, not three Holy Ghosts. And in this Trinity none is afore or after other, none is greater or less than another; But the whole three Persons are co-eternal together, and co-equal. So that in all things, as is aforesaid, the Unity in Trinity, and the Trinity in Unity is to be worshipped. He therefore that will be saved must thus think of the Trinity.

Furthermore, it is necessary to everlasting salvation that he also believe faithfully the Incarnation of Our Lord Jesus Christ. For the right Faith is, that we believe and confess that Our Lord Jesus Christ, the Son of God, is God and Man; God, of the Substance of the Father, begotten before the worlds; and Man, of the Substance of His Mother, born in the world; Perfect God, and perfect Man, of a reasonable soul and human flesh subsisting; Equal to the Father, as touching His Godhead; and inferior to the Father, as touching His Manhood. Who although He be God and Man, yet He is not two, but one Christ; One, not by conversion of the Godhead into flesh, but by taking of the Manhood into God; One altogether, not by confusion of Substance, but by unity of Person. For as the reasonable soul and flesh is one man, so God and Man is one Christ; Who suffered for our salvation, descended into hell, rose again the third day from the dead. He ascended into heaven, He sitteth on the right hand of the Father, God Almighty, from whence He shall come to judge the quick and the dead. At Whose coming all men shall rise again with their bodies, and shall give account for their own works. And they that have done good shall go into life everlasting, and they that have done evil into everlasting fire.

This is the Catholic Faith, which except a man believe faithfully and firmly, he cannot be saved. Amen.""",
            latinText = """QUICUMQUE vult salvus esse, ante omnia opus est, ut teneat catholicam fidem: Quam nisi quisque integram inviolatamque servaverit, absque dubio in aeternum peribit.

Fides autem catholica haec est: ut unum Deum in Trinitate, et Trinitatem in unitate veneremur. Neque confundentes personas, neque substantiam separantes. Alia est enim persona Patris, alia Filii, alia Spiritus Sancti. Sed Patris, et Filii, et Spiritus Sancti una est divinitas, aequalis gloria, coaeterna maiestas.

Qualis Pater, talis Filius, talis Spiritus Sanctus. Increatus Pater, increatus Filius, increatus Spiritus Sanctus. Immensus Pater, immensus Filius, immensus Spiritus Sanctus. Aeternus Pater, aeternus Filius, aeternus Spiritus Sanctus. Et tamen non tres aeterni, sed unus aeternus. Sicut non tres increati, nec tres immensi, sed unus increatus, et unus immensus. Similiter omnipotens Pater, omnipotens Filius, omnipotens Spiritus Sanctus. Et tamen non tres omnipotentes, sed unus omnipotens.

Ita Deus Pater, Deus Filius, Deus Spiritus Sanctus. Et tamen non tres Dei, sed unus est Deus. Ita Dominus Pater, Dominus Filius, Dominus Spiritus Sanctus. Et tamen non tres Domini, sed unus est Dominus. Quia sicut singillatim unamquamque personam Deum ac Dominum confiteri christiana veritate compellimur: ita tres Deos aut Dominos dicere catholica religione prohibemur.

Pater a nullo est factus: nec creatus, nec genitus. Filius a Patre solo est: non factus, nec creatus, sed genitus. Spiritus Sanctus a Patre et Filio: non factus, nec creatus, nec genitus, sed procedens. Unus ergo Pater, non tres Patres: unus Filius, non tres Filii: unus Spiritus Sanctus, non tres Spiritus Sancti. Et in hac Trinitate nihil prius aut posterius, nihil maius aut minus: sed totae tres personae coaeternae sibi sunt et coaequales. Ita ut per omnia, sicut iam supra dictum est, et unitas in Trinitate, et Trinitas in unitate veneranda sit. Qui vult ergo salvus esse, ita de Trinitate sentiat.

Sed necessarium est ad aeternam salutem, ut Incarnationem quoque Domini nostri Iesu Christi fideliter credat. Est ergo fides recta ut credamus et confiteamur, quia Dominus noster Iesus Christus, Dei Filius, Deus et homo est. Deus est ex substantia Patris ante saecula genitus: et homo est ex substantia matris in saeculo natus. Perfectus Deus, perfectus homo: ex anima rationali et humana carne subsistens. Aequalis Patri secundum divinitatem: minor Patre secundum humanitatem. Qui licet Deus sit et homo, non duo tamen, sed unus est Christus. Unus autem non conversione divinitatis in carnem, sed assumptione humanitatis in Deum. Unus omnino, non confusione substantiae, sed unitate personae. Nam sicut anima rationalis et caro unus est homo: ita Deus et homo unus est Christus. Qui passus est pro salute nostra: descendit ad inferos: tertia die resurrexit a mortuis. Ascendit ad caelos, sedet ad dexteram Dei Patris omnipotentis: inde venturus est iudicare vivos et mortuos. Ad cuius adventum omnes homines resurgere habent cum corporibus suis: et reddituri sunt de factis propriis rationem. Et qui bona egerunt, ibunt in vitam aeternam: qui vero mala, in ignem aeternum.

Haec est fides catholica, quam nisi quisque fideliter firmiterque crediderit, salvus esse non poterit. Amen."""
        ),
        PrayerItem(
            id = "creed_tridentine",
            title = "Tridentine Profession of Faith (Professio Fidei Tridentinae)",
            category = "Traditional Creeds",
            subtitle = "Solemn Oath of Faith Promulgated by Pope Pius IV (1564) and Re-affirmed by Vatican I (1870)",
            notes = "Required of all converts, pastors, bishops, and theologians in the 1954 Codex Iuris Canonici (Can. 1406).",
            englishText = """I, N., with a firm faith believe and profess each and every article contained in the Symbol of Faith which the Holy Roman Church uses, namely:

I believe in one God, the Father Almighty, maker of heaven and earth, and of all things visible and invisible. And in one Lord Jesus Christ, the Only-begotten Son of God, born of the Father before all ages. God of God, Light of Light, true God of true God, begotten, not made, consubstantial with the Father: by Whom all things were made. Who for us men and for our salvation came down from heaven, and was incarnate by the Holy Ghost of the Virgin Mary, and was made man. He was crucified also for us under Pontius Pilate; suffered and was buried, and the third day He rose again, according to the Scriptures, and ascended into heaven, and sitteth at the right hand of the Father. And He shall come again with glory to judge the living and the dead: of Whose kingdom there shall be no end. And in the Holy Ghost, the Lord and Giver of life: Who proceedeth from the Father and the Son. Who together with the Father and the Son is adored and glorified: Who spoke by the prophets. And one, holy, catholic, and apostolic Church. I confess one baptism for the remission of sins. And I look for the resurrection of the dead, and the life of the world to come. Amen.

I most firmly accept and embrace the Apostolic and ecclesiastical traditions and other observances and constitutions of that same Church. I likewise accept Holy Scripture according to that sense which Holy Mother Church has held and holds, to whom it belongs to judge of the true sense and interpretation of the Sacred Scriptures; nor will I ever accept or interpret it except according to the unanimous consent of the Fathers.

I also profess that there are truly and properly seven sacraments of the New Law instituted by Jesus Christ Our Lord, and necessary for the salvation of mankind: namely, Baptism, Confirmation, the Eucharist, Penance, Extreme Unction, Holy Orders, and Matrimony; and that they confer grace.

I embrace and receive each and every definition and declaration of the Sacred Council of Trent concerning original sin and justification. I likewise profess that in the Mass there is offered to God a true, proper, and propitiatory Sacrifice for the living and the dead, and that in the Most Holy Sacrament of the Eucharist there is truly, really, and substantially the Body and Blood, together with the Soul and Divinity, of Our Lord Jesus Christ.

I steadfastly hold that there is a Purgatory, and that the souls therein detained are helped by the suffrages of the faithful; likewise that the Saints reigning together with Christ are to be honored and invoked.

I acknowledge the Holy Catholic and Apostolic Roman Church as the mother and mistress of all churches, and I promise and swear true obedience to the Roman Pontiff, successor of Blessed Peter Prince of the Apostles and Vicar of Jesus Christ.

I likewise undoubtedly receive and profess all other things handed down, defined, and declared by the sacred Canons and Ecumenical Councils, and especially by the Sacred Council of Trent and by the Ecumenical Vatican Council.

This true Catholic Faith, outside of which no one can be saved, which I now freely profess and truly hold, I promise, vow, and swear that I will most constantly retain and confess the same entire and inviolate until the last breath of my life, with God's help. So help me God, and these His holy Gospels. Amen.""",
            latinText = """Ego N. firma fide credo et profiteor omnia et singula, quae continentur in Symbolo Fidei, quo sancta Romana Ecclesia utitur, videlicet:

Credo in unum Deum, Patrem omnipotentem, factorem caeli et terrae, visibilium omnium et invisibilium. Et in unum Dominum Iesum Christum, Filium Dei Unigenitum, et ex Patre natum ante omnia saecula. Deum de Deo, lumen de lumine, Deum verum de Deo vero, genitum, non factum, consubstantialem Patri: per quem omnia facta sunt. Qui propter nos homines et propter nostram salutem descendit de caelis, et incarnatus est de Spiritu Sancto ex Maria Virgine, et homo factus est. Crucifixus etiam pro nobis sub Pontio Pilato; passus, et sepultus est, et resurrexit tertia die, secundum Scripturas, et ascendit in caelum, sedet ad dexteram Patris. Et iterum venturus est cum gloria, iudicare vivos et mortuos, cuius regni non erit finis. Et in Spiritum Sanctum, Dominum et vivificantem: qui ex Patre Filioque procedit. Qui cum Patre et Filio simul adoratur et conglorificatur: qui locutus est per prophetas. Et unam, sanctam, catholicam et apostolicam Ecclesiam. Confiteor unum baptisma in remissionem peccatorum. Et exspecto resurrectionem mortuorum, et vitam venturi saeculi. Amen.

Apostolicas et ecclesiasticas traditiones, reliquasque eiusdem Ecclesiae observationes et constitutiones firmissime admitto et amplector. Item sacram Scripturam iuxta eum sensum, quem tenuit et tenet sancta Mater Ecclesia, cuius est iudicare de vero sensu et interpretatione sacrarum Scripturarum, admitto; nec eam unquam nisi iuxta unanimem consensum Patrum accipiam et interpretabor.

Profiteor quoque septem esse proprie et vere Sacramenta novae legis a Iesu Christo Domino nostro instituta...

Amplector et recipio omnia et singula, quae in sacrosancta Tridentina Synodo definita et declarata sunt de peccato originali et de iustificatione... Profitentur pariter in Missa offerri Deo verum, proprium et propitiatorium Sacrificium pro vivis et defunctis...

Constanter teneo Purgatorium esse, animasque ibi detentas fidelium suffragiis iuvari...

Sanctam catholicam et apostolicam Romanam Ecclesiam omnium ecclesiarum matrem et magistram agnosco, Romanoque Pontifici, beati Petri Apostolorum Principis successori ac Iesu Christi Vicario, veram obedientiam spondeo ac iuro.

Caetera item omnia a sacris Canonibus et Oecumenicis Conciliis, ac praecipue a sacrosancta Tridentina Synodo, et ab Oecumenico Concilio Vaticano tradita, definita et declarata, indubitanter recipio atque profiteor...

Hanc veram catholicam fidem, extra quam nemo salvus esse potest, quam in praesenti sponte profiteor et veraciter teneo, eandem integram et immaculatam usque ad extremum vitae spiritum constantissime, Deo adiuvante, retinere et confiteri me spondeo, voveo et iuro. Ita me Deus adiuvet, et haec sancta Dei Evangelia. Amen."""
        ),
        PrayerItem(
            id = "creed_oath_anti_modernism",
            title = "Oath Against Modernism (Iuramentum Contra Errores Modernismi)",
            category = "Traditional Creeds",
            subtitle = "Promulgated by Pope St. Pius X in the Motu Proprio 'Sacrorum Antistitum' (September 1, 1910)",
            notes = "Required of all clergy, confessors, preachers, religious superiors, and seminary professors in the 1954 Roman Rite.",
            englishText = """I, N., firmly embrace and accept each and every definition, assertion, and declaration of the unerring Magisterium of the Church, especially those principal doctrines which are directly opposed to the errors of the present day.

First: I profess that God, the origin and end of all things, can be known with certainty by the natural light of human reason through the created world, and that His existence can even be demonstrated.

Second: I accept and acknowledge the external proofs of revelation, that is, divine facts, especially miracles and prophecies, as most certain signs of the divine origin of the Christian religion.

Third: I likewise believe with a firm faith that the Church, guardian and teacher of the revealed Word, was directly and proximately instituted by the real and historical Christ Himself during His life among us, and built upon Peter, the prince of the apostolic hierarchy, and his successors unto the end of time.

Fourth: I sincerely accept the doctrine of faith transmitted to us from the Apostles through the orthodox Fathers, in the same sense and always in the same meaning; and therefore I completely reject the heretical misrepresentation of the evolution of dogmas.

Fifth: I hold with complete certainty and sincerely profess that faith is not a blind religious feeling welling up from the secret recesses of the subconscious under the impulse of the heart and the motion of a morally trained will, but a genuine assent of the intellect to truth received externally by hearing.

I also submit with my whole heart to all the condemnations, declarations, and prescriptions contained in the Encyclical 'Pascendi' and the Decree 'Lamentabili'... All these things I promise to observe faithfully, entirely, and sincerely, and to guard inviolably. So help me God, and these His holy Gospels. Amen.""",
            latinText = """Ego N. firmiter amplector et recipio omnia et singula, quae ab inerranti Ecclesiae magisterio definita, asserta ac declarata sunt, praesertim ea doctrinae capita, quae hodierni temporis erroribus ex adverso opponuntur.

Primo quidem: Deum, rerum omnium principium et finem, naturali rationis lumine per ea quae facta sunt, hoc est per visibilia creationis opera, tanquam causam per effectus, certo cognosci, adeoque demonstrari etiam posse, profiteor.

Secundo: Externa revelationis argumenta, hoc est facta divina, in primisque miracula et prophetias, ut signa certissima divinitus ortae christianae religionis admitto et agnosco.

Tertio: Firma pariter fide credo, Ecclesiam, verbi incarnati custodem et magistram, per ipsum supremum et verum historicum Christum, cum apud nos degeret, proxime et directe institutam, eandemque super Petrum, apostolicae hierarchiae principem, eiusque in aevum successores aedificatam.

Quarto: Fidei doctrinam ab Apostolis per orthodoxos Patres eodem sensu eademque semper sententia ad nos usque transmissam, sincere recipio; proindeque hereticum commentum de dogmatum evolutione penitus reiicio.

Quinto: Certissime teneo ac sincere profiteor, fidem non esse sensum religiosum e latebris subconscientiae erumpentem sub impulsione cordis et inflexione voluntatis moraliter informatae, sed verum assensum intellectus veritati extrinsecus ex auditu acceptae.

Submitto etiam me toto animo condemnationibus, declarationibus, praescriptis omnibus, quae in Encyclicis Litteris 'Pascendi' et in Decreto 'Lamentabili' continentur... Haec omnia fideliter, integre sincereque servaturum et inviolabiliter custoditurum me spondeo. Ita me Deus adiuvet, et haec sancta Dei Evangelia. Amen."""
        ),
        PrayerItem(
            id = "creed_damasus",
            title = "The Creed of St. Damasus (Symbolum Damasi)",
            category = "Traditional Creeds",
            subtitle = "Ancient Roman Profession of Faith on the Most Holy Trinity and Incarnation (c. 382 AD)",
            notes = "Preserved in traditional Latin liturgical collections and dogmatic codices of the Roman Church.",
            englishText = """WE BELIEVE in one God, the Father Almighty, and in one Lord Jesus Christ, the Son of God, and in the Holy Ghost. We confess and believe not three gods, but one God: Father, Son, and Holy Ghost; one God in Trinity and Trinity in Unity.

We believe that the Son of God, God born of the Father before all ages, was also born in time of the Virgin Mary; He suffered, was buried, rose again on the third day, ascended into heaven, sitteth at the right hand of the Father, and shall come to judge the living and the dead. And we believe in the Holy Ghost, God, not created, but proceeding from the Father and the Son, co-eternal and consubstantial with the Father and the Son.

This Catholic Faith we profess and hold. Amen.""",
            latinText = """CREDIMUS in unum Deum Patrem omnipotentem, et in unum Dominum Iesum Christum Filium Dei, et in Spiritum Sanctum. Deum non tres Deos, sed Patrem et Filium et Spiritum Sanctum unum Deum confitemur et credimus; unum Deum in Trinitate et Trinitatem in unitate.

Credimus Filium Dei, Deum natum a Patre ante omnia saecula, et eundem in tempore natum ex Maria Virgine; passum, sepultum, tertia die resurgentem, ascendentem in caelos, sedentem ad dexteram Patris, venturum iudicare vivos et mortuos. Et in Spiritum Sanctum Deum, non creatum, sed de Patre et Filio procedentem, coaeternum et consubstantialem Patri et Filio.

Hanc fidem catholicam profitemur et tenemus. Amen."""
        ),
        PrayerItem(
            id = "creed_toledo_xi",
            title = "Creed of the XI Council of Toledo (Symbolum Toletanum XI)",
            category = "Traditional Creeds",
            subtitle = "Monumental Latin Dogmatic Declaration on the Most Holy Trinity and Incarnation (675 AD)",
            notes = "Formally referenced in traditional Roman dogmatic manual theology as one of the most magnificent formulations of Trinitarian theology.",
            englishText = """WE CONFESS and believe the holy and ineffable Trinity, Father, Son, and Holy Ghost, one God by natural property, not by plurality, of one essence, one majesty, and power.

The Father is made of none, neither created, nor begotten. The Son is of the Father alone, not made, nor created, but begotten. The Holy Ghost is neither begotten nor created, but proceeding from the Father and the Son.

We likewise believe that the Son of God, Who is the second Person in the Trinity, is true God and true Man: not by conversion of the Godhead into flesh, but by taking of humanity into the Divine Person.

In this truth of faith we live and abide, and we profess to guard the same undefiled unto the end of life. Amen.""",
            latinText = """CONFITEMUR et credimus sanctam et ineffabilem Trinitatem, Patrem et Filium et Spiritum Sanctum, unum Deum naturali proprietate, non pluralitate, unius essentiae, unius maiestatis atque virtutis.

Pater a nullo est factus, nec creatus, nec genitus. Filius a Patre est non factus, nec creatus, sed genitus. Spiritus Sanctus quoque nec genitus nec creatus, sed a Patre Filioque procedens est.

Credimus quoque Filium Dei, qui est secunda in Trinitate Persona, verum Deum esse et verum hominem: non conversione divinitatis in carnem, sed assumptione humanitatis in personam divinam.

In hac fidei veritate vivimus et permanemus, et eandem usque ad finem vitae intemeratam custodire profitemur. Amen."""
        )
    )

    val prayersBeforeWork = listOf(
        PrayerItem(
            id = "prayer_before_work",
            title = "Prayer before work",
            category = "Prayer before Work",
            subtitle = "To St. Joseph the Worker (Pope St. Pius X, 1906)",
            notes = "An indulgenced prayer to St. Joseph, Patron of Workers, asking for grace to work in a spirit of penance, gratitude, order, and devotion.",
            englishText = """Glorious St. Joseph, model of all who are devoted to labor, obtain for me the grace to work in the spirit of penance in expiation of my many sins; to work conscientiously by placing the love of duty above my inclinations; to gratefully and joyfully deem it an honor to employ and to develop by labor the gifts I have received from God; to work methodically, peacefully, in moderation and patience, without ever shrinking from hardship and difficulties; to work above all with purity of intention and detachment from self, having unceasingly before my eyes death and the account I have to render of time lost, talents unused, good omitted, and vain complacency in success, so fatal to the work of God.

All for Jesus, all through Mary, all in imitation of thee, O Patriarch St. Joseph! This shall be my motto in life and in death. Amen.""",
            latinText = """Gloriose Sancte Ioseph, exemplar omnium qui labori sunt addicti, impetra mihi gratiam laborandi in spiritu paenitentiae pro expiatione multorum peccatorum meorum; laborandi conscientia ductum, anteponendo officii amorem propriis inclinationibus; laborandi cum gratitudine et gaudio, honoris loco ducentem adhibere et evolvere per laborem dona a Deo accepta; laborandi cum ordine, pace, moderatione et patience, nunquam succumbentem fatigationi vel difficultatibus; laborandi praesertim cum puritate intentionis et cum abnegatione sui ipsius, semper prae oculis habentem mortem et rationem reddendam de tempore amisso, de talentis inusitatis, de bono omisso, de vana complacentia in successu, tam noxia operi Dei.

Omnia pro Iesu, omnia per Mariam, omnia ad imitationem tuam, O Patriarcha Sancte Ioseph! Hoc erit synthema meum in vita et in morte. Amen."""
        )
    )

    val prayersBeforeStudy = listOf(
        PrayerItem(
            id = "prayer_before_study",
            title = "Prayer before study",
            category = "Prayer before Study",
            subtitle = "By St. Thomas Aquinas (Doctor Angelicus)",
            notes = "Traditional prayer composed by St. Thomas Aquinas before studying, preaching, or writing, asking for clarity of understanding and subtlety of interpretation.",
            englishText = """Creator of all things, true Source of light and wisdom, lofty Origin of all being, graciously let a ray of Your brilliance penetrate into the darkness of my understanding and take from me the double darkness in which I have been born, an obscurity of both sin and ignorance.

Give me a sharp sense of understanding, a retentive memory, and the ability to grasp things correctly and fundamentally. Grant me the favor of being exact in my explanations, and the ability to express myself with thoroughness and charm.

Point out the beginning, direct the progress, and help in the completion; through Christ Our Lord. Amen.""",
            latinText = """Creator ineffabilis, qui de thesauris sapientiae tuae tres Angelorum hierarchias designasti, et eas super caelum empyreum miro ordine collocasti, et universi partes elegantissime distribuisti:

Tu, inquam, qui verus fons luminis et sapientiae diceris, et supereminens principium, infundere digneris super intellectus mei tenebras tuae radium claritatis, duplices, in quibus natus sum, a me removens tenebras, peccatum scilicet et ignorantiam.

Tu, qui linguas infantium facis disertas, linguae meae erudias et in labiis meis gratiam tuae benedictionis infundas. Da mihi intelligendi acumen, retinendi capacitatem, interpretandi methodum et facilitatem, discendi subtilitatem, loquendi gratiam uberem.

Ingressum instruas, progressum dirigas, egressum compleas: Tu, qui es verus Deus et homo, qui vivis et regnas in saecula saeculorum. Amen."""
        )
    )

    val rosarySets = listOf(
        RosarySet(
            category = "Joyful Mysteries",
            days = "Mondays & Thursdays (Traditional)",
            mysteries = listOf(
                RosaryMystery(1, "The Annunciation", "Humility", "The Archangel Gabriel announces to Mary that she is to become the Mother of God. Mary responds: 'Behold the handmaid of the Lord.'", "Annuntiatio"),
                RosaryMystery(2, "The Visitation", "Charity toward Neighbor", "Mary visits her cousin Elizabeth, who is pregnant with St. John the Baptist. At Mary's greeting, John leaps in the womb.", "Visitatio"),
                RosaryMystery(3, "The Nativity of Our Lord", "Detachment from Worldly Wealth", "Jesus Christ is born in a humble manger in Bethlehem under angelic choirs singing Glory to God.", "Nativitas"),
                RosaryMystery(4, "The Presentation in the Temple", "Obedience & Purity", "Forty days after His birth, Mary presents Jesus in the Temple. Holy Simeon prophesies that a sword shall pierce her heart.", "Praesentatio"),
                RosaryMystery(5, "The Finding of Jesus in the Temple", "True Wisdom & Zeal for God", "After three days of agonizing search, Mary and Joseph find the twelve-year-old Jesus sitting among the doctors of the Law.", "Inventio in Templo")
            )
        ),
        RosarySet(
            category = "Sorrowful Mysteries",
            days = "Tuesdays & Fridays (Traditional)",
            mysteries = listOf(
                RosaryMystery(1, "The Agony in the Garden", "Contrition for Sin", "Jesus prays in agony in Gethsemane, sweating drops of blood as He accepts the chalice of our sins.", "Agonia in Horto"),
                RosaryMystery(2, "The Scourging at the Pillar", "Mortification of the Senses", "Christ is bound to a cold pillar and brutally scourged by Roman soldiers, suffering raw flesh wound for our purity.", "Flagellatio"),
                RosaryMystery(3, "The Crowning with Thorns", "Moral Courage & Humility", "A crown of sharp thorns is crushed onto Our Savior's Sacred Head as soldiers mock His divine Kingship.", "Coronatio Spinis"),
                RosaryMystery(4, "The Carrying of the Cross", "Patience in Suffering", "Jesus carries the heavy wooden Cross through the streets of Jerusalem to Calvary, falling three times.", "Bajulatio Crucis"),
                RosaryMystery(5, "The Crucifixion and Death", "Self-Sacrifice & Forgiveness", "Christ is nailed to the Cross, hanging in agony for three hours before committing His Spirit to the Father for our redemption.", "Crucifixio")
            )
        ),
        RosarySet(
            category = "Glorious Mysteries",
            days = "Wednesdays, Saturdays & Sundays (Traditional)",
            mysteries = listOf(
                RosaryMystery(1, "The Resurrection", "Faith", "On the third day, Christ breaks the bonds of death and rises gloriously from the tomb, victorious over sin.", "Resurrectio"),
                RosaryMystery(2, "The Ascension into Heaven", "Hope & Heavenly Mindedness", "Forty days after His Resurrection, Jesus ascends into Heaven before His Apostles, promising to prepare a place for us.", "Ascensio"),
                RosaryMystery(3, "The Descent of the Holy Ghost", "Love of God & Holy Zeal", "Ten days after Ascension, the Holy Ghost descends upon Mary and the Apostles in the Upper Room as tongues of fire.", "Descensus Spiritus Sancti"),
                RosaryMystery(4, "The Assumption of Our Lady", "Grace of a Holy Death", "Our Blessed Mother is taken up body and soul into Heavenly Glory at the end of her earthly life.", "Assumptio"),
                RosaryMystery(5, "The Coronation of Our Lady", "Perseverance & Trust in Mary", "Mary is crowned Queen of Heaven and Earth by the Most Holy Trinity, reigning as Mother and Intercessor.", "Coronatio in Caelo")
            )
        )
    )
}

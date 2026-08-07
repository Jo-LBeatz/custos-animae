package com.example.data.prayers

data class PrayerItem(
    val id: String,
    val title: String,
    val category: String,
    val subtitle: String = "",
    val englishText: String,
    val latinText: String = "",
    val notes: String = ""
)

data class RosaryMystery(
    val number: Int,
    val title: String,
    val fruit: String,
    val meditation: String,
    val latinTitle: String = ""
)

data class RosarySet(
    val category: String, // Joyful, Sorrowful, Glorious, Luminous
    val days: String,
    val mysteries: List<RosaryMystery>
)

object TraditionalPrayerLibrary {

    val morningPrayers = listOf(
        PrayerItem(
            id = "m_offering",
            title = "Morning Offering",
            category = "Morning Prayers",
            subtitle = "Consecrating the day to the Sacred Heart of Jesus through Mary",
            englishText = """O Jesus, through the Immaculate Heart of Mary, I offer Thee my prayers, works, joys, and sufferings of this day, for all the intentions of Thy Sacred Heart, in union with the Holy Sacrifice of the Mass throughout the world, in reparation for my sins, for the intentions of all my relatives and friends, and in particular for the salvation of souls.""",
            latinText = """O Iesu, per Immaculatum Cor Mariae, offero Tibi orationes, opera, gaudia et passiones huius diei, pro omnibus intentionibus Sacratissimi Cordis Tui, in unione cum Sancto Missae Sacrificio ubique terrarum celebrato, in reparationem peccatorum meorum, et pro intentionibus Sanctorum Pontificum. Amen."""
        ),
        PrayerItem(
            id = "act_faith",
            title = "Act of Faith (Actus Fidei)",
            category = "Morning Prayers",
            englishText = """O my God, I firmly believe that Thou art one God in three Divine Persons, Father, Son, and Holy Ghost. I believe that Thy Divine Son became man, and died for our sins, and that He will come to judge the living and the dead. I believe these and all the truths which the Holy Catholic Church teaches, because Thou hast revealed them, who canst neither deceive nor be deceived.""",
            latinText = """Domine Deus, firma fide credo et confiteor omnia et singula quae sancta Ecclesia Catholica credenda proponit, quia Tu, Deus, ea omnia revelasti, qui es aeterna veritas et sapientia quae nec falli nec fallere potest. In hac fide vivere et mori statuo. Amen."""
        ),
        PrayerItem(
            id = "act_hope",
            title = "Act of Hope (Actus Spei)",
            category = "Morning Prayers",
            englishText = """O my God, relying on Thy almighty power and infinite mercy and promises, I hope to obtain pardon of my sins, the help of Thy grace, and life everlasting, through the merits of Jesus Christ, my Lord and Redeemer.""",
            latinText = """Domine Deus, spero per gratiam tuam remissionem omnium peccatorum, et post hanc vitam aeternam felicitatem me esse consecuturum: quia Tu promisisti, qui es praepotens, fidelissimus et misericors. In hac spe vivere et mori statuo. Amen."""
        ),
        PrayerItem(
            id = "act_charity",
            title = "Act of Charity (Actus Caritatis)",
            category = "Morning Prayers",
            englishText = """O my God, I love Thee above all things, with my whole heart and soul, because Thou art all-good and worthy of all love. I love my neighbor as myself for the love of Thee. I forgive all who have injured me, and ask pardon of all whom I have injured.""",
            latinText = """Domine Deus, amo Te super omnia et propter Te proximum meum sicut me ipsum, quia Tu es summum, infinitum, et perfectissimum bonum, omni dilectione dignum. In hac dilectione vivere et mori statuo. Amen."""
        ),
        PrayerItem(
            id = "guardian_angel",
            title = "Prayer to Guardian Angel (Angele Dei)",
            category = "Morning Prayers",
            englishText = """Angel of God, my guardian dear, to whom God's love commits me here, ever this day be at my side, to light and guard, to rule and guide. Amen.""",
            latinText = """Angele Dei, qui custos es mei, me, tibi commissum pietate superna, illumina, custodi, rege et guberna. Amen."""
        )
    )

    val nightPrayers = listOf(
        PrayerItem(
            id = "anima_christi",
            title = "Anima Christi",
            category = "Night Prayers",
            subtitle = "Soul of Christ, sanctify me",
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
            subtitle = "Traditional Compline Hymn",
            englishText = """To Thee before the close of day, Creator of the world, we pray, That, with Thy wonted favor, Thou Wouldst be our Guard and Keeper now.
From all ill dreams defend our eyes, From nightly fears and fantasies; Tread under foot our ghostly foe, That no pollution we may know.
O Father, that we ask be done, Through Jesus Christ, Thine only Son, Who, with the Holy Ghost and Thee, Doth live and reign eternally. Amen.""",
            latinText = """Te lucis ante terminum, Rerum Creator, poscimus, Ut pro tua clementia Sis praesul et custodia.
Procul recedant somnia, Et noctium phantasmata; Hostemque nostrum comprime, Ne polluantur corpora.
Praesta, Pater piissime, Patrique compar Unice, Cum Spiritu Paraclito Regnans per omne saeculum. Amen."""
        ),
        PrayerItem(
            id = "act_contrition",
            title = "Act of Contrition",
            category = "Night Prayers",
            englishText = """O my God, I am heartily sorry for having offended Thee, and I detest all my sins because of Thy just punishments, but most of all because they offend Thee, my God, who art all-good and deserving of all my love. I firmly resolve, with the help of Thy grace, to sin no more and to avoid the near occasions of sin. Amen.""",
            latinText = """Deus meus, ex toto corde me poenitet omnium meorum peccatorum, eaque detestor, quia peccando, non solum poenas a Te iuste statutas promeritus sum, sed praesertim quia offendi Te, summum bonum, ac dignum qui super omnia ameris. Ideo firmiter propono, adiuvante gratia Tua, de cetero me non peccaturum peccandique occasiones proximas fugiturum. Amen."""
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
            englishText = """St. Michael the Archangel, defend us in battle. Be our protection against the wickedness and snares of the devil. May God rebuke him, we humbly pray, and do thou, O Prince of the Heavenly Host, by the power of God, thrust into hell Satan and all the evil spirits who prowl about the world seeking the ruin of souls. Amen.""",
            latinText = """Sancte Michael Archangele, defende nos in praelio; contra nequitiam et insidias diaboli esto praesidium. Imperet illi Deus, supplices deprecamur: tuque, Princeps militiae caelestis, Satanam aliosque spiritus malignos, qui ad perditionem animarum pervagantur in mundo, divina virtute, in infernum detrude. Amen."""
        ),
        PrayerItem(
            id = "memorare_prayer",
            title = "The Memorare",
            category = "Protection & Saints",
            subtitle = "Prayer of St. Bernard of Clairvaux",
            englishText = """Remember, O most gracious Virgin Mary, that never was it known that anyone who fled to thy protection, implored thy help, or sought thine intercession was left unaided. Inspired by this confidence, I fly unto thee, O Virgin of virgins, my Mother; to thee do I come, before thee I stand, sinful and sorrowful. O Mother of the Word Incarnate, despise not my petitions, but in thy mercy hear and answer me. Amen.""",
            latinText = """Memorare, o piissima Virgo Maria, a saeculo non esse auditum quemquam ad tua currentem praesidia, tua implorantem auxilia, tua petentem suffragia, esse derelictum. Ego tali animatus confidentia, ad te, Virgo Virginum, Mater, curro, ad te venio, coram te gemens peccator assisto. Noli, Mater Verbi, verba mea despicere; sed audi propitia et exaudi. Amen."""
        ),
        PrayerItem(
            id = "st_benedict_prayer",
            title = "Prayer to Saint Benedict for Protection",
            category = "Protection & Saints",
            subtitle = "Oratio ad Sanctum Benedictum",
            englishText = """O glorious Saint Benedict, sublime model of all virtues, pure vessel of God's grace! Behold me, humbly prostrate before thee. I implore thy loving heart to pray for me before the throne of God. To thee I have recourse in all the dangers which daily surround me. Shield me against my enemies, inspire me to imitate thee in all things. May thy blessing be with me always, so that I may flee from sin and avoid the occasions of it.

Graciously obtain for me from God those favors and graces which I so greatly need, in the trials, miseries and afflictions of life. Thy heart was always so full of love, compassion, and mercy towards those who were afflicted or troubled in any way. Thou didst never dismiss without consolation and assistance any one who had recourse to thee. I therefore invoke thy powerful intercession, in the confident hope that thou wilt hear my prayers and obtain for me the special grace and favor I earnestly implore, if it be for the greater glory of God and the welfare of my soul.

Help me, O great Saint Benedict, to live and die as a faithful child of God, to be ever submissive to His holy will, and to attain the eternal happiness of heaven. Amen.""",
            latinText = """Crux sacra sit mihi lux! Non draco sit mihi dux! Vade retro Satana! Nunquam suade mihi vana! Sunt mala quae libas. Ipse venena bibas!

(The Medal of Saint Benedict Exorcism: May the holy Cross be my light! Let not the dragon be my guide! Depart, Satan! Never suggest to me thy vanities! Evil are the things thou profferest. Drink thou thine own poison!)"""
        )
    )

    val devotionsImmaculateHeart = listOf(
        PrayerItem(
            id = "consecration_mary",
            title = "Act of Consecration to the Immaculate Heart of Mary",
            category = "Immaculate Heart Devotions",
            englishText = """O Queen of the Most Holy Rosary and tender Mother of men, to thy Immaculate Heart we solemnly consecrate ourselves, our families, our homes, and our country. We promise to cultivate a genuine spirit of prayer and penance, to fulfill our daily duties faithfully, and to practice the First Saturdays Devotion in reparation for blasphemies committed against thy Motherly Heart. Queen of Peace, reign over our hearts and guide us to eternal salvation.""",
            latinText = """O Regina Sacratissimi Rosarii et dulcissima Mater hominum, Consecramus Nos Regio et Immaculato Cordi Tuo, familias nostras, domos nostras, et patriam nostram. Promittimus nos verum spiritum orationis et poenitentiae culturos, quotidiana officia fideliter impleturos, et Devotionem Primi Sabbati in reparationem blasphemiarum contra Maternum Cor tuum exercituros. Regina Pacis, regna super corda nostra et ad salutem aeternam nos perduc. Amen."""
        ),
        PrayerItem(
            id = "five_first_saturdays",
            title = "The Five First Saturdays Devotion",
            category = "Immaculate Heart Devotions",
            subtitle = "Our Lady's Promise at Fatima",
            englishText = """Our Lady promised to assist at the hour of death with all the graces necessary for salvation to all who, on the First Saturday of 5 consecutive months:
1. Go to Confession (within 8 days before or after).
2. Receive Holy Communion in reparation to the Immaculate Heart.
3. Recite 5 decades of the Holy Rosary.
4. Keep Our Lady company for 15 minutes meditating on the mysteries of the Rosary with the intention of making reparation to her.""",
            latinText = ""
        )
    )

    val devotionsSacredHeart = listOf(
        PrayerItem(
            id = "litany_sacred_heart",
            title = "Litany of the Sacred Heart of Jesus",
            category = "Sacred Heart Devotions",
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
Heart of Jesus, Burning Furnace of Charity, have mercy on us.
Heart of Jesus, Abode of Justice and Love, have mercy on us.
Heart of Jesus, King and Center of all hearts, have mercy on us.
Heart of Jesus, in whom are all the treasures of wisdom and knowledge, have mercy on us.
Heart of Jesus, Salvation of those who trust in Thee, have mercy on us.
Heart of Jesus, Hope of those who die in Thee, have mercy on us.
Heart of Jesus, Delight of all the Saints, have mercy on us.

Lamb of God, who takest away the sins of the world, spare us, O Lord.
Lamb of God, who takest away the sins of the world, graciously hear us, O Lord.
Lamb of God, who takest away the sins of the world, have mercy on us.

V. Jesus, meek and humble of Heart,
R. Make our hearts like unto Thine.""",
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
Cor Iesu, fornax ardens caritatis, miserere nobis.

Agnus Dei, qui tollis peccata mundi, parce nobis, Domine.
Agnus Dei, qui tollis peccata mundi, exaudi nos, Domine.
Agnus Dei, qui tollis peccata mundi, miserere nobis.

V. Iesu, mitis et humilis Corde,
R. Fac cor nostrum secundum Cor tuum."""
        ),
        PrayerItem(
            id = "nine_first_fridays",
            title = "The Nine First Fridays Devotion",
            category = "Sacred Heart Devotions",
            subtitle = "Great Promise of St. Margaret Mary Alacoque",
            englishText = """Our Lord promised to St. Margaret Mary: 'I promise thee in the excessive mercy of My Heart that My all-powerful love will grant to all those who receive Holy Communion on the First Friday in nine consecutive months the grace of final perseverance; they shall not die in My disgrace, nor without receiving their sacraments; My Divine Heart shall be their safe refuge in this last moment.'""",
            latinText = ""
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

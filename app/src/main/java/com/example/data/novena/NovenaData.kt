package com.example.data.novena

import com.example.data.calendar.LiturgicalCalculator
import com.example.data.calendar.SaintData
import java.util.Calendar
import java.util.Locale

data class NovenaDefinition(
    val id: String,
    val feastTitle: String,
    val novenaTitle: String,
    val fixedStartMonth: Int? = null, // 1..12
    val fixedStartDay: Int? = null,   // 1..31
    val fixedFeastMonth: Int? = null, // 1..12
    val fixedFeastDay: Int? = null,   // 1..31
    val category: String, // "MARIAN", "OUR_LORD", "SAINT", "ANGEL", "TEMPORAL"
    val summary: String,
    val intention: String,
    val englishPrayer: String,
    val latinPrayer: String = "",
    val isMovable: Boolean = false,
    val movableType: String = ""
)

data class ActiveNovenaInfo(
    val definition: NovenaDefinition,
    val currentDay: Int, // 1..9
    val isStartDay: Boolean, // true if currentDay == 1
    val feastDateString: String,
    val daysRemainingToFeast: Int
)

object NovenaData {

    val allNovenas: List<NovenaDefinition> = listOf(
        // MARIAN NOVENAS
        NovenaDefinition(
            id = "novena_immaculate_conception",
            feastTitle = "Immaculate Conception of the Blessed Virgin Mary",
            novenaTitle = "Novena to the Immaculate Conception",
            fixedStartMonth = 11, fixedStartDay = 29,
            fixedFeastMonth = 12, fixedFeastDay = 8,
            category = "MARIAN",
            summary = "Prepare your soul for the great Solemnity of Mary's Immaculate Conception through 9 days of purificatory prayer.",
            intention = "Purity of heart, defense of holy holy Catholic doctrine, and total Marian consecration.",
            englishPrayer = """O Most Holy Virgin, who wast pleasing to the Lord and becamest His Mother, immaculate in body and soul, in faith and love, look graciously upon the wretched who implore thy powerful protection. The serpent, against whom the first curse was hurled, continues to attack and ensnare the unhappy children of Eve.

Do thou, then, O blessed Mother, our Queen and Advocate, who from the first instant of thy Conception didst crush the head of the enemy, receive the prayers which we unite with one heart to thine, and beseech thee to present at the Throne of God, that we may never yield to the snares prepared for us.

Obtain for us, O purest Virgin, immaculate purity of mind, heart, and body, and the supreme grace of final perseverance in the holy Catholic Faith. Amen.""",
            latinPrayer = """O Virgo Sacratissima, quae Domino placuisti et ejus Mater facta es, immaculata in corpore et anima, in fide et charitate, respice propitia ad nos miseros qui potens tuum auxilium imploramus. Serpens ille prima maledictione percussus, pugnare et insidias parare non cessat infelicibus filiis Evae.

Tu ergo, o benedicta Mater, Regina et Advocata nostra, quae ab ipso tuae Conceptionis initio caput inimici contrivisti, suscipe preces quas unanimes tibi offerimus, ac te rogamus ut ad Thronum Dei eas praesentes, ne unquam in laqueos nobis paratos incidamus. Amen."""
        ),
        NovenaDefinition(
            id = "novena_guadalupe",
            feastTitle = "Our Lady of Guadalupe, Empress of the Americas",
            novenaTitle = "Novena to Our Lady of Guadalupe",
            fixedStartMonth = 12, fixedStartDay = 3,
            fixedFeastMonth = 12, fixedFeastDay = 12,
            category = "MARIAN",
            summary = "Celebrate the miraculous apparition of Our Lady of Guadalupe on Tepeyac Hill.",
            intention = "Conversion of sinners, protection of the unborn, and defense of holy family life.",
            englishPrayer = """Our Lady of Guadalupe, Mystical Rose, make intercession for the Holy Church, protect the Sovereign Pontiff, help all who invoke thee in their necessities, and since thou art the ever Virgin Mary and Mother of the true God, obtain for us from thy most holy Son the grace of keeping our faith, sweet hope in the midst of the bitterness of life, burning charity, and the precious gift of final perseverance. Amen.""",
            latinPrayer = """Domina Nostra de Guadalupe, Rosa Mystica, intercede pro Sancta Ecclesia, protege Summum Pontificem, adjuva omnes qui te in necessitatibus suis invocant, et cum sis semper Virgo Maria et Mater veri Dei, impetra nobis a sanctissimo Filio tuo gratiam conservandae fidei nostrae, spem dulcem inter vitae amaritudines, ardentem charitatem et pretiosum donum finalis perseverantiae. Amen."""
        ),
        NovenaDefinition(
            id = "novena_christmas",
            feastTitle = "Nativity of Our Lord Jesus Christ (Christmas)",
            novenaTitle = "Christmas Novena (Las Posadas)",
            fixedStartMonth = 12, fixedStartDay = 16,
            fixedFeastMonth = 12, fixedFeastDay = 25,
            category = "OUR_LORD",
            summary = "The ancient 9-day Christmas Novena preparing for the holy Birth of the Savior.",
            intention = "Holy humility, adoration of the Incarnate Word, and peace in Christ.",
            englishPrayer = """Hail, and blessed be the hour and moment in which the Son of God was born of the most pure Virgin Mary, at midnight, in Bethlehem, in piercing cold. In that hour vouchsafe, O my God, to hear my prayer and grant my desires, through the merits of Our Savior Jesus Christ, and of His Blessed Mother. Amen.""",
            latinPrayer = """Ave et benedicta sit hora et momentum in quo Filius Dei natus est ex purissima Virgine Maria, media nocte, in Bethlehem, in acri frigore. In illa hora dignare, O Deus meus, exaudire precem meam et concedere desideria mea, per merita Salvatoris nostri Jesu Christi et Ejus Beatissimae Matris. Amen."""
        ),
        NovenaDefinition(
            id = "novena_holy_name",
            feastTitle = "Feast of the Most Holy Name of Jesus",
            novenaTitle = "Novena to the Most Holy Name of Jesus",
            fixedStartMonth = 12, fixedStartDay = 25,
            fixedFeastMonth = 1, fixedFeastDay = 3,
            category = "OUR_LORD",
            summary = "Reverence and adore the Holy Name at which every knee shall bow.",
            intention = "Reverence for God's Name, reparation for blasphemies, and spiritual strength.",
            englishPrayer = """O sweet Jesus, Whose Holy Name is above every name, so that at the Name of Jesus every knee should bow, of those that are in heaven, on earth, and under the earth: Grant that we who honor Thy Holy Name on earth may enjoy the sweetness of Thy holy presence in Heaven. Amen."""
        ),
        NovenaDefinition(
            id = "novena_purification",
            feastTitle = "Purification of the Blessed Virgin Mary (Candlemas)",
            novenaTitle = "Novena to Our Lady of Candlemas",
            fixedStartMonth = 1, fixedStartDay = 24,
            fixedFeastMonth = 2, fixedFeastDay = 2,
            category = "MARIAN",
            summary = "Novena honoring Mary's Presentation of Jesus in the Temple and her Holy Purification.",
            intention = "Purity, light in spiritual darkness, and obedience to God's holy laws.",
            englishPrayer = """O Holy Mother of God, who in holy obedience didst present the Light of the World in the Temple, enlighten our minds and purify our souls from all worldly corruption. Amen."""
        ),
        NovenaDefinition(
            id = "novena_lourdes",
            feastTitle = "Feast of Our Lady of Lourdes",
            novenaTitle = "Novena to Our Lady of Lourdes",
            fixedStartMonth = 2, fixedStartDay = 2,
            fixedFeastMonth = 2, fixedFeastDay = 11,
            category = "MARIAN",
            summary = "Invoke Our Lady of Lourdes for bodily healing, spiritual conversion, and penance.",
            intention = "Healing of the sick, spirit of penance, and true repentance.",
            englishPrayer = """O Ever-Immaculate Virgin, Mother of Mercy, Health of the Sick, Refuge of Sinners, Comfortress of the Afflicted, thou knowest my wants, my troubles, my sufferings; deign to cast upon me a look of mercy. By appearing in the Grotto of Lourdes, thou wert pleased to make it a privileged sanctuary, whence thou dost dispense thy favors; and already many sufferers have obtained the cure of their infirmities, both spiritual and corporal. I come, therefore, with complete confidence to implore thy maternal intercession. Obtain, O loving Mother, the grant of my request. Amen."""
        ),
        NovenaDefinition(
            id = "novena_st_patrick",
            feastTitle = "Feast of St. Patrick, Apostle of Ireland",
            novenaTitle = "Novena to St. Patrick",
            fixedStartMonth = 3, fixedStartDay = 8,
            fixedFeastMonth = 3, fixedFeastDay = 17,
            category = "SAINT",
            summary = "Seek the intercession of the great Apostle St. Patrick for steadfast Catholic faith.",
            intention = "Zeal for souls, missionary courage, and preservation of the true Faith.",
            englishPrayer = """O Great Apostle of Ireland, St. Patrick, who didst bring the light of the true Catholic Faith to a pagan nation, intercede for us before the throne of God. Obtain for us a burning zeal for souls, unshakeable loyalty to Holy Mother Church, and the grace to proclaim Christ boldly in word and deed. Amen."""
        ),
        NovenaDefinition(
            id = "novena_st_joseph",
            feastTitle = "Solemnity of St. Joseph, Spouse of the BVM & Patron of the Universal Church",
            novenaTitle = "Novena to St. Joseph",
            fixedStartMonth = 3, fixedStartDay = 10,
            fixedFeastMonth = 3, fixedFeastDay = 19,
            category = "SAINT",
            summary = "The famous 9-day Novena to St. Joseph, terror of demons and protector of Holy Mother Church.",
            intention = "Purity, protection of families, provider in temporal needs, and a happy death.",
            englishPrayer = """O Saint Joseph, whose protection is so great, so strong, so prompt before the throne of God, I place in thee all my interests and desires. O Saint Joseph, do assist me by thy powerful intercession and obtain for me from thy Divine Son all spiritual blessings through Jesus Christ, Our Lord; so that having engaged here below thy Heavenly power I may offer my Thanksgiving and Homage to the most Loving of Fathers.

O Saint Joseph, I never weary contemplating thee and Jesus asleep in thy arms. I dare not approach while He reposes near thy heart. Press Him in my name and kiss His fine Head for me, and ask Him to return the Kiss when I draw my dying breath. Saint Joseph, Patron of departing souls, pray for me. Amen.""",
            latinPrayer = """Sancte Joseph, sponse Deiparae Virginis, custos pientissime Divini Infantis, intercede pro nobis ad Thronum Dei. Amen."""
        ),
        NovenaDefinition(
            id = "novena_annunciation",
            feastTitle = "Feast of the Annunciation of the Blessed Virgin Mary",
            novenaTitle = "Novena to the Annunciation",
            fixedStartMonth = 3, fixedStartDay = 16,
            fixedFeastMonth = 3, fixedFeastDay = 25,
            category = "MARIAN",
            summary = "Honor the sublime moment when the Word was made Flesh in Mary's virginal womb.",
            intention = "Holy humility, submission to God's Divine Will, and the virtue of chastity.",
            englishPrayer = """O Holy Mary, Mother of God, who with humble 'Fiat' didst welcome the Incarnation of the Eternal Word in thy immaculate womb, teach us total surrender to the Will of God. Amen."""
        ),
        NovenaDefinition(
            id = "novena_fatima",
            feastTitle = "Feast of Our Lady of Fatima",
            novenaTitle = "Novena to Our Lady of Fatima",
            fixedStartMonth = 5, fixedStartDay = 4,
            fixedFeastMonth = 5, fixedFeastDay = 13,
            category = "MARIAN",
            summary = "Respond to Our Lady's call at Fatima for daily Rosary, penance, and First Saturdays reparation.",
            intention = "Peace in the world, conversion of Russia, and reparation to the Immaculate Heart.",
            englishPrayer = """O Most Holy Virgin Mary, Queen of the Rosary and Mother of Mercy, who didst deign to appear at Fatima to reveal the treasures of grace hidden in the Holy Rosary, inspire our hearts with a sincere love for this devotion. Amen."""
        ),
        NovenaDefinition(
            id = "novena_st_rita",
            feastTitle = "Feast of St. Rita of Cascia, Patroness of Impossible Causes",
            novenaTitle = "Novena to St. Rita of Cascia",
            fixedStartMonth = 5, fixedStartDay = 13,
            fixedFeastMonth = 5, fixedFeastDay = 22,
            category = "SAINT",
            summary = "Seek miraculous help in desperate and humanly impossible situations.",
            intention = "Relief in hopeless trials, patience in suffering, and marital harmony.",
            englishPrayer = """O Holy Patroness of those in need, St. Rita, whose pleadings before the Divine Lord are almost irresistible, who for thy lavishness in granting favors hast been called the Advocate of the Hopeless and even of the Impossible: St. Rita, so humble, so pure, so mortified, so patient and of such compassionate love for thy Crucified Jesus, obtain for me from Him my petition. Amen."""
        ),
        NovenaDefinition(
            id = "novena_visitation",
            feastTitle = "Feast of the Visitation of the Blessed Virgin Mary",
            novenaTitle = "Novena to the Visitation",
            fixedStartMonth = 5, fixedStartDay = 22,
            fixedFeastMonth = 5, fixedFeastDay = 31,
            category = "MARIAN",
            summary = "Meditate on Mary's journey to St. Elizabeth and her glorious Magnificat.",
            intention = "Active charity toward neighbor, joy in the Lord, and sanctification of unborn infants.",
            englishPrayer = """O Virgin Most Pure, who didst hasten over the mountains to visit thy cousin Elizabeth, bring Jesus into our hearts and homes today. Amen."""
        ),
        NovenaDefinition(
            id = "novena_st_anthony",
            feastTitle = "Feast of St. Anthony of Padua, Doctor of the Church",
            novenaTitle = "Novena to St. Anthony of Padua",
            fixedStartMonth = 6, fixedStartDay = 4,
            fixedFeastMonth = 6, fixedFeastDay = 13,
            category = "SAINT",
            summary = "Pray to St. Anthony, Wonder-Worker and Finder of Lost Items and Lost Souls.",
            intention = "Recovery of lost virtues, peace in trial, and true devotion to the Child Jesus.",
            englishPrayer = """O Holy St. Anthony, gentlest of Saints, thy love for God and Charity for His creatures made thee worthy, when on earth, to possess miraculous powers. Miracles waited on thy word, which thou wert ever ready to speak for those in trouble or anxiety. Encouraged by this thought, I implore of thee to obtain for me my request. Amen."""
        ),
        NovenaDefinition(
            id = "novena_st_john_baptist",
            feastTitle = "Nativity of St. John the Baptist",
            novenaTitle = "Novena to St. John the Baptist",
            fixedStartMonth = 6, fixedStartDay = 15,
            fixedFeastMonth = 6, fixedFeastDay = 24,
            category = "SAINT",
            summary = "Honor the Forerunner of Christ who prepared the way of the Lord in penance.",
            intention = "Spirit of penance, mortification, and fearlessness in testifying to Truth.",
            englishPrayer = """O Glorious St. John the Baptist, greatest prophet among those born of women, who didst prepare the way for the Lamb of God, grant us the spirit of true penance and humility. Amen."""
        ),
        NovenaDefinition(
            id = "novena_peter_and_paul",
            feastTitle = "Feast of SS. Peter and Paul, Apostles",
            novenaTitle = "Novena to SS. Peter and Paul",
            fixedStartMonth = 6, fixedStartDay = 20,
            fixedFeastMonth = 6, fixedFeastDay = 29,
            category = "SAINT",
            summary = "Novena to the Princes of the Apostles, glorious martyrs and pillars of the Church.",
            intention = "Defense of the Papacy, steadfast Apostolic doctrine, and martyr's courage.",
            englishPrayer = """O Holy Apostles Peter and Paul, who sealed your witness to Christ in Rome with your holy blood, intercede for the Holy Catholic Church and preserve us in the true Apostolic Faith. Amen."""
        ),
        NovenaDefinition(
            id = "novena_precious_blood",
            feastTitle = "Feast of the Most Precious Blood of Jesus",
            novenaTitle = "Novena to the Most Precious Blood",
            fixedStartMonth = 6, fixedStartDay = 22,
            fixedFeastMonth = 7, fixedFeastDay = 1,
            category = "OUR_LORD",
            summary = "Adore the Precious Blood shed for our redemption on Mount Calvary.",
            intention = "Expiation of sins, protection from demonic attacks, and salvation of dying souls.",
            englishPrayer = """Almighty and Eternal God, Who hast appointed Thine only-begotten Son the Redeemer of the world, and hast willed to be appeased by His Blood: Grant, we beseech Thee, that we may so venerate with solemn worship the price of our salvation upon earth, that we may be defended by its power from the evils of the present life in heaven. Amen."""
        ),
        NovenaDefinition(
            id = "novena_mt_carmel",
            feastTitle = "Feast of Our Lady of Mount Carmel",
            novenaTitle = "Novena to Our Lady of Mount Carmel",
            fixedStartMonth = 7, fixedStartDay = 7,
            fixedFeastMonth = 7, fixedFeastDay = 16,
            category = "MARIAN",
            summary = "The famous Novena of the Brown Scapular, promise of salvation, and Carmelite grace.",
            intention = "Fidelity to the Brown Scapular, chastity according to one's state, and deliverance from Purgatory.",
            englishPrayer = """O Beautiful Flower of Carmel, most fruitful vine, splendor of heaven, holy and singular, who brought forth the Son of God, still remaining a pure Virgin, assist me in this my necessity! O Star of the Sea, help and protect me! Show me that thou art my Mother.

O Mary, conceived without sin, pray for us who have recourse to thee! Sweet Mother, I place this cause in thy hands. Amen."""
        ),
        NovenaDefinition(
            id = "novena_st_anne",
            feastTitle = "Feast of St. Anne & St. Joachim, Parents of the BVM",
            novenaTitle = "Novena to Good St. Anne",
            fixedStartMonth = 7, fixedStartDay = 17,
            fixedFeastMonth = 7, fixedFeastDay = 26,
            category = "SAINT",
            summary = "Pray to Good St. Anne, Grandmother of Jesus and Mother of the Virgin Mary.",
            intention = "Holy Christian marriages, protection of mothers and grandmothers, and fertility.",
            englishPrayer = """Good St. Anne, mother of her who is our Life, our Sweetness and our Hope, pray to her for us, and obtain our request. Good St. Anne, patroness of Christian families, pray for us. Amen."""
        ),
        NovenaDefinition(
            id = "novena_st_ignatius",
            feastTitle = "Feast of St. Ignatius of Loyola, Founder of the Society of Jesus",
            novenaTitle = "Novena to St. Ignatius of Loyola",
            fixedStartMonth = 7, fixedStartDay = 22,
            fixedFeastMonth = 7, fixedFeastDay = 31,
            category = "SAINT",
            summary = "Invoke the author of the Spiritual Exercises for discernment of spirits and soldier-like zeal for God.",
            intention = "Greater glory of God (AMDG), mastery over passions, and spiritual soldiering.",
            englishPrayer = """O Glorious St. Ignatius, who didst live purely for the Greater Glory of God (Ad Majorem Dei Gloriam), teach us to fight bravely under the banner of the Holy Cross. Amen."""
        ),
        NovenaDefinition(
            id = "novena_st_alphonsus",
            feastTitle = "Feast of St. Alphonsus Liguori, Doctor of the Church",
            novenaTitle = "Novena to St. Alphonsus Liguori",
            fixedStartMonth = 7, fixedStartDay = 23,
            fixedFeastMonth = 8, fixedFeastDay = 1,
            category = "SAINT",
            summary = "Pray to the holy founder of the Redemptorists and patron of moral theologians.",
            intention = "Devotion to the Blessed Sacrament and Mary, and avoidance of mortal sin.",
            englishPrayer = """O Glorious St. Alphonsus, patron of confessors and moral theologians, obtain for us a deep hatred for sin and burning love for Jesus in the Blessed Sacrament. Amen."""
        ),
        NovenaDefinition(
            id = "novena_transfiguration",
            feastTitle = "Feast of the Transfiguration of Our Lord",
            novenaTitle = "Novena to the Transfiguration",
            fixedStartMonth = 7, fixedStartDay = 28,
            fixedFeastMonth = 8, fixedFeastDay = 6,
            category = "OUR_LORD",
            summary = "Contemplate Christ's divine glory revealed on Mount Tabor.",
            intention = "Spiritual illumination, desire for heavenly glory, and transformation in Christ.",
            englishPrayer = """O God, Who in the glorious Transfiguration of Thine Only-Begotten Son didst confirm the mysteries of the faith by the testimony of the fathers: mercifully grant that we may be made co-heirs with Him Who is the King of Glory. Amen."""
        ),
        NovenaDefinition(
            id = "novena_st_dominic",
            feastTitle = "Feast of St. Dominic, Founder of the Order of Preachers",
            novenaTitle = "Novena to St. Dominic",
            fixedStartMonth = 7, fixedStartDay = 30,
            fixedFeastMonth = 8, fixedFeastDay = 8,
            category = "SAINT",
            summary = "Pray to the holy patriarch who received the Holy Rosary from the Blessed Virgin Mary.",
            intention = "Love for the Holy Rosary, defense of orthodoxy against heresy, and preaching Truth.",
            englishPrayer = """O Light of the Church, Doctor of Truth, Rose of Patience, Ivory of Chastity, who didst preach the Gospel with zeal, intercede for us to Christ Our Lord. Amen."""
        ),
        NovenaDefinition(
            id = "novena_st_clare",
            feastTitle = "Feast of St. Clare of Assisi",
            novenaTitle = "Novena to St. Clare of Assisi",
            fixedStartMonth = 8, fixedStartDay = 2,
            fixedFeastMonth = 8, fixedFeastDay = 11,
            category = "SAINT",
            summary = "Invoke St. Clare, spiritual sister of St. Francis, who repelled invaders with the Blessed Sacrament.",
            intention = "Love for holy poverty, Eucharistic adoration, and purity of soul.",
            englishPrayer = """O Seraphic St. Clare, who didst turn back hostiles with the monstrance of the Most Blessed Sacrament, protect our families and grant us ardent love for Jesus in the Eucharist. Amen."""
        ),
        NovenaDefinition(
            id = "novena_assumption",
            feastTitle = "Solemnity of the Assumption of the Blessed Virgin Mary",
            novenaTitle = "Novena to the Assumption of Mary",
            fixedStartMonth = 8, fixedStartDay = 6,
            fixedFeastMonth = 8, fixedFeastDay = 15,
            category = "MARIAN",
            summary = "The great August Novena preparing for Mary's bodily Assumption into Heavenly Glory.",
            intention = "Desire for Heaven, purity of body and soul, and peaceful departure at death.",
            englishPrayer = """O Mary, Queen assumed into Heaven, rejoice that after years of heroic martyrdom on earth, thou art at last taken up to the throne prepared for thee in Heaven by the Holy Trinity.

Obtain for us, O sweet Mother, a holy desire for Heaven, that keeping our eyes fixed on eternity, we may despise the vanity of this passing world and merit to share in thy celestial glory. Amen.""",
            latinPrayer = """O Maria, in Caelum Assumpta, gaude quia post annos heroici martyrii in terris, ad thronum tibi paratum in Caelo a Sanctissima Trinitate tandem evecta es. Impetra nobis, o dulcis Mater, sanctum Caelorum desiderium. Amen."""
        ),
        NovenaDefinition(
            id = "novena_immaculate_heart_august",
            feastTitle = "Feast of the Immaculate Heart of Mary (Traditional Aug 22)",
            novenaTitle = "Novena to the Immaculate Heart of Mary",
            fixedStartMonth = 8, fixedStartDay = 13,
            fixedFeastMonth = 8, fixedFeastDay = 22,
            category = "MARIAN",
            summary = "Novena of reparation to the Immaculate Heart, furnace of divine love.",
            intention = "Reparation for blasphemies against Mary's Heart, family conversion, and peace.",
            englishPrayer = """O Heart of Mary, Mother of God and our Mother; Heart most worthy of love, in which the Adorable Trinity is well-pleased: do thou melt the coldness of our hearts and make them to be entirely turned to the Heart of our Divine Savior. Amen."""
        ),
        NovenaDefinition(
            id = "novena_st_monica",
            feastTitle = "Feast of St. Monica, Model of Christian Mothers",
            novenaTitle = "Novena to St. Monica",
            fixedStartMonth = 8, fixedStartDay = 18,
            fixedFeastMonth = 8, fixedFeastDay = 27,
            category = "SAINT",
            summary = "Pray to St. Monica, whose 17 years of tearful prayers won the conversion of St. Augustine.",
            intention = "Conversion of straying children, husbands, and relatives; perseverance in prayer.",
            englishPrayer = """O Exemplary Mother St. Monica, who didst weep for thy rebellious son Augustine until God granted his total conversion, look upon my tears for those I love. Intercede before the Throne of Grace that those who have fallen away from Christ may return to the fold of salvation. Amen."""
        ),
        NovenaDefinition(
            id = "novena_st_augustine",
            feastTitle = "Feast of St. Augustine, Bishop & Doctor of the Church",
            novenaTitle = "Novena to St. Augustine",
            fixedStartMonth = 8, fixedStartDay = 19,
            fixedFeastMonth = 8, fixedFeastDay = 28,
            category = "SAINT",
            summary = "Pray to the Great Doctor of Grace who declared 'Our heart is restless until it rests in Thee.'",
            intention = "True repentance, intellectual clarity, and victory over lust.",
            englishPrayer = """O Glorious St. Augustine, Doctor of Grace, whose heart was inflamed with divine love after years of wandering: 'Late have I loved Thee, O Beauty ever ancient, ever new!' Pray for us that we may abandon all sin and rest solely in God. Amen."""
        ),
        NovenaDefinition(
            id = "novena_queenship_mary",
            feastTitle = "Feast of the Queenship of Mary",
            novenaTitle = "Novena to Mary Queen of Heaven & Earth",
            fixedStartMonth = 8, fixedStartDay = 22,
            fixedFeastMonth = 8, fixedFeastDay = 31,
            category = "MARIAN",
            summary = "Honor Mary crowned in Heaven as Queen of Angels, Saints, and the universe.",
            intention = "Triumph of the Catholic Church, crushing of heresies, and peace under Mary's mantle.",
            englishPrayer = """O Queen of Heaven and Earth, most powerful Sovereign Lady, to whom the King of Kings hath granted all power in Heaven and on earth, extend thy royal mantle over us and reign in our hearts. Amen."""
        ),
        NovenaDefinition(
            id = "novena_nativity_mary",
            feastTitle = "Feast of the Nativity of the Blessed Virgin Mary",
            novenaTitle = "Novena to the Birthday of Mary",
            fixedStartMonth = 8, fixedStartDay = 30,
            fixedFeastMonth = 9, fixedFeastDay = 8,
            category = "MARIAN",
            summary = "Celebrate the Holy Birth of Mary, dawn of salvation and morning star.",
            intention = "Spiritual rebirth, holy joy, and destruction of darkness in our lives.",
            englishPrayer = """O Holy Child Mary, destined from eternity to be the Mother of our Redeemer, we celebrate with joy thy glorious Birth. Obtain for us holy innocence and child-like purity of heart. Amen."""
        ),
        NovenaDefinition(
            id = "novena_holy_cross",
            feastTitle = "Feast of the Exaltation of the Holy Cross",
            novenaTitle = "Novena to the Holy Cross",
            fixedStartMonth = 9, fixedStartDay = 5,
            fixedFeastMonth = 9, fixedFeastDay = 14,
            category = "OUR_LORD",
            summary = "Triumph in the Holy Cross of Christ, tree of life and weapon of salvation.",
            intention = "Patient endurance of suffering, love for mortification, and victory over Satan.",
            englishPrayer = """O Holy Cross of Christ, tree of eternal life, upon which hung the Salvation of the World: We adore Thee, O Christ, and we bless Thee, because by Thy Holy Cross Thou hast redeemed the world. Amen."""
        ),
        NovenaDefinition(
            id = "novena_our_lady_sorrows",
            feastTitle = "Feast of Our Lady of Sorrows (Seven Sorrows)",
            novenaTitle = "Novena to Our Lady of Sorrows",
            fixedStartMonth = 9, fixedStartDay = 6,
            fixedFeastMonth = 9, fixedFeastDay = 15,
            category = "MARIAN",
            summary = "Meditate on the Seven Sorrows of Mary at the foot of the Cross.",
            intention = "Contrition for sins, compassion for Our Lord's Passion, and comfort in affliction.",
            englishPrayer = """O Sorrowful and Immaculate Heart of Mary, pierced by seven swords of grief, obtain for us true tears of contrition for our sins which crucified thy Divine Son. Amen."""
        ),
        NovenaDefinition(
            id = "novena_padre_pio",
            feastTitle = "Feast of St. Padre Pio of Pietrelcina",
            novenaTitle = "Novena to St. Padre Pio",
            fixedStartMonth = 9, fixedStartDay = 14,
            fixedFeastMonth = 9, fixedFeastDay = 23,
            category = "SAINT",
            summary = "Pray to the stigmata-bearing Capuchin priest, wonder-worker of our modern era.",
            intention = "Fidelity to the Holy Mass, patient suffering, trust in Providence ('Pray, Hope, and Don't Worry').",
            englishPrayer = """Beloved Padre Pio, who didst bear in thy body the Sacred Stigmata of Our Crucified Lord, intercede for us in our trials. Teach us thy holy maxim: 'Pray, Hope, and Don't Worry.' Amen."""
        ),
        NovenaDefinition(
            id = "novena_archangels",
            feastTitle = "Feast of SS. Michael, Gabriel & Raphael, Holy Archangels",
            novenaTitle = "Novena to the Holy Archangels",
            fixedStartMonth = 9, fixedStartDay = 20,
            fixedFeastMonth = 9, fixedFeastDay = 29,
            category = "ANGEL",
            summary = "Invoke St. Michael the Archangel, St. Gabriel, and St. Raphael for angelic protection.",
            intention = "Protection against demonic forces, physical safety, and guidance on life's path.",
            englishPrayer = """St. Michael the Archangel, defend us in battle; be our protection against the wickedness and snares of the devil. May God rebuke him, we humbly pray; and do thou, O Prince of the Heavenly Host, by the power of God, cast into hell Satan and all evil spirits who prowl through the world seeking the ruin of souls. Amen."""
        ),
        NovenaDefinition(
            id = "novena_little_flower",
            feastTitle = "Feast of St. Thérèse of the Child Jesus (Little Flower)",
            novenaTitle = "Novena to the Little Flower (St. Thérèse)",
            fixedStartMonth = 9, fixedStartDay = 24,
            fixedFeastMonth = 10, fixedFeastDay = 3,
            category = "SAINT",
            summary = "The famous Rose Novena to St. Thérèse, who promised to let fall a shower of roses from Heaven.",
            intention = "The 'Little Way' of spiritual childhood, trust in God's mercy, and special favors.",
            englishPrayer = """O Little Therese of the Child Jesus, please pick for me a rose from the heavenly garden and send it to me with a message of love. Ask God to grant me the favor I now implore, and tell Him I will love Him each day more and more. Amen."""
        ),
        NovenaDefinition(
            id = "novena_st_francis",
            feastTitle = "Feast of St. Francis of Assisi",
            novenaTitle = "Novena to St. Francis of Assisi",
            fixedStartMonth = 9, fixedStartDay = 25,
            fixedFeastMonth = 10, fixedFeastDay = 4,
            category = "SAINT",
            summary = "Novena to the Seraphic Father St. Francis, lover of Christ Crucified.",
            intention = "Detachment from worldly wealth, peace, and total devotion to the Holy Cross.",
            englishPrayer = """Lord, make me an instrument of Thy peace; where there is hatred, let me sow love; where there is injury, pardon; where there is doubt, faith; where there is despair, hope; where there is darkness, light; and where there is sadness, joy. Amen."""
        ),
        NovenaDefinition(
            id = "novena_holy_rosary",
            feastTitle = "Feast of Our Lady of the Most Holy Rosary",
            novenaTitle = "Novena to Our Lady of the Rosary",
            fixedStartMonth = 9, fixedStartDay = 28,
            fixedFeastMonth = 10, fixedFeastDay = 7,
            category = "MARIAN",
            summary = "Celebrate Mary's victory through the Holy Rosary at the Battle of Lepanto.",
            intention = "Daily fidelity to the 15 mysteries of the Rosary, family unity, and defeat of errors.",
            englishPrayer = """O Queen of the Most Holy Rosary, weapon of salvation given to St. Dominic, grant that through daily recitation of thy Rosary we may obtain victory over all temptations. Amen."""
        ),
        NovenaDefinition(
            id = "novena_st_teresa_avila",
            feastTitle = "Feast of St. Teresa of Avila, Virgin & Doctor of the Church",
            novenaTitle = "Novena to St. Teresa of Avila",
            fixedStartMonth = 10, fixedStartDay = 6,
            fixedFeastMonth = 10, fixedFeastDay = 15,
            category = "SAINT",
            summary = "Pray to the Great Reformer of Carmel and master of interior prayer.",
            intention = "Depth in mental prayer, recollection of heart, and 'Let nothing disturb thee'.",
            englishPrayer = """Let nothing disturb thee, let nothing affright thee. All things are passing; God never changeth. Patience gaineth all things. Who hath God wanteth nothing; God alone sufficeth. Amen."""
        ),
        NovenaDefinition(
            id = "novena_st_jude",
            feastTitle = "Feast of St. Jude Thaddeus, Apostle & Patron of Desperate Cases",
            novenaTitle = "Novena to St. Jude Thaddeus",
            fixedStartMonth = 10, fixedStartDay = 19,
            fixedFeastMonth = 10, fixedFeastDay = 28,
            category = "SAINT",
            summary = "The beloved Novena to St. Jude, patron of hopeless and forgotten causes.",
            intention = "Relief in extreme financial, medical, or family crises where all human help fails.",
            englishPrayer = """Most holy Apostle, St. Jude, faithful servant and friend of Jesus, the Church honors and invokes thee universally, as the patron of difficult and hopeless cases. Pray for me, I am so helpless and alone. Intercede with God for me that He bring visible and speedy help where help is almost despaired of. Come to my assistance in this great need. Amen."""
        ),
        NovenaDefinition(
            id = "novena_all_saints",
            feastTitle = "Solemnity of All Saints (All Saints' Day)",
            novenaTitle = "Novena to All Saints",
            fixedStartMonth = 10, fixedStartDay = 23,
            fixedFeastMonth = 11, fixedFeastDay = 1,
            category = "SAINT",
            summary = "Invoke the entire Church Triumphant in Heaven for holiness and grace.",
            intention = "Desire for sainthood, union with the Church Triumphant, and final perseverance.",
            englishPrayer = """O Glorious Citizens of Heaven, Holy Saints of God, who have fought the good fight and received the crown of righteousness: look upon us earthly pilgrims with love. Intercede for us that we may walk in your footsteps and join you in eternal glory. Amen."""
        ),
        NovenaDefinition(
            id = "novena_all_souls",
            feastTitle = "Commemoration of All the Faithful Departed (All Souls' Day)",
            novenaTitle = "Novena for the Holy Souls in Purgatory",
            fixedStartMonth = 10, fixedStartDay = 24,
            fixedFeastMonth = 11, fixedFeastDay = 2,
            category = "TEMPORAL",
            summary = "Offer 9 days of intense suffrage, Masses, and indulgences for the Suffering Souls in Purgatory.",
            intention = "Relief and speedy deliverance of the Holy Souls in Purgatory, especially abandoned souls.",
            englishPrayer = """Eternal Father, I offer Thee the Most Precious Blood of Thy Divine Son, Jesus, in union with the Masses said throughout the world today, for all the holy souls in Purgatory, for sinners everywhere, for sinners in the universal church, those in my own home and within my family. Amen.

Eternal rest grant unto them, O Lord, and let perpetual light shine upon them. May they rest in peace. Amen."""
        ),
        NovenaDefinition(
            id = "novena_miraculous_medal",
            feastTitle = "Feast of Our Lady of the Miraculous Medal",
            novenaTitle = "Novena to Our Lady of the Miraculous Medal",
            fixedStartMonth = 11, fixedStartDay = 18,
            fixedFeastMonth = 11, fixedFeastDay = 27,
            category = "MARIAN",
            summary = "Novena revealed to St. Catherine Labouré with the promise of abundant graces.",
            intention = "Physical and spiritual protection through the Miraculous Medal, and conversion.",
            englishPrayer = """O Mary, conceived without sin, pray for us who have recourse to thee!

O Immaculate Virgin Mary, Mother of Our Lord Jesus and Our Mother, penetrated with the most lively confidence in thy all-powerful and never-failing intercession, manifested so often through the Miraculous Medal, we thy loving and trustful children implore thee to obtain for us the graces and favors we ask during this Novena. Amen."""
        ),
        NovenaDefinition(
            id = "novena_st_andrew",
            feastTitle = "Feast of St. Andrew the Apostle (St. Andrew Christmas Novena)",
            novenaTitle = "St. Andrew Christmas Novena",
            fixedStartMonth = 11, fixedStartDay = 21,
            fixedFeastMonth = 11, fixedFeastDay = 30,
            category = "OUR_LORD",
            summary = "The legendary Advent prayer recited 15 times daily from St. Andrew's Day to Christmas.",
            intention = "Graces for Advent, special intentions, and holy preparation for Christmas.",
            englishPrayer = """Hail and blessed be the hour and moment in which the Son of God was born of the most pure Virgin Mary, at midnight, in Bethlehem, in piercing cold. In that hour vouchsafe, O my God, to hear my prayer and grant my desires, through the merits of Our Savior Jesus Christ, and of His Blessed Mother. Amen."""
        ),
        NovenaDefinition(
            id = "novena_st_francis_xavier",
            feastTitle = "Feast of St. Francis Xavier, Patron of Catholic Missions",
            novenaTitle = "Novena of Grace (St. Francis Xavier)",
            fixedStartMonth = 11, fixedStartDay = 24,
            fixedFeastMonth = 12, fixedFeastDay = 3,
            category = "SAINT",
            summary = "The famous 'Novena of Grace' to the great Apostle of the Indies and Japan.",
            intention = "Missionary zeal, baptism of unbelievers, and urgent temporal favors.",
            englishPrayer = """O most amiable and loving St. Francis Xavier, in union with thee I reverently adore the Divine Majesty. I rejoice in the special gifts of grace so abundantly bestowed on thee during thy earthly life and in thy glory after death. I ask thee to obtain for me the grace to live a holy life and die a happy death. Amen."""
        )
    )

    val sampleNovena: NovenaDefinition get() = allNovenas.first()

    fun getNovenasStartingOnDate(calendar: Calendar): List<NovenaDefinition> {
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)

        val startingNovenas = mutableListOf<NovenaDefinition>()

        // 1. Check fixed date novenas
        for (novena in allNovenas) {
            if (!novena.isMovable && novena.fixedStartMonth == month && novena.fixedStartDay == day) {
                startingNovenas.add(novena)
            }
        }

        // 2. Check movable novenas
        val movableNovenas = getMovableNovenasForYear(year)
        for (novena in movableNovenas) {
            if (novena.fixedStartMonth == month && novena.fixedStartDay == day) {
                startingNovenas.add(novena)
            }
        }

        // 3. Dynamic Fallback Check: Check if a major feast occurs 9 days from today!
        val targetCal = (calendar.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 9) }
        val targetMmDd = String.format(Locale.US, "%02d-%02d", targetCal.get(Calendar.MONTH) + 1, targetCal.get(Calendar.DAY_OF_MONTH))
        val saint = SaintData.getSaintForDate(targetMmDd)

        if (saint != null && (saint.feastRank.contains("Double") || saint.feastRank.contains("I Class") || saint.feastRank.contains("II Class"))) {
            val alreadyListed = startingNovenas.any { it.feastTitle.contains(saint.saintName, ignoreCase = true) || it.novenaTitle.contains(saint.saintName, ignoreCase = true) }
            if (!alreadyListed) {
                startingNovenas.add(
                    NovenaDefinition(
                        id = "dynamic_novena_${targetMmDd}",
                        feastTitle = saint.feastTitle,
                        novenaTitle = "Novena to ${saint.saintName}",
                        fixedStartMonth = month,
                        fixedStartDay = day,
                        fixedFeastMonth = targetCal.get(Calendar.MONTH) + 1,
                        fixedFeastDay = targetCal.get(Calendar.DAY_OF_MONTH),
                        category = if (saint.feastTitle.contains("Mary", ignoreCase = true) || saint.saintName.contains("Mary", ignoreCase = true) || saint.feastTitle.contains("Virgin", ignoreCase = true)) "MARIAN" else "SAINT",
                        summary = "Prepare for the upcoming Feast of ${saint.saintName} (${saint.feastTitle}) with 9 days of faithful prayer.",
                        intention = saint.resolution.ifBlank { "Spiritual growth, virtue, and heavenly intercession." },
                        englishPrayer = saint.intercessoryPrayer.ifBlank {
                            "O Almighty and Eternal God, who hast glorified Thy holy servant ${saint.saintName}, grant that by their powerful intercession we may be defended from all adversity and walk in the path of eternal salvation. Through Christ Our Lord. Amen."
                        }
                    )
                )
            }
        }

        return startingNovenas
    }

    fun getActiveNovenasForDate(calendar: Calendar): List<ActiveNovenaInfo> {
        val year = calendar.get(Calendar.YEAR)
        val activeList = mutableListOf<ActiveNovenaInfo>()

        val novenaPool = mutableListOf<NovenaDefinition>()
        novenaPool.addAll(allNovenas)
        novenaPool.addAll(getMovableNovenasForYear(year))

        for (novena in novenaPool) {
            val startCal = Calendar.getInstance().apply {
                set(year, (novena.fixedStartMonth ?: 1) - 1, novena.fixedStartDay ?: 1, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val endCal = Calendar.getInstance().apply {
                set(year, (novena.fixedFeastMonth ?: 1) - 1, novena.fixedFeastDay ?: 1, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
                if (before(startCal)) {
                    add(Calendar.YEAR, 1)
                }
            }

            val todayCal = (calendar.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // Calculate day difference
            val diffMs = todayCal.timeInMillis - startCal.timeInMillis
            val diffDays = (diffMs / (1000 * 60 * 60 * 24)).toInt()

            if (diffDays in 0..8) {
                val currentDay = diffDays + 1
                val feastDateStr = String.format(Locale.US, "%02d/%02d", novena.fixedFeastMonth, novena.fixedFeastDay)
                val daysRemaining = 9 - currentDay
                activeList.add(
                    ActiveNovenaInfo(
                        definition = novena,
                        currentDay = currentDay,
                        isStartDay = (currentDay == 1),
                        feastDateString = feastDateStr,
                        daysRemainingToFeast = daysRemaining
                    )
                )
            }
        }

        return activeList
    }

    fun getMovableNovenasForYear(year: Int): List<NovenaDefinition> {
        val movables = mutableListOf<NovenaDefinition>()
        try {
            val easterCal = LiturgicalCalculator.calculateEaster(year)

            // 1. Pentecost Novena (Original Novena to the Holy Ghost - Friday after Ascension to Pentecost Eve)
            val pentecost = (easterCal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 49) }
            val pentecostNovenaStart = (easterCal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 40) } // Friday after Ascension
            movables.add(
                NovenaDefinition(
                    id = "novena_pentecost_${year}",
                    feastTitle = "Pentecost Sunday (Outpouring of the Holy Ghost)",
                    novenaTitle = "Novena to the Holy Ghost (Pentecost Novena)",
                    fixedStartMonth = pentecostNovenaStart.get(Calendar.MONTH) + 1,
                    fixedStartDay = pentecostNovenaStart.get(Calendar.DAY_OF_MONTH),
                    fixedFeastMonth = pentecost.get(Calendar.MONTH) + 1,
                    fixedFeastDay = pentecost.get(Calendar.DAY_OF_MONTH),
                    category = "TEMPORAL",
                    summary = "The original Novena commanded by Our Lord when the Apostles prayed in the Upper Room with Mary.",
                    intention = "The 7 Gifts of the Holy Ghost and revival of Catholic fervor.",
                    englishPrayer = "Holy Spirit, Lord of Light, from Thy clear celestial height, Thy pure beaming radiance give! Come, Thou Father of the poor, come with treasures which endure, come Thou Light of all that live!",
                    isMovable = true, movableType = "PENTECOST"
                )
            )

            // 3. Sacred Heart Novena (9 days before Feast of Sacred Heart)
            val sacredHeart = (easterCal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 68) }
            val sacredHeartNovenaStart = (sacredHeart.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -9) }
            movables.add(
                NovenaDefinition(
                    id = "novena_sacred_heart_${year}",
                    feastTitle = "Feast of the Most Sacred Heart of Jesus",
                    novenaTitle = "Efficacious Novena to the Sacred Heart (Padre Pio's Novena)",
                    fixedStartMonth = sacredHeartNovenaStart.get(Calendar.MONTH) + 1,
                    fixedStartDay = sacredHeartNovenaStart.get(Calendar.DAY_OF_MONTH),
                    fixedFeastMonth = sacredHeart.get(Calendar.MONTH) + 1,
                    fixedFeastDay = sacredHeart.get(Calendar.DAY_OF_MONTH),
                    category = "OUR_LORD",
                    summary = "Padre Pio's daily Novena of the Three Promises to the Sacred Heart.",
                    intention = "Conformity to the Sacred Heart, reparation, and trust in His promises.",
                    englishPrayer = "O my Jesus, Thou hast said: 'Truly I say to you, ask and you shall receive, seek and you shall find, knock and it shall be opened to you.' Behold I knock, I seek and I ask for the grace of this Novena. Sacred Heart of Jesus, I place all my trust in Thee.",
                    isMovable = true, movableType = "SACRED_HEART"
                )
            )

            // 4. Corpus Christi Novena (9 days before Feast of Corpus Christi = Easter + 60 days)
            val corpusChristi = (easterCal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 60) }
            val corpusChristiStart = (corpusChristi.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -9) }
            movables.add(
                NovenaDefinition(
                    id = "novena_corpus_christi_${year}",
                    feastTitle = "Solemnity of Corpus Christi (Most Holy Body of Christ)",
                    novenaTitle = "Novena to the Most Blessed Sacrament (Corpus Christi)",
                    fixedStartMonth = corpusChristiStart.get(Calendar.MONTH) + 1,
                    fixedStartDay = corpusChristiStart.get(Calendar.DAY_OF_MONTH),
                    fixedFeastMonth = corpusChristi.get(Calendar.MONTH) + 1,
                    fixedFeastDay = corpusChristi.get(Calendar.DAY_OF_MONTH),
                    category = "OUR_LORD",
                    summary = "9 days of Eucharistic reparation and adoration preceding the Solemnity of Corpus Christi.",
                    intention = "Reverence for the Holy Eucharist, reparation for sacrileges, and growth in faith.",
                    englishPrayer = "O Divine Jesus, true God and true Man, present in the Most Blessed Sacrament of the Altar: We adore Thee with the deepest humility and offer Thee reparation for all sacrileges, coldness, and contempt committed against Thy Holy Presence. Amen.",
                    isMovable = true, movableType = "CORPUS_CHRISTI"
                )
            )

            // 5. Christ the King Novena (1954 Calendar: Last Sunday of October. Starts 9 days prior)
            val oct31Cal = (Calendar.getInstance()).apply {
                set(year, 9, 31, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            while (oct31Cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                oct31Cal.add(Calendar.DAY_OF_YEAR, -1)
            }
            val christTheKing = oct31Cal
            val christTheKingStart = (christTheKing.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -9) }
            movables.add(
                NovenaDefinition(
                    id = "novena_christ_king_${year}",
                    feastTitle = "Solemnity of Our Lord Jesus Christ the King (1954 Rite)",
                    novenaTitle = "Novena to Christ the King",
                    fixedStartMonth = christTheKingStart.get(Calendar.MONTH) + 1,
                    fixedStartDay = christTheKingStart.get(Calendar.DAY_OF_MONTH),
                    fixedFeastMonth = christTheKing.get(Calendar.MONTH) + 1,
                    fixedFeastDay = christTheKing.get(Calendar.DAY_OF_MONTH),
                    category = "OUR_LORD",
                    summary = "Novena honoring the Universal Kingship of Our Lord Jesus Christ on the last Sunday of October.",
                    intention = "Social Reign of Christ the King, submission of nations to His holy law, and defense of the Faith.",
                    englishPrayer = "Almighty and Everlasting God, Who in Thy beloved Son, King of the whole world, hast willed to restore all things: Mercifully grant that all families of nations may be subjected to His most gentle rule. Amen.",
                    isMovable = true, movableType = "CHRIST_KING"
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return movables
    }
}

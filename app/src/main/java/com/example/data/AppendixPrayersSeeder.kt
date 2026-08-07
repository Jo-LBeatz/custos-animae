package com.example.data

object AppendixPrayersSeeder {

    fun getAppendixPrayers(): List<PrayerEntity> {
        val list = mutableListOf<PrayerEntity>()

        // -------------------------------------------------------------------
        // 1. ACTS OF FAITH, HOPE, CHARITY & CONTRITION
        // -------------------------------------------------------------------
        list.add(
            PrayerEntity(
                id = "act_of_faith",
                title = "Act of Faith (Actus Fidei)",
                category = "Acts & Creeds",
                subtitle = "Appendix Prayer",
                englishText = """O my God, I firmly believe that Thou art one God in three Divine Persons, Father, Son, and Holy Ghost; I believe that Thy Divine Son became man, and died for our sins, and that He will come to judge the living and the dead. I believe these and all the truths which the Holy Catholic Church teaches, because Thou hast revealed them, who canst neither deceive nor be deceived. Amen.""",
                latinText = """Domine Deus, firma fide credo et confiteor omnia et singula quae sancta Ecclesia Catholica proponit, quia tu, Deus, ea omnia revelasti, qui es aeterna veritas et sapientia quae nec fallere nec falli potest. In hac fide vivere et mori statuo. Amen."""
            )
        )

        list.add(
            PrayerEntity(
                id = "act_of_hope",
                title = "Act of Hope (Actus Spei)",
                category = "Acts & Creeds",
                subtitle = "Appendix Prayer",
                englishText = """O my God, relying on Thy almighty power and infinite mercy and promises, I hope to obtain pardon of my sins, the help of Thy grace, and life everlasting, through the merits of Jesus Christ, my Lord and Redeemer. Amen.""",
                latinText = """Domine Deus, spero per gratiam tuam remissionem omnium peccatorum, et post hanc vitam aeternam felicitatem me esse consecuturum: quia tu promisisti, qui es fidelissimus et omnipotens. Amen."""
            )
        )

        list.add(
            PrayerEntity(
                id = "act_of_charity",
                title = "Act of Charity (Actus Caritatis)",
                category = "Acts & Creeds",
                subtitle = "Appendix Prayer",
                englishText = """O my God, I love Thee above all things, with my whole heart and soul, because Thou art all-good and worthy of all love. I love my neighbor as myself for the love of Thee. I forgive all who have injured me, and ask pardon of all whom I have injured. Amen.""",
                latinText = """Domine Deus, amo te super omnia et propter te ex toto corde, quia tu es summum et infinitum bonum, dignusque qui super omnia ameris; et propter te amo proximum meum sicut meipsum. Amen."""
            )
        )

        list.add(
            PrayerEntity(
                id = "act_of_contrition",
                title = "Act of Contrition (Actus Contritionis)",
                category = "Acts & Creeds",
                subtitle = "Appendix Prayer",
                englishText = """O my God, I am heartily sorry for having offended Thee, and I detest all my sins, because I dread the loss of heaven and the pains of hell; but most of all because they offend Thee, my God, who art all-good and deserving of all my love. I firmly resolve, with the help of Thy grace, to confess my sins, to do penance, and to amend my life. Amen.""",
                latinText = """Deus meus, ex toto corde me poenitet omnium meorum peccatorum, eaque detestor, quia peccando, non solum poenas a te iuste statutas promeritus sum, sed praesertim quia offendi te, summum bonum, ac dignum qui super omnia ameris. Ideo firmiter propono, adiuvante gratia tua, de cetero me non peccaturum peccandique occasiones proximas fugiturum. Amen."""
            )
        )

        // -------------------------------------------------------------------
        // 2. ROSARY CONCLUDING & DEVOTIONAL PRAYERS
        // -------------------------------------------------------------------
        list.add(
            PrayerEntity(
                id = "fatima_prayer",
                title = "Fatima Prayer (O My Jesus)",
                category = "Marian Prayers",
                subtitle = "Revealed by Our Lady of Fatima (1917)",
                englishText = """O my Jesus, forgive us our sins, save us from the fires of hell, lead all souls to Heaven, especially those most in need of Thy mercy. Amen.""",
                latinText = """Domine Iesu, dimitte nobis debita nostra, salva nos ab igne inferiori, perduc in caelum omnes animas, praesertim eas, quae misericordiae tuae maxime indigent. Amen."""
            )
        )

        list.add(
            PrayerEntity(
                id = "rosary_concluding_prayer",
                title = "Concluding Rosary Prayer (O God, Whose Only-Begotten Son)",
                category = "Marian Prayers",
                subtitle = "Appendix Rosary Prayer",
                englishText = """O God, whose only-begotten Son, by His life, death, and resurrection, has purchased for us the rewards of eternal life, grant, we beseech Thee, that meditating upon these mysteries of the Most Holy Rosary of the Blessed Virgin Mary, we may imitate what they contain and obtain what they promise, through the same Christ our Lord. Amen.""",
                latinText = """Deus, cuius Unigenitus per vitam, mortem et resurrectionem suam nobis salutis aeternae praemia comparavit: concede, quaesumus; ut haec mysteria sacratissimo Beatae Mariae Virginis Rosario recolentes, et imitemur quod continent, et quod promittunt assequamur. Per eundem Christum Dominum nostrum. Amen."""
            )
        )

        // -------------------------------------------------------------------
        // 3. HOLY COMMUNION PREPARATION & THANKSGIVING PRAYERS
        // -------------------------------------------------------------------
        list.add(
            PrayerEntity(
                id = "anima_christi",
                title = "Anima Christi (Soul of Christ)",
                category = "Holy Mass & Communion",
                subtitle = "Traditional Thanksgiving Prayer",
                englishText = """Soul of Christ, sanctify me.
Body of Christ, save me.
Blood of Christ, inebriate me.
Water from the side of Christ, wash me.
Passion of Christ, strengthen me.
O good Jesus, hear me.
Within Thy wounds hide me.
Suffer me not to be separated from Thee.
From the malicious enemy defend me.
In the hour of my death call me,
And bid me come unto Thee,
That with Thy Saints I may praise Thee
For ever and ever. Amen.""",
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
Ut cum Sanctis tuis laudem te
In saecula saeculorum. Amen."""
            )
        )

        list.add(
            PrayerEntity(
                id = "prayer_before_crucifix",
                title = "Prayer Before a Crucifix (En Ego, O Bone et Dulcissime Iesu)",
                category = "Holy Mass & Communion",
                subtitle = "Plenary Indulgence after Holy Communion",
                englishText = """Look down upon me, good and gentle Jesus, while before Thy face I humbly kneel and, with the most fervent desire of my soul, I pray and beseech Thee to fix deep in my heart lively sentiments of faith, hope, and charity, with true repentance for my sins, and a firm purpose of amendment; while I contemplate with great affection and grief Thy five most precious wounds, having before my eyes that which David spoke in prophecy: 'They have pierced My hands and My feet; they have numbered all My bones.' Amen.""",
                latinText = """En ego, o bone et dulcissime Iesu, ante conspectum tuum me in genua provolvo, ac maximo animi ardore te oro atque obtestor, ut meum in cor vividos fidei, spei et caritatis sensus, atque veram peccatorum meorum poenitentiam, ea quoque propositum firmissimum emendandi imprimas; dum magno animi affectu et dolore tua quinque vulnera mecum ipse considero, ac mente contemplor, illud prae oculis habens, quod iam in ore ponebat tuo David Propheta de te, o bone Iesu: Foderunt manus meas et pedes meos; dinumeraverunt omnia ossa mea. Amen."""
            )
        )

        list.add(
            PrayerEntity(
                id = "universal_prayer_clement_xi",
                title = "Universal Prayer (Pope Clement XI)",
                category = "General Devotions",
                subtitle = "For all things necessary for salvation",
                englishText = """O my God, I believe in Thee, but do Thou strengthen my faith. All my hopes are in Thee, but do Thou secure them. I love Thee, but do Thou kindle my love more and more. I repent of my sins, but do Thou increase my contrition. I adore Thee as my first beginning; I long for Thee as my last end; I thank Thee as my constant benefactor; I call upon Thee as my sovereign protector. Deign, O my God, to guide me by Thy wisdom, to restrain me by Thy justice, to comfort me by Thy mercy, to defend me by Thy power. Amen.""",
                latinText = """Credo, Domine, sed credam firmius; spero, sed sperem securius; amo, sed amem ardentius; doleo, sed doleam magis. Adoro te ut primum principium; desidero ut finem ultimum; laudo ut benefactorem perpetuum; invoco ut defensorem propitium. Tua sapientia me dirige, iustitia contine, clementia solare, potentia me muni. Amen."""
            )
        )

        // -------------------------------------------------------------------
        // 4. LITANIES
        // -------------------------------------------------------------------
        list.add(
            PrayerEntity(
                id = "litany_holy_name",
                title = "Litany of the Most Holy Name of Jesus",
                category = "Litanies & Devotions",
                subtitle = "Papally Approved Litany",
                englishText = """Lord, have mercy on us. Christ, have mercy on us. Lord, have mercy on us. Jesus, hear us. Jesus, graciously hear us. God the Father of Heaven, have mercy on us. God the Son, Redeemer of the world, have mercy on us. God the Holy Ghost, have mercy on us. Holy Trinity, one God, have mercy on us. Jesus, Son of the living God, have mercy on us. Jesus, Splendor of the Father, have mercy on us. Jesus, Brightness of eternal Light, have mercy on us. Jesus, King of glory, have mercy on us. Jesus, Sun of justice, have mercy on us. Jesus, Son of the Virgin Mary, have mercy on us. Jesus, most amiable, have mercy on us. Jesus, most admirable, have mercy on us. Jesus, the mighty God, have mercy on us. Jesus, Father of the world to come, have mercy on us. Jesus, Angel of great counsel, have mercy on us. Jesus, most powerful, have mercy on us. Jesus, most patient, have mercy on us. Jesus, most obedient, have mercy on us. Jesus, meek and humble of heart, have mercy on us. Jesus, lover of chastity, have mercy on us. Jesus, lover of us, have mercy on us. Jesus, God of peace, have mercy on us. Jesus, Author of life, have mercy on us. Jesus, Model of virtues, have mercy on us. Jesus, zealous lover of souls, have mercy on us. Jesus, our God, have mercy on us. Jesus, our Refuge, have mercy on us. Jesus, Father of the poor, have mercy on us. Jesus, Treasure of the faithful, have mercy on us. Jesus, good Shepherd, have mercy on us. Jesus, true Light, have mercy on us. Jesus, eternal Wisdom, have mercy on us. Jesus, infinite Goodness, have mercy on us. Jesus, our Way and our Life, have mercy on us. Jesus, Joy of Angels, have mercy on us. Jesus, King of the Patriarchs, have mercy on us. Jesus, Master of the Apostles, have mercy on us. Jesus, Teacher of the Evangelists, have mercy on us. Jesus, Strength of Martyrs, have mercy on us. Jesus, Light of Confessors, have mercy on us. Jesus, Purity of Virgins, have mercy on us. Jesus, Crown of all Saints, have mercy on us. Be merciful, spare us, O Jesus. Be merciful, graciously hear us, O Jesus. From all evil, deliver us, O Jesus. From all sin, deliver us, O Jesus. From Thy wrath, deliver us, O Jesus. From the snares of the devil, deliver us, O Jesus. From the spirit of fornication, deliver us, O Jesus. From everlasting death, deliver us, O Jesus. Lamb of God, who takest away the sins of the world, spare us, O Jesus. Lamb of God, who takest away the sins of the world, graciously hear us, O Jesus. Lamb of God, who takest away the sins of the world, have mercy on us, O Jesus. Jesus, hear us. Jesus, graciously hear us. Let us pray: O Lord Jesus Christ, who hast said: Ask and ye shall receive, seek and ye shall find, knock and it shall be opened unto you; grant, we beseech Thee, to us who ask, the gift of Thy most divine love, that we may ever love Thee with our whole heart, in word and deed, and never cease praising Thee. Who livest and reignest world without end. Amen.""",
                latinText = """Kyrie, eleison. Christe, eleison. Kyrie, eleison. Iesu, audi nos. Iesu, exaudi nos. Pater de caelis, Deus, miserere nobis. Fili, Redemptor mundi, Deus, miserere nobis. Spiritus Sancte, Deus, miserere nobis. Sancta Trinitas, unus Deus, miserere nobis. Iesu, Fili Dei vivi, miserere nobis. Iesu, splendor Patris, miserere nobis. Iesu, candor lucis aeternae, miserere nobis. Iesu, rex gloriae, miserere nobis. Iesu, sol iustitiae, miserere nobis. Iesu, Fili Mariae Virginis, miserere nobis. Agnus Dei, qui tollis peccata mundi, parce nobis, Iesu. Agnus Dei, qui tollis peccata mundi, exaudi nos, Iesu. Agnus Dei, qui tollis peccata mundi, miserere nobis, Iesu. Amen."""
            )
        )

        list.add(
            PrayerEntity(
                id = "litany_precious_blood",
                title = "Litany of the Most Precious Blood of Our Lord Jesus Christ",
                category = "Litanies & Devotions",
                subtitle = "Papally Approved Litany",
                englishText = """Lord, have mercy on us. Christ, have mercy on us. Lord, have mercy on us. Christ, hear us. Christ, graciously hear us. God the Father of Heaven, have mercy on us. God the Son, Redeemer of the world, have mercy on us. God the Holy Ghost, have mercy on us. Holy Trinity, one God, have mercy on us.
Blood of Christ, only-begotten Son of the Eternal Father, save us.
Blood of Christ, Incarnate Word of God, save us.
Blood of Christ, of the New and Eternal Testament, save us.
Blood of Christ, falling upon the earth in the Agony, save us.
Blood of Christ, shed profusely in the Scourging, save us.
Blood of Christ, flowing forth in the Crowning with Thorns, save us.
Blood of Christ, poured out on the Cross, save us.
Blood of Christ, price of our salvation, save us.
Blood of Christ, without which there is no forgiveness, save us.
Blood of Christ, Eucharistic drink and refreshment of souls, save us.
Blood of Christ, stream of mercy, save us.
Blood of Christ, victor over demons, save us.
Blood of Christ, courage of Martyrs, save us.
Blood of Christ, strength of Confessors, save us.
Blood of Christ, bringing forth Virgins, save us.
Blood of Christ, help of those in peril, save us.
Blood of Christ, relief of the burdened, save us.
Blood of Christ, solace in sorrow, save us.
Blood of Christ, hope of the penitent, save us.
Blood of Christ, consolation of the dying, save us.
Blood of Christ, peace and tenderness of hearts, save us.
Blood of Christ, pledge of eternal life, save us.
Blood of Christ, freeing souls from purgatory, save us.
Blood of Christ, most worthy of all glory and honor, save us.
Lamb of God, who takest away the sins of the world, spare us, O Lord.
Lamb of God, who takest away the sins of the world, graciously hear us, O Lord.
Lamb of God, who takest away the sins of the world, have mercy on us.
V. Thou hast redeemed us, O Lord, in Thy Blood.
R. And hast made us, for our God, a kingdom.
Let us pray: Almighty, everlasting God, who hast appointed Thine only-begotten Son to be the Redeemer of the world, and hast willed to be appeased by His Blood; grant unto us, we beseech Thee, so to venerate with solemn worship the price of our salvation, that by its power we may be defended from the evils of the present life on earth, and enjoy its everlasting fruit in Heaven. Through the same Christ our Lord. Amen.""",
                latinText = """Kyrie, eleison. Christe, eleison. Kyrie, eleison. Sanguis Christi, Unigeniti Patris Aeterni, salva nos. Sanguis Christi, Verbi Dei incarnati, salva nos. Sanguis Christi, Novi et Aeterni Testamenti, salva nos. Agnus Dei, qui tollis peccata mundi, parce nobis, Domine. Agnus Dei, qui tollis peccata mundi, exaudi nos, Domine. Agnus Dei, qui tollis peccata mundi, miserere nobis. Amen."""
            )
        )

        // -------------------------------------------------------------------
        // 5. PRAYERS FOR THE SICK, DYING, DECEASED & PURGATORY
        // -------------------------------------------------------------------
        list.add(
            PrayerEntity(
                id = "de_profundis",
                title = "De Profundis (Psalm 129)",
                category = "Prayers for Deceased",
                subtitle = "Traditional Penitential Psalm for Holy Souls",
                englishText = """Out of the depths I have cried to Thee, O Lord: Lord, hear my voice. Let Thine ears be attentive to the voice of my supplication. If Thou, O Lord, wilt mark iniquities: Lord, who shall stand it? For with Thee there is merciful forgiveness: and by reason of Thy law, I have waited for Thee, O Lord. My soul hath relied on His word: my soul hath hoped in the Lord. From the morning watch even until night: let Israel hope in the Lord. Because with the Lord there is mercy: and with Him plentiful redemption. And He shall redeem Israel from all his iniquities.
V. Eternal rest grant unto them, O Lord.
R. And let perpetual light shine upon them.
May they rest in peace. Amen.""",
                latinText = """De profundis clamavi ad te, Domine: Domine, exaudi vocem meam. Fiant aures tuae intendentes: in vocem deprecationis meae. Si iniquitates observaveris, Domine: Domine, quis sustinebit? Quia apud te misericordia est: et propter legem tuam sustinui te, Domine. Sustinuit anima mea in verbo eius: speravit anima mea in Domino. A custodia matutina usque ad noctem: speret Israel in Domino. Quia apud Dominum misericordia: et copiosa apud eum redemptio. Et ipse redimet Israel: ex omnibus iniquitatibus eius.
V. Requiem aeternam dona eis, Domine.
R. Et lux perpetua luceat eis.
Requiescant in pace. Amen."""
            )
        )

        list.add(
            PrayerEntity(
                id = "prayer_happy_death",
                title = "Prayer for a Happy Death",
                category = "Prayers for Dying",
                subtitle = "Appendix Prayer for Final Perseverance",
                englishText = """O Lord Jesus Christ, God of goodness and Father of mercies, I approach Thee with a contrite and humble heart; to Thee I recommend my last hour and that which awaits me after death. When my feet, numbed with death, shall warn me that my course in this life is near its finish, merciful Jesus, have mercy on me. When my trembling hands shall no longer be able to hold Thy Crucifix, and shall let it fall upon my bed of suffering, merciful Jesus, have mercy on me. When my eyes, dim and distorted by the dread of impending death, shall fix their langupathy gaze upon Thee, merciful Jesus, have mercy on me. When my soul shall depart from my body, receive it into Thy divine hands, merciful Jesus, have mercy on me. Amen.""",
                latinText = """Domine Iesu Christe, Deus bonitatis et Pater misericordiarum, contrito et humiliato corde me tibi commendo in hora mortis meae. Misericors Iesu, miserere mei. Amen."""
            )
        )

        // -------------------------------------------------------------------
        // 6. ALL TRADITIONAL PRAYERS OF ASPIRATION (SHORT EJACULATIONS)
        // -------------------------------------------------------------------
        list.add(
            PrayerEntity(
                id = "aspirations_collection",
                title = "Complete Appendix Prayers of Aspiration & Ejaculations",
                category = "Aspirations & Ejaculations",
                subtitle = "Short Indulgenced Ejaculations for Constant Recollection",
                englishText = """1. Jesus, Mary, and Joseph, I give you my heart and my soul.
2. Jesus, Mary, and Joseph, assist me in my last agony.
3. Jesus, Mary, and Joseph, may I breathe forth my soul in peace with you.
4. O Mary, conceived without sin, pray for us who have recourse to thee!
5. Sacred Heart of Jesus, I place my trust in Thee!
6. Eucharistic Heart of Jesus, have mercy on us!
7. Sweet Heart of Mary, be my salvation!
8. Jesus, meek and humble of heart, make my heart like unto Thine.
9. My Lord and my God! (said at the Elevation of the Host)
10. O Sacrament most Holy, O Sacrament Divine, all praise and all thanksgiving be every moment Thine!
11. Praised, adored, loved, and preserved be the Sacred Heart of Jesus in all the tabernacles of the world until the end of time. Amen.
12. St. Joseph, protector of the Holy Family and patron of the universal Church, pray for us!
13. Holy Guardian Angel, defend me in battle and guide my steps.
14. My Jesus, mercy!
15. Lord, increase our faith!
16. Divine Heart of Jesus, convert sinners, save the dying, and set free the Holy Souls in Purgatory!
17. In the Name of the Father, and of the Son, and of the Holy Ghost. Amen.""",
                latinText = """1. Iesus, Maria, Ioseph, vobis cor et animam meam dono.
2. Iesus, Maria, Ioseph, adstate mihi in extrema agone.
3. Iesus, Maria, Ioseph, in pace vobiscum obdormiam et requiescam.
4. Regina sine labe originali concepta, ora pro nobis!
5. Sacratissimum Cor Iesu, in te confido!
6. Cor Iesu Eucharisticum, miserere nobis!
7. Dulcissimum Cor Mariae, esto salus mea!
8. Iesu, mitis et humilis Corde, fac cor nostrum secundum Cor tuum.
9. Dominus meus et Deus meus!
10. O Sacramentum sanctissimum, o Sacramentum divinum, omnis laus et gratiarum actio sit tibi omni momento!
11. Ioseph, custos Sanctae Familiae et patronus Ecclesiae, ora pro nobis!
12. Sancte Angele Custos, defende me in praelio.
13. Iesu mi, misericordia!
14. Domine, adauge nobis fidem!
15. In nomine Patris, et Filii, et Spiritus Sancti. Amen."""
            )
        )

        return list
    }
}

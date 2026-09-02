package com.example.data

import com.example.data.prayers.TraditionalPrayerLibrary

object PrayerAndMissalDataSeeder {

    fun getInitialPrayers(): List<PrayerEntity> {
        val list = mutableListOf<PrayerEntity>()

        // 1. Morning Prayers
        TraditionalPrayerLibrary.morningPrayers.forEach { item ->
            list.add(
                PrayerEntity(
                    id = item.id,
                    title = item.title,
                    category = item.category,
                    subtitle = item.subtitle,
                    englishText = item.englishText,
                    latinText = item.latinText,
                    notes = item.notes
                )
            )
        }

        // 1b. Leonine Prayers
        TraditionalPrayerLibrary.leoninePrayers.forEach { item ->
            list.add(
                PrayerEntity(
                    id = item.id,
                    title = item.title,
                    category = item.category,
                    subtitle = item.subtitle,
                    englishText = item.englishText,
                    latinText = item.latinText,
                    notes = item.notes
                )
            )
        }

        // 2. Night Prayers
        TraditionalPrayerLibrary.nightPrayers.forEach { item ->
            list.add(
                PrayerEntity(
                    id = item.id,
                    title = item.title,
                    category = item.category,
                    subtitle = item.subtitle,
                    englishText = item.englishText,
                    latinText = item.latinText,
                    notes = item.notes
                )
            )
        }

        // 3. Angelus & Regina Caeli
        TraditionalPrayerLibrary.angelusAndRegina.forEach { item ->
            list.add(
                PrayerEntity(
                    id = item.id,
                    title = item.title,
                    category = item.category,
                    subtitle = item.subtitle,
                    englishText = item.englishText,
                    latinText = item.latinText,
                    notes = item.notes
                )
            )
        }

        // 4. Immaculate Heart Devotions
        TraditionalPrayerLibrary.devotionsImmaculateHeart.forEach { item ->
            list.add(
                PrayerEntity(
                    id = item.id,
                    title = item.title,
                    category = item.category,
                    subtitle = item.subtitle,
                    englishText = item.englishText,
                    latinText = item.latinText,
                    notes = item.notes
                )
            )
        }

        // 5. Sacred Heart Devotions
        TraditionalPrayerLibrary.devotionsSacredHeart.forEach { item ->
            list.add(
                PrayerEntity(
                    id = item.id,
                    title = item.title,
                    category = item.category,
                    subtitle = item.subtitle,
                    englishText = item.englishText,
                    latinText = item.latinText,
                    notes = item.notes
                )
            )
        }

        // 6. Eucharistic Devotions
        TraditionalPrayerLibrary.eucharisticDevotions.forEach { item ->
            list.add(
                PrayerEntity(
                    id = item.id,
                    title = item.title,
                    category = item.category,
                    subtitle = item.subtitle,
                    englishText = item.englishText,
                    latinText = item.latinText,
                    notes = item.notes
                )
            )
        }

        // 7. Protection & Saints Devotions
        TraditionalPrayerLibrary.protectionAndSaints.forEach { item ->
            list.add(
                PrayerEntity(
                    id = item.id,
                    title = item.title,
                    category = item.category,
                    subtitle = item.subtitle,
                    englishText = item.englishText,
                    latinText = item.latinText,
                    notes = item.notes
                )
            )
        }

        // 8. Additional Essential Traditional Catholic Prayers
        list.add(
            PrayerEntity(
                id = "st_michael",
                title = "Prayer to St. Michael the Archangel",
                category = "Protection & Spiritual Warfare",
                subtitle = "Sancte Michael Archangele",
                englishText = """St. Michael the Archangel, defend us in battle. Be our protection against the wickedness and snares of the devil. May God rebuke him, we humbly pray, and do thou, O Prince of the Heavenly Host, by the power of God, cast into hell Satan and all the evil spirits who prowl about the world seeking the ruin of souls. Amen.""",
                latinText = """Sancte Michael Archangele, defende nos in praelio; contra nequitiam et insidias diaboli esto praesidium. Imperet illi Deus, supplices deprecamur: tuque, Princeps militiae caelestis, Satanam aliosque spiritus malignos, qui ad perditionem animarum pervagantur in mundo, divina virtute, in infernum detrude. Amen."""
            )
        )

        list.add(
            PrayerEntity(
                id = "salve_regina",
                title = "Salve Regina (Hail Holy Queen)",
                category = "Marian Prayers",
                subtitle = "Traditional Concluding Antiphon",
                englishText = """Hail, Holy Queen, Mother of Mercy, our life, our sweetness and our hope. To thee do we cry, poor banished children of Eve. To thee do we send up our sighs, mourning and weeping in this valley of tears. Turn then, most gracious advocate, thine eyes of mercy toward us, and after this our exile, show unto us the blessed fruit of thy womb, Jesus. O clement, O loving, O sweet Virgin Mary.
V. Pray for us, O Holy Mother of God.
R. That we may be made worthy of the promises of Christ.""",
                latinText = """Salve, Regina, Mater misericordiae, vita, dulcedo, et spes nostra, salve. Ad te clamamus exsules filii Hevae. Ad te suspiramus gementes et flentes in hac lacrimarum valle. Eia ergo, advocata nostra, illos tuos misericordes oculos ad nos converte. Et Iesum, benedictum fructum ventris tui, nobis post hoc exsilium ostende. O clemens, o pia, o dulcis Virgo Maria.
V. Ora pro nobis, Sancta Dei Genetrix.
R. Ut digni efficiamur promissionibus Christi."""
            )
        )

        list.add(
            PrayerEntity(
                id = "memorare",
                title = "The Memorare",
                category = "Marian Prayers",
                subtitle = "Prayer of St. Bernard of Clairvaux",
                englishText = """Remember, O most gracious Virgin Mary, that never was it known that anyone who fled to thy protection, implored thy help, or sought thine intercession was left unaided. Inspired by this confidence, I fly unto thee, O Virgin of virgins, my Mother; to thee do I come, before thee I stand, sinful and sorrowful. O Mother of the Word Incarnate, despise not my petitions, but in thy mercy hear and answer me. Amen.""",
                latinText = """Memorare, o piissima Virgo Maria, a saeculo non esse auditum quemquam ad tua currentem praesidia, tua implorantem auxilia, tua petentem suffragia, esse derelictum. Ego tali animatus confidentia, ad te, Virgo Virginum, Mater, curro, ad te venio, coram te gemens peccator assisto. Noli, Mater Verbi, verba mea despicere; sed audi propitia et exaudi. Amen."""
            )
        )

        list.add(
            PrayerEntity(
                id = "sub_tuum",
                title = "Sub Tuum Praesidium",
                category = "Marian Prayers",
                subtitle = "Oldest known Marian Prayer (c. 250 AD)",
                englishText = """We fly to thy protection, O Holy Mother of God; despise not our petitions in our necessities, but deliver us always from all dangers, O Glorious and Blessed Virgin. Amen.""",
                latinText = """Sub tuum praesidium confugimus, Sancta Dei Genetrix. Nostras deprecationes ne despicias in necessitatibus, sed a periculis omnibus libera nos semper, Virgo gloriosa et benedicta. Amen."""
            )
        )

        list.add(
            PrayerEntity(
                id = "litany_loretto",
                title = "Litany of Loreto (Litaniae Lauretanae)",
                category = "Litanies & Marian Devotions",
                subtitle = "Approved Papal Marian Litany",
                englishText = """Holy Mary, pray for us. Holy Mother of God, pray for us. Holy Virgin of virgins, pray for us. Mother of Christ, pray for us. Mother of divine grace, pray for us. Mother most pure, pray for us. Mother most chaste, pray for us. Queen of Angels, pray for us. Queen of Patriarchs, pray for us. Queen of Apostles, pray for us. Queen of Martyrs, pray for us. Queen of All Saints, pray for us. Queen conceived without original sin, pray for us. Queen assumed into heaven, pray for us. Queen of the Most Holy Rosary, pray for us. Queen of Peace, pray for us.""",
                latinText = """Sancta Maria, ora pro nobis. Sancta Dei Genetrix, ora pro nobis. Sancta Virgo virginum, ora pro nobis. Mater Christi, ora pro nobis. Mater divinae gratiae, ora pro nobis. Mater purissima, ora pro nobis. Regina Angelorum, ora pro nobis. Regina Patriarcharum, ora pro nobis. Regina Apostolorum, ora pro nobis. Regina Martyrum, ora pro nobis. Regina Confessorum, ora pro nobis. Regina Virginum, ora pro nobis. Regina Sanctorum omnium, ora pro nobis. Regina sine labe originali concepta, ora pro nobis. Regina in caelum assumpta, ora pro nobis. Regina Sacratissimi Rosarii, ora pro nobis. Regina pacis, ora pro nobis."""
            )
        )

        list.add(
            PrayerEntity(
                id = "litany_st_joseph",
                title = "Litany of St. Joseph",
                category = "Litanies & Devotions",
                subtitle = "Patron of the Universal Church & Families",
                englishText = """St. Joseph, renowned offspring of David, pray for us. Light of Patriarchs, pray for us. Spouse of the Mother of God, pray for us. Chaste guardian of the Virgin, pray for us. Foster father of the Son of God, pray for us. Diligent protector of Christ, pray for us. Head of the Holy Family, pray for us. Joseph most just, pray for us. Joseph most chaste, pray for us. Joseph most prudent, pray for us. Joseph most courageous, pray for us. Joseph most obedient, pray for us. Mirror of patience, pray for us. Lover of poverty, pray for us. Model of artisans, pray for us. Glory of home life, pray for us. Guardian of virgins, pray for us. Solace of the wretched, pray for us. Hope of the sick, pray for us. Patron of the dying, pray for us. Terror of demons, pray for us. Protector of Holy Church, pray for us.""",
                latinText = """Sancte Ioseph, proles David inclyta, ora pro nobis. Lumen Patriarcharum, ora pro nobis. Dei Genetricis Sponse, ora pro nobis. Custos pudice Virginis, ora pro nobis. Altoris Filii Dei nutrix sedule, ora pro nobis. Sedulus defensor Christi, ora pro nobis. Almae Familiae praeses, ora pro nobis. Ioseph iustissime, ora pro nobis. Ioseph castissime, ora pro nobis. Ioseph prudentissime, ora pro nobis. Ioseph fortissime, ora pro nobis. Ioseph oboedientissime, ora pro nobis. Speculum patientiae, ora pro nobis. Amator paupertatis, ora pro nobis. Exemplar opificum, ora pro nobis. Domesticae vitae decus, ora pro nobis. Custos virginum, ora pro nobis. Solacium miserorum, ora pro nobis. Spes aegrotantium, ora pro nobis. Patronus morientium, ora pro nobis. Terror daemonum, ora pro nobis. Protector Sanctae Ecclesiae, ora pro nobis."""
            )
        )

        // Appendix Traditional Prayers
        list.addAll(AppendixPrayersSeeder.getAppendixPrayers())

        return list
    }
}

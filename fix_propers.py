import re

with open("app/src/main/java/com/example/data/missal/DailyPropers1954.kt", "r") as f:
    content = f.read()

martha_proper = """        "07-29" to MassProper(
            title = "St. Martha, Virgin",
            latinTitle = "S. Marthae, Virginis",
            rank = "Semi-Double",
            vestmentColor = "White",
            introitLatin = "Dilexísti iustítiam, et odísti iniquitátem: proptérea unxit te Deus, Deus tuus, óleo laetítiae prae consórtibus tuis. Ps. 44:2 Eructávit cor meum verbum bonum: dico ego ópera mea Regi.",
            introitEnglish = "Thou hast loved justice and hated iniquity: therefore God, thy God, hath anointed thee with the oil of gladness above thy fellows. Ps. 44:2 My heart hath uttered a good word: I speak my works to the King.",
            collectLatin = "Exáudi nos, Deus, salutáris noster: ut, sicut de beátae Marthae Vírginis tuae festivitáte gaudémus; ita piae devotiónis erudiámur afféctu. Per Dóminum.",
            collectEnglish = "Hear us, O God, our Savior, that, as we rejoice in the festivity of blessed Martha, Thy Virgin, so we may be taught by the affection of her pious devotion. Through our Lord.",
            epistleRef = "2 Corinthians 10:17-18; 11:1-2",
            epistleLatin = "Fratres: Qui gloriátur, in Dómino gloriétur. Non enim qui seípsum comméndat, ille probátus est; sed quem Deus comméndat. Útinam sustinerétis módicum quid insipiéntiae meae, sed et portáte me: aemulor enim vos Dei aemulatióne. Despóndi enim vos uni viro vírginem castam exhibére Christo.",
            epistleEnglish = "Brethren: He that glorieth, let him glory in the Lord. For not he who commendeth himself is approved; but he whom God commendeth. Would to God you could bear with some little of my folly, but do bear with me: for I am jealous of you with the jealousy of God. For I have espoused you to one husband, that I may present you as a chaste virgin to Christ.",
            gradualLatin = "Spécie tua et pulchritúdine tua inténde, próspere procéde, et regna. V. Propter veritátem, et mansuetúdinem, et iustítiam: et dedúcet te mirabíliter déxtera tua. Allelúia, allelúia. V. Adducéntur Regi vírgines post eam: próximae eius afferéntur tibi in laetítia. Allelúia.",
            gradualEnglish = "With thy comeliness and thy beauty set out, proceed prosperously, and reign. V. Because of truth, and meekness, and justice: and thy right hand shall conduct thee wonderfully. Alleluia, alleluia. V. After her shall virgins be brought to the King: her neighbors shall be brought to thee with gladness. Alleluia.",
            gospelRef = "Luke 10:38-42",
            gospelLatin = "In illo témpore: Intrávit Iesus in quoddam castéllum: et múlier quaedam, Martha nómine, excépit illum in domum suam, et huic erat soror nómine María, quae étiam sedens secus pedes Dómini, audiébat verbum illíus. Martha autem satagébat circa frequens ministérium: quae stetit, et ait: Dómine, non est tibi curae quod soror mea relíquit me solam ministráre? dic ergo illi ut me ádiuvet. Et respóndens dixit illi Dóminus: Martha, Martha, sollícita es, et turbáris erga plúrima, porro unum est necessárium. María óptimam partem elégit, quae non auferétur ab ea.",
            gospelEnglish = "At that time, Jesus entered into a certain town: and a certain woman named Martha received Him into her house, and she had a sister called Mary, who sitting also at the Lord's feet, heard His word. But Martha was busy about much serving: who stood and said: Lord, hast Thou no care that my sister hath left me alone to serve? Speak to her therefore, that she help me. And the Lord answering, said to her: Martha, Martha, thou art careful, and art troubled about many things, but one thing is necessary. Mary hath chosen the best part, which shall not be taken away from her.",
            offertoryLatin = "Fíliae regum in honóre tuo: ástitit regína a dextris tuis in vestítu deauráto, circúmdata varietáte.",
            offertoryEnglish = "The daughters of kings are in thine honor: the queen stood on thy right hand, in gilded clothing, surrounded with variety.",
            secretLatin = "Accépta tibi sit, Dómine, sacrátae plebis oblátio pro tuórum honóre Sanctórum: quorum se méritis de tribulatióne percepísse cognóscit auxílium. Per Dóminum.",
            secretEnglish = "May the offering of Thy holy people be accepted by Thee, O Lord, in honor of Thy Saints: by whose merits they know they have received help in tribulation. Through our Lord.",
            communionLatin = "Quinque prudéntes vírgines accepérunt óleum in vasis suis cum lampádibus: média autem nocte clamor factus est: Ecce sponsus venit: exíte óbviam Christo Dómino.",
            communionEnglish = "The five wise virgins took oil in their vessels with the lamps: and at midnight there was a cry made: Behold the bridegroom cometh: go ye forth to meet Christ the Lord.",
            postcommunionLatin = "Satiásti, Dómine, famíliam tuam munéribus sacris: eius, quaésumus, semper interventióne nos réfove, cuius solémnia celebrámus. Per Dóminum.",
            postcommunionEnglish = "Thou hast filled Thy family, O Lord, with holy gifts: we beseech Thee, do Thou ever comfort us by her intercession, whose festival we celebrate. Through our Lord."
        ),
        // Common / General Daily Mass Propers"""

content = content.replace("        // Common / General Daily Mass Propers", martha_proper)

# Now edit getTodayProper
new_get_today_proper = """    fun getTodayProper(dateStr: String? = null): MassProper {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) // 0-based
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        val lookupStr = dateStr ?: String.format("%02d-%02d", month + 1, day)

        if (propersMap.containsKey(lookupStr)) {
            return propersMap[lookupStr]!!
        }

        return when {
            month == Calendar.DECEMBER && day in 24..25 -> propersMap["CHRISTMAS"]!!
            month == Calendar.APRIL || month == Calendar.MARCH -> propersMap["EASTER"]!! // Simplified seasonal match
            month == Calendar.MAY || month == Calendar.JUNE -> propersMap["PENTECOST"]!!
            else -> propersMap["GENERAL"]!!
        }
    }"""

content = re.sub(r'    fun getTodayProper\(\): MassProper \{.*?    \}', new_get_today_proper, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/data/missal/DailyPropers1954.kt", "w") as f:
    f.write(content)

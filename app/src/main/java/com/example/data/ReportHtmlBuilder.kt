package com.example.data

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import java.util.Calendar

object ReportHtmlBuilder {
    private fun esc(s: String?): String = (s ?: "")
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")

    fun printHtmlReport(context: Context, jobName: String, html: String, landscape: Boolean = true) {
        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false
            override fun onPageFinished(view: WebView?, url: String?) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
                val adapter = webView.createPrintDocumentAdapter(jobName)
                val attrs = PrintAttributes.Builder()
                    .setMediaSize(if (landscape) PrintAttributes.MediaSize.ISO_A4.asLandscape() else PrintAttributes.MediaSize.ISO_A4)
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()
                printManager.print(jobName, adapter, attrs)
            }
        }
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }

    private val commonCss = """
        <style>
            * { box-sizing: border-box; font-family: sans-serif; }
            body { margin: 12px; color: #000; background: #fff; font-size: 11px; }
            h2, h3 { margin: 4px 0; text-align: center; }
            table { border-collapse: collapse; width: 100%; margin: 10px 0; }
            th, td { border: 1.5px solid #000; padding: 4px 6px; text-align: center; font-weight: 700; }
            th { background: #e2e8f0; }
            .l { text-align: left; }
            .r { text-align: right; }
            .c-cy { background: #00ffff !important; }
            .c-ye { background: #ffff00 !important; }
            .c-or { background: #ff9900 !important; }
            .c-gr { background: #00ff00 !important; }
            .c-pk { background: #ead1dc !important; }
            .c-bl { background: #c9daf8 !important; }
            .c-gy { background: #d9d9d9 !important; }
            .t-red { color: #cc0000; }
            .t-blue { color: #1d4ed8; }
            .page-break { page-break-after: always; }
            .sm th, .sm td { font-size: 8.5px; padding: 2px 3px; }
        </style>
    """.trimIndent()

    fun buildMonthlyA4ReportHtml(
        sub: SubstationDoc,
        year: Int,
        monthIdx: Int,
        feeders: List<MonthlyFeederStat>,
        loss: MonthlyLossStat?,
        auxMeters: List<NameplateDoc>
    ): String {
        val mName = MasterSartodiData.MONTHS.getOrElse(monthIdx) { "JANUARY" }
        val feederRows = if (feeders.isEmpty()) {
            """<tr><td colspan="13">या महिन्याचा डेटा उपलब्ध नाही</td></tr>"""
        } else {
            feeders.joinToString("") { r ->
                """<tr><td class="l">${esc(r.feeder)}</td><td>${r.maxLoad}</td><td>${r.minLoad}</td><td>${r.avgLoad}</td><td>${if (r.maxVolt > 0) r.maxVolt else "-"}</td><td>${if (r.minVolt > 0) r.minVolt else "-"}</td><td>${r.tp}</td><td>${r.ht}</td><td>${r.sd}</td><td>${r.bd}</td><td>${r.ls}</td><td><b>${r.totalTripping}</b></td><td>${r.durationMin}</td></tr>"""
            }
        }
        val meterRows = auxMeters.joinToString("") { m ->
            """<tr><td class="l">${esc(m.bay)}</td><td class="l">${esc(m.name)}</td><td><b>${esc(m.serialNo)}</b></td><td class="l">${esc(m.specs["spec"])}</td></tr>"""
        }
        return """
            <!DOCTYPE html><html><head><meta charset="utf-8">$commonCss</head><body>
            <div style="text-align:center; border-bottom:2px solid #000; padding-bottom:8px; margin-bottom:10px;">
                <div style="font-size:10px;">महाराष्ट्र राज्य विद्युत वितरण कंपनी मर्यादित (MSEDCL)</div>
                <h2>${esc(sub.name)} — कोड: ${esc(sub.code)}</h2>
                <div>${esc(sub.division)} • मासिक तांत्रिक व कामकाज अहवाल (माहे: $mName $year)</div>
            </div>
            <h4>१. मासिक उपकेंद्र एनर्जी व लॉस सारांश</h4>
            <table>
                <tr><th>33kV इनकमर (kWh)</th><th>11kV आउटगोइंग (kWh)</th><th>फरक युनिट्स (kWh)</th><th>उपकेंद्र लॉस %</th></tr>
                <tr>
                    <td>${loss?.let { MasterSartodiData.fmtIndian(it.incomerKwh) } ?: "N/A"}</td>
                    <td>${loss?.let { MasterSartodiData.fmtIndian(it.outgoingKwh) } ?: "N/A"}</td>
                    <td>${loss?.let { MasterSartodiData.fmtIndian(it.diffKwh) } ?: "N/A"}</td>
                    <td>${loss?.let { "${it.lossPct} %" } ?: "N/A"}</td>
                </tr>
            </table>
            <h4>२. फिडरनिहाय मासिक ट्रिपिंग, लोड व व्होल्टेज सारांश ($mName $year)</h4>
            <table>
                <tr><th>फिडर / उपकरण</th><th>Max A</th><th>Min A</th><th>Avg A</th><th>Max kV</th><th>Min kV</th><th>TP</th><th>HT</th><th>SD</th><th>BD</th><th>LS</th><th>Total</th><th>Min</th></tr>
                $feederRows
            </table>
            <h4>३. SECURE मीटर सिरीयल नंबर, CT Ratio, MF व रिले सेटिंग मास्टर</h4>
            <table>
                <tr><th>बे / फिडर</th><th>मीटर व कंपनी</th><th>Serial No.</th><th>तांत्रिक तपशील</th></tr>
                $meterRows
            </table>
            <div style="display:flex; justify-content:space-between; margin-top:28px; font-weight:700;">
                <span>यंत्रचालक / शिफ्ट ऑपरेटर स्वाक्षरी</span>
                <span>सहाय्यक अभियंता / उपकार्यकारी अभियंता स्वाक्षरी</span>
            </div>
            </body></html>
        """.trimIndent()
    }

    fun buildDailyLossHtml(sub: SubstationDoc, date: String, calc: DailyLossSummary): String {
        val parts = date.split("-")
        val monthTitle = if (parts.size == 3) {
            "${MasterSartodiData.MONTHS.getOrElse((parts[1].toIntOrNull() ?: 1) - 1) { "" }} ${parts[0]}"
        } else date
        val dmy = MasterSartodiData.formatDmy(date)
        val rows = calc.rows.mapIndexed { idx, r ->
            """<tr><td>${idx + 1}</td><td class="l">${esc(r.meter.name)}</td><td class="r">${r.initialVal ?: ""}</td><td class="r">${r.finalVal ?: ""}</td><td class="r">${r.diff?.let { "%.2f".format(it) } ?: ""}</td><td class="r">${r.mf.toInt()}</td><td class="r">${MasterSartodiData.fmtIndian(r.consumption)}</td></tr>"""
        }.joinToString("")
        val lossRows = calc.lossRows.joinToString("") { r ->
            """<tr><td class="l">${esc(r.label)}</td><td class="r">${MasterSartodiData.fmtIndian(r.diffKwh)}</td><td class="r">${r.lossPct?.let { "${"%.3f".format(it)} %" } ?: ""}</td></tr>"""
        }
        return """
            <!DOCTYPE html><html><head><meta charset="utf-8">$commonCss</head><body>
            <h2>${esc(sub.name)} (${esc(sub.code)})</h2>
            <h3>SUBSTATION LOSS CALCULATION — $monthTitle ($dmy)</h3>
            <table>
                <tr><th>अ.क्र.</th><th>FEEDER NAME</th><th>INITIAL @00:00</th><th>FINAL @24:00</th><th>DIFFERENCE</th><th>M.F.</th><th>CONSUMPTION (kWh)</th></tr>
                $rows
            </table>
            <table>
                <tr><th>LOSS COMPARISON</th><th>CALC. (kWh)</th><th>LOSS IN %</th></tr>
                $lossRows
            </table>
            </body></html>
        """.trimIndent()
    }

    fun buildSelectedMonthlyPackHtml(
        sub: SubstationDoc,
        yearMonth: String,
        selectedSections: Set<String>,
        allMinMax: Map<String, MinMaxDayDoc>,
        allDailyLosses: Map<String, DailyLossDayDoc>,
        mfOverrides: Map<String, Double>,
        trips: List<TripEntry>,
        faults: List<FaultEntry>
    ): String {
        val parts = yearMonth.split("-")
        val yr = parts.getOrNull(0)?.toIntOrNull() ?: 2026
        val mon = parts.getOrNull(1)?.toIntOrNull() ?: 1
        val titleMonth = "${MasterSartodiData.MONTHS.getOrElse(mon - 1) { "JANUARY" }} $yr"
        val cal = Calendar.getInstance().apply { set(yr, mon - 1, 1) }
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val sections = mutableListOf<String>()

        if ("minmax" in selectedSections) {
            val st = MasterSartodiData.computeMinMaxMonthStats(yearMonth, allMinMax)
            val body = MasterSartodiData.MINMAX_ITEMS.joinToString("") { it ->
                val head = it.sectionHeader?.let { h -> """<tr class="${it.cssClass}"><td colspan="7">$h</td></tr>""" } ?: ""
                val s = st[it.id] ?: MinMaxMonthStat()
                head + """<tr class="${it.cssClass}"><td class="l">${esc(it.name)}</td><td>${if (s.maxDate.isNotBlank()) MasterSartodiData.formatDmy(s.maxDate) else ""}</td><td>${esc(s.maxTime)}</td><td>${s.maxVal ?: ""}</td><td>${if (s.minDate.isNotBlank()) MasterSartodiData.formatDmy(s.minDate) else ""}</td><td>${esc(s.minTime)}</td><td>${s.minVal ?: ""}</td></tr>"""
            }
            sections += """<div class="page-break"><table><tr><th colspan="7" class="c-cy">${esc(sub.name)} — MINIMUM-MAXIMUM REPORT ($titleMonth)</th></tr><tr><th class="c-cy">FEEDER NAME</th><th class="c-cy">DATE</th><th class="c-cy">TIME</th><th class="c-cy">MAXIMUM</th><th class="c-cy">DATE</th><th class="c-cy">TIME</th><th class="c-cy">MINIMUM</th></tr>$body</table></div>"""
        }

        if ("msedcl" in selectedSections) {
            val intr = MasterSartodiData.computeMonthlyInterruptions(yearMonth, trips)
            val st = MasterSartodiData.computeMinMaxMonthStats(yearMonth, allMinMax)
            val order = listOf(
                "inc33" to "33 KV INCOMMER", "tf1" to "POWER T/F NO.1", "tf2" to "POWER T/F NO. 2",
                "hvtot" to "TOTAL H.V. LOAD", "lv1" to "INCOMMER L.V. NO.1", "lv2" to "INCOMMER L.V. NO. 2",
                "saphala" to "11 K.V. SAPHALA FEEDER", "dfcc" to "11 K.V. DFCC RAILWAY FEEDER",
                "pargaon" to "11 K.V. PARGAON FEEDER", "navghar" to "11 K.V. NAVGHAR FEEDER",
                "volt" to "11 K.V. VOLTAGE", "sstot" to "TOTAL SUBSTATION LOAD"
            )
            val types = listOf("BD", "SD", "TP", "HT", "LS", "PL")
            val body = order.mapIndexed { idx, (id, nm) ->
                val map = intr[id]
                val cells = types.joinToString("") { t ->
                    val c = map?.get(t)
                    if (c != null) "<td>${c.count}</td><td>${c.minutes}</td>" else "<td>-</td><td>-</td>"
                }
                val s = st[id]
                """<tr><td>${idx + 1}</td><td class="l">$nm</td>$cells<td>${s?.maxVal ?: "-"}</td><td>${s?.minVal ?: "-"}</td></tr>"""
            }.joinToString("")
            sections += """<div class="page-break"><table class="sm"><tr><th colspan="16" class="t-red">MAHARASHTRA STATE ELECTRICITY DISTRIBUTION CO. LTD. — Monthly Interruption Report ($titleMonth)</th></tr><tr><th>SR</th><th>FEEDER</th><th colspan="2">BD</th><th colspan="2">SD</th><th colspan="2">TP</th><th colspan="2">HT</th><th colspan="2">LS</th><th colspan="2">PL</th><th>MAX</th><th>MIN</th></tr>$body</table></div>"""
        }

        if ("intr" in selectedSections) {
            val intr = MasterSartodiData.computeMonthlyInterruptions(yearMonth, trips)
            fun fRow(k: String, nm: String, cls: String): String {
                val m = intr[k] ?: emptyMap()
                val tp = m["TP"] ?: InterruptionCount()
                val ht = m["HT"] ?: InterruptionCount()
                val sd = m["SD"] ?: InterruptionCount()
                val bd = m["BD"] ?: InterruptionCount()
                val lsN = (m["LS"]?.count ?: 0) + (m["PL"]?.count ?: 0)
                val lsM = (m["LS"]?.minutes ?: 0) + (m["PL"]?.minutes ?: 0)
                val totN = tp.count + ht.count + sd.count + bd.count + lsN
                val totM = tp.minutes + ht.minutes + sd.minutes + bd.minutes + lsM
                return """<tr class="$cls"><td class="l">$nm</td><td>${tp.count}</td><td>${tp.minutes}</td><td>${ht.count}</td><td>${ht.minutes}</td><td>${sd.count}</td><td>${sd.minutes}</td><td>${bd.count}</td><td>${bd.minutes}</td><td>$lsN</td><td>$lsM</td><td class="c-or">$totN</td><td class="c-or">$totM</td></tr>"""
            }
            sections += """<div class="page-break"><table><tr><th colspan="13" class="c-gy t-blue">INTERRUPTION REPORT MONTH OF $titleMonth</th></tr><tr><th class="c-cy">FEEDER</th><th colspan="2" class="c-cy">T/P</th><th colspan="2" class="c-cy">H/T</th><th colspan="2" class="c-cy">S/D</th><th colspan="2" class="c-cy">B/D</th><th colspan="2" class="c-cy">L/S</th><th colspan="2" class="c-cy">TOTAL</th></tr>${fRow("inc33","33 K.V. INCOMMER","c-or")}${fRow("saphala","11 K.V. SAPHALA FDR","c-ye")}${fRow("dfcc","11 K.V. DFCC RAILWAY FDR","c-ye")}${fRow("pargaon","11 K.V. PARGAON FDR","c-gr")}${fRow("navghar","11 K.V. NAVGHAR FDR","c-gr")}</table></div>"""
        }

        if ("lossm" in selectedSections) {
            val tot = MasterSartodiData.DAILY_LOSS_METERS.associate { it.id to 0.0 }.toMutableMap()
            var fullDays = 0
            for (d in 1..daysInMonth) {
                val dk = "$yr-${mon.toString().padStart(2, '0')}-${d.toString().padStart(2, '0')}"
                if (dk !in allDailyLosses) continue
                val calc = MasterSartodiData.calculateDailyLoss(dk, allDailyLosses, mfOverrides)
                if (MasterSartodiData.DAILY_LOSS_METERS.all { calc.consumptionMap[it.id] != null }) {
                    fullDays++
                    MasterSartodiData.DAILY_LOSS_METERS.forEach { m ->
                        tot[m.id] = (tot[m.id] ?: 0.0) + (calc.consumptionMap[m.id] ?: 0.0)
                    }
                }
            }
            val ls = MasterSartodiData.calculateLossSet(tot)
            val mRows = MasterSartodiData.DAILY_LOSS_METERS.joinToString("") { m ->
                """<tr><td class="l">${esc(m.name)}</td><td class="r">${if (fullDays > 0) MasterSartodiData.fmtIndian(tot[m.id]) else "—"}</td></tr>"""
            }
            val lRows = ls.lossRows.joinToString("") { r ->
                """<tr><td class="l">${esc(r.label)}</td><td class="r">${if (fullDays > 0) MasterSartodiData.fmtIndian(r.diffKwh) else "—"}</td><td class="r">${if (fullDays > 0 && r.lossPct != null) "${"%.3f".format(r.lossPct)} %" else "—"}</td></tr>"""
            }
            sections += """<div class="page-break"><table><tr><th colspan="2" class="c-cy">SUBSTATION LOSS CALCULATION — $titleMonth ($fullDays दिवस)</th></tr><tr><th class="c-cy">FEEDER NAME</th><th class="c-cy">TOTAL CONSUMPTION (kWh)</th></tr>$mRows</table><table><tr><th class="c-cy">LOSS</th><th class="c-cy">CALC. (kWh)</th><th class="c-cy">LOSS %</th></tr>$lRows</table></div>"""
        }

        if ("faults" in selectedSections) {
            val list = faults.filter {
                it.date.startsWith(yearMonth) || it.restoredDate.startsWith(yearMonth) ||
                    it.status == "बिघाड (Open)" || it.status == "दुरुस्ती सुरू"
            }
            val fRows = if (list.isEmpty()) """<tr><td colspan="8">या महिन्यात नोंद नाही</td></tr>""" else list.mapIndexed { i, f ->
                """<tr><td>${i + 1}</td><td>${MasterSartodiData.formatDmy(f.date)}</td><td class="l">${esc(f.equip)} ${esc(f.bay)}</td><td class="l">${esc(f.faultDesc)}</td><td class="l">${esc(f.actionTaken)}</td><td>${esc(f.oldSn)} → ${esc(f.newSn)}</td><td>${esc(f.status)}</td><td>${if (f.restoredDate.isNotBlank()) MasterSartodiData.formatDmy(f.restoredDate) else "-"}</td></tr>"""
            }.joinToString("")
            sections += """<div class="page-break"><table><tr><th colspan="8" class="c-cy">यंत्रसामग्री बिघाड / दुरुस्ती नोंद — $titleMonth</th></tr><tr><th>अ.क्र.</th><th>तारीख</th><th>यंत्रसामग्री</th><th>बिघाड</th><th>कार्यवाही</th><th>जुना → नवीन S/N</th><th>स्थिती</th><th>दुरुस्ती तारीख</th></tr>$fRows</table></div>"""
        }

        return """<!DOCTYPE html><html><head><meta charset="utf-8">$commonCss</head><body>${sections.joinToString("")}</body></html>"""
    }
}

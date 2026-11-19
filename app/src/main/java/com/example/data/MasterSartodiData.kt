package com.example.data

import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.round

object MasterSartodiData {
    val MONTHS = listOf(
        "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
        "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
    )

    val FEEDERS = listOf(
        "33 KV INCOMMER",
        "POWER T/F NO.1",
        "POWER T/F NO. 2",
        "TOTAL H.V. LOAD",
        "11 K.V. SAPHALA FEEDER",
        "11 K.V. DFCC RAILWAY FEEDER",
        "11 K.V. PARGAON FEEDER",
        "11 K.V. NAVGHAR FEEDER"
    )

    val TRIP_FEEDER_OPTIONS = listOf(
        "33 KV INCOMMER",
        "POWER T/F NO.1",
        "POWER T/F NO. 2",
        "11 K.V. INCOMMER L.V. NO. 1",
        "11 K.V. INCOMMER L.V. NO. 2",
        "11 K.V. SAPHALA FEEDER (205)",
        "11 K.V. DFCC RAILWAY FEEDER (204)",
        "11 K.V. NAVGHAR FEEDER (203)",
        "11 K.V. PARGAON FEEDER (201)"
    )

    val TRIP_TYPES = listOf(
        "TP - Tripping (फॉल्ट ट्रिपिंग)",
        "HT - Hand Trip (हॅण्ड ट्रिप)",
        "SD - Shutdown / LC (शटडाउन)",
        "BD - Breakdown (ब्रेकडाउन)",
        "LS - Load Shedding (लोडशेडिंग)",
        "PL - Plan Load Shedding (नियोजित लोडशेडिंग)"
    )

    data class EnergyMeterOption(val label: String, val category: String, val defaultMf: Double)
    val ENERGY_METER_OPTIONS = listOf(
        EnergyMeterOption("33 KV INCOMMER (M32450021966)", "33kV_IN", 600.0),
        EnergyMeterOption("T/F NO. 1 H.V. (X1095238)", "TF_HV", 900.0),
        EnergyMeterOption("T/F NO. 2 H.V. (X1095384)", "TF_HV", 600.0),
        EnergyMeterOption("T/F NO. 1 L.V. (XD596070)", "11kV_LV", 80.0),
        EnergyMeterOption("T/F NO. 2 L.V. (X0400243)", "11kV_LV", 60.0),
        EnergyMeterOption("11 KV SAPHALA FDR 205 (M32450003003)", "11kV_OUT", 40.0),
        EnergyMeterOption("11 KV DFCC RAILWAY FDR 204 (M32450003452)", "11kV_OUT", 40.0),
        EnergyMeterOption("11 KV NAVGHAR FDR 203 (M32450003001)", "11kV_OUT", 40.0),
        EnergyMeterOption("11 KV PARGAON FDR 201 (M32450003002)", "11kV_OUT", 40.0)
    )

    val FAULT_EQUIP_OPTIONS = listOf(
        "Power T/F No. 1", "Power T/F No. 2",
        "33 kV CT", "33 kV PT", "33 kV HV Breaker T/F-1", "33 kV HV Breaker T/F-2",
        "33 kV Isolator / Lightning Arrester",
        "11 kV VCB - Incomer LV-1", "11 kV VCB - Incomer LV-2",
        "11 kV VCB - 205 Saphala", "11 kV VCB - 204 DFCC Railway",
        "11 kV VCB - 203 Navghar", "11 kV VCB - 201 Pargaon",
        "11 kV CT / PT", "Protection Relay", "Energy Meter (SECURE)",
        "110V DC Battery Bank", "Battery Charger",
        "Control / Relay Panel", "Substation T/F (LT Supply)", "इतर"
    )

    val FAULT_STATUS_OPTIONS = listOf(
        "बिघाड (Open)",
        "दुरुस्ती सुरू",
        "दुरुस्त झाले",
        "बदलले (Replaced)"
    )

    val DAILY_LOSS_METERS = listOf(
        DailyLossMeterDef("hv1", "H.V. SIDE POWER T/F NO. 1", "H.V. T/F-1", 900.0),
        DailyLossMeterDef("lv1", "L.V. SIDE POWER T/F NO. 1", "L.V. T/F-1", 80.0),
        DailyLossMeterDef("saphala", "11 K.V. SAPHALA FDR", "SAPHALA", 40.0),
        DailyLossMeterDef("dfcc", "11 K.V. DFCC RAILWAY FDR", "DFCC", 40.0),
        DailyLossMeterDef("hv2", "H.V. SIDE POWER T/F NO. 2", "H.V. T/F-2", 600.0),
        DailyLossMeterDef("lv2", "L.V. SIDE POWER T/F NO. 2", "L.V. T/F-2", 60.0),
        DailyLossMeterDef("pargaon", "11 K.V. PARGAON FDR", "PARGAON", 40.0),
        DailyLossMeterDef("navghar", "11 K.V. NAVGHAR FDR", "NAVGHAR", 40.0),
        DailyLossMeterDef("inc", "INCOMMER METER (33 kV)", "33kV INC", 600.0),
        DailyLossMeterDef("sst", "SUBSTATION T/F", "S/S T/F", 10.0)
    )

    val MINMAX_ITEMS = listOf(
        MinMaxItemDef("inc33", "33 K.V. INCOMMER", "A", null, "c-ye"),
        MinMaxItemDef("tf1", "POWER T/F NO. 1", "A", null, "c-ye"),
        MinMaxItemDef("tf2", "POWER T/F NO. 2", "A", null, "c-ye"),
        MinMaxItemDef("hvtot", "TOTAL H.V. LOAD", "A", null, "c-ye"),
        MinMaxItemDef("lv1", "INCOMMER L.V. NO. 1", "A", "11 K.V. INCOMMER", "c-or"),
        MinMaxItemDef("lv2", "INCOMMER L.V. NO. 2", "A", null, "c-or"),
        MinMaxItemDef("saphala", "11 K.V. SAPHALA FEEDER", "A", "11 K.V. OUTGOING FEEDER", "c-gr"),
        MinMaxItemDef("dfcc", "11 K.V. DFCC RAILWAY FEEDER", "A", null, "c-gr"),
        MinMaxItemDef("pargaon", "11 K.V. PARGAON FEEDER", "A", null, "c-gr"),
        MinMaxItemDef("navghar", "11 K.V. NAVGHAR FEEDER", "A", null, "c-gr"),
        MinMaxItemDef("volt", "11 K.V. VOLTAGE", "kV", null, "c-pk"),
        MinMaxItemDef("sstot", "TOTAL S/S LOAD", "A", null, "c-bl")
    )

    // 33 Months of Substation Loss Data (Jan 2024 – Sep 2026)
    val LOSS_HISTORY: List<MonthlyLossStat> = listOf(
        MonthlyLossStat(2024, 0, "JANUARY", 1598400.0, 1582788.0, 15612.0, 0.977),
        MonthlyLossStat(2024, 1, "FEBRUARY", 1555800.0, 1542360.0, 13440.0, 0.864),
        MonthlyLossStat(2024, 2, "MARCH", 1879200.0, 1867252.0, 11948.0, 0.636),
        MonthlyLossStat(2024, 3, "APRIL", 2251200.0, 2248268.0, 2932.0, 0.13),
        MonthlyLossStat(2024, 4, "MAY", 2579400.0, 2580080.0, -680.0, -0.026),
        MonthlyLossStat(2024, 5, "JUNE", 2263800.0, 2248560.0, 15240.0, 0.673),
        MonthlyLossStat(2024, 6, "JULY", 1927200.0, 1911044.0, 16156.0, 0.838),
        MonthlyLossStat(2024, 7, "AUGUST", 1934400.0, 1936468.0, -2068.0, -0.107),
        MonthlyLossStat(2024, 8, "SEPTEMBER", 1878600.0, 1883289.0, -4689.0, -0.25),
        MonthlyLossStat(2024, 9, "OCTOBER", 2097000.0, 2098739.0, -1739.0, -0.083),
        MonthlyLossStat(2024, 10, "NOVEMBER", 1837800.0, 1834480.0, 3320.0, 0.181),
        MonthlyLossStat(2024, 11, "DECEMBER", 1737000.0, 1733852.0, 3148.0, 0.181),
        MonthlyLossStat(2025, 0, "JANUARY", 1735800.0, 1751769.0, -15969.0, -0.92),
        MonthlyLossStat(2025, 1, "FEBRUARY", 1674600.0, 1702860.0, -28260.0, -1.688),
        MonthlyLossStat(2025, 2, "MARCH", 2133000.0, 2157899.0, -24899.0, -1.167),
        MonthlyLossStat(2025, 3, "APRIL", 2310600.0, 2347251.0, -36651.0, -1.586),
        MonthlyLossStat(2025, 4, "MAY", 2155800.0, 2191509.0, -35709.0, -1.656),
        MonthlyLossStat(2025, 5, "JUNE", 2029200.0, 2019881.0, 9319.0, 0.459),
        MonthlyLossStat(2025, 6, "JULY", 2011200.0, 1991219.0, 19981.0, 0.993),
        MonthlyLossStat(2025, 7, "AUGUST", 1905600.0, 1885359.0, 20241.0, 1.062),
        MonthlyLossStat(2025, 8, "SEPTEMBER", 1756800.0, 1740804.0, 15996.0, 0.911),
        MonthlyLossStat(2025, 9, "OCTOBER", 1976400.0, 1953642.0, 22758.0, 1.151),
        MonthlyLossStat(2025, 10, "NOVEMBER", 1743600.0, 1729146.0, 14454.0, 0.829),
        MonthlyLossStat(2025, 11, "DECEMBER", 1773000.0, 1715627.0, 57373.0, 3.236),
        MonthlyLossStat(2026, 0, "JANUARY", 1865400.0, 1841398.0, 24002.0, 1.287),
        MonthlyLossStat(2026, 1, "FEBRUARY", 1851600.0, 1830920.0, 20680.0, 1.117),
        MonthlyLossStat(2026, 2, "MARCH", 2326200.0, 2304400.0, 21800.0, 0.937),
        MonthlyLossStat(2026, 3, "APRIL", 2410800.0, 2387440.0, 23360.0, 0.969),
        MonthlyLossStat(2026, 4, "MAY", 2747400.0, 2722400.0, 25000.0, 0.91),
        MonthlyLossStat(2026, 5, "JUNE", 2671800.0, 2647720.0, 24080.0, 0.901),
        MonthlyLossStat(2026, 6, "JULY", 2053800.0, 2033600.0, 20200.0, 0.984),
        MonthlyLossStat(2026, 7, "AUGUST", 2275200.0, 2249320.0, 25880.0, 1.137),
        MonthlyLossStat(2026, 8, "SEPTEMBER", 2314800.0, 2296400.0, 18400.0, 0.795)
    )

    private fun ms(
        y: Int, m: Int, f: Int,
        tp: Int, ht: Int, sd: Int, bd: Int, ls: Int, tot: Int, dur: Int,
        mxL: Double, mnL: Double, mxV: Double, mnV: Double, pdf: String = ""
    ) = MonthlyFeederStat(
        year = y, monthIdx = m, month = MONTHS[m], feederIdx = f, feeder = FEEDERS[f],
        tp = tp, ht = ht, sd = sd, bd = bd, ls = ls, totalTripping = tot, durationMin = dur,
        maxLoad = mxL, minLoad = mnL, avgLoad = round(((mxL + mnL) / 2.0) * 10.0) / 10.0,
        maxVolt = mxV, minVolt = mnV, drivePdfId = pdf
    )

    // Complete 34 months (Jan 2024 – Oct 2026) preloaded summary rows
    val MONTHLY_SUMMARY: List<MonthlyFeederStat> by lazy {
        listOf(
            // 2024
            ms(2024,0,0,3,1,2,4,0,10,829,219.0,59.0,33.7,30.5,"1dI5rxU1_Gn7uUBKyAh8XEshcqiYMRgBm"),
            ms(2024,0,1,0,0,0,0,0,0,0,82.0,14.0,0.0,0.0), ms(2024,0,2,0,0,0,0,0,0,0,32.0,9.0,0.0,0.0), ms(2024,0,3,0,0,0,0,0,0,0,114.0,0.0,0.0,0.0),
            ms(2024,0,4,8,8,2,4,0,22,891,128.0,35.0,11.7,10.4), ms(2024,0,5,5,1,2,4,0,12,841,0.0,0.0,0.0,0.0), ms(2024,0,6,12,2,4,4,0,22,888,47.0,15.0,0.0,0.0), ms(2024,0,7,9,5,2,4,0,20,919,55.0,10.0,0.0,0.0),

            ms(2024,1,0,5,0,3,2,0,10,1072,233.0,96.0,33.6,30.6,"1axamdNvWYGZ96KibRmoPzhv9eYCTgQxG"),
            ms(2024,1,1,0,0,0,0,0,0,0,46.0,16.0,0.0,0.0), ms(2024,1,2,0,0,0,0,0,0,0,36.0,8.0,0.0,0.0), ms(2024,1,3,0,0,0,0,0,0,0,82.0,0.0,0.0,0.0),
            ms(2024,1,4,15,4,3,2,0,24,1183,132.0,60.0,11.6,10.4), ms(2024,1,5,6,5,3,2,0,16,1114,0.0,0.0,0.0,0.0), ms(2024,1,6,18,3,3,2,0,26,1170,64.0,17.0,0.0,0.0), ms(2024,1,7,15,6,3,2,0,26,1159,56.0,4.0,0.0,0.0),

            ms(2024,2,0,3,0,0,1,3,7,516,219.0,60.0,33.5,30.3,"1PL-EGXQ7Xzp7-Doa5AXo7XJBcehlfIbl"),
            ms(2024,2,1,0,0,0,0,0,0,0,47.0,18.0,0.0,0.0), ms(2024,2,2,0,0,0,0,0,0,0,34.0,8.0,0.0,0.0), ms(2024,2,3,0,0,0,0,0,0,0,81.0,0.0,0.0,0.0),
            ms(2024,2,4,18,2,1,1,3,25,762,136.0,60.0,11.5,10.3), ms(2024,2,5,5,3,0,1,3,12,576,0.0,0.0,0.0,0.0), ms(2024,2,6,22,4,0,2,3,31,749,56.0,18.0,0.0,0.0), ms(2024,2,7,10,7,1,1,3,22,630,62.0,4.0,0.0,0.0),

            ms(2024,3,0,6,0,2,0,6,14,971,270.0,75.0,32.9,30.2,"1V3rBD2I0zP506N6T8EpqELiLVM8jkHCm"),
            ms(2024,3,1,0,0,0,0,0,0,0,52.0,6.0,0.0,0.0), ms(2024,3,2,0,0,0,0,0,0,0,43.0,8.0,0.0,0.0), ms(2024,3,3,0,0,0,0,0,0,0,95.0,0.0,0.0,0.0),
            ms(2024,3,4,21,11,3,1,6,42,1325,159.0,17.0,11.4,10.1), ms(2024,3,5,7,6,3,0,6,22,1055,0.0,0.0,0.0,0.0), ms(2024,3,6,21,10,8,0,6,45,2767,83.0,26.0,0.0,0.0), ms(2024,3,7,21,8,5,2,6,42,1465,66.0,3.0,0.0,0.0),

            ms(2024,4,0,4,0,3,0,5,12,918,293.0,47.0,33.2,30.2,"1heLaXaPv84sFSvVftGOY93TzUz8pndpe"),
            ms(2024,4,1,0,0,0,0,0,0,0,59.0,29.0,0.0,0.0), ms(2024,4,2,0,0,0,0,0,0,0,51.0,8.0,0.0,0.0), ms(2024,4,3,0,0,0,0,0,0,0,110.0,0.0,0.0,0.0),
            ms(2024,4,4,13,5,5,0,5,28,1248,171.0,79.0,11.3,10.2), ms(2024,4,5,4,7,3,0,9,23,949,0.0,0.0,0.0,0.0), ms(2024,4,6,31,12,6,1,5,55,1884,83.0,20.0,0.0,0.0), ms(2024,4,7,10,13,4,1,5,33,1323,76.0,6.0,0.0,0.0),

            ms(2024,5,0,10,0,2,8,4,24,1491,279.0,113.0,33.6,30.3,"1z-YIfJNSBh4gRZCaLJa6tqRJ4FyJUyvK"),
            ms(2024,5,1,0,0,0,0,0,0,0,55.0,24.0,0.0,0.0), ms(2024,5,2,0,0,0,0,0,0,0,47.0,10.0,0.0,0.0), ms(2024,5,3,0,0,0,0,0,0,0,102.0,19.0,0.0,0.0),
            ms(2024,5,4,24,14,5,9,4,56,1765,160.0,72.0,11.4,10.2), ms(2024,5,5,15,0,4,9,4,32,1709,0.0,0.0,0.0,0.0), ms(2024,5,6,71,11,3,11,4,100,2228,88.0,20.0,0.0,0.0), ms(2024,5,7,64,10,4,11,4,93,2144,63.0,5.0,0.0,0.0),

            ms(2024,6,0,14,0,0,3,0,17,505,206.0,47.0,33.4,31.0,"1dCNGDvnsNqGgFt3uWRTWEPwVWR-FCOnO"),
            ms(2024,6,1,0,0,0,0,0,0,0,44.0,8.0,0.0,0.0), ms(2024,6,2,0,0,0,0,0,0,0,32.0,8.0,0.0,0.0), ms(2024,6,3,0,0,0,0,0,0,0,76.0,10.0,0.0,0.0),
            ms(2024,6,4,33,15,6,14,0,68,1433,131.0,23.0,11.6,10.3), ms(2024,6,5,22,0,2,3,0,27,811,0.0,0.0,0.0,0.0), ms(2024,6,6,77,16,2,18,0,113,1733,61.0,13.0,0.0,0.0), ms(2024,6,7,85,14,3,6,0,108,1469,36.0,3.0,0.0,0.0),

            ms(2024,7,0,15,1,1,7,0,24,1361,226.0,36.0,33.5,31.0,"1n-Nj5xj8M5upILr2tVxbSLF980ScmJt9"),
            ms(2024,7,1,0,0,0,0,0,0,0,43.0,20.0,0.0,0.0), ms(2024,7,2,0,0,0,0,0,0,0,31.0,8.0,0.0,0.0), ms(2024,7,3,0,0,0,0,0,0,0,74.0,11.0,0.0,0.0),
            ms(2024,7,4,36,12,2,8,0,58,1791,131.0,61.0,11.6,10.4), ms(2024,7,5,29,1,3,7,0,40,1754,0.0,0.0,0.0,0.0), ms(2024,7,6,72,10,1,11,0,94,1858,65.0,18.0,0.0,0.0), ms(2024,7,7,58,6,1,8,0,73,1664,36.0,7.0,0.0,0.0),

            ms(2024,8,0,14,0,3,9,0,26,2011,237.0,60.0,33.5,30.8,"1grs4RnWkl_K_FHohszXw9-l40JHwc-ss"),
            ms(2024,8,1,0,0,0,0,0,0,0,51.0,17.0,0.0,0.0), ms(2024,8,2,0,0,0,0,0,0,0,35.0,8.0,0.0,0.0), ms(2024,8,3,0,0,0,0,0,0,0,86.0,21.0,0.0,0.0),
            ms(2024,8,4,27,8,3,10,0,48,2187,147.0,47.0,11.6,10.0), ms(2024,8,5,15,0,3,9,0,27,2013,0.0,0.0,0.0,0.0), ms(2024,8,6,48,7,4,14,0,73,2443,70.0,25.0,0.0,0.0), ms(2024,8,7,54,6,5,10,0,75,2496,37.0,8.0,0.0,0.0),

            ms(2024,9,0,9,0,1,3,0,13,907,211.0,57.0,33.3,30.8,"1kHn7p5_MNZOJnKteRP7LdpZnYQf--NHd"),
            ms(2024,9,1,0,0,0,0,0,0,0,40.0,20.0,0.0,0.0), ms(2024,9,2,0,0,0,0,0,0,0,35.0,8.0,0.0,0.0), ms(2024,9,3,0,0,0,0,0,0,0,75.0,20.0,0.0,0.0),
            ms(2024,9,4,26,10,1,7,0,44,991,124.0,22.0,11.5,10.4), ms(2024,9,5,21,0,1,4,0,26,989,0.0,0.0,0.0,0.0), ms(2024,9,6,55,12,1,11,0,79,1547,66.0,16.0,0.0,0.0), ms(2024,9,7,44,7,1,5,0,57,1219,45.0,13.0,0.0,0.0),

            ms(2024,10,0,9,0,2,1,0,12,311,213.0,81.0,33.5,31.0,"1zLtwI1Y8xrzdGFbcYEQUWc0w9jb1tmEm"),
            ms(2024,10,1,0,0,0,0,0,0,0,43.0,15.0,0.0,0.0), ms(2024,10,2,0,0,0,0,0,0,0,33.0,8.0,0.0,0.0), ms(2024,10,3,0,0,0,0,0,0,0,76.0,15.0,0.0,0.0),
            ms(2024,10,4,23,2,2,1,0,28,423,124.0,45.0,11.6,10.0), ms(2024,10,5,16,0,2,1,0,19,334,0.0,0.0,0.0,0.0), ms(2024,10,6,45,3,3,3,0,54,672,65.0,18.0,0.0,0.0), ms(2024,10,7,43,2,2,1,0,48,415,45.0,14.0,0.0,0.0),

            ms(2024,11,0,6,0,2,1,0,9,1131,219.0,79.0,33.5,30.2,"1ePaGpmz8ytxO7jFEO62nfll5Gz7pOYT2"),
            ms(2024,11,1,0,0,0,0,0,0,0,40.0,13.0,0.0,0.0), ms(2024,11,2,0,0,0,0,0,0,0,34.0,11.0,0.0,0.0), ms(2024,11,3,0,0,0,0,0,0,0,74.0,24.0,0.0,0.0),
            ms(2024,11,4,13,2,1,1,0,26,1270,126.0,40.0,11.6,10.2), ms(2024,11,5,3,0,0,0,0,12,1140,0.0,0.0,0.0,0.0), ms(2024,11,6,34,8,1,1,0,53,1442,70.0,12.0,0.0,0.0), ms(2024,11,7,11,0,0,1,0,21,1217,44.0,11.0,0.0,0.0),

            // 2025
            ms(2025,0,0,5,0,1,0,0,6,143,185.0,63.0,33.4,30.6,"1eAUtDwWqTSMmbcGE_y3A0mNuFsvVKBq7"),
            ms(2025,0,1,0,0,0,0,0,0,0,31.0,14.0,0.0,0.0), ms(2025,0,2,0,0,0,0,0,0,0,31.0,8.0,0.0,0.0), ms(2025,0,3,0,0,0,0,0,0,0,62.0,19.0,0.0,0.0),
            ms(2025,0,4,14,7,1,0,0,22,274,98.0,43.0,11.6,10.4), ms(2025,0,5,5,0,1,0,0,6,143,0.0,0.0,0.0,0.0), ms(2025,0,6,19,7,1,2,0,29,323,61.0,23.0,0.0,0.0), ms(2025,0,7,12,3,1,0,0,16,201,35.0,11.0,0.0,0.0),

            ms(2025,1,0,4,0,0,2,0,6,331,220.0,89.0,33.4,30.5,"1kt7FjZZptGCr-HfsYrFr0g5jPYShjO1z"),
            ms(2025,1,1,0,0,0,0,0,0,0,41.0,14.0,0.0,0.0), ms(2025,1,2,0,0,0,0,0,0,0,35.0,8.0,0.0,0.0), ms(2025,1,3,0,0,0,0,0,0,0,76.0,19.0,0.0,0.0),
            ms(2025,1,4,15,4,1,2,0,22,474,119.0,45.0,11.5,10.3), ms(2025,1,5,5,0,0,2,0,7,336,0.0,0.0,0.0,0.0), ms(2025,1,6,45,7,0,4,0,56,692,70.0,15.0,0.0,0.0), ms(2025,1,7,15,5,0,3,0,23,441,41.0,8.0,0.0,0.0),

            ms(2025,2,0,5,0,2,0,1,8,611,231.0,87.0,33.7,29.3,"1OL27lxTfO4WNm5T3PeobEjmX1Nym6vN_"),
            ms(2025,2,1,0,0,0,0,0,0,0,42.0,6.0,0.0,0.0), ms(2025,2,2,0,0,0,0,0,0,0,38.0,15.0,0.0,0.0), ms(2025,2,3,0,0,0,0,0,0,0,80.0,21.0,0.0,0.0),
            ms(2025,2,4,27,6,3,0,1,37,839,120.0,19.0,11.6,9.8), ms(2025,2,5,5,0,2,0,1,8,611,0.0,0.0,0.0,0.0), ms(2025,2,6,24,2,2,0,1,29,737,77.0,33.0,0.0,0.0), ms(2025,2,7,14,4,2,0,1,21,678,37.0,16.0,0.0,0.0),

            ms(2025,3,0,5,0,2,1,1,9,1199,265.0,70.0,33.9,31.0,"1Y4_PV11zsYm7sHMe_D_9JCpkdEWzllqT"),
            ms(2025,3,1,0,0,0,0,0,0,0,48.0,25.0,0.0,0.0), ms(2025,3,2,0,0,0,0,0,0,0,41.0,8.0,0.0,0.0), ms(2025,3,3,0,0,0,0,0,0,0,89.0,22.0,0.0,0.0),
            ms(2025,3,4,13,6,2,2,1,24,1330,148.0,76.0,11.4,10.3), ms(2025,3,5,6,1,2,1,1,11,1210,0.0,0.0,0.0,0.0), ms(2025,3,6,30,5,2,4,6,47,1855,90.0,22.0,0.0,0.0), ms(2025,3,7,14,5,2,1,5,27,1658,40.0,15.0,0.0,0.0),

            ms(2025,4,0,6,0,5,11,0,22,3366,261.0,24.0,33.1,31.3,"1Q7TO4im3Gxb7lx3StA9FcdrD9dwGWAeM"),
            ms(2025,4,1,0,0,0,0,0,0,0,49.0,22.0,0.0,0.0), ms(2025,4,2,0,0,0,0,0,0,0,41.0,8.0,0.0,0.0), ms(2025,4,3,0,0,0,0,0,0,0,90.0,8.0,0.0,0.0),
            ms(2025,4,4,26,5,7,17,0,55,4376,158.0,65.0,11.6,10.1), ms(2025,4,5,7,1,5,11,0,24,3389,0.0,0.0,0.0,0.0), ms(2025,4,6,85,7,6,20,0,118,5121,81.0,8.0,0.0,0.0), ms(2025,4,7,38,2,5,13,0,58,3791,36.0,6.0,0.0,0.0),

            ms(2025,5,0,13,0,3,7,0,23,2365,233.0,59.0,34.0,31.5,"1rhQslUh7eteJ5wH7gKf9uCVMcSXIoCEL"),
            ms(2025,5,1,0,0,0,0,0,0,0,59.0,9.0,0.0,0.0), ms(2025,5,2,0,0,0,0,0,0,0,37.0,8.0,0.0,0.0), ms(2025,5,3,0,0,0,0,0,0,0,96.0,22.0,0.0,0.0),
            ms(2025,5,4,54,11,5,9,0,79,2856,168.0,67.0,11.7,10.5), ms(2025,5,5,15,0,5,7,0,27,2453,1.0,0.0,0.0,0.0), ms(2025,5,6,97,22,4,10,0,133,3250,71.0,14.0,0.0,0.0), ms(2025,5,7,65,3,3,14,0,85,2943,36.0,3.0,0.0,0.0),

            ms(2025,6,0,10,0,5,4,0,19,893,215.0,60.0,33.8,31.4,"1xo9OVZsVBQg6awjgyNfzHSb5XykAJFfb"),
            ms(2025,6,1,0,0,0,0,0,0,0,45.0,18.0,0.0,0.0), ms(2025,6,2,0,0,0,0,0,0,0,62.0,7.0,0.0,0.0), ms(2025,6,3,0,0,0,0,0,0,0,107.0,18.0,0.0,0.0),
            ms(2025,6,4,25,7,8,10,0,50,1357,136.0,59.0,11.8,10.4), ms(2025,6,5,11,0,5,4,0,20,897,0.0,0.0,0.0,0.0), ms(2025,6,6,66,15,5,7,0,93,1381,60.0,21.0,0.0,0.0), ms(2025,6,7,60,13,6,6,0,85,1372,33.0,5.0,0.0,0.0),

            ms(2025,7,0,4,0,3,1,0,8,596,221.0,27.0,34.0,31.1,"16Xu76gzVmKCnGSnhKXDEdVfM7F4gcjRC"),
            ms(2025,7,1,0,0,0,0,0,0,0,42.0,19.0,0.0,0.0), ms(2025,7,2,0,0,0,0,0,0,0,53.0,4.0,0.0,0.0), ms(2025,7,3,0,0,0,0,0,0,0,95.0,10.0,0.0,0.0),
            ms(2025,7,4,34,7,4,3,0,48,1250,131.0,60.0,11.6,10.5), ms(2025,7,5,6,0,3,1,0,10,614,0.0,0.0,0.0,0.0), ms(2025,7,6,97,21,4,4,0,126,1417,61.0,21.0,0.0,0.0), ms(2025,7,7,64,17,3,4,0,88,1217,30.0,2.0,0.0,0.0),

            ms(2025,8,0,18,0,3,10,0,31,2064,208.0,31.0,33.7,31.4,"1t_TWBQslnc3VuXW1mHnTrG4tlzb7aKV0"),
            ms(2025,8,1,0,0,0,0,0,0,0,47.0,18.0,0.0,0.0), ms(2025,8,2,0,0,0,0,0,0,0,29.0,8.0,0.0,0.0), ms(2025,8,3,0,0,0,0,0,0,0,76.0,8.0,0.0,0.0),
            ms(2025,8,4,36,9,5,10,0,60,2637,127.0,61.0,11.7,10.7), ms(2025,8,5,20,1,3,10,0,34,2092,0.0,0.0,0.0,0.0), ms(2025,8,6,81,10,5,16,0,112,2953,54.0,20.0,0.0,0.0), ms(2025,8,7,51,20,6,11,0,88,3559,40.0,4.0,0.0,0.0),

            ms(2025,9,0,13,0,4,1,0,18,665,243.0,61.0,33.7,31.0,"1IlBiSWvmtCOKudZBlHN5yRopHyA9eQxJ"),
            ms(2025,9,1,0,0,0,0,0,0,0,74.0,20.0,0.0,0.0), ms(2025,9,2,0,0,0,0,0,0,0,30.0,10.0,0.0,0.0), ms(2025,9,3,0,0,0,0,0,0,0,104.0,20.0,0.0,0.0),
            ms(2025,9,4,29,6,4,2,0,41,875,164.0,60.0,11.6,10.0), ms(2025,9,5,15,0,4,1,0,20,675,0.0,0.0,0.0,0.0), ms(2025,9,6,55,4,5,3,0,67,1084,61.0,20.0,0.0,0.0), ms(2025,9,7,67,7,8,8,0,90,2759,40.0,5.0,0.0,0.0),

            ms(2025,10,0,19,0,2,5,0,26,715,218.0,87.0,34.1,31.5,"1ZyIKIC45nH3BHI2eV2QhZEhryw6JJTg4"),
            ms(2025,10,1,0,0,0,0,0,0,0,41.0,15.0,0.0,0.0), ms(2025,10,2,0,0,0,0,0,0,0,37.0,8.0,0.0,0.0), ms(2025,10,3,0,0,0,0,0,0,0,78.0,22.0,0.0,0.0),
            ms(2025,10,4,43,6,3,5,0,57,1017,130.0,48.0,11.7,10.6), ms(2025,10,5,21,0,2,5,0,28,725,0.0,0.0,0.0,0.0), ms(2025,10,6,44,8,4,8,0,64,1026,57.0,13.0,0.0,0.0), ms(2025,10,7,38,6,3,6,0,53,910,60.0,12.0,0.0,0.0),

            ms(2025,11,0,3,0,1,2,0,6,684,229.0,45.0,34.1,31.7,"1gANrQUMJMGpIW1_vgfZmOPOWU8eOayuK"),
            ms(2025,11,1,0,0,0,0,0,0,0,44.0,10.0,0.0,0.0), ms(2025,11,2,0,0,0,0,0,0,0,43.0,8.0,0.0,0.0), ms(2025,11,3,0,0,0,0,0,0,0,87.0,14.0,0.0,0.0),
            ms(2025,11,4,15,4,3,5,0,27,1255,122.0,40.0,11.8,10.6), ms(2025,11,5,4,1,2,2,0,9,754,0.0,0.0,0.0,0.0), ms(2025,11,6,24,14,3,3,0,44,1118,72.0,8.0,0.0,0.0), ms(2025,11,7,11,6,3,2,0,22,911,62.0,10.0,0.0,0.0),

            // 2026
            ms(2026,0,0,4,0,1,2,0,7,545,211.0,34.0,34.2,31.6,"1fqF28udIOgTA0CPdsLBlgUxzRQCktxuJ"),
            ms(2026,0,1,0,0,0,0,0,0,0,36.0,12.0,0.0,0.0), ms(2026,0,2,0,0,0,0,0,0,0,43.0,12.0,0.0,0.0), ms(2026,0,3,0,0,0,0,0,0,0,79.0,12.0,0.0,0.0),
            ms(2026,0,4,14,4,1,3,0,22,697,107.0,46.0,11.9,10.2), ms(2026,0,5,7,1,1,2,0,11,560,1.0,0.0,0.0,0.0), ms(2026,0,6,18,6,1,3,0,28,747,65.0,19.0,0.0,0.0), ms(2026,0,7,8,2,1,2,0,13,585,68.0,11.0,0.0,0.0),

            ms(2026,1,0,5,1,0,0,0,6,43,218.0,60.0,33.9,31.2,"1myBdcWxDtTr4Hn62_t6PBCjj8bU-DV4i"),
            ms(2026,1,1,0,0,0,0,0,0,0,35.0,17.0,0.0,0.0), ms(2026,1,2,0,0,0,0,0,0,0,42.0,15.0,0.0,0.0), ms(2026,1,3,0,0,0,0,0,0,0,77.0,20.0,0.0,0.0),
            ms(2026,1,4,14,3,0,1,0,18,194,99.0,55.0,11.8,10.6), ms(2026,1,5,6,4,0,0,0,10,64,0.0,0.0,0.0,0.0), ms(2026,1,6,18,8,0,0,0,26,142,61.0,25.0,0.0,0.0), ms(2026,1,7,16,5,0,0,0,21,112,67.0,9.0,0.0,0.0),

            ms(2026,2,0,1,0,1,1,0,3,508,240.0,59.0,33.7,31.0,"1G3c1QxSonOOdM8VX_PEURfcAx9FbV37l"),
            ms(2026,2,1,0,0,0,0,0,0,0,44.0,6.0,0.0,0.0), ms(2026,2,2,0,0,0,0,0,0,0,44.0,17.0,0.0,0.0), ms(2026,2,3,0,0,0,0,0,0,0,88.0,20.0,0.0,0.0),
            ms(2026,2,4,24,6,1,3,0,34,792,140.0,19.0,11.7,10.0), ms(2026,2,5,3,8,1,1,0,13,541,0.0,0.0,0.0,0.0), ms(2026,2,6,25,9,1,5,0,40,849,65.0,19.0,0.0,0.0), ms(2026,2,7,12,5,1,1,0,19,621,72.0,16.0,0.0,0.0),

            ms(2026,3,0,2,0,3,1,0,6,966,262.0,89.0,33.7,31.2,"1VwrBZaDUnrAtvjFTnt63T40yDuGRORfI"),
            ms(2026,3,1,0,0,0,0,0,0,0,46.0,21.0,0.0,0.0), ms(2026,3,2,0,0,0,0,0,0,0,42.0,4.0,0.0,0.0), ms(2026,3,3,0,0,0,0,0,0,0,88.0,29.0,0.0,0.0),
            ms(2026,3,4,10,3,4,1,0,18,1396,139.0,68.0,11.6,10.3), ms(2026,3,5,2,1,5,1,0,9,1529,0.0,0.0,0.0,0.0), ms(2026,3,6,22,11,4,3,0,40,1575,70.0,24.0,0.0,0.0), ms(2026,3,7,10,7,4,1,0,22,1426,64.0,10.0,0.0,0.0),

            ms(2026,4,0,7,0,2,1,0,10,387,283.0,71.0,33.7,31.5,"1-T2p90MO238AdP1Y1zywoIum7zbPs7g5"),
            ms(2026,4,1,0,0,0,0,0,0,0,54.0,22.0,0.0,0.0), ms(2026,4,2,0,0,0,0,0,0,0,45.0,10.0,0.0,0.0), ms(2026,4,3,0,0,0,0,0,0,0,99.0,23.0,0.0,0.0),
            ms(2026,4,4,25,4,5,3,0,37,1152,164.0,70.0,11.7,10.5), ms(2026,4,5,9,0,3,1,0,13,569,7.0,0.0,0.0,0.0), ms(2026,4,6,25,7,4,1,0,37,969,73.0,4.0,0.0,0.0), ms(2026,4,7,16,1,4,1,0,22,1034,66.0,7.0,0.0,0.0),

            ms(2026,5,0,12,1,5,1,0,19,786,298.0,81.0,33.9,31.1,"1bqLYS2raYxjC_X1NCshMtglg8gC1v8lp"),
            ms(2026,5,1,0,0,0,0,0,0,0,54.0,8.0,0.0,0.0), ms(2026,5,2,0,0,0,0,0,0,0,46.0,7.0,0.0,0.0), ms(2026,5,3,0,0,0,0,0,0,0,100.0,27.0,0.0,0.0),
            ms(2026,5,4,27,6,6,1,0,40,1123,165.0,29.0,11.8,10.5), ms(2026,5,5,14,7,5,1,0,27,834,7.0,0.0,0.0,0.0), ms(2026,5,6,68,12,5,11,0,96,1592,72.0,3.0,0.0,0.0), ms(2026,5,7,30,11,6,2,0,49,1266,71.0,7.0,0.0,0.0),

            ms(2026,6,0,6,0,5,9,0,20,1677,268.0,65.0,34.1,31.2,"1YBO6NTrzkZnxvivioRg5eZzD9PSmxM2d"),
            ms(2026,6,1,0,0,0,0,0,0,0,48.0,14.0,0.0,0.0), ms(2026,6,2,0,0,0,0,0,0,0,40.0,3.0,0.0,0.0), ms(2026,6,3,0,0,0,0,0,0,0,88.0,21.0,0.0,0.0),
            ms(2026,6,4,38,11,5,15,0,69,2459,147.0,58.0,11.9,10.7), ms(2026,6,5,6,17,5,9,0,37,1812,2.0,0.0,0.0,0.0), ms(2026,6,6,122,19,6,23,0,170,3344,61.0,2.0,0.0,0.0), ms(2026,6,7,44,34,5,15,0,98,2586,68.0,5.0,0.0,0.0),

            ms(2026,7,0,7,0,1,1,0,9,461,248.0,85.0,34.0,31.5,"1mo-OKB_rRV52d9gppGIEeWnPskUnsTow"),
            ms(2026,7,1,0,0,0,0,0,0,0,51.0,20.0,0.0,0.0), ms(2026,7,2,0,0,0,0,0,0,0,37.0,7.0,0.0,0.0), ms(2026,7,3,0,0,0,0,0,0,0,82.0,28.0,0.0,0.0),
            ms(2026,7,4,21,4,3,1,0,29,591,157.0,42.0,11.7,10.5), ms(2026,7,5,7,10,1,1,0,19,492,2.0,0.0,0.0,0.0), ms(2026,7,6,70,13,2,7,0,92,950,63.0,4.0,0.0,0.0), ms(2026,7,7,25,12,4,1,0,42,904,67.0,4.0,0.0,0.0),

            ms(2026,8,0,9,0,2,1,0,12,469,239.0,112.0,34.0,31.5,"1zJubHX6HQU2XWHMbwLn_53TkPws1Ymnu"),
            ms(2026,8,1,0,0,0,0,0,0,0,46.0,20.0,0.0,0.0), ms(2026,8,2,0,0,0,0,0,0,0,43.0,9.0,0.0,0.0), ms(2026,8,3,0,0,0,0,0,0,0,80.0,34.0,0.0,0.0),
            ms(2026,8,4,25,2,2,1,0,30,563,136.0,52.0,11.7,10.7), ms(2026,8,5,9,8,2,1,0,20,501,1.0,0.0,0.0,0.0), ms(2026,8,6,40,5,3,5,0,53,610,60.0,2.0,0.0,0.0), ms(2026,8,7,12,8,2,1,0,23,516,62.0,13.0,0.0,0.0),

            ms(2026,9,0,8,0,0,0,0,8,74,240.0,59.0,33.8,31.0,"1AHjMo_VvADBqHxFau0f2ubgScpe-L1pT"),
            ms(2026,9,1,0,0,0,0,0,0,0,44.0,7.0,0.0,0.0), ms(2026,9,2,0,0,0,0,0,0,0,43.0,9.0,0.0,0.0), ms(2026,9,3,0,0,0,0,0,0,0,80.0,20.0,0.0,0.0),
            ms(2026,9,4,24,9,1,1,0,35,398,130.0,23.0,11.7,10.0), ms(2026,9,5,8,6,0,0,0,14,92,1.0,0.0,0.0,0.0), ms(2026,9,6,52,7,1,4,0,64,308,68.0,26.0,0.0,0.0), ms(2026,9,7,16,8,0,0,0,24,147,69.0,3.0,0.0,0.0)
        )
    }

    private fun ctDoc(
        id: String, bay: String, name: String, ratio: String, mf: String, status: String,
        make: String, yr: String, rSn: String, ySn: String, bSn: String, met: String, prot: String
    ) = NameplateDoc(
        id = id, category = "cts", bay = bay, name = name, status = status, make = make, serialNo = rSn, year = yr,
        specs = mapOf(
            "connectedRatio" to ratio, "mf" to mf,
            "r_make" to make, "r_serial" to rSn, "r_year" to yr, "r_met" to met, "r_prot" to prot,
            "y_make" to make, "y_serial" to ySn, "y_year" to yr, "y_met" to met, "y_prot" to prot,
            "b_make" to make, "b_serial" to bSn, "b_year" to yr, "b_met" to met, "b_prot" to prot
        )
    )

    private fun ptDoc(
        id: String, bay: String, name: String, vRatio: String, make: String, yr: String,
        rSn: String, ySn: String, bSn: String, met: String
    ) = NameplateDoc(
        id = id, category = "pts", bay = bay, name = name, status = "In Service (ON)", make = make, serialNo = rSn, year = yr,
        specs = mapOf(
            "voltageRatio" to vRatio, "voltageFactor" to "1.2 Cont. / 1.9 for 30s",
            "r_make" to make, "r_serial" to rSn, "r_year" to yr, "r_met" to met, "r_prot" to "3P / 50 VA",
            "y_make" to make, "y_serial" to ySn, "y_year" to yr, "y_met" to met, "y_prot" to "3P / 50 VA",
            "b_make" to make, "b_serial" to bSn, "b_year" to yr, "b_met" to met, "b_prot" to "3P / 50 VA"
        )
    )

    val DEFAULT_NAMEPLATES: List<NameplateDoc> by lazy {
        listOf(
            // Transformers
            NameplateDoc(
                id = "tf_1", category = "transformers", bay = "Power T/F Bay-1 (Left)", name = "5 MVA Power Transformer No. 1",
                status = "In Service (ON)", make = "Crompton Greaves Ltd. (CGL)", serialNo = "T-5MVA-284619-01", year = "2018",
                specs = mapOf(
                    "capacity" to "5 MVA (5000 kVA)", "voltageRatio" to "33 / 11 kV", "ratedCurrent" to "HV: 87.48 A | LV: 262.43 A",
                    "impedance" to "7.15 %", "vectorGroup" to "Dyn11", "cooling" to "ONAN", "tapChanger" to "OCTC (+5% to -10% in 7 Steps)",
                    "oilQty" to "3250 Litres", "coreWeight" to "5400 kg", "totalWeight" to "11200 kg",
                    "notes" to "HV Meter S/N: X1095238 (MF 900, Relay 30%) | LV Meter S/N: XD596070 (MF 80, Relay 65%)."
                )
            ),
            NameplateDoc(
                id = "tf_2", category = "transformers", bay = "Power T/F Bay-2 (Right)", name = "5 MVA Power Transformer No. 2",
                status = "In Service (ON)", make = "Bharat Bijlee Ltd.", serialNo = "BB-5MVA-284619-02", year = "2019",
                specs = mapOf(
                    "capacity" to "5 MVA (5000 kVA)", "voltageRatio" to "33 / 11 kV", "ratedCurrent" to "HV: 87.48 A | LV: 262.43 A",
                    "impedance" to "7.18 %", "vectorGroup" to "Dyn11", "cooling" to "ONAN", "tapChanger" to "OCTC (+5% to -10% in 7 Steps)",
                    "oilQty" to "3300 Litres", "coreWeight" to "5480 kg", "totalWeight" to "11350 kg",
                    "notes" to "HV Meter S/N: X1095384 (MF 600, Relay 44%) | LV Meter S/N: X0400243 (MF 60, Relay 84%)."
                )
            ),
            NameplateDoc(
                id = "tf_dtc", category = "transformers", bay = "11kV LV-1 Auxiliary", name = "63 KVA Station Transformer (DTC)",
                status = "In Service (ON)", make = "MSEDCL Approved Make", serialNo = "DTC-63-284619", year = "2018",
                specs = mapOf(
                    "capacity" to "63 kVA", "voltageRatio" to "11 kV / 433 V", "ratedCurrent" to "HV: 3.31 A | LV: 84.0 A",
                    "impedance" to "4.50 %", "vectorGroup" to "Dyn11", "cooling" to "ONAN", "tapChanger" to "Off-Circuit (+5% to -5%)",
                    "oilQty" to "145 Litres", "coreWeight" to "210 kg", "totalWeight" to "465 kg",
                    "notes" to "11kV बसवरून प्रथम 11kV Isolator ➔ D.O. Fuse Set ➔ 63 KVA DTC अर्थिंगसह."
                )
            ),
            // CTs (11)
            ctDoc("ct_33main", "33kV सफाळे मेन इनकमर", "33 kV Saphale Main Incomer C.T.", "200 / 1 A", "600", "In Service (ON)", "Pragati Electricals", "2018", "CT33-INC-R-101", "CT33-INC-Y-102", "CT33-INC-B-103", "Class 0.2s / 15 VA", "5P20 / 15 VA"),
            ctDoc("ct_tf1", "33kV T/F-1 HV Bay", "T/F No. 1 HV Side C.T.", "300 / 1 A", "900", "In Service (ON)", "Pragati Electricals", "2018", "CT33-TF1-R-201", "CT33-TF1-Y-202", "CT33-TF1-B-203", "Class 0.5 / 15 VA", "5P20 / 15 VA"),
            ctDoc("ct_tf2", "33kV T/F-2 HV Bay", "T/F No. 2 HV Side C.T.", "200 / 1 A", "600", "In Service (ON)", "Pragati Electricals", "2019", "CT33-TF2-R-301", "CT33-TF2-Y-302", "CT33-TF2-B-303", "Class 0.5 / 15 VA", "5P20 / 15 VA"),
            ctDoc("ct_lv1", "11kV Incomer LV-1", "11 kV Incomer LV-1 C.T.", "400 / 5 A", "80", "In Service (ON)", "Kappa Electricals", "2018", "CT11-LV1-R-401", "CT11-LV1-Y-402", "CT11-LV1-B-403", "Class 0.5 / 15 VA", "5P20 / 15 VA"),
            ctDoc("ct_lv2", "11kV Incomer LV-2", "11 kV Incomer LV-2 C.T.", "300 / 5 A", "60", "In Service (ON)", "Kappa Electricals", "2019", "CT11-LV2-R-501", "CT11-LV2-Y-502", "CT11-LV2-B-503", "Class 0.5 / 15 VA", "5P20 / 15 VA"),
            ctDoc("ct_205", "Bay 205 (11kV सफाळा)", "11 kV सफाळा फिडर (205) C.T.", "200 / 5 A", "40", "In Service (ON)", "Pragati Electricals", "2018", "CT205-R-01", "CT205-Y-02", "CT205-B-03", "Class 0.5 / 15 VA", "5P10 / 15 VA"),
            ctDoc("ct_204", "Bay 204 (11kV DFCCIL)", "11 kV DFCCIL Railway Express (204) C.T.", "200 / 5 A", "40", "In Service (ON)", "Pragati Electricals", "2020", "CT204-R-01", "CT204-Y-02", "CT204-B-03", "Class 0.2s / 15 VA", "5P20 / 15 VA"),
            ctDoc("ct_203", "Bay 203 (11kV नवघर)", "11 kV नवघर फिडर (203) C.T.", "200 / 5 A", "40", "In Service (ON)", "Pragati Electricals", "2018", "CT203-R-01", "CT203-Y-02", "CT203-B-03", "Class 0.5 / 15 VA", "5P10 / 15 VA"),
            ctDoc("ct_201", "Bay 201 (11kV पारगाव)", "11 kV पारगाव फिडर (201) C.T.", "200 / 5 A", "40", "In Service (ON)", "Pragati Electricals", "2018", "CT201-R-01", "CT201-Y-02", "CT201-B-03", "Class 0.5 / 15 VA", "5P10 / 15 VA"),
            ctDoc("ct_206", "Bay 206 (11kV SPARE)", "11 kV SPARE बे (206) C.T.", "200 / 5 A", "40", "Spare / Standby", "Pragati Electricals", "2019", "CT206-R-01", "CT206-Y-02", "CT206-B-03", "Class 0.5 / 15 VA", "5P10 / 15 VA"),
            ctDoc("ct_202", "Bay 202 (11kV SPARE)", "11 kV SPARE बे (202) C.T.", "200 / 5 A", "40", "Spare / Standby", "Pragati Electricals", "2019", "CT202-R-01", "CT202-Y-02", "CT202-B-03", "Class 0.5 / 15 VA", "5P10 / 15 VA"),
            // PTs (4)
            ptDoc("pt_33main", "33kV सफाळे मेन इनकमर", "33 kV Saphale Main Incomer P.T.", "33 kV / √3 : 110 V / √3", "Pragati Electricals", "2018", "PT33-INC-R-11", "PT33-INC-Y-12", "PT33-INC-B-13", "Class 0.2 / 50 VA"),
            ptDoc("pt_33bus", "33kV Main Bus (With G.O.D.)", "33 kV Bus Common P.T. (T/F-1 व 2 साठी)", "33 kV / √3 : 110 V / √3", "Pragati Electricals", "2018", "PT33-BUS-R-21", "PT33-BUS-Y-22", "PT33-BUS-B-23", "Class 0.5 / 50 VA"),
            ptDoc("pt_11bus1", "11kV Bus Section-1 (LV-1)", "11 kV Common P.T. Set-1", "11 kV / √3 : 110 V / √3", "Kappa Electricals", "2018", "PT11-S1-R-31", "PT11-S1-Y-32", "PT11-S1-B-33", "Class 0.5 / 50 VA"),
            ptDoc("pt_11bus2", "11kV Bus Section-2 (LV-2)", "11 kV Common P.T. Set-2", "11 kV / √3 : 110 V / √3", "Kappa Electricals", "2019", "PT11-S2-R-41", "PT11-S2-Y-42", "PT11-S2-B-43", "Class 0.5 / 50 VA"),
            // VCBs (10)
            NameplateDoc(id = "vcb_33_1", category = "vcbs", bay = "33kV T/F-1 HV", name = "33 kV Outdoor VCB (T/F No. 1)", status = "In Service (ON)", make = "ABB India", serialNo = "ABB-33V-01", year = "2018", specs = mapOf("ratedKv" to "36 kV", "ratedAmp" to "1250 A", "breakingKa" to "25 kA for 3s", "closingCoil" to "110 V DC", "tripCoil" to "110 V DC", "motorVolt" to "110 V DC / 230 V AC")),
            NameplateDoc(id = "vcb_33_2", category = "vcbs", bay = "33kV T/F-2 HV", name = "33 kV Outdoor VCB (T/F No. 2)", status = "In Service (ON)", make = "ABB India", serialNo = "ABB-33V-02", year = "2019", specs = mapOf("ratedKv" to "36 kV", "ratedAmp" to "1250 A", "breakingKa" to "25 kA for 3s", "closingCoil" to "110 V DC", "tripCoil" to "110 V DC", "motorVolt" to "110 V DC / 230 V AC")),
            NameplateDoc(id = "vcb_lv1", category = "vcbs", bay = "11kV Incomer LV-1", name = "11 kV VCB Breaker (LV-1)", status = "In Service (ON)", make = "Siemens", serialNo = "SIE-11V-LV1", year = "2018", specs = mapOf("ratedKv" to "12 kV", "ratedAmp" to "1250 A", "breakingKa" to "25 kA for 3s", "closingCoil" to "110 V DC", "tripCoil" to "110 V DC", "motorVolt" to "110 V DC")),
            NameplateDoc(id = "vcb_lv2", category = "vcbs", bay = "11kV Incomer LV-2", name = "11 kV VCB Breaker (LV-2)", status = "In Service (ON)", make = "Siemens", serialNo = "SIE-11V-LV2", year = "2019", specs = mapOf("ratedKv" to "12 kV", "ratedAmp" to "1250 A", "breakingKa" to "25 kA for 3s", "closingCoil" to "110 V DC", "tripCoil" to "110 V DC", "motorVolt" to "110 V DC")),
            NameplateDoc(id = "vcb_205", category = "vcbs", bay = "Bay 205 (सफाळा)", name = "11 kV Feeder VCB (Bay 205)", status = "In Service (ON)", make = "Siemens", serialNo = "SIE-11V-205", year = "2018", specs = mapOf("ratedKv" to "12 kV", "ratedAmp" to "800 A", "breakingKa" to "25 kA for 3s", "closingCoil" to "110 V DC", "tripCoil" to "110 V DC", "motorVolt" to "110 V DC")),
            NameplateDoc(id = "vcb_204", category = "vcbs", bay = "Bay 204 (DFCCIL)", name = "11 kV Feeder VCB (Bay 204)", status = "In Service (ON)", make = "ABB India", serialNo = "ABB-11V-204", year = "2020", specs = mapOf("ratedKv" to "12 kV", "ratedAmp" to "800 A", "breakingKa" to "25 kA for 3s", "closingCoil" to "110 V DC", "tripCoil" to "110 V DC", "motorVolt" to "110 V DC")),
            NameplateDoc(id = "vcb_203", category = "vcbs", bay = "Bay 203 (नवघर)", name = "11 kV Feeder VCB (Bay 203)", status = "In Service (ON)", make = "Siemens", serialNo = "SIE-11V-203", year = "2018", specs = mapOf("ratedKv" to "12 kV", "ratedAmp" to "800 A", "breakingKa" to "25 kA for 3s", "closingCoil" to "110 V DC", "tripCoil" to "110 V DC", "motorVolt" to "110 V DC")),
            NameplateDoc(id = "vcb_201", category = "vcbs", bay = "Bay 201 (पारगाव)", name = "11 kV Feeder VCB (Bay 201)", status = "In Service (ON)", make = "Siemens", serialNo = "SIE-11V-201", year = "2018", specs = mapOf("ratedKv" to "12 kV", "ratedAmp" to "800 A", "breakingKa" to "25 kA for 3s", "closingCoil" to "110 V DC", "tripCoil" to "110 V DC", "motorVolt" to "110 V DC")),
            NameplateDoc(id = "vcb_206", category = "vcbs", bay = "Bay 206 (Spare)", name = "11 kV Spare VCB (Bay 206)", status = "Spare / Standby", make = "ABB India", serialNo = "ABB-11V-206", year = "2019", specs = mapOf("ratedKv" to "12 kV", "ratedAmp" to "800 A", "breakingKa" to "25 kA for 3s", "closingCoil" to "110 V DC", "tripCoil" to "110 V DC", "motorVolt" to "110 V DC")),
            NameplateDoc(id = "vcb_202", category = "vcbs", bay = "Bay 202 (Spare)", name = "11 kV Spare VCB (Bay 202)", status = "Spare / Standby", make = "ABB India", serialNo = "ABB-11V-202", year = "2019", specs = mapOf("ratedKv" to "12 kV", "ratedAmp" to "800 A", "breakingKa" to "25 kA for 3s", "closingCoil" to "110 V DC", "tripCoil" to "110 V DC", "motorVolt" to "110 V DC")),
            // SECURE Meters & Aux (11)
            NameplateDoc(id = "m_33inc", category = "aux", bay = "33 KV INCOMMER", name = "SECURE ABT Meter — 33 KV INCOMMER", status = "In Service (ON)", make = "SECURE", serialNo = "M32450021966", year = "2020", specs = mapOf("spec" to "CT Ratio: 200/1 A | MF: 600 | PT: 33kV/110V", "notes" to "मुख्य 33 kV इनकमर बिलींग व एनर्जी मीटर.")),
            NameplateDoc(id = "m_tf1hv", category = "aux", bay = "T/F NO. 1 H.V. SIDE", name = "SECURE ABT Meter — T/F NO. 1 H.V. SIDE", status = "In Service (ON)", make = "SECURE", serialNo = "X1095238", year = "2018", specs = mapOf("spec" to "CT Ratio: 300/1 A | MF: 900 | Relay Setting: 30%", "notes" to "पॉवर ट्रान्सफॉर्मर नं. 1 HV मीटर व रिले सेटिंग.")),
            NameplateDoc(id = "m_tf2hv", category = "aux", bay = "T/F NO. 2 H.V. SIDE", name = "SECURE ABT Meter — T/F NO. 2 H.V. SIDE", status = "In Service (ON)", make = "SECURE", serialNo = "X1095384", year = "2019", specs = mapOf("spec" to "CT Ratio: 200/1 A | MF: 600 | Relay Setting: 44%", "notes" to "पॉवर ट्रान्सफॉर्मर नं. 2 HV मीटर व रिले सेटिंग.")),
            NameplateDoc(id = "m_tf1lv", category = "aux", bay = "T/F NO. 1 L.V. SIDE", name = "SECURE Meter — T/F NO. 1 L.V. SIDE", status = "In Service (ON)", make = "SECURE", serialNo = "XD596070", year = "2018", specs = mapOf("spec" to "CT Ratio: 400/5 A | MF: 80 | Relay Setting: 65%", "notes" to "11 kV इनकमर LV-1 मीटर.")),
            NameplateDoc(id = "m_tf2lv", category = "aux", bay = "T/F NO. 2 L.V. SIDE", name = "SECURE Meter — T/F NO. 2 L.V. SIDE", status = "In Service (ON)", make = "SECURE", serialNo = "X0400243", year = "2019", specs = mapOf("spec" to "CT Ratio: 300/5 A | MF: 60 | Relay Setting: 84%", "notes" to "11 kV इनकमर LV-2 मीटर.")),
            NameplateDoc(id = "m_205", category = "aux", bay = "Bay 205 (11 KV SAPHALA)", name = "SECURE Meter — 11 KV SAPHALA FDR (205)", status = "In Service (ON)", make = "SECURE", serialNo = "M32450003003", year = "2020", specs = mapOf("spec" to "CT Ratio: 200/5 A | MF: 40 | Relay Setting: 100%", "notes" to "सफाळा फिडर बे 205.")),
            NameplateDoc(id = "m_204", category = "aux", bay = "Bay 204 (11 KV D.F.C.C.)", name = "SECURE Meter — 11 KV D.F.C.C. RAILWAY EXPRESS (204)", status = "In Service (ON)", make = "SECURE", serialNo = "M32450003452", year = "2020", specs = mapOf("spec" to "CT Ratio: 200/5 A | MF: 40 | Relay Setting: 15%", "notes" to "रेल्वे एक्सप्रेस फिडर बे 204.")),
            NameplateDoc(id = "m_203", category = "aux", bay = "Bay 203 (11 KV NAVGHAR)", name = "SECURE Meter — 11 KV NAVGHAR FDR (203)", status = "In Service (ON)", make = "SECURE", serialNo = "M32450003001", year = "2020", specs = mapOf("spec" to "CT Ratio: 200/5 A | MF: 40 | Relay Setting: 45%", "notes" to "नवघर फिडर बे 203.")),
            NameplateDoc(id = "m_201", category = "aux", bay = "Bay 201 (11 KV PARGAON)", name = "SECURE Meter — 11 KV PARGAON FDR (201)", status = "In Service (ON)", make = "SECURE", serialNo = "M32450003002", year = "2020", specs = mapOf("spec" to "CT Ratio: 200/5 A | MF: 40 | Relay Setting: 60%", "notes" to "पारगाव फिडर बे 201.")),
            NameplateDoc(id = "aux_bat", category = "aux", bay = "Battery Room", name = "110V DC Battery Bank & Charger (55 Cells)", status = "In Service (ON)", make = "Exide / Amar Raja", serialNo = "EX-110V-100AH", year = "2021", specs = mapOf("spec" to "110 V DC, 100 Ah (55 Cells × 2V), Float-cum-Boost Charger", "notes" to "ब्रेकर ट्रिपिंग, क्लोजिंग व पॅनल इंडिकेशनसाठी 110V DC सप्लाय.")),
            NameplateDoc(id = "aux_cap", category = "aux", bay = "11kV Bus Left & Right", name = "11 kV Capacitor Banks (Left LV-1 & Right LV-2)", status = "In Service (ON)", make = "Meher / Epcos", serialNo = "CAP-11KV-2X600", year = "2019", specs = mapOf("spec" to "2 × 600 kVAr, 11 kV, Isolator व Switch सह", "notes" to "पॉवर फॅक्टर सुधारणेसाठी कार्यरत."))
        )
    }

    // ==================== CALCULATION ENGINES ====================
    fun calcDurationMinutes(offTime: String, onTime: String): Long {
        val p1 = offTime.split(":")
        val p2 = onTime.split(":")
        if (p1.size < 2 || p2.size < 2) return 0L
        val h1 = p1[0].toIntOrNull() ?: return 0L
        val m1 = p1[1].toIntOrNull() ?: return 0L
        val h2 = p2[0].toIntOrNull() ?: return 0L
        val m2 = p2[1].toIntOrNull() ?: return 0L
        var diff = (h2 * 60 + m2) - (h1 * 60 + m1)
        if (diff < 0) diff += 1440
        return diff.toLong()
    }

    fun formatDurationMr(minutes: Long): String {
        val hrs = minutes / 60
        val mins = minutes % 60
        return if (hrs > 0) "$hrs तास $mins मि." else "$mins मिनिटे"
    }

    fun fmtIndian(value: Double?, decimals: Int = 0): String {
        if (value == null) return "—"
        val pattern = if (decimals <= 0) "#,##0" else "#,##0." + "0".repeat(decimals)
        return DecimalFormat(pattern).format(value)
    }

    fun formatDmy(ymd: String): String {
        val p = ymd.split("-")
        return if (p.size == 3) "${p[2]}/${p[1]}/${p[0]}" else ymd
    }

    fun calculateLossSet(c: Map<String, Double?>): DailyLossSummary {
        fun g(id: String) = c[id]
        fun need(vararg ids: String) = ids.all { g(it) != null }
        val t1Out = if (need("saphala", "dfcc", "sst")) g("saphala")!! + g("dfcc")!! + g("sst")!! else null
        val t2Out = if (need("pargaon", "navghar")) g("pargaon")!! + g("navghar")!! else null
        val totalHv = if (need("hv1", "hv2")) g("hv1")!! + g("hv2")!! else null
        val totalLv = if (need("lv1", "lv2")) g("lv1")!! + g("lv2")!! else null
        val totalOg = if (t1Out != null && t2Out != null) t1Out + t2Out else null

        fun row(label: String, a: Double?, b: Double?): LossPairRow {
            val diff = if (a != null && b != null) a - b else null
            val pct = if (a != null && b != null && a != 0.0) ((a - b) / a) * 100.0 else null
            return LossPairRow(label, diff, pct)
        }

        val lossRows = listOf(
            row("H.V. VS L.V. SIDE (T/F 1)", g("hv1"), g("lv1")),
            row("H.V. VS O/G FDR (T/F 1)", g("hv1"), t1Out),
            row("L.V. VS O/G FDR (T/F 1)", g("lv1"), t1Out),
            row("H.V. VS L.V. SIDE (T/F 2)", g("hv2"), g("lv2")),
            row("H.V. VS O/G FDR (T/F 2)", g("hv2"), t2Out),
            row("L.V. VS O/G FDR (T/F 2)", g("lv2"), t2Out),
            row("INC. VS H.V. SIDE", g("inc"), totalHv),
            row("INC. VS O/G FDR", g("inc"), totalOg),
            row("INC. VS L.V. SIDE", g("inc"), totalLv)
        )
        return DailyLossSummary(emptyList(), c, t1Out, t2Out, totalHv, totalLv, totalOg, lossRows)
    }

    fun calculateDailyLoss(
        date: String,
        allDays: Map<String, DailyLossDayDoc>,
        mfOverrides: Map<String, Double>
    ): DailyLossSummary {
        val sortedDates = allDays.keys.sorted()
        val currentDoc = allDays[date]
        val rows = DAILY_LOSS_METERS.map { m ->
            val mf = mfOverrides[m.id] ?: m.defaultMf
            val manualInit = currentDoc?.initialOverrides?.get(m.id)
            val prevFinal = if (manualInit != null) null else {
                sortedDates.lastOrNull { it < date && allDays[it]?.finalReadings?.get(m.id) != null }
                    ?.let { allDays[it]?.finalReadings?.get(m.id) }
            }
            val initVal = manualInit ?: prevFinal
            val finalVal = currentDoc?.finalReadings?.get(m.id)
            val diff = if (finalVal != null && initVal != null) finalVal - initVal else null
            val cons = diff?.let { it * mf }
            DailyLossRowCalc(
                meter = m,
                initialVal = initVal,
                manualInitial = manualInit != null,
                finalVal = finalVal,
                diff = diff,
                mf = mf,
                consumption = cons
            )
        }
        val cMap = rows.associate { it.meter.id to it.consumption }
        return calculateLossSet(cMap).copy(rows = rows)
    }

    fun computeMinMaxMonthStats(
        yearMonth: String,
        allMinMax: Map<String, MinMaxDayDoc>
    ): Map<String, MinMaxMonthStat> {
        val result = MINMAX_ITEMS.associate { it.id to MinMaxMonthStat() }.toMutableMap()
        allMinMax.keys.filter { it.startsWith(yearMonth) }.sorted().forEach { dateKey ->
            val dayEntries = allMinMax[dateKey]?.entries ?: return@forEach
            MINMAX_ITEMS.forEach { item ->
                val map = dayEntries[item.id] ?: return@forEach
                val mx = map["mx"]?.toDoubleOrNull()
                val mn = map["mn"]?.toDoubleOrNull()
                var cur = result[item.id] ?: MinMaxMonthStat()
                if (mx != null && (cur.maxVal == null || mx > cur.maxVal)) {
                    cur = cur.copy(maxVal = mx, maxDate = dateKey, maxTime = map["mxT"] ?: "")
                }
                if (mn != null && (cur.minVal == null || mn < cur.minVal)) {
                    cur = cur.copy(minVal = mn, minDate = dateKey, minTime = map["mnT"] ?: "")
                }
                result[item.id] = cur
            }
        }
        return result
    }

    fun matchTripFeederKey(feederName: String): String? {
        val s = feederName.uppercase(Locale.ROOT)
        return when {
            Regex("L\\.?V\\.?\\s*NO\\.?\\s*1|LV\\s*-?1").containsMatchIn(s) -> "lv1"
            Regex("L\\.?V\\.?\\s*NO\\.?\\s*2|LV\\s*-?2").containsMatchIn(s) -> "lv2"
            s.contains("INCOM") && s.contains("33") -> "inc33"
            Regex("T/F\\s*NO\\.?\\s*1").containsMatchIn(s) -> "tf1"
            Regex("T/F\\s*NO\\.?\\s*2").containsMatchIn(s) -> "tf2"
            s.contains("SAPHALA") || s.contains("SAFALA") -> "saphala"
            s.contains("DFCC") -> "dfcc"
            s.contains("PARGAON") -> "pargaon"
            s.contains("NAVGHAR") -> "navghar"
            else -> null
        }
    }

    fun computeMonthlyInterruptions(
        yearMonth: String,
        trips: List<TripEntry>
    ): Map<String, Map<String, InterruptionCount>> {
        val keys = listOf("inc33", "tf1", "tf2", "lv1", "lv2", "saphala", "dfcc", "pargaon", "navghar")
        val types = listOf("TP", "HT", "SD", "BD", "LS", "PL")
        val out = keys.associateWith { types.associateWith { InterruptionCount() }.toMutableMap() }
        trips.filter { it.date.startsWith(yearMonth) }.forEach { t ->
            val fk = matchTripFeederKey(t.feeder) ?: return@forEach
            val code = t.type.take(2).uppercase(Locale.ROOT)
            val bucket = out[fk]?.get(code) ?: return@forEach
            bucket.count += 1
            bucket.minutes += t.durationMin.toInt()
        }
        return out
    }
}

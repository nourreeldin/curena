package com.fueians.medicationapp.model.services

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.fueians.medicationapp.model.entities.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.FileOutputStream

class ReportGenerationService(private val context: Context) {

    // ---------------------------------------------------
    // Main: Generate Report
    // ---------------------------------------------------
    fun generateReport(
        startDate: Date,
        endDate: Date,
        type: ReportType,
        logs: List<AdherenceLogEntity>,
        totalMedications: Int
    ): ReportEntity {
        val adherenceRate = calculateAdherenceRate(logs)
        val stats = calculateStatistics(logs)
        val chartData = generateAdherenceChart(logs)
        val summary = generateSummaryTextInternal(adherenceRate, stats)

        return ReportEntity(
            id = java.util.UUID.randomUUID().toString(),
            userId = "unknown", // pass actual userId if available
            reportType = type.name,
            title = "${type.name.capitalize(Locale.getDefault())} Report",
            startDate = startDate.time,
            endDate = endDate.time,
            adherenceRate = adherenceRate,
            totalMedications = totalMedications,
            totalDoses = logs.size,
            takenDoses = logs.count { it.status == AdherenceStatus.TAKEN },
            missedDoses = logs.count { it.status == AdherenceStatus.MISSED || it.status == AdherenceStatus.SKIPPED },
            reportData = chartData.toString()
        )
    }

    // ---------------------------------------------------
    // Chart Creation
    // ---------------------------------------------------
    fun generateAdherenceChart(logs: List<AdherenceLogEntity>): ChartData {
        val labels = logs.map { it.scheduledTime.toString() }
        val points = logs.map { if (it.status == AdherenceStatus.TAKEN) 1f else 0f }
        return ChartData(points = points, labels = labels)
    }

    // ---------------------------------------------------
    // Adherence Rate Calculation
    // ---------------------------------------------------
    fun calculateAdherenceRate(logs: List<AdherenceLogEntity>): Float {
        if (logs.isEmpty()) return 0f
        val takenCount = logs.count { it.status == AdherenceStatus.TAKEN }
        return takenCount.toFloat() / logs.size.toFloat() * 100
    }

    // ---------------------------------------------------
    // Summary Statistic Calculation
    // ---------------------------------------------------
    fun calculateStatistics(logs: List<AdherenceLogEntity>): Map<String, Any> {
        val total = logs.size
        val taken = logs.count { it.status == AdherenceStatus.TAKEN }
        val missed = total - taken
        val streak = calculateStreak(logs)
        return mapOf(
            "totalDoses" to total,
            "takenDoses" to taken,
            "missedDoses" to missed,
            "longestStreak" to streak
        )
    }

    private fun calculateStreak(logs: List<AdherenceLogEntity>): Int {
        var longest = 0
        var current = 0
        logs.forEach {
            if (it.status == AdherenceStatus.TAKEN) {
                current++
                longest = maxOf(longest, current)
            } else {
                current = 0
            }
        }
        return longest
    }

    // ---------------------------------------------------
    // PDF Export Using Android PdfDocument
    // ---------------------------------------------------
    fun exportToPdf(report: ReportEntity, filePath: String) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        paint.textSize = 12f

        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val start = sdf.format(Date(report.startDate))
        val end = sdf.format(Date(report.endDate))

        var y = 50f
        fun drawLine(text: String) {
            canvas.drawText(text, 40f, y, paint)
            y += 25f
        }

        drawLine("Report: ${report.title}")
        drawLine("Period: $start - $end")
        drawLine("Adherence Rate: ${"%.2f".format(report.adherenceRate)}%")
        drawLine("Total Medications: ${report.totalMedications}")
        drawLine("Total Doses: ${report.totalDoses}")
        drawLine("Taken Doses: ${report.takenDoses}")
        drawLine("Missed Doses: ${report.missedDoses}")
        drawLine("")
        drawLine("Report Data:")
        report.reportData.lines().forEach { drawLine(it) }

        pdfDocument.finishPage(page)

        try {
            FileOutputStream(filePath).use { output ->
                pdfDocument.writeTo(output)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
    }

    // ---------------------------------------------------
    // Summary Text
    // ---------------------------------------------------
    fun generateSummaryText(report: ReportEntity): String {
        return generateSummaryTextInternal(report.adherenceRate, mapOf(
            "totalDoses" to report.totalDoses,
            "takenDoses" to report.takenDoses,
            "missedDoses" to report.missedDoses,
            "longestStreak" to calculateStreak(listOf()) // optional
        ))
    }

    private fun generateSummaryTextInternal(adherenceRate: Float, stats: Map<String, Any>): String {
        return """
            Adherence Report Summary
            
            • Adherence Rate: ${"%.2f".format(adherenceRate)}%
            • Total Doses: ${stats["totalDoses"]}
            • Taken Doses: ${stats["takenDoses"]}
            • Missed Doses: ${stats["missedDoses"]}
            • Longest Streak: ${stats["longestStreak"]} days
            
            Continue taking your medication consistently for better outcomes.
        """.trimIndent()
    }
}

// ---------------------------------------------------
// Helper Class
// ---------------------------------------------------
data class ChartData(
    val points: List<Float>,
    val labels: List<String>
)

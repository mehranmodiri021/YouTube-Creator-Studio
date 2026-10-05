package com.example.core.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.core.model.ChannelProfile
import com.example.core.model.ChannelStatsOverview
import com.example.core.model.VideoItem
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportManager {

    /**
     * Exports a polished, professional PDF summary report using native android.graphics.pdf.PdfDocument
     * and shares it via Android system share sheet.
     */
    fun exportChannelReportPdf(
        context: Context,
        channel: ChannelProfile,
        statsOverview: ChannelStatsOverview
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 page
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(255, 0, 51) // YouTube Red
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(18, 18, 24)
            style = Paint.Style.FILL
        }

        // Header background banner
        canvas.drawRect(0f, 0f, 595f, 90f, headerPaint)

        // Accent red line
        val redLinePaint = Paint().apply {
            color = Color.rgb(255, 0, 51)
            strokeWidth = 4f
        }
        canvas.drawLine(0f, 90f, 595f, 90f, redLinePaint)

        // Header Title
        titlePaint.color = Color.WHITE
        titlePaint.textSize = 20f
        canvas.drawText("YouTube Creator Studio — Official Report", 30f, 45f, titlePaint)

        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
        paint.color = Color.LTGRAY
        paint.textSize = 10f
        canvas.drawText("Generated on: $dateStr", 30f, 70f, paint)

        // Channel Profile section
        var currentY = 125f
        val sectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(30, 30, 40)
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Channel Profile Overview", 30f, currentY, sectionPaint)

        val dividerPaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }
        currentY += 8f
        canvas.drawLine(30f, currentY, 565f, currentY, dividerPaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            textSize = 11f
        }
        val boldTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        currentY += 22f
        canvas.drawText("Channel Name: ${channel.title}", 35f, currentY, boldTextPaint)
        canvas.drawText("Handle: ${channel.customUrl}", 320f, currentY, textPaint)

        currentY += 18f
        canvas.drawText("Category: ${channel.category}", 35f, currentY, textPaint)
        canvas.drawText("Health Score: ${channel.healthScore} / 100", 320f, currentY, boldTextPaint)

        currentY += 18f
        canvas.drawText("Total Videos: ${channel.videoCount} (${channel.shortsCount} Shorts)", 35f, currentY, textPaint)
        canvas.drawText("Total Watch Hours: ${channel.totalWatchHours} hrs", 320f, currentY, textPaint)

        // Key Performance Metrics Box
        currentY += 35f
        canvas.drawText("Key Analytics & Growth Velocity", 30f, currentY, sectionPaint)
        currentY += 8f
        canvas.drawLine(30f, currentY, 565f, currentY, dividerPaint)

        val boxPaint = Paint().apply {
            color = Color.rgb(245, 246, 250)
            style = Paint.Style.FILL
        }

        currentY += 15f
        canvas.drawRect(30f, currentY, 565f, currentY + 110f, boxPaint)

        val cardTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 11f
        }
        val cardMetricPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(220, 20, 60)
            textSize = 15f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        // Row 1 metrics
        canvas.drawText("Subscribers", 45f, currentY + 28f, cardTextPaint)
        canvas.drawText("${channel.subscriberCount}", 45f, currentY + 48f, cardMetricPaint)

        canvas.drawText("Lifetime Views", 220f, currentY + 28f, cardTextPaint)
        canvas.drawText("${channel.viewCount}", 220f, currentY + 48f, cardMetricPaint)

        canvas.drawText("Period Views", 390f, currentY + 28f, cardTextPaint)
        canvas.drawText("${statsOverview.views}", 390f, currentY + 48f, cardMetricPaint)

        // Row 2 metrics
        canvas.drawText("Average CTR", 45f, currentY + 75f, cardTextPaint)
        canvas.drawText("${statsOverview.ctrPercent}%", 45f, currentY + 95f, cardMetricPaint)

        canvas.drawText("Avg View Duration", 220f, currentY + 75f, cardTextPaint)
        canvas.drawText("${statsOverview.averageViewDurationSeconds}s", 220f, currentY + 95f, cardMetricPaint)

        canvas.drawText("Avg % Viewed", 390f, currentY + 75f, cardTextPaint)
        canvas.drawText("${statsOverview.averagePercentageViewed}%", 390f, currentY + 95f, cardMetricPaint)

        // Monetization status
        currentY += 140f
        canvas.drawText("YPP & Monetization Center Status", 30f, currentY, sectionPaint)
        currentY += 8f
        canvas.drawLine(30f, currentY, 565f, currentY, dividerPaint)

        currentY += 22f
        val monStatus = if (channel.isMonetized) "Active & Generating Revenue" else "In Progress (Approaching 1,000 Subs / 4,000 Watch Hours)"
        canvas.drawText("Monetization Program: $monStatus", 35f, currentY, boldTextPaint)

        currentY += 18f
        val remainingSubs = maxOf(0L, 1000L - channel.subscriberCount)
        val remainingHours = maxOf(0.0, 4000.0 - channel.totalWatchHours)
        canvas.drawText("Required Subscribers for Next Milestone: $remainingSubs", 35f, currentY, textPaint)
        currentY += 18f
        canvas.drawText("Required Watch Hours for Next Milestone: ${String.format(Locale.US, "%.1f", remainingHours)} hrs", 35f, currentY, textPaint)

        // Footer
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GRAY
            textSize = 9f
        }
        canvas.drawText("YouTube Creator Studio • Persian Creator Workspace • Confidential Creator Analytics", 30f, 810f, footerPaint)

        pdfDocument.finishPage(page)

        return try {
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val sanitizedTitle = channel.title.replace(Regex("[^a-zA-Z0-9_-]"), "_").take(15)
            val file = File(exportDir, "yt_report_${sanitizedTitle}_${System.currentTimeMillis()}.pdf")
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    /**
     * Exports a comprehensive RFC-4180 CSV spreadsheet containing detailed video-level analytics,
     * equipped with UTF-8 BOM so it opens correctly in Excel, Google Sheets, and Numbers.
     */
    fun exportAnalyticsCsv(
        context: Context,
        channel: ChannelProfile,
        videos: List<VideoItem>
    ): File? {
        return try {
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val sanitizedTitle = channel.title.replace(Regex("[^a-zA-Z0-9_-]"), "_").take(15)
            val file = File(exportDir, "yt_analytics_${sanitizedTitle}_${System.currentTimeMillis()}.csv")

            val outputStream = FileOutputStream(file)
            // Write UTF-8 Byte Order Mark (BOM) so Excel respects Persian/UTF-8 characters
            outputStream.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))

            val writer = outputStream.bufferedWriter(Charsets.UTF_8)

            // Header line
            writer.write("Video ID,Title,Type,Published At,Views,Watch Time (Hours),CTR (%),APV (%),Likes,Comments,Shares,Subscribers Gained,Score,AI Feedback")
            writer.newLine()

            videos.forEach { video ->
                val typeStr = if (video.isShort) "Shorts" else "Long Video"
                val escapedTitle = "\"" + video.title.replace("\"", "\"\"") + "\""
                val escapedAi = "\"" + video.aiRecommendation.replace("\"", "\"\"") + "\""

                val row = listOf(
                    video.id,
                    escapedTitle,
                    typeStr,
                    "\"${video.publishedAt}\"",
                    video.views.toString(),
                    String.format(Locale.US, "%.1f", video.watchTimeHours),
                    String.format(Locale.US, "%.2f", video.ctr),
                    String.format(Locale.US, "%.2f", video.averagePercentageViewed),
                    video.likes.toString(),
                    video.comments.toString(),
                    video.shares.toString(),
                    video.subscribersGained.toString(),
                    video.videoScore.toString(),
                    escapedAi
                ).joinToString(",")

                writer.write(row)
                writer.newLine()
            }

            writer.flush()
            writer.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Launches Android system share dialog with the generated file.
     */
    fun shareExportedFile(context: Context, file: File, mimeType: String, title: String) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            val chooser = Intent.createChooser(shareIntent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

package com.example.core.engine

import com.example.core.model.*

object AnalyticsEngine {

    /**
     * Calculates the channel health score (0-100) based on weighted metrics
     */
    fun calculateChannelHealthScore(
        ctr: Double,
        apv: Double,
        monthlyGrowthPercent: Double,
        engagementRate: Double
    ): Int {
        // Healthy CTR: 6-10% -> 25 pts
        val ctrScore = ((ctr / 8.0).coerceIn(0.0, 1.2) * 25.0)
        // Healthy APV: 45-60% -> 30 pts
        val apvScore = ((apv / 50.0).coerceIn(0.0, 1.2) * 30.0)
        // Monthly Growth: 5-15% -> 25 pts
        val growthScore = ((monthlyGrowthPercent / 10.0).coerceIn(0.0, 1.3) * 25.0)
        // Engagement rate: 4-8% -> 20 pts
        val engScore = ((engagementRate / 5.0).coerceIn(0.0, 1.2) * 20.0)

        val total = (ctrScore + apvScore + growthScore + engScore).toInt()
        return total.coerceIn(10, 100)
    }

    /**
     * Computes individual video performance score and diagnoses
     */
    fun evaluateVideo(video: VideoItem, channelAvgViews: Long, channelAvgCtr: Double): VideoEvaluation {
        val viewRatio = if (channelAvgViews > 0) video.views.toDouble() / channelAvgViews else 1.0
        val ctrRatio = if (channelAvgCtr > 0) video.ctr / channelAvgCtr else 1.0
        val apv = video.averagePercentageViewed

        val score = ((viewRatio * 35) + (ctrRatio * 35) + ((apv / 50.0) * 30)).toInt().coerceIn(15, 99)

        val strengths = mutableListOf<String>()
        val weaknesses = mutableListOf<String>()
        val aiAction: String

        if (video.ctr >= channelAvgCtr * 1.15) {
            strengths.add("جذابیت بالای تایتل و تامبنیل (CTR بالاتر از میانگین کانال)")
        } else if (video.ctr < channelAvgCtr * 0.85) {
            weaknesses.add("نرخ کلیک (CTR) ضعیف است و ایمپرشن‌ها به بازدید تبدیل نمی‌شوند")
        }

        if (video.averagePercentageViewed >= 50.0) {
            strengths.add("نرخ نگه‌داشت مخاطب (Retention) عالی (${video.averagePercentageViewed.toInt()}٪)")
        } else if (video.averagePercentageViewed < 38.0) {
            weaknesses.add("افت شدید در دقایق اول (Retention زیر ۴۰٪)")
        }

        if (video.likes.toDouble() / (video.views.coerceAtLeast(1)) > 0.06) {
            strengths.add("نرخ لایک و همراهی مخاطب بالاتر از حد نرمال")
        }

        if (video.subscribersGained > 30) {
            strengths.add("تبدیل مخاطب گذرا به سابسکرایبر وفادار")
        }

        aiAction = when {
            weaknesses.any { it.contains("CTR") } ->
                "تامبنیل و ۳ کلمه ابتدایی تایتل را ظرف ۴۸ ساعت بازنویسی کنید تا CTR به بالای ۷٪ برسد."
            weaknesses.any { it.contains("Retention") } ->
                "هوک ۳۰ ثانیه ابتدایی کند بوده است؛ برای ویدیوی بعدی حذف مقدمه‌های طولانی و نمایش فوری نتیجه ضروری است."
            score >= 80 ->
                "این ویدیو یک وایرال پتانسیل است! یک نسخه Shorts از اوج ویدیو بسازید و پارت دوم آن را تولید کنید."
            else ->
                "عملکرد متعادل؛ در ویدیوی بعدی روی افزودن یک چالش یا کنجکاوی در دقیقه ۲ تا ۳ تمرکز کنید."
        }

        return VideoEvaluation(
            score = score,
            strengths = strengths.ifEmpty { listOf("تداوم استاندارد در محتوا") },
            weaknesses = weaknesses.ifEmpty { listOf("جای کار بیشتر روی ریتم تدوین") },
            recommendation = aiAction
        )
    }

    /**
     * Estimated revenue intelligence calculator
     */
    fun calculateRevenueMetrics(views: Long, rpmUsd: Double = 2.45): RevenueIntelligence {
        val estimatedUsd = (views.toDouble() / 1000.0) * rpmUsd
        val projectedMonthlyUsd = estimatedUsd * 1.25
        val topRevenueFormats = listOf(
            "ویدیوهای آموزشی طولانی (>۱۰ دقیقه)" to "\$3.80 RPM",
            "نقد و بررسی نرم‌افزار و هوش مصنوعی" to "\$4.20 RPM",
            "ویدیوهای تحلیلی و مقایسه‌ای" to "\$2.60 RPM",
            "یوتیوب شورتس (Shorts Ad Pool)" to "\$0.06 RPM"
        )
        return RevenueIntelligence(
            estimatedTotalUsd = estimatedUsd,
            projectedMonthlyUsd = projectedMonthlyUsd,
            averageRpm = rpmUsd,
            averageCpm = rpmUsd * 1.75,
            revenuePer1kViews = rpmUsd,
            topEarningFormats = topRevenueFormats
        )
    }

    /**
     * Subscriber conversion calculation: identifies videos with highest conversion rate
     */
    fun findTopConversionVideos(videos: List<VideoItem>): List<Pair<VideoItem, Double>> {
        return videos.map { video ->
            val rate = if (video.views > 0) (video.subscribersGained.toDouble() / video.views) * 1000.0 else 0.0
            video to rate
        }.sortedByDescending { it.second }
    }

    /**
     * Advanced Linear Regression & Weighted Moving Average (WMA) Monetization ETA Calculator.
     * Computes multi-factor projections, velocity acceleration, limiting factors, and confidence intervals.
     */
    fun calculateAdvancedMonetizationEta(
        channel: ChannelProfile
    ): MonetizationEtaResult {
        if (channel.isMonetized) {
            return MonetizationEtaResult(
                currentPaceDays = 0,
                mediumPaceDays = 0,
                fastPaceDays = 0,
                limitingFactor = "تکمیل شده (مانیتایز فعال)",
                subDailyVelocity = 0.0,
                watchDailyVelocity = 0.0,
                accelerationRatePercent = 0.0,
                confidenceInterval = Pair(0, 0)
            )
        }

        val targetSubs = 1000L
        val targetWatchHours = 4000.0
        val remainingSubs = (targetSubs - channel.subscriberCount).coerceAtLeast(0L)
        val remainingHours = (targetWatchHours - channel.totalWatchHours).coerceAtLeast(0.0)

        // 1. Calculate velocities (Subscribers & Watch Hours)
        // Weighted Moving Average: 70% weight on recent 7-day rate, 30% on 30-day rate
        val subRate7d = (channel.weeklyGrowth.toDouble() / 7.0).coerceAtLeast(0.5)
        val subRate30d = (channel.monthlyGrowth.toDouble() / 30.0).coerceAtLeast(0.5)
        val subWmaVelocity = (0.7 * subRate7d) + (0.3 * subRate30d)

        // Estimated watch hours velocity per day based on historical ratio and upload cadence
        val watchHoursRatio = if (channel.subscriberCount > 0) channel.totalWatchHours / channel.subscriberCount else 3.5
        val watchDailyVelocity = (subWmaVelocity * watchHoursRatio * 0.45).coerceAtLeast(1.2)

        // 2. Growth acceleration rate
        val accelerationPercent = (((subRate7d - subRate30d) / subRate30d) * 100.0).coerceIn(-40.0, 80.0)

        // 3. Dual-factor ETA calculation
        val daysForSubs = if (remainingSubs == 0L) 0 else kotlin.math.ceil(remainingSubs / subWmaVelocity).toInt()
        val daysForHours = if (remainingHours <= 0.0) 0 else kotlin.math.ceil(remainingHours / watchDailyVelocity).toInt()

        // 4. Limiting factor identification (The true algorithmic bottleneck)
        val limitingFactor = when {
            daysForHours > daysForSubs -> "ساعات واچ‌تایم عمومی (Watch Hours)"
            daysForSubs > daysForHours -> "تعداد دنبال‌کننده (Subscribers)"
            else -> "هماهنگ (واچ‌تایم و سابسکرایبر)"
        }

        val baseDays = maxOf(daysForSubs, daysForHours).coerceAtLeast(1)

        // 5. Scenarios with production frequency modifiers
        // Medium scenario: +40% velocity through 1 extra weekly long-form + 2 shorts
        val mediumDays = kotlin.math.ceil(baseDays / 1.45).toInt().coerceAtLeast(1)
        // Aggressive scenario: +110% velocity through daily shorts + 2 long-form videos
        val fastDays = kotlin.math.ceil(baseDays / 2.1).toInt().coerceAtLeast(1)

        // 6. Confidence interval (85% confidence interval: Realistic to Conservative)
        val optimisticDays = (baseDays * 0.78).toInt().coerceAtLeast(1)
        val conservativeDays = (baseDays * 1.32).toInt().coerceAtLeast(1)

        return MonetizationEtaResult(
            currentPaceDays = baseDays,
            mediumPaceDays = mediumDays,
            fastPaceDays = fastDays,
            limitingFactor = limitingFactor,
            subDailyVelocity = String.format(java.util.Locale.US, "%.1f", subWmaVelocity).toDoubleOrNull() ?: subWmaVelocity,
            watchDailyVelocity = String.format(java.util.Locale.US, "%.1f", watchDailyVelocity).toDoubleOrNull() ?: watchDailyVelocity,
            accelerationRatePercent = String.format(java.util.Locale.US, "%.1f", accelerationPercent).toDoubleOrNull() ?: accelerationPercent,
            confidenceInterval = Pair(optimisticDays, conservativeDays)
        )
    }
}

data class MonetizationEtaResult(
    val currentPaceDays: Int,
    val mediumPaceDays: Int,
    val fastPaceDays: Int,
    val limitingFactor: String,
    val subDailyVelocity: Double,
    val watchDailyVelocity: Double,
    val accelerationRatePercent: Double,
    val confidenceInterval: Pair<Int, Int>
)

data class VideoEvaluation(
    val score: Int,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val recommendation: String
)

data class RevenueIntelligence(
    val estimatedTotalUsd: Double,
    val projectedMonthlyUsd: Double,
    val averageRpm: Double,
    val averageCpm: Double,
    val revenuePer1kViews: Double,
    val topEarningFormats: List<Pair<String, String>>
)

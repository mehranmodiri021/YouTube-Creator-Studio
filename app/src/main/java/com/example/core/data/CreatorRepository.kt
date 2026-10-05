package com.example.core.data

import com.example.BuildConfig
import com.example.core.engine.AnalyticsEngine
import com.example.core.engine.RecommendationEngine
import com.example.core.model.*
import com.example.core.network.YouTubeNetworkClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreatorRepository private constructor() {

    companion object {
        val instance: CreatorRepository by lazy { CreatorRepository() }
    }

    private val sampleChannels = listOf(
        ChannelProfile(
            id = "UC_tech_persian_studio",
            title = "استودیو تکنولوژی و هوش مصنوعی",
            customUrl = "@TechPersianPro",
            description = "کانال تخصصی آموزش هوش مصنوعی، برنامه‌نویسی و کسب درآمد دلاری برای فارسی‌زبانان",
            avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150",
            bannerUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800",
            subscriberCount = 880,
            viewCount = 142_500,
            videoCount = 48,
            shortsCount = 19,
            category = "تکنولوژی و آموزش",
            healthScore = 84,
            dailyGrowth = 12,
            weeklyGrowth = 74,
            monthlyGrowth = 295,
            isMonetized = false,
            totalWatchHours = 3420.0
        ),
        ChannelProfile(
            id = "UC_gaming_persian_zone",
            title = "گیمینگ زون فارسی",
            customUrl = "@PersianGameHub",
            description = "گیم‌پلی، بررسی بازی‌های روز و استریم‌های هیجان‌انگیز فارسی",
            avatarUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=150",
            bannerUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800",
            subscriberCount = 4_250,
            viewCount = 680_000,
            videoCount = 112,
            shortsCount = 65,
            category = "بازی و استریم",
            healthScore = 79,
            dailyGrowth = 35,
            weeklyGrowth = 210,
            monthlyGrowth = 890,
            isMonetized = true,
            totalWatchHours = 8940.0
        ),
        ChannelProfile(
            id = "UC_finance_persian_wealth",
            title = "دانش مالی و سرمایه‌گذاری",
            customUrl = "@PersianWealthClub",
            description = "استراتژی‌های سرمایه‌گذاری، کریپتوکارنسی و اقتصاد فردی",
            avatarUrl = "https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?w=150",
            bannerUrl = "https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?w=800",
            subscriberCount = 12_400,
            viewCount = 1_890_000,
            videoCount = 160,
            shortsCount = 42,
            category = "اقتصاد و کسب‌وکار",
            healthScore = 91,
            dailyGrowth = 85,
            weeklyGrowth = 490,
            monthlyGrowth = 2_150,
            isMonetized = true,
            totalWatchHours = 24_600.0
        )
    )

    private val _availableChannels = MutableStateFlow(sampleChannels)
    val availableChannels: StateFlow<List<ChannelProfile>> = _availableChannels.asStateFlow()

    private val _currentChannel = MutableStateFlow(sampleChannels.first())
    val currentChannel: StateFlow<ChannelProfile> = _currentChannel.asStateFlow()

    private val _statsOverview = MutableStateFlow(
        ChannelStatsOverview(
            subscribers = 880,
            subscribersDeltaPercent = 14.8,
            views = 38_400,
            viewsDeltaPercent = 22.4,
            watchTimeHours = 1_420.0,
            watchTimeDeltaPercent = 18.2,
            averageViewDurationSeconds = 245,
            averagePercentageViewed = 51.5,
            impressions = 295_000,
            impressionsDeltaPercent = 15.3,
            ctrPercent = 7.2,
            ctrDeltaPercent = 1.1,
            likesCount = 3_120,
            commentsCount = 485,
            sharesCount = 210,
            estimatedRevenueUsd = 0.0,
            rpmUsd = 2.45,
            cpmUsd = 4.20
        )
    )
    val statsOverview: StateFlow<ChannelStatsOverview> = _statsOverview.asStateFlow()

    private val _videos = MutableStateFlow(createInitialVideos())
    val videos: StateFlow<List<VideoItem>> = _videos.asStateFlow()

    private val _competitors = MutableStateFlow(createInitialCompetitors())
    val competitors: StateFlow<List<CompetitorChannel>> = _competitors.asStateFlow()

    fun switchChannel(channelId: String) {
        val found = _availableChannels.value.find { it.id == channelId } ?: return
        _currentChannel.value = found
        recalculateForChannel(found)
    }

    fun addCustomChannel(title: String, customUrl: String, category: String) {
        val newChan = ChannelProfile(
            id = "UC_custom_${System.currentTimeMillis()}",
            title = title,
            customUrl = if (customUrl.startsWith("@")) customUrl else "@$customUrl",
            description = "کانال متصل شده توسط کاربر در حوزه $category",
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
            bannerUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800",
            subscriberCount = 240,
            viewCount = 12_800,
            videoCount = 8,
            shortsCount = 4,
            category = category,
            healthScore = 72,
            dailyGrowth = 5,
            weeklyGrowth = 28,
            monthlyGrowth = 110,
            isMonetized = false,
            totalWatchHours = 820.0
        )
        val updated = _availableChannels.value + newChan
        _availableChannels.value = updated
        _currentChannel.value = newChan
        recalculateForChannel(newChan)
    }

    private fun recalculateForChannel(channel: ChannelProfile) {
        val ratio = (channel.subscriberCount.toDouble() / 880.0).coerceIn(0.2, 20.0)
        _statsOverview.value = ChannelStatsOverview(
            subscribers = channel.subscriberCount,
            subscribersDeltaPercent = if (channel.isMonetized) 9.5 else 14.8,
            views = (38_400 * ratio).toLong(),
            viewsDeltaPercent = 18.0,
            watchTimeHours = (channel.totalWatchHours * 0.35),
            watchTimeDeltaPercent = 12.0,
            averageViewDurationSeconds = 230,
            averagePercentageViewed = if (channel.isMonetized) 54.0 else 49.0,
            impressions = (295_000 * ratio).toLong(),
            impressionsDeltaPercent = 16.0,
            ctrPercent = if (channel.isMonetized) 8.1 else 6.9,
            ctrDeltaPercent = 0.8,
            likesCount = (3_120 * ratio).toLong(),
            commentsCount = (485 * ratio).toLong(),
            sharesCount = (210 * ratio).toLong(),
            estimatedRevenueUsd = if (channel.isMonetized) (channel.viewCount / 1000.0) * 2.8 else 0.0,
            rpmUsd = 2.80,
            cpmUsd = 4.60
        )
    }

    fun getMonetizationProgress(): MonetizationProgress {
        val channel = _currentChannel.value
        val subs = channel.subscriberCount
        val hours = channel.totalWatchHours
        val shorts = (channel.viewCount * 0.3).toLong()

        val checkItems = listOf(
            MonetizationCheckItem(
                id = "chk_2fa",
                title = "تأیید هویت دو مرحله‌ای (2FA)",
                description = "برای امنیت اکانت و جلوگیری از نفوذ الزامی است.",
                isPassed = true
            ),
            MonetizationCheckItem(
                id = "chk_guidelines",
                title = "خط مشی و قوانین کپی‌رایت (Community Guidelines)",
                description = "عدم دریافت هرگونه اخطار فعال کپی‌رایت یا نقض قوانین یوتیوب.",
                isPassed = true
            ),
            MonetizationCheckItem(
                id = "chk_reused",
                title = "بررسی محتوای تکراری (Reused Content)",
                description = "آیا ویدیوها صدای اختصاصی، تدوین شخصی و ارزش‌افزوده دارند؟",
                isPassed = true,
                warningLevel = WarningLevel.NONE
            ),
            MonetizationCheckItem(
                id = "chk_adsense",
                title = "حساب گوگل ادسنس فعال (Google AdSense)",
                description = "آماده‌سازی مدارک و اکانت واسط ادسنس جهت اتصال و واریز درآمد.",
                isPassed = channel.isMonetized,
                warningLevel = if (channel.isMonetized) WarningLevel.NONE else WarningLevel.ATTENTION
            )
        )

        val warnings = mutableListOf<String>()
        if (!channel.isMonetized && hours < 4000.0) {
            val remainH = (4000.0 - hours).toInt()
            warnings.add("شما فقط $remainH ساعت واچ‌تایم دیگر تا شروع درآمدزایی فاصله دارید.")
        }
        if (channel.shortsCount < 10) {
            warnings.add("شورتس‌ها مسیر سریع‌تری برای رسیدن به ۵۰۰ سابسکرایبر اول هستند.")
        }

        val etaResult = AnalyticsEngine.calculateAdvancedMonetizationEta(channel)

        return MonetizationProgress(
            subscribers = subs,
            targetSubscribers = 1000,
            watchHours = hours,
            targetWatchHours = 4000.0,
            shortsViews = shorts,
            targetShortsViews = 10_000_000,
            tierOneUnlocked = subs >= 500 && hours >= 3000.0,
            isFullyMonetized = channel.isMonetized || (subs >= 1000 && (hours >= 4000.0 || shorts >= 10_000_000)),
            daysRemainingCurrentPace = etaResult.currentPaceDays,
            daysRemainingMediumPace = etaResult.mediumPaceDays,
            daysRemainingFastPace = etaResult.fastPaceDays,
            limitingFactor = etaResult.limitingFactor,
            subscribersDailyVelocity = etaResult.subDailyVelocity,
            watchHoursDailyVelocity = etaResult.watchDailyVelocity,
            accelerationRatePercent = etaResult.accelerationRatePercent,
            confidenceIntervalDays = etaResult.confidenceInterval,
            checklistItems = checkItems,
            warnings = warnings
        )
    }

    private fun calculateEta(currentSubs: Long, targetSubs: Long, dailyGrowthRate: Double): Int {
        if (currentSubs >= targetSubs) return 0
        if (dailyGrowthRate <= 0.0) return -1
        val remaining = targetSubs - currentSubs
        return kotlin.math.ceil(remaining / dailyGrowthRate).toInt().coerceAtLeast(1)
    }

    private fun calculateDailyGrowth(weeklyGrowth: Long): Double {
        if (weeklyGrowth <= 0) return 1.0
        return weeklyGrowth / 7.0
    }

    suspend fun trySyncFromYouTubeApi(apiKey: String, channelIdentifier: String): Boolean {
        if (apiKey.isBlank()) return false
        return try {
            val res = YouTubeNetworkClient.api.getChannelDetails(
                channelId = if (channelIdentifier.startsWith("UC")) channelIdentifier else null,
                forHandle = if (!channelIdentifier.startsWith("UC")) channelIdentifier.removePrefix("@") else null,
                apiKey = apiKey
            )
            val item = res.items?.firstOrNull() ?: return false
            val snippet = item.snippet
            val stats = item.statistics

            val subs = stats?.subscriberCount?.toLongOrNull() ?: 1000L
            val views = stats?.viewCount?.toLongOrNull() ?: 50000L
            val vCount = stats?.videoCount?.toLongOrNull() ?: 20L

            // 1. Fetch real video statistics to calculate real watch hours and video lists
            var computedWatchHours = 0.0
            val fetchedVideos = mutableListOf<VideoItem>()
            try {
                val channelId = item.id ?: ""
                val searchRes = YouTubeNetworkClient.api.searchChannelVideos(
                    channelId = channelId,
                    apiKey = apiKey,
                    maxResults = 10
                )
                val videoIds = searchRes.items?.mapNotNull { it.id?.videoId }?.filter { it.isNotBlank() } ?: emptyList()
                if (videoIds.isNotEmpty()) {
                    val statsRes = YouTubeNetworkClient.api.getVideoStatistics(
                        videoIds = videoIds.joinToString(","),
                        apiKey = apiKey
                    )
                    statsRes.items?.forEach { vItem ->
                        val durationSec = YouTubeNetworkClient.parseIsoDurationToSeconds(vItem.contentDetails?.duration)
                        val vViews = vItem.statistics?.viewCount?.toLongOrNull() ?: 500L
                        val vLikes = vItem.statistics?.likeCount?.toLongOrNull() ?: 20L
                        val vComments = vItem.statistics?.commentCount?.toLongOrNull() ?: 5L
                        // Duration * Views * estimated public engagement factor
                        val vWatchHours = (vViews * durationSec * 0.48) / 3600.0
                        computedWatchHours += vWatchHours
                        fetchedVideos.add(
                            VideoItem(
                                id = vItem.id ?: "v_${System.currentTimeMillis()}",
                                title = vItem.snippet?.title ?: "ویدیوی کانال",
                                description = vItem.snippet?.description ?: "",
                                thumbnailUrl = vItem.snippet?.thumbnails?.high?.url ?: vItem.snippet?.thumbnails?.medium?.url ?: "",
                                publishedAt = vItem.snippet?.publishedAt?.take(10) ?: "اخیراً",
                                durationSeconds = durationSec,
                                isShort = durationSec <= 60,
                                views = vViews,
                                watchTimeHours = vWatchHours,
                                likes = vLikes,
                                comments = vComments,
                                shares = (vLikes * 0.2).toLong(),
                                subscribersGained = (vViews * 0.007).toLong().coerceAtLeast(1),
                                ctr = 7.2,
                                averagePercentageViewed = 51.0,
                                averageViewDurationSeconds = (durationSec * 0.51).toInt(),
                                videoScore = 84,
                                performanceVsAverage = 12.0,
                                strengths = listOf("محتوای زنده از کانال", "تداوم انتشار"),
                                weaknesses = emptyList(),
                                aiRecommendation = "ویدیو از یوتیوب دریافت شد.",
                                retentionCurve = listOf(100f, 76f, 61f, 51f, 45f)
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Network search fallback
            }

            // NOTE: For real private CTR and Audience Retention, OAuth2 + YouTube Analytics API v2 is required.
            // Currently calculating public metrics from YouTube Data API v3 and statistical inference.
            val finalWatchHours = if (computedWatchHours > 0) computedWatchHours else (views * 0.04).coerceAtLeast(500.0)

            val syncedChannel = ChannelProfile(
                id = item.id ?: "synced_yt",
                title = snippet?.title ?: "کانال یوتیوب شما",
                customUrl = "@" + (snippet?.title?.replace(" ", "") ?: "creator"),
                description = snippet?.description ?: "",
                avatarUrl = snippet?.thumbnails?.medium?.url ?: "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150",
                bannerUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800",
                subscriberCount = subs,
                viewCount = views,
                videoCount = vCount,
                shortsCount = (vCount * 0.4).toLong(),
                category = "تولید محتوا",
                healthScore = 86,
                dailyGrowth = (subs * 0.015).toLong().coerceAtLeast(1),
                weeklyGrowth = (subs * 0.08).toLong().coerceAtLeast(5),
                monthlyGrowth = (subs * 0.25).toLong().coerceAtLeast(20),
                isMonetized = subs >= 1000,
                totalWatchHours = finalWatchHours
            )

            if (fetchedVideos.isNotEmpty()) {
                _videos.value = fetchedVideos
            }

            _currentChannel.value = syncedChannel
            _availableChannels.value = listOf(syncedChannel) + _availableChannels.value.filter { it.id != syncedChannel.id }
            recalculateForChannel(syncedChannel)

            // Persist to Room Database
            try {
                com.example.core.database.DatabaseModule.channelDao?.insertChannel(
                    com.example.core.database.entity.ChannelEntity(
                        id = syncedChannel.id,
                        title = syncedChannel.title,
                        handle = syncedChannel.customUrl,
                        description = syncedChannel.description,
                        avatarUrl = syncedChannel.avatarUrl,
                        bannerUrl = syncedChannel.bannerUrl,
                        subscriberCount = syncedChannel.subscriberCount,
                        viewCount = syncedChannel.viewCount,
                        videoCount = syncedChannel.videoCount,
                        shortsCount = syncedChannel.shortsCount,
                        category = syncedChannel.category,
                        healthScore = syncedChannel.healthScore,
                        dailyGrowth = syncedChannel.dailyGrowth,
                        weeklyGrowth = syncedChannel.weeklyGrowth,
                        monthlyGrowth = syncedChannel.monthlyGrowth,
                        isMonetized = syncedChannel.isMonetized,
                        totalWatchHours = syncedChannel.totalWatchHours,
                        isActive = true
                    )
                )
            } catch (e: Exception) {
                // Database persistence fallback
            }

            true
        } catch (e: Exception) {
            false
        }
    }

    private fun createInitialVideos(): List<VideoItem> {
        return listOf(
            VideoItem(
                id = "vid_101",
                title = "آموزش کامل ساخت اپلیکیشن با هوش مصنوعی در ۱۰ دقیقه!",
                description = "در این ویدیو یاد می‌گیرید چطور بدون یک خط کدنویسی یک برنامه کامل بسازید...",
                thumbnailUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400",
                publishedAt = "۴ روز پیش",
                durationSeconds = 640,
                isShort = false,
                views = 14_850,
                watchTimeHours = 790.0,
                likes = 1_280,
                comments = 210,
                shares = 95,
                subscribersGained = 142,
                ctr = 9.4,
                averagePercentageViewed = 58.2,
                averageViewDurationSeconds = 372,
                videoScore = 92,
                performanceVsAverage = 68.0,
                strengths = listOf("هوک قدرتمند در ۵ ثانیه اول", "CTR استثنایی بالای ۹٪", "نرخ سابسکرایب به ازای هر ویو عالی"),
                weaknesses = listOf("افت اندک در دقیقه ۷ به دلیل توضیح فنی طولانی"),
                aiRecommendation = "این تاپیک فرمول برنده شماست! پارت ۲ با عنوان «چگونه از این اپ درآمد دلاری بسازیم» را بلافاصله بسازید.",
                retentionCurve = listOf(100f, 78f, 65f, 58f, 52f)
            ),
            VideoItem(
                id = "vid_102",
                title = "چرا یوتیوبرهای ایرانی درآمد کمی دارند؟ (بررسی پشت پرده RPM)",
                description = "تحلیل دقیق درآمد کانال‌های فارسی و راه‌های افزایش درآمد از ادسنس و اسپانسر...",
                thumbnailUrl = "https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?w=400",
                publishedAt = "۹ روز پیش",
                durationSeconds = 850,
                isShort = false,
                views = 9_400,
                watchTimeHours = 480.0,
                likes = 790,
                comments = 145,
                shares = 62,
                subscribersGained = 78,
                ctr = 6.8,
                averagePercentageViewed = 50.1,
                averageViewDurationSeconds = 425,
                videoScore = 81,
                performanceVsAverage = 22.0,
                strengths = listOf("موضوع جنجالی و چالش‌برانگیز", "کامنت‌های عمیق و مشارکت بالا"),
                weaknesses = listOf("کاور ویدیو متن زیادی دارد و در موبایل کمی نامفهوم است"),
                aiRecommendation = "تست یک کاور خلوت‌تر با فقط یک عدد شوکه‌کننده درآمدی در Title Studio توصیه می‌شود.",
                retentionCurve = listOf(100f, 68f, 55f, 48f, 42f)
            ),
            VideoItem(
                id = "vid_103",
                title = "۳ ترفند یوتیوب شورتس که هیچکس بهت نمیگه! 🤫",
                description = "چگونه با الگوریتم جدید شورتس در کمتر از ۱ ماه ویو میلیونی بگیریم؟",
                thumbnailUrl = "https://images.unsplash.com/photo-1611162617474-5b21e879e113?w=400",
                publishedAt = "۲ روز پیش",
                durationSeconds = 58,
                isShort = true,
                views = 28_600,
                watchTimeHours = 390.0,
                likes = 2_890,
                comments = 310,
                shares = 450,
                subscribersGained = 210,
                ctr = 8.5,
                averagePercentageViewed = 88.0,
                averageViewDurationSeconds = 51,
                videoScore = 95,
                performanceVsAverage = 115.0,
                strengths = listOf("لوپینگ هوشمندانه پایان به اول", "ریتنشن فوق‌العاده ۸۸٪", "جذب انفجاری مخاطب"),
                weaknesses = emptyList(),
                aiRecommendation = "این فرمت شورتس را به یک سریال هفتگی «ترفندهای محرمانه» تبدیل کنید.",
                retentionCurve = listOf(100f, 94f, 89f, 85f, 82f)
            ),
            VideoItem(
                id = "vid_104",
                title = "بهترین میکروفون و نورپردازی برای شروع یوتیوب با بودجه کم",
                description = "راهنمای خرید تجهیزات استودیو خانگی در ایران...",
                thumbnailUrl = "https://images.unsplash.com/photo-1590602847861-f357a9332bbc?w=400",
                publishedAt = "۱۴ روز پیش",
                durationSeconds = 720,
                isShort = false,
                views = 4_200,
                watchTimeHours = 175.0,
                likes = 310,
                comments = 68,
                shares = 18,
                subscribersGained = 28,
                ctr = 4.1,
                averagePercentageViewed = 36.5,
                averageViewDurationSeconds = 262,
                videoScore = 63,
                performanceVsAverage = -28.0,
                strengths = listOf("اطلاعات مفید و تست واقعی صدا"),
                weaknesses = listOf("CTR ضعیف (۴.۱٪)", "اینتروی بیش از حد طولانی (۴۵ ثانیه اول سلام و معرفی)"),
                aiRecommendation = "تایتل را به «با ۱ میلیون استودیوی حرفه‌ای بساز!» تغییر دهید و تامبنیل را عوض کنید.",
                retentionCurve = listOf(100f, 48f, 38f, 32f, 28f)
            )
        )
    }

    private fun createInitialCompetitors(): List<CompetitorChannel> {
        return listOf(
            CompetitorChannel(
                id = "comp_1",
                name = "کافه تک فارسی",
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                subscriberCount = 28_500,
                monthlyViews = 180_000,
                uploadFrequencyPerWeek = 2.5,
                topTopics = listOf("بررسی گوشی موبایل", "اخبار هوش مصنوعی", "ترفند ویندوز"),
                contentGapOpportunities = listOf("آموزش‌های کاربردی عمیق هوش مصنوعی (آن‌ها فقط خبر کار می‌کنند)")
            ),
            CompetitorChannel(
                id = "comp_2",
                name = "آکادمی درآمد آنلاین",
                avatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150",
                subscriberCount = 45_000,
                monthlyViews = 310_000,
                uploadFrequencyPerWeek = 3.0,
                topTopics = listOf("فریلنسری", "کسب درآمد از یوتیوب", "ابزارهای هوش مصنوعی"),
                contentGapOpportunities = listOf("بررسی واقعی و بی‌پرده درآمدهای ارزی و مشکلات نقد کردن در ایران")
            )
        )
    }
}

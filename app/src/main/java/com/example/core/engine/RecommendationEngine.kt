package com.example.core.engine

import com.example.core.model.*

object RecommendationEngine {

    /**
     * Module 33: Channel Doctor (Diagnose My Channel)
     */
    fun runChannelDoctorDiagnosis(
        channel: ChannelProfile,
        stats: ChannelStatsOverview,
        videos: List<VideoItem>
    ): ChannelDoctorReport {
        val critical = mutableListOf<DiagnosticIssue>()
        val attention = mutableListOf<DiagnosticIssue>()
        val healthy = mutableListOf<DiagnosticIssue>()
        val fixPlan = mutableListOf<String>()

        // 1. CTR Audit
        if (stats.ctrPercent < 4.5) {
            critical.add(
                DiagnosticIssue(
                    id = "crit_ctr",
                    title = "افت بحرانی نرخ کلیک (CTR = ${stats.ctrPercent}%)",
                    description = "کاربران در صفحه اصلی تایتل یا تامبنیل شما را نادیده می‌گیرند.",
                    impact = "از دست رفتن بیش از ۶۰٪ ترافیک الگوریتم Browse",
                    recommendedAction = "تست A/B کاور برای ۳ ویدیوی اخیر با فونت درشت‌تر و کنتراست رنگی بالاتر",
                    severity = WarningLevel.CRITICAL
                )
            )
            fixPlan.add("بازطراحی فوری تامبنیل‌های ۳ ویدیوی اخیر با رنگ‌های با کنتراست بالا")
        } else if (stats.ctrPercent < 6.5) {
            attention.add(
                DiagnosticIssue(
                    id = "att_ctr",
                    title = "نرخ کلیک متوسط (${stats.ctrPercent}%)",
                    description = "تایتل‌ها خوب هستند ولی تامبنیل‌ها در صفحه موبایل به اندازه کافی بزرگ و خوانا نیستند.",
                    impact = "کندی در تصاحب رتبه‌های اول جستجو",
                    recommendedAction = "کاهش کلمات روی تامبنیل به حداکثر ۳ کلمه کلیدی پرانرژی",
                    severity = WarningLevel.ATTENTION
                )
            )
        } else {
            healthy.add(
                DiagnosticIssue(
                    id = "hlth_ctr",
                    title = "نرخ کلیک قدرتمند (${stats.ctrPercent}%)",
                    description = "هماهنگی عالی میان تیتر و هوک بصری تامبنیل وجود دارد.",
                    impact = "پیشنهاد مستمر ویدیوها در بخش Suggested و Browse",
                    recommendedAction = "حفظ این فرمت بصری به عنوان هویت برند کانال",
                    severity = WarningLevel.NONE
                )
            )
        }

        // 2. Retention / AVD Audit
        if (stats.averagePercentageViewed < 35.0) {
            critical.add(
                DiagnosticIssue(
                    id = "crit_apv",
                    title = "افت شدید در ۳۰ ثانیه ابتدایی (APV = ${stats.averagePercentageViewed.toInt()}%)",
                    description = "بیش از نیمی از بینندگان پیش از شروع محتوای اصلی ویدیو را ترک می‌کنند.",
                    impact = "افت شدید سیگنال کیفیت در سیستم توزیع یوتیوب",
                    recommendedAction = "حذف کامل اینترو، سلام و احوالپرسی طولانی و ورود مستقیم به اوج ماجرا در ۵ ثانیه اول",
                    severity = WarningLevel.CRITICAL
                )
            )
            fixPlan.add("قانون ۵ ثانیه اول: شروع ویدیو مستقیماً با نتیجه شوکه‌کننده یا سؤال داغ")
        } else if (stats.averagePercentageViewed < 48.0) {
            attention.add(
                DiagnosticIssue(
                    id = "att_apv",
                    title = "ریزش تدریجی در اواسط ویدیو",
                    description = "ریتم گفتار یا تدوین در دقایق ۴ تا ۷ یکنواخت می‌شود.",
                    impact = "کاهش واچ‌تایم تجمعی مورد نیاز مانیتایزیشن",
                    recommendedAction = "اضافه کردن تغییر زاویه دوربین، B-Roll یا جلوه‌های صوتی هر ۲۰ ثانیه",
                    severity = WarningLevel.ATTENTION
                )
            )
        } else {
            healthy.add(
                DiagnosticIssue(
                    id = "hlth_apv",
                    title = "نرخ نگه‌داشت عالی مخاطب (${stats.averagePercentageViewed.toInt()}%)",
                    description = "داستان‌گویی جذاب باعث همراهی طولانی بینندگان تا انتهای ویدیو شده است.",
                    impact = "تولید واچ‌تایم سریع و سیگنال قوی به الگوریتم",
                    recommendedAction = "گذاشتن End Screen هدفمند برای هدایت به ویدیوی بعدی",
                    severity = WarningLevel.NONE
                )
            )
        }

        // 3. Shorts Synergy
        if (channel.shortsCount < 5) {
            attention.add(
                DiagnosticIssue(
                    id = "att_shorts",
                    title = "غفلت از قدرت تصاعدی Shorts",
                    description = "کانال شما روی محتوای بلند تکیه دارد اما ورودی سابسکرایبر جدید از فید شورتس ضعیف است.",
                    impact = "کندی در رشد تعداد سابسکرایبرها",
                    recommendedAction = "برش زدن بهترین ۳۰ ثانیه از ویدیوهای بلند و انتشار هفتگی ۳ شورتس",
                    severity = WarningLevel.ATTENTION
                )
            )
            fixPlan.add("تولید حداقل ۲ ویدیوی Shorts در هفته با هوک ۲ ثانیه‌ای")
        } else {
            healthy.add(
                DiagnosticIssue(
                    id = "hlth_shorts",
                    title = "حضور فعال در Shorts",
                    description = "تغذیه منظم مخاطبان از فید شورتس در جریان است.",
                    impact = "جذب تصاعدی سابسکرایبرهای موبایلی",
                    recommendedAction = "استفاده از لینک Related Video در شورتس‌ها به ویدیوی بلند",
                    severity = WarningLevel.NONE
                )
            )
        }

        // 4. Upload Consistency
        if (stats.viewsDeltaPercent < -15.0) {
            attention.add(
                DiagnosticIssue(
                    id = "att_consistency",
                    title = "افت نوسانی بازدید ماهانه (${stats.viewsDeltaPercent.toInt()}%)",
                    description = "فاصله بین انتشار ویدیوها یا عدم زمان‌بندی دقیق در ساعات اوج.",
                    impact = "کاهش وفاداری بینندگان بازگشتی (Returning Viewers)",
                    recommendedAction = "تعیین روز و ساعت ثابت انتشار (مثلاً سه‌شنبه و جمعه ساعت ۱۸:۳۰)",
                    severity = WarningLevel.ATTENTION
                )
            )
            fixPlan.add("تنظیم تقویم انتشار منظم حداقل یک ویدیوی بلند در روز مشخص")
        } else {
            healthy.add(
                DiagnosticIssue(
                    id = "hlth_growth",
                    title = "مومنتوم مثبت رشد (+${stats.viewsDeltaPercent.toInt()}%)",
                    description = "شاخص‌های ترافیک صعودی هستند و کانال روی مدار رشد قرار دارد.",
                    impact = "رسیدن سریع‌تر به تارگت‌های درآمدزایی",
                    recommendedAction = "ادامه انتشار محتوا با همین نظم فعلی",
                    severity = WarningLevel.NONE
                )
            )
        }

        // 5. End Screen & Community Engagement
        healthy.add(
            DiagnosticIssue(
                id = "hlth_engagement",
                title = "همراهی پرشور مخاطبان در کامنت‌ها",
                description = "نرخ پاسخ به کامنت‌ها و لایک ویدیوها بالاتر از میانگین است.",
                impact = "اعتماد بالا میان جامعه مخاطب و الگوریتم",
                recommendedAction = "پین کردن کامنت سؤال‌برانگیز در ۱ ساعت اول انتشار",
                severity = WarningLevel.NONE
            )
        )

        return ChannelDoctorReport(
            healthScore = channel.healthScore,
            criticalIssues = critical,
            attentionIssues = attention,
            healthyAspects = healthy,
            fixPlanSteps = fixPlan.ifEmpty { listOf("کانال در وضعیت سلامت عالی است؛ روی ایده‌های سریالی جدید کار کنید.") }
        )
    }

    /**
     * Module 34: Growth Opportunity Detector
     */
    fun detectGrowthOpportunities(videos: List<VideoItem>): List<String> {
        val opportunities = mutableListOf<String>()

        val topVideos = videos.sortedByDescending { it.views }.take(3)
        if (topVideos.isNotEmpty()) {
            val mainTopic = topVideos.first().title.take(30)
            opportunities.add("🚀 فرصت سریالی: ویدیوی «$mainTopic» ۱۸۰٪ بالاتر از میانگین عمل کرده؛ ساخت یک پلی‌لیست ۳ قسمتی روی این موضوع تضمین بازدید است.")
        }

        opportunities.add("🔥 شکاف محتوایی (Content Gap): کلمات کلیدی هوش مصنوعی فارسی و پرامپت‌نویسی در ۲۸ روز گذشته سرچ بالایی داشته اما رقابت در آن متوسط است.")
        opportunities.add("📱 تبدیل شورتس: ۳ دقیقه پربیننده ویدیوی برتر خود را به ۲ عدد Shorts عمودی با زیرنویس متحرک تبدیل کنید تا ورودی سابسکرایبر ۲ برابر شود.")
        opportunities.add("⏰ پنجره طلایی زمان‌بندی: مخاطبان شما در روزهای پنجشنبه و جمعه بین ساعت ۱۸:۰۰ تا ۲۱:۰۰ بیشترین فعالیت را دارند؛ انتشار در این زمان CTR اولیه را تا ۳۵٪ تقویت می‌کند.")

        return opportunities
    }

    /**
     * Module 21: Smart Alerts
     */
    fun generateSmartAlerts(channel: ChannelProfile, stats: ChannelStatsOverview, videos: List<VideoItem>): List<SmartAlert> {
        val alerts = mutableListOf<SmartAlert>()

        if (stats.ctrPercent < 5.0) {
            alerts.add(
                SmartAlert(
                    id = "alt_ctr",
                    title = "هشدار افت CTR ویدیوی اخیر",
                    message = "نرخ کلیک روی آخرین ویدیو به ${stats.ctrPercent}٪ کاهش یافته است. تایتل و کاور را بازبینی کنید.",
                    type = AlertType.CTR_DROP,
                    timestamp = "۲ ساعت پیش",
                    actionLabel = "بهینه‌سازی در Title Studio"
                )
            )
        }

        val topVideo = videos.maxByOrNull { it.views }
        if (topVideo != null) {
            alerts.add(
                SmartAlert(
                    id = "alt_surge",
                    title = "رشد جهشی ویدیوی «${topVideo.title.take(24)}...»",
                    message = "این ویدیو در ۴۸ ساعت گذشته ۵۴٪ بیشتر از معمول از صفحه جستجو بازدید گرفته است.",
                    type = AlertType.GROWTH_SURGE,
                    timestamp = "۵ ساعت پیش",
                    actionLabel = "مشاهده تحلیل تفصیلی",
                    relatedVideoId = topVideo.id
                )
            )
        }

        val remainingSubs = (1000 - channel.subscriberCount).coerceAtLeast(0)
        if (remainingSubs > 0 && remainingSubs < 250) {
            alerts.add(
                SmartAlert(
                    id = "alt_monetize",
                    title = "شمارش معکوس مانیتایزیشن! 🎯",
                    message = "تنها $remainingSubs سابسکرایبر دیگر تا دستیابی به شرایط اصلی درآمدزایی باقی مانده است.",
                    type = AlertType.MILESTONE_NEAR,
                    timestamp = "امروز",
                    actionLabel = "بررسی مرکز درآمدزایی"
                )
            )
        }

        alerts.add(
            SmartAlert(
                id = "alt_retention",
                title = "سیگنال واچ‌تایم صعودی",
                message = "میانگین مدت تماشای مخاطبان (AVD) این هفته به ۴ دقیقه و ۱۲ ثانیه ارتقا یافته است.",
                type = AlertType.RECOMMENDATION,
                timestamp = "دیروز",
                actionLabel = "مشاهده آنالیتیکس"
            )
        )

        return alerts
    }

    /**
     * Module 29 & 1: "امروز چه کاری انجام بدهم؟" Daily smart suggestions
     */
    fun getDailySmartActionItems(channel: ChannelProfile, stats: ChannelStatsOverview): List<String> {
        return listOf(
            "پاسخ به ۳ کامنت برتر ویدیوی اخیر برای تحریک تعامل الگوریتمی و ارسال سیگنال فعالیت",
            "طراحی ۲ کاور آلترناتیو برای ویدیوی هفته گذشته با تست کنتراست زرد و مشکی",
            "ثبت ایده یک ویدیوی شورتس در تقویم محتوایی با موضوع ترند روز",
            "بررسی تب رقبا در استودیو برای شناسایی ویدیوهای پربازدید ۲۴ ساعت گذشته در حوزه ${channel.category}"
        )
    }

    /**
     * Module 20: Smart Publishing
     */
    fun getBestPublishingSchedule(): List<Pair<String, String>> {
        return listOf(
            "شنبه" to "ساعت ۱۹:۰۰ الی ۲۱:۳۰ (اوج ترافیک عصرگاهی)",
            "دوشنبه" to "ساعت ۱۷:۳۰ الی ۱۹:۰۰ (مناسب ویدیوهای آموزشی)",
            "چهارشنبه" to "ساعت ۱۸:۰۰ الی ۲۱:۰۰ (آمادگی آخر هفته)",
            "پنجشنبه" to "ساعت ۱۶:۰۰ الی ۲۲:۰۰ (پربازدیدترین روز هفته)",
            "جمعه" to "ساعت ۱۱:۰۰ صبح و ۱۸:۰۰ عصر (مناسب لانگ ویدیو)"
        )
    }
}

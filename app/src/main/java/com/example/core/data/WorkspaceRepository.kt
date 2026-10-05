package com.example.core.data

import com.example.core.database.DatabaseModule
import com.example.core.database.entity.GoalEntity
import com.example.core.database.entity.ScriptDraftEntity
import com.example.core.database.entity.WorkspaceItemEntity
import com.example.core.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkspaceRepository private constructor() {

    companion object {
        val instance: WorkspaceRepository by lazy { WorkspaceRepository() }
    }

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    // Pipeline Workspace Items (Ideas -> Scripts -> Recording -> Editing -> Thumbnail -> Scheduled -> Published)
    private val _pipelineItems = MutableStateFlow(createInitialPipeline())
    val pipelineItems: StateFlow<List<WorkspaceItem>> = _pipelineItems.asStateFlow()

    // Content Ideas
    private val _contentIdeas = MutableStateFlow(createInitialIdeas())
    val contentIdeas: StateFlow<List<ContentIdea>> = _contentIdeas.asStateFlow()

    // Experiments A/B
    private val _experiments = MutableStateFlow(createInitialExperiments())
    val experiments: StateFlow<List<ExperimentItem>> = _experiments.asStateFlow()

    // Gamification
    private val _creatorXp = MutableStateFlow(1850)
    val creatorXp: StateFlow<Int> = _creatorXp.asStateFlow()

    private val _currentStreak = MutableStateFlow(12)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    private val _achievements = MutableStateFlow(createInitialAchievements())
    val achievements: StateFlow<List<CreatorAchievement>> = _achievements.asStateFlow()

    // Goals
    private val _goals = MutableStateFlow(createInitialGoals())
    val goals: StateFlow<List<CreatorGoal>> = _goals.asStateFlow()

    // 90 Day Strategy
    private val _strategyPhases = MutableStateFlow(createInitialStrategy())
    val strategyPhases: StateFlow<List<GrowthStrategyPhase>> = _strategyPhases.asStateFlow()

    // Scripts
    private val _scripts = MutableStateFlow(createInitialScripts())
    val scripts: StateFlow<List<ScriptDraft>> = _scripts.asStateFlow()

    fun updateItemStage(itemId: String, newStage: PipelineStage) {
        val updated = _pipelineItems.value.map {
            if (it.id == itemId) it.copy(stage = newStage) else it
        }
        _pipelineItems.value = updated
        addXp(50)

        // Persist update to Room
        repositoryScope.launch {
            try {
                DatabaseModule.workspaceDao?.updateStage(itemId, newStage.name)
            } catch (e: Exception) {
                // Room update fallback
            }
        }
    }

    fun addPipelineItem(title: String, stage: PipelineStage, type: ContentType, date: String) {
        val newItem = WorkspaceItem(
            id = "pipe_${System.currentTimeMillis()}",
            title = title,
            stage = stage,
            type = type,
            deadline = date,
            targetPublishDate = date
        )
        _pipelineItems.value = listOf(newItem) + _pipelineItems.value
        addXp(25)

        // Persist insert to Room
        repositoryScope.launch {
            try {
                DatabaseModule.workspaceDao?.insertItem(
                    WorkspaceItemEntity(
                        id = newItem.id,
                        title = newItem.title,
                        stage = newItem.stage.name,
                        type = newItem.type.name,
                        dueDate = newItem.deadline,
                        targetPublishDate = newItem.targetPublishDate,
                        notes = newItem.notes,
                        tags = newItem.tags.joinToString(",")
                    )
                )
            } catch (e: Exception) {
                // Room insert fallback
            }
        }
    }

    fun addIdea(idea: ContentIdea) {
        _contentIdeas.value = listOf(idea) + _contentIdeas.value
        addXp(30)
    }

    fun addXp(amount: Int) {
        _creatorXp.value += amount
    }

    fun createExperiment(title: String, type: ExperimentType, varA: String, varB: String) {
        val exp = ExperimentItem(
            id = "exp_${System.currentTimeMillis()}",
            title = title,
            testType = type,
            variantA = varA,
            variantB = varB,
            durationDays = 7,
            resultVariantAWins = null,
            winnerMetricSummary = "در حال جمع‌آوری دیتا...",
            status = "در حال اجرا"
        )
        _experiments.value = listOf(exp) + _experiments.value
        addXp(40)
    }

    fun saveScript(script: ScriptDraft) {
        _scripts.value = listOf(script) + _scripts.value.filter { it.id != script.id }
        addXp(60)

        // Persist script to Room
        repositoryScope.launch {
            try {
                DatabaseModule.scriptDao?.insertScript(
                    ScriptDraftEntity(
                        id = script.id,
                        title = script.title,
                        topic = script.topic,
                        hook = script.hook,
                        intro = script.intro,
                        mainContentJson = script.mainBody.joinToString(";") { "${it.title}|${it.durationSeconds}|${it.speakerNote}|${it.visualCue}" },
                        storyElement = script.storyElement,
                        cta = script.cta,
                        outro = script.outro,
                        estimatedMinutes = script.estimatedMinutes
                    )
                )
            } catch (e: Exception) {
                // Room insert fallback
            }
        }
    }

    private fun createInitialPipeline(): List<WorkspaceItem> {
        return listOf(
            WorkspaceItem(
                id = "pipe_1",
                title = "چگونه با ابزارهای AI در یک روز ۱۰ ویدیو بسازیم؟",
                stage = PipelineStage.SCRIPT,
                type = ContentType.LONG_VIDEO,
                deadline = "فردا",
                targetPublishDate = "پنجشنبه ساعت ۱۸:۳۰",
                notes = "معرفی Claude، Midjourney و ابزارهای برش شورتس",
                tags = listOf("AI", "تولید محتوا", "اتوماسیون")
            ),
            WorkspaceItem(
                id = "pipe_2",
                title = "ترفند پنهان یوتیوب استودیو برای دیدن ترافیک مخفی",
                stage = PipelineStage.RECORDING,
                type = ContentType.SHORTS,
                deadline = "امروز",
                targetPublishDate = "امروز ساعت ۲۰:۰۰",
                notes = "ضبط مستقیم از روی صفحه لپ‌تاپ با وب‌کم گوشه تصویر",
                tags = listOf("Shorts", "یوتیوب")
            ),
            WorkspaceItem(
                id = "pipe_3",
                title = "بررسی کامل یوتیوب ادسنس و افتتاح حساب ارزی",
                stage = PipelineStage.EDITING,
                type = ContentType.LONG_VIDEO,
                deadline = "۳ روز دیگر",
                targetPublishDate = "جمعه ساعت ۱۹:۰۰",
                notes = "اضافه کردن گرافیک موشن و کاور با تضاد رنگی قرمز-سبز",
                tags = listOf("درآمدزایی", "ادسنس")
            ),
            WorkspaceItem(
                id = "pipe_4",
                title = "معرفی ۵ وب‌سایت رایگان برای ادیتورهای ویدیو",
                stage = PipelineStage.IDEA,
                type = ContentType.TUTORIAL,
                deadline = "هفته بعد",
                targetPublishDate = "دوشنبه بعد",
                notes = "سایت‌های دانلود افکت صوتی، B-roll و فونت فارسی",
                tags = listOf("ابزار", "ادیت")
            )
        )
    }

    private fun createInitialIdeas(): List<ContentIdea> {
        return listOf(
            ContentIdea(
                id = "idea_1",
                title = "من ۱ ماه روزی ۱ شورتس گذاشتم؛ نتیجه عجیب بود!",
                type = ContentType.SHORTS,
                hook = "همه میگن شورتس کانالتو نابود می‌کنه! ولی آمار کانالم چیز دیگه‌ای نشون میده...",
                outline = listOf("روز ۱ تا ۱۰: هیچ خبری از ویو نیست", "روز ۱۵: یک ویدیو ۲۰۰K بازدید گرفت!", "نتیجه روی واچ‌تایم و سابسکرایبر واقعی"),
                thumbnailConcept = "تقسیم صفحه به دو نیم: نمودار قرمز سقوط vs نمودار سبز صعودی با چهره متعجب",
                callToAction = "اگه می‌خوای فایل قالب شورتس‌هام رو بگیری کامنت بذار «قالب»",
                targetAudience = "یوتیوبرهای تازه‌کار و کنجکاو",
                estimatedCompetition = "متوسط",
                potentialScore = 94
            ),
            ContentIdea(
                id = "idea_2",
                title = "راهنمای صفر تا صد مانیتایز یوتیوب در ایران (آپدیت جدید)",
                type = ContentType.EVERGREEN,
                hook = "اگر فکر می‌کنی به خاطر تحریم نمی‌تونی پولت رو بگیری، این ۳ دقیقه رو ببین!",
                outline = listOf("شرایط جدید ۴۰۰۰ ساعت", "نحوه ثبت حساب بانکی امن", "شرکت‌های واسط MCN یا اکانت اختصاصی؟", "مالیات یوتیوب و کسر درصدها"),
                thumbnailConcept = "لوگوی یوتیوب متصل به دلار نقد و پاسپورت با تگ «تضمینی»",
                callToAction = "سابسکرایب کن چون پارت دوم رفع ارور ادسنس رو فردا می‌ذارم",
                targetAudience = "تمام فعالان یوتیوب فارسی",
                estimatedCompetition = "پایین",
                potentialScore = 98
            ),
            ContentIdea(
                id = "idea_3",
                title = "کامل‌ترین مقایسه ChatGPT و Gemini برای نوشتن سناریوی یوتیوب",
                type = ContentType.TUTORIAL,
                hook = "یک پرامپت یکسان دادم به هردو هوش مصنوعی؛ ببینید کدوم ویدیوم رو وایرال کرد!",
                outline = listOf("تست پرامپت هوک", "تست سناریونویسی شوخ‌طبعانه", "تولید تایتل با CTR بالا", "نتیجه‌گیری نهایی"),
                thumbnailConcept = "مسابقه بوکس بین ربات جمینای گوگل و چت‌جی‌پی‌تی",
                callToAction = "تو کدوم رو ترجیح میدی؟ تو کامنت بنویس",
                targetAudience = "تولیدکنندگان محتوا و علاقه‌مندان به AI",
                estimatedCompetition = "کم‌رقابت و پرتقاضا",
                potentialScore = 91
            )
        )
    }

    private fun createInitialExperiments(): List<ExperimentItem> {
        return listOf(
            ExperimentItem(
                id = "exp_1",
                title = "تست کاور زرد-مشکی در برابر کاور قرمز-سفید",
                testType = ExperimentType.THUMBNAIL,
                variantA = "کاور زرد پرانرژی با ۳ کلمه درشت",
                variantB = "کاور رسمی قرمز یوتیوب با اسکرین‌شات لپ‌تاپ",
                durationDays = 7,
                resultVariantAWins = true,
                winnerMetricSummary = "کاور A نرخ کلیک (CTR) را ۲.۴٪ بالاتر برد (+۳۴٪ ایمپرشن موثر)",
                status = "پایان‌یافته"
            ),
            ExperimentItem(
                id = "exp_2",
                title = "تست تیتر سؤالی کنجکاوی‌برانگیز vs تیتر آموزشی مستقیم",
                testType = ExperimentType.TITLE,
                variantA = "آیا این هوش مصنوعی جای برنامه‌نویس‌ها رو می‌گیره؟",
                variantB = "آموزش کامل ساخت پروژه هوش مصنوعی از صفر",
                durationDays = 5,
                resultVariantAWins = true,
                winnerMetricSummary = "تیتر سؤالی ۶۴٪ ویو ارگانیک بیشتری جذب کرد",
                status = "در حال اجرا"
            )
        )
    }

    private fun createInitialAchievements(): List<CreatorAchievement> {
        return listOf(
            CreatorAchievement(
                id = "ach_1",
                title = "اولین قدم استوار",
                description = "بارگذاری و تحلیل اولین ویدیو در استودیو",
                iconEmoji = "🚀",
                isUnlocked = true,
                progress = 1.0f,
                xpReward = 100
            ),
            CreatorAchievement(
                id = "ach_2",
                title = "استاد ریتنشن",
                description = "رسیدن به میانگین تماشای بالای ۵۰٪ در یک ویدیو",
                iconEmoji = "⏱️",
                isUnlocked = true,
                progress = 1.0f,
                xpReward = 250
            ),
            CreatorAchievement(
                id = "ach_3",
                title = "ماشین تولید شورتس",
                description = "انتشار و پیگیری ۱۰ ویدیوی شورتس در تقویم محتوا",
                iconEmoji = "⚡",
                isUnlocked = false,
                progress = 0.6f,
                xpReward = 300
            ),
            CreatorAchievement(
                id = "ach_4",
                title = "کلاب ۱۰۰۰ سابسکرایبر 🏆",
                description = "رسیدن به مرز طلایی ۱۰۰۰ سابسکرایبر برای مانیتایزیشن",
                iconEmoji = "👑",
                isUnlocked = false,
                progress = 0.88f,
                xpReward = 1000
            )
        )
    }

    private fun createInitialGoals(): List<CreatorGoal> {
        return listOf(
            CreatorGoal(
                id = "goal_1",
                title = "رسیدن به ۱,۰۰۰ سابسکرایبر",
                current = 880,
                target = 1000,
                unit = "نفر",
                deadline = "تا پایان این ماه"
            ),
            CreatorGoal(
                id = "goal_2",
                title = "۴,۰۰۰ ساعت واچ‌تایم سالانه",
                current = 3420,
                target = 4000,
                unit = "ساعت",
                deadline = "تا ۴۵ روز آینده"
            ),
            CreatorGoal(
                id = "goal_3",
                title = "تولید ۱۲ ویدیوی عمیق و باکیفیت",
                current = 8,
                target = 12,
                unit = "ویدیو",
                deadline = "فصل جاری"
            )
        )
    }

    private fun createInitialStrategy(): List<GrowthStrategyPhase> {
        return listOf(
            GrowthStrategyPhase(
                phaseTitle = "فاز ۱: تثبیت هوک و اصلاح CTR (روز ۱ تا ۷)",
                timeline = "هفته اول",
                focusArea = "بصری و اولین ۵ ثانیه",
                actionItems = listOf(
                    "بازطراحی تامبنیل‌های ۵ ویدیوی پرطرفدار با پالت رنگی ۳ تایی با کنتراست شدید",
                    "تدوین هوک بدون مقدمه: حذف سلام و ورود فوری به موضوع اصلی",
                    "انتشار ۲ شورتس مرتبط با موضوع اصلی برای تست ترجیح مخاطب"
                ),
                expectedOutcome = "ارتقای CTR میانگین کانال از ۶.۸٪ به بالای ۸.۲٪"
            ),
            GrowthStrategyPhase(
                phaseTitle = "فاز ۲: سریال‌سازی و واچ‌تایم تجمعی (هفته ۲ تا ۴)",
                timeline = "ماه اول",
                focusArea = "طراحی سری محتوایی و End Screen",
                actionItems = listOf(
                    "شروع یک مینی سریال ۳ قسمتی با وعده جذاب در پایان هر قسمت",
                    "تنظیم هوشمندانه End Screen و پلی‌لیست برای تماشای متوالی ویدیوها",
                    "استفاده از سیستم کامیونیتی پست با نظرسنجی روز قبل از انتشار"
                ),
                expectedOutcome = "افزایش میانگین تماشای هر بیننده به ۲.۳ ویدیو در هر نشست"
            ),
            GrowthStrategyPhase(
                phaseTitle = "فاز ۳: هجوم به مانیتایز و پایدارسازی جامعه (ماه ۲)",
                timeline = "ماه دوم",
                focusArea = "تکمیل ۱۰۰۰ سابسکرایبر و ۴۰۰۰ ساعت",
                actionItems = listOf(
                    "برگزاری ۱ لایو استریم پرسش و پاسخ تخصصی جهت تولید واچ‌تایم خالص",
                    "همکاری مشترک یا ریکشن به یک ویدیوی تاپ حوزه کاری",
                    "آماده‌سازی مدارک افتتاح حساب ادسنس و پاسپورت"
                ),
                expectedOutcome = "عبور تضمینی از ۱۰۰۰ سابسکرایبر و واجد شرایط شدن ادسنس"
            ),
            GrowthStrategyPhase(
                phaseTitle = "فاز ۴: مقیاس‌پذیری و جریان‌های درآمد جانبی (ماه ۳)",
                timeline = "ماه سوم",
                focusArea = "اسپانسری، افیلیت و عضویت کانال (Memberships)",
                actionItems = listOf(
                    "طراحی پکیج اسپانسرینگ اولیه برای برندهای نرم‌افزاری و خدماتی",
                    "فعال‌سازی دکمه Thanks و Join با مزایای اختصاصی",
                    "اتوماسیون پروسه تحقیق تا تدوین با چک‌لیست‌های استودیو"
                ),
                expectedOutcome = "دستیابی به درآمد پایدار حداقل ۳۰۰ تا ۵۰۰ دلار ماهانه"
            )
        )
    }

    private fun createInitialScripts(): List<ScriptDraft> {
        return listOf(
            ScriptDraft(
                id = "scr_1",
                title = "چگونه با ابزارهای AI در یک روز ۱۰ ویدیو بسازیم؟",
                topic = "اتوماسیون تولید محتوای هوشمند",
                hook = "اگر هنوز روزی ۴ ساعت پای ادیت می‌نشینید، این ویدیو شیوه کار شما را برای همیشه تغییر خواهد داد.",
                intro = "سلام دوستان! امروز می‌خواهم ۳ ابزار هوش مصنوعی معرفی کنم که صفر تا صد سناریو، صداگذاری و برش را در چند دقیقه انجام می‌دهند.",
                mainBody = listOf(
                    ScriptSection(
                        title = "بخش ۱: ایده‌پردازی با ابزار پرامپت اختصاصی",
                        durationSeconds = 120,
                        speakerNote = "با هیجان صحبت کنید و صفحه لپ‌تاپ را با اسکرین‌کست نشان دهید.",
                        visualCue = "زوم روی خروجی جدول اکسل تولید شده"
                    ),
                    ScriptSection(
                        title = "بخش ۲: تبدیل متن به سناریوی انسانی",
                        durationSeconds = 180,
                        speakerNote = "تاکید کنید که خروجی خام هوش مصنوعی نباید کپی شود و نیاز به لحن شخصی دارد.",
                        visualCue = "نمایش مقایسه قبل و بعد متن با خط قرمز"
                    ),
                    ScriptSection(
                        title = "بخش ۳: ادیت و زیرنویس اتوماتیک",
                        durationSeconds = 180,
                        speakerNote = "یک تکه ویدیوی خام را در نرم‌افزار بندازید و خروجی زیرنویس فارسی انیمیشنی را نشان دهید.",
                        visualCue = "نمایش تایم‌لاین ادیت با سرعت بالا (Timelapse)"
                    )
                ),
                storyElement = "داستان کانالی که با همین سیستم در ۴۵ روز به ۱۰۰ هزار بازدید ماهانه رسید.",
                cta = "اگر می‌خواهید لیست ابزارها را به صورت PDF دانلود کنید، لینک در توضیحات اول قرار گرفته.",
                outro = "ویدیوی بعدی درباره نحوه نقد کردن درآمد است، حتماً سابسکرایب کنید!",
                estimatedMinutes = 8
            )
        )
    }
}

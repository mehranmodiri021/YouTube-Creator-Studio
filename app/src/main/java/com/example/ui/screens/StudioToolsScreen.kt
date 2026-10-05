package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.engine.AiIntelligenceService
import com.example.core.engine.ThumbnailAuditResult
import com.example.core.model.ContentIdea
import com.example.core.model.ContentType
import com.example.ui.components.SectionHeader
import com.example.ui.components.StudioGlassCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun StudioToolsScreen(
    ideas: List<ContentIdea>,
    onAddIdeaToPipeline: (ContentIdea) -> Unit,
    initialTab: Int = 0,
    modifier: Modifier = Modifier
) {
    var selectedToolTab by remember { mutableStateOf(initialTab) }
    val toolTabs = listOf("ایده‌ساز (Ideas)", "تایتل استودیو", "آزمایشگاه کاور", "سئو (SEO)", "ترندها و فرصت‌ها", "استودیو شورتس")

    var titleInput by remember { mutableStateOf("ساخت اپلیکیشن با هوش مصنوعی") }
    val titleVariants = remember(titleInput) {
        AiIntelligenceService.generateTitleVariants(titleInput)
    }

    var seoKeywordInput by remember { mutableStateOf("آموزش یوتیوب فارسی") }
    val seoResult = remember(seoKeywordInput) {
        AiIntelligenceService.analyzeSeo(seoKeywordInput)
    }

    var shortsTopicInput by remember { mutableStateOf("ترفندهای یوتیوب") }
    val shortsHooks = remember(shortsTopicInput) {
        AiIntelligenceService.generateShortsHooks(shortsTopicInput)
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var isAnalyzingThumbnail by remember { mutableStateOf(false) }
    var customThumbnailAudit by remember { mutableStateOf<ThumbnailAuditResult?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            isAnalyzingThumbnail = true
            coroutineScope.launch {
                try {
                    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    if (bytes != null) {
                        customThumbnailAudit = AiIntelligenceService.analyzeThumbnailImage(bytes, titleInput)
                    }
                } catch (e: Exception) {
                    // Fallback
                } finally {
                    isAnalyzingThumbnail = false
                }
            }
        }
    }

    val defaultThumbnailAudit = remember(titleInput) {
        AiIntelligenceService.auditThumbnailConcept(titleInput, "کاور با پس‌زمینه دارک و لوگو")
    }

    val activeThumbnailAudit = customThumbnailAudit ?: defaultThumbnailAudit

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab Selector Row
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedToolTab,
                containerColor = StudioSurface,
                contentColor = YouTubeRed,
                edgePadding = 0.dp,
                divider = {},
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, StudioBorder, RoundedCornerShape(12.dp))
            ) {
                toolTabs.forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedToolTab == idx,
                        onClick = { selectedToolTab = idx },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedToolTab == idx) YouTubeRed else TextSecondary,
                                fontWeight = if (selectedToolTab == idx) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }
        }

        when (selectedToolTab) {
            0 -> {
                // Module 10: Content Idea Engine
                item {
                    SectionHeader(
                        title = "موتور تولید ایده محتوا (Content Idea Engine)",
                        subtitle = "ایده‌های داغ، آموزشی، سریالی و شورتس همراه با هوک و ساختار کامل",
                        icon = { Icon(Icons.Default.Lightbulb, contentDescription = null, tint = WarningAmber) }
                    )
                }

                items(ideas) { idea ->
                    StudioGlassCard(
                        modifier = Modifier.fillMaxWidth().testTag("idea_card_${idea.id}"),
                        borderColor = MetricCyan.copy(alpha = 0.3f)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(YouTubeRed.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = when (idea.type) {
                                            ContentType.SHORTS -> "⚡ Shorts ایده"
                                            ContentType.EVERGREEN -> "🌲 همیشه‌سبز (Evergreen)"
                                            ContentType.TUTORIAL -> "🎓 آموزشی عمیق"
                                            else -> "🎬 Long Video"
                                        },
                                        color = YouTubeRed,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "پتانسیل: ", color = TextMuted, fontSize = 11.sp)
                                    Text(text = "${idea.potentialScore}٪", color = GrowthGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = idea.title,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Hook Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(StudioCardHover)
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(text = "🎯 قلاب ۵ ثانیه اول (Hook):", color = WarningAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = idea.hook, color = TextPrimary, fontSize = 12.sp, lineHeight = 18.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = "📌 ساختار ویدیو:", color = TextMuted, fontSize = 11.sp)
                            idea.outline.forEach { step ->
                                Text(text = "• $step", color = TextSecondary, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { onAddIdeaToPipeline(idea) },
                                colors = ButtonDefaults.buttonColors(containerColor = StudioCardHover),
                                modifier = Modifier.border(1.dp, GrowthGreen, RoundedCornerShape(10.dp))
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = GrowthGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "افزودن به پایپ‌لاین تولید محتوا", color = GrowthGreen, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            1 -> {
                // Module 7: AI Title Studio
                item {
                    SectionHeader(
                        title = "استودیوی بازنویسی و تولید عنوان (Title Studio)",
                        subtitle = "تولید تیترهای با کلیک‌خور بالا (CTR)، سئو، کنجکاوی و امتیازدهی",
                        icon = { Icon(Icons.Default.EditNote, contentDescription = null, tint = MetricCyan) }
                    )

                    StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "موضوع یا تایتل فعلی خود را وارد کنید:", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = titleInput,
                                onValueChange = { titleInput = it },
                                modifier = Modifier.fillMaxWidth().testTag("title_input_field"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = StudioCardHover,
                                    unfocusedContainerColor = StudioCardHover,
                                    focusedBorderColor = YouTubeRed,
                                    unfocusedBorderColor = StudioBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }
                    }
                }

                items(titleVariants) { variant ->
                    StudioGlassCard(modifier = Modifier.fillMaxWidth(), borderColor = YouTubeRed.copy(alpha = 0.25f)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MetricCyan.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = variant.type, color = MetricCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = variant.title,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = variant.highlight, color = TextMuted, fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(GrowthGreen.copy(alpha = 0.15f))
                                    .border(1.5.dp, GrowthGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "${variant.score}", color = GrowthGreen, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }

            2 -> {
                // Module 8: Thumbnail Lab
                item {
                    SectionHeader(
                        title = "آزمایشگاه کاور و تامبنیل (Thumbnail Lab)",
                        subtitle = "آنالیز ترکیب‌بندی، نسبت ۱۶:۹ و ۹:۱۶ شورتس، رنگ‌ها و خوانایی متن",
                        icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = AiPurple) }
                    )

                    // Upload Action Card
                    StudioGlassCard(modifier = Modifier.fillMaxWidth(), borderColor = AiPurple.copy(alpha = 0.3f)) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "تحلیل هوشمند تصویر با هوش مصنوعی (Vision)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "عکس کاور مورد نظر را آپلود کنید تا کنتراست، وضوح و CTR آن ممیزی شود", color = TextMuted, fontSize = 11.sp)
                                }
                                Button(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AiPurple),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "انتخاب کاور", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (selectedImageUri != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, StudioBorder, RoundedCornerShape(12.dp))
                                ) {
                                    AsyncImage(
                                        model = selectedImageUri,
                                        contentDescription = "کاور انتخاب شده",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    if (isAnalyzingThumbnail) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.6f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                CircularProgressIndicator(color = AiPurple, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(text = "در حال تحلیل بینایی هوش مصنوعی...", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    StudioGlassCard(modifier = Modifier.fillMaxWidth(), borderColor = AiPurple.copy(alpha = 0.4f)) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "نمره جذابیت بصری کاور", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "${activeThumbnailAudit.score} از ۱۰۰", color = GrowthGreen, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "کنتراست و شفافیت:", color = TextSecondary, fontSize = 12.sp)
                                Text(text = activeThumbnailAudit.contrastGrade, color = MetricCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "خوانایی متن روی موبایل:", color = TextSecondary, fontSize = 12.sp)
                                Text(text = activeThumbnailAudit.textReadability, color = GrowthGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(text = "پالت رنگی با بالاترین بازخورد روانی:", color = TextMuted, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                activeThumbnailAudit.colorPalette.forEach { hex ->
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(android.graphics.Color.parseColor(hex)))
                                            .border(1.dp, StudioBorder, RoundedCornerShape(8.dp))
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(text = "📌 توصیه‌های کلیدی اصلاح کاور:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            activeThumbnailAudit.improvementSuggestions.forEach { sug ->
                                Text(text = "• $sug", color = TextSecondary, fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }
                    }
                }
            }

            3 -> {
                // Module 9: SEO Studio
                item {
                    SectionHeader(
                        title = "استودیوی سئو و کلیدواژه (SEO Studio)",
                        subtitle = "تحقیق کلمات کلیدی، قصد جستجو، چگالی کلمات و برچسب‌ها",
                        icon = { Icon(Icons.Default.Search, contentDescription = null, tint = MetricCyan) }
                    )

                    StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "کلمه کلیدی هدف را جستجو کنید:", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = seoKeywordInput,
                                onValueChange = { seoKeywordInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = StudioCardHover,
                                    unfocusedContainerColor = StudioCardHover,
                                    focusedBorderColor = MetricCyan,
                                    unfocusedBorderColor = StudioBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }
                    }
                }

                item {
                    StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "نمره کل سئو (SEO Score)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "${seoResult.overallSeoScore}/100", color = GrowthGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = "حجم جستجو: ${seoResult.searchVolume}", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "سطح رقابت: ${seoResult.competition}", color = WarningAmber, fontSize = 12.sp)
                            Text(text = "قصد مخاطب: ${seoResult.searchIntent}", color = TextMuted, fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(text = "تگ‌ها و کلیدواژه‌های پیشنهادی برای دیسکریپشن:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                seoResult.tagSuggestions.take(4).forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(StudioCardHover)
                                            .border(1.dp, MetricCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = "#$tag", color = MetricCyan, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            4 -> {
                // Module 11: Trend & Opportunity Center
                item {
                    SectionHeader(
                        title = "مرکز ترندها و شکاف محتوایی (Trends & Content Gap)",
                        subtitle = "الان درباره چه چیزی ویدیو بسازم؟",
                        icon = { Icon(Icons.Default.Whatshot, contentDescription = null, tint = YouTubeRed) }
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        StudioGlassCard(modifier = Modifier.fillMaxWidth(), borderColor = YouTubeRed.copy(alpha = 0.4f)) {
                            Column {
                                Text(text = "🔥 ترند داغ ۲۴ ساعت اخیر فارسی:", color = YouTubeRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "«آپدیت جدید هوش مصنوعی و جایگزینی کارها» بیش از ۶۰K سرچ ثبت کرده در حالی که کمتر از ۱۰ کانال ویدیوی اختصاصی ساخته‌اند.",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        StudioGlassCard(modifier = Modifier.fillMaxWidth(), borderColor = GrowthGreen.copy(alpha = 0.4f)) {
                            Column {
                                Text(text = "💡 حوزه کم‌رقابت با ارزش دلاری بالا:", color = GrowthGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "نقد و بررسی نرم‌افزارهای مالی و ترید دارای بالاترین RPM ($4.50+) در میان مخاطبان فارسی است و بینندگان وفاداری دارد.",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            5 -> {
                // Module 12: Shorts Studio
                item {
                    SectionHeader(
                        title = "استودیوی اختصاصی شورتس (Shorts Studio)",
                        subtitle = "قلاب‌های انفجاری، متن و سناریوی وایرال برای محتوای کوتاه عمودی",
                        icon = { Icon(Icons.Default.FlashOn, contentDescription = null, tint = WarningAmber) }
                    )

                    StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "موضوع شورتس را بنویسید تا هوک انفجاری بسازیم:", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = shortsTopicInput,
                                onValueChange = { shortsTopicInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = StudioCardHover,
                                    unfocusedContainerColor = StudioCardHover,
                                    focusedBorderColor = WarningAmber,
                                    unfocusedBorderColor = StudioBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }
                    }
                }

                items(shortsHooks) { hook ->
                    StudioGlassCard(modifier = Modifier.fillMaxWidth(), borderColor = WarningAmber.copy(alpha = 0.3f)) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "هوک ۲ ثانیه‌ای برای فید شورتس:", color = WarningAmber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = hook, color = TextPrimary, fontSize = 13.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

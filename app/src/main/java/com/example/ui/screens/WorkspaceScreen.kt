package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.*
import com.example.ui.components.SectionHeader
import com.example.ui.components.StudioGlassCard
import com.example.ui.theme.*

@Composable
fun WorkspaceScreen(
    pipelineItems: List<WorkspaceItem>,
    scripts: List<ScriptDraft>,
    experiments: List<ExperimentItem>,
    onUpdateStage: (String, PipelineStage) -> Unit,
    onAddNewPipelineItem: (String, PipelineStage, ContentType, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var workspaceTab by remember { mutableStateOf(0) } // 0: Kanban Pipeline, 1: Script Studio & Teleprompter, 2: A/B Experiments, 3: Content Library

    var showAddItemDialog by remember { mutableStateOf(false) }
    var newItemTitle by remember { mutableStateOf("") }
    var newItemDate by remember { mutableStateOf("پنجشنبه ساعت ۱۸:۰۰") }

    // Teleprompter state
    var isTeleprompterActive by remember { mutableStateOf(false) }
    var teleprompterSpeed by remember { mutableFloatStateOf(1.0f) }
    var teleprompterFontSize by remember { mutableFloatStateOf(18.0f) }

    val activeScript = scripts.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab switcher
        item {
            ScrollableTabRow(
                selectedTabIndex = workspaceTab,
                containerColor = StudioSurface,
                contentColor = YouTubeRed,
                edgePadding = 0.dp,
                divider = {},
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, StudioBorder, RoundedCornerShape(12.dp))
            ) {
                listOf("پایپ‌لاین تولید (Kanban)", "سناریونویس و تله‌پروامپتر", "آزمایشگاه A/B", "کتابخانه محتوا").forEachIndexed { idx, title ->
                    Tab(
                        selected = workspaceTab == idx,
                        onClick = { workspaceTab = idx },
                        text = {
                            Text(
                                text = title,
                                color = if (workspaceTab == idx) YouTubeRed else TextSecondary,
                                fontWeight = if (workspaceTab == idx) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }
        }

        when (workspaceTab) {
            0 -> {
                // Module 30: Creator Workspace Pipeline & Module 13: Content Planner
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionHeader(
                            title = "میزکار و خط تولید محتوا (Production Pipeline)",
                            subtitle = "مسیر تبدیل ایده به ویدیوی منتشر شده",
                            icon = { Icon(Icons.Default.ViewKanban, contentDescription = null, tint = MetricCyan) }
                        )

                        Button(
                            onClick = { showAddItemDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "محتوای جدید", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Kanban Columns / List
                items(pipelineItems) { item ->
                    StudioGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = when (item.stage) {
                            PipelineStage.IDEA -> MetricCyan.copy(alpha = 0.4f)
                            PipelineStage.SCRIPT -> WarningAmber.copy(alpha = 0.4f)
                            PipelineStage.RECORDING -> YouTubeRed.copy(alpha = 0.4f)
                            PipelineStage.EDITING -> AiPurple.copy(alpha = 0.4f)
                            PipelineStage.PUBLISHED -> GrowthGreen.copy(alpha = 0.4f)
                            else -> StudioBorder
                        }
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
                                        .background(StudioCardHover)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = when (item.stage) {
                                            PipelineStage.IDEA -> "💡 ۱. ایده خام"
                                            PipelineStage.SCRIPT -> "📝 ۲. نگارش سناریو"
                                            PipelineStage.RECORDING -> "🎙️ ۳. ضبط صدا و تصویر"
                                            PipelineStage.EDITING -> "✂️ ۴. تدوین و صداگذاری"
                                            PipelineStage.THUMBNAIL -> "🖼️ ۵. طراحی کاور"
                                            PipelineStage.SCHEDULED -> "⏰ ۶. زمان‌بندی شده"
                                            PipelineStage.PUBLISHED -> "✅ ۷. منتشر شده"
                                        },
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(text = item.targetPublishDate, color = TextMuted, fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = item.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                            if (item.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = item.notes, color = TextSecondary, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Stage Advancement Actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (item.stage != PipelineStage.PUBLISHED) {
                                    val nextStage = when (item.stage) {
                                        PipelineStage.IDEA -> PipelineStage.SCRIPT
                                        PipelineStage.SCRIPT -> PipelineStage.RECORDING
                                        PipelineStage.RECORDING -> PipelineStage.EDITING
                                        PipelineStage.EDITING -> PipelineStage.THUMBNAIL
                                        PipelineStage.THUMBNAIL -> PipelineStage.SCHEDULED
                                        PipelineStage.SCHEDULED -> PipelineStage.PUBLISHED
                                        else -> PipelineStage.PUBLISHED
                                    }

                                    Button(
                                        onClick = { onUpdateStage(item.id, nextStage) },
                                        colors = ButtonDefaults.buttonColors(containerColor = StudioCardHover),
                                        modifier = Modifier.border(1.dp, GrowthGreen, RoundedCornerShape(8.dp)),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = GrowthGreen, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "ارتقا به مرحله بعد (+50 XP)", color = GrowthGreen, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Module 14: Script Studio & Module 15: Teleprompter
                item {
                    SectionHeader(
                        title = "استودیوی سناریونویسی و تله‌پروامپتر",
                        subtitle = "نگارش حرفه‌ای هوک، بدنه، داستان و متن‌خوان روی دوربین",
                        icon = { Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = AiPurple) }
                    )

                    if (activeScript != null) {
                        StudioGlassCard(modifier = Modifier.fillMaxWidth(), borderColor = AiPurple.copy(alpha = 0.4f)) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = activeScript.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Button(
                                        onClick = { isTeleprompterActive = !isTeleprompterActive },
                                        colors = ButtonDefaults.buttonColors(containerColor = if (isTeleprompterActive) YouTubeRed else GrowthGreen),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(text = if (isTeleprompterActive) "بستن متن‌خوان" else "🎙️ حالت Teleprompter", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (isTeleprompterActive) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    // Teleprompter Controls
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "اندازه فونت: ${teleprompterFontSize.toInt()}sp", color = TextSecondary, fontSize = 11.sp)
                                        Slider(
                                            value = teleprompterFontSize,
                                            onValueChange = { teleprompterFontSize = it },
                                            valueRange = 14f..28f,
                                            modifier = Modifier.width(150.dp)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.Black)
                                            .border(1.dp, GrowthGreen, RoundedCornerShape(12.dp))
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = "${activeScript.hook}\n\n${activeScript.intro}\n\n" +
                                                    activeScript.mainBody.joinToString("\n\n") { "${it.title}: ${it.speakerNote}" } +
                                                    "\n\n${activeScript.cta}\n\n${activeScript.outro}",
                                            color = Color.White,
                                            fontSize = teleprompterFontSize.sp,
                                            lineHeight = (teleprompterFontSize * 1.5).sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(text = "🎯 هوک (Hook):", color = WarningAmber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(text = activeScript.hook, color = TextPrimary, fontSize = 13.sp)

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = "📢 مقدمه (Intro):", color = MetricCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(text = activeScript.intro, color = TextPrimary, fontSize = 13.sp)

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = "🎬 سرفصل‌های بدنه:", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    activeScript.mainBody.forEach { sec ->
                                        Text(text = "• ${sec.title} (${sec.durationSeconds}s) - نشانه بصری: ${sec.visualCue}", color = TextMuted, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Module 19: Experiment Center (A/B Testing)
                item {
                    SectionHeader(
                        title = "آزمایشگاه تست مقایسه‌ای (A/B Experiment Center)",
                        subtitle = "مقایسه علمی کاور، تیتر و هوک برای یافتن بالاترین نرخ کلیک",
                        icon = { Icon(Icons.Default.Science, contentDescription = null, tint = MetricCyan) }
                    )
                }

                items(experiments) { exp ->
                    StudioGlassCard(modifier = Modifier.fillMaxWidth(), borderColor = MetricCyan.copy(alpha = 0.3f)) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = exp.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (exp.status == "پایان‌یافته") GrowthGreen.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = exp.status,
                                        color = if (exp.status == "پایان‌یافته") GrowthGreen else WarningAmber,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(StudioCardHover)
                                        .border(1.dp, if (exp.resultVariantAWins == true) GrowthGreen else StudioBorder, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(text = "گزینه A:", color = MetricCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        Text(text = exp.variantA, color = TextPrimary, fontSize = 12.sp)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(StudioCardHover)
                                        .border(1.dp, if (exp.resultVariantAWins == false) GrowthGreen else StudioBorder, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(text = "گزینه B:", color = YouTubeRed, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        Text(text = exp.variantB, color = TextPrimary, fontSize = 12.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(text = "نتیجه آزمایش: ${exp.winnerMetricSummary}", color = GrowthGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            3 -> {
                // Module 35: Content Library
                item {
                    SectionHeader(
                        title = "کتابخانه محتوا (Content Library)",
                        subtitle = "آرشیو متمرکز پیش‌نویس‌ها، ایده‌ها، تحقیقات و اسکریپت‌ها",
                        icon = { Icon(Icons.Default.FolderSpecial, contentDescription = null, tint = WarningAmber) }
                    )

                    StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text(text = "📁 ایده‌های ذخیره شده: ۴ مورد", color = TextPrimary, fontSize = 13.sp)
                            Text(text = "📄 سناریوهای آماده ضبط: ۲ مورد", color = TextPrimary, fontSize = 13.sp)
                            Text(text = "🎨 الگوهای کاور و گرافیک: ۶ مورد", color = TextPrimary, fontSize = 13.sp)
                            Text(text = "🔍 تحقیقات کلمات کلیدی ذخیره شده: ۸ مورد", color = TextPrimary, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }

    if (showAddItemDialog) {
        AlertDialog(
            onDismissRequest = { showAddItemDialog = false },
            title = { Text(text = "افزودن محتوای جدید به پایپ‌لاین", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newItemTitle,
                        onValueChange = { newItemTitle = it },
                        placeholder = { Text("عنوان ویدیو یا ایده...", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = YouTubeRed
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newItemDate,
                        onValueChange = { newItemDate = it },
                        placeholder = { Text("زمان انتشار هدف...", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = YouTubeRed
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newItemTitle.isNotBlank()) {
                            onAddNewPipelineItem(newItemTitle, PipelineStage.IDEA, ContentType.LONG_VIDEO, newItemDate)
                            newItemTitle = ""
                            showAddItemDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed)
                ) {
                    Text("ثبت در پایپ‌لاین", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddItemDialog = false }) {
                    Text("انصراف", color = TextSecondary)
                }
            },
            containerColor = StudioCard
        )
    }
}

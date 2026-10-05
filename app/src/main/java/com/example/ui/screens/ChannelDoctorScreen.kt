package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.engine.RecommendationEngine
import com.example.core.model.*
import com.example.ui.components.SectionHeader
import com.example.ui.components.StudioGlassCard
import com.example.ui.components.formatNumberPersian
import com.example.ui.theme.*

@Composable
fun ChannelDoctorScreen(
    channel: ChannelProfile,
    stats: ChannelStatsOverview,
    videos: List<VideoItem>,
    xp: Int,
    streak: Int,
    achievements: List<CreatorAchievement>,
    goals: List<CreatorGoal>,
    strategyPhases: List<GrowthStrategyPhase>,
    onFixIssueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var doctorTab by remember { mutableStateOf(0) } // 0: Channel Doctor & Audit, 1: 90-Day Strategy, 2: Gamification & Goals, 3: Reports

    val doctorReport = remember(channel, stats, videos) {
        RecommendationEngine.runChannelDoctorDiagnosis(channel, stats, videos)
    }

    val opportunities = remember(videos) {
        RecommendationEngine.detectGrowthOpportunities(videos)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab row
        item {
            ScrollableTabRow(
                selectedTabIndex = doctorTab,
                containerColor = StudioSurface,
                contentColor = YouTubeRed,
                edgePadding = 0.dp,
                divider = {},
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, StudioBorder, RoundedCornerShape(12.dp))
            ) {
                listOf("پزشک کانال (Doctor)", "استراتژی ۹۰ روزه", "اهداف و امتیاز (Gamification)", "گزارش ماهانه").forEachIndexed { idx, title ->
                    Tab(
                        selected = doctorTab == idx,
                        onClick = { doctorTab = idx },
                        text = {
                            Text(
                                text = title,
                                color = if (doctorTab == idx) YouTubeRed else TextSecondary,
                                fontWeight = if (doctorTab == idx) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }
        }

        when (doctorTab) {
            0 -> {
                // Module 33: Channel Doctor & Module 34: Growth Opportunity Detector
                item {
                    // Doctor Health Hero
                    StudioGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = GrowthGreen.copy(alpha = 0.5f),
                        glowColor = GlowGreen
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(GrowthGreen.copy(alpha = 0.2f))
                                    .border(2.dp, GrowthGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${doctorReport.healthScore}٪",
                                    color = GrowthGreen,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "تشخیص جامع سلامت کانال (Channel Doctor)",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(text = "🔴 ${doctorReport.criticalIssues.size} بحرانی", color = CriticalRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "🟠 ${doctorReport.attentionIssues.size} نیازمند توجه", color = WarningAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "🟢 ${doctorReport.healthyAspects.size} عالی", color = GrowthGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // 1-Click Fix Plan
                item {
                    StudioGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = AiPurple.copy(alpha = 0.4f),
                        backgroundColor = StudioCardHover
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Healing, contentDescription = null, tint = AiPurple, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "برنامه اقدام و درمان کانال (Fix Plan):", color = AiPurple, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            doctorReport.fixPlanSteps.forEachIndexed { i, step ->
                                Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                    Text(text = "${i + 1}. ", color = AiPurple, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(text = step, color = TextPrimary, fontSize = 12.sp, lineHeight = 18.sp)
                                }
                            }
                        }
                    }
                }

                // Issues List
                item {
                    SectionHeader(
                        title = "جزییات مشکلات و نقاط قوت کانال",
                        subtitle = "تحلیل سیستماتیک الگوریتم یوتیوب",
                        icon = { Icon(Icons.Default.BugReport, contentDescription = null, tint = WarningAmber) }
                    )
                }

                items(doctorReport.criticalIssues + doctorReport.attentionIssues + doctorReport.healthyAspects) { issue ->
                    val color = when (issue.severity) {
                        WarningLevel.CRITICAL -> CriticalRed
                        WarningLevel.ATTENTION -> WarningAmber
                        WarningLevel.NONE -> GrowthGreen
                    }

                    StudioGlassCard(modifier = Modifier.fillMaxWidth(), borderColor = color.copy(alpha = 0.3f)) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = issue.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = issue.description, color = TextSecondary, fontSize = 12.sp, lineHeight = 18.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "💡 راهکار پیشنهادی: ${issue.recommendedAction}", color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Module 34: Growth Opportunities
                item {
                    SectionHeader(
                        title = "آشکارساز فرصت‌های پنهان رشد (Opportunity Detector)",
                        subtitle = "فرصت‌هایی که شاید خودتان متوجه آنها نشده‌اید",
                        icon = { Icon(Icons.Default.Radar, contentDescription = null, tint = MetricCyan) }
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        opportunities.forEach { opp ->
                            StudioGlassCard(modifier = Modifier.fillMaxWidth(), borderColor = MetricCyan.copy(alpha = 0.35f)) {
                                Text(text = opp, color = TextPrimary, fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }
                    }
                }
            }

            1 -> {
                // Module 32: AI Strategy Generator (90 Days)
                item {
                    SectionHeader(
                        title = "نقشه راه رشد ۹۰ روزه کانال (90-Day AI Roadmap)",
                        subtitle = "برنامه استراتژیک مرحله‌به‌مرحله با تطبیق پیوسته",
                        icon = { Icon(Icons.Default.Map, contentDescription = null, tint = GrowthGreen) }
                    )
                }

                items(strategyPhases) { phase ->
                    StudioGlassCard(modifier = Modifier.fillMaxWidth(), borderColor = GrowthGreen.copy(alpha = 0.3f)) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = phase.phaseTitle, color = GrowthGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(StudioCardHover)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(text = phase.timeline, color = TextSecondary, fontSize = 10.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "تمرکز اصلی: ${phase.focusArea}", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                            Spacer(modifier = Modifier.height(8.dp))
                            phase.actionItems.forEach { item ->
                                Text(text = "• $item", color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "🎯 نتیجه مورد انتظار: ${phase.expectedOutcome}", color = MetricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            2 -> {
                // Module 24: Gamification & Module 23: Goals & Milestones
                item {
                    StudioGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = WarningAmber.copy(alpha = 0.4f),
                        glowColor = GlowPurple
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "سطح یوتیوبر (Level)", color = TextMuted, fontSize = 11.sp)
                                Text(text = "سطح ${xp / 500 + 1}", color = WarningAmber, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                                Text(text = "$xp XP تجمعی", color = TextSecondary, fontSize = 11.sp)
                            }

                            Box(modifier = Modifier.width(1.dp).height(50.dp).background(StudioBorder))

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "استریک انتشار (Streak)", color = TextMuted, fontSize = 11.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🔥 $streak", color = YouTubeRed, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                                }
                                Text(text = "روز مداوم در استودیو", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Goals
                item {
                    SectionHeader(
                        title = "اهداف و مایلسون‌های من (Goals & Milestones)",
                        subtitle = "مسیر پیشرفت تا اهداف بعدی سابسکرایبر و واچ‌تایم",
                        icon = { Icon(Icons.Default.Flag, contentDescription = null, tint = MetricCyan) }
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        goals.forEach { g ->
                            StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = g.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = "${g.current} / ${g.target} ${g.unit}", color = GrowthGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { (g.current.toFloat() / g.target.toFloat()).coerceIn(0f, 1f) },
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                        color = GrowthGreen,
                                        trackColor = StudioCardHover
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "مهلت هدف: ${g.deadline}", color = TextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                // Achievements
                item {
                    SectionHeader(
                        title = "مدال‌ها و دستاوردهای باز شده (Achievements)",
                        subtitle = "پاداش تلاش و رشد پیوسته کانال",
                        icon = { Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = WarningAmber) }
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        achievements.forEach { ach ->
                            StudioGlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = if (ach.isUnlocked) WarningAmber.copy(alpha = 0.4f) else StudioBorder
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = ach.iconEmoji, fontSize = 28.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = ach.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = ach.description, color = TextSecondary, fontSize = 11.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (ach.isUnlocked) GrowthGreen.copy(alpha = 0.2f) else StudioCardHover)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (ach.isUnlocked) "باز شده ✓" else "+${ach.xpReward} XP",
                                            color = if (ach.isUnlocked) GrowthGreen else TextMuted,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // Module 22: Weekly / Monthly Reports
                item {
                    SectionHeader(
                        title = "گزارش تحلیلی ماهانه استودیو (Executive Report)",
                        subtitle = "خلاصه خودکار عملکرد، رشد، افت‌ها و برنامه هفته بعد",
                        icon = { Icon(Icons.Default.Assessment, contentDescription = null, tint = MetricCyan) }
                    )

                    StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text(text = "📊 گزارش ماهانه کانال «${channel.title}»", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "• کل بازدید جدید: ${formatNumberPersian(stats.views)} (+${stats.viewsDeltaPercent}% رشد نسبت به ماه قبل)", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "• سابسکرایبر جدید اضافه شده: +${channel.monthlyGrowth} نفر", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "• ساعات واچ‌تایم تجمعی: ${stats.watchTimeHours.toInt()} ساعت", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "• میانگین نرخ کلیک تامبنیل‌ها: ${stats.ctrPercent}%", color = TextSecondary, fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(text = "🎯 ۳ اقدام اولویت‌دار برای هفته آینده:", color = GrowthGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "۱. تولید و انتشار پارت ۲ ویدیوی هوش مصنوعی", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "۲. تست دو کاور جدید با کلمات کمتر روی ویدیوی افت‌کرده", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "۳. برگزاری لایو استریم ۴۵ دقیقه‌ای برای تولید واچ‌تایم مانیتایز", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
